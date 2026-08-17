Assuma a posição de um designer que está focado em apresentar o design de um componente a um grupo de desenvolvedores de componentes Android, iOS e React Native.
Foque no O QUE e não no COMO. Descreva todos os detalhes de funcionamento, interação, estilização e propriedades do componente.
Tudo o que os desenvolvedores precisam saber deve ser informado.
Use o template [`template.md`](./template.md) para criar a documentação do componente visual. Salve esta documentação na pasta `.handoff` da raiz do projeto com o nome `handoff-<nome-do-componente>.md`.

# Definições iniciais

Antes de começar a extração de dados da fonte de informação, é importante pautar algumas definições que serão vitais para a execução das instruções seguintes.
- Todas as medidas são expressas em pixels.
- Para valores extraídos dos temas, como cores, espaçamentos, curvas, etc., use sempre os tokens como referência.
- Nunca use tokens de espaçamento para definir altura e largura.
- Foque no O QUE e no POR QUE.


## Nomenclatura:
*fonte de informação*: é qualquer arquivo, MCP, screenshot, documentação, código ou outra entrada usada para extrair as informações necessárias para a execução das instruções deste arquivo.
*template*: são as definições de formato para cada seção do resultado final, definidas no arquivo [`template.md`](./template.md).
*nome do componente*: nome estável do componente normalizado em `kebab-case`, sem o prefixo `handoff-`.

## Saída obrigatória

O arquivo final deve ser criado ou atualizado em `.handoff/` na raiz do projeto.
O nome deve seguir exatamente o formato `handoff-<nome-do-componente>.md`.
Não salvar o resultado dentro da pasta da skill, em `handoff/` ou com o sufixo
`-handoff-plan`.

# Properties

## Definições
Para definir as *properties* do template, siga as instruções abaixo.
Apenas os textos, as imagens, os ícones, os valores numéricos e os valores booleanos que configuram o componente visualmente são definidos aqui.
Configurações de variação de estilo, como cores, tipografias, curvas, espessuras e espaçamentos, NÃO devem ser definidas aqui na seção de properties.
<critical>NÃO devem ser consideradas propriedades do componente: estilos, estados e funções de callback.</critical>
Áreas destinadas a outros componentes, como slots, não são properties. Devem ser especificadas em outra seção.
<critical>NÃO crie properties para ACESSIBILIDADE. Especificações de acessibilidade serão definidas na seção de acessibilidade.</critical>
Extraia da fonte de informação os tipos dos dados das propriedades, esses tipos vão variar de acordo com a propriedade. Eles podem ser *texto*, *numéricos inteiros*, *numéricos decimais*, *boleanos*, *ícones*, *imagens* ou *caracter*.
Extraia os tipos dos dados das propriedades para cada propriedade e preencha o capo **Tipo** . Identifique se o tipo é *texto*, *numéricos inteiros*, *numéricos decimais*, *boleanos*, *ícones*, *imagens* ou *caracter*.

Caso não sejam identificados necessidades de uso de propriedades, o preenchimento deverá ignorar o template e apenas indicar que não se aplica, da seguinte forma: **Não se aplica**


## Formato de cada declaração do componente
Nome: {{ nome da propriedade }}
Tipo: {{ tipo da propriedade, podendo ser um *texto*, *número decimal*, *número inteiro* , *boleano*, *caracter*, *enum* ou *uma estrutura de dados* }}
Descrição: {{ descrição da propriedade }}

## Exemplo:
```MD
## Nome: title
## Descrição: título do componente, exibido ao topo do mesmo. Informação mais relevante.

## Nome: is_selected
## Descrição: informação booleana que indica se o componente está selecionado.
```

Com base nas definições e exemplos, use o template para preencher as properties dos componentes.

# Styles

## Definições

Styles, variations e style variations são formas diferentes de se referir a estilos.
Extraia todas as definições de *variant style* da fonte de informação. Cada definição deve ter um conjunto de tokens para estilizar o componente.
Extraia o contrato de tokens dos estilos. Todos os locais e tipos que usam recursos. Exemplo: background: color, content: color, paddingTop: spacing, cornerTopRight: corner.
Os tokens que devem ser extraidos para estilizar os componentes são: cores (color), tipografias (typography), cores financeiras (if-color), gradientes (gradient), curvatura das bordas (border-radius), espessura das bordas (border-width), espacamentos (spacing), elevações (elevation), opacidade (opacity), efeitos de borrões (blur).                                             
Cada variante de estilo deve ter o mesmo conjunto de tokens das demais variantes. Todos os estilos devem implementar o contrato de tokens.
Deixe o contrato de tokens evidente e, abaixo, coloque cada variação com seus tokens preenchendo o contrato.
Apenas estilizações são atribuídas a essas variações de estilo. Nenhuma regra de visibilidade ou conteúdo é atribuída aos styles.
Caso o componente tenha outros componentes internos, o contrato deste subcomponente interno deve ser adicionado ao contrato do componente, e o nome da variante desse subcomponente deve ser adicionada a cada variante do componente.

Todo componente tem pelo menos 1 style

Com base nas definições e exemplos, use o template para preencher os styles dos componentes.

# Acessibility

Extraia todas as informações necessárias para fazer a configuração de acessibilidade.
Caso as configurações de acessibilidade precisem de alguma informação exclusiva e externa, que não possa ser extraída das properties do componente, crie uma propriedade para obter esta informação.

Caso não sejam identificados necessidades de manipulação do leitor de tela, como no caso dos componente de textos puros, o preenchimento deverá ignorar o template e apenas indicar que não se aplica, da seguinte forma: **Não se aplica**

Com base nas definições e exemplos, use o template para preencher as definições de acessibility dos componentes.

# Layout

