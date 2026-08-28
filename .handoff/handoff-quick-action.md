---
component: Quick Action
stage: plan
created_at: 2026-08-28
next_agent: create-ui-component
source_type: Figma MCP
source_reference: >-
  https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=146-8&t=8ENyNFXeXt7dnf3v-4 (node 146:8),
  https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=146-15&t=8ENyNFXeXt7dnf3v-4 (node 146:15),
  https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=146-22&t=8ENyNFXeXt7dnf3v-4 (node 146:22)
---

# Propriedades

- Nome: action
- Tipo: enumeração
- Descrição: configuração semântica que seleciona uma das três ações visuais
  observadas. As possibilidades são `FAVORITAR`, `VISITAR` e `IGNORAR`; cada
  valor define o rótulo, o ícone e a cor do ícone. Esta é uma normalização do
  handoff para representar os três componentes irmãos como um único Quick
  Action; ela não corresponde a uma propriedade exposta no Figma.

# Styles

Contrato do Estilo:
- fundo do tile: color,
- cor da borda: color,
- espessura da borda: border-width,
- raio do tile: border-radius,
- cor do ícone de ênfase: color,
- cor do ícone atenuado: color,
- cor do rótulo: color,
- cor do ripple: color,
- tipografia do rótulo: typography,
- padding vertical do tile: spacing,
- espaço vertical entre ícone e rótulo: spacing,
- opacidade do preenchimento selecionado: opacity,
- opacidade do contorno selecionado: opacity,

Variantes de estilo:

Default:
- fundo do tile: `md_sys_color_surface_container`, valor observado `#1E1E1E`,
- cor da borda: `md_sys_color_outline_variant`, valor observado `#4D463C`,
- espessura da borda: 1 px, aplicada para dentro do limite do tile,
- raio do tile: `md_sys_shape_medium`, valor observado 12 px,
- cor do ícone de ênfase: `#FE9A00`, usada em `FAVORITAR` e `VISITAR`,
- cor do ícone atenuado: `#9A9080`, usada em `IGNORAR`,
- cor do ripple: `text-primary`, valor aplicado `#F2EDE4`; a fonte Figma
  não especifica a cor do ripple, que foi confirmada como requisito pelo
  produto,
- opacidade do preenchimento selecionado: 50% (50% de transparência),
- opacidade do contorno selecionado: 100%, preservada nos três ícones,
- cor do rótulo: `#9A9080`,
- tipografia do rótulo: Roboto Bold, 11 px, caixa alta, line-height de 16,5
  px e letter-spacing de -0,275 px,
- padding vertical do tile: 12 px no topo e 12 px na base,
- espaço vertical entre ícone e rótulo: 8 px,

# Acessibilidade

Os três nós exibem um rótulo textual visível em caixa alta (`FAVORITAR`,
`VISITAR` ou `IGNORAR`). Quando o tile for usado como ação, o rótulo visível
deve ser o nome acessível do controle e o ícone deve ser tratado como
decorativo, evitando que a mesma ação seja anunciada duas vezes.

A nomenclatura e as descrições dos componentes indicam uso acionável, mas a
fonte não define explicitamente papel semântico, foco, navegação por teclado,
alvo de toque, estado desabilitado ou tratamento de foco. A confirmação
funcional do produto define que o tile é clicável e selecionável; o contrato
atual usa papel de botão, expõe a seleção semanticamente e permite navegação
por foco. O rótulo visível é o nome acessível do controle e o ícone é
decorativo, evitando que a mesma ação seja anunciada duas vezes. Os 102 x 80
px são os limites visuais observados e não constituem, por si só, uma
especificação de alvo de toque acessível.

Quando `enabled` for `false`, o controle não deve ser ativável nem focável e
deve expor a condição desabilitada à tecnologia assistiva. A implementação
atual não altera a aparência visual nesse estado. O contrato atual não possui
uma variante estática: para uso apenas informativo, deve-se usar conteúdo
textual separado, sem semântica de botão.

# Layouts

Não se aplica.

# States

Propriedade-de-configuração: action
Descrição: configuração semântica que altera o conteúdo do ícone, o rótulo e,
no caso de `IGNORAR`, a ênfase cromática do ícone. Estes são tipos de ação
pré-definidos.

