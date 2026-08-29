# Decisões do agente

## 2026-08-28 — DESIGN.md do sistema visual do Sextou

Foi criado `DESIGN.md` na raiz seguindo a especificação atual do Stitch: front
matter YAML com tokens e as seções canônicas `Overview`, `Colors`,
`Typography`, `Layout`, `Elevation & Depth`, `Shapes`, `Components` e
`Do's and Don'ts`. A caixa alta foi mantida porque é o nome definido pela
documentação do Stitch e referenciado pelo `AGENTS.md` do projeto, embora o
pedido tenha usado `design.md` em caixa baixa.

O conteúdo foi extraído dos nós `19:785` (tema/papéis semânticos) e `19:1195`
(escala primitiva) do arquivo Figma `pASFSURvaP2uIMXDZOFNk5`. A camada
semântica ficou normativa para telas e componentes; as escalas Orange, Slate,
Emerald, Rose, transparência, espaçamento, raios e tipografia foram preservadas
como referência primitiva. O linter do formato reporta avisos de tokens
primitivos não referenciados por componentes; isso é intencional, pois
primitivos são a base de composição e não devem ser aplicados diretamente ao
produto.

O handoff semântico e a cobertura MD3 usam o mesmo rótulo para valores
diferentes em alguns casos. Para não perder informação, `secondary-container`
MD3 foi mantido como `#6F2F19` e o swatch de produto `#2A2A2A` foi nomeado
`product-secondary-container`; `product-surface-variant` preserva `#262626`
separado de `surface-container-high` (`#292929`). A tipografia semântica usa
Inter, enquanto a prancha primitiva demonstra Plus Jakarta Sans; o documento
prioriza Inter para a UI do produto e registra Plus Jakarta Sans somente como
espécime atômico da fonte.

As cores adicionais observadas no showcase semântico — dourado da marca,
hover, sucesso forte e estados fechado — também foram preservadas como
`product-*`, mesmo quando duplicam uma função MD3, para manter a fidelidade ao
handoff sem criar uma segunda semântica concorrente.

## 2026-08-28 — Ícones de estabelecimentos do Figma (frame 71:4)

Os 22 subframes de ícones do frame `71:4` (`Establishment Iconography Board`)
foram exportados individualmente como SVG pelo Figma MCP e convertidos para
`VectorDrawable` Android em `design-system/src/main/res/drawable`, mantendo
48 × 48 px, `viewBox` 48 × 48, paths, cor `#F2EDE4` e opacidades observadas.
Os nomes usam o prefixo `ic_sextou_establishment_` e a normalização ASCII dos
rótulos para permitir o uso por `painterResource`. A alteração é restrita a
recursos compartilhados: não foi criada API Kotlin, componente ou handoff
novo, porque o pedido não solicitou comportamento de UI.

## 2026-08-28 — Handoff do Menu Item a partir dos nós 153:17 e 153:18

Foi criado `.handoff/handoff-menu-item.md` a partir do Figma e da
especificação textual do usuário. O componente foi normalizado como `Menu
Item`, com `title`, `supportingText` opcional, `highlightText` opcional e tile
configurável por imagem ou por duas letras. A imagem versus abreviação foi
documentada como variação de layout; a ausência dos textos não foi modelada
como estado. O componente permanece informativo e não clicável, sem callback,
foco, ripple ou estados de interação.

O Figma define a referência de 342 × 84 px, tile de 56 × 56 px, padding de
12 px, gaps de 12/2 px, raio de 12 px, borda de 1 px e os tokens de cor
observados no arquivo. Como o Figma mostra somente o acento
`#7F2D12`, mas o pedido exige uma cor aleatória, o handoff registra a paleta,
seed e ciclo de vida da escolha como lacunas do produto e recomenda manter a
cor estável por identidade do item. Para os campos opcionais, foi adotada a
decisão documental de preservar a altura externa e o tile, remover campos
ausentes sem placeholders e permitir que a área central aproveite o espaço
quando o texto de ênfase não existir; a validação visual dessa combinação
continua pendente antes da implementação.

## 2026-08-24 — Atualização do Button a partir do Figma (node 56:294)

O handoff completo do Button foi extraído do Figma MCP e salvo em
`.handoff/handoff-button.md`. O componente foi ampliado para suportar as
variantes `Primary`, `Secondary`, `Outline` e `Ghost`, os tamanhos `Large`,
`Medium` e `Small`, ícones opcionais à esquerda/direita e os estados
`Default`, `Hover / Pressed` e `Disabled`. A largura continua flexível; as
alturas 48/40/32 px, paddings horizontais 24/20/16 px, raios 16/12/8 px e
ícone de 20 px foram centralizados em tokens do tema conforme a matriz do
Figma.

Como o Material Button existente não oferece diretamente a troca de cor e a
escala 1,05 do estado `Hover / Pressed` mantendo os três tamanhos da matriz,
o componente usa uma superfície Compose clicável com semântica de Button,
ripple, foco e estado desabilitado. A escala é animada em 200 ms com easing
`ease-in-out`, aplicada apenas ao conteúdo visual para não provocar mudança
de layout. A API anterior baseada em slot e `SextouOutlinedButton` foi
preservada como sobrecarga/ponte, enquanto a nova API baseada em `label`
expõe o contrato completo do handoff.

Foram adicionados ao tema somente tokens ausentes na fonte existente: cor
secundária de hover, bordas de Outline, superfícies/opacidades de interação,
métricas do Button, raio Large e tipografia Small. Não foram criados testes
unitários porque a alteração é exclusivamente visual; a compilação do módulo
e os previews cobrem a verificação aplicável.

## 2026-08-24 — Pacotes dedicados para componentes do design-system

Cada componente visual foi isolado em um pacote próprio sob
`com.sextou.designsystem.component`, mantendo arquivos de implementação,
defaults e previews juntos. `SextouInput` e `SextouInputDefaults` permanecem no
pacote `input`; `SextouSearchBar` fica no pacote `searchbar` por possuir API
pública própria e depende explicitamente do pacote `input`. A mudança é
organizacional e não altera assinaturas nem comportamento dos composables.

