---
name: architecture
description: Criar e manter aplicativos Android nativos com MVVM, Clean Architecture e Jetpack Compose. Usar ao adicionar, modificar, revisar ou apagar elementos das camadas View, ViewModel, UseCase e Repository, incluindo navegação, estado, coroutines, acesso a dados, analytics, injeção de dependências, testes funcionais, testes de arquitetura e convenções arquiteturais.
---

# Criar e manter aplicativos Android nativos

## Objetivo

Orientar mudanças incrementais e seguras em aplicativos Android nativos
baseados em MVVM, Clean Architecture e Jetpack Compose.

Aplicar as convenções da skill tanto na criação de código quanto na manutenção
de código existente. Antes de agir, classificar a mudança como adição,
modificação ou exclusão e identificar todas as camadas afetadas.

## Como selecionar as referências

Carregar apenas as referências necessárias para a mudança. Ler
[Visão geral da arquitetura](references/overview.md) antes de:

- criar ou apagar uma feature, camada ou módulo;
- mudar o fluxo de dependências entre camadas;
- adotar, substituir ou remover uma tecnologia da stack;
- executar uma alteração que atravesse mais de uma camada;
- resolver conflito entre convenções das referências.

Para uma alteração local, ler a referência da camada e as referências das
camadas consumidoras quando o contrato público, os dados ou o comportamento
observável forem alterados.

## Uso de cada arquivo de referência

### Visão geral da arquitetura

Usar
[references/overview.md](references/overview.md)
para validar limites, responsabilidades, módulos, stack e direção das
dependências.

- Ao adicionar: confirmar em qual camada o novo elemento pertence e quais
  dependências ele pode receber.
- Ao modificar: verificar se a mudança preserva o fluxo arquitetural e a
  separação de responsabilidades.
- Ao apagar: confirmar que o fluxo restante continua coerente e que nenhuma
  responsabilidade obrigatória ficou sem uma camada proprietária.

### Repository

Usar [references/repository.md](references/repository.md) ao adicionar,
modificar ou apagar contratos e implementações de Repository, fontes remotas
ou locais, DTOs, entidades, DAOs, bancos Room, migrations, mappers, conversão
de respostas, tratamento de erros, injeção de dependências ou testes da camada
de dados.

- Ao adicionar: seguir os pacotes, sufixos, contratos e fluxos documentados;
  criar ou reutilizar um módulo `local` próprio quando houver persistência;
  instalar somente os assets exigidos pelo cenário; usar o scaffold da camada
  apenas para arquivos novos; registrar as dependências e criar testes.
- Ao modificar: inspecionar o contrato, a implementação, os mappers, os
  módulos de DI e todos os UseCases consumidores. Não regenerar nem
  sobrescrever arquivos existentes.
- Ao apagar: localizar usos do contrato e da implementação; remover somente
  após tratar UseCases consumidores, registros de DI, DTOs, mappers e testes
  que tenham se tornado exclusivamente órfãos.

Se o tipo de retorno, modelo de domínio ou erro observável mudar, ler também
[UseCase](references/use-case.md),
[ViewModel](references/view-model.md) e [View](references/view.md) até a última
camada afetada.

### Analytics

Usar [references/analytics.md](references/analytics.md) ao
adicionar, modificar ou apagar contratos, eventos, identificações, managers,
trackers, provedores, injeção de dependências ou testes de Analytics.

- Ao adicionar: instalar em conjunto os assets exigidos, preservar o módulo
  próprio e expor às features somente `AppAnalytics` e os modelos de eventos e
  identificação.
- Ao modificar: inspecionar o contrato público, todos os trackers, a seleção por
  tipo de build, a DI e as features consumidoras. Manter falhas isoladas do
  fluxo funcional.
- Ao apagar: localizar os usos nas features e remover somente após tratar
  eventos, bindings, trackers, dependências e testes que tenham se tornado
  exclusivamente órfãos.

### UseCase

Usar [references/use-case.md](references/use-case.md) ao adicionar, modificar
ou apagar regras de negócio, contratos consumidos do Repository, operações
suspensas, `Flow`, `StateFlow`, composição de coroutines, DI ou testes da
camada de caso de uso.

- Ao adicionar: confirmar primeiro o contrato do Repository; aplicar pacote e
  sufixo definidos; usar o scaffold somente para um arquivo novo; implementar
  a regra, registrar DI e criar testes.
- Ao modificar: revisar o Repository de origem e todos os ViewModels
  consumidores; preservar cancelamento, dispatcher, fluxo de erros e contrato
  público, salvo quando a mudança solicitada exigir sua evolução.
