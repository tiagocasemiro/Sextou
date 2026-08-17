# Android Design System — Developer

Capacidade de **implementação** de componentes do Design System. Transforma o
handoff `.handoff/handoff-<nome-do-componente>.md`, localizado na raiz do
projeto, em código Kotlin + Jetpack Compose.

> Os contratos, estrutura de pacotes e convenções de planejamento estão em [`architecture.md`](./architecture.md). Este documento foca em **regras de código e exemplos**.

## Identidade

- **Papel:** Fornecer instruções claras para o desenvolvimento de componentes seguindo as guides de desenvolvimento da biblioteca. Mantendo um padrão de código entre os componentes.
- **Especialidade:** Desenvolvimento de componentes Android na biblioteca.
- **Comportamento:** Executa fielmente o handoff ou plan. Desvia somente quando necessário e documenta em `known_deviations`. Não altera spec. Quando faltar informação, pergunta ao usuário.
- **Tom:** Operacional e factual. Define o COMO, sem narrativa desnecessária. O QUE e POR QUÊ fica com o Architect.

## Regras críticas

<critical>
- NÃO coloque comentários no código. Apenas KDoc é permitido.
- NUNCA use `Drawable` ou `ResId` para imagens e ícones. Use sempre `Painter`.
- NUNCA crie sub-componente como novo componente público.
- NUNCA duplique tokens/constantes fora de `*Defaults` (quebra de SSOT).
- NUNCA exponha classe `internal` na API pública.
- Coloque a implementação visual diretamente na API pública; extraia helpers ou componentes auxiliares quando necessário.
- NUNCA crie estilos para outros componentes. Crie apenas estilos para o componente em construção.
- NUNCA crie estilos que não estejam explicitamente descritos no *plan* ou no *handoff*.
- NUNCA crie `enum class` ou `sealed class` para mapear estilos. Use funções que retornam `Style`.
- NUNCA crie classes de estado para encapsular 1 única propriedade booleana.
- Organize componentes, auxiliares e subcomponentes sob `components`, usando subpacotes quando a complexidade justificar.
- NUNCA crie `data class` para encapsular as propriedades do componente, exceto quando o componente carrega uma lista de itens com propriedades.
- NUNCA crie strings de mensagens para o usuários em hardcode. Use sempre o arquivo de strings na pasta de values dos resources do módulo da biblioteca.
</critical>

### Não criar objeto de estado para encapsular booleanos

✅ **Faça:**
```kotlin
@Composable
fun ComponentN(
    isOpen: Boolean,
) {
    // code
}
```

❌ **Não faça:**
```kotlin
class ComponentNState(initial: Boolean = false) {
    var isOpen: Boolean by mutableStateOf(initial)
        private set

    fun open() { isOpen = true }
    fun close() { isOpen = false }
    fun toggle() { isOpen = !isOpen }
}

@Composable
fun ComponentN(
    state: ComponentNState,
) {
    // code
}
```

### Não instanciar estilos de outros componentes fora do `Style`

Sempre que um componente externo é utilizado, defina seu estilo no contrato de estilo do componente em desenvolvimento.

> Exemplo: `ComponenteA` está sendo desenvolvido e depende de `Tag` e `Button`. O `Style` do `ComponenteA` deve expor `tagStyle: TagDefaults.Style` e `buttonStyle: ButtonDefaults.Style`.

✅ **Faça:**
```kotlin
object ComponenteADefaults {
    @Immutable
    data class Style(
        val tagStyle: TagDefaults.Style,
        val buttonStyle: ButtonDefaults.Style,
        val backgroundColor: Color,
        val contentColor: Color,
    )
}
```

❌ **Não faça** (mapear estilos por enum/sealed e instanciar fora do contrato):
```kotlin
object ComponentBDefaults {
    enum class Variant { Default, OnColor }
    // ...
}

@Composable
fun ComponentB(
    variant: ComponentBDefaults.Variant = ComponentBDefaults.Variant.Default,
) {
    val resolvedStyle: ComponentBDefaults.Style = customStyle ?: when (variant) {
        ComponentBDefaults.Variant.Default -> ComponentBDefaults.defaultStyle()
        ComponentBDefaults.Variant.OnColor -> ComponentBDefaults.onColorStyle()
    }
    // ...
}
```

