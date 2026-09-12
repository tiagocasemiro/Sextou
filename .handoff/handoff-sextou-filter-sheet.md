---
component: Sextou Filter Sheet
stage: plan
created_at: 2026-09-12
next_agent: create-ui-component
source_type: Figma MCP, plano de implementação e DESIGN.md
source_reference: Figma file pASFSURvaP2uIMXDZOFNk5, node 209:29 (painel 209:619), https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=209-29; assets/plano-painel-filtros-feed.md; DESIGN.md
---

# Propriedades

- Nome: title
- Tipo: texto
- Descrição: Título exibido no cabeçalho do painel. A referência visual usa “FILTROS”.

- Nome: groups
- Tipo: estrutura de dados
- Descrição: Lista ordenada de grupos. Cada grupo possui identificador, título, modo de apresentação e opções ordenadas; os modos observados são chips, cartões de preço e interruptores.

- Nome: selectedIds
- Tipo: estrutura de dados
- Descrição: Conjunto de identificadores das opções atualmente selecionadas. A seleção é controlada pelo consumidor e pode conter múltiplas opções do mesmo grupo.

- Nome: applyLabel
- Tipo: texto
- Descrição: Rótulo da ação principal exibida no rodapé. A referência usa “Aplicar filtros”.

- Nome: enabled
- Tipo: boleano
- Descrição: Indica se as opções e a ação principal podem ser alteradas. O fechamento continua disponível quando o painel está desabilitado.

# Styles

Contrato do Estilo:
- superfície do painel: cor,
- scrim atrás do painel: cor com opacidade,
- borda superior e divisórias: cor,
- cantos superiores do painel: curva,
- alça: cor, largura e altura,
- título e rótulos de seção: tipografia e cor,
- texto das opções: tipografia e cor para não selecionado, selecionado e desabilitado,
- fundo e borda dos chips: cor para não selecionado, selecionado e desabilitado,
- fundo e borda dos cartões de preço: cor para não selecionado, selecionado e desabilitado,
- trilho e polegar dos interruptores: cores para desligado, ligado e desabilitado,
- botão de fechar: superfície, ícone e cor de interação,
- ação principal: estilo de botão, incluindo conteúdo, superfície, interação e desabilitado,
- espaçamentos e dimensões complementares: tokens de dimensão.

Variantes de estilo:
Dark Sextou:
- superfície do painel: superfície tonal escura do Sextou, equivalente visual a `#1C1C1C`,
- scrim atrás do painel: preto com opacidade suficiente para separar o modal do feed,
- borda superior e divisórias: branco com baixa opacidade, equivalente a 8%,
- cantos superiores do painel: 24 px, arredondados somente no topo,
- alça: superfície neutra elevada, equivalente visual a `#52525C`,
- título e rótulos de seção: texto primário claro; rótulos em caixa alta com texto secundário/muted e tracking ampliado,
- texto das opções: texto primário claro, com indicação adicional de seleção além da cor,
- fundo e borda dos chips: superfície variante escura, borda clara de baixa opacidade; seleção usa primária e borda de destaque,
- fundo e borda dos cartões de preço: superfície variante escura, borda clara de baixa opacidade; seleção usa primária e borda de destaque,
- trilho e polegar dos interruptores: trilho neutro escuro e polegar claro quando desligado; estado ligado usa o papel primário/ativo do tema,
- botão de fechar: superfície variante escura circular, ícone taupe de fechar,
- ação principal: botão primário Sextou, com texto e ícone em `on-primary`,
- espaçamentos e dimensões complementares: escala de espaçamento Sextou e alvos interativos de no mínimo 48 px.

Os valores acima distinguem evidências observadas do Figma de nomes semânticos do tema; a implementação deve resolver os papéis pelos tokens existentes do Sextou. A aparência selecionada não está demonstrada no nó fornecido: usar primária, borda de destaque e indicação semântica conforme o plano aprovado.

# Acessibilidade

- O painel é modal e deve anunciar o título como cabeçalho.
- O botão de fechar deve ter descrição acessível equivalente a “Fechar filtros” e permanecer ativável mesmo quando os controles estiverem desabilitados.
- Cada chip e cartão de preço deve anunciar seu rótulo e se está selecionado ou não. Preços devem anunciar símbolo e descrição juntos, por exemplo “$$, Médio”.
- Cada linha de interruptor deve ser uma única ação acessível, anunciar o rótulo e o estado ligado/desligado e não disparar duas alterações pelo toque na linha e no controle.
- Toda opção deve ter área interativa mínima de 48 × 48 px, mesmo quando a cápsula visual for menor.
- O significado de seleção não pode depender somente de cor; borda, estado semântico ou outro indicador deve acompanhar a mudança visual.
- O conteúdo deve continuar legível e sem truncar rótulos essenciais com fonte ampliada.
- O fechamento deve ser possível por botão, ação de voltar, toque externo e gesto de fechamento, conforme o comportamento modal do produto.

