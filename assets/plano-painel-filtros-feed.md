# Plano de implementação — painel de filtros da tela principal

**Destino do documento:** `assets/plano-painel-filtros-feed.md`  
**Status:** implementado em 2026-09-12.

## 1. Objetivo e comportamento aprovado

Substituir o diálogo simples acionado pelo filtro do campo de busca por um painel inferior modal, seguindo o [Figma — nó 209:29](https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=209-29).

**Esta entrega contempla somente interface e estado de seleção**, conforme escolha do usuário. “Aplicar filtros” confirma as opções e fecha o painel; não altera os estabelecimentos exibidos nem consulta a API. A filtragem funcional será outra entrega.

Comportamento obrigatório:

- Abrir pelo botão de filtro já existente na barra de busca.
- Na primeira abertura, apresentar todas as opções desmarcadas.
- Permitir múltipla seleção em tipos, preços e categorias.
- Permitir ligar e desligar cada interruptor independentemente.
- Editar um rascunho enquanto o painel estiver aberto.
- Confirmar o rascunho somente em “Aplicar filtros”.
- Descartar o rascunho ao fechar pelo X, botão Voltar, toque externo ou gesto de fechamento.
- Reabrir com a última seleção confirmada.
- Manter a seleção durante a vida da `FeedViewModel`, inclusive em recomposições e mudanças de configuração.
- Reiniciar sem seleção quando uma nova `FeedViewModel` for criada; não adicionar persistência.
- Permitir confirmar uma seleção vazia. Para desmarcar tudo, o usuário desmarca as opções; não adicionar botão “Limpar”, ausente no desenho.
- Ignorar confirmações repetidas depois que o painel estiver fechado.

A busca textual atual continua funcionando sobre os dados carregados. Carregamento diário, mapa, favoritos, visitados e fotos permanecem com seus fluxos existentes.

O antigo `openOnly` será substituído pelo estado visual do novo painel. Seu predicado que oculta estabelecimentos fechados não será conectado ao novo interruptor “Aberto agora”: isso contrariaria o escopo de interface aprovado.

## 2. Especificação visual e contratos

### Conteúdo e identificação das opções

Manter a ordem, os textos e os agrupamentos do Figma.

| Grupo | Identificador na apresentação | Texto |
|---|---|---|
| Tipo de local | `TYPE_BOTECO` | Boteco raiz |
| Tipo de local | `TYPE_SKEWER` | Espetinho / Podrão |
| Tipo de local | `TYPE_WINE_STORE` | Adega |
| Tipo de local | `TYPE_KARAOKE` | Karaokê |
| Tipo de local | `TYPE_FOOD_TRAILER` | Trailer de comida |
| Faixa de preço | `PRICE_LOW` | $ / Baratinho |
| Faixa de preço | `PRICE_MEDIUM` | $$ / Médio |
| Faixa de preço | `PRICE_HIGH` | $$$ / Caprichado |
| Outros filtros | `OPEN_NOW` | Aberto agora |
| Outros filtros | `KIDS_SPACE` | Espaço kids |
| Outros filtros | `LIVE_MUSIC` | Música ao vivo |
| Categorias | `CATEGORY_KARAOKE` | Karaokê |
| Categorias | `CATEGORY_KIDS` | Espaço Kids |
| Categorias | `CATEGORY_STREET_FOOD` | Podrões |
| Categorias | `CATEGORY_WINE_STORE` | Adegas |
| Categorias | `CATEGORY_LIVE_MUSIC` | Ao vivo |
| Categorias | `CATEGORY_24_HOURS` | 24h |

Opções semelhantes de grupos diferentes permanecem independentes nesta entrega. Por exemplo, selecionar “Espaço kids” não seleciona automaticamente a categoria “Espaço Kids”.

### Apresentação

O Figma mostra um painel inferior com alça, título “FILTROS”, botão de fechamento, quatro seções separadas e ação principal laranja.

Implementar:

- `ModalBottomSheet` Material 3, inicialmente expandido, sem parada intermediária.
- Cabeçalho com título e fechamento; corpo rolável; ação principal acessível na parte inferior.
- Chips com quebra de linha para tipos e categorias.
- Três opções de preço com símbolo e descrição. Em largura insuficiente ou fonte ampliada, permitir quebra de linha.
- Interruptores Material com rótulos associados.
- Fundo escuro, superfícies tonais, texto claro e ação principal com tokens Sextou.
- Conteúdo adaptável à janela e aos insets; não reproduzir moldura de celular, relógio ou elementos do sistema desenhados no Figma.
- Área interativa mínima de 48 dp.
- Previews padrão, múltiplas seleções, desabilitado e fonte ampliada.