## 2026-08-24 — Handoff e atualização do Input / Search Bar

O node `56:502` do Figma foi usado como fonte do handoff completo salvo em
`.handoff/handoff-input-search-bar.md`. O contrato foi modelado como dois
layouts (`Text Input` e `Search Bar`) com os estados `Default`, `Focused /
Active`, `Error / Invalid` e `Disabled`, mantendo `value`, callbacks e
validação sob responsabilidade do consumidor.

O tema já possuía os tokens semânticos para superfície, texto, foco, erro,
espaçamentos e opacidade. Como a fonte exige raios específicos de 14 px para
Text Input, 16 px para Search Bar e 10 px para o botão de ação, foram criados
tokens centrais em `SextouCornerRadius`; as alturas de 48/56 px, borda de foco
de 2 px e alvos/ícones também foram centralizadas em `SextouDimensions`. A
diferença entre a matriz (54 px em alguns exemplos de Search Bar) e a seção de
dimensões (56 px) foi resolvida adotando 56 px como altura externa fixa, sem
alterar o layout ao aplicar a borda de foco.

## 2026-08-24 — Substituição do mapa da localização no Figma

O preenchimento de imagem do node `17:727`, dentro do container `17:726`, foi
substituído por um tile real do Google Maps centrado em `Rua Visconde de Pirajá,
550 - Ipanema, RJ`. O pin existente (`17:728`) foi preservado. Para aproximar
o mapa da linguagem visual do Sextou, foram adicionadas as camadas `61:2` e
`61:3` com scrim `#111111` e acento `#FE9A00`, tokens equivalentes a
`SextouColors.Background` e `SextouColors.Primary`. O texto de endereço
(`17:733`) foi atualizado para permanecer coerente com a localização mostrada.
As três camadas do mapa foram configuradas como posicionamento absoluto para
não sofrerem redistribuição pelo auto-layout horizontal do background.

## 2026-08-23 — Organização do fluxograma completo

O frame `42:2` foi reorganizado em três níveis de leitura: `Feed` e `Detalhe
do estabelecimento` formam o fluxo principal no topo; abaixo, os acessos
foram separados em dois grupos visuais, `Acessos a partir do Feed` e `Ações a
partir do Detalhe`. Os conectores secundários desalinhados foram removidos,
mantendo somente o conector principal e trilhos verticais que ligam cada tela
de origem ao seu grupo. Os cartões e suas descrições foram preservados.

## 2026-08-23 — Novo frame com fluxo de navegação completo

Foi criado o frame `42:2` (`Complete Navigation Flow`) na página `Navigation`,
ao lado do diagrama anterior. O novo frame usa cartões abstratos, sem
miniaturas das interfaces, para atender ao pedido de registrar apenas o nome
da tela e uma descrição curta de sua responsabilidade.

O fluxo inclui `Feed`, `Detalhe do estabelecimento`, `Busca e filtros`, `Mapa`,
`Locais`, `Perfil`, `Cardápio completo`, `Favoritos e visitas` e `Como chegar e
contato`. `Contato` foi mantido junto de `Como chegar` porque ambas as ações
partem do detalhe do estabelecimento e representam o mesmo caminho
complementar no mapa.

## 2026-08-23 — Diagrama de navegação do Sextou no Figma

Foi preenchido o frame `32:1605` da página `Navigation` com um diagrama de
navegação. As telas `1:632` (Feed) e `17:577` (Detalhe do estabelecimento)
foram clonadas como o fluxo principal, preservando a referência visual já
criada no arquivo.

As telas complementares foram inferidas somente a partir das interações
visíveis nas duas referências: `Mapa` e `Locais` da navegação inferior,
`Busca e filtros` do cabeçalho do feed, `Cardápio completo` da ação “ver
completo”, `Favoritos e visitas` das ações de salvar/visitar e `Como chegar e
contato` das ações de localização e contato do detalhe. O fluxo principal foi
destacado em laranja; as telas derivadas usam cartões claros com acento laranja
e conectores horizontais alinhados aos destinos para manter a leitura do mapa.

## 2026-08-17 — Correção do frontmatter da create-ui-component

O frontmatter de `.agents/skills/create-ui-component/SKILL.md` foi ajustado
para atender ao carregador de skills: os valores de `metadata` passaram de
listas YAML para strings e o campo `name` foi explicitamente delimitado como
string. A validação oficial da skill passou após a correção.

## 2026-08-17 — Auditoria dos componentes do design-system

Foi realizada uma validação somente de leitura dos nove componentes públicos
do módulo `design-system` usando as skills `create-ui-component` e
`design-system`. O módulo compila e o lint termina com sucesso, mas os
componentes ainda não atendem integralmente ao contrato da skill: faltam
`*Defaults`/`Style` por componente e KDoc nas APIs públicas, e há ajustes de
interação e acessibilidade pendentes nos controles customizados. A auditoria
não alterou os componentes.

## 2026-08-17 — Módulo obrigatório para componentes visuais

A skill `.agents/skills/create-ui-component` passou a determinar que todo
componente criado por ela deve ser implementado no módulo `design-system`.
Módulos como `app`, `domain`, `networking` e módulos de feature devem apenas
consumir a API pública do componente.

## 2026-08-17 — Skill de handoff de componentes visuais

Foi preenchida a skill `.agents/skills/handoff-ui-component` com um fluxo
agnóstico de fonte para extrair dados de Figma MCP, XML, screenshots,
documentação, código ou outras entradas. O contrato de saída foi fixado em
`.handoff/handoff-<nome-do-componente>.md` na raiz do projeto, usando o nome do
componente normalizado em `kebab-case`; o template também passou a registrar o
tipo e a referência da fonte, em vez de assumir somente uma URL do Figma.

A skill `design-system` passou a documentar a composição das skills
`handoff-ui-component` e `create-ui-component`: o handoff é extraído primeiro,
o componente é planejado e implementado a partir dele, e as regras específicas
de tokens, tema e organização do módulo Sextou continuam sob responsabilidade
da skill `design-system`.