# Layouts

Nome: GroupLayout
Descrição geral: cada grupo usa um modo de apresentação próprio, preservando ordem de leitura e permitindo que o corpo role quando a janela ou a escala de fonte não comportarem todo o conteúdo.

Possibilidades:
## CHIPS:
Opções em cápsulas com quebra de linha. A ordem é da esquerda para a direita e de cima para baixo; tipos e categorias usam este modo.

## PRICE_CARDS:
Opções em cartões lado a lado quando houver espaço. Cada cartão combina símbolo monetário e descrição em duas linhas; em largura insuficiente ou fonte ampliada pode quebrar o conteúdo sem perder o rótulo.

## SWITCHES:
Opções em linhas com rótulo à esquerda e interruptor à direita. A linha inteira é o alvo de interação; os interruptores são independentes entre si.

# States

Propriedade-de-estado: selectedIds
Descrição: altera a indicação visual e semântica da opção sem modificar o conteúdo, a ordem ou os demais grupos.

Possibilidades:
## Não selecionado:
Chip ou cartão usa superfície tonal e borda neutra; interruptor permanece desligado.

## Selecionado:
Chip ou cartão usa o papel primário e borda de destaque, mantendo contraste e indicação semântica; interruptor permanece ligado. O nó fornecido não mostra este estado; essa é uma decisão de implementação do plano.

Propriedade-de-estado: enabled
Descrição: controla a interação dos controles e da ação de confirmação, preservando a leitura do conteúdo e permitindo fechar o modal.

Possibilidades:
## true:
Opções, interruptores, cartões, chips e ação principal respondem à interação.

## false:
Opções e ação principal ficam visualmente desabilitadas e não emitem alterações; botão de fechar e mecanismos de dismiss continuam disponíveis.

Propriedade-de-estado: modal
Descrição: o painel aparece sobre o conteúdo da tela como superfície inferior modal.

Possibilidades:
## aberto:
Painel expandido a partir da parte inferior, com scrim e conteúdo rolável.

## fechado:
O painel não ocupa a tela e o scrim não é exibido. O estado é controlado pelo consumidor do componente.

# Constraints

Referência observada no frame `209:619`: painel de 388 × 761 px, posicionado no rodapé do frame de 402 × 892 px. Os valores abaixo são medidas da referência em pixels; a implementação Android deve tratá-los como intenção responsiva e usar os tokens equivalentes.

- Painel: largura disponível, altura limitada pela janela e pelos insets; cantos superiores de 24 px; overflow interno recortado.
- Área da alça: 20 px de altura; alça centralizada com 40 × 4 px, iniciando 12 px abaixo do topo e com 4 px de respiro inferior.
- Cabeçalho: 57 px de altura após a alça; padding horizontal de 20 px e vertical de 12 px; divisor inferior de 1 px; botão de fechar visual de 32 × 32 px, com ícone de 15 × 15 px.
- Corpo: padding horizontal de 20 px; seções separadas por divisórias de 1 px com 20 px de recuo; corpo verticalmente rolável e ação inferior fora da área rolável quando possível.
- Títulos de seção: padding superior de 16 px; título com altura visual de 15 px; espaço de 12 px até o conteúdo.
- Tipos: área de conteúdo com 130 px de altura na referência; chips visuais com 38 px de altura, padding horizontal de 12 px e vertical de 8 px; distância vertical entre linhas de 8 px; permitir mais linhas em janelas estreitas.
- Preços: seção com conteúdo de 70 px; cartões visuais com 58 px de altura, três colunas flexíveis, intervalo horizontal de 8 px, raio de 14 px e padding vertical de 10 px; largura de referência aproximada de 110,67 px por cartão.
- Outros filtros: três linhas visuais de 20 px, intervalo vertical de 14 px e 12 px de espaço antes da primeira linha; interruptor visual de 40 × 20 px; cada linha deve expandir o alvo de toque para no mínimo 48 px.
- Categorias: área de conteúdo com 84 px de altura na referência; chips visuais com 38 px de altura, duas linhas com 8 px de distância vertical e quebra responsiva.
- Rodapé: área de 88 px; ação com largura disponível dentro de 20 px de recuo lateral; a referência usa 348 × 52 px, enquanto o plano determina reutilizar o botão Sextou com altura padrão de 48 dp.
- Insets: respeitar barras do sistema e teclado; a ação e o último conteúdo devem permanecer alcançáveis quando a janela for menor ou a fonte estiver ampliada.

