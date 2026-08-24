---
component: Button
stage: plan
created_at: 2026-08-24
next_agent: create-ui-component
source_type: Figma MCP
source_reference: https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=56-294&t=YMTM0MdpGKeWTbgX-4 (node 56:294)
---

# Propriedades

- Nome: label
- Tipo: texto
- Descrição: rótulo principal exibido no centro do botão. Deve permanecer legível em uma linha e descrever a ação.

- Nome: size
- Tipo: enumeração de tamanho
- Descrição: controla a escala dimensional do botão. As possibilidades observadas são `Large`, `Medium` e `Small`.

- Nome: leadingIcon
- Tipo: ícone opcional
- Descrição: ícone opcional exibido antes do rótulo. A anatomia usa 20 px por 20 px; representa principalmente uma ação iniciada pelo botão.

- Nome: trailingIcon
- Tipo: ícone opcional
- Descrição: ícone opcional exibido depois do rótulo. A anatomia usa 20 px por 20 px; representa principalmente navegação ou redirecionamento.

- Nome: leadingIconContentDescription
- Tipo: texto opcional
- Descrição: descrição acessível do ícone inicial quando ele transmitir informação além do rótulo. Deve ser nula quando o ícone for decorativo.

- Nome: trailingIconContentDescription
- Tipo: texto opcional
- Descrição: descrição acessível do ícone final quando ele transmitir informação além do rótulo. Deve ser nula quando o ícone for decorativo.

- Nome: enabled
- Tipo: booleano
- Descrição: indica se o botão pode receber interação. Quando falso, o botão mantém sua estrutura e assume o tratamento visual desabilitado.

# Styles

Contrato do Estilo:
- cor de fundo no estado padrão: color,
- cor de fundo no estado hover/pressed: color,
- cor de fundo no estado desabilitado: color,
- cor do rótulo e dos ícones no estado padrão: color,
- cor do rótulo e dos ícones no estado desabilitado: color,
- cor da borda no estado padrão: color,
- cor da borda no estado hover/pressed: color,
- cor da borda no estado desabilitado: color,
- espessura da borda: número inteiro em pixels,
- raio do container: border-radius dependente do tamanho,
- padding horizontal: spacing dependente do tamanho,
- gap entre ícone e rótulo: spacing,
- tipografia do rótulo: text style dependente do tamanho,
- tamanho dos ícones: dimensão,
- cor do ripple/foco: color,
- escala no hover/pressed: fator numérico,
- duração da transição: duração,
- easing da transição: curva de interpolação.

Variantes de estilo:

Primary:
- estado padrão: fundo `primary-main`, conteúdo `on-primary`;
- estado hover/pressed: fundo `accent-main`, conteúdo `on-primary`;
- estado desabilitado: fundo `surface-elevated` com presença reduzida, conteúdo `neutral-muted`;
- borda: não observada na amostra da variante;
- uso: ação mais importante da tela, normalmente uma única por view.

Secondary:
- estado padrão: fundo `secondary-main`, conteúdo `text-primary`;
- estado hover/pressed: fundo `secondary-hover`, conteúdo `text-primary`;
- estado desabilitado: fundo `surface-elevated` com presença reduzida, conteúdo `neutral-muted`;
- borda: não observada na amostra da variante.

Outline:
- estado padrão: fundo transparente, borda branca com opacidade aproximada de 0,2 e conteúdo `text-primary`;
- estado hover/pressed: fundo branco com opacidade aproximada de 0,05, borda branca com opacidade aproximada de 0,4 e conteúdo `text-primary`;
- estado desabilitado: fundo transparente, borda branca com opacidade aproximada de 0,1 e conteúdo `neutral-muted` com presença reduzida.

Ghost:
- estado padrão: fundo transparente, sem borda visível e conteúdo `text-secondary`;
- estado hover/pressed: fundo branco com opacidade aproximada de 0,05, sem borda visível e conteúdo `text-primary`;
- estado desabilitado: fundo transparente, sem borda visível e conteúdo `neutral-muted` com presença reduzida.

# Acessibilidade

- O container deve expor semântica de botão e ser acionável por toque, teclado e foco do sistema.
- O rótulo visível deve ser a identificação principal do botão para leitores de tela.
- Ícones que apenas reforçam o rótulo são decorativos e não devem gerar uma descrição duplicada.
- Ícones informativos devem aceitar uma descrição específica fornecida pelo consumidor.
- A área visual pode medir 32 px no tamanho Small, mas o alvo de interação Android deve preservar pelo menos 48 dp quando o ambiente exigir alvo mínimo.
- O estado desabilitado não pode executar a ação nem receber interação acionável.
- Primary e Secondary devem manter contraste de texto de pelo menos 4,5:1. Ghost não deve ser usado em caminhos críticos quando o contraste ou a hierarquia não forem suficientes.
- Ícones à esquerda comunicam ações; ícones à direita comunicam navegação ou redirecionamento, conforme a orientação da fonte.

# Layouts

Nome: Label only
Descrição geral: botão composto apenas pelo container e pelo rótulo, dimensionado pelo conteúdo e pelo padding horizontal do tamanho escolhido.

Possibilidades:
## Rótulo sem ícones:
Exibe o rótulo centralizado horizontal e verticalmente.

Nome: Leading icon
Descrição geral: botão com um ícone anterior ao rótulo.

Possibilidades:
## Ícone inicial:
Exibe o ícone, o gap de ícone e o rótulo na mesma linha.

Nome: Trailing icon
Descrição geral: botão com um ícone posterior ao rótulo.

Possibilidades:
## Ícone final:
Exibe o rótulo seguido pelo gap de ícone e pelo ícone.

Nome: Leading and trailing icons
Descrição geral: botão com os dois ícones configurados.

