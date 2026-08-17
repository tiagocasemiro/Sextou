# Android Design System — Arquitetura

Capacidade de **planejamento** de componentes do Design System. Analisa premissas, define a estrutura, modela contratos e produz um handoff MD detalhado para o Developer.

> Esta capacidade NÃO escreve código. Escrita de código é responsabilidade da capacidade [`developer`](./developer.md).

## Identidade

- **Papel:** Arquiteto — transforma requisitos em estrutura e define os padrões arquiteturais descritos nas guias da biblioteca.
- **Especialidade:** Modelagem de componentes seguindo especificamente os padrões da biblioteca.
- **Comportamento:** Respeita todas as definições, regras e padrões da biblioteca. Quando faltar informação, pergunta ao usuário antes de assumir.
- **Tom:** Operacional e factual. Define O QUE e POR QUÊ; o COMO fica com o Developer.

## Convenção de exemplo

Todo este documento usa um componente fictício chamado `ExampleComponent`, com:
- Branch de trabalho: `feature/COMP-1234/criacao-do-componente-ExampleComponent`
- Issue no issue tracker: `COMP-1234 - Criação do componente ExampleComponent`

## Terminologia

| Termo      | Definição |
|------------| --------- |
| **Layout** | Classe que define as múltiplas formas de exibir o componente. Exemplo: um `Avatar` pode exibir as 2 primeiras letras do nome ou uma foto — são 2 behaviors. |
| **State**  | Variações de estado do componente, como expandido/colapsado, não iniciado/em andamento/completo. |
| **Style**  | Conjunto de propriedades de estilização: cores, arredondamento, bordas, tipografia, espaçamentos. |

> **Regra de combinação:** Todos os states devem funcionar com todos os behaviors, e o resultado deve funcionar com todos os styles. Total de configurações = `nº states × nº behaviors × nº styles`.

## Tipos de componente

Todos os componentes e templates são organizados sob o pacote `components`. A organização interna deve refletir o domínio do componente e não depende de categorias paralelas de biblioteca.

## Estrutura de pacotes

Todo desenvolvimento ocorre dentro de `package com.example.designsystem.components`, na pasta física `/design-system/src/main/java/com/example/designsystem/components`.

```
com.example.designsystem.components
├── examplecomponent
└── exampletemplate
```

A estrutura interna de cada componente pode ser dividida em arquivos e subpacotes conforme a complexidade da implementação, sem impor uma organização por categoria de biblioteca.

## Estrutura de arquivos

Inclua apenas os arquivos necessários para a implementação. Para o `ExampleComponent`, no pacote `com.example.designsystem.components.examplecomponent`:

| Arquivo | Responsabilidade | Obrigatório |
| ------- | ---------------- | ----------- |
| `ExampleComponent.kt` | API pública e implementação visual do componente. | Sim |
| `ExampleComponentDefaults.kt` | Object com defaults: constantes, styles, types, states, behaviors e utilitários. Sem lógica de negócio nem renderização. | Sim |
| `ExampleComponentState.kt` | Holder de estado coeso (texto + mensagem etc.) com factory `rememberExampleComponentState()`. | Quando aplicável |
| `ExampleComponentEffects.kt` | Helpers `LaunchedEffect`/`DisposableEffect` (ex.: idle, debounce). | Quando aplicável |
| `ExampleComponentLoading.kt` | Estado visual de carregamento. | Quando aplicável |
| `ExamplePreviewParameterProvider.kt` | `PreviewParameterProvider` para iterar sobre dados nos previews. | Quando aplicável |

Subpacotes internos especializados (transformações, animadores, parsers) podem ser criados dentro do pacote do componente quando representarem domínios técnicos próprios (ex.: `inputtext/visualtransformation/{mask,numeric}/`). Conteúdo público nesses subpacotes só é permitido quando faz parte da API de uso (ex.: `MaskType`); a visibilidade deve refletir o contrato da API.

## Contratos por arquivo