## 2026-08-17 — Referência da skill de componentes visuais

O `AGENTS.md` passou a referenciar a skill local
`.agents/skills/create-ui-component/SKILL.md` como
`create-ui-component`, responsável por planejar e implementar
componentes visuais em Jetpack Compose. As referências internas da skill foram
ajustadas para os caminhos reais em `references/`, mantendo a skill
`design-system` separada para regras específicas do módulo compartilhado.

O arquivo de desenvolvimento da skill foi renomeado para
`.agents/skills/create-ui-component/references/developer.md`, e o frontmatter
e os links relativos foram atualizados para usar a extensão `.md`.

A skill `create-ui-component` passou a exigir a leitura integral do arquivo
`.handoff/handoff-<nome-do-componente>.md` na raiz do projeto antes do
planejamento ou da implementação, eliminando a referência ao diretório antigo
`./handoff`.

## 2026-08-17 — Auditoria das skills e compatibilidade do board

A auditoria confirmou que o GitHub Project `Sextou` nº 4 continua compatível
com a skill `manage-sextou-tasks`: repositório, proprietário, campo `Status`,
seis opções de status e os 13 cards atuais foram conferidos remotamente. As
inconsistências encontradas ficaram registradas no retorno da auditoria: a
skill visual ainda usa exemplos genéricos e convenções de pacote diferentes
do módulo real do Sextou, e o contrato de título em
`.codex/feature-description/README.md` não menciona o prefixo obrigatório de
tipo usado pela skill de tasks.

## 2026-08-16 — Botão de mais resultados do Figma (node 2:1960)

Foi criado `SextouMoreButton` com texto variável, callback `onClick` e estado
`enabled`, usando `OutlinedButton` do Material 3 para preservar semântica,
foco, ripple e estado desabilitado. O botão mantém borda, raio, espaçamentos,
tipografia e ícone de chope do Figma; o SVG exportado foi convertido para
VectorDrawable e o texto permanece sob responsabilidade do consumidor.

## 2026-08-16 — Badge de status do Figma (nodes 2:1834 e 2:1902)

Foi criado `SextouStatusBadge` com os estados `SextouStatus.OPEN` e
`SextouStatus.CLOSED`. O componente é deliberadamente não clicável e não
recebe callbacks. Os dois estados compartilham a estrutura de pill com ponto
indicador e variam apenas nas cores e no texto localizado; dimensões, cores e
tipografia foram centralizadas nos tokens do `design-system`.

## 2026-08-16 — Cabeçalho de seção do Figma (node 1:694)

Foi criado `SextouSectionHeader` com texto variável para representar o divisor
de seção com chamas laterais. O `padding-top` de 16 dp e o `padding-horizontal`
de 20 dp presentes no node foram deliberadamente removidos; o container não
aplica espaçamento vertical ou lateral, preservando a solicitação de ausência
de margens superior, inferior, esquerda e direita. As linhas ocupam o espaço
restante com o token de divisor existente, e o ícone de chama foi exportado do
Figma e convertido para VectorDrawable.

## 2026-08-16 — Barra de busca do Figma (node 1:669)

Foi criado `SextouSearchBar` no `design-system` como componente stateless, com
valor editável, placeholder e ação de filtro separados. A superfície, borda,
tipografia e cores reutilizam os tokens existentes; foram adicionados apenas
tokens dimensionais para o ícone, altura do campo e área de toque do filtro.
O ícone de busca foi exportado do Figma e convertido para VectorDrawable. O
ícone de filtro já existente no módulo foi reutilizado. A área de toque do
filtro mede 48 dp e mantém o visual laranja de 28 dp alinhado à direita para
preservar acessibilidade sem alterar a composição visual do node.

## 2026-08-16 — Botão de ícone clicável do Figma (node 1:675)

Foi criado `SextouIconButton` no `design-system` para representar o botão
compacto de 28 dp do Figma. A API recebe um `Painter`, permitindo trocar o
ícone sem acoplar o componente a uma feature, além de descrição de
acessibilidade, callback `onClick` e `enabled`. O bloco visual mantém 28 dp,
envolvido pelo alvo mínimo de toque do Compose. A interação usa `Surface` do
Material 3 para preservar a semântica de botão e a indicação/ripple padrão ao
pressionar. O ícone do node `1:676` foi exportado do Figma e convertido para
VectorDrawable apenas no preview; consumidores continuam fornecendo o ícone
por parâmetro.

## 2026-08-16 — Componente de marca do Figma (node 1:652)

Foi criado `SextouBrand` em `design-system` a partir do node `1:652` do
Figma. O componente é stateless e não clicável: recebe `Painter` para o ícone,
`String` para título e subtítulo, `Modifier` opcional e uma descrição de
acessibilidade opcional. O layout reutiliza os tokens existentes de cor,
espaçamento e forma, com novos tokens apenas para dimensões do ícone e para o
estilo tipográfico do subtítulo que não existiam no tema.

O asset visual do ícone foi exportado do Figma e salvo como
`design-system/src/main/res/drawable/ic_sextou_chopp.png` somente para o
preview; a API do componente permanece dinâmica para os consumidores.

## 2026-08-16 — Aplicação das regras de tokens, estado e previews

O módulo `design-system` foi ajustado para remover dimensões hardcoded dos
componentes. Os valores de botão de perfil, badge, bordas, offset e elevação
foram centralizados em `theme/Dimensions.kt`; as cores específicas do esquema
claro foram centralizadas em `theme/Color.kt`, e o background dos previews usa
o token ARGB compartilhado. A revisão confirmou que os componentes atuais já
são stateless e que cada componente possui preview no próprio arquivo.

`design-system:assembleDebug` passou. `design-system:testDebugUnitTest` não
possui fontes de teste (`NO-SOURCE`). A compilação de `app` foi tentada, mas o
ambiente falhou no `androidJdkImage` ao executar `jlink` do JDK 22.3 contra o
SDK Android 34; não houve erro de Kotlin ou de integração do design-system.