- Ao apagar: localizar ViewModels consumidores; remover ou adaptar chamadas,
  DI e testes antes de eliminar o UseCase.

Se assinatura, resultado, estado ou erro observável mudar, ler também
[ViewModel](references/view-model.md) e [View](references/view.md).

### ViewModel

Usar [references/view-model.md](references/view-model.md) ao adicionar,
modificar ou apagar ViewModels, `UiState`, ações, eventos, integração com
UseCases, `StateFlow`, `SharedFlow`, coroutines, DI ou testes da camada de
apresentação.

- Ao adicionar: confirmar os UseCases necessários; aplicar pacote e sufixo
  definidos; usar o scaffold apenas para arquivos novos; expor estado
  imutável, registrar DI e criar testes.
- Ao modificar: revisar os UseCases de origem e todas as Destinations e Screens
  consumidoras; manter trabalho assíncrono no escopo e no dispatcher
  apropriados.
- Ao apagar: remover ou adaptar a View consumidora, seus registros de
  navegação, DI e testes antes de eliminar o ViewModel.

Se `UiState`, ação, evento ou comportamento observável mudar, ler também
[View](references/view.md).

### View

Usar [references/view.md](references/view.md) ao adicionar, modificar ou apagar
Destinations, Screens, componentes de tela, rotas, grafos de Compose
Navigation, coleta de `StateFlow`, efeitos, previews ou testes de UI.

- Ao adicionar: confirmar o contrato do ViewModel; aplicar pacote, sufixos e
  hierarquia de composables definidos; usar o scaffold somente para arquivos
  novos; registrar a rota no `NavHost`.
- Ao modificar: revisar `UiState`, ações e eventos do ViewModel; preservar o
  fluxo unidirecional, o ciclo de vida da coleta e a separação entre Screen e
  componentes.
- Ao apagar: remover a rota e seu registro no `NavHost`; localizar chamadas de
  navegação; apagar apenas componentes, previews, testes e ViewModel que
  ficarem comprovadamente sem uso.

Não fazer a View acessar diretamente UseCases ou Repositories. Se a mudança
exigir novo estado, ação ou evento, retornar à referência de ViewModel.

### Testes de arquitetura

Usar [references/tests.md](references/tests.md) para criar, revisar ou manter
testes que fiscalizem as convenções arquiteturais.

Executar toda a suíte no contexto de testes unitários definido nessa
referência. Não criar testes instrumentados para cumprir suas verificações.

Não usar essa referência isoladamente. Ler sempre:

1. a [visão geral](references/overview.md);
2. a referência de cada camada coberta pelo teste;
3. os assets e scripts indicados por essas referências quando o teste validar
   classes reutilizáveis ou código gerado.

Extrair as regras no momento da tarefa, sem copiá-las para `tests.md`. Usar
`scripts/extract_architecture_rules.py` para produzir o inventário atual por
origem e perspectiva. Manter cada teste rastreável à referência e à seção que
define a regra.

Ao alterar uma referência, asset ou scaffold, revisar os testes arquiteturais
correspondentes. Ao alterar código da aplicação, atualizar um teste somente
quando a regra de origem também mudar; não enfraquecer o teste para aceitar uma
violação.

## Impacto entre camadas

Usar esta ordem para rastrear consumidores:

| Camada alterada | Inspeção mínima obrigatória |
| --- | --- |
| Repository | UseCases, DI e testes; módulos `networking` ou `local` conforme a origem; ViewModels e Views se o resultado observável mudar; testes arquiteturais de Repository e assets |
| Analytics | Features consumidoras, DI, seleção de trackers por build e testes; assets e dependências de provedores |
| UseCase | ViewModels consumidores, DI e testes; Views se estado ou comportamento mudar; testes arquiteturais de domínio |
| ViewModel | Destinations, Screens, DI e testes; testes arquiteturais de apresentação |
| View ou navegação | Rotas, `NavHost`, componentes e testes de UI; ViewModel se o contrato da UI mudar; testes arquiteturais de Compose e navegação |
| Referência, asset ou scaffold | Regras extraídas, testes arquiteturais e código gerado ou reutilizável relacionado |

Não interpretar a tabela como autorização para apagar dependentes. Adaptar ou
remover cada consumidor conforme o escopo solicitado e o resultado da busca de
usos.

## Procedimento por tipo de mudança

### Adicionar

1. Inspecionar a estrutura, os pacotes, as convenções e implementações
   equivalentes existentes.
2. Ler a referência da camada, a visão geral quando aplicável e as referências
   das camadas cujo contrato será afetado.