### Contrato: `ExampleComponent.kt` (API pública)

Responsabilidades:
- Expor o composable público e a implementação visual principal.
- API previsível: parâmetros obrigatórios primeiro; opcionais com defaults depois (`modifier: Modifier = Modifier` é o **primeiro** parâmetro com default).
- Delegar para componentes auxiliares quando isso melhorar a legibilidade, mantendo-os em arquivos ou subpacotes adequados.
- Não expor detalhes de helpers privados no contrato público.
- Usar tipos das classes `ExampleComponentDefaults` para *behavior* e *state*, quando existirem.
- Manter callbacks e state hoisting explícitos no contrato.

Defaults sensatos:
- Coleções vazias: `emptyList()` (não `listOf()`).
- Strings vazias: `""` ou um helper equivalente da biblioteca.
- Callbacks opcionais podem ter default `= {}` para reduzir verbosidade. Use `(() -> Unit)? = null` quando o "não passar" precisa ser distinguível (ex.: para condicionar `clickable`).

Sobrecargas do composable público são permitidas quando representam variações da mesma API (ex.: `InputText` com `String` e com `TextFieldValue`). Todas devem delegar ao mesmo corpo de implementação, normalizando os parâmetros antes.

KDoc obrigatório, na ordem:
1. Breve descrição do propósito (2–3 linhas).
2. `@param` para cada parâmetro.
3. `@see` para cada classe de `ExampleComponentDefaults` usada no contrato (ex.: `@see ExampleComponentDefaults.Style`).
4. Explicações ou orientações adicionais (opcional, ao final).

Annotations no composable público — ordem obrigatória:
1. `@OptIn(...)` quando consumir API experimental.
2. `@Composable`.

> Os exemplos de annotations vivem em [`developer.md`](./developer.md).

### Contrato: `ExampleComponentDefaults.kt`

Centraliza tudo que é default/configurável, evitando hardcode e facilitando manutenção.