## 2026-08-16 — Regras de tokens, estado e previews do design-system

A skill `.agents/skills/design-system` passou a exigir que componentes nunca
usem valores visuais hardcoded: cores, espaçamentos, dimensões, corner radius e
tipografia devem reutilizar tokens existentes ou receber um novo token
semântico centralizado. Também foi definido que componentes são stateless por
padrão, com exceções mínimas para estados transitórios de inputs, e que todo
componente deve ter um preview privado no próprio arquivo.

## 2026-08-16 — Metadata da skill de arquitetura

Foi adicionado `.agents/skills/architecture/agents/openai.yaml` com nome,
descrição curta e prompt padrão coerentes com a skill `architecture`. A skill
não declara dependências externas e permanece elegível para invocação implícita.

## 2026-08-16 — Skill local para o módulo design-system

Foi criada a skill `.agents/skills/design-system` para concentrar o fluxo e as
regras específicas do módulo visual compartilhado: limites arquiteturais,
tokens semânticos, tema Material 3, componentes Compose, acessibilidade,
previews, dependências e validação. A skill foi mantida sem scripts ou
referências auxiliares porque o conhecimento necessário é pequeno e estável,
enquanto os arquivos do módulo permanecem a fonte de verdade para a
implementação.

## 2026-08-16 — Foto opcional no botão de perfil

* `SextouProfileButton` passou a aceitar `avatarPainter` opcional. Quando
  informado, ele substitui o ícone do Figma por uma foto circular de 18 dp;
  quando ausente, o ícone original permanece como fallback.
* A API usa `Painter` em vez de receber URL ou introduzir Coil no
  `design-system`, mantendo o carregamento de imagens sob responsabilidade do
  módulo consumidor.

## 2026-08-16 — Botão de perfil do design-system

* O node `1:663` do Figma foi implementado como `SextouProfileButton`, com
  tamanho visual de 40 dp, ícone de perfil de 18 dp e indicador opcional de
  14 dp no canto superior direito.
* Os valores visuais foram ligados aos tokens existentes do design-system:
  `SurfaceImage`, `Border`, `TextSecondary`, `Error` e `Background`.
* O SVG exportado do Figma foi convertido para um VectorDrawable Android para
  preservar o traçado original sem introduzir uma biblioteca de ícones.

## 2026-08-16 — Preview co-localizado ao tema

* O preview composto do tema foi movido de `ThemePreview.kt` para `Theme.kt`,
  mantendo a visualização da composição no mesmo arquivo da API `SextouTheme`.

## 2026-08-16 — Runtime do renderer de previews

* O `design-system` passou a declarar `ui-tooling` em `debugImplementation`,
  além de `ui-tooling-preview`, porque a anotação sozinha permite compilar os
  previews, mas não fornece o runtime usado pelo renderer do Android Studio.

## 2026-08-16 — Previews do design-system

* Foram adicionados previews privados para `SextouButton`,
  `SextouOutlinedButton`, `SextouCard` (estático e clicável) e uma composição
  do `SextouTheme`, todos isolados de ViewModel, Koin e navegação.
* Os textos usados exclusivamente nos previews foram colocados em
  `design-system/src/main/res/values/strings.xml`, mantendo a regra de não
  hardcodar strings visíveis em composables.

## 2026-08-16 — Tokens visuais da Home no Figma

* O nó `1:632` da Home foi consultado pelo Figma MCP e os tokens foram
  adaptados ao `design-system`: tema escuro com `#111111` como base, superfícies
  `#1C1C1C` e `#2A2A2A`, texto `#F2EDE4`, texto secundário `#9A9080`, laranja
  `#FE9A00`, amarelo `#FFB900`, verde `#00D492` e vermelho `#FF5722`.
* A escala de layout foi centralizada em espaçamentos de 4, 6, 8, 12, 16, 20,
  24 e 32 dp; os principais raios foram definidos como 10, 14, 16 e 44 dp.
* A tipografia preserva os tamanhos, pesos, alturas de linha e espaçamentos
  observados no Figma. Como o projeto não possui fontes embarcadas e SF Pro é
  proprietária, `FontFamily.SansSerif` foi adotada como fallback Android para os
  estilos equivalentes a SF Pro, Anton, Nunito e Barlow Condensed.

## 2026-08-24 — Camadas de tokens primitivos e semânticos do Figma

Os nós `19:1195` (Primitive Tokens - Granular Scale) e `19:785` (MD3
Semantic Architecture) foram consultados antes da implementação, usando as
skills `design-system` e `mobile-android-design`. Os valores crus foram
centralizados em `theme/Primitive.kt`: paletas Orange/Slate/Emerald/Rose,
alpha, espaçamento de 2 a 128 dp, raios de 0 a full, pesos/tamanhos
tipográficos e seis níveis de elevação. Os papéis de produto passaram a
referenciar esses primitivos em `Color.kt`, `Spacing.kt`, `Shape.kt`,
`Type.kt` e `Elevation.kt`.

`SextouColors` foi preservado como API semântica pública para não quebrar os
componentes existentes. O fallback claro foi remapeado para as mesmas paletas
primitivas, e `SextouTypography` passou a expor os papéis do Figma (`Display
Large`, `Headline Medium`, `Title Medium`, `Body Large` e `Label Small`) no
tema Material 3. A família `FontFamily.SansSerif` foi mantida como fallback
porque o módulo não embarca as fontes do arquivo de design.

## 2026-08-16 — Módulo compartilhado de design

* Foi criado o módulo Android Library `design-system` para concentrar os
  recursos visuais agnósticos de negócio: tema Compose Material 3, esquemas de
  cores claro/escuro, tipografia, formas e componentes base de botão e card.
* O módulo usa `dynamicColor` somente quando solicitado explicitamente; o
  padrão permanece no esquema visual do Sextou para manter consistência entre
  dispositivos.