Propriedade-de-estado: selected
Descrição: booleano controlado pelo consumidor, com padrão `false`. Quando
`false`, o componente exibe apenas o ícone em contorno; quando `true`, adiciona
o preenchimento com 50% de opacidade (50% de transparência) sob o contorno
original, que permanece na mesma cor e com 100% de opacidade. Em `IGNORAR`, o
mesmo glifo e a mesma cor do contorno permanecem visíveis e recebem apenas um
preenchimento interno translúcido. A borda do tile permanece na cor e na
espessura definidas pelo estilo em ambos os valores. O clique é comunicado por
callback e a atualização de `selected` fica em state hoisting no consumidor.

Propriedade-de-estado: enabled
Descrição: booleano controlado pelo consumidor, com padrão `true`, que define
se o tile pode ser ativado e receber foco. Esse estado não altera as dimensões,
as cores, a borda ou a composição do ícone.

Possibilidades:
## true:
O tile responde ao clique, exibe o ripple e pode receber foco; o estado
`selected` continua controlando a composição do ícone.
## false:
O tile não responde ao clique, não exibe ripple e não recebe foco. A aparência
visual permanece igual, inclusive se `selected` estiver em `true`.

Possibilidades de `action`:
## FAVORITAR:
Exibe um ícone vetorial de coração na cor de ênfase `#FE9A00` e o rótulo
`FAVORITAR`. Em estado não selecionado o coração é de contorno; em estado
selecionado, recebe preenchimento com 50% de opacidade sob o contorno original,
que permanece 100% opaco. A descrição do componente no Figma define a ação
como salvar um estabelecimento.

## VISITAR:
Exibe um ícone vetorial de marcador/bookmark na cor de ênfase `#FE9A00` e o
rótulo `VISITAR`. Em estado não selecionado o marcador é de contorno; em
estado selecionado, recebe preenchimento com 50% de opacidade sob o contorno
original, que permanece 100% opaco. A descrição do componente no Figma define
a ação como abrir a rota ou a página do estabelecimento.

## IGNORAR:
Exibe um ícone vetorial de proibição, com círculo e barra diagonal, na cor
atenuada `#9A9080` e o rótulo `IGNORAR`. Em estado selecionado, mantém o mesmo
glifo de proibição e a mesma cor do contorno, adicionando somente um
preenchimento circular interno com essa cor a 50% de opacidade, mantendo o
contorno 100% opaco. A descrição do componente no Figma define a ação como
dispensar um estabelecimento.

Além da seleção, o componente possui estado pressionado transitório com
ripple delimitado pelo tile. Não há estados de hover, loading ou erro
especificados; `enabled=false` é o estado funcional desabilitado descrito
acima, sem variante visual própria.

# Constraints

- Os três estados usam um tile com largura fixa de 102 px e altura fixa de 80
  px.
- O tile usa composição vertical, com alinhamento horizontal centralizado.
- O layout do Figma informa padding esquerdo e direito de 0 px, padding de 12
  px no topo e na base e espaçamento vertical de 8 px entre os dois blocos.
- O contorno tem 1 px, é aplicado internamente, e o raio é de 12 px em todos
  os cantos.
- O primeiro bloco tem altura de 28 px e mantém o ícone centralizado
  horizontalmente. A posição observada começa em y=13 px; a diferença de 1 px
  em relação ao padding nominal decorre do contorno interno.
- O bloco do rótulo fica em y=49 px e permanece em uma única linha, sem quebra.
  Sua largura deve ser intrínseca ao conteúdo, mantendo o tile fixo e o texto
  centralizado.
- As constraints do componente no Figma são `STRETCH` na horizontal e na
  vertical. A fonte não demonstra redimensionamento responsivo do tile; a
  dimensão fixa de 102 x 80 px é a referência visual.

## FAVORITAR

- O container do ícone mede 18 x 28 px e possui padding interno de 4,25 px no
  topo e 5,75 px na base.
- O frame do ícone mede 18 x 18 px; o vetor visível do coração ocupa 18 x
  15,37 px dentro desse frame.
- O rótulo observado ocupa largura intrínseca de aproximadamente 59,45 px.