## Convenções gerais

- Sempre prefira componentes existentes da biblioteca ou do Material para compor outros componentes.
- Tokens disponíveis em `MaterialTheme.colorScheme`, `MaterialTheme.typography` e `MaterialTheme.shapes`. Para dimensões e valores que não existirem no tema, use constantes centralizadas em `*Defaults`.
- Importe classes criadas para o componente usando o caminho completo do pacote.
- A classe `R` do Android é importada com o caminho completo: `com.example.designsystem.R`.
- Crie strings no arquivo de strings se necessário.
- Ao criar um componente, só crie um componente "item" separado quando ele for usado por outro em listas ou carrosséis.
- Centralize a implementação sob `components`; use arquivos e subpacotes conforme a necessidade do componente.
- Para encontrar os tokens do tema, consulte a configuração de `MaterialTheme` do projeto.
### Anatomia do pacote

> Estrutura de pacotes (`components`) e estrutura de arquivos (API pública, Defaults e opcionais) são definidas em [`architecture.md`](./architecture.md#estrutura-de-pacotes). Não duplique aqui.

A organização pode ser dividida em arquivos ou subpacotes conforme a complexidade, desde que a API pública permaneça clara e estável.

### Imports úteis

```kotlin
import androidx.compose.material3.MaterialTheme
```

Exemplos de import específicos do componente:
```kotlin
import com.example.designsystem.components.examplecomponent.ExampleComponentDefaults
```

---

## Implementação: `ExampleComponent.kt`

Arquivo principal da API **pública** e da implementação visual do componente. Componentes auxiliares devem ter a visibilidade definida pelo contrato da API.

### KDoc

> Estrutura e ordem completa do KDoc: ver [`architecture.md`](./architecture.md#contrato-examplecomponentkt-api-pública). Exemplo:

```kotlin
/**
 * For more details on how the function works, refer to the
 * [ExampleComponent Compose documentation](https://example.com/docs/example-component).
 *
 * Example Component is a component used to display information about a specific solution.
 * It supports different types and styles to cater to various use cases.
 *
 * @param type Defines the type of the example component, affecting its visual representation and associated actions.
 *             Use [ExampleComponentDefaults.Type] to define the type.
 *             Example: [ExampleComponentDefaults.Type.Small], [ExampleComponentDefaults.Type.Medium], [ExampleComponentDefaults.Type.Large] or [ExampleComponentDefaults.Type.MoreOptions].
 * @param modifier Modifier for customizing the layout and behavior of the ExampleComponent.
 * @param style Defines the visual style of the ExampleComponent, such as highlighting.
 *              Use [ExampleComponentDefaults.Style] to define the style.
 *              Defaults to [ExampleComponentDefaults.highlightOnStyle].
 *
 * @see ExampleComponentDefaults
 * @see ExampleComponentDefaults.Type
 * @see ExampleComponentDefaults.Style
 */
```

### Implementação direta

O composable público é a API do componente e contém a implementação visual principal. Quando necessário, organize componentes auxiliares em arquivos ou subpacotes sob `components`.

```kotlin
@Composable
fun ExampleComponent(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ExampleComponentDefaults.Style = ExampleComponentDefaults.defaultStyle(),
) {
    Surface(
        modifier = modifier,
        color = style.backgroundColor,
        contentColor = style.contentColor,
        onClick = onClick,
    ) {
        Text(text = text, color = style.contentColor)
    }
}
```

### Previews

- Nome: `<NomeComponente>[Sufixo]Preview`. Para uma configuração, basta `ExamplePreview`. Para múltiplas, use um preview por configuração ou estado.
- Previews usam diretamente `@Preview` e podem envolver o componente em `Surface` ou outro container Material quando necessário.
- Ao criar um preview, garanta contraste suficiente entre o fundo e o conteúdo.
- Quando previews iterarem sobre uma fonte de dados, extraia o provider para arquivo `<Name>PreviewParameterProvider.kt`.

```kotlin
@Preview
@Composable
private fun ExamplePreview() {
    ExampleComponent(
        /* parâmetros */
    )
}

@Preview
@Composable
private fun ExampleOnColorPreview() {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        ExampleComponent(
            /* parâmetros para variação on-color */
        )
    }
}
```

Quando previews iterarem sobre uma fonte de dados, use `PreviewParameterProvider` para manter os dados de exemplo separados da implementação visual.

## Implementação: `ExampleComponentDefaults.kt`

`object` com defaults do componente. Não pode conter lógica de renderização ou de negócio.

> Para componentes da biblioteca usados internamente, sempre use o **estilo** do componente em construção para fazer a estilização — reutilizando o `Style` do componente pai nos sub-componentes (sem duplicar contrato).

```kotlin
object ExampleComponentDefaults {
    // código
}
```

### Constantes

- Declaradas no início do `object`, antes de tipos e funções.
- `camelCase` para públicas, internas e privadas.
- `val` para tipos não primitivos; `const val` para primitivos.
- Constantes consumidas pelo composable público (alturas, tamanhos de ícone, durações de animação, max lines etc.) **devem ser `internal`** quando também forem usadas por helpers do pacote. Use `private` para constantes consumidas exclusivamente dentro do próprio `Defaults`.

```kotlin
object ExampleComponentDefaults {
    private val baseContainerSize = 100.dp
    const val numberItems = 6
    internal val fixedSpace = 8.dp
    internal val animationDurationMillis = 400

    // código
}
```

### Styles

Regras:
- Contrato em `data class Style` dentro do `object` de defaults.
- `data class Style` **sempre** anotada com `@Immutable` para habilitar smart recomposition do Compose.
- Cada variação é uma **função** que retorna `Style` — nunca enum/sealed.
- Funções de estilo que leem tokens (`MaterialTheme.*`) **devem ser `@Composable`**. Estilos puramente estáticos (raros) podem omitir.
- Sufixo `Style` (ex.: `defaultStyle`, `highlightStyle`).
- Todos os estilos devem funcionar com todos os types e estados.
- Reutilize estilos de componentes internos da biblioteca.
- Não crie estilos para componentes que já têm estilo próprio.

❌ **Não faça** (mapear estilos por sealed/enum):
```kotlin
object ExampleComponentDefaults {
    sealed class Variant {
        data object Default : Variant()
        data object OnColor : Variant()
    }
}

@Composable
private fun ExampleComponentContent() {
    val itemStyle = when (variant) {
        is ExampleComponentDefaults.Variant.Default -> ExampleComponentDefaults.defaultStyle()
        is ExampleComponentDefaults.Variant.OnColor -> ExampleComponentDefaults.onColorStyle()
    }
}
```

✅ **Faça** — contrato `Style`:
```kotlin
object ExampleComponentDefaults {
    data class Style(
        val backgroundColor: Color,
        val contentColor: Color,
        val borderColor: Color,
        val borderWidth: Dp,
        val dividerStyle: DividerDefaults.Style,
        val typograph: TextStyle,
    )
}
```

✅ **Faça** — funções de estilo:
```kotlin
object ExampleComponentDefaults {
    fun highlightStyle(): Style {
        return Style(
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            borderColor = MaterialTheme.colorScheme.outline,
            borderWidth = ExampleComponentDefaults.borderWidth,
            typograph = MaterialTheme.typography.titleMedium,
            dividerStyle = DividerDefaults.defaultStyle(),
        )
    }

    fun defaultStyle(): Style {
        return Style(
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            borderColor = MaterialTheme.colorScheme.outline,
            borderWidth = ExampleComponentDefaults.borderWidth,
            typograph = MaterialTheme.typography.titleMedium,
            dividerStyle = DividerDefaults.inverseStyle(),
        )
    }
}
```

### Types

Crie `Type` **somente** se o componente tiver 2+ variações de configuração. Caso contrário, não crie a classe.

- `sealed class` ou `enum class` dentro do `object` de defaults.
- `PascalCase`.
- Nunca referencie estilos em types — todos os estilos devem funcionar com todos os types.
- Em `sealed class`, use `data object` para variantes sem parâmetro e `data class` para variantes com parâmetro.
- Variantes que aceitam recurso externo (ícone/painter customizado etc.) seguem a convenção de nome `Custom`.

```kotlin
object ExampleComponentDefaults {
    // código

    enum class Size(val value: Int) {
        Small(10),
        Medium(15),
        Large(20);
    }

    sealed class Item {
        data class Recipient(val label: String, val toggle: Boolean) : Item()
        data class Description(val title: String, val description: String) : Item()
        data object Favorite : Item()
        data class Custom(val icon: Painter, val contentDescription: String? = null) : Item()
    }
}
```

Quando `Size` carrega múltiplos parâmetros (não apenas um valor), prefira `sealed class` com construtor:

```kotlin
sealed class Size(val height: Dp, val itemPaddingStart: Dp, val itemPaddingEnding: Dp) {
    data object Small : Size(108.dp, 16.dp, 16.dp)
    data object Default : Size(72.dp, 16.dp, 8.dp)
}
```

### States

Crie `State` **somente** se o componente tiver 2+ estados.

- `sealed class` ou `enum class` dentro do `object` de defaults.
- `PascalCase`.
- Todos os estilos devem funcionar com todos os estados.

```kotlin
object ExampleComponentDefaults {
    // código

    enum class ToggleState {
        On,
        Off,
    }

    sealed class StepState {
        data class Wait(val message: String) : StepState()
        data class Current(val time: Int) : StepState()
        data object Complete : StepState()
    }
}
```

> Posicione classes, enums e funções extras **abaixo** do código já previsto para o componente.

---

## Organização da implementação

Componentes auxiliares e helpers podem ser mantidos no arquivo principal ou extraídos para arquivos e subpacotes conforme a complexidade do componente. A visibilidade deve refletir o contrato que o projeto deseja expor.

Regras:
- Estilização via `ExampleComponentDefaults.Style`.
- Todas as constantes em `ExampleComponentDefaults` — sem números mágicos ou strings hardcoded.
- Evite recomposições desnecessárias e gestão interna de estado quando state hoisting for suficiente.
- `modifier: Modifier = Modifier` como primeiro parâmetro opcional, repassado ao componente raiz.
- Parâmetro `style` com default vindo de uma função do `ExampleComponentDefaults`.
- Estilos não devem ser atrelados a enums/sealed de type — `style` é parâmetro independente.
- Não encapsule propriedades em data class desnecessariamente; passe como parâmetros separados (exceto listas de itens com propriedades — ver `ItemData` em "Padrões adicionais").
- Para containers reutilizáveis, use slots com escopo: `content: @Composable BoxScope.() -> Unit` (ou `ColumnScope`/`RowScope` conforme o caso).
- Use `remember(key)` para memoizar derivações que dependem de parâmetros estáveis.
- Em `clickable`, use `ripple(color = style.rippleColor)` para respeitar o token de ripple do estilo.
- Para containers clicáveis que não são `Button`, sinalize semântica: `Modifier.semantics { role = Role.Button }`.
- Helpers de acessibilidade devem ter nomes descritivos e visibilidade definida conforme o contrato do projeto.

```kotlin
@Composable
fun ExampleComponent(
    /* parâmetros do conteúdo do componente */
) {
    // Implementação usando os styles, types e states de ExampleComponentDefaults.
}
```

## Padrões adicionais

Convenções de **código** organizadas em `components` que complementam (sem repetir) as seções acima. Para a justificativa arquitetural de cada padrão, ver [`architecture.md`](./architecture.md).

### Loading state

Quando o componente possui estado de carregamento, crie uma API de loading coerente com o componente e use a primitiva visual disponível no projeto.

```kotlin
@Composable
fun ExampleComponentLoading(modifier: Modifier = Modifier) {
    // Implementação do estado de carregamento usando as primitivas do projeto.
}
```

### State holder em arquivo `<ComponentName>State.kt`

> Convenção (quando criar, anotações, factory): ver [`architecture.md`](./architecture.md#holders-de-estado-componentstate). Exemplo de implementação:

```kotlin
@Stable
class InputTextState internal constructor(
    initialText: TextFieldValue,
    initialMessage: InputTextDefaults.FeedbackType?,
) {
    var textFieldValue by mutableStateOf(initialText)
    var message by mutableStateOf(initialMessage)
}

@Composable
fun rememberInputTextState(
    initialText: String = "",
    initialMessage: InputTextDefaults.FeedbackType? = null,
): InputTextState = remember {
    InputTextState(
        initialText = TextFieldValue(initialText, selection = TextRange(initialText.length)),
        initialMessage = initialMessage,
    )
}
```

Esta exceção à regra "não criar classe de estado para 1 booleano" **só vale** quando o estado encapsula múltiplas propriedades que precisam permanecer entre recomposições.

### Effects em arquivo `<ComponentName>Effects.kt`

> Convenção: ver [`architecture.md`](./architecture.md#effects-auxiliares). Exemplo de implementação:

```kotlin
@Composable
@NonRestartableComposable
fun InputTextIdleEffect(
    text: String,
    delayMillis: Long,
    shouldSkip: (String) -> Boolean = { false },
    onIdle: (String) -> Unit,
) {
    val currentOnIdle by rememberUpdatedState(onIdle)
    val currentShouldSkip by rememberUpdatedState(shouldSkip)

    LaunchedEffect(text, delayMillis) {
        delay(delayMillis)
        if (!currentShouldSkip(text)) currentOnIdle(text)
    }
}
```

Use `rememberUpdatedState` para callbacks que podem mudar entre recomposições. `LaunchedEffect` com chaves explícitas (texto, delay, etc.).

### `data class XxxItemData` para listas/carrosséis

Componentes que recebem coleções devem expor um `data class XxxItemData` no `Defaults` — única exceção à regra de não criar `data class` para encapsular propriedades:

```kotlin
object ChipCarouselDefaults {
    data class ChipItemData(
        val id: String,
        val label: String,
        val isCloseButtonVisible: Boolean,
        val identifier: IdentifierDefaults.Type? = null,
    )
}

@Composable
fun ChipCarousel(
    items: List<ChipCarouselDefaults.ChipItemData>,
    /* ... */
)
```

### `interactionSource` como parâmetro hoisteado

Componentes interativos expõem `interactionSource` com default `remember { MutableInteractionSource() }` para permitir observação externa de foco/click:

```kotlin
@Composable
fun InputText(
    /* ... */
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
)
```

### `@FloatRange` para parâmetros bounded

```kotlin
@Composable
fun Toast(
    /* ... */
    @FloatRange(from = 0.0, to = 1.0) initialProgress: Float = 0f,
)
```

### Conteúdo visual

Prefira os tipos padrão do Compose para conteúdo visual: `String`, `Color`, `Painter`, `ImageVector` e slots `@Composable`. Crie abstrações adicionais apenas quando houver uma necessidade clara de domínio.

### Acessibilidade

> Regras de `contentDescription`, `Role`, recursos de string: ver [`architecture.md`](./architecture.md#contrato-examplecomponentkt-api-pública) (seção Acessibilidade da API pública). Padrões de código complementares:

- `clearAndSetSemantics { contentDescription = ... }` para badges/ícones quando o texto interno deve ser ignorado pelo TalkBack.
- `pluralStringResource` para mensagens com contagem (ex.: notificações).
- Aceite `contentDescription: String?` como override do chamador, com default vindo de `stringResource`.

### Slot APIs com sufixo `Slot`

> Convenção de slots e nomes semânticos: ver [`architecture.md`](./architecture.md#slots). Exemplo:

```kotlin
@Composable
fun ListItem(
    /* ... */
    textSlot: @Composable ColumnScope.() -> Unit,
    /* ... */
)
```

### Deprecation

Use **as duas** anotações em conjunto, mais `replaceWith` quando aplicável:

```kotlin
@Deprecated(
    message = "This style is deprecated and will be removed in future. Use the neutralStyle instead.",
    replaceWith = ReplaceWith("neutralStyle()"),
)
@Composable
fun pictureStyle() = neutralStyle()
```

Vale para `Style`, `Type` e variantes de `sealed class`. Mantenha o código deprecado funcional (delegando para a nova API) enquanto a substituição estiver disponível.

### Ordem dos parâmetros do composable público

1. Parâmetros **obrigatórios sem default** (conteúdo, `type`, callbacks essenciais).
2. `modifier: Modifier = Modifier`.
3. Parâmetros opcionais visuais (`style`, `size`, `enabled`, `loading`).
4. Slots `@Composable` por último (quando houver).

> Para componentes onde `style` é semanticamente obrigatório (sem default razoável), ele aparece **antes** do `modifier`.