* Apenas o `app` foi conectado ao módulo porque é o único módulo atual com
  interface. `domain` e `networking` continuam sem dependência de Compose ou
  Android, preservando a direção das dependências da arquitetura.

## 2026-08-16 — Épicos das funcionalidades do features.md

* Foram criadas 13 Issues correspondentes às funcionalidades de
  `.codex/features.md`, numeradas de #2 a #14, usando o prefixo `Épico -` no
  título porque o repositório não possui tipos nativos de Issue habilitados.
* Cada Issue recebeu somente o briefing da funcionalidade correspondente no
  próprio `.codex/features.md`, sem adicionar conteúdo de outras fontes.
* Todas as Issues foram vinculadas ao Project nº 4, definidas explicitamente
  como `Status: Backlog` e verificadas por leituras independentes.

## 2026-08-15 — Entrada padrão das Issues no board

* A skill `manage-sextou-tasks` passou a definir `Backlog` como ponto de
  entrada obrigatório para toda Issue adicionada ao Project sem coluna
  explicitamente informada.
* O status deve ser definido explicitamente após a vinculação e confirmado por
  leitura independente, sem depender do padrão automático do GitHub Project.

## 2026-08-15 — Semântica Jira para tipos de task

* A skill `manage-sextou-tasks` passou a orientar a escolha de `Épico`,
  `História`, `Tarefa` e `Bug` conforme a semântica padrão do Jira.
* `Épico` representa trabalho amplo composto por itens menores; `História`
  representa objetivo/valor do usuário; `Tarefa` representa ação técnica ou
  administrativa; e `Bug` representa defeito em comportamento existente.
* A hierarquia e o significado são do Jira, mas a implementação do Sextou
  continua usando o prefixo no título, pois o GitHub do projeto não habilita
  tipos nativos de Issue.

## 2026-08-15 — Substituição do tipo Sub-tarefa por Bug

* O conjunto vigente de tipos da skill `manage-sextou-tasks` foi alterado para
  `Épico`, `História`, `Tarefa` e `Bug`.
* `Bug` passa a usar o mesmo formato de prefixo: `Bug - <Título>`.

## 2026-08-15 — Tipos de task no prefixo do título

* A skill `manage-sextou-tasks` passou a exigir os tipos `Épico`, `História`,
  `Tarefa` e `Sub-tarefa` no prefixo do título, usando o formato
  `<Tipo> - <Título>`.
* O tipo será representado no título em vez de depender de tipos nativos do
  GitHub, que não estão habilitados no repositório pessoal do projeto.
* A normalização evita prefixos duplicados, preserva o tipo em edições e exige
  a definição do tipo antes da criação de uma task.

## 2026-08-15 — Bloqueio na criação de épicos das features

* A criação das 13 Issues solicitadas foi interrompida antes de qualquer
  mutação porque o repositório pessoal `tiagocasemiro/Sextou` não oferece tipos
  nativos de Issue: `list_issue_types` retornou `404` e não há campo `Type`
  configurado no repositório.
* O parâmetro `type: "Epic"` só deve ser enviado quando os tipos de Issue
  estiverem habilitados; criar Issues comuns com um título ou label de épico
  seria uma representação diferente do pedido e depende de confirmação.

## 2026-08-15 — Instruções de uso das skills locais

* O `AGENTS.md` passou a documentar a seleção, leitura, composição e uso das
  skills em `.agents/skills/`, incluindo as skills locais `architecture` e
  `manage-sextou-tasks`.
* A orientação exige leitura integral do `SKILL.md`, uso de referências e
  scripts conforme a necessidade, preservação do escopo solicitado e registro
  de decisões materiais em `assets/agent-decision.md`.

## 2026-08-15 — Atualização do fluxo do board na skill de tasks

* A skill `.agents/skills/manage-sextou-tasks` e sua referência operacional foram atualizadas para refletir as seis colunas atuais do Project: `Backlog`, `Read to work`, `In Progress`, `Validation`, `Wait publish` e `Done`.
* As descrições configuradas em cada coluna foram preservadas como fonte do significado operacional e convertidas em critérios de transição do fluxo.
* `Done` continua tratado separadamente porque a automação confirmada do Project fecha a Issue como `completed`; as demais colunas não devem ser presumidas como reabertura automática.

## 2026-08-15 — Adiamento de Bombando Agora

* A funcionalidade “Bombando Agora” foi removida de `.codex/features.md` e movida integralmente para `.codex/future-features.md`.
* O adiamento foi decidido porque a lista oficial de campos da Places API (New) e do Places SDK for Android não oferece lotação ao vivo, quantidade de pessoas, movimento atual ou horários de pico.
* A especificação futura foi preservada porque a ideia continua viável com uma fonte própria do Sextou baseada em check-ins e sinais consentidos, mas essa infraestrutura está fora do escopo atual.
* Scraping da interface do Google Maps permanece descartado por não ser uma API oficial e por introduzir riscos de conformidade e confiabilidade.

## 2026-08-15 — Visualização do cardápio

* A funcionalidade principal foi especificada como um resumo dos atributos de alimentação fornecidos pela Places API, pois essa API não expõe itens e preços de cardápios completos de estabelecimentos arbitrários.
* O acesso ao cardápio completo foi definido como navegação para o site oficial ou para a página do local no Google Maps.
* A Google Business Profile API foi documentada apenas como limitação, porque a leitura de `FoodMenus` exige autorização OAuth da conta proprietária do estabelecimento e não atende ao fluxo geral do Sextou.
* Foram incluídos estados de ausência de dados e uso de máscara de campos para evitar que a especificação pressuponha disponibilidade universal e para reduzir custo e latência.

## 2026-08-15 — Nota do estabelecimento e faixa de preço

* A nota geral foi definida com os campos `RATING` e `USER_RATING_COUNT`, pois ambos são disponibilizados pelo Places SDK for Android.
* O pedido de “nota para o preço” foi documentado como faixa de preço usando `PRICE_LEVEL` e `PRICE_RANGE`, porque a Places API não oferece uma nota de custo-benefício.
* A faixa de preço não foi dividida entre comida e bebida, pois os campos do Google descrevem o estabelecimento como um todo.
* Foram especificados estados de ausência de dados e cache temporário para evitar valores inventados ou apresentados como atuais quando a API não os retornar.

