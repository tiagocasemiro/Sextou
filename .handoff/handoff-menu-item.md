---
component: Menu Item
stage: plan
created_at: 2026-08-28
next_agent: create-ui-component
source_type: Figma MCP + especificação textual do usuário
source_reference: >-
  https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=153-17&t=8ENyNFXeXt7dnf3v-4
  (node 153:17, `Sextou / Menu Item`),
  https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=153-18&t=8ENyNFXeXt7dnf3v-4
  (node 153:18, `Dish abbreviation tile`), e requisito textual de que o tile
  aceite imagem ou duas letras com cor aleatória, que os textos sejam
  configuráveis, que o texto secundário e o texto de ênfase amarelo sejam
  opcionais e que o componente não seja clicável.
---

# Propriedades

- Nome: title
- Tipo: texto
- Descrição: texto principal do item, exibido na área central. No exemplar do
  Figma, o valor é `Costela no Bafo (500g)`. O conteúdo deve ser fornecido pelo
  consumidor e pode ocupar até a área de duas linhas observada na referência.

- Nome: supportingText
- Tipo: texto
- Descrição: texto secundário opcional, exibido abaixo do título. No exemplar
  do Figma, o valor é `Serve 2 pessoas`. Quando ausente, não deve ser exibido
  placeholder nem rótulo vazio.

- Nome: highlightText
- Tipo: texto
- Descrição: texto opcional de ênfase, alinhado à direita e apresentado na
  cor amarela/laranja do componente. O layer de origem se chama `Dish price` e
  mostra `R$ 54,90`, mas o valor deve ser configurável e não deve ser tratado
  como uma constante do componente.

- Nome: image
- Tipo: imagem
- Descrição: imagem opcional usada como conteúdo visual do tile de 56 × 56 px.
  É mutuamente exclusiva com `initials`; quando fornecida, substitui a
  abreviação de letras. A fonte não define uma imagem específica.

- Nome: initials
- Tipo: caracteres
- Descrição: abreviação composta por exatamente duas letras, usada quando
  `image` não for fornecida. O exemplar mostra `CB`, centralizado no tile. A
  fonte não define transformação automática de caixa; o valor deve ser
  fornecido já na forma desejada pelo consumidor.

# Styles

Contrato do Estilo:
- fundo do item: color,
- cor da borda do item: color,
- espessura da borda: border-width,
- raio do item: border-radius,
- fundo do tile: color,
- cor do conteúdo textual do tile: color,
- tipografia e cor do título: typography e color,
- tipografia e cor do texto secundário: typography e color,
- tipografia e cor do texto de ênfase: typography e color,
- padding horizontal e vertical do item: spacing,
- espaço horizontal entre tile, informação e ênfase: spacing,
- espaço vertical entre título e texto secundário: spacing,
- dimensões externas do item e do tile: dimensão fixa,

Variantes de estilo:

Default:
- fundo do item: token `md_sys_color_surface_container`, valor observado
  `#1E1E1E`,
- cor da borda do item: token `md_sys_color_outline_variant`, valor observado
  `#4D463C`,
- espessura da borda: 1 px, aplicada no contorno do item,
- raio do item: token `md_sys_shape_medium`, valor observado 12 px,
- fundo do tile: cor de acento escolhida aleatoriamente por item a partir da
  paleta aprovada pelo produto; o exemplar do Figma usa o token
  `md_sys_color_primary_container`, com valor observado `#7F2D12`,
- cor do conteúdo textual do tile: token
  `md_sys_color_on_primary_container`, com valor observado `#FFF6EA`; a cor
  escolhida para o tile deve manter contraste suficiente com esse conteúdo,
- tipografia e cor do título: Roboto Bold, 14 px, line-height 20 px, token
  `md_sys_color_on_surface`, valor observado `#F2EDE4`,
- tipografia e cor do texto secundário: Roboto Regular, 12 px, line-height
  16 px, token `md_sys_color_on_surface_variant`, valor observado `#C8BFB0`,
