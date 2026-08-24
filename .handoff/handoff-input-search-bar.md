---
component: Input / Search Bar
stage: plan
created_at: 2026-08-24
next_agent: create-ui-component
source_type: Figma MCP
source_reference: https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=56-502&t=YMTM0MdpGKeWTbgX-4 (node 56:502)
---

# Propriedades

- Nome: value
- Tipo: texto
- Descrição: conteúdo atual digitado no campo. O texto é editável pelo usuário quando o componente não está desabilitado.

- Nome: placeholder
- Tipo: texto
- Descrição: instrução curta exibida quando `value` está vazio. Deve indicar com clareza o tipo de conteúdo esperado, por exemplo “Buscar espetinho, trailers...”.

- Nome: label
- Tipo: texto
- Descrição: identificação opcional exibida acima do campo. Quando presente, permanece visível mesmo depois que o usuário começa a digitar.

- Nome: supportingText
- Tipo: texto
- Descrição: texto auxiliar ou mensagem de validação exibido abaixo do campo. No estado de erro, recebe o conteúdo da mensagem de validação.

- Nome: leadingIcon
- Tipo: ícone
- Descrição: ícone opcional exibido antes do conteúdo. No layout Search Bar, representa a busca e ocupa 18 px por 18 px.

- Nome: actionIcon
- Tipo: ícone
- Descrição: ícone opcional de ação exibido no extremo direito. No layout Search Bar, representa o filtro dentro de um botão visual de 28 px por 28 px.

# Styles

Contrato do Estilo:
- fundo do container: color,
- cor da borda: color,
- espessura da borda: número inteiro em pixels,
- cor do conteúdo digitado: color,
- cor do placeholder: color,
- cor do label: color,
- cor do supporting text: color,
- cor do cursor: color,
- cor do ícone de início: color,
- fundo do botão de ação: color,
- opacidade do botão de ação: número decimal,
- raio do container: border-radius,
- raio do botão de ação: border-radius,
- padding horizontal do container: spacing,
- gap entre os elementos: spacing.

Variantes de estilo:

Text Input:
- fundo do container: `surface-variant` (`SextouColors.SurfaceElevated`),
- cor da borda: `border` no estado padrão, `primary-main` no foco, `secondary-main` no erro e `border` no desabilitado,
- espessura da borda: 1 px no padrão/erro/desabilitado e 2 px no foco,
- cor do conteúdo digitado: `text-primary`,
- cor do placeholder: `neutral-muted`,
- cor do label: `neutral-muted`,
- cor do supporting text: `neutral-muted` ou `secondary-main` no erro,
- cor do cursor: `primary-main`,
- cor do ícone de início: `text-secondary`,
- fundo do botão de ação: `primary-main` quando configurado,
- opacidade do botão de ação: 1 no padrão/foco/erro e 0,4 no desabilitado,
- raio do container: `radius-input` (14 px),
- raio do botão de ação: `radius-control` (10 px),
- padding horizontal do container: `spacing-m` (16 px),
- gap entre os elementos: `spacing-xs` (12 px quando houver mais de um elemento).

Search Bar:
- fundo do container: `surface-variant` (`SextouColors.SurfaceElevated`) no padrão/erro e `background` (`SextouColors.Background`) no foco/desabilitado,
- cor da borda: `border` no estado padrão, `primary-main` no foco, `secondary-main` no erro e `border` no desabilitado,
- espessura da borda: 1 px no padrão/erro/desabilitado e 2 px no foco,
- cor do conteúdo digitado: `text-primary`,
- cor do placeholder: `neutral-muted`,
- cor do label: `neutral-muted`,
- cor do supporting text: `neutral-muted` ou `secondary-main` no erro,
- cor do cursor: `primary-main`,
- cor do ícone de início: `text-primary` no foco e `text-secondary` nos demais estados habilitados,
- fundo do botão de ação: `primary-main` nos estados habilitados e `surface-variant` no desabilitado,
- opacidade do botão de ação: 1 no padrão/foco/erro e 0,4 no erro sem resultados/desabilitado quando indicado pela fonte,
- raio do container: `radius-search` (16 px),
- raio do botão de ação: `radius-control` (10 px),
- padding horizontal do container: `spacing-m` (16 px),
- gap entre os elementos: `spacing-m` (12 px).