## 2026-08-15 — Bombando Agora

* A feature foi adicionada em `.codex/features.md`, conforme solicitado, sem alterar `.codex/funcionalidades.md`.
* A Places API foi mantida apenas como fonte de identificação, localização e funcionamento do estabelecimento, porque seus campos públicos para Android não expõem movimento ou lotação atual.
* Scraping da interface do Google Maps foi descartado por não constituir uma integração oficial ou um contrato de API confiável.
* O movimento foi definido a partir de check-ins e sinais consentidos dos próprios usuários do Sextou, agregados por um serviço remoto. Essa dependência é necessária porque o banco local não agrega eventos de diferentes usuários.
* Foram adotadas faixas qualitativas, mínimo de cinco participantes, identificadores rotativos e ausência de rastreamento em segundo plano para reduzir riscos de privacidade.
* O padrão inicial para “Bombando agora” foi definido como dez sinais únicos em 30 minutos e movimento 50% acima da média do local para o mesmo período. Os limites foram tornados configuráveis por dependerem de validação futura com dados reais.
* A proposta segue a skill local `architecture`: Compose e ViewModel na apresentação, UseCase e contratos no domínio, Retrofit no módulo `networking` e cache Room no módulo `local`.

## 2026-08-15 — Briefings de tasks e subagentes das features

* Foram criados 13 prompts independentes em `.codex/subagents/`, um para cada
  feature atualmente descrita em `.codex/features.md`, com contrato de saída
  para Issue, critérios de aceite, dependências, fora de escopo e referências.
* A análise reutiliza os modelos já existentes no módulo `domain`, como
  `PlaceSummary`, `PlaceDetails`, `GeoPoint`, `PlaceAmenities`, `PlaceOpeningHours`,
  `PriceRange` e `PlacePhotoReference`, e propõe entidades Room somente para o
  estado próprio do usuário ou cache mínimo aprovado.
* A integração foi alinhada à Places SDK for Android (New), Maps SDK for
  Android e Routes API. A especificação de tags foi corrigida: o contrato
  Android usa `Place.Field.LIVE_MUSIC`/`GOOD_FOR_CHILDREN`; karaokê requer fonte
  própria ou curadoria, pois não há campo estruturado garantido.
* A rota foi dividida entre Intent/Google Maps URLs no MVP e Routes API para
  trajeto desenhado no app. Directions API e Distance Matrix API legadas não
  foram recomendadas.
* O cache de conteúdo Google foi descrito como temporário e condicionado aos
  termos vigentes, sem tratar a janela de 7–15 dias da especificação como uma
  autorização universal de armazenamento.
* Não foi adicionada nova tecnologia ao projeto; portanto, não houve débito de
  skill para registrar em `skill-debts.md`.

## 2026-08-23 — Validação dos tipos de estabelecimento da Places API

* A validação de `.codex/sextou/tipos-de-estabelecimento.md` foi feita contra
  a lista oficial atual de Place Types (New), sem alterar o arquivo solicitado.
* A recomendação separa tipos genéricos (`bar`, `restaurant`) de tipos
  específicos (`bar_and_grill`, `barbecue_restaurant`, `brewery` e
  `brewpub`), e trata `warehouse_store`/`wholesaler` como alternativas amplas,
  não como equivalentes de `liquor_store`.
* As sugestões de novas categorias usam somente identificadores presentes na
  Places API (New); não foram inferidos tipos proprietários ou categorias fora
  da API.

## 2026-08-23 — Inclusão das categorias da Places API

* `.codex/sextou/tipos-de-estabelecimento.md` foi atualizado com as correções
  de mapeamento e todas as categorias sugeridas na validação.
* Categorias ambíguas foram separadas em rótulos específicos, como adega,
  depósito de bebidas, cervejaria e brewpub, para evitar tratar tipos amplos
  como equivalentes exatos.

## 2026-08-23 — Identificação de categorias mescláveis

* O subtítulo `## Categorias adicionais` foi removido para manter todas as
  categorias no mesmo nível documental.
* Foram identificados agrupamentos de produto para bares, restaurantes,
  bebidas, cafés e sobremesas, entretenimento noturno e cervejas.
* As mesclas foram tratadas como agrupamentos de interface, preservando os
  identificadores específicos da Places API (New) para filtragem e mantendo
  separados os casos em que produtor e local de consumo têm significados
  diferentes.

## 2026-08-24 — Iconografia dos tipos de estabelecimento no Figma

* O frame `69:3878` foi preenchido via MCP do Figma com 22 tiles, um para cada
  rótulo humano listado em `.codex/assets/sextou/tipos-de-estabelecimento.md`.
* Os códigos alternativos da Places API não foram transformados em tiles
  adicionais; eles continuam sendo mapeamentos de dados para o mesmo rótulo
  de produto.
* A composição usa auto-layout em cinco linhas, reutiliza os ícones Flat Icon
  existentes de cerveja, espeto, microfone e vinho, e completa a coleção com
  ícones vetoriais semânticos no mesmo tratamento visual.
* As cores recorrentes do arquivo foram usadas como tokens visuais para os
  acentos dos grupos, com grafite e creme para o contraste dos cards e dos
  ícones.

## 2026-08-24 — Atualização do Status Badge a partir do Figma (node 56:766)

* O handoff completo foi extraído para `.handoff/handoff-status-badge.md`,
  usando os exemplos específicos dos nodes `56:883`, `56:888` e `56:893`.
* O componente permanece estático e não clicável, preservando a API existente
  baseada em `SextouStatus.OPEN` e `SextouStatus.CLOSED`; o status
  `UNAVAILABLE` foi adicionado para cobrir a terceira variação documentada.
* As dimensões específicas do Status Badge foram priorizadas sobre o exemplo
  genérico de pill da página: altura 28 dp, padding 10/4 dp, gap 4 dp e
  indicador 6 dp. A largura continua intrínseca ao conteúdo.