- tipografia e cor do texto de ênfase: Roboto Bold, 14 px, line-height 20 px,
  token `md_sys_color_primary`, valor observado `#FE9A00`,
- padding horizontal do item: 12 px,
- padding vertical do item: 12 px,
- espaço horizontal entre os blocos: 12 px,
- espaço vertical entre título e texto secundário: 2 px,
- dimensões externas do item: 342 × 84 px,
- dimensões do tile: 56 × 56 px,
- raio do tile: token `md_sys_shape_medium`, valor observado 12 px,
- sombra, elevação, gradiente e blur: não se aplicam na referência.

A cor aleatória do tile é um requisito textual do usuário, não uma variante
de estilo publicada no Figma. A paleta permitida, a forma de sorteio e o
momento em que a cor deve ser fixada não estão definidos pela fonte.

# Acessibilidade

- O componente é informativo e não interativo. Não deve expor papel de botão
  ou link, callback de ativação, estado pressionado, foco próprio, ripple ou
  alvo de toque.
- O título deve permanecer disponível para a árvore semântica. Quando
  presentes, `supportingText` e `highlightText` também devem ser anunciados
  como conteúdo do mesmo item, em uma ordem equivalente à leitura visual:
  título, apoio e texto de ênfase.
- O tile de imagem ou de abreviação não deve receber foco nem ser anunciado
  como uma ação independente. Quando a imagem apenas ilustra o item e o
  título já identifica o conteúdo, ela deve ser tratada como decorativa para
  evitar anúncio duplicado.
- A fonte não define texto alternativo para imagens nem uma regra para
  imagens que carreguem informação adicional. Se uma imagem acrescentar
  significado que não esteja no texto do item, o consumidor deverá fornecer
  essa descrição no contexto acessível que engloba o componente.
- A cor aleatória é decorativa e não pode ser a única forma de comunicar
  informação semântica. O contraste entre o fundo escolhido e as duas letras
  deve ser preservado.

# Layouts

Nome: conteúdo visual do tile
Descrição geral: a área esquerda mantém as mesmas dimensões e o mesmo
posicionamento, mas seu conteúdo pode ser uma imagem ou uma abreviação textual.
As opções são mutuamente exclusivas e não alteram a altura, a largura ou a
posição dos demais blocos.

Possibilidades:
## Imagem:
O tile exibe a imagem fornecida em `image`, limitada à área de 56 × 56 px e
recortada pelos cantos arredondados de 12 px. O tratamento de escala e recorte
da imagem não é observável no Figma e deve seguir a política de imagens do
produto; não há placeholder visual definido.
## Abreviação:
O tile exibe as duas letras fornecidas em `initials`, centralizadas
horizontal e verticalmente. O exemplar mostra `CB`; a abreviação não deve
quebrar em mais de uma linha nem alterar as dimensões do tile.

# States

Não se aplica. O componente não é clicável e a fonte não apresenta estados de
hover, pressionado, selecionado, desabilitado, carregando ou erro. A ausência
dos textos opcionais e a troca entre imagem e letras são configurações de
conteúdo/layout, não estados transitórios.

# Constraints

- A referência externa do item mede 342 × 84 px.
- O item usa borda de 1 px, raio de 12 px e padding horizontal e vertical de
  12 px. Não há sombra, elevação, gradiente ou blur.
- O tile ocupa 56 × 56 px. Ele fica à esquerda, centralizado verticalmente,
  com raio de 12 px e conteúdo recortado pelo seu limite.
- No nó de referência, o tile começa em x=12 px e y=14 px. A diferença entre
  o padding vertical nominal e a posição observada decorre do contorno do
  item.
- O espaço horizontal entre o tile, a área de informação e o texto de
  ênfase é 12 px.
- A área `Dish information` começa em x=80 px, y=14 px e mede 172 × 56 px no
  exemplar. Seus textos internos têm largura observada de 168 px.