**Decisões de adaptação:** usar a tipografia atualmente disponibilizada pelo tema Sextou; não adicionar fontes para reproduzir SF Pro ou Barlow do desenho. Usar o botão Sextou existente, com altura padrão de 48 dp, documentando a diferença para os 52 px do Figma.

Estados selecionados, não demonstrados no nó fornecido, usarão cor primária, borda de destaque e indicação semântica de seleção. Registrar isso como decisão de implementação, não como informação extraída do Figma.

### Divisão entre módulos

| Local | Responsabilidade |
|---|---|
| `design-system` | Renderização, estilos, controles, acessibilidade e previews |
| `app`, feature `feed` | Catálogo de opções, strings, estado confirmado, rascunho e callbacks |
| `domain`, `local`, `networking` | Nenhuma mudança funcional nesta entrega |

Criar `SextouFilterSheet` no pacote `com.sextou.designsystem.component.filtersheet`, acompanhado de `SextouFilterSheetDefaults`.

O componente recebe dados genéricos; não conhece estabelecimentos, categorias de negócio, ViewModels ou UseCases.

Contrato público definido:

| Parâmetro | Tipo / responsabilidade |
|---|---|
| `title` | `String`, título |
| `groups` | Lista de `SextouFilterSheetDefaults.GroupData` |
| `selectedIds` | `Set<String>`, seleção controlada externamente |
| `applyLabel` | `String`, texto da confirmação |
| `closeContentDescription` | `String`, descrição acessível do fechamento |
| `onSelectionChanged` | `(String, Boolean) -> Unit` |
| `onApply` | `() -> Unit` |
| `onDismiss` | `() -> Unit` |
| `modifier` | Primeiro parâmetro opcional |
| `enabled` | `Boolean = true`; desabilita seleção e confirmação, permite fechar |
| `style` | `SextouFilterSheetDefaults.Style`, com factory baseada nos tokens |

Dentro de `Defaults`:

```kotlin
enum class GroupLayout {
    CHIPS,
    PRICE_CARDS,
    SWITCHES,
}

data class OptionData(
    val id: String,
    val label: String,
    val supportingText: String? = null,
)

data class GroupData(
    val id: String,
    val title: String,
    val layout: GroupLayout,
    val options: List<OptionData>,
)
```

Adicionar `Style` com `@Immutable`, contendo os estilos de superfície, textos, opções, divisórias e botão. Compor o estilo do botão utilizando `SextouButtonDefaults.Style`.

Somente estado técnico do modal, rolagem e interações pode existir internamente. A seleção sempre vem dos parâmetros.

## 3. Etapas e subagentes

O agente principal coordena a execução, entrega contexto a cada subagente, revisa suas saídas e autoriza a próxima etapa. Os nomes abaixo representam papéis; não pressupõem modelos ou ferramentas específicos.

| Etapa | Responsável | Dependência | Entrega |
|---|---|---|---|
| 0 | Agente principal | Nenhuma | Contexto, limites e contratos congelados |
| 1 | Subagente de handoff | Etapa 0 | Especificação visual documentada |
| 2 | Subagente de estado Android | Etapa 1 | Estado e ações na ViewModel |
| 3 | Subagente de design system | Etapa 1 | Componente stateless e previews |
| 4 | Subagente de integração Compose | Etapas 2 e 3 | Painel conectado à tela principal |
| 5 | Subagente de testes e revisão | Etapa 4 | Evidências funcionais, visuais e de compilação |
| 6 | Agente principal | Etapa 5 | Revisão final e documentação |

As etapas 2 e 3 podem acontecer em paralelo. Nenhum subagente altera arquivos atribuídos a outro sem coordenação do principal.

### Etapa 0 — Preparação pelo agente principal

1. Ler as regras aplicáveis em `.codex/rule/`, as skills locais e `DESIGN.md`.
2. Inspecionar alterações existentes e preservá-las.
3. Conferir os contratos atuais de `FeedScreen`, `FeedDestination`, `FeedUiState` e `FeedViewModel`.
4. Distribuir este plano integralmente aos subagentes.
5. Registrar que esta entrega é de interface, sem filtragem dos resultados.
6. Reservar para si as alterações em `assets/agent-decision.md` e a gravação deste plano.