## Definições
Layouts são slots ou espaços onde diferentes possibilidades de layouts serão inseridas.
Essa variação será definida apenas quando, no componente, um subcomponente for substituído por outro diferente. Essa variação de layout acontece em função dos dados de entrada.
Exemplo: um texto que possa ser substituído por uma imagem dependendo do parâmetro de entrada do componente. O componente permite entrada de texto ou imagem e se configura conforme a entrada.
Caso uma variação de layout seja identificada no handoff, ela deverá ser registrada com o mesmo formato de registro das properties, seguindo o formato adiante.

Identifique na fonte de informação possíveis variações de layouts para o mesmo componente e preencha as informações das variações de layout seguindo o template na seção de layouts.
As instruções de preenchimento estão no template.

Áreas destinadas ao preenchimento com componentes EXTERNOS, como os slots, não são alternativas de layouts. Apenas modificações pré-definidas internamente são alternativas de layouts.

<critical>Manipulação de visibilidade NÂO  caracteriza variação de layout.</critical>
<critical>Parte com preenchimento opcional, como um texto que possa estar vazio, NÂO caracteriza variação de layout.</critical>

Caso não sejam identificados diferentes layouts para um mesmo componente, o preenchimento deverá ignorar o template e apenas indicar que não se aplica, da seguinte forma: **Não se aplica**

Com base nas definições e exemplos, use o template para preencher os layouts dos componentes.

# States

## Definições:
Quando uma modificação visual ou uma modificação comportamental é pré-definida, temos uma variação de estado.
Os estados podem ser definidos por uma propriedade do componente ou podem ser definidos de forma livre, apenas com um nome e uma descrição.
Um componente de seleção pode assumir o estado *selecionado* e *deselecionado*, ou um componente que pode ficar *expandido* ou *colapsado*; logo, identificamos uma variação de estado.
Outras variações com mais de duas opções também são possíveis. Um card que determina o estágio atual de uma ação de múltiplos estágios pode ser um bom exemplo.
Exemplo: em um card de múltiplos estágios, temos os seguintes estados para cada estágio: state_complete, state_current, state_not_started.
É importante pontuar que todos os estados deverão funcionar de forma harmônica com todas as variações de layouts e com todos os estilos.
Caso um estado altere uma cor do componente, na definição de estilo deverá haver uma opção dessa cor alterada para cada estado do componente. Exemplo: caso a cor de um texto de um label mude nos estados *checked* e *unchecked*, no estilo deste componente deverá haver as cores *color_label_checked* e *color_label_unchecked*.

Caso não sejam identificados diferentes estados para um mesmo componente, o preenchimento deverá ignorar o template e apenas indicar que não se aplica, da seguinte forma: **Não se aplica**

Com base nas definições e exemplos, use o template para preencher os states dos componentes.

# Constraints

## Definições
Deve-se extrair todas as medidas, constraints e espaçamentos do componente, em todas as variações, em todos os estados, fazendo todos os cruzamentos possíveis.
Caso seja importante coletar as medidas e constraints de posicionamento dos subcomponentes, elas também serão salvas nesta seção.
<critical>NUNCA coloque medidas da tipografia. As medidas de tipografia JÀ estão disponives no tema do design system</critical>
Verifique quais medidas dos subcomponentes precisam ser informadas. Evite definir medidas que já são fixas em subcomponentes. Exemplo: definir a altura de um *button* que já tem altura fixa.
<critical>NUNCA considere variações de estilo nesta seção.</critical>

Com base nas definições, use o template para preencher as constraints e medidas dos componentes.

# Anatomy

## Definições:
Extraia da fonte de informação como se comportam os itens internos, podendo eles se enquadrar nas seguintes possibilidades:
- Sempre visíveis
- Configuráveis por parâmetro: ícones, textos, visibilidade.
- Slots: são partes que podem ser trocadas por outros componentes internos; ex.: uma área com (label e toggle) que pode ser substituída por uma com (título e descrição).

Extraia todas essas informações vinculando-as com properties quando necessário.

Com base nas definições, use o template para preencher as anatomias do componente.

# Description

## Definições:
Extraia o máximo de detalhes a respeito do componente, com o objetivo de criar uma descrição bem completa.
Faça a descrição de forma livre; use todos os recursos de identação e formatação que precisar.
Acrescente o máximo de informação possível e necessária para o desenvolvimento do componente.
Descreva o componente sob diferentes personas: personas desenvolvedora, design e produto.

Com base nas definições, use o template para preencher as descriptions do componente.

## Animation

Crie uma explicação livre e bem detalhada para o funcionamento das animações.
Inclua todos os recursos técnicos, como a interpolação, as distâncias caso necessário, os diversos tempos em cada etapa da animação, as direções das animações, as rotações horárias ou anti-horárias e qualquer outra informação relevante.
Descreva as animações simultâneas separadamente. Exemplo: se um ícone rotaciona enquanto um conteúdo desaparece, descreva as duas animações separadamente.

Caso não sejam identificado uso de animações, o preenchimento deverá ignorar o template e apenas indicar que não se aplica, da seguinte forma: **Não se aplica**

Com base nas definições, use o template para preencher as especificações de animation do componente.

# Build

Extraia toda informação necessária para organizar a construção do componente de forma completamente livre. Adicione o máximo de informação possível.
A fonte de informação pode conter sintaxe ou semântica de desenvolvimento usando React Native; traduza para uma linguagem neutra o que fizer sentido e ignore as instruções que sejam exclusivas de uma plataforma.
Algumas definições semânticas, como estrutura de classes e parâmetros, também devem ser eliminadas; neste handoff descrevemos apenas o componente sob a ótica de design que está sendo apresentado aos desenvolvedores.

Com base nas definições, use o template para preencher as especificações de build do componente.