## VISITAR

- O container do ícone mede 13,5 x 28 px e possui padding interno de 4,25 px
  no topo e 5,75 px na base.
- O frame do ícone mede 13,5 x 18 px; o vetor visível do marcador ocupa
  aproximadamente 13,5 x 18 px.
- O rótulo observado ocupa largura intrínseca de aproximadamente 40,66 px.

## IGNORAR

- O container do ícone mede 18 x 28 px e possui padding interno de 4,25 px no
  topo e 5,75 px na base.
- O frame e o vetor visível do ícone de proibição medem 18 x 18 px.
- O rótulo observado ocupa largura intrínseca de aproximadamente 50,03 px.

Os estados `selected` e `enabled` não alteram a largura, a altura, o raio, a
borda, o padding, o posicionamento dos blocos ou o tamanho dos ícones. Quando
`selected=true`, a camada de preenchimento usa o mesmo frame do contorno e não
cria espaço adicional no layout.

# Anatomy

Sempre visíveis:
- tile retangular com fundo escuro, contorno interno e cantos arredondados;
- container vertical do ícone, com altura de 28 px;
- contorno vetorial original do ícone, centralizado e sempre visível;
- rótulo textual em caixa alta, centralizado abaixo do ícone.

Configuráveis por parâmetro:
- Ícone: muda conforme `action`; coração para `FAVORITAR`, marcador/bookmark
  para `VISITAR` e proibição para `IGNORAR`.
- Rótulo: muda conforme `action`, permanecendo respectivamente `FAVORITAR`,
  `VISITAR` ou `IGNORAR`.
- Cor do ícone: usa a cor de ênfase em `FAVORITAR` e `VISITAR` e a cor
  atenuada em `IGNORAR`.
- Camada de preenchimento selecionado: aparece quando `selected=true`, abaixo
  do contorno original, com 50% de opacidade. Em `FAVORITAR` e `VISITAR`,
  preenche a silhueta do respectivo ícone; em `IGNORAR`, preenche somente o
  círculo interno.

Slots:

Não se aplica.

# Description

- Developer: Quick Action é um tile visual compacto, stateless e composto por
  uma superfície, um ícone e um rótulo. As três configurações fornecidas pelo
  Figma são componentes independentes, mas têm a mesma dimensão, hierarquia e
  composição; o handoff as normaliza pela propriedade `action`. O componente
  mantém a largura e altura fixas, centraliza os dois blocos e resolve a
  iconografia e o texto a partir da ação escolhida. O componente não deve
  incorporar a regra de salvar, visitar ou dispensar um estabelecimento; ele
  apenas apresenta a ação recebida, comunica o clique, expõe `selected` e
  respeita `enabled` conforme o contexto que o utiliza.

- Design: a linguagem visual usa uma superfície grafite escura (`#1E1E1E`),
  contorno marrom-grafite sutil (`#4D463C`), raio médio de 12 px e uma pilha
  vertical com 8 px de separação. Favoritar e Visitar recebem ícones laranja
  de destaque; Ignorar reduz a ênfase do ícone para o mesmo tom quente e
  acinzentado usado no rótulo. O texto é curto, pesado, em caixa alta e
  alinhado ao centro. No estado selecionado, o preenchimento translúcido é
  desenhado sob o contorno original 100% opaco. As diferenças de largura dos
  ícones e rótulos não devem alterar os limites externos do tile.

- Product: o componente oferece três decisões rápidas para um estabelecimento:
  salvar para consulta posterior, abrir uma rota ou página e dispensar a
  sugestão. Os rótulos foram definidos em português na fonte e devem
  permanecer claros no contexto em que a ação aparece. O comportamento
  acionável, o feedback de ripple e a seleção visual foram confirmados pelo
  produto; a possibilidade de desfazer `IGNORAR` continua pertencendo ao
  fluxo de produto e não está especificada nos nós fornecidos.

# Animation

Interação de ativação e pressão:

- O tile é clicável e expõe um callback para o consumidor; a atualização do
  estado `selected` usa state hoisting.