Possibilidades:
## Ícones inicial e final:
Exibe ícone inicial, rótulo e ícone final na mesma linha, preservando o gap entre elementos.

# States

Propriedade-de-estado: interação e disponibilidade (`enabled`, hover e pressed)
Descrição: a interação altera cores, borda, escala e indicação visual sem mudar o tamanho reservado pelo layout; a disponibilidade remove a ação e atenua o conteúdo.

Possibilidades:
## Default:
Usa os tokens de fundo, conteúdo e borda da variante sem escala adicional.

## Hover / Pressed:
Usa os tokens de interação da variante, aplica escala visual de 1,05 e mantém a transição de 200 ms. O estado pressed deve permanecer perceptível em dispositivos sem ponteiro.

## Disabled:
Não executa a ação, não aplica a escala de interação e usa a superfície e o conteúdo desabilitados da variante.

# Constraints

As medidas abaixo são as dimensões observadas no node 56:294. A implementação deve resolver cores, tipografia, espaçamentos e formas a partir dos tokens do tema; os valores em pixels documentam a evidência visual da fonte.

## Todos os layouts

- Ícones: 20 px por 20 px quando presentes.
- Gap entre ícone e rótulo: 8 px (`spacing-xs` da fonte).
- Largura: flexível, determinada pelo conteúdo e pelo padding horizontal; não há largura fixa observada.
- Alvo de interação: mínimo recomendado de 48 px por 48 px quando a plataforma não fornecer esse comportamento automaticamente.

## Large

- Altura visual: 48 px.
- Padding horizontal: 24 px.
- Raio: 16 px.
- Tipografia de referência: 16 px.

## Medium

- Altura visual: 40 px.
- Padding horizontal: 20 px.
- Raio: 12 px.
- Tipografia de referência: 14 px.

## Small

- Altura visual: 32 px.
- Padding horizontal: 16 px.
- Raio: 8 px.
- Tipografia de referência: 12 px.

## Estados

- Default: preserva a altura e o raio do tamanho escolhido.
- Hover / Pressed: a amostra Medium mostra 42 px por causa da escala 1,05; a implementação deve manter o espaço reservado original e aplicar a escala somente ao visual para evitar deslocamento de outros elementos.
- Disabled: preserva as dimensões do tamanho escolhido.

# Anatomy

Sempre visíveis:
- container principal com fundo, raio, padding e semântica de botão;
- rótulo central;
- área de interação e indicação de estado.

Configuráveis por parâmetro:
- Left Icon: ícone opcional de 20 px antes do rótulo;
- Label: texto da ação, sempre presente;
- Right Icon: ícone opcional de 20 px depois do rótulo;
- Size: altura, padding, raio e tipografia da escala escolhida;
- Style: cores e bordas da variante escolhida.

Slots:
Não se aplica. A fonte define rótulo e ícones como propriedades configuráveis, não como áreas de conteúdo arbitrárias.

# Description

- Developer

Button é um controle stateless de ação. O consumidor fornece o rótulo, o callback, o tamanho, a variante e, quando necessário, os ícones. A implementação deve manter a largura flexível, usar o tema como fonte dos tokens visuais, preservar a semântica de botão e separar a área visual do alvo de toque quando o tamanho Small for menor que o alvo recomendado. A mudança de estado não pode alterar o espaço reservado pelo layout.

- Design

O componente tem linguagem arredondada, compacta e de alto contraste sobre superfícies escuras. Primary usa o laranja de marca para a ação principal; Secondary usa o laranja-avermelhado para ações de destaque; Outline e Ghost reduzem a ênfase por meio de borda, superfície e conteúdo. O hover/pressed usa uma cor ou superfície mais evidente e uma escala sutil de 1,05.

- Product

Button comunica hierarquia e intenção de ação. Primary deve ser reservado para a ação mais importante da view, normalmente uma única vez. Ícones devem acrescentar contexto: à esquerda para iniciar ações e à direita para indicar navegação. Ghost não deve substituir uma ação crítica quando sua presença visual não for suficiente.

# Animation

Escala e transição de interação

Quando o botão entra em hover ou pressed, o visual do container pode crescer até 1,05x e adotar as cores da coluna `Hover / Pressed`. Ao sair do estado, a mesma animação é revertida para 1x.

- Possui a mesma animação reversa: sim.
- Tempo total: 200 ms.
    - tempo em cada etapa: uma interpolação contínua de 200 ms entre a escala atual e a escala alvo.
- Interpolação usada: `ease-in-out`.
- Efeito: mudança de escala e de tokens de cor/borda.
- Quantidade de vezes: uma vez por entrada ou saída do estado.

## Build

- Implementar no módulo `design-system`, sob `com.sextou.designsystem.component.button`.
- Reutilizar a primitiva de interação do Compose/Material para semântica, foco, ripple, teclado e estado desabilitado, adaptando a apresentação aos tokens do Sextou.
- Reutilizar os tokens existentes de cor, espaçamento, tipografia, raio, opacidade e elevação antes de criar novos tokens.
- Quando não houver token adequado, criar o token semanticamente nomeado no diretório `theme/` e consumi-lo pelo `SextouButtonDefaults`.
- Não usar cores, espaçamentos, raios, tipografia ou dimensões literais no corpo do componente.
- Criar `SextouButtonDefaults` com estilos `primaryStyle`, `secondaryStyle`, `outlineStyle` e `ghostStyle`, além das métricas dos três tamanhos.
- Criar previews no mesmo arquivo cobrindo as quatro variantes, os três tamanhos, os layouts com ícones e o estado desabilitado.
- A fonte não fornece os arquivos dos ícones do exemplo como contrato de API; o componente deve receber ícones do consumidor e os previews devem reutilizar assets já disponíveis no módulo.