# Anatomy

Sempre visíveis:
- scrim modal atrás da superfície,
- superfície inferior com cantos superiores arredondados,
- alça superior,
- cabeçalho com título e fechamento,
- corpo com grupos na ordem recebida,
- divisórias entre grupos,
- ação principal no rodapé.

Configuráveis por parâmetro:
- título: texto do cabeçalho,
- grupos: quantidade, títulos, modo de layout e opções exibidas,
- seleção: estado de cada opção,
- rótulo da ação: texto do botão principal,
- habilitação: estado interativo do conteúdo.

Slots:
Não se aplica. O componente recebe dados estruturados e não substitui regiões internas por conteúdo externo.

# Description

- Developer

O Filter Sheet é um painel modal inferior controlado externamente. Ele apresenta uma coleção genérica de grupos e opções em quatro seções da referência, mas não conhece regras de negócio nem persiste seleção. A cada interação, o painel informa o próximo estado de uma opção; confirmação e dismiss são ações distintas. A lista de opções deve ser renderizada na ordem recebida, o corpo deve rolar e o rodapé deve continuar acessível. A implementação deve aproveitar os controles nativos do sistema visual para chips, cartões, interruptores, semântica modal e botão.

O componente deve aceitar tanto a largura da referência quanto janelas menores e fontes ampliadas. A altura da cápsula visual não pode reduzir o alvo de toque. A seleção deve ser controlada pelo consumidor, portanto reabrir o painel com novos dados deve refletir exatamente os identificadores recebidos.

- Design

O painel usa a linguagem dark-first do Sextou: superfície carvão sobre fundo escurecido, texto marfim, labels taupe em caixa alta, contornos discretos e ação laranja dominante. A referência mostra opções inicialmente não selecionadas; a variação selecionada deve preservar a mesma geometria e usar primária, borda de destaque e sinal semântico. A alça, a borda superior, as divisórias e os espaçamentos criam uma hierarquia compacta sem reproduzir a moldura de celular nem elementos de sistema do frame original.

- Product

O painel permite combinar preferências temporárias de tipos de local, faixa de preço, filtros booleanos e categorias. “Aplicar filtros” é a única ação que confirma o rascunho; qualquer outra forma de fechamento abandona alterações não confirmadas. Opções com o mesmo texto em grupos diferentes são independentes. O componente deve ser reutilizável para outros catálogos porque seus identificadores e rótulos são dados, não regras de estabelecimentos.

# Animation

Não se aplica como especificação visual própria. O nó fornece um estado estático; a abertura, o fechamento e o gesto devem usar a transição padrão do modal inferior da plataforma, sem animações internas adicionais em chips, cartões ou interruptores.

## Build

- Isolar somente a superfície `FilterBottomSheet` do nó `209:619`; moldura do telefone, relógio, notch, feed, navegação inferior e demais elementos do frame `209:29` não fazem parte do componente.
- Preservar quatro grupos e a ordem das opções: Tipo de local (Boteco raiz, Espetinho / Podrão, Adega, Karaokê, Trailer de comida); Faixa de preço ($ / Baratinho, $$ / Médio, $$$ / Caprichado); Outros filtros (Aberto agora, Espaço kids, Música ao vivo); Categorias (Karaokê, Espaço Kids, Podrões, Adegas, Ao vivo, 24h).
- Usar os tokens semânticos existentes do Sextou para superfície, texto, primária, borda, raio, espaçamento, tipografia e botão. A referência usa SF Pro, Barlow e dimensões específicas; o projeto aprovou usar a tipografia já disponível no tema e o botão Sextou de 48 dp.
- Os vetores de fechar (15 × 15 px, taupe) e confirmar (16 × 16 px, preto) foram exportados pelo Figma MCP a partir do nó `209:619`; caso a plataforma não aceite SVG diretamente, preservar sua geometria ao adaptar para o formato nativo.
- Não incluir contador, botão “Limpar”, mensagem de sucesso, carregamento, filtragem de estabelecimentos ou chamada de rede.
- Validar padrão, múltiplas seleções, estado desabilitado, fonte ampliada e largura estreita; confirmar dismiss por X, voltar, toque externo e gesto.
