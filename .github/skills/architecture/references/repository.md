# Repository

## Conteúdo

- [Responsabilidades](#responsabilidades)
- [Convenções obrigatórias](#convenções-obrigatórias)
- [Fluxo de dados remoto](#fluxo-de-dados-remoto)
- [Fluxo de dados local](#fluxo-de-dados-local)
- [Uso dos assets](#uso-dos-assets)
- [Automação](#automação)
- [Checklist de Repository](#checklist-de-repository)
- [Origens dos dados](#origens-dos-dados)
- [Tecnologias de referência](#tecnologias-de-referência)

## Responsabilidades

- Abstrair a origem dos dados.
- Decidir entre API, cache e banco local.
- Converter os modelos recebidos da fonte de dados em modelos de domínio.
- Traduzir respostas HTTP e exceções de infraestrutura para o tipo `Result` do domínio.

## Convenções obrigatórias

Todo Repository deve cumprir as seguintes regras:

1. Ser declarado como `interface` no módulo de domínio.
2. Terminar o nome exatamente com o sufixo `Repository`.
3. Estar no pacote
   `com.example.app.domain.<feature>.repository`.
4. Declarar cada contrato de origem na interface aninhada correspondente:
   `Remote`, `Local` ou `Cache`.
5. Expor operações pontuais como funções `suspend` e observações contínuas
   locais como `Flow<T>`.
6. Retornar somente tipos do domínio, sem expor `retrofit2.Response`, DTOs,
   DAOs, entidades Room ou exceções de infraestrutura.
7. Implementar o contrato remoto no módulo `networking` com uma classe cujo nome
   termine em `RemoteImpl`, no pacote de adapters.
8. Executar a chamada remota e a extração da resposta dentro de `fetchData`.
9. Registrar a implementação no módulo de injeção de dependências vinculando-a
   ao respectivo contrato `Repository.Remote`.
10. Implementar o contrato local no módulo Android Library `local`, com uma
    classe cujo nome termine em `LocalImpl`, no pacote de adapters.
11. Manter Room, bancos, DAOs, entidades, migrations, adapters locais e a DI
    correspondente exclusivamente em `local`; `app` apenas inclui o módulo de
    DI local no composition root.
12. Registrar a implementação local pelo respectivo contrato
    `Repository.Local` e mapear entidades para tipos de domínio antes de
    atravessar a fronteira do módulo.

Usar as seguintes estruturas de pacote:

```text
com.example.app.domain.<feature>.repository
com.example.app.networking.adapter
com.example.app.local.adapter
com.example.app.local.database
com.example.app.local.di
```

### Exemplo válido

```kotlin
package com.example.app.domain.user.repository

interface UserRepository {
    interface Remote {
        suspend fun findUser(id: String): Result<User>
    }
}
```

A implementação correspondente deve cumprir o contrato:

```kotlin
package com.example.app.networking.adapter

class UserRemoteImpl(
    private val gateway: UserGateway
) : UserRepository.Remote {

    override suspend fun findUser(id: String): Result<User> {
        return fetchData {
            gateway.findUser(id).extractData()
        }
    }
}
```

E deve ser registrada pelo tipo do contrato:

```kotlin
factory<UserRepository.Remote> {
    UserRemoteImpl(gateway = get())
}
```

## Fluxo de dados remoto

Adotar o seguinte fluxo:

```text
Retrofit Gateway
↓ Response<ResponseDto>, Response<List<ResponseDto>> ou Response<Unit>
Remote Repository
↓ fetchData { response.extract...() }
Result<DomainModel>
↓
UseCase / ViewModel
```

## Fluxo de dados local

Adotar o seguinte fluxo:

```text
Room Database / DAO
↓ Entity, chave ou projeção local
Local Repository (`LocalImpl`)
↓ modelo do domínio ou Flow<modelo do domínio>
Repository.Local
↓
UseCase / ViewModel
```

### Propriedade do módulo local

Criar `:local` como Android Library quando o projeto persistir dados e ainda não
possuir um módulo equivalente. O módulo deve:

- depender de `:domain`;
- aplicar KSP e declarar Room runtime, Room KTX e Room compiler;
- manter testes Room e migrations em seu próprio source set unitário;
- expor para outros módulos somente implementações dos contratos do domínio e
  módulos de DI;
- preservar o nome do arquivo, tabelas, versões e migrations ao mover um banco
  existente entre módulos.

Remover Room e KSP de `app` quando deixarem de ter outro uso nesse módulo.
`domain` e `features` nunca dependem de `local`. O módulo `app` pode depender de
`local` apenas para compor DI e iniciar a aplicação.

### Contrato e implementação local

```kotlin
interface FavoriteRepository {
    interface Local {
        fun observeIds(): Flow<Set<String>>
        suspend fun setSelected(id: String, selected: Boolean)
    }
}
```

```kotlin
package com.example.app.local.adapter

class FavoriteLocalImpl(
    private val dao: FavoriteDao
) : FavoriteRepository.Local {
    override fun observeIds(): Flow<Set<String>> =
        dao.observeIds().map(List<String>::toSet)

    override suspend fun setSelected(id: String, selected: Boolean) {
        if (selected) dao.insert(FavoriteEntity(id)) else dao.delete(id)
    }
}
```

O DAO e a entidade pertencem a `com.example.app.local.database`. Não retornar
nenhum deles pelo contrato de domínio. Room já executa queries `suspend` e
`Flow` fora da main thread; não duplicar `Dispatchers.IO` no UseCase.

## Uso dos assets

Usar os arquivos de `assets` como templates de implementação, não apenas como
referência conceitual. Copiar o conjunto correspondente para o projeto e
preservar os packages `com.example.app` declarados nos arquivos.

Para implementar o fluxo remoto de Repository, usar em conjunto:

| Asset | Destino e uso obrigatório |
| --- | --- |
| `assets/usecase/domain/Result.kt` | Copiar para o módulo de domínio. Usar `Result`, `Success`, `Failure`, `Loading` e `Error` nos contratos e consumidores. |
| `assets/repository/DomainMapper.kt` | Copiar para a infraestrutura de Repository. Fazer DTOs de resposta com dados implementarem `DomainMapperResponse`. |
| `assets/repository/FetchData.kt` | Copiar para a infraestrutura de Repository. Envolver chamadas remotas e extração de respostas com `fetchData`. |
| `assets/repository/NetworkResult.kt` | Copiar para a infraestrutura de Repository. Converter respostas Retrofit com `extractData`, `extractList`, `extractNoData` ou `processData`. |

Não recriar manualmente essas classes quando o asset correspondente puder ser
copiado. Se o projeto já possuir uma implementação equivalente, comparar o
contrato existente com o asset e adaptar sem manter duas abstrações concorrentes.

## Automação

Executar os scripts a partir da raiz da skill.

### Instalar assets

Usar `install_assets.py` para copiar os assets, criar os diretórios dos módulos e
substituir `com.example.app` pelo pacote selecionado.

Simular a instalação do conjunto de Repository:

```bash
python3 scripts/install_assets.py repository \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --dry-run
```

Após revisar os caminhos, repetir sem `--dry-run`. O grupo `repository` instala
`Result.kt`, `DomainMapper.kt`, `FetchData.kt` e `NetworkResult.kt`. O script
recusa sobrescrever arquivos. Usar `--force` somente após comparar a
implementação existente e confirmar explicitamente a substituição.

### Gerar contrato e implementação remota

Usar o subcomando `repository` para criar o contrato no domínio e a implementação
`RemoteImpl` no módulo de networking:

```bash
python3 scripts/scaffold_architecture.py repository \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --feature user \
  --name User \
  --operation findUser \
  --parameter "id: String" \
  --result-type User \
  --import com.example.app.domain.user.User \
  --dry-run
```

O comando gera:

```text
domain/.../domain/user/repository/UserRepository.kt
networking/.../networking/adapter/UserRemoteImpl.kt
```

Repetir sem `--dry-run` para gravar. Em seguida:

1. Injetar o Gateway em `UserRemoteImpl`.
2. Substituir o `TODO` pela chamada Retrofit e pela extensão `extract...`
   adequada.
3. Criar ou adaptar os DTOs com `DomainMapperResponse`.
4. Registrar `UserRepository.Remote` na DI.
5. Formatar e compilar os módulos afetados.

Usar `--parameter` e `--import` mais de uma vez quando necessário. O gerador não
altera Gateway, módulo de DI ou arquivos existentes.

### Gerar contrato e implementação local

Usar `--source local` para criar `Repository.Local` no domínio e `LocalImpl` no
módulo `local`:

```bash
python3 scripts/scaffold_architecture.py repository \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --feature favorites \
  --name Favorite \
  --operation observeIds \
  --result-type "Set<String>" \
  --source local \
  --stream \
  --dry-run
```

O comando gera:

```text
domain/.../domain/favorites/repository/FavoriteRepository.kt
local/.../local/adapter/FavoriteLocalImpl.kt
```

Repetir sem `--dry-run` para gravar. Depois criar ou adaptar entidade, DAO,
database, migration e DI no módulo `local`, injetar o DAO no `LocalImpl`,
substituir o `TODO` e testar o mapeamento e a persistência. O scaffold não
inventa schemas Room nem altera arquivos existentes.

### 1. Definir o contrato do Repository no domínio

O contrato exposto para as demais camadas deve retornar
`com.example.app.domain.Result`, sem expor `retrofit2.Response` ou DTOs da API:

```kotlin
interface UserRepository {
    interface Remote {
        suspend fun findUser(id: String): Result<User>
        suspend fun findAll(): Result<List<User>>
        suspend fun update(user: User): Result<Unit>
    }
}
```

O módulo de domínio não deve depender do módulo de network. A implementação remota,
por outro lado, pode depender dos tipos do domínio para cumprir esse contrato.

### 2. Mapear respostas com `DomainMapperResponse`

Todo DTO retornado pela API que representa um objeto do domínio deve implementar
`DomainMapperResponse<T>` e realizar a transformação em `mapToDomain()`:

```kotlin
data class UserResponse(
    val id: String,
    val displayName: String
) : DomainMapperResponse<User> {
    override fun mapToDomain(): User {
        return User(
            id = id,
            name = displayName
        )
    }
}
```

O mapper pertence ao DTO de resposta. DTOs usados somente como corpo de requisição
não precisam implementar a interface.

### 3. Converter a `Response` do Retrofit

Selecionar a extensão de acordo com a assinatura do endpoint:

| Retorno do Gateway | Extensão | Retorno do Repository |
| --- | --- | --- |
| `Response<UserResponse>` | `extractData()` | `Result<User>` |
| `Response<List<UserResponse>>` | `extractList()` | `Result<List<User>>` |
| `Response<Unit>` | `extractNoData()` | `Result<Unit>` |
| `Response<Void>` | `processData()` | `Result<Unit>` |

Exemplo de Gateway:

```kotlin
interface UserGateway {
    @GET("users/{id}")
    suspend fun findUser(@Path("id") id: String): Response<UserResponse>

    @GET("users")
    suspend fun findAll(): Response<List<UserResponse>>

    @PUT("users/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): Response<Unit>
}
```

As extensões aplicam as seguintes regras:

- Resposta HTTP bem-sucedida com corpo: cria `Success`, mapeando objetos e listas
  para o domínio quando necessário.
- Resposta HTTP sem sucesso e com `errorBody`: cria `Failure` a partir do JSON de
  erro e preenche `Error.httpError` com o status HTTP.
- Resposta sem o corpo esperado, inclusive sucesso com corpo nulo, ou falha sem
  `errorBody`: retorna `Failure(null)`, representado por `generalFailure`.
- `extractError()` espera que o corpo de erro possa ser desserializado para
  `Error(code, httpError, title, message)`.

`extractNoData()` e `processData()` têm a mesma saída de domínio, mas atendem tipos
de resposta Retrofit diferentes. Usar a função compatível com a assinatura do
Gateway.

### 4. Executar a chamada com `fetchData`

A implementação remota deve envolver a chamada ao Gateway e a extração da resposta
em `fetchData`:

```kotlin
class UserRemoteImpl(
    private val gateway: UserGateway
) : UserRepository.Remote {

    override suspend fun findUser(id: String): Result<User> {
        return fetchData {
            gateway.findUser(id).extractData()
        }
    }

    override suspend fun findAll(): Result<List<User>> {
        return fetchData {
            gateway.findAll().extractList()
        }
    }

    override suspend fun update(user: User): Result<Unit> {
        return fetchData {
            val request = UpdateUserRequest(name = user.name)
            gateway.update(user.id, request).extractNoData()
        }
    }
}
```

`fetchData`:

- executa todo o bloco em `Dispatchers.IO`;
- preserva o `Result` produzido pelo bloco quando não ocorre exceção;
- converte `ConnectException` em `Failure` com código interno `166` e mensagem de
  falha de conexão;
- converte qualquer outra `Exception` em `Failure` com código interno `266` e
  mensagem de erro inesperado.

O bloco deve incluir tanto a chamada Retrofit quanto `extractData()`,
`extractList()`, `extractNoData()` ou `processData()`. Assim, exceções da chamada,
da leitura do erro e do mapeamento ficam sujeitas ao mesmo tratamento.

### 5. Consumir o `Result` do domínio

`Result<T>` é uma classe selada com os estados:

- `Success<T>(data)`: contém o dado de domínio.
- `Failure(error)`: contém um `Error?`; o erro pode ser nulo no caso de
  `generalFailure`.
- `Loading<T>(data)`: representa carregamento com dado associado. Esse estado está
  definido, mas não é utilizado no fluxo analisado.

É possível tratar os estados com `when`:

```kotlin
when (val result = repository.findUser(id)) {
    is Success -> showUser(result.data)
    is Failure -> showError(result.error?.formattedMessage.orEmpty())
    is Loading -> showLoading()
}
```

Também estão disponíveis as extensões `onSuccess` e `onFailure` do domínio:

```kotlin
repository.findUser(id)
    .onSuccess { user -> showUser(user) }
    .onFailure { error -> showError(error?.formattedMessage.orEmpty()) }
```

`onSuccess` devolve o próprio `Result`, permitindo o encadeamento acima.
`onFailure` encerra o encadeamento e recebe `Error?`.

O tipo `Error` oferece:

- `formattedTitle`: título ou string vazia.
- `formattedMessage`: mensagem ou string vazia.
- `formattedMessageCode`: concatenação de código, título e mensagem.
- `httpError`: status HTTP preenchido durante a extração de uma falha da API.

### Exemplos inválidos

```kotlin
package com.example.app.data

interface UserRepository
```

O nome está correto, mas o contrato não está no pacote de domínio da feature.

```kotlin
package com.example.app.domain.user.repository

interface UserData
```

O pacote está correto, mas o nome não termina com `Repository`.

```kotlin
interface UserRepository {
    interface Remote {
        fun findUser(id: String): Response<UserResponse>
    }
}
```

O contrato remoto não é `suspend`, expõe um tipo do Retrofit e retorna um DTO.

```kotlin
package com.example.app

@Entity
data class FavoriteEntity(@PrimaryKey val id: String)
```

A entidade está no módulo/pacote de aplicação. Toda declaração Room deve
pertencer ao módulo e ao pacote `local`.

## Checklist de Repository

Antes de considerar um Repository válido, verificar:

- [ ] O contrato é uma `interface` declarada no módulo de domínio.
- [ ] O nome termina exatamente com `Repository`.
- [ ] O package pertence ao caminho
  `com.example.app.domain.<feature>.repository`.
- [ ] O contrato remoto está na interface aninhada `Remote`.
- [ ] O contrato local está na interface aninhada `Local`.
- [ ] As operações remotas são `suspend`.
- [ ] O contrato retorna `Result` do domínio e não expõe `Response` ou DTOs.
- [ ] A implementação está no módulo de network, no pacote de adapters, e seu
  nome termina em `RemoteImpl`.
- [ ] DTOs de resposta com dados implementam `DomainMapperResponse`.
- [ ] A extensão `extract...` corresponde ao tipo retornado pelo Gateway.
- [ ] A chamada remota e a extração da resposta estão dentro de `fetchData`.
- [ ] A implementação está registrada na injeção de dependências pelo tipo
  `Repository.Remote`.
- [ ] `LocalImpl`, Room, DAOs, entidades, migrations e a DI local estão
  exclusivamente no módulo `local`.
- [ ] `app` apenas compõe o módulo local e não declara nem importa Room.
- [ ] A implementação local está registrada pelo tipo `Repository.Local`.
- [ ] Nenhum DAO ou entidade atravessa a fronteira para domínio ou features.
- [ ] Operações locais pontuais são `suspend` e observações usam `Flow`.
- [ ] Testes Room e de migration pertencem ao source set unitário de `local`.
- [ ] O consumidor trata `Failure.error` como anulável.

## Origens dos dados

- Remote API
- Banco local
- Cache

## Tecnologias de referência

- Retrofit
- Room
