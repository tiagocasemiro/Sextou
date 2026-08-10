# ViewModel

## Conteúdo

- [Camada](#camada)
- [Responsabilidades](#responsabilidades)
- [Convenções obrigatórias](#convenções-obrigatórias)
- [Automação](#automação)
- [Comunicação com UseCase](#comunicação-com-usecase)
- [Estado com StateFlow](#estado-com-stateflow)
- [Eventos únicos](#eventos-únicos)
- [Coroutines](#coroutines)
- [Flow vindo do UseCase](#flow-vindo-do-usecase)
- [Injeção de dependências](#injeção-de-dependências)
- [Testes](#testes)
- [Checklist](#checklist)

## Camada

UI Layer.

A ViewModel conecta a interface aos UseCases e transforma resultados de domínio
em estado renderizável:

```text
View (Compose)
↓ ações do usuário
ViewModel
↓ suspend / Flow / StateFlow
UseCase
↓
Repository
```

## Responsabilidades

- Receber ações da View.
- Executar um ou mais UseCases.
- Transformar `Result` e modelos de domínio em estado de UI.
- Expor estado imutável e observável.
- Controlar carregamento, mensagens e disponibilidade de ações.
- Iniciar e cancelar coroutines conforme o ciclo de vida da ViewModel.
- Emitir efeitos únicos sem armazená-los como estado persistente.
- Manter regras de negócio no UseCase.

## Convenções obrigatórias

Toda ViewModel pública deve:

1. Herdar de `androidx.lifecycle.ViewModel`.
2. Estar no pacote `com.example.app.features.<feature>.<screen>`.
3. Terminar o nome exatamente com o sufixo `ViewModel`.
4. Receber UseCases pelo construtor.
5. Acessar dados de negócio somente por UseCases.
6. Não depender de Repository, Gateway, Retrofit, DAO, banco ou cache
   diretamente.
7. Expor estado por `StateFlow`, sem tornar `MutableStateFlow` público.
8. Executar funções `suspend` dentro de `viewModelScope`.
9. Não receber nem armazenar Activity, Fragment, View ou `Context`.
10. Não executar navegação ou renderização diretamente.

Tipos auxiliares da tela devem seguir:

- `<Screen>UiState` para estado persistente e renderizável.
- `<Screen>UiEvent` para efeitos consumidos uma única vez.
- `<Screen>UiAction` quando todas as entradas forem centralizadas em uma função
  `onAction`.

### Exemplo de estrutura

```kotlin
package com.example.app.features.user.details

data class UserDetailsUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)

sealed interface UserDetailsUiEvent {
    data object NavigateBack : UserDetailsUiEvent
}

class UserDetailsViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel()
```

## Automação

Executar o gerador a partir da raiz da skill para criar `UiState`, `UiEvent` e
ViewModel:

```bash
python3 scripts/scaffold_architecture.py viewmodel \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --feature user \
  --screen UserDetails \
  --screen-package details \
  --usecase GetUser \
  --dry-run
```

O comando gera:

```text
features/.../features/user/details/UserDetailsUiState.kt
features/.../features/user/details/UserDetailsUiEvent.kt
features/.../features/user/details/UserDetailsViewModel.kt
```

Repetir sem `--dry-run` para gravar. O scaffold contém estado mínimo de
carregamento/erro, evento de retorno e chamada sem parâmetros ao UseCase. Após
gerar:

1. Adicionar ao `UiState` os dados renderizados pela tela.
2. Mapear `Success` para esses dados.
3. Ajustar parâmetros de `load()` quando o UseCase exigir entrada.
4. Adicionar ações e eventos específicos da tela.
5. Registrar a ViewModel na DI.
6. Criar testes de estado e eventos.
7. Formatar e compilar o módulo de features.

Usar `--parameter "nome: Tipo"` e `--import pacote.Tipo` para gerar uma chamada
com parâmetros. Repetir as opções quando necessário. Usar `--force` somente
depois de comparar os arquivos existentes.

## Comunicação com UseCase

Injetar todos os UseCases necessários à tela:

```kotlin
class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val completeOrderUseCase: CompleteOrderUseCase
) : ViewModel()
```

Aplicar estas regras:

- Chamar funções `suspend` para operações únicas.
- Coletar `Flow` para dados contínuos.
- Observar `StateFlow` do UseCase quando ele mantiver estado de domínio
  compartilhado.
- Tratar `Success`, `Failure` e `Loading` de forma explícita.
- Converter modelos de domínio para modelos de apresentação quando a tela
  precisar de formatação específica.
- Não repassar `Result` diretamente à View.
- Não duplicar validações ou decisões já pertencentes ao UseCase.
- Não coordenar Repositories na ViewModel. Criar ou ampliar um UseCase quando a
  operação exigir composição de fontes.

### Operação única

```kotlin
class UserDetailsViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(UserDetailsUiState())
    val uiState: StateFlow<UserDetailsUiState> = mutableUiState.asStateFlow()

    fun loadUser(id: String) {
        viewModelScope.launch {
            mutableUiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            when (val result = getUserUseCase(id)) {
                is Success -> mutableUiState.update {
                    it.copy(user = result.data)
                }
                is Failure -> mutableUiState.update {
                    it.copy(
                        errorMessage = result.error
                            ?.formattedMessage
                            .orEmpty()
                            .ifBlank { "Unexpected error" }
                    )
                }
                is Loading -> Unit
            }

            mutableUiState.update { it.copy(isLoading = false) }
        }
    }
}
```

Se a operação puder lançar uma exceção não representada por `Failure`, garantir
que o carregamento seja encerrado com `finally` e preservar cancelamento:

```kotlin
viewModelScope.launch {
    mutableUiState.update { it.copy(isLoading = true) }
    try {
        handle(getUserUseCase(id))
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        mutableUiState.update {
            it.copy(errorMessage = "Unexpected error")
        }
    } finally {
        mutableUiState.update { it.copy(isLoading = false) }
    }
}
```

Erros esperados devem chegar como `Failure`; o `catch` é a última proteção da
UI, não o fluxo principal de tratamento.

## Estado com StateFlow

Representar todo o estado necessário para renderizar uma tela em uma data class
imutável:

```kotlin
data class UsersUiState(
    val isLoading: Boolean = false,
    val users: List<UserItem> = emptyList(),
    val query: String = "",
    val errorMessage: String? = null
)
```

Preferir um único `StateFlow<ScreenUiState>` por tela. Isso mantém alterações
atômicas e evita combinações incoerentes entre múltiplos estados públicos.

### Encapsulamento

```kotlin
private val mutableUiState = MutableStateFlow(UsersUiState())
val uiState: StateFlow<UsersUiState> = mutableUiState.asStateFlow()
```

- Manter `MutableStateFlow` privado.
- Expor o tipo somente leitura `StateFlow`.
- Fornecer um valor inicial completo e renderizável.
- Não expor coleções mutáveis dentro de `UiState`.
- Atualizar estado com `update` para alterações baseadas no valor anterior.
- Usar atribuição a `value` quando o novo estado não depender do anterior.
- Limpar erros antigos ao iniciar uma nova tentativa.
- Preservar dados anteriores durante recarregamento quando isso melhorar a
  experiência da tela.

### Atualização atômica

```kotlin
fun onQueryChanged(query: String) {
    mutableUiState.update { current ->
        current.copy(
            query = query,
            users = current.users.filter { it.matches(query) }
        )
    }
}
```

Se o filtro representar regra de negócio ou exigir nova consulta, encaminhar a
entrada ao UseCase em vez de filtrar na ViewModel.

### Consumo na View

Em Compose, coletar o estado respeitando o ciclo de vida:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

A View deve renderizar o valor recebido e encaminhar ações. Não deve alterar o
estado interno da ViewModel.

## Eventos únicos

Não usar `StateFlow` para navegação, snackbar, toast, abertura de diálogo ou
outro efeito que não possa ser repetido após uma nova coleta.

Usar `SharedFlow`:

```kotlin
private val mutableEvents = MutableSharedFlow<UserDetailsUiEvent>(
    extraBufferCapacity = 1
)
val events: SharedFlow<UserDetailsUiEvent> = mutableEvents.asSharedFlow()

fun onBackClicked() {
    mutableEvents.tryEmit(UserDetailsUiEvent.NavigateBack)
}
```

Usar `emit()` dentro de uma coroutine quando a entrega puder suspender. Usar
`tryEmit()` apenas quando a estratégia de buffer e descarte estiver definida.
Quando a entrega de cada evento for obrigatória para um único consumidor,
considerar `Channel` e expor `receiveAsFlow()`.

A View executa o efeito ao coletar o evento; a ViewModel apenas descreve o que
deve acontecer.

## Coroutines

### Escopo

Usar exclusivamente `viewModelScope` para trabalho iniciado pela ViewModel:

```kotlin
fun retry() {
    viewModelScope.launch {
        reloadUseCase()
    }
}
```

- Não criar `CoroutineScope` manual.
- Não usar `GlobalScope`.
- Não cancelar `viewModelScope` manualmente.
- Permitir que `ViewModel.onCleared()` cancele o trabalho automaticamente.
- Não usar `runBlocking`.

### Dispatchers

- Não usar `Dispatchers.IO` para chamar UseCases; Repository e UseCase já
  controlam os dispatchers necessários.
- Não usar `withContext(Dispatchers.Main)`; `viewModelScope.launch` inicia no
  dispatcher principal.
- Manter transformação intensiva de CPU no UseCase com dispatcher injetado.
- Injetar dispatcher na ViewModel somente quando existir trabalho de
  apresentação comprovadamente pesado e testável.

### Cancelamento de operações repetidas

Cancelar a operação anterior quando apenas o resultado mais recente for
relevante:

```kotlin
private var searchJob: Job? = null

fun search(query: String) {
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
        searchUseCase(query)
    }
}
```

Para entradas representadas por `Flow`, preferir `debounce` e `flatMapLatest`,
que cancelam automaticamente a busca anterior.

Não armazenar `Job` quando todas as execuções devam terminar. Nesse caso, cada
ação pode usar seu próprio `viewModelScope.launch`.

### Paralelismo

Manter composição e paralelismo de operações de negócio no UseCase. A ViewModel
não deve usar `async` para coordenar múltiplos Repositories ou reconstruir uma
regra já pertencente ao domínio.

Se duas operações forem apenas necessidades independentes de apresentação,
preferir UseCases separados e combinar seus `Flow`s para produzir `UiState`.

### Falhas e cancelamento

- Tratar falhas esperadas pelo `Result` do domínio.
- Relançar `CancellationException` em qualquer `catch`.
- Usar `finally` para encerrar indicadores de carregamento.
- Não transformar cancelamento em mensagem de erro.
- Não deixar uma exceção apagar silenciosamente o estado anterior.

## Flow vindo do UseCase

### Converter Flow em StateFlow

Converter o `Flow` frio exposto pelo UseCase com `stateIn` e
`viewModelScope`:

```kotlin
val uiState: StateFlow<UsersUiState> = observeUsersUseCase()
    .map { users ->
        UsersUiState(
            users = users.map(UserItem::from)
        )
    }
    .catch { throwable ->
        if (throwable is CancellationException) throw throwable
        emit(
            UsersUiState(
                errorMessage = "Unable to load users"
            )
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UsersUiState(isLoading = true)
    )
```

Usar `SharingStarted.WhileSubscribed(5_000)` como padrão para interromper
trabalho upstream quando não houver observadores, tolerando mudanças breves de
configuração.

Usar `SharingStarted.Eagerly` somente quando o fluxo precisar iniciar junto com
a ViewModel, mesmo sem observador. Usar `Lazily` somente quando o primeiro
observador deva iniciar uma coleta que continuará ativa durante toda a vida da
ViewModel.

### Observar StateFlow do UseCase

Quando o UseCase expuser estado de domínio compartilhado, mapeá-lo para estado
de UI:

```kotlin
val uiState: StateFlow<SessionUiState> = sessionUseCase.session
    .map { session ->
        SessionUiState(
            isAuthenticated = session != null,
            userName = session?.userName.orEmpty()
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionUiState()
    )
```

Não duplicar o estado do UseCase em outro `MutableStateFlow` quando um
`map` seguido de `stateIn` for suficiente.

### Coleta imperativa

Usar coleta imperativa quando cada emissão exigir uma atualização parcial ou
efeito controlado:

```kotlin
init {
    observeUsersUseCase()
        .onEach { users ->
            mutableUiState.update {
                it.copy(
                    isLoading = false,
                    users = users.map(UserItem::from)
                )
            }
        }
        .launchIn(viewModelScope)
}
```

Usar `collectLatest` quando o processamento da emissão anterior deva ser
cancelado ao chegar um valor novo.

## Injeção de dependências

Registrar ViewModels com o DSL próprio do Koin:

```kotlin
viewModel {
    UserDetailsViewModel(
        getUserUseCase = get()
    )
}
```

Não registrar ViewModel como `single`. O ciclo de vida deve ser controlado pelo
`ViewModelStoreOwner`.

Usar `SavedStateHandle` para argumentos de navegação e pequeno estado necessário
à restauração de processo:

```kotlin
class UserDetailsViewModel(
    private val getUserUseCase: GetUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val userId: String = checkNotNull(savedStateHandle["userId"])
}
```

Não usar `SavedStateHandle` como banco, cache geral ou substituto de Repository.

## Testes

Testar a ViewModel com UseCases falsos ou simulados, nunca com Repository real.

Para coroutines:

- substituir `Dispatchers.Main` com uma regra de dispatcher de teste;
- usar `runTest`;
- executar tarefas pendentes com `advanceUntilIdle` quando necessário;
- verificar cancelamento de buscas substituídas;
- confirmar que `CancellationException` não produz erro de UI.

Para `StateFlow`:

- verificar o valor inicial;
- verificar as transições de carregamento, sucesso e falha;
- confirmar que `MutableStateFlow` não é exposto;
- coletar fluxos materializados por `stateIn` durante o teste;
- usar uma ferramenta de teste de Flow quando for necessário validar a ordem das
  emissões.

Para eventos:

- verificar que cada ação emite o `UiEvent` correto;
- confirmar que eventos antigos não são reapresentados após nova coleta;
- testar a política de buffer quando `tryEmit` for utilizado.

## Checklist

- [ ] A classe herda de `ViewModel`.
- [ ] O pacote segue `com.example.app.features.<feature>.<screen>`.
- [ ] O nome termina exatamente com `ViewModel`.
- [ ] Dependências de negócio são somente UseCases.
- [ ] Repository e fontes de dados não são acessados diretamente.
- [ ] O estado é representado por `<Screen>UiState` imutável.
- [ ] `MutableStateFlow` e `MutableSharedFlow` são privados.
- [ ] A View recebe somente `StateFlow` e fluxos somente leitura.
- [ ] Operações `suspend` usam `viewModelScope`.
- [ ] A ViewModel não duplica `Dispatchers.IO`.
- [ ] Cancelamentos são propagados.
- [ ] Operações substituídas cancelam o `Job` anterior.
- [ ] `StateFlow` não é usado para eventos únicos.
- [ ] Navegação e efeitos são executados pela View.
- [ ] `Flow` do UseCase é materializado com política de compartilhamento
  explícita.
- [ ] A ViewModel está registrada com o DSL `viewModel`.
- [ ] Testes cobrem estado inicial, carregamento, sucesso, falha e eventos.