* Foram adicionados ao tema somente os tokens ausentes: tipografia semântica
  Bold 12/18, opacidade do container de status, opacidade do indicador aberto,
  opacidade de indisponibilidade e altura do badge. As cores existentes de
  status fechado e superfície de imagem foram reutilizadas.

## 2026-08-28 — Handoff do Quick Action a partir dos nós 146:8, 146:15 e 146:22

* O handoff foi salvo em `.handoff/handoff-quick-action.md`, consolidando os
  três componentes independentes `Favoritar`, `Visitar` e `Ignorar` em uma
  especificação reutilizável com a propriedade semântica `action`.
* A consolidação foi registrada como decisão documental porque o Figma não
  apresenta `COMPONENT_SET`, propriedades de componente ou reações; a
  semelhança de nome, anatomia, dimensões e descrições sustenta a modelagem,
  mas não comprova uma API publicada no arquivo.
* O handoff preserva os tokens Figma vinculados (`surface_container`,
  `outline_variant` e `shape_medium`), registra as cores literais dos vetores
  e deixa explícitos como lacunas os estados de interação, o alvo de toque, o
  papel de acessibilidade e o feedback após as ações.

## 2026-08-28 — Implementação do Quick Action

* `SextouQuickAction` foi implementado no módulo `design-system` como um
  componente stateless sem callback ou semântica de botão, preservando as
  lacunas de interação explicitamente deixadas pelo handoff para o contexto
  consumidor.
* A propriedade pública `action` foi modelada como
  `SextouQuickActionDefaults.Action`, com as três ações do handoff; o estilo
  compartilhado ficou em `SextouQuickActionDefaults.Style`.
* Foram adicionados tokens centrais para os valores exatos de superfície,
  borda, tipografia e dimensões que não existiam no tema. Os três ícones foram
  exportados dos nós Figma e convertidos para VectorDrawable, mantendo o path
  fornecido pela fonte e aplicando a cor por tint do estilo.

## 2026-08-28 — Validação do Quick Action no device via ADB

* A execução do harness temporário no aparelho revelou que o helper de cores
  passava valores ARGB como `ULong` empacotado, fazendo o Compose interpretar
  os bits inferiores como um índice inválido de `ColorSpace`.
* A conversão foi corrigida para usar o construtor ARGB de `Color(Long)`, sem
  alterar os valores dos tokens, para permitir a renderização real do tema e
  do Quick Action no device.

## 2026-08-28 — Interação e seleção do Quick Action

* A confirmação funcional do produto estendeu o contrato do handoff: o
  componente agora recebe `onClick`, `selected` e `enabled`, usando state
  hoisting para que a regra de cada ação permaneça fora do design system.
* Foi usada a primitiva `selectable` com papel de botão, foco e ripple
  delimitado pelo shape do tile; o ícone é decorativo porque o rótulo já
  fornece a descrição acessível da ação.
* Foram adicionadas variantes VectorDrawable preenchidas para os três glifos;
  o estado `selected` escolhe essas variantes sem alterar as cores semânticas
  definidas para cada ação.

## 2026-08-28 — Opacidade do preenchimento selecionado

* Para destacar melhor a borda do tile, a confirmação visual do produto aplica
  30% de transparência (alpha `0.7`) somente à cor do ícone preenchido no
  estado selecionado.
* A mesma regra é compartilhada por `FAVORITAR`, `VISITAR` e `IGNORAR`, usando
  o token central `SextouPrimitiveAlpha.QuickActionSelectedIconAlpha`.

## 2026-08-28 — Ajuste da opacidade do preenchimento selecionado

* A opacidade do preenchimento dos ícones selecionados foi reduzida de 70%
  para 50% conforme solicitado, mantendo a regra aplicada igualmente às três
  ações para preservar o destaque da borda.

## 2026-08-28 — Contorno preservado no Quick Action Ignorar

* O estado selecionado de `IGNORAR` não substitui mais o glifo original por
  uma forma diferente: mantém o círculo e a diagonal na cor integral do
  contorno e desenha apenas um preenchimento circular interno da mesma cor
  com alpha `0.5` por baixo.

## 2026-08-28 — Contorno opaco dos Quick Actions selecionados

* O estado selecionado de `FAVORITAR` e `VISITAR` passou a compor o
  preenchimento com alpha `0.5` sob o vetor de contorno original, mantendo a
  borda na mesma cor e com 100% de opacidade.
* A composição foi unificada com `IGNORAR` em duas camadas: preenchimento
  translúcido abaixo e contorno original opaco acima.

## 2026-08-28 — Auditoria de paridade do handoff do Quick Action

* O handoff foi completado para refletir o contrato implementado: cor do
  ripple, estados `selected` e `enabled`, defaults, semântica de botão, foco,
  ausência de variante visual desabilitada e dimensões invariáveis entre os
  estados.
* A seção de animação foi alinhada ao template, distinguindo o ripple
  transitório da troca imediata das camadas de preenchimento e contorno.

## 2026-08-28 — Implementação da tela Feed a partir do Figma (node 65:1406)

* A tela foi implementada como uma feature de apresentação em
  `app/src/main/java/com/sextou/features/feed`, separando `FeedDestination`,
  `FeedScreen`, componentes visuais e `FeedViewModel`. A composição reutiliza
  `SextouBrand`, `SextouSearchBar`, `SextouProfileButton`,
  `SextouSectionHeader`, `SextouStatusBadge` e `SextouMoreButton`, sem criar
  APIs duplicadas no módulo `design-system`.
* O frame Figma representa o status bar como parte do mockup; a implementação
  deixa essa área sob responsabilidade do sistema Android e renderiza apenas
  o conteúdo da aplicação. Os controles visuais de 36/38 dp mantêm alvo de
  toque de pelo menos 48 dp, conforme as regras de acessibilidade do
  `DESIGN.md`.
