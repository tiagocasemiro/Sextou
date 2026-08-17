---
component: {{ nome do componente }}
stage: plan
created_at: {{ data de criação }}
next_agent: android-design-system-components
source_type: {{ tipo da fonte }}
source_reference: {{ caminho, URL, identificador MCP ou descrição da fonte }}
---

# Propriedades

{{
Cada propriedades do componente devem seguir este formato

- Nome: [nome da propriedade]
- Tipo: [tipo do dado da propriedade]
- Descrição: [descrição da propriedade]

}}

# Styles

{{
Escreva o contrato de tokens que todos os estilos devem implementar:

Contrato do Estilo:
[Contrato de tokens]
- [local de aplicação do token-1]:[tipo do token-1],
- [local de aplicação do token-2]:[tipo do token-2],
- [local de aplicação do token-N]:[tipo do token-N],

Variantes de estilo:
[Nome da variante de stilo-1]:
- [local de aplicação do token-1]: [Tipo do token-1],
- [local de aplicação do token-2]: [Tipo do token-2],
- [local de aplicação do token-N]: [Tipo do token-N],

[Nome da variante de stilo-2]:
- [local de aplicação do token-1]: [Tipo do token-1],
- [local de aplicação do token-2]: [Tipo do token-2],
- [local de aplicação do token-N]: [Tipo do token-N],

}}

# Acessibilidade

[Definições de acessibilidade extraidas]

# Layouts

{{
Cada definição de multiplos layouts do componente devem seguir este formato

Nome: [nome da variação de layout]
Descrição geral: [descrição geral do comportamento da sessão onde será inserio as variações]

Possibilidades:
## [Variação 1]:
[Descrição detalhada da variação 1],
## [Variação 2]:
[Descrição detalhada da variação 2],
## [Variação N]:
[Descrição detalhada da variação N],

}}
# States

{{
Temos 2 formas para preecher este template, para cada estado. Abaixo seguem as 2 possibilides:

1 - Caso o estado do componente seja definido por uma propriedade (dos tipos: texto, booleana, numerica ou caracter) já definida na sessão de propriedades,
deverá ser preenchido da seguinte forma.

Propriedade-de-estado: [propriedade usada para definir o estado]
Descição: [Descrição das mudanças de forma mais genérica]
Possibilidades:
## [Valor 1]:
[Descrição do que muda com esse valor 1],
## [Valor 2]:
[Descrição do que muda com esse valor 2],
## [Valor N]:
[Descrição do que muda com esse valor N],

2 - Caso o estado do componente seja definido de forma livre, deverá ser extraido da fonte de informação o nome para cada variação de estado e as mudanças que cada definição deve trazer, siga o modelo abaixo

Propriedade-de-estado: [propriedade usada para definir o estado]
Descição: [Descrição das mudanças de forma mais genérica]
Possibilidades:
## [Estado-1]:
[Descrição do que muda com esse Estado-1],
## [Estado-2]:
[Descrição do que muda com esse Estado-2],
## [Estado-N]:
[Descrição do que muda com esse Estado-N],

}}

# Constraints

{{
Quando não tiver variação de layout ou estado, não será necessário citar estado ou layout.
Só crie definições para variações de layout e estado quando tiver alguma diferença nas medidas em função das mudanças nos layouts ou estados.
Para cada variação de layout crie uma sub-sessão.
[medidas e espacamentos do componente da variação]

Para cada estado crie uma sub-sessão. Caso je tenha sub-sessões de layouts, crie as sub-sessões de estado dentro de cada sub-sesão de layouts.
[medidas e espacamentos do componente do estado]

Por fim salve todas as contrants, medidas, espacamentos, para cada possibilidade de configuração do componente gerado na sessão/sub-sessão corrente.

Exemplo do resultado final pra cada possibilidade:

* Caso não tenha variações de layout e estado:
  [medidas e espacamentos do componente]

* Caso tenha variações de layout:
  Estado: [variação-1]:
  [medidas e espacamentos do componente da variação-1],
  Estado: [variação-2]:
  [medidas e espacamentos do componente da variação-2],
  Estado: [variação-N]:
  [medidas e espacamentos do componente da variação-N],

* Caso tenha variações de estado:
  Estado: [estado-1]:
  [medidas e espacamentos do componente no estado-1],
  Estado: [estado-2]:
  [medidas e espacamentos do componente no estado-2],
  Estado: [estado-N]:
  [medidas e espacamentos do componente no estado-N],

* Caso tenha variações de layout e estado:
  Variação: [variação-1],
  Propriedade-de-estado: [nome da propriedade desse estado],
  Estado: [estado-1],
  [medidas e espacamentos do componente da variação-1 + estado-1],
  Estado: [estado-2],
  [medidas e espacamentos do componente da variação-1 + estado-2],
  Estado: [estado-N],
  [medidas e espacamentos do componente da variação-1 + estado-N],

  Variação: [variação-2],
  Propriedade-de-estado: [nome da propriedade desse estado],
  Estado: [estado-1],
  [medidas e espacamentos do componente da variação-2 + estado-1],
  Estado: [estado-2],
  [medidas e espacamentos do componente da variação-2 + estado-2],
  Estado: [estado-N],
  [medidas e espacamentos do componente da variação-2 + estado-N],

  Variação: [variação-N],
  Propriedade-de-estado: [nome da propriedade desse estado],
  Estado: [estado-1],
  [medidas e espacamentos do componente da  variação-N + estado-1],
  Estado: [estado-2],
  [medidas e espacamentos do componente da variação-N + estado-2],
  Estado: [estado-N],
  [medidas e espacamentos do componente da variação-N + estado-N],

}}

# Anatomy

{{
Sempre visiveis:
[Sub-componente-1],
[Sub-componente-2],
[Sub-componente-N],

Configuráveis por parâmetro:
[Sub-componente-1]:
[Breve descrição do funcionamento da configuração do Sub-componente-1],

[Sub-componente-2],
[Breve descrição do funcionamento da configuração do Sub-componente-2],

[Sub-componente-N],
[Breve descrição do funcionamento da configuração do Sub-componente-N],

Slots:
[Breve descrição para o Area-disponivel-1]:
((Area-disponivel-1)),

[Breve descrição para o Area-disponivel-2]:
((Area-disponivel-2)),

[Breve descrição para o Area-disponivel-N]:
((Area-disponivel-N)),

}}

# Description

[Descreva multiplas vezes e com muitos detalhes o componente. Crie uma descrição para cada persona abaixo]

- Developer
- Design
- Product

# Animation
{{
Para cada animação siga os passos abaixo

[Breve descrição da animação]

[Descrição completa do funcionamento e gatilho da animação. Relacione com outras animações se necessário]
Para cada animação preencha ao menos os passos abaixo. Acrescente mais informações se necessário

- Possui a mesma animação reversa
- Tempo total
    - tempo em cada etapa
- Interpolação usada
    - valores da interpolação (opcional)
- direção da rotação (opcional)
- direção do deslocamento (opcional)
- Efeito (opcional)
- Quantidade de vezes (opcional)

}}

## Build

[Adicione aqui todas as informações coletadas]