Deve conter:
- `object ExampleComponentDefaults` (nunca `class`).
- Constantes do componente (alturas, paddings fixos, tamanhos de ícone, durações, delays, max-lines) como `internal val`/`internal const val`.
- `data class Style` com propriedades de estilização, anotada com `@Immutable`.
- Funções `@Composable` que retornam variações de `Style`.
- Sealed class/interface ou enum para **types/behaviors** — somente se houver 2+ variações.
- Sealed class ou enum para **states** — somente se houver 2+ estados.
- `data class XxxData` para agregar configurações de sub-componentes (ex.: `IconButtonData(icon, onClick, contentDescription, badge)`).
- Funções utilitárias auxiliares (constantes derivadas) quando necessário. Holders de estado e factories `remember*` ficam em arquivo próprio (ver [Holders de estado](#holders-de-estado-componentstate)).

Não deve conter:
- Lógica de negócio.
- Implementação visual.

#### Modelagem de tipos

| Caso | Construção |
| ---- | ---------- |
| Variações com payloads diferentes (ex.: `Type.Image` x `Type.Initials`) | `sealed class` / `sealed interface` com `data class` / `data object` aninhados |
| Variações simples só com valores fixos (ex.: `Size.SMALL`/`LARGE`) | `enum class` com parâmetros no construtor |
| Estados/fases internas | `internal sealed interface` no `Defaults` |
| Agregadores de configuração de sub-componentes | `data class XxxData` no `Defaults` |

#### Anotações de estabilidade

- Toda `data class Style` é anotada com `@Immutable`.
- Estruturas auxiliares com mutabilidade controlada (medições, métricas) usam `@Stable`.

#### Convenção de nomes de styles

| Nome | Quando usar |
| ---- | ----------- |
| `defaultStyle()` / `normalStyle()` | Variação principal sobre fundo claro |
| `customStyle()` | Variação principal sobre fundo colorido/escuro |
| `primaryStyle()`, `secondaryStyle()`, `tertiaryStyle()` | Hierarquia de ênfase |
| `criticalStyle()` | Variação destrutiva/erro |
| `<variant>OnColorStyle()` | Variante do style para uso sobre fundo colorido |
Todas são `@Composable` e retornam `Style`.

#### Composição de styles

O `Style` de um componente "container" deve compor os `Style`s dos componentes filhos como propriedades do tipo `<Filho>Defaults.Style`. Cada style factory do container monta os styles filhos chamando suas factories correspondentes.

> Exemplo de código: ver `developer` → "Não instanciar estilos de outros componentes fora do `Style`".

### Implementação direta do componente

A implementação visual fica diretamente no arquivo `ExampleComponent.kt`, junto da API pública. A função pública pode delegar para componentes auxiliares quando isso melhorar a organização.

Responsabilidades:
- Toda a lógica de renderização e composição visual.
- Estilizar exclusivamente via `ExampleComponentDefaults.Style`.
- Estado externo favorecido (state hoisting).
- Constantes vêm de `ExampleComponentDefaults` — nunca números mágicos ou strings hardcoded.
- Componentes auxiliares podem ficar em arquivos próprios, com visibilidade definida conforme o contrato da API e nomes descritivos (`ReminderSurface`, `CloseButton`, `ButtonContent`).
- Quando o arquivo crescer muito (mais de aproximadamente 400 linhas), criar utilitários privados ou arquivos auxiliares no mesmo pacote, sem criar uma API paralela.
- Previews ficam no próprio arquivo do componente ou em provider dedicado, cobrindo as combinações relevantes.

#### Click, ripple e estados interativos

- Usar `ripple(color = style.rippleColor, bounded = true)` — nunca o `LocalIndication` padrão.
- Sempre acompanhar `interactionSource = remember { MutableInteractionSource() }`.
- Cliques opcionais são tipados como `(() -> Unit)? = null` no contrato e habilitados por composição condicional de modifiers.
- Callbacks têm nomes semânticos: `onClick`, `onCloseButtonClick`, `onValueChange`, `onIdle`, `onReminderClick`.

#### Acessibilidade

- `Icon`s decorativos sempre com `contentDescription = null` quando o parent já carrega semântica.
- `contentDescription` de elementos acionáveis vem de um recurso de string descritivo — **nunca hardcoded**.
- Usar `Modifier.semantics { role = Role.Button; contentDescription = ... }` no nó clicável raiz.
- Para componentes com texto rico (HTML), passar `contentDescription` em paralelo (texto plano legível).

## Previews

- Todo composable público deve ter ao menos um preview.
- Nome: `<NomeComponente>[Sufixo]Preview`. Ex.: `ExamplePreview`, `ExamplePrimaryPreview`, `ExampleExpandedPrimaryPreview`.
- Cada layout relevante deve ter seu próprio preview.
- Previews devem cobrir ao menos o estado padrão e os principais layouts.
- Devem ser simples, focados em mostrar o visual; sem lógica complexa ou dependências externas.
- Dados via mocks estáticos ou `PreviewParameterProvider`.
- Funções composables auxiliares de preview devem ser privadas e ter sufixo `Preview` (ex.: `ContentPreview`).
- Quando o componente tem comportamento dinâmico (colapsar, expandir, loading), usar `mutableStateOf` + `remember` no preview para demonstrá-lo.

Convenções complementares:
- Função utilitária `<Component>ContentPreview(...)` pode ser usada quando a mesma estrutura é reutilizada em vários `@Preview` (estados enabled/disabled, on-color/normal, etc.).
- Para previews em fundo colorido, usar `@Preview(showBackground = true, backgroundColor = ...)` ou envolver o componente em um container Material.

## Considerações gerais

**Consistência de design system**
- Reutilizar padrões já existentes em `components/*`.
- Manter nomenclatura consistente com o nome do componente.
- Evitar criar nova convenção sem necessidade.
- Implementação visual mantida na API pública, com helpers organizados em arquivos ou subpacotes quando necessário.
- API pública limpa, estável e sem leak interno.

**Testes unitários**
- Não implementar testes unitários para componentes de UI.
- Garantir que a implementação seja testável: lógica de negócio separada do visual, state hoisting, acessibilidade.

## Tokens do tema

Use o tema Material 3 como fonte de tokens visuais:

- `MaterialTheme.colorScheme` para cores de superfície, conteúdo, borda e estados.
- `MaterialTheme.typography` para estilos tipográficos.
- `MaterialTheme.shapes` para formas e arredondamentos.
- Use `ExampleComponentDefaults` para dimensões, espaçamentos, opacidades e elevações que não façam parte do tema Material.
- Evite cores hexadecimais, tipografia inline e strings hardcoded. Prefira o tema Material ou valores centralizados em `*Defaults`.

**Exemplo:**

```kotlin
val backgroundColor = MaterialTheme.colorScheme.surface
val contentColor = MaterialTheme.colorScheme.onSurface
val borderColor = MaterialTheme.colorScheme.outline
val textStyle = MaterialTheme.typography.bodyLarge
val shape = MaterialTheme.shapes.medium

@Composable
internal fun ExampleComponentDefaults.defaultStyle(): Style = Style(
    backgroundColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
    borderColor = MaterialTheme.colorScheme.outline,
    textStyle = MaterialTheme.typography.bodyLarge,
    shape = MaterialTheme.shapes.medium,
)
```

## Slots

- Conteúdo customizável é exposto via slots `@Composable () -> Unit` ou `@Composable ColumnScope.() -> Unit`.
- Slots vão **antes** dos parâmetros opcionais ou ao final do composable (idiomático Kotlin para trailing lambda).
- Nome semântico: `textSlot`, `bottomSheetContent`, `content`. Evitar slots genéricos sem nome.

## Conteúdo visual

Prefira os tipos padrão do Compose na API pública: `String`, `Color`, `Painter`, `ImageVector` e slots `@Composable`. Crie wrappers próprios apenas quando houver uma necessidade clara de domínio e documente o contrato no componente.

## Holders de estado (`<Component>State`)

Quando o componente precisa de um estado coeso (texto + mensagem + cursor + etc.):

- Criar arquivo `<Component>State.kt` no mesmo pacote do composable público.
- Classe `<Component>State` anotada com `@Stable`, `internal constructor`.
- Expor factory `@Composable fun remember<Component>State(initialX = ..., initialY = ...): <Component>State` com `remember { ... }`.
- Permitir overloads de inicialização (ex.: `String` vs `TextFieldValue`).
- Documentar com KDoc + bloco de uso real (`**Example usage:**`).

## Effects auxiliares

Comportamentos baseados em `LaunchedEffect`/`DisposableEffect` ficam em `<Component>Effects.kt` (mesmo pacote do composable público):

- Funções marcadas `@Composable @NonRestartableComposable` quando aplicável.
- Capturar callbacks com `rememberUpdatedState` para evitar relançamentos.
- KDoc com `**Example usage:**`.

## Skeleton de loading

Quando o componente tem estado "carregando":

- Criar arquivo separado `<Component>Loading.kt` no mesmo pacote do composable público.
- Composable público: `<Component>Loading(modifier: Modifier = Modifier)`.
- Usar a primitiva de carregamento escolhida pelo projeto, com forma e dimensões coerentes com o componente.
- Tamanhos coerentes com as constantes do `<Component>Defaults` (mesmo `buttonHeight`, etc.).

## Composição entre componentes da biblioteca

- Um composable público pode usar **outros composables públicos da biblioteca** (ex.: `Header` usa `IconButton`, `Button` pode usar `IconButton` em previews).
- O `Defaults` de um container referencia `Defaults` de filhos via tipos públicos (`AvatarDefaults.Type`, `BadgeDefaults.Type`, `IconButtonDefaults.Style`).
- Nunca acessar implementação `internal` de outro componente.
