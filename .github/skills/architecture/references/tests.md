# Testes de arquitetura

## Conteúdo

- [Objetivo](#objetivo)
- [Fontes de verdade](#fontes-de-verdade)
- [Ferramentas e contexto de execução](#ferramentas-e-contexto-de-execução)
- [Extração das regras](#extração-das-regras)
- [Perspectivas obrigatórias](#perspectivas-obrigatórias)
- [Matriz de cobertura](#matriz-de-cobertura)
- [Estrutura dos testes](#estrutura-dos-testes)
- [Estratégias de verificação](#estratégias-de-verificação)
- [Classes reutilizáveis e assets](#classes-reutilizáveis-e-assets)
- [Frameworks](#frameworks)
- [Qualidade dos testes](#qualidade-dos-testes)
- [Pareamento com a skill](#pareamento-com-a-skill)
- [Execução e CI](#execução-e-ci)
- [Checklist](#checklist)

## Objetivo

Criar uma suíte executável que detecte desvios das decisões registradas pela
skill. Cobrir nomenclatura, pacotes, módulos, dependências, padrões,
frameworks, visibilidade, classes reutilizáveis e demais restrições passíveis
de análise estática.

Não redefinir neste arquivo as regras das camadas. Extrair cada regra da sua
referência de origem e fazer o teste apontar para essa origem. Tratar uma
convenção não automatizável por análise estrutural como verificação
comportamental ou revisão explícita, nunca como regra implicitamente ignorada.

## Fontes de verdade

Ler as fontes nesta ordem:

1. [overview.md](overview.md) para camadas, direção
   das dependências e stack.
2. [repository.md](repository.md), [use-case.md](use-case.md),
   [view-model.md](view-model.md) e [view.md](view.md) conforme as camadas
   cobertas.
3. Os arquivos de `assets/` inventariados pelas referências para contratos,
   visibilidade, pacotes, imports e estruturas reutilizáveis.
4. Os arquivos de `scripts/` quando a suíte precisar validar a organização ou
   o código produzido pelos scaffolds.

Considerar a referência da camada como autoridade sobre a regra. Considerar o
asset como modelo executável daquela regra. Se referência, asset e scaffold
divergirem, registrar a inconsistência e interromper a criação do teste dessa
regra até definir uma única fonte coerente. Não escolher silenciosamente uma
das versões.

## Ferramentas e contexto de execução

Executar todos os testes definidos por esta referência como testes unitários na
JVM. Colocar os arquivos em `src/test/kotlin` ou `src/test/java` do módulo
correspondente, ou em um módulo dedicado cujo source set seja `test`.

Não criar testes arquiteturais em `src/androidTest`. Não depender de
instrumentation runner, dispositivo físico, emulador ou tarefa
`connectedAndroidTest`.

Usar obrigatoriamente:

| Ferramenta  | Responsabilidade                                                                                                                         |
| ----------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| JUnit       | Descobrir, organizar, executar e reportar todos os testes da suíte unitária                                                              |
| Konsist     | Inspecionar estrutura Kotlin, nomes, pacotes, imports, declarações, modifiers e dependências observáveis no código-fonte                 |
| Robolectric | Fornecer runtime Android simulado na JVM quando o teste unitário depender de APIs, recursos, ciclo de vida, Compose ou navegação Android |

Usar JUnit como base de toda classe de teste, inclusive testes com Konsist ou
Robolectric. Selecionar uma versão do JUnit compatível com as versões de Konsist
e Robolectric adotadas pelo projeto e não misturar modelos de execução dentro
da mesma suíte.

Usar Konsist para verificações puramente estruturais, sem inicializar
Robolectric. Usar Robolectric somente quando o comportamento fiscalizado
depender do runtime Android. Manter testes de domínio e demais testes Kotlin
puros sem Robolectric.

Declarar as três ferramentas somente nas configurações de teste. Não expor
JUnit, Konsist ou Robolectric como dependência de produção. Quando Robolectric
precisar acessar recursos Android, habilitar os recursos para testes unitários
na configuração do Android Gradle Plugin adotada pelo projeto.

Aplicar esta seleção:

| Cenário                                                               | Ferramentas         |
| --------------------------------------------------------------------- | ------------------- |
| Nomes, pacotes, imports, herança, visibilidade e assinaturas          | JUnit + Konsist     |
| Regras comportamentais Kotlin sem Android                             | JUnit               |
| Android Framework, recursos ou ciclo de vida                          | JUnit + Robolectric |
| Compose, ViewModel ou Navigation que exijam runtime Android           | JUnit + Robolectric |
| Regra estrutural que examine tipos de Compose, Koin, Retrofit ou Room | JUnit + Konsist     |

Mesmo quando Robolectric for necessário, manter o teste no contexto unitário e
executá-lo pelas tarefas Gradle de unit test.

## Extração das regras

Executar a partir da raiz da skill:

```bash
python3 scripts/extract_architecture_rules.py
```

Filtrar pelas camadas da tarefa:

```bash
python3 scripts/extract_architecture_rules.py \
  --scope repository \
  --scope usecase
```

Gerar saída estruturada quando for necessário compor ou auditar uma matriz:

```bash
python3 scripts/extract_architecture_rules.py \
  --format json
```

O script somente lê a skill e envia o inventário para a saída padrão. Usá-lo
para localizar candidatos normativos, suas seções, perspectivas e a estrutura
atual dos assets. Revisar o resultado contra o texto completo das referências;
não tratar a heurística do extrator como substituta da leitura.

Para cada referência selecionada:

1. Ler `Convenções obrigatórias` e o checklist integralmente.
2. Buscar também termos normativos fora dessas seções, como `deve`, `não`,
   `somente`, `nunca`, `usar`, `manter`, `expor` e `terminar`.
3. Inspecionar tabelas de sufixos, retornos, destinos e assets.
4. Inspecionar exemplos válidos e inválidos para descobrir limites que o texto
   possa ter deixado implícitos.
5. Comparar as regras extraídas com o código real da aplicação antes de
   escrever seletores.

Não criar um catálogo manual separado dentro da skill. Manter a rastreabilidade
no código de teste por meio do caminho e do título da seção de origem.

## Perspectivas obrigatórias

Analisar cada camada por todas as perspectivas abaixo. Não presumir que uma
única consulta do framework de testes cobre mais de uma perspectiva.

| Perspectiva            | O que extrair das fontes                                                                                     |
| ---------------------- | ------------------------------------------------------------------------------------------------------------ |
| Nomenclatura           | Sufixos, prefixos, nomes proibidos, nomes de funções e relação entre nome e responsabilidade                 |
| Pacotes e módulos      | Pacote esperado, source set, módulo proprietário, subpacotes e isolamento entre módulos                      |
| Dependências           | Direção permitida, tipos injetados, imports proibidos e vazamento de infraestrutura                          |
| Estruturas e patterns  | Herança, interfaces, contratos aninhados, imutabilidade, visibilidade e formato das classes                  |
| Frameworks             | APIs autorizadas, annotations, tipos-base, DSLs, integrações e fronteiras registradas nas referências        |
| Assincronismo e estado | `suspend`, escopos, dispatchers, cancelamento, exposição de `Flow` e mutabilidade                            |
| DI e navegação         | Tipo de binding, ciclo de vida, registro de componentes, rotas e grafos                                      |
| Classes reutilizáveis  | Presença, pacote, unicidade, exposição e uso dos contratos fornecidos em `assets/`                           |
| Qualidade de código    | Classes órfãs, abstrações duplicadas, dependências concretas, visibilidade excessiva e violações detectáveis |

Usar as perspectivas como lentes de extração, não como novas regras. O valor
esperado de cada teste deve vir da referência ou do asset correspondente.

## Matriz de cobertura

Antes da implementação, montar uma matriz temporária ou no próprio código de
teste com:

| Campo       | Conteúdo                                                                           |
| ----------- | ---------------------------------------------------------------------------------- |
| Origem      | Arquivo e título da seção que define a regra                                       |
| Escopo      | Camada, módulo, pacote ou tipo alcançado                                           |
| Perspectiva | Uma ou mais perspectivas da seção anterior                                         |
| Evidência   | Declaração, import, dependência, annotation, assinatura ou comportamento observado |
| Estratégia  | Estrutural, dependência de módulo, compilação, comportamento ou revisão            |
| Estado      | Automatizada, comportamental, manual justificada ou não aplicável                  |
| Teste       | Classe e nome do teste responsável                                                 |

Dar destino a toda regra obrigatória e a todo item de checklist. Não declarar
cobertura total enquanto houver regra sem estado. Quando uma regra não puder
ser garantida por teste estrutural, documentar o motivo e indicar a verificação
que a cobre.

Não converter recomendações contextuais em proibições globais. Respeitar
condições e exceções escritas na referência.

Para a fronteira de dados locais, cobrir explicitamente:

- grafo Gradle: `local` depende de `domain`; `domain` e `features` não dependem
  de `local`;
- estrutura: imports e annotations Room, DAOs, entidades, databases,
  migrations, `LocalImpl` e DI local aparecem somente em `local`;
- contrato: UseCases dependem de `Repository.Local`, nunca de DAO, entidade ou
  implementação concreta;
- compilação: `app` compõe o módulo local sem receber Room como dependência
  direta;
- comportamento: persistência, reabertura do banco e migrations são testadas
  no source set unitário de `local`.

## Estrutura dos testes

Colocar a suíte em um módulo ou source set de testes que consiga inspecionar
todos os módulos necessários sem criar dependência de produção. Adaptar o
pacote de teste ao pacote-base do projeto e terminar as classes com
`ArchitectureTest`.

Manter toda a estrutura abaixo no source set unitário `test`; não criar uma
estrutura equivalente em `androidTest`.

Agrupar por responsabilidade de fiscalização, por exemplo:

```text
architecture/
├── NamingArchitectureTest
├── PackageArchitectureTest
├── DependencyArchitectureTest
├── RepositoryArchitectureTest
├── UseCaseArchitectureTest
├── ViewModelArchitectureTest
├── ViewArchitectureTest
├── FrameworkBoundaryArchitectureTest
└── ReusableAssetsArchitectureTest
```

Criar uma classe separada somente quando o agrupamento facilitar diagnóstico e
manutenção. Não repetir a mesma asserção em testes por camada e testes por
perspectiva; eleger um teste proprietário e referenciá-lo na matriz.

Executar as consultas de Konsist como testes JUnit. Confirmar as APIs disponíveis
na versão adotada pelo projeto antes de implementar consultas. Construir
escopos a partir do código de produção e excluir explicitamente testes, código
gerado e diretórios de build, salvo quando a regra tiver esses alvos.

Nomear testes pelo comportamento fiscalizado e incluir na mensagem de falha:

- elemento infrator;
- expectativa;
- referência e seção de origem;
- orientação mínima para localizar a correção.

Evitar nomes ligados a uma feature de exemplo. Fazer consultas descobrirem
todas as classes alcançadas pela regra, inclusive as adicionadas no futuro.

## Estratégias de verificação

Escolher a estratégia compatível com a natureza da regra:

- Usar inspeção estrutural para nomes, pacotes, imports, modifiers, herança,
  annotations, assinaturas e visibilidade.
- Usar verificação do grafo Gradle para dependências entre módulos. Não inferir
  todo o grafo apenas pelos imports Kotlin.
- Usar compilação para confirmar compatibilidade de tipos, APIs de frameworks e
  código produzido pelos scaffolds.
- Usar testes unitários para regras comportamentais, tratamento de resultados,
  estado, cancelamento, DI e classes reutilizáveis.
- Usar JUnit com Robolectric para testes de Compose, UI ou navegação que
  dependam do runtime Android, mantendo-os no source set unitário.
- Usar revisão justificada somente quando semântica de negócio, intenção ou
  contexto não puderem ser determinados com segurança.

Não fazer um teste passar por substring ampla quando a regra exigir identidade
de tipo. Resolver símbolos e imports sempre que a ferramenta permitir. Evitar
listas de exceção globais; limitar qualquer exceção a um alvo exato, explicar o
motivo e definir quando ela deve ser removida.

## Classes reutilizáveis e assets

Ler os inventários de assets nas referências de Repository e UseCase antes de
criar verificações. Derivar dos arquivos Kotlin atuais:

- pacote e módulo de destino;
- declarações públicas e internas;
- interfaces e hierarquias;
- assinaturas e tipos de retorno;
- dependências de frameworks;
- relações obrigatórias entre as classes.

Verificar no projeto consumidor:

1. existência de uma única implementação canônica por abstração;
2. ausência de cópias concorrentes ou tipos equivalentes com outro nome;
3. pacote, módulo e visibilidade compatíveis com a referência;
4. uso das abstrações reutilizáveis nos pontos determinados pela camada;
5. ausência de tipos de infraestrutura em contratos de domínio;
6. compatibilidade comportamental por testes unitários quando estrutura não
   for suficiente.

Não comparar arquivos por texto integral. O pacote-base, imports, formatação e
adaptações legítimas podem variar. Comparar contratos e comportamento exigidos
pela referência.

Quando um asset mudar, extrair novamente seu inventário, revisar os testes que o
representam e executar também os testes comportamentais da classe reutilizável.

## Frameworks

Extrair a lista de frameworks da visão geral e as restrições de uso das
referências das camadas. Para cada framework aplicável:

1. identificar em quais módulos e camadas ele pode aparecer;
2. identificar tipos, annotations ou DSLs exigidos;
3. identificar camadas das quais seus imports não podem vazar;
4. verificar integração, ciclo de vida e binding quando isso for estrutural;
5. encaminhar comportamento de runtime para o tipo de teste adequado.

Não adicionar uma regra apenas porque o framework oferece uma prática comum.
Fiscalizar somente as decisões registradas na skill. Não fixar no teste APIs de
uma versão diferente da adotada pelo projeto.

## Qualidade dos testes

- Fazer cada falha identificar exatamente os elementos infratores.
- Preferir uma asserção conceitual por teste.
- Evitar amostras fixas de classes; descobrir todo o escopo.
- Criar casos de controle que comprovem que a consulta falha diante de uma
  violação conhecida.
- Evitar testes que passem quando o escopo estiver vazio; exigir ao menos um
  elemento quando a arquitetura determinar sua existência.
- Separar ausência de elementos, seleção incorreta e violação da regra em
  diagnósticos diferentes.
- Manter testes determinísticos e independentes de ordem, rede, horário ou
  máquina.
- Não inspecionar somente o código gerado pelos scaffolds; incluir todo código
  de produção alcançado.

Para regras críticas, validar a própria consulta com pequenos fixtures válidos
e inválidos ou um projeto de teste isolado. Garantir que o fixture inválido
falhe pelo motivo esperado.

## Pareamento com a skill

Aplicar este protocolo sempre que a arquitetura evoluir:

1. Alterar primeiro a referência proprietária da decisão.
2. Atualizar assets e scaffolds que materializam a decisão.
3. Executar novamente o extrator de regras.
4. Comparar a matriz anterior com o inventário atual.
5. Adicionar, modificar ou remover testes arquiteturais conforme a mudança real
   da regra.
6. Executar testes estruturais, comportamentais e de compilação afetados.

Não alterar somente o teste para acomodar código divergente. Corrigir o código
quando a referência não mudou. Não remover um teste sem localizar a regra de
origem e confirmar que ela foi removida ou substituída.

Ao criar ou modificar código em qualquer camada:

1. ler a referência da camada;
2. localizar os testes arquiteturais que apontam para essa referência;
3. executar esses testes antes e depois da mudança;
4. ampliar a cobertura se a regra aplicável ainda não possuir teste.

Manter o teste autocontido no projeto consumidor. Usar os caminhos da skill
como rastreabilidade de origem, não como dependência de runtime ou requisito
para executar o CI do aplicativo.

## Execução e CI

Integrar a suíte às tarefas de verificação já utilizadas pelo projeto. Executar
os testes de arquitetura:

- em toda mudança de código de produção;
- em mudanças de dependências, módulos ou plugins;
- em mudanças de referências, assets ou scaffolds da arquitetura;
- antes de integrar uma feature.

Executar somente tarefas Gradle de testes unitários, como a tarefa unitária da
variante ou a tarefa agregadora equivalente adotada pelo projeto. Não exigir
tarefas de testes instrumentados para validar esta suíte.

Fazer o pipeline falhar diante de violação. Publicar relatórios de teste com as
mensagens de origem e evitar etapas opcionais que permitam ignorar a suíte.

Após gerar código com os scripts da skill, compilar e executar imediatamente os
testes arquiteturais das camadas geradas.

## Checklist

- [ ] A visão geral e todas as referências das camadas cobertas foram lidas.
- [ ] Todos os testes estão no source set unitário `test`.
- [ ] JUnit executa toda a suíte.
- [ ] Konsist é usado para regras estruturais.
- [ ] Robolectric é usado somente quando há dependência do runtime Android.
- [ ] Nenhum teste depende de instrumentation, dispositivo ou emulador.
- [ ] As regras foram extraídas novamente da versão atual da skill.
- [ ] Toda regra obrigatória recebeu uma estratégia e um estado de cobertura.
- [ ] Todas as perspectivas aplicáveis foram avaliadas.
- [ ] Consultas cobrem todo o código de produção e falham para escopo vazio.
- [ ] Assets são verificados por contrato e comportamento, não por texto integral.
- [ ] Frameworks são fiscalizados somente conforme decisões registradas.
- [ ] Room e implementações `LocalImpl` são fiscalizados como propriedade
  exclusiva do módulo `local`.
- [ ] Exceções são exatas, justificadas e rastreáveis.
- [ ] Mensagens de falha apontam para referência e seção de origem.
- [ ] Testes não dependem da skill em runtime.
- [ ] Mudanças de referência, asset e scaffold acionam revisão dos testes.
- [ ] A suíte faz parte do fluxo obrigatório de CI.