**Critério de saída:** cada subagente conhece seus arquivos, dependências, contrato e verificações.

### Etapa 1 — Subagente de handoff

**Skills:** `handoff-ui-component`, seguida de `design-system`.

Produzir `.handoff/handoff-sextou-filter-sheet.md` com o template local completo: Propriedades, Styles, Acessibilidade, Layouts, States, Constraints, Anatomy, Description, Animation e Build.

Instruções:

1. Usar o nó `209:29` como fonte visual.
2. Isolar o painel inferior do restante da tela.
3. Registrar os quatro grupos e as 17 opções da tabela.
4. Separar evidências do Figma das decisões deste plano.
5. Documentar seleção, confirmação, cancelamento, rolagem e estado desabilitado.
6. Mapear valores observados para tokens existentes.
7. Registrar adaptações de tipografia, dimensões e acessibilidade.
8. Não incluir Kotlin no handoff; o código pertence à implementação.

**Critério de saída:** documento sem placeholders e compatível com o contrato da seção 2.

### Etapa 2 — Subagente de estado Android

**Skill:** `architecture`.

**Propriedade de arquivos:** modelos de estado do feed, `FeedViewModel` e seus testes.

Criar um enum `FeedFilterOption` com os 17 identificadores da tabela.

Adicionar ao `FeedUiState` os campos abaixo. O trecho representa propriedades a inserir na classe existente, preservando os demais campos:

```kotlin
val confirmedFilterOptions: Set<FeedFilterOption> = emptySet(),
val draftFilterOptions: Set<FeedFilterOption>? = null,
```

Usar `draftFilterOptions != null` como condição de visibilidade. Não manter um segundo booleano mutável representando o mesmo estado.

Substituir as ações antigas pelas seguintes:

```kotlin
fun onFilterClicked() {
    mutableUiState.update { state ->
        if (state.draftFilterOptions != null) {
            state
        } else {
            state.copy(
                draftFilterOptions =
                    state.confirmedFilterOptions.toSet(),
            )
        }
    }
}

fun onFilterOptionChanged(
    option: FeedFilterOption,
    selected: Boolean,
) {
    mutableUiState.update { state ->
        val draft = state.draftFilterOptions
            ?: return@update state

        state.copy(
            draftFilterOptions = if (selected) {
                draft + option
            } else {
                draft - option
            },
        )
    }
}

fun onFiltersApplied() {
    mutableUiState.update { state ->
        val draft = state.draftFilterOptions
            ?: return@update state

        state.copy(
            confirmedFilterOptions = draft.toSet(),
            draftFilterOptions = null,
        )
    }
}

fun onFilterDialogDismissed() {
    mutableUiState.update { state ->
        state.copy(draftFilterOptions = null)
    }
}
```

Instruções adicionais:

- Usar `kotlinx.coroutines.flow.update`.
- Não criar coroutine para essas operações síncronas.
- Não modificar listas de estabelecimentos dentro das ações.
- Não chamar UseCases nas ações do painel.
- Remover `openOnly`, `onOpenOnlyChanged` e exclusivamente seu predicado antigo em `visiblePlaces`.
- Preservar a seleção da aba de favoritos e a busca textual.
- Ao sair efetivamente da tela, descartar apenas o rascunho e preservar a seleção confirmada.
- Preservar a lógica já existente em `onScreenClosed`.
- Alterações de configuração que conservem a composição/ViewModel devem manter o estado; não adicionar descarte em efeitos ligados a recomposição.

**Critério de saída:** abrir, editar, confirmar e cancelar funcionam em testes sem chamadas remotas ou alteração da lista.

### Etapa 3 — Subagente de design system

**Skills:** `create-ui-component` e `design-system`, após leitura integral do handoff.

**Propriedade de arquivos:** novo componente, Defaults, recursos e eventuais tokens necessários.

Instruções:

1. Implementar a API definida na seção 2.
2. Usar `ModalBottomSheet`, com estado técnico configurado para ignorar expansão parcial.
3. Renderizar grupos em ordem.
4. Para chips e cartões, emitir o próximo valor de seleção:

```kotlin
val selected = option.id in selectedIds

FilterChip(
    selected = selected,
    onClick = {
        onSelectionChanged(option.id, !selected)
    },
    label = {
        Text(option.label)
    },
    enabled = enabled,
)
```

Esse trecho exemplifica somente a interação. Aplicar os estilos e dimensões definidos no handoff e nos Defaults.