# Acessibilidade

- O campo deve expor semântica de campo de texto editável e aceitar foco por teclado.
- Quando `label` estiver presente, ele deve ser associado semanticamente ao campo; o placeholder não substitui o label para leitores de tela.
- `supportingText` de erro deve ser anunciado como mensagem de erro associada ao campo, sem depender apenas da cor laranja-vermelha.
- `leadingIcon` é decorativo quando apenas reforça o tipo do campo e, nesse caso, não deve gerar uma descrição duplicada.
- `actionIcon` deve expor uma descrição de conteúdo específica da ação, como “Abrir filtros”, e semântica de botão.
- O botão de ação tem visual de 28 px por 28 px, mas deve oferecer alvo de toque de pelo menos 48 px por 48 px.
- O estado focado deve apresentar uma borda laranja forte (`primary-main`) claramente perceptível; o foco não pode ser comunicado somente por mudança de fundo.
- O estado desabilitado não pode aceitar edição nem acionar a ação final.
- O componente deve preservar contraste suficiente entre texto, placeholder, bordas de estado e o fundo em tema claro e escuro.

# Layouts

Nome: Text Input
Descrição geral: campo de texto para entrada livre, com label e supporting text opcionais. Não exige ícone de início nem botão de ação.

Possibilidades:
## Campo simples:
Contém somente o valor/placeholder dentro do container.
## Campo com label:
Exibe o label acima do container com separação de 8 px.
## Campo com supporting text:
Exibe texto auxiliar ou de validação abaixo do container com separação de 8 px; o texto de erro substitui o auxiliar quando o campo está inválido.

Nome: Search Bar
Descrição geral: campo de busca com ícone de início, conteúdo/placeholder e ação final configurável.

Possibilidades:
## Busca com ação:
Exibe ícone de busca de 18 px por 18 px, conteúdo flexível e botão visual final de 28 px por 28 px dentro de uma área de toque maior.
## Busca sem resultado:
Mantém o mesmo layout e exibe a mensagem de resultado no supporting text ou no conteúdo fornecido pelo consumidor; a ação final pode assumir a aparência desabilitada observada na matriz.

# States

Propriedade-de-estado: estado de interação e validação (`enabled`, foco e mensagem de erro)
Descrição: o estado altera cor, fundo, borda, opacidade, cursor, mensagem de apoio e possibilidade de interação, mantendo a estrutura do layout.

Possibilidades:
## Default:
Campo habilitado sem foco. Usa fundo `surface-variant`, borda de 1 px, placeholder `neutral-muted` e ação habilitada quando configurada.
## Focused / Active:
Campo habilitado e focado. Usa fundo `background`, borda `primary-main` de 2 px e cursor visível em `primary-main`; quando já houver valor, o texto usa `text-primary`. A documentação recomenda uma mudança de fundo mais profunda para destacar a interação por teclado.
## Error / Invalid:
Campo com entrada inválida. Usa borda `secondary-main` de 1 px e exibe a mensagem de validação abaixo do campo em `secondary-main`; a mensagem deve aparecer imediatamente após a validação.
## Disabled:
Campo não editável e ação não acionável. A composição usa aparência atenuada, com opacidade observada de aproximadamente 0,4, fundo mais profundo e conteúdo `neutral-muted`.

# Constraints

As medidas abaixo são as dimensões observadas e as regras de composição do
node 56:502. A tipografia é resolvida pelos tokens do tema e não é repetida
nesta seção.

## Text Input

- Altura base: 48 px.
- Padding horizontal interno: 16 px.
- Label opcional: 4 px de recuo à esquerda e 8 px de separação vertical antes do campo.
- Supporting text opcional: 4 px de recuo à esquerda e 8 px de separação após o campo.
- Raio do container: 14 px.
- Borda: 1 px no padrão/erro/desabilitado e 2 px no foco.
- Gap interno entre elementos, quando houver ícone ou ação: 12 px.

## Search Bar

