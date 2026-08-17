---
name: design-system
description: Criar, modificar, revisar ou remover elementos do módulo Android `design-system` do Sextou, incluindo tokens visuais, tema Jetpack Compose Material 3, componentes reutilizáveis, acessibilidade, previews e dependências. Usar ao trabalhar em `design-system/`, em APIs `com.sextou.designsystem` ou ao aplicar uma especificação visual a componentes compartilhados sem introduzir lógica de negócio.
---

# Módulo de design system do Sextou

## Objetivo

Manter uma fonte única para a linguagem visual compartilhada do Sextou em
Jetpack Compose. Tratar o módulo como UI reutilizável e agnóstica de negócio;
não colocá-lo no fluxo MVVM de uma feature.

Usar a skill local `architecture` para regras arquiteturais gerais e esta skill
para decisões específicas do módulo visual. Em caso de conflito, preservar as
regras mais específicas desta skill sem violar a direção de dependências do
projeto.

## Uso conjunto das skills de componentes

Compor as skills abaixo quando a tarefa envolver a criação ou alteração de um
componente visual do `design-system`:

1. Usar [`handoff-ui-component`](../handoff-ui-component/SKILL.md) primeiro
   quando houver uma fonte visual ou funcional externa, como Figma MCP, XML,
   screenshot, documentação ou código existente. Essa skill extrai o contrato
   visual e salva o handoff em `.handoff/handoff-<nome-do-componente>.md` na
   raiz do projeto.
2. Usar [`create-ui-component`](../create-ui-component/SKILL.md) depois para
   ler integralmente o handoff na raiz, planejar a estrutura e implementar o
   componente conforme o contrato recebido.
3. Aplicar esta skill durante todo o trabalho no módulo para preservar o
   namespace `com.sextou.designsystem`, a organização `component/` e `theme/`,
   os tokens Sextou, o tema, os recursos, a acessibilidade e os previews.

Não criar um novo handoff para alterações exclusivas de tokens, tema,
dependências ou recursos do módulo quando não houver mudança no contrato
visual de um componente. Quando já existir um handoff correspondente, usá-lo
como entrada da `create-ui-component` e atualizar o handoff somente se a
especificação visual também tiver sido alterada.

Em caso de conflito, o handoff é a fonte do contrato visual solicitado,
`create-ui-component` orienta a estrutura da implementação e esta skill é a
fonte de verdade para as convenções específicas do módulo `design-system`.

## Escopo atual do módulo

Antes de editar, confirmar a estrutura real no repositório. A organização
vigente é:

```text
design-system/
├── src/main/java/com/sextou/designsystem/
│   ├── component/   # composables públicos e previews privados
│   └── theme/       # SextouTheme e tokens visuais
└── src/main/res/    # strings de preview e drawables compartilhados
```

O módulo é um Android Library incluído em `settings.gradle.kts`, com namespace
`com.sextou.designsystem`, Compose habilitado, Java/Kotlin 17 e Material 3.
Verificar as versões no `design-system/build.gradle.kts` antes de adicionar ou
atualizar dependências; não inventar versões fora da configuração do projeto.

## Limites arquiteturais

- Permitir dependências de Compose, Material 3 e recursos Android necessários à
  apresentação.
- Não importar `domain`, `networking`, `local`, ViewModel, UseCase, Repository,
  navegação, gateways ou modelos de uma feature.
- Não colocar regras de negócio, chamadas de rede, persistência, DI de dados ou
  estado de tela no módulo.
- Receber dados e callbacks pela API dos composables. Deixar carregamento de
  imagens e decisões de negócio no módulo consumidor; receber um `Painter` ou
  outro contrato visual quando isso mantiver a dependência desacoplada.
- Manter componentes pequenos, composables e reutilizáveis. Se um componente
  conhecer um estabelecimento, uma feature ou uma regra de produto, ele
  provavelmente pertence à feature, não ao design system.

## Fluxo de trabalho

