---
component: Status Badge
stage: plan
created_at: 2026-08-24
next_agent: create-ui-component
source_type: Figma MCP
source_reference: https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=56-766&t=YMTM0MdpGKeWTbgX-4 (node 56:766; status badge examples 56:883, 56:888 and 56:893)
---

# Propriedades

- Nome: status
- Tipo: enumeração
- Descrição: estado semântico estático representado pelo badge. As
  possibilidades observadas são `OPEN` (`Aberto agora`), `CLOSED` (`Fechado`)
  e `UNAVAILABLE` (`Indisponível`). O valor define o rótulo e o conjunto de
  tokens visuais usados pelo badge.

# Styles

Contrato do Estilo:
- fundo do container: color,
- cor do indicador: color,
- cor do rótulo: color,
- tipografia do rótulo: typography,
- raio do container: border-radius,
- padding horizontal do container: spacing,
- padding vertical do container: spacing,
- espaço entre indicador e rótulo: spacing,
- altura do container: dimensão fixa,

Variantes de estilo:

OPEN:
- fundo do container: token semântico de sucesso, com opacidade de 90%,
- cor do indicador: branco com opacidade de aproximadamente 59%,
- cor do rótulo: branco,
- tipografia do rótulo: Bold, 12 px, line-height 18 px,
- raio do container: token de raio full,
- padding horizontal do container: 10 px,
- padding vertical do container: 4 px,
- espaço entre indicador e rótulo: 4 px,
- altura do container: 28 px,

CLOSED:
- fundo do container: token de superfície fechada, com opacidade de 90%,
- cor do indicador: token de indicador fechado,
- cor do rótulo: token de conteúdo fechado,
- tipografia do rótulo: Bold, 12 px, line-height 18 px,
- raio do container: token de raio full,
- padding horizontal do container: 10 px,
- padding vertical do container: 4 px,
- espaço entre indicador e rótulo: 4 px,
- altura do container: 28 px,

UNAVAILABLE:
- fundo do container: token de superfície de imagem com opacidade de 30%,
- cor do indicador: token de texto secundário,
- cor do rótulo: token de texto secundário,
- tipografia do rótulo: Bold, 12 px, line-height 18 px,
- raio do container: token de raio full,
- padding horizontal do container: 10 px,
- padding vertical do container: 4 px,
- espaço entre indicador e rótulo: 4 px,
- altura do container: 28 px,

# Acessibilidade

O badge é informativo e não interativo. O rótulo textual deve permanecer
exposto à árvore semântica para que o leitor de tela comunique o status. O
indicador circular é decorativo e não deve receber uma descrição independente.
Não há ação, foco, estado de seleção ou alvo de toque próprio.

# Layouts

Não se aplica.

# States

Propriedade-de-estado: status
Descrição: o status altera o texto apresentado e os tokens de fundo,
indicador e conteúdo, preservando a mesma anatomia e dimensões.
Possibilidades:
## OPEN:
Exibe `Aberto agora` com fundo de sucesso e indicador branco translúcido.
## CLOSED:
Exibe `Fechado` com fundo e conteúdo de status fechado.
## UNAVAILABLE:
Exibe `Indisponível` com tratamento visual de baixa ênfase e opacidade de
30% no container.

# Constraints

- O container tem altura fixa de 28 px.
- A largura não é fixa; deve ser determinada pelo conteúdo e pelos paddings.
- O container usa 10 px de padding horizontal e 4 px de padding vertical.
- O indicador é circular e mede 6 px por 6 px.
- O espaço horizontal entre indicador e rótulo é 4 px.
- O rótulo permanece em uma linha e não deve provocar quebra de conteúdo.
- O raio do container é full/pill.
- As constraints são iguais para `OPEN`, `CLOSED` e `UNAVAILABLE`.

# Anatomy

Sempre visíveis:
- Container em formato de pill.
- Indicador circular de 6 px.
- Rótulo textual em uma linha.

Configuráveis por parâmetro:
- Indicador: varia de cor conforme `status`.
- Rótulo: varia conforme `status`.
- Container: varia de cor e opacidade conforme `status`.

Slots:

Não se aplica.

# Description

- Developer: componente visual compacto, stateless e não clicável para
  comunicar o status semântico de um estabelecimento ou item. A implementação
  deve manter a largura intrínseca do conteúdo, usar o mesmo contrato
  dimensional para todos os status e resolver os valores visuais por tokens do
  tema. O badge não deve expor callbacks, foco ou semântica de botão.

- Design: pill de baixa altura com um ponto circular à esquerda e rótulo em
  negrito. `OPEN` usa sucesso verde e texto branco; `CLOSED` usa superfície
  cinza escura com indicador e texto cinza; `UNAVAILABLE` usa uma superfície
  escura de baixa opacidade e conteúdo secundário. O padrão visual é o mesmo
  para todos os status.

- Product: use o badge para feedback semântico estático, como “Aberto agora”,
  “Fechado” e “Indisponível”, ou para dar contexto rápido em cards. Badges não
  devem ser usados como filtros ou ações; a própria documentação do componente
  diferencia esse uso dos Chips interativos.

# Animation

Não se aplica.

## Build

- Fonte principal: documentação Figma `Chips & Badges Handoff`, node `56:766`.
- Evidências específicas do componente: nodes `56:883` (`OPEN`), `56:888`
  (`CLOSED`) e `56:893` (`UNAVAILABLE`).
- Os nós específicos do Status Badge mostram padding 10/4 px, gap 4 px,
  indicador 6 px, raio 9999 px e tipografia Bold 12/18 px. A altura de 28 px
  vem da matriz do componente (node `56:883` e equivalentes).
- A seção genérica de spacing da página também mostra um exemplo de pill de
  32 px. Como os nós específicos do Status Badge fornecem medidas diretamente
  para este componente, a especificação deste handoff prioriza 28 px e registra
  32 px apenas como uma referência genérica de outro exemplo.
- Valores visuais devem ser mapeados para tokens existentes do tema Sextou.
  Quando um token semântico não existir, ele deve ser criado no tema antes de
  ser usado pelo componente.