3. Usar `scripts/install_assets.py` para copiar templates Kotlin exigidos pela
   referência e `scripts/scaffold_architecture.py` para gerar estruturas
   repetitivas.
   Quando a origem for Room ou outra persistência local, criar a implementação
   no módulo `local`; nunca colocar banco, DAO, entidade ou adapter local em
   `app`.
4. Executar qualquer automação primeiro com `--dry-run` e revisar todos os
   destinos. Não usar `--force` para criar sobre arquivos existentes.
5. Completar os `TODOs`, adaptar tipos, ligar DI e navegação e criar testes
   antes de considerar a adição pronta.

### Modificar

1. Ler a referência da camada e inspecionar o código existente antes de editar.
2. Buscar declarações e usos com `rg`; rastrear os consumidores conforme a
   matriz de impacto.
3. Preservar mudanças do usuário e código não relacionado.
4. Não usar scripts de instalação ou scaffold para regenerar arquivos
   existentes.
5. Atualizar contratos, consumidores, DI, navegação e testes dentro do impacto
   real da mudança.

### Apagar

1. Resolver os alvos exatos e buscar referências com `rg` antes da exclusão.
2. Não usar scripts de instalação ou scaffold.
3. Remover integrações associadas, como módulos de DI, rotas, registros no
   `NavHost`, imports, testes e recursos que tenham ficado exclusivamente
   órfãos.
4. Não apagar código compartilhado apenas porque um consumidor foi removido.
5. Verificar que não restaram referências quebradas e informar claramente os
   arquivos materiais excluídos.

## Uso de assets e automações

- Tratar os inventários e as condições de uso descritos em
  [Repository](references/repository.md) e
  [Analytics](references/analytics.md) e
  [UseCase](references/use-case.md) como fonte de verdade para todos os
  arquivos em `assets/`.
- Não copiar assets indiscriminadamente; instalar apenas os exigidos pela
  arquitetura em uso.
- Usar `scripts/install_assets.py` somente para adicionar templates ausentes.
- Usar `scripts/scaffold_architecture.py` somente para adicionar estruturas
  repetitivas de Repository, UseCase, ViewModel ou View.
- Usar `scripts/extract_architecture_rules.py` para extrair das referências e
  assets o inventário que orientará os testes de arquitetura.
- Executar com `--dry-run`, revisar o pacote-base e os caminhos gerados e
  evitar `--force`.
- Após a geração, tratar o resultado como código da aplicação: completar,
  revisar, formatar, compilar e testar.

## Fluxo de execução

1. Classificar a solicitação em adição, modificação e/ou exclusão.
2. Identificar as camadas afetadas e o contrato público que pode mudar.
3. Ler a visão geral quando a mudança for estrutural ou atravessar camadas.
4. Ler a referência de cada camada afetada e das consumidoras indicadas na
   matriz de impacto.
5. Inspecionar o código real, as implementações equivalentes e todos os usos.
6. Extrair novamente as regras quando a tarefa envolver testes de arquitetura.
7. Aplicar a mudança usando automações apenas para adições repetitivas.
8. Atualizar DI, navegação, consumidores e testes afetados.
9. Formatar, compilar e testar os módulos alterados.
10. Confirmar que não existem referências órfãs, violações entre camadas ou
   decisões arquiteturais inventadas.

## Critérios de conclusão

- Pacotes, nomes e sufixos seguem a referência da camada.
- A direção das dependências permanece válida.
- Persistência, Room, DAOs, entidades, migrations, adapters locais e sua DI
  pertencem ao módulo `local`; `app` apenas inclui esse módulo na composição.
- View não acessa UseCase ou Repository diretamente.
- ViewModel não acessa Repository diretamente.
- Toda string estática visível ao usuário está nos recursos Android e é
  resolvida pela View.
- DI e Compose Navigation refletem a estrutura final.
- Assets usados estão previstos nas referências e foram adaptados ao projeto.
- Cada regra arquitetural aplicável possui teste automatizado ou justificativa
  rastreável para outra forma de verificação.
- Testes de arquitetura apontam para a referência de origem e permanecem
  pareados com as camadas, assets e frameworks cobertos.
- Testes afetados foram atualizados e as verificações disponíveis passaram.
- Não restaram imports, rotas, bindings ou chamadas órfãs.

## Evolução da skill

- Manter neste arquivo apenas o roteamento, o fluxo e as regras operacionais.
- Registrar detalhes de implementação em `references/`.
- Adicionar scripts apenas para automações reutilizáveis e seguras.
- Adicionar assets apenas quando forem modelos reutilizáveis da arquitetura.
- Referenciar no fluxo todo recurso que passar a ser obrigatório.