* Como o domínio existente expõe apenas o contrato remoto genérico do Google
  Places e ainda não possui um caso de uso de feed, os sete estabelecimentos
  do frame foram modelados como fixture local em `FeedUiState`. O ViewModel
  mantém busca, seleção de favoritos, lista de visitas e aba selecionada para
  deixar a tela navegável; a conexão com um caso de uso/repositório real fica
  como próximo passo quando o contrato de produto estiver definido.
* As três imagens disponíveis na referência foram salvas em
  `drawable-nodpi`; os quatro cards restantes usam um tratamento de
  placeholder sem imagem, mantendo a hierarquia e as informações do layout.
  Os ícones específicos da tela foram convertidos para VectorDrawable a
  partir dos exports do Figma e mantidos no módulo `app` por serem parte da
  composição do feed.

## 2026-08-28 — Correção dos estados da bottom navigation a partir do Figma

* O fundo externo de `FeedBottomNavigation` foi preservado; somente a anatomia
  e os estados visuais dos três botões foram ajustados.
* O Feed mantém o círculo laranja como ação central em todos os estados.
  Mapas e Favoritos selecionados não recebem esse círculo: usam os glifos
  exportados selecionados e suas cores/rótulos correspondentes.
* Os assets selecionados/inativos foram convertidos fielmente dos exports Figma
  para VectorDrawable. O terceiro destino permanece `Favoritos`, alinhado ao
  contrato atual do app; o frame de Feed selecionado apresenta `Locais`, uma
  divergência de nomenclatura da referência que não foi propagada para a
  navegação existente.
* O alvo interativo continua com pelo menos 48 dp, enquanto os glifos
  preservam as dimensões visuais da referência.

## 2026-08-28 — Ripple do botão Feed na bottom navigation

* O `selectable` do Feed foi movido do contêiner de 56 dp para o botão laranja
  visual de 64 dp, que fica deslocado 32 dp para cima.
* O botão visual recebeu recorte com o mesmo raio da superfície para manter o
  ripple dentro de toda a área laranja, incluindo a parte que ultrapassa o
  contêiner da navegação.
* Os demais botões e o fundo externo da bottom navigation permaneceram
  inalterados.

## 2026-08-28 — Gradiente do símbolo de fogo

* O preenchimento do símbolo em `.codex/assets/sextou-fire.svg` passou a usar
  um gradiente diagonal a aproximadamente 45 graus, partindo do amarelo na
  região inferior esquerda e chegando ao vermelho na região superior direita.
* As três cores originais (`#FFC400`, `#FF9100` e `#DD2C00`) foram mantidas
  como pontos do gradiente, enquanto o canal alfa do bitmap original foi
  reutilizado como máscara para preservar exatamente o contorno e a
  transparência da imagem.

## 2026-08-28 — Ícone adaptativo do aplicativo

* O launcher usa a chama de `.codex/assets/sextou-fire.svg` como foreground
  centralizado em um canvas de 108 dp, com a silhueta visível dentro da área
  segura de 66 dp definida pelo Android. Uma versão monocromática derivada do
  mesmo canal alfa habilita os ícones temáticos do sistema.
* A escala preserva a proporção original entre os assets: o canvas de 192 px
  da chama é mapeado sobre os 328 px úteis do card, resultando em 63,22 dp no
  foreground adaptativo e mantendo o respiro previsto pela composição fonte.
* Nos ícones adaptativos, a cor `#0A0A0B` de
  `.codex/assets/sextou-background.svg` ocupa todo o background; o
  arredondamento e a sombra do SVG não foram incorporados nessa camada porque
  a máscara e os efeitos são aplicados pelo launcher para cada formato.
* Os fallbacks das APIs 24 e 25 mantêm o card escuro, a borda sutil e a sombra
  do background original, com variantes quadrada arredondada e circular em
  todas as densidades Android.

## 2026-08-28 — Integração do feed com Places e SearchPlacesUseCase

* O feed passou a consultar `SearchPlacesUseCase`, que depende do contrato
  `PlacesRepository.Remote`. A consulta vazia com localização usa Nearby Search
  com as categorias do produto; uma consulta textual usa Text Search e mantém
  o viés de localização quando disponível.
* O `FeedViewModel` passou a executar a busca em `viewModelScope`, cancelar uma
  busca substituída, expor estados de carregamento/erro e mapear os campos
  opcionais de `PlaceSummary` para o card sem inventar nota, preço, distância,
  horário ou status de abertura. As fixtures permanecem somente nos previews.
* Não foi criado módulo local, cache ou filtro de lista negra nesta alteração:
  o projeto ainda não possui infraestrutura local e o pedido foi limitado à
  integração remota do feed. Esses comportamentos devem entrar junto de uma
  política de persistência e dos contratos de favoritos/lista negra.

## 2026-08-28 — Correção arquitetural da feature feeds

* A decisão anterior sobre não criar persistência deixou de ser válida para a
  correção solicitada: favoritos e locais visitados agora usam contratos no
  domínio, casos de uso e Room no módulo `local`, com operações idempotentes.
* As credenciais permanecem fora do código-fonte: `PLACES_API_KEY` é injetada
  no módulo de networking e `MAPS_API_KEY` é usada apenas como placeholder da
  metadata do Maps no manifesto, ambas lidas de `local.properties`.
* A navegação de mapa e detalhes foi ligada a rotas tipadas; a aba Favoritos,
  o filtro de locais abertos, retry e estados de erro/stale passaram a ter
  comportamento real. Ações sem contrato implementado foram removidas da UI.
* A validação incluiu testes de domínio, persistência local, ViewModel e
  compilação/lint do app, além da instalação e inicialização em dispositivo
  Android conectado.

## 2026-08-28 — Fundo escuro da splash screen

* O fundo nativo de inicialização passou a reutilizar o mesmo valor semântico
  do feed (`#111111`) por meio de `@color/sextou_background`.
* A variante `values-v31` define explicitamente
  `android:windowSplashScreenBackground`; o tema base mantém o fallback de
  `windowBackground`, status bar e navigation bar para versões anteriores.
