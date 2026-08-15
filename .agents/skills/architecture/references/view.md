# View

## Conteúdo

- [Camada](#camada)
- [Responsabilidades](#responsabilidades)
- [Convenções obrigatórias](#convenções-obrigatórias)
- [Recursos de texto](#recursos-de-texto)
- [Hierarquia de Composables](#hierarquia-de-composables)
- [Automação](#automação)
- [Comunicação com ViewModel](#comunicação-com-viewmodel)
- [Consumo de StateFlow](#consumo-de-stateflow)
- [Estado local](#estado-local)
- [Compose Navigation](#compose-navigation)
- [Efeitos e ciclo de vida](#efeitos-e-ciclo-de-vida)
- [Previews e testes](#previews-e-testes)
- [Checklist](#checklist)

## Camada

UI Layer.

Usar Jetpack Compose como tecnologia principal. Usar XML somente em telas
legadas ou quando uma integração específica ainda exigir Views.

```text
ViewModel
↓ StateFlow<UiState> / Flow<UiEvent>
Destination
↓ parâmetros imutáveis e callbacks
Screen
↓
Componentes da tela
```

## Responsabilidades

- Coletar o estado público da ViewModel.
- Renderizar uma função pura do `UiState`.
- Encaminhar ações do usuário para a ViewModel.
- Consumir `UiEvent` e executar efeitos de UI.
- Coordenar navegação por callbacks.
- Manter apenas estado estritamente visual e local.
- Oferecer acessibilidade, previews e pontos de teste.
- Não conter regras de negócio.

## Convenções obrigatórias

Toda tela Compose pública deve:

1. Estar no pacote `com.example.app.features.<feature>.<screen>`.
2. Ser implementada por uma função `@Composable` cujo nome termine em `Screen`.
3. Ter um conector stateful terminado em `Destination` quando houver ViewModel.
4. Receber `UiState` imutável e callbacks, sem acessar Repository ou UseCase.
5. Não receber `NavController` na função `Screen`.
6. Coletar `StateFlow` com `collectAsStateWithLifecycle()`.
7. Encaminhar ações à ViewModel, sem alterar diretamente seu estado.
8. Manter componentes específicos em
   `com.example.app.features.<feature>.<screen>.components`.
9. Definir rotas tipadas no pacote `com.example.app.navigation`.
10. Terminar classes e objetos de rota com o sufixo `Route`.
11. Declarar toda string estática visível ao usuário nos recursos Android.

Usar os seguintes sufixos:

| Elemento | Sufixo | Exemplo |
| --- | --- | --- |
| Tela stateless | `Screen` | `UserDetailsScreen` |
| Conector com ViewModel | `Destination` | `UserDetailsDestination` |
| Rota de navegação | `Route` | `UserDetailsRoute` |
| Estado da tela | `UiState` | `UserDetailsUiState` |
| Evento único | `UiEvent` | `UserDetailsUiEvent` |

Componentes menores devem receber nomes semânticos, como `UserHeader`,
`RetryButton` ou `AddressCard`. Não adicionar o sufixo genérico `Component`.

## Recursos de texto

Declarar toda string estática exibida ou anunciada ao usuário em
`src/main/res/values/strings.xml`. Isso inclui textos de botões, títulos,
rótulos, mensagens, placeholders, diálogos, snackbars e descrições de
acessibilidade. Não escrever esses textos diretamente em código Kotlin ou em
composables.

Consumir o recurso na View com `stringResource`:

```xml
<resources>
    <string name="user_details_retry">Tentar novamente</string>
    <string name="user_details_greeting">Olá, %1$s</string>
</resources>
```

```kotlin
Text(text = stringResource(R.string.user_details_retry))
Text(text = stringResource(R.string.user_details_greeting, user.name))
```

- Usar recursos formatados para textos com valores dinâmicos.
- Usar `plurals` e `pluralStringResource` para quantidades.
- Resolver recursos na View; não passar `Context` para ViewModel, UseCase ou
  Repository.
- Não mover para `strings.xml` valores que não são textos de interface, como
  URLs, chaves de API, identificadores, nomes de rotas e mensagens de log.
- Executar o Android Lint e corrigir ocorrências de `HardcodedText` no código
  alterado.

## Hierarquia de Composables

Organizar cada tela em três níveis:

```text
App
└── AppNavHost
    └── <Screen>Destination
        └── <Screen>Screen
            ├── <Screen>Content
            │   ├── componente de seção
            │   └── componente de item
            ├── estado de carregamento
            └── estado de erro
```

### Estrutura de arquivos

```text
com/example/app/
├── navigation/
│   ├── AppNavHost.kt
│   └── AppRoutes.kt
└── features/user/details/
    ├── UserDetailsDestination.kt
    ├── UserDetailsScreen.kt
    ├── UserDetailsViewModel.kt
    ├── UserDetailsUiState.kt
    ├── UserDetailsUiEvent.kt
    └── components/
        ├── UserDetailsContent.kt
        ├── UserHeader.kt
        └── RetryButton.kt
```

### Destination

Usar `Destination` como fronteira stateful. Obter a ViewModel, coletar estado e
eventos e ligar callbacks de navegação:

```kotlin
@Composable
fun UserDetailsDestination(
    onNavigateBack: () -> Unit,
    viewModel: UserDetailsViewModel = koinNavViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    UserDetailsUiEvent.NavigateBack -> onNavigateBack()
                }
            }
        }
    }

    UserDetailsScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onBackClick = viewModel::onBackClicked
    )
}
```

### Screen

Manter `Screen` stateless em relação à ViewModel e à navegação:

```kotlin
@Composable
fun UserDetailsScreen(
    uiState: UserDetailsUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            UserDetailsTopBar(onBackClick = onBackClick)
        }
    ) { contentPadding ->
        when {
            uiState.user != null -> {
                UserDetailsContent(
                    user = uiState.user,
                    isRefreshing = uiState.isLoading,
                    modifier = Modifier.padding(contentPadding)
                )
            }
            uiState.isLoading -> {
                LoadingContent(
                    modifier = Modifier.padding(contentPadding)
                )
            }
            uiState.errorMessage != null -> {
                ErrorContent(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.padding(contentPadding)
                )
            }
            else -> {
                EmptyContent(
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    }
}
```

### Componentes de tela

Extrair um componente quando ele:

- possuir responsabilidade visual própria;
- for repetido;
- tiver preview ou teste isolado útil;
- tornar a função `Screen` difícil de ler;
- precisar ser reutilizado por outra tela.

```kotlin
@Composable
internal fun UserHeader(
    name: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        UserAvatar(url = avatarUrl)
        Text(text = name)
    }
}
```

Manter componentes específicos como `internal`. Tornar um componente público
somente quando fizer parte de uma API compartilhada entre módulos; nesse caso,
movê-lo para o módulo de design system.

## Automação

Executar o gerador a partir da raiz da skill para criar a hierarquia inicial da
tela:

```bash
python3 scripts/scaffold_architecture.py view \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --feature user \
  --screen UserDetails \
  --screen-package details \
  --dry-run
```

O comando gera:

```text
features/.../features/user/details/UserDetailsDestination.kt
features/.../features/user/details/UserDetailsScreen.kt
features/.../features/user/details/UserDetailsNavigation.kt
features/.../features/user/details/components/UserDetailsContent.kt
features/.../navigation/UserDetailsRoute.kt
```

Repetir sem `--dry-run` para gravar. O scaffold espera que `UiState`, `UiEvent`
e ViewModel tenham sido criados antes com o subcomando `viewmodel`. Após gerar:

1. Adaptar conteúdo, carregamento e erro ao `UiState`.
2. Adicionar callbacks para todas as ações da tela.
3. Ajustar argumentos de `UserDetailsRoute`, se existirem.
4. Registrar `userDetailsDestination()` no `AppNavHost`.
5. Criar previews e testes da `Screen`.
6. Testar a navegação e o back stack.
7. Formatar e compilar o módulo de features.

O gerador não edita o `AppNavHost`, evitando sobrescrever um grafo existente.
Usar `--force` somente após revisar a diferença do arquivo que será substituído.

## Comunicação com ViewModel

A View conhece somente os contratos públicos desenhados na camada ViewModel:

- `StateFlow<ScreenUiState>` para estado renderizável;
- `SharedFlow<ScreenUiEvent>` ou `Flow<ScreenUiEvent>` para eventos;
- funções públicas ou `onAction(<Screen>UiAction)` para entradas.

Não acessar UseCase, Repository, banco, cache, analytics ou cliente de rede
diretamente na `Screen`.

Preferir callbacks específicos:

```kotlin
UserDetailsScreen(
    uiState = uiState,
    onRetry = viewModel::retry,
    onBackClick = viewModel::onBackClicked
)
```

Usar `onAction` quando a tela possuir muitas ações relacionadas:

```kotlin
UserDetailsScreen(
    uiState = uiState,
    onAction = viewModel::onAction
)
```

Não passar a ViewModel inteira para componentes descendentes. O
`Destination` deve transformar sua API em dados e callbacks.

## Consumo de StateFlow

Em Android Compose, coletar `StateFlow` com
`collectAsStateWithLifecycle()`:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

Essa coleta:

- usa o valor atual do `StateFlow` como estado inicial;
- inicia quando o `LifecycleOwner` atinge o estado ativo configurado;
- interrompe quando o ciclo de vida deixa esse estado;
- atualiza o Compose e agenda recomposição a cada novo valor observado.

Adicionar `androidx.lifecycle:lifecycle-runtime-compose` ao módulo de UI. Não
fixar a versão na referência; usar o catálogo de versões adotado pelo projeto.

Aplicar estas regras:

- Coletar o `UiState` uma vez no `Destination`.
- Passar o valor imutável para `Screen` e componentes.
- Não chamar `.value` diretamente durante toda a hierarquia.
- Não copiar o estado da ViewModel para outro `remember` ou
  `mutableStateOf`.
- Não iniciar chamadas de carregamento diretamente no corpo do composable.
- Evitar observar campos separados quando a ViewModel fornece um único
  `UiState`.

### Recomposição

Tratar a função `Screen` como uma função do estado:

```text
UiState + callbacks → árvore Compose
```

Não executar navegação, analytics, escrita em cache ou chamada à ViewModel como
efeito colateral da recomposição. Usar callbacks ou APIs de efeito do Compose.

Em listas, fornecer chaves estáveis:

```kotlin
LazyColumn {
    items(
        items = uiState.users,
        key = { user -> user.id }
    ) { user ->
        UserRow(user = user)
    }
}
```

## Estado local

Manter na View apenas estado visual que não pertença à regra de negócio:

- expansão de um item;
- foco;
- posição temporária de scroll;
- visibilidade transitória de um menu;
- valor intermediário antes de ser confirmado.

Usar:

- `remember` para estado que pode ser perdido quando o composable sair da
  composição;
- `rememberSaveable` para estado pequeno que deve sobreviver a recriação da
  Activity ou do processo;
- ViewModel para estado de tela e ações de negócio.

Aplicar state hoisting:

```kotlin
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
)
```

Preferir a versão stateless para componentes reutilizáveis e testáveis.

## Compose Navigation

### Dependências

Adicionar ao módulo de UI:

- `androidx.navigation:navigation-compose`;
- plugin Kotlin Serialization;
- biblioteca Kotlin Serialization usada pelas rotas;
- `androidx.lifecycle:lifecycle-runtime-compose`.
- integração Compose do Koin para `koinViewModel()` e, em destinos com
  argumentos, integração de navegação para `koinNavViewModel()`.

Usar as versões definidas no catálogo do projeto.

### Rotas tipadas

Representar destinos por objetos ou data classes serializáveis:

```kotlin
package com.example.app.navigation

@Serializable
data object UsersRoute

@Serializable
data class UserDetailsRoute(
    val userId: String
)
```

- Usar `data object` para rota sem argumentos.
- Usar `data class` para rota com argumentos.
- Passar apenas identificadores e valores pequenos.
- Não navegar com DTOs, entidades, objetos de Repository ou estado completo da
  tela.
- Carregar os dados no destino por ViewModel e UseCase.

### NavHost

Criar o `NavController` uma vez na raiz e passá-lo ao `AppNavHost`:

```kotlin
@Composable
fun App() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = UsersRoute,
        modifier = modifier
    ) {
        composable<UsersRoute> {
            UsersDestination(
                onUserClick = { userId ->
                    navController.navigate(
                        UserDetailsRoute(userId = userId)
                    )
                }
            )
        }

        composable<UserDetailsRoute> {
            UserDetailsDestination(
                onNavigateBack = navController::popBackStack
            )
        }
    }
}
```

Restringir o `NavController` à raiz da aplicação e ao `AppNavHost`. Passar
callbacks de navegação para `Destination` e `Screen`.

### Argumentos

Preferir que a ViewModel obtenha argumentos pelo `SavedStateHandle` quando eles
forem necessários à inicialização. Não duplicar o argumento simultaneamente no
construtor da tela, no estado local e na ViewModel.

```kotlin
class UserDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val route = savedStateHandle.toRoute<UserDetailsRoute>()
    private val userId = route.userId
}
```

Validar argumentos obrigatórios e definir comportamento claro para deep links
inválidos.

### Back stack

- Usar `popBackStack()` para voltar.
- Usar `launchSingleTop` quando um destino não puder ser duplicado no topo.
- Usar `popUpTo` para remover etapas concluídas de um fluxo.
- Usar `saveState` e `restoreState` em navegação de nível superior quando cada
  destino precisar preservar sua pilha.
- Não chamar `navigate()` como consequência direta de `UiState`; consumir um
  `UiEvent` único.

Exemplo para destinos de nível superior:

```kotlin
navController.navigate(route) {
    launchSingleTop = true
    restoreState = true
    popUpTo(navController.graph.startDestinationId) {
        saveState = true
    }
}
```

### Grafos

Separar grafos por fluxo ou feature quando o `AppNavHost` ficar extenso:

```kotlin
fun NavGraphBuilder.userGraph(
    navController: NavHostController
) {
    composable<UsersRoute> {
        // Destination
    }
    composable<UserDetailsRoute> {
        // Destination
    }
}
```

Manter a decisão de navegação no grafo e a intenção de navegar no `UiEvent`.

## Efeitos e ciclo de vida

Usar APIs de efeito somente para sincronizar Compose com algo externo:

- `LaunchedEffect` para coroutine vinculada à composição;
- `DisposableEffect` para registrar e remover listeners;
- `SideEffect` para publicar estado do Compose após recomposição bem-sucedida;
- `rememberUpdatedState` para capturar callbacks atuais em efeitos duradouros.

Não usar `LaunchedEffect(Unit)` para executar novamente uma carga que a
ViewModel já inicia em `init`. Se a View precisar disparar a carga, usar uma
chave estável e tornar a operação idempotente.

Coletar eventos com lifecycle:

```kotlin
val lifecycleOwner = LocalLifecycleOwner.current

LaunchedEffect(viewModel, lifecycleOwner) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.events.collect(::handleEvent)
    }
}
```

## Previews e testes

### Previews

Criar previews para `Screen` e componentes stateless, sem ViewModel, Koin ou
`NavController`:

```kotlin
@Preview
@Composable
private fun UserDetailsScreenPreview() {
    AppTheme {
        UserDetailsScreen(
            uiState = UserDetailsUiState.preview(),
            onRetry = {},
            onBackClick = {}
        )
    }
}
```

Cobrir ao menos os estados de conteúdo, carregamento e erro.

### Testes de tela

Testar `Screen` isoladamente passando estado e callbacks:

- verificar conteúdo para cada `UiState`;
- executar ações e verificar callbacks;
- usar tags somente quando seletores semânticos não forem suficientes;
- validar conteúdo descritivo e acessibilidade.
- executar o Android Lint para impedir strings de interface hardcoded.

### Testes de navegação

Testar `AppNavHost` com `TestNavHostController`:

- verificar o destino inicial;
- executar cliques e confirmar o destino;
- verificar argumentos tipados;
- testar back stack e deep links relevantes.

Manter telas independentes de `NavController` permite testá-las sem montar um
grafo de navegação.

## Checklist

- [ ] A tela está em `com.example.app.features.<feature>.<screen>`.
- [ ] A função pública termina com `Screen`.
- [ ] O conector com ViewModel termina com `Destination`.
- [ ] Rotas ficam em `com.example.app.navigation` e terminam com `Route`.
- [ ] `Destination` coleta a ViewModel e `Screen` recebe apenas estado e
  callbacks.
- [ ] `StateFlow` é coletado com `collectAsStateWithLifecycle()`.
- [ ] Eventos únicos são coletados separadamente do estado.
- [ ] A `Screen` não recebe ViewModel nem `NavController`.
- [ ] Componentes específicos ficam no subpacote `components`.
- [ ] Toda string estática visível ao usuário está em `strings.xml` e é
  consumida pela View por recurso.
- [ ] Estado de negócio não é armazenado com `remember`.
- [ ] Efeitos colaterais não são executados durante recomposição.
- [ ] Rotas são tipadas e serializáveis.
- [ ] Navegação transporta identificadores, não objetos complexos.
- [ ] O `NavController` fica restrito à raiz e ao host de navegação.
- [ ] `UiEvent` controla navegação disparada pela ViewModel.
- [ ] Listas usam chaves estáveis.
- [ ] Screens e componentes possuem previews.
- [ ] Testes cobrem estado, ações e navegação.