5. Nos interruptores, tornar a linha uma única ação acessível. Evitar que o toque na linha e no `Switch` dispare o callback duas vezes.
6. Usar `FlowRow` para quebra de chips e preços, preservando ordem de leitura.
7. Garantir rolagem vertical com altura limitada pela janela.
8. Reutilizar `SextouButton` para confirmação.
9. Resolver ícones a partir de recursos existentes equivalentes; quando não houver correspondência, obter o asset do Figma e convertê-lo preservando sua geometria.
10. Manter strings dos previews nos recursos do módulo.

Acessibilidade:

- Título identificado como cabeçalho.
- Botão X anunciado como “Fechar filtros”.
- Opções anunciam rótulo e estado selecionado.
- Preços anunciam símbolo e descrição.
- Interruptores anunciam ligado/desligado.
- Nenhum significado comunicado somente por cor.
- Fonte ampliada sem truncar rótulos essenciais.
- Fechamento disponível mesmo com controles desabilitados.

**Critério de saída:** componente compilável, sem dependência de negócio e com todos os previews obrigatórios.

### Etapa 4 — Subagente de integração Compose

**Skills:** `architecture` e `design-system`.

**Propriedade de arquivos:** `FeedScreen`, `FeedDestination`, catálogo visual e strings do app.

Instruções:

1. Remover o `FeedFilterDialog` privado atual.
2. Criar `feedFilterGroups()`, função composable que transforma a tabela da seção 2 em `GroupData`.
3. Resolver todos os rótulos com `stringResource`.
4. Usar `FeedFilterOption.name` como identificador enviado ao design system.
5. Conectar callbacks na Destination.
6. Exibir o novo painel somente quando existir rascunho.

Exemplo de integração na Screen, considerando os novos callbacks em sua assinatura:

```kotlin
val draft = uiState.draftFilterOptions

if (draft != null) {
    SextouFilterSheet(
        title = stringResource(R.string.feed_filter_title),
        groups = feedFilterGroups(),
        selectedIds = draft.mapTo(mutableSetOf()) { it.name },
        applyLabel = stringResource(
            R.string.feed_filter_apply,
        ),
        closeContentDescription = stringResource(
            R.string.feed_filter_close_description,
        ),
        onSelectionChanged = { id, selected ->
            FeedFilterOption.entries
                .firstOrNull { it.name == id }
                ?.let { option ->
                    onFilterOptionChanged(option, selected)
                }
        },
        onApply = onFiltersApplied,
        onDismiss = onFilterDialogDismissed,
    )
}
```

Recursos novos devem incluir:

```xml
<string name="feed_filter_apply">Aplicar filtros</string>
<string name="feed_filter_close_description">Fechar filtros</string>
```

Adicionar também títulos de seção, 17 rótulos e descrições dos preços. Reutilizar recursos existentes quando o texto e o significado forem equivalentes.

Ao abrir o painel, remover o foco do campo e ocultar o teclado, preservando o texto digitado. O usuário precisa acionar o botão novamente para reabrir após o fechamento.

Não adicionar contador de resultados, badge, mensagem de sucesso, carregamento ou novo botão ausente na especificação.

**Critério de saída:** o fluxo parte da barra de busca, funciona sobre o feed real e preserva busca textual e conteúdo carregado.

### Etapa 5 — Subagente de testes e revisão

**Propriedade de arquivos:** testes adicionais acordados com o principal. Corrigir implementação por meio do subagente proprietário.

Executar a matriz da seção 4. Revisar especialmente:

- Confirmação alterando somente o estado visual.
- Descarte sem apagar seleção confirmada.
- Preservação do estado em emissões do feed.
- Ausência de consultas por interação.
- Semântica de seleção múltipla.
- Ausência de dependências de domínio no componente.

**Critério de saída:** relatório com comandos, resultados, evidências visuais e limitações reais.

### Etapa 6 — Encerramento pelo agente principal

1. Revisar o diff integrado.
2. Conferir a remoção dos usos antigos de `openOnly`.
3. Confirmar que ninguém alterou o carregamento diário ou o mapa.
4. Registrar decisões em `assets/agent-decision.md`.
5. Salvar este documento no destino indicado quando a sessão permitir escrita.
6. Entregar resumo com mudanças, testes e limitações.
7. Não criar commit, PR ou publicação sem solicitação.

## 4. Verificação e critérios de aceite

### Testes automatizados da ViewModel