1. Classificar a solicitação como adição, modificação, revisão ou remoção.
2. Ler `design-system/build.gradle.kts`, `settings.gradle.kts` e os arquivos
   equivalentes existentes antes de propor novos tokens, componentes ou
   dependências.
3. Buscar declarações e usos com `rg`, incluindo consumidores no `app` e
   referências a tokens ou componentes que serão alterados.
4. Identificar o contrato público afetado: assinatura do composable, defaults,
   tokens expostos, tema, recursos ou dependências transitivas.
5. Implementar apenas dentro do escopo solicitado, preservando mudanças
   existentes e mantendo a direção das dependências.
6. Adicionar ou atualizar o preview obrigatório no mesmo arquivo de cada
   componente visual, cobrindo estados relevantes e tema claro/escuro quando
   aplicável.
7. Formatar, compilar o módulo e verificar os consumidores quando uma API
   pública mudar.
8. Registrar em `assets/agent-decision.md` toda decisão material, como novo
   token, fallback de fonte, mudança de semântica de cor, alteração de API ou
   inclusão de dependência.

## Tokens e tema

Centralizar valores visuais em `theme/`; nunca usar valores visuais em hardcode
nem espalhar cores, espaçamentos, formas ou estilos tipográficos arbitrários
pelos componentes.

- Usar `SextouColors` para tokens semânticos de cor, `SextouSpacing` para
  espaçamentos, `SextouCornerRadius`/`SextouShapes` para formas e
  `SextouTextStyles`/`SextouTypography` para tipografia.
- Antes de definir qualquer valor, verificar se já existe um token no tema ou
  um valor Material apropriado. Isso vale para cores, `dp`, `sp`, corner
  radius, tipografia, padding, margin, tamanhos, bordas, elevação, alpha e
  dimensões de interação.
- Se não existir um valor adequado, criar um token próprio no local central de
  tokens, com nome semântico e justificativa, e então usá-lo no componente.
  Não criar a exceção diretamente no corpo do composable. Literais visuais são
  permitidos somente na definição central do token.
- Nomear tokens pelo papel visual (`TextSecondary`, `Surface`, `Primary`), não
  apenas pela aparência (`Gray2`, `Orange`). Evitar duplicar um valor existente
  com outro nome.
- Ao alterar uma cor semântica, revisar o uso nos componentes e o mapeamento
  para `darkColorScheme` e `lightColorScheme` em `Theme.kt`.
- Preservar `SextouTheme` como ponto de entrada. Manter o padrão visual do
  Sextou como default; só habilitar cores dinâmicas quando isso for uma opção
  explícita da API.
- Usar `MaterialTheme` para papéis Material e os tokens Sextou para semântica
  visual específica do produto. Não criar um segundo tema paralelo.
- Não introduzir fontes embarcadas ou uma nova biblioteca de ícones sem
  verificar o impacto no APK, licenciamento e a necessidade real. Manter o
  fallback definido pelo projeto quando a fonte de design não estiver
  disponível.

## Componentes Compose

Ao criar ou modificar um componente:

- Preferir uma API declarativa, estável e mínima, com `modifier` logo após os
  parâmetros obrigatórios e defaults coerentes com os tokens.
- Fazer todo componente ser stateless: receber estado e callbacks por
  parâmetro, sem manter estado de tela internamente. Usar `remember`,
  `mutableStateOf` ou equivalentes somente em pouquíssimas exceções locais,
  como pequenos estados transitórios de um input; mesmo nesses casos, manter
  o estado principal elevado para o consumidor sempre que possível.
- Expor `enabled` quando o controle puder ser desabilitado e preservar o
  comportamento semântico do componente Material subjacente.
- Aceitar conteúdo por slot (`RowScope`/`ColumnScope`) quando o componente
  precisar acomodar texto, ícones ou conteúdo variável; não fixar copy de
  negócio dentro do componente.
- Reutilizar `Button`, `OutlinedButton`, `Card` e demais primitivas Material 3
  quando elas já cobrirem o comportamento. Customizar tokens, forma, borda,
  cores e padding sem reimplementar interações que Material já fornece.
