# UseCase

## Conteúdo

- [Camada](#camada)
- [Responsabilidades](#responsabilidades)
- [Convenções obrigatórias](#convenções-obrigatórias)
- [Automação](#automação)
- [Comunicação com Repository](#comunicação-com-repository)
- [Implementação](#implementação)
- [Coroutines](#coroutines)
- [Flow e StateFlow](#flow-e-stateflow)
- [Injeção de dependências](#injeção-de-dependências)
- [Testes](#testes)
- [Checklist](#checklist)

## Camada

Domain Layer.

O UseCase é o ponto de entrada para uma ação ou consulta de negócio. Posicioná-lo
entre ViewModel e Repository:

```text
ViewModel
↓
UseCase
↓
Repository.Remote / Repository.Local / Repository.Cache
↓
Fonte de dados
```

## Responsabilidades

- Centralizar regras e decisões de negócio.
- Coordenar um ou mais contratos de Repository.
- Combinar dados remotos, locais e de cache sem expor suas implementações.
- Validar e transformar dados de domínio.
- Definir a ordem, dependência e paralelismo das operações.
- Expor operações assíncronas por funções `suspend`, `Flow` ou, quando
  justificado, `StateFlow`.
- Evitar lógica de negócio espalhada no ViewModel.

## Convenções obrigatórias

Toda classe pública desta camada deve:

1. Estar no módulo de domínio.
2. Usar o pacote `com.example.app.domain.<feature>.usecase`.
3. Terminar o nome exatamente com o sufixo `UseCase`.
4. Receber contratos de Repository pelo construtor.
5. Depender de `Repository.Remote`, `Repository.Local` ou `Repository.Cache`,
   nunca de implementações concretas.
6. Expor operações de execução única como funções `suspend`.
7. Retornar modelos e `Result` do domínio, sem expor `Response`, DTO, DAO,
   entidade de banco ou exceção de infraestrutura.
8. Não depender de Activity, Fragment, ViewModel, Compose, `Context` ou outro
   tipo da UI.
9. Não criar `CoroutineScope` sem uma duração explicitamente definida.

### Exemplo válido

```kotlin
package com.example.app.domain.user.usecase

class GetUserUseCase(
    private val repository: UserRepository.Remote
) {
    suspend operator fun invoke(id: String): Result<User> {
        require(id.isNotBlank()) { "id must not be blank" }
        return repository.findUser(id)
    }
}
```

Usar `operator fun invoke` quando a classe representar uma única ação. Quando um
UseCase agrupar operações fortemente relacionadas, usar nomes de função
explícitos:

```kotlin
class SessionUseCase(
    private val repository: SessionRepository.Remote
) {
    suspend fun signIn(username: String, password: String): Result<Session> {
        return repository.signIn(username, password)
    }

    suspend fun signOut(): Result<Unit> {
        return repository.signOut()
    }
}
```

Mesmo ao agrupar operações, manter o sufixo `UseCase` e uma única
responsabilidade de negócio.

## Automação

Executar os scripts a partir da raiz da skill.

Se o projeto ainda não possuir o `Result` do domínio, simular sua instalação:

```bash
python3 scripts/install_assets.py domain \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --dry-run
```

Após revisar o destino, repetir sem `--dry-run`.

Gerar um UseCase ligado a um contrato `Repository.Remote`:

```bash
python3 scripts/scaffold_architecture.py usecase \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --feature user \
  --name GetUser \
  --repository User \
  --operation findUser \
  --parameter "id: String" \
  --result-type User \
  --import com.example.app.domain.user.User \
  --dry-run
```

O comando gera:

```text
domain/.../domain/user/usecase/GetUserUseCase.kt
```

Repetir sem `--dry-run` para gravar. O contrato e a operação do Repository devem
existir antes da compilação. Após gerar:

1. Adicionar validações e transformações de negócio.
2. Coordenar outros Repositories quando a operação exigir.
3. Registrar o UseCase na DI.
4. Criar testes de sucesso, falha e validação.
5. Formatar e compilar o módulo de domínio.

Usar `--parameter` e `--import` mais de uma vez quando necessário. Usar
`--force` somente para uma substituição previamente revisada.

## Comunicação com Repository

O UseCase pode se comunicar com todos os tipos de origem definidos pela camada
de Repository, mas sempre por seus contratos:

```kotlin
class GetUserUseCase(
    private val remote: UserRepository.Remote,
    private val local: UserRepository.Local
) {
    suspend operator fun invoke(id: String, forceRefresh: Boolean): Result<User> {
        if (!forceRefresh) {
            when (val cached = local.findUser(id)) {
                is Success -> return cached
                is Failure -> Unit
                is Loading -> Unit
            }
        }

        return when (val result = remote.findUser(id)) {
            is Success -> {
                local.saveUser(result.data)
                result
            }
            is Failure -> result
            is Loading -> result
        }
    }
}
```

Aplicar estas regras:

- Chamar Gateway, Retrofit, Room, DAO ou cache concreto somente dentro da
  implementação do Repository.
- Resolver `Repository.Local` com uma implementação do módulo `local`; o
  UseCase e o módulo de domínio nunca dependem desse módulo concreto.
- Manter no UseCase a decisão entre remoto, local e cache.
- Retornar imediatamente quando uma falha impedir as próximas etapas.
- Preservar `Failure.error` como anulável.
- Converter resultados apenas quando houver uma transformação de negócio.
- Não transformar falha em sucesso silenciosamente.
- Não duplicar no UseCase o tratamento técnico já realizado por `fetchData`.

Para uma operação que apenas encaminha ao Repository, preservar o `Result`
recebido:

```kotlin
suspend operator fun invoke(id: String): Result<User> {
    return repository.findUser(id)
}
```

Para uma transformação de domínio, criar um novo `Result`:

```kotlin
suspend operator fun invoke(id: String): Result<UserSummary> {
    return when (val result = repository.findUser(id)) {
        is Success -> Success(UserSummary.from(result.data))
        is Failure -> result
        is Loading -> result
    }
}
```

O `Loading` definido em `assets/usecase/domain/Result.kt` herda de
`Result<Nothing>`. Preservar esse estado sem tentar transformar `data`, pois seu
tipo não está associado ao tipo genérico do `Result` recebido.

## Implementação

### Validação

Validar pré-condições antes de acessar o Repository. Usar `require` somente para
violações de contrato de programação. Para uma entrada inválida esperada do
usuário, retornar um `Failure` de domínio:

```kotlin
suspend operator fun invoke(email: String): Result<User> {
    if (!email.isValidEmail()) {
        return Failure(
            Error(
                code = INVALID_EMAIL,
                title = "Invalid email",
                message = "Provide a valid email address."
            )
        )
    }
    return repository.findByEmail(email)
}
```

### Composição

Manter no UseCase a sequência exigida pela regra de negócio:

```kotlin
class CompleteOrderUseCase(
    private val orderRepository: OrderRepository.Remote,
    private val cartRepository: CartRepository.Local
) {
    suspend operator fun invoke(order: Order): Result<Receipt> {
        return when (val result = orderRepository.complete(order)) {
            is Success -> {
                cartRepository.clear()
                result
            }
            is Failure -> result
            is Loading -> result
        }
    }
}
```

Se a limpeza do carrinho também puder falhar e for essencial para considerar a
operação concluída, tratar seu `Result` explicitamente. Não ignorar falhas de
etapas obrigatórias.

## Coroutines

### Funções `suspend`

Usar `suspend` para operações únicas que dependam de Repository. Não chamar
`launch` apenas para transformar uma função síncrona em assíncrona:

```kotlin
suspend operator fun invoke(id: String): Result<User> {
    return repository.findUser(id)
}
```

O chamador controla o ciclo de vida. Em uma ViewModel:

```kotlin
viewModelScope.launch {
    when (val result = getUserUseCase(id)) {
        is Success -> handleSuccess(result.data)
        is Failure -> handleFailure(result.error)
        is Loading -> handleLoading()
    }
}
```

### Dispatchers

- Não usar `Dispatchers.Main` no UseCase.
- Não repetir `Dispatchers.IO` quando o Repository já garante o dispatcher de
  I/O com `fetchData`.
- Usar `withContext` apenas para trabalho relevante executado pelo próprio
  UseCase, como transformação intensiva de CPU.
- Injetar o `CoroutineDispatcher` quando o UseCase precisar trocar de contexto,
  permitindo controle nos testes.

```kotlin
class BuildReportUseCase(
    private val repository: ReportRepository.Remote,
    private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<Report> {
        return when (val result = repository.findData()) {
            is Success -> withContext(defaultDispatcher) {
                Success(Report.from(result.data))
            }
            is Failure -> result
            is Loading -> result
        }
    }
}
```

### Concorrência estruturada

Usar `coroutineScope` e `async` somente quando operações independentes puderem ser
executadas em paralelo:

```kotlin
suspend operator fun invoke(): Result<Dashboard> = coroutineScope {
    val profile = async { profileRepository.findProfile() }
    val notifications = async { notificationRepository.findAll() }

    val profileResult = profile.await()
    val notificationResult = notifications.await()

    if (profileResult is Success && notificationResult is Success) {
        Success(
            Dashboard(
                profile = profileResult.data,
                notifications = notificationResult.data
            )
        )
    } else {
        (profileResult as? Failure)
            ?: (notificationResult as? Failure)
            ?: Failure(null)
    }
}
```

- Usar execução sequencial quando a segunda operação depender da primeira.
- Usar `supervisorScope` somente quando a falha de uma operação não deva cancelar
  as demais.
- Não usar `GlobalScope`.
- Não armazenar `Job` sem uma política explícita de cancelamento.
- Propagar cancelamento. Se houver `catch`, relançar `CancellationException`.
- Preferir que erros esperados sejam representados por `Failure`.

## Flow e StateFlow

### Quando expor `Flow`

Usar `Flow<T>` para dados contínuos fornecidos pelo Repository, como mudanças de
banco, cache ou preferências. Manter o fluxo frio no UseCase e aplicar nele
somente regras de domínio:

```kotlin
class ObserveUsersUseCase(
    private val repository: UserRepository.Local
) {
    operator fun invoke(): Flow<List<User>> {
        return repository.observeUsers()
            .map { users -> users.filter(User::isActive) }
            .distinctUntilChanged()
    }
}
```

Usar operadores conforme a responsabilidade:

- `map` para transformação de domínio;
- `filter` para descartar valores inválidos;
- `combine` para compor fluxos de Repositories diferentes;
- `distinctUntilChanged` para evitar emissões equivalentes;
- `catch` para converter exceções esperadas em um tipo de domínio;
- `flowOn` somente quando uma transformação do UseCase exigir outro dispatcher.

Não usar `catch` para ocultar `CancellationException`.

### Quando usar `StateFlow`

Preferir que o UseCase retorne `Flow` e que a ViewModel converta esse fluxo em
`StateFlow`, porque a ViewModel possui um `viewModelScope` com ciclo de vida
definido:

```kotlin
val users: StateFlow<List<User>> = observeUsersUseCase()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
```

O UseCase só deve manter `StateFlow` quando o estado:

1. representar estado de domínio, não estado visual;
2. precisar ser compartilhado por múltiplos consumidores;
3. sobreviver à recriação de uma ViewModel;
4. possuir duração definida pelo escopo de DI do próprio UseCase.

Nesse caso:

- manter `MutableStateFlow` privado;
- expor somente `StateFlow` com `asStateFlow()`;
- fornecer sempre um valor inicial;
- atualizar com `update` ou atribuição a `value`;
- registrar o UseCase como `single` na DI;
- não usar `StateFlow` para eventos únicos.

```kotlin
class SessionUseCase(
    private val repository: SessionRepository.Remote
) {
    private val mutableSession = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = mutableSession.asStateFlow()

    suspend fun signIn(username: String, password: String): Result<Session> {
        val result = repository.signIn(username, password)
        if (result is Success) {
            mutableSession.value = result.data
        }
        return result
    }

    suspend fun signOut(): Result<Unit> {
        val result = repository.signOut()
        if (result is Success) {
            mutableSession.value = null
        }
        return result
    }
}
```

Usar `SharedFlow` ou um canal controlado pela camada de UI para eventos que
devem ser consumidos uma única vez, como navegação, snackbar ou abertura de
diálogo.

## Injeção de dependências

Registrar UseCases sem estado como `factory`:

```kotlin
factory {
    GetUserUseCase(repository = get())
}
```

Registrar como `single` apenas UseCases que mantenham estado de domínio
compartilhado por `StateFlow`:

```kotlin
single {
    SessionUseCase(repository = get())
}
```

Injetar dispatchers por qualifiers quando necessários. Não obter dependências por
service locator dentro do UseCase.

## Testes

Testar UseCases como unidades de domínio, substituindo Repositories por fakes ou
mocks.

Para funções `suspend`:

```kotlin
@Test
fun `returns user from repository`() = runTest {
    val expected = User(id = "1", name = "User")
    coEvery { repository.findUser("1") } returns Success(expected)

    val result = GetUserUseCase(repository)("1")

    assertEquals(Success(expected), result)
}
```

Para `Flow` e `StateFlow`:

- usar `runTest`;
- controlar o scheduler e os dispatchers de teste;
- verificar valor inicial e ordem das emissões;
- cancelar coletores criados pelo teste;
- validar que valores equivalentes não sejam emitidos quando houver
  `distinctUntilChanged`;
- confirmar que estado privado só muda após `Success`.

## Checklist

- [ ] A classe está em `com.example.app.domain.<feature>.usecase`.
- [ ] O nome termina exatamente com `UseCase`.
- [ ] Somente contratos de Repository são injetados.
- [ ] Nenhum tipo de Retrofit, banco, Android ou UI é exposto.
- [ ] Operações únicas são `suspend`.
- [ ] O dispatcher de I/O não é duplicado em relação ao Repository.
- [ ] Paralelismo usa concorrência estruturada.
- [ ] Cancelamentos de coroutines são propagados.
- [ ] Fluxos contínuos são expostos como `Flow` por padrão.
- [ ] `MutableStateFlow` nunca é público.
- [ ] UseCase com `StateFlow` possui duração de DI definida e é `single`.
- [ ] Eventos únicos não usam `StateFlow`.
- [ ] Resultados de todas as etapas obrigatórias são tratados.
- [ ] Testes cobrem sucesso, falha, validação e emissões.