- O clique/pressionamento exibe um ripple bounded, recortado pelo raio do
  tile. No estilo padrão, a cor do ripple é `text-primary` (`#F2EDE4`); a
  confirmação do produto exige o feedback, mas a fonte Figma não define sua
  cor.
- Possui a mesma animação reversa: após o fim da pressão, o ripple desaparece
  usando o comportamento padrão do mecanismo de feedback da plataforma.
- Tempo total e tempo de cada etapa: não especificados pela fonte; não há
  duração customizada no contrato visual.
- Interpolação: não especificada; deve seguir o comportamento padrão do
  ripple da plataforma.
- Efeito: expansão e desaparecimento do ripple dentro dos limites do tile.
- Quantidade de vezes: uma indicação por interação de pressionamento.

Transição de seleção:

- Não existe animação adicional de escala, cor ou opacidade para `selected`.
  A alteração é imediata quando o consumidor fornece o novo valor.
- O preenchimento com 50% de opacidade aparece abaixo do contorno original
  100% opaco nos três ícones. Em `IGNORAR`, ele fica restrito ao círculo
  interno; a diagonal e o anel permanecem opacos.
- Possui a mesma reversão lógica: ao receber `selected=false`, a camada de
  preenchimento deixa de ser exibida.
- Tempo total: imediato, sem interpolação.

## Build

- Fonte principal: arquivo Figma `Sextou`, página `App` (`0:1`), com os
  componentes `146:8` (`Sextou / Quick Action / Favoritar`), `146:15`
  (`Sextou / Quick Action / Visitar`) e `146:22`
  (`Sextou / Quick Action / Ignorar`).
- Os três nós são `COMPONENT`s independentes. A inspeção não encontrou
  `COMPONENT_SET`, `variantProperties`, `componentPropertyDefinitions` ou
  reações de prototipagem. A propriedade `action` e a união em um único
  componente são uma decisão documental baseada no nome comum `Quick Action`,
  na anatomia compartilhada e nas dimensões idênticas; não devem ser tratadas
  como propriedades já publicadas no Figma.
- O fundo, a borda e o raio estão vinculados no Figma aos tokens da coleção
  `md.sys`: `md_sys_color_surface_container`,
  `md_sys_color_outline_variant` e `md_sys_shape_medium`. Os valores
  observados são respectivamente `#1E1E1E`, `#4D463C` e 12 px.
- As cores dos vetores e do rótulo aparecem como valores literais na fonte:
  `#FE9A00` para os ícones de Favoritar/Visitar e `#9A9080` para o ícone de
  Ignorar e para os rótulos. Na implementação atual, esses valores são
  mapeados respectivamente para os tokens semânticos `Primary` e
  `TextSecondary` do design system do Sextou.
- A tipografia observada é Roboto Bold em caixa alta, com 11 px, line-height
  de 16,5 px e letter-spacing de -0,275 px. O rótulo não usa text style local
  do Figma; a implementação atual mapeia a combinação para o token
  tipográfico `QuickAction` do design system do Sextou.
- Os ícones são vetores filhos sem nomes semânticos (`Vector`). A geometria
  exata deve ser reutilizada ou exportada a partir dos nós de origem; a
  implementação atual mantém vetores de contorno para os três ícones e usa
  silhuetas preenchidas auxiliares para `FAVORITAR` e `VISITAR`, além de um
  preenchimento circular interno auxiliar para `IGNORAR`.
- Não foram observados sombra, elevação, blur, gradiente ou preenchimento de
  imagem. O ripple e a seleção são requisitos funcionais confirmados pelo
  produto, embora não estejam representados nos nós Figma fornecidos.
- As opacidades de seleção são requisitos fixos do comportamento visual: alpha
  `0.5` no preenchimento e alpha `1.0` no contorno. Elas não são opções
  adicionais do contrato de estilo atual.
- A composição deve permanecer stateless: o consumidor controla `selected`,
  fornece o callback de ativação e controla `enabled`. Quando `enabled=false`,
  o componente perde ativação e foco sem alterar a aparência ou a seleção
  visual.
- Todas as medidas desta especificação estão em pixels e devem ser
  consideradas dimensões visuais do componente. A implementação deve evitar
  alterar a largura externa para acomodar os rótulos ou ícones específicos de
  cada ação.