Usar a infraestrutura JUnit e coroutines já existente. Inicializar fixtures em `@Before fun beforeEach()` e liberar recursos em `@After fun afterEach()` quando necessário.

| Cenário | Resultado esperado |
|---|---|
| Primeira abertura | Rascunho vazio |
| Abrir duas vezes | Rascunho existente preservado |
| Selecionar duas opções do mesmo grupo | Ambas selecionadas |
| Desmarcar uma opção | Somente ela removida |
| Editar | Seleção confirmada permanece igual |
| Aplicar | Rascunho confirmado e painel fechado |
| Aplicar novamente | Nenhuma alteração |
| Fechar sem aplicar | Rascunho descartado |
| Reabrir | Última confirmação restaurada |
| Aplicar vazio | Confirmação anterior limpa |
| Alterar opção com painel fechado | Nenhuma alteração |
| Nova emissão de estabelecimentos | Seleções preservadas |
| Interagir com todos os controles | Nenhuma nova chamada remota |
| Aplicar qualquer combinação | IDs dos resultados permanecem iguais |
| Digitar na busca | Filtragem textual existente preservada |

Exemplo a acrescentar à suíte existente, reutilizando suas fixtures:

```kotlin
@Test
fun closingDiscardsDraftAndPreservesConfirmedSelection() {
    val viewModel = feedViewModel(
        searchRepository = FakeSearchPlacesRepository {
            error("Filter interaction must not search")
        },
    )

    viewModel.onFilterClicked()
    viewModel.onFilterOptionChanged(
        FeedFilterOption.TYPE_BOTECO,
        true,
    )
    viewModel.onFiltersApplied()

    viewModel.onFilterClicked()
    viewModel.onFilterOptionChanged(
        FeedFilterOption.PRICE_LOW,
        true,
    )
    viewModel.onFilterDialogDismissed()
    viewModel.onFilterClicked()

    assertEquals(
        setOf(FeedFilterOption.TYPE_BOTECO),
        viewModel.uiState.value.draftFilterOptions,
    )
}
```

### Validação visual e acessibilidade

Inspecionar previews e executar o fluxo em emulador ou dispositivo:

- Referência de 390 × 844 dp, janela estreita, paisagem e fonte em 200%.
- Todas as opções e a confirmação alcançáveis.
- Painel acima do teclado e respeitando barras do sistema.
- Fechamento por X, Voltar, toque externo e gesto.
- Seleção múltipla e interruptores com TalkBack.
- Foco retornando ao acionador após fechamento.
- Sem sobreposição entre alvos de toque.
- Comparação com o nó do Figma, documentando adaptações aprovadas.

Previews não substituem testes de interação. Se não houver ambiente visual disponível, registrar essa validação como pendente.

### Comandos

Executar na raiz, com JDK 17 compatível com o projeto:

```bash
./gradlew :app:testDebugUnitTest :design-system:testDebugUnitTest
./gradlew :domain:test :networking:testDebugUnitTest
./gradlew :design-system:assembleDebug :app:assembleDebug
./gradlew :design-system:lintDebug :app:lintDebug
```

A segunda linha inclui as verificações arquiteturais atualmente localizadas nesses módulos.

Registrar `NO-SOURCE` quando o design system não possuir testes. Não apresentá-lo como suíte executada. Falhas de infraestrutura ou análise parcial do Lint devem constar no relatório.

## 5. Instruções de orquestração

O principal deve enviar a cada subagente uma tarefa neste formato:

```text
Execute somente a etapa atribuída deste plano.

Leia:
- As regras e skills aplicáveis.
- O contrato aprovado.
- Os arquivos atuais sob sua responsabilidade.
- O handoff, quando houver implementação visual.

Respeite:
- Escopo de interface, sem filtragem dos estabelecimentos.
- Arquivos de propriedade dos outros subagentes.
- Alterações preexistentes.
- Tokens, recursos e convenções do Sextou.

Entregue:
1. Arquivos alterados.
2. Comportamento implementado.
3. Verificações executadas e resultados.
4. Desvios ou bloqueios.
5. Decisões para registro pelo agente principal.

Não amplie contratos por iniciativa própria.
Encaminhe conflitos ao agente principal.
Não faça commit.
```

O principal revisa cada entrega antes de liberar suas dependentes. Se um contrato precisar mudar, atualiza este plano e comunica todos os consumidores antes de permitir novas alterações.

**Conclusão da entrega:** painel visual integrado, seleção e confirmação verificadas, componente reutilizável documentado e limite de “somente interface” explícito no relatório final.