- Para componentes clicáveis, usar a primitiva clicável apropriada e declarar
  `Role`/semantics quando a semântica não for inferida. Garantir alvo de toque
  adequado, foco e estado desabilitado.
- Exigir `contentDescription` em ícones ou controles que transmitam informação;
  usar `null` apenas para imagens decorativas ou quando a descrição já estiver
  no container.
- Receber `Modifier` e aplicá-lo no container raiz sem sobrescrever
  silenciosamente os modificadores do consumidor.
- Evitar `dp`, `sp`, `Color(...)` e `TextStyle(...)` literais no corpo quando
  houver token equivalente. Um valor novo deve ser justificado e centralizado.
- Manter strings visíveis em recursos Android. Textos usados apenas por
  previews também devem ficar em `design-system/src/main/res/values`.

## Previews e documentação visual

- Tornar obrigatório um preview privado no próprio arquivo de cada componente.
  Não criar um arquivo separado de previews nem considerar um preview distante
  ou compartilhado suficiente para cumprir essa regra.
- Co-localizar o preview com o componente que ele demonstra, mantendo a
  visualização próxima da API e atualizada junto com ela.
- Envolver cada preview em `SextouTheme` e exercitar estados relevantes:
  padrão, desabilitado, clicável, com conteúdo longo ou com imagem quando
  esses estados fizerem parte da API.
- Não conectar ViewModel, Koin, navegação, rede ou dados reais a previews.
- Usar strings de recurso nos previews e configurar background/contraste que
  torne o resultado legível.
- Não tratar um preview como teste de comportamento. Para lógica pura nova,
  criar testes unitários; para mudança puramente visual, validar a compilação,
  os previews e, quando disponível, a inspeção visual.

## Dependências e API pública

Manter a configuração do módulo pequena. Antes de adicionar uma biblioteca:

1. Verificar se Compose Material 3 ou APIs já presentes resolvem o caso.
2. Confirmar que a biblioteca é própria para UI compartilhada e não arrasta
   infraestrutura ou lógica de negócio.
3. Escolher `implementation` por padrão; expor com `api` somente se os
   consumidores precisarem compilar contra o tipo ou API transitiva.
4. Atualizar os consumidores e a documentação local somente se a mudança
   exigir isso, e registrar a decisão em `assets/agent-decision.md`.

Ao mudar uma API pública, localizar todos os usos antes de editar, preservar
defaults compatíveis quando possível e compilar pelo menos `design-system` e
o módulo consumidor afetado. Não remover um token ou componente sem tratar os
consumidores e sem confirmar que ele ficou órfão.

## Validação

Executar, conforme o impacto:

```bash
./gradlew :design-system:assembleDebug
./gradlew :design-system:testDebugUnitTest
```

Quando a API pública ou a integração mudar, executar também a compilação do
consumidor, por exemplo `./gradlew :app:assembleDebug`. Se não houver testes
unitários no módulo, registrar isso como limitação da verificação e usar
compilação, previews e inspeção dos usos. Corrigir warnings relevantes de
acessibilidade, recursos e API antes de concluir.

## Critérios de conclusão

- O módulo continua agnóstico de negócio e respeita a direção arquitetural.
- Não existem valores visuais hardcoded nos componentes; tokens não estão
  duplicados nem espalhados.
- Tema, cores claras/escuras, tipografia e formas continuam coerentes.
- Componentes são stateless, salvo exceções locais mínimas e justificadas.
- APIs públicas têm `Modifier`, defaults e semântica acessível adequados.
- Strings visíveis estão em recursos e previews não dependem de runtime de
  feature.
- Todo componente alterado tem preview no próprio arquivo, com estados
  importantes cobertos.
- Dependências, consumidores e testes afetados foram verificados.
- Decisões visuais ou arquiteturais materiais foram registradas em
  `assets/agent-decision.md`.