- O título ocupa uma região de 40 px de altura, permite quebra de palavra e
  não tem regra de truncamento/reticências definida na fonte.
- O texto secundário fica 2 px abaixo da região do título e ocupa uma região
  de 16 px de altura no exemplar.
- O texto de ênfase começa em x=264 px, y=32 px, mede 66 × 20 px e é alinhado
  à direita. Quando `highlightText` estiver ausente, não deve ser exibido
  espaço reservado visível; a área de informação pode usar o espaço horizontal
  remanescente até o padding direito, sem alterar a altura externa do item.
- Quando `supportingText` estiver ausente, sua linha e seu gap devem ser
  removidos, sem alterar a altura externa nem a posição do tile. A fonte não
  apresenta uma combinação sem texto secundário; este handoff mantém o título
  ancorado no início da área de informação, sem re-centralizá-lo
  automaticamente.
- A configuração por imagem e a configuração por duas letras compartilham
  todas as constraints externas. A imagem deve respeitar 56 × 56 px e as
  letras devem permanecer centralizadas dentro desse mesmo espaço.
- A fonte não especifica comportamento responsivo para larguras diferentes de
  342 px. A dimensão de 342 × 84 px é a referência visual a preservar; caso o
  componente seja colocado em uma largura menor, a regra de adaptação deve ser
  validada com o produto.

# Anatomy

Sempre visíveis:
- superfície horizontal do item, com fundo escuro, contorno e cantos
  arredondados;
- tile visual de 56 × 56 px, com raio de 12 px;
- área central de informação do item;
- título principal;

Configuráveis por parâmetro:
- Conteúdo do tile: recebe uma imagem ou duas letras, conforme `image` ou
  `initials`; somente uma das opções deve ocupar a área ao mesmo tempo;
- Cor do tile: recebe uma cor de acento aleatória por item, com contraste
  adequado para o conteúdo do tile;
- Título: recebe o valor de `title`;
- Texto secundário: recebe `supportingText` e fica oculto quando a propriedade
  não é fornecida;
- Texto de ênfase: recebe `highlightText`, fica à direita e na cor de
  destaque quando fornecido, e desaparece quando ausente;
- Imagem: quando usada, preenche o tile segundo a política de imagem definida
  pelo produto e permanece recortada pelo raio do tile;
- Abreviação: quando usada, exibe exatamente duas letras centralizadas no
  tile;

Slots:

Não se aplica. O componente não possui área para receber componentes externos.

# Description

- Developer: Menu Item é uma linha informativa, stateless e não clicável para
  apresentar um item de cardápio em um contexto de detalhe de estabelecimento.
  A implementação deve separar o conteúdo configurável da aparência: `title`
  é o conteúdo principal, `supportingText` e `highlightText` podem desaparecer
  sem placeholders, e o tile recebe imagem ou duas letras. A linha de
  referência tem 342 × 84 px, com tile de 56 × 56 px, e deve preservar o ritmo
  vertical mesmo quando os campos opcionais não forem fornecidos. O componente
  não deve incorporar navegação, seleção, callback, foco ou qualquer ação de
  usuário. A cor aleatória do tile deve ser estável durante a vida visual do
  item; caso fosse recalculada a cada atualização de tela, a interface poderia
  mudar de cor sem alteração de conteúdo.

- Design: a composição é uma superfície grafite com contorno marrom-grafite
  sutil e raio médio de 12 px. O tile quente à esquerda funciona como a âncora
  visual do item: no exemplar ele usa marrom-avermelhado e letras claras, mas
  o requisito do produto permite variar o acento por item. O título tem maior
  ênfase e pode ocupar duas linhas; o texto de apoio é menor e neutro; o texto
  final usa o laranja da marca e permanece alinhado à direita. Imagem e
  abreviação são tratamentos alternativos da mesma área e não devem alterar a
  geometria da linha. A ausência dos textos opcionais deve resultar em uma
  composição limpa, sem rótulos vazios ou artefatos de espaçamento.