- Altura base documentada: 56 px; a matriz visual mostra 54 px em alguns estados sem borda de foco e 56 px no foco. A implementação deve preservar a altura externa fixa do contrato de 56 px.
- Padding horizontal interno: 16 px.
- Ícone de início: 18 px por 18 px.
- Gap entre ícone, conteúdo e ação: 12 px.
- Botão de ação visual: 28 px por 28 px.
- Área de toque da ação: mínimo de 48 px por 48 px.
- Raio do container: 16 px.
- Raio do botão de ação: 10 px.
- Borda: 1 px no padrão/erro/desabilitado e 2 px no foco.

## Combinações de estado

- Default: manter as dimensões do layout escolhido.
- Focused / Active: manter a altura externa; a borda de 2 px deve ser desenhada dentro do limite do componente para não alterar o layout ao redor.
- Error / Invalid: adicionar supporting text abaixo sem alterar a altura do container principal.
- Disabled: manter dimensões e alvo de toque, removendo somente a interação.

# Anatomy

Sempre visíveis:
- container principal com fundo, borda e raio;
- área de edição do texto;
- cursor quando o campo está focado;
- placeholder quando `value` está vazio.

Configuráveis por parâmetro:
- Label: texto opcional acima do campo.
- Supporting text: texto auxiliar ou mensagem de validação abaixo do campo.
- Leading icon: ícone opcional; obrigatório visualmente no layout Search Bar.
- Action button: ação opcional; obrigatória visualmente no layout Search Bar, com ícone configurável.

Slots:
Não se aplica. Os elementos variáveis são propriedades de conteúdo e ícones,
não áreas substituíveis por um componente externo.

# Description

- Developer

O Input / Search Bar é um controle stateless de entrada de texto. O consumidor
mantém `value`, reage a alterações por callback e fornece o conteúdo de
placeholder, label, supporting text e ícones conforme o layout. A implementação
deve manter a edição e o foco nativos do sistema, aplicar os tokens do tema e
preservar o alvo de toque da ação final mesmo quando seu desenho visual mede
28 px. O componente não conhece regras de negócio, filtragem ou validação; ele
apenas apresenta o estado recebido.

- Design

O componente usa uma superfície escura elevada com borda sutil e linguagem
visual arredondada. A hierarquia é: label discreto, campo de leitura imediata,
placeholder muted e feedback auxiliar abaixo. O foco é deliberadamente forte
em laranja, o erro usa laranja-vermelho e o desabilitado reduz a presença
visual sem remover a estrutura. Text Input e Search Bar compartilham a base
visual, mas Search Bar acrescenta busca à esquerda e ação de filtro à direita.

- Product

O controle deve orientar rapidamente o usuário sobre o que pode ser digitado,
ser confortável para buscas recorrentes e comunicar validação sem depender
exclusivamente de cor. Placeholders precisam ser descritivos. A ação final da
Search Bar deve ser fácil de alcançar no celular e possuir nome acessível. A
mesma base permite entrada genérica e busca contextual sem duplicar padrões de
interação.

# Animation

Não se aplica. A fonte especifica mudanças imediatas de estado para foco,
validação e desabilitação, mas não define transições animadas, duração,
interpolação ou deslocamento.

## Build

- Implementar no módulo `design-system`, sob `com.sextou.designsystem.component`.
- Reutilizar `BasicTextField` ou uma primitiva Material equivalente que permita o controle visual da decoração sem perder foco, teclado, cursor e state hoisting.
- Mapear `surface-variant`, `neutral-muted`, `primary-main`, `secondary-main`, `text-primary`, `text-secondary`, `background`, `spacing-m`, `spacing-xs` e os raios para tokens existentes ou novos tokens centrais do tema.
- Não usar valores de cor, espaçamento, raio, opacidade ou tipografia diretamente no corpo do componente.
- O botão de ação deve reutilizar a semântica de botão, com ripple/foco e descrição fornecida pelo consumidor.
- Usar os ícones existentes de busca e filtro no preview; consumidores devem poder fornecer ícones diferentes sem acoplar o componente a uma feature.
- Manter o componente stateless, com estado principal elevado, e criar previews no mesmo arquivo cobrindo os dois layouts e os quatro estados.
- As medidas de 48/56 px e os raios 14/16 px são as únicas diferenças de dimensão específicas do contrato; a fonte apresenta pequenas diferenças entre o diagrama de anatomia e a matriz, por isso a implementação segue a matriz e registra essa decisão no projeto.