- Product: use o componente para resumir rapidamente um prato ou outro item
  de cardápio dentro do detalhe de um estabelecimento. O usuário deve
  identificar o item pelo título, entender um contexto adicional quando houver
  texto secundário e consultar o preço ou outra informação destacada quando
  houver texto de ênfase. A linha é deliberadamente passiva: qualquer ação de
  abrir detalhes, adicionar, favoritar ou selecionar deve pertencer a um
  componente ou fluxo externo. A configuração de imagem/abreviação permite
  representar itens com fotografia ou com um identificador curto quando uma
  imagem não estiver disponível.

# Animation

Não se aplica. O componente é não interativo e a fonte não especifica entrada,
saída, mudança de conteúdo ou transição animada.

## Build

- Fonte principal: arquivo Figma `Sextou`, nos nós `153:17` (`Sextou / Menu
  Item`) e `153:18` (`Dish abbreviation tile`). O Figma descreve o nó principal
  como uma linha reutilizável de pré-visualização de menu para detalhes de
  estabelecimento.
- O nó `153:17` contém o tile `153:18`, a área `153:20` (`Dish information`) e
  o texto `153:23` (`Dish price`). Dentro do tile, o texto `153:19`
  (`Dish abbreviation`) é o conteúdo textual observado.
- A renderização do Figma mostra um item de 342 × 84 px. O tile tem 56 × 56
  px; a área de informação tem 172 × 56 px; e o texto de preço ocupa 66 × 20
  px. As posições e dimensões específicas estão detalhadas em
  `Constraints`.
- Os valores variáveis observados no Figma são
  `md_sys_color_surface_container` (`#1E1E1E`),
  `md_sys_color_outline_variant` (`#4D463C`),
  `md_sys_shape_medium` (12 px),
  `md_sys_color_primary_container` (`#7F2D12`),
  `md_sys_color_on_primary_container` (`#FFF6EA`),
  `md_sys_color_on_surface` (`#F2EDE4`),
  `md_sys_color_on_surface_variant` (`#C8BFB0`) e
  `md_sys_color_primary` (`#FE9A00`). Esses nomes devem ser mapeados para o
  sistema de tokens do projeto, sem transformar os valores observados em
  estilos locais do componente.
- A tipografia observada é Roboto: título e texto de ênfase em Bold, apoio em
  Regular. Os valores tipográficos pertencem ao contrato de estilo e devem
  ser resolvidos pelo tema da plataforma.
- O Figma fornece somente o estado com abreviação `CB`; a alternativa de
  imagem, a cor aleatória, os textos configuráveis e a opcionalidade do apoio
  e do texto amarelo vêm da especificação textual do usuário. Não há asset de
  imagem específico a exportar a partir dos nós fornecidos.
- A cor do tile precisa ser escolhida de uma paleta aprovada que preserve
  contraste com o conteúdo. A fonte não define paleta, distribuição aleatória,
  seed, persistência ou fallback; esses pontos devem ser confirmados pelo
  produto. Este handoff recomenda fixar a cor por identidade do item durante
  sua exibição, para evitar que recomposições ou atualizações de dados troquem
  o acento sem motivo visual.
- A fonte mostra todos os textos preenchidos. Para atender à solicitação de
  que o apoio e o texto de ênfase sejam opcionais, este handoff assume que os
  campos ausentes são removidos sem placeholder, que a altura externa de 84 px
  e o tile permanecem estáveis, e que a área central aproveita o espaço livre
  quando o texto de ênfase não existir. O alinhamento vertical sem apoio é uma
  decisão documental; deve ser validado no preview antes da implementação.
- Não foram observados reações de prototipagem, estados interativos,
  animações, sombra, elevação, gradiente, blur ou slots externos. A
  implementação deve manter o componente informativo e não clicável.
