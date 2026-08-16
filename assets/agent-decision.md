# Decisões do agente

## 2026-08-16 — Componente de marca do Figma (node 1:652)

Foi criado `SextouBrand` em `design-system` a partir do node `1:652` do
Figma. O componente é stateless e não clicável: recebe `Painter` para o ícone,
`String` para título e subtítulo, `Modifier` opcional e uma descrição de
acessibilidade opcional. O layout reutiliza os tokens existentes de cor,
espaçamento e forma, com novos tokens apenas para dimensões do ícone e para o
estilo tipográfico do subtítulo que não existiam no tema.

O asset visual do ícone foi exportado do Figma e salvo como
`design-system/src/main/res/drawable/ic_sextou_chopp.png` somente para o
preview; a API do componente permanece dinâmica para os consumidores.

## 2026-08-16 — Aplicação das regras de tokens, estado e previews

O módulo `design-system` foi ajustado para remover dimensões hardcoded dos
componentes. Os valores de botão de perfil, badge, bordas, offset e elevação
foram centralizados em `theme/Dimensions.kt`; as cores específicas do esquema
claro foram centralizadas em `theme/Color.kt`, e o background dos previews usa
o token ARGB compartilhado. A revisão confirmou que os componentes atuais já
são stateless e que cada componente possui preview no próprio arquivo.

`design-system:assembleDebug` passou. `design-system:testDebugUnitTest` não
possui fontes de teste (`NO-SOURCE`). A compilação de `app` foi tentada, mas o
ambiente falhou no `androidJdkImage` ao executar `jlink` do JDK 22.3 contra o
SDK Android 34; não houve erro de Kotlin ou de integração do design-system.

## 2026-08-16 — Regras de tokens, estado e previews do design-system

A skill `.agents/skills/design-system` passou a exigir que componentes nunca
usem valores visuais hardcoded: cores, espaçamentos, dimensões, corner radius e
tipografia devem reutilizar tokens existentes ou receber um novo token
semântico centralizado. Também foi definido que componentes são stateless por
padrão, com exceções mínimas para estados transitórios de inputs, e que todo
componente deve ter um preview privado no próprio arquivo.

## 2026-08-16 — Metadata da skill de arquitetura

Foi adicionado `.agents/skills/architecture/agents/openai.yaml` com nome,
descrição curta e prompt padrão coerentes com a skill `architecture`. A skill
não declara dependências externas e permanece elegível para invocação implícita.

## 2026-08-16 — Skill local para o módulo design-system

Foi criada a skill `.agents/skills/design-system` para concentrar o fluxo e as
regras específicas do módulo visual compartilhado: limites arquiteturais,
tokens semânticos, tema Material 3, componentes Compose, acessibilidade,
previews, dependências e validação. A skill foi mantida sem scripts ou
referências auxiliares porque o conhecimento necessário é pequeno e estável,
enquanto os arquivos do módulo permanecem a fonte de verdade para a
implementação.

## 2026-08-16 — Foto opcional no botão de perfil

* `SextouProfileButton` passou a aceitar `avatarPainter` opcional. Quando
  informado, ele substitui o ícone do Figma por uma foto circular de 18 dp;
  quando ausente, o ícone original permanece como fallback.
* A API usa `Painter` em vez de receber URL ou introduzir Coil no
  `design-system`, mantendo o carregamento de imagens sob responsabilidade do
  módulo consumidor.

## 2026-08-16 — Botão de perfil do design-system

* O node `1:663` do Figma foi implementado como `SextouProfileButton`, com
  tamanho visual de 40 dp, ícone de perfil de 18 dp e indicador opcional de
  14 dp no canto superior direito.
* Os valores visuais foram ligados aos tokens existentes do design-system:
  `SurfaceImage`, `Border`, `TextSecondary`, `Error` e `Background`.
* O SVG exportado do Figma foi convertido para um VectorDrawable Android para
  preservar o traçado original sem introduzir uma biblioteca de ícones.

## 2026-08-16 — Preview co-localizado ao tema

* O preview composto do tema foi movido de `ThemePreview.kt` para `Theme.kt`,
  mantendo a visualização da composição no mesmo arquivo da API `SextouTheme`.

## 2026-08-16 — Runtime do renderer de previews

* O `design-system` passou a declarar `ui-tooling` em `debugImplementation`,
  além de `ui-tooling-preview`, porque a anotação sozinha permite compilar os
  previews, mas não fornece o runtime usado pelo renderer do Android Studio.

## 2026-08-16 — Previews do design-system

* Foram adicionados previews privados para `SextouButton`,
  `SextouOutlinedButton`, `SextouCard` (estático e clicável) e uma composição
  do `SextouTheme`, todos isolados de ViewModel, Koin e navegação.
* Os textos usados exclusivamente nos previews foram colocados em
  `design-system/src/main/res/values/strings.xml`, mantendo a regra de não
  hardcodar strings visíveis em composables.

## 2026-08-16 — Tokens visuais da Home no Figma

* O nó `1:632` da Home foi consultado pelo Figma MCP e os tokens foram
  adaptados ao `design-system`: tema escuro com `#111111` como base, superfícies
  `#1C1C1C` e `#2A2A2A`, texto `#F2EDE4`, texto secundário `#9A9080`, laranja
  `#FE9A00`, amarelo `#FFB900`, verde `#00D492` e vermelho `#FF5722`.
* A escala de layout foi centralizada em espaçamentos de 4, 6, 8, 12, 16, 20,
  24 e 32 dp; os principais raios foram definidos como 10, 14, 16 e 44 dp.
* A tipografia preserva os tamanhos, pesos, alturas de linha e espaçamentos
  observados no Figma. Como o projeto não possui fontes embarcadas e SF Pro é
  proprietária, `FontFamily.SansSerif` foi adotada como fallback Android para os
  estilos equivalentes a SF Pro, Anton, Nunito e Barlow Condensed.

## 2026-08-16 — Módulo compartilhado de design

* Foi criado o módulo Android Library `design-system` para concentrar os
  recursos visuais agnósticos de negócio: tema Compose Material 3, esquemas de
  cores claro/escuro, tipografia, formas e componentes base de botão e card.
* O módulo usa `dynamicColor` somente quando solicitado explicitamente; o
  padrão permanece no esquema visual do Sextou para manter consistência entre
  dispositivos.
* Apenas o `app` foi conectado ao módulo porque é o único módulo atual com
  interface. `domain` e `networking` continuam sem dependência de Compose ou
  Android, preservando a direção das dependências da arquitetura.

## 2026-08-16 — Épicos das funcionalidades do features.md

* Foram criadas 13 Issues correspondentes às funcionalidades de
  `.codex/features.md`, numeradas de #2 a #14, usando o prefixo `Épico -` no
  título porque o repositório não possui tipos nativos de Issue habilitados.
* Cada Issue recebeu somente o briefing da funcionalidade correspondente no
  próprio `.codex/features.md`, sem adicionar conteúdo de outras fontes.
* Todas as Issues foram vinculadas ao Project nº 4, definidas explicitamente
  como `Status: Backlog` e verificadas por leituras independentes.

## 2026-08-15 — Entrada padrão das Issues no board

* A skill `manage-sextou-tasks` passou a definir `Backlog` como ponto de
  entrada obrigatório para toda Issue adicionada ao Project sem coluna
  explicitamente informada.
* O status deve ser definido explicitamente após a vinculação e confirmado por
  leitura independente, sem depender do padrão automático do GitHub Project.

## 2026-08-15 — Semântica Jira para tipos de task

* A skill `manage-sextou-tasks` passou a orientar a escolha de `Épico`,
  `História`, `Tarefa` e `Bug` conforme a semântica padrão do Jira.
* `Épico` representa trabalho amplo composto por itens menores; `História`
  representa objetivo/valor do usuário; `Tarefa` representa ação técnica ou
  administrativa; e `Bug` representa defeito em comportamento existente.
* A hierarquia e o significado são do Jira, mas a implementação do Sextou
  continua usando o prefixo no título, pois o GitHub do projeto não habilita
  tipos nativos de Issue.

## 2026-08-15 — Substituição do tipo Sub-tarefa por Bug

* O conjunto vigente de tipos da skill `manage-sextou-tasks` foi alterado para
  `Épico`, `História`, `Tarefa` e `Bug`.
* `Bug` passa a usar o mesmo formato de prefixo: `Bug - <Título>`.

## 2026-08-15 — Tipos de task no prefixo do título

* A skill `manage-sextou-tasks` passou a exigir os tipos `Épico`, `História`,
  `Tarefa` e `Sub-tarefa` no prefixo do título, usando o formato
  `<Tipo> - <Título>`.
* O tipo será representado no título em vez de depender de tipos nativos do
  GitHub, que não estão habilitados no repositório pessoal do projeto.
* A normalização evita prefixos duplicados, preserva o tipo em edições e exige
  a definição do tipo antes da criação de uma task.

## 2026-08-15 — Bloqueio na criação de épicos das features

* A criação das 13 Issues solicitadas foi interrompida antes de qualquer
  mutação porque o repositório pessoal `tiagocasemiro/Sextou` não oferece tipos
  nativos de Issue: `list_issue_types` retornou `404` e não há campo `Type`
  configurado no repositório.
* O parâmetro `type: "Epic"` só deve ser enviado quando os tipos de Issue
  estiverem habilitados; criar Issues comuns com um título ou label de épico
  seria uma representação diferente do pedido e depende de confirmação.

## 2026-08-15 — Instruções de uso das skills locais

* O `AGENTS.md` passou a documentar a seleção, leitura, composição e uso das
  skills em `.agents/skills/`, incluindo as skills locais `architecture` e
  `manage-sextou-tasks`.
* A orientação exige leitura integral do `SKILL.md`, uso de referências e
  scripts conforme a necessidade, preservação do escopo solicitado e registro
  de decisões materiais em `assets/agent-decision.md`.

## 2026-08-15 — Atualização do fluxo do board na skill de tasks

* A skill `.agents/skills/manage-sextou-tasks` e sua referência operacional foram atualizadas para refletir as seis colunas atuais do Project: `Backlog`, `Read to work`, `In Progress`, `Validation`, `Wait publish` e `Done`.
* As descrições configuradas em cada coluna foram preservadas como fonte do significado operacional e convertidas em critérios de transição do fluxo.
* `Done` continua tratado separadamente porque a automação confirmada do Project fecha a Issue como `completed`; as demais colunas não devem ser presumidas como reabertura automática.

## 2026-08-15 — Adiamento de Bombando Agora

* A funcionalidade “Bombando Agora” foi removida de `.codex/features.md` e movida integralmente para `.codex/future-features.md`.
* O adiamento foi decidido porque a lista oficial de campos da Places API (New) e do Places SDK for Android não oferece lotação ao vivo, quantidade de pessoas, movimento atual ou horários de pico.
* A especificação futura foi preservada porque a ideia continua viável com uma fonte própria do Sextou baseada em check-ins e sinais consentidos, mas essa infraestrutura está fora do escopo atual.
* Scraping da interface do Google Maps permanece descartado por não ser uma API oficial e por introduzir riscos de conformidade e confiabilidade.

## 2026-08-15 — Visualização do cardápio

* A funcionalidade principal foi especificada como um resumo dos atributos de alimentação fornecidos pela Places API, pois essa API não expõe itens e preços de cardápios completos de estabelecimentos arbitrários.
* O acesso ao cardápio completo foi definido como navegação para o site oficial ou para a página do local no Google Maps.
* A Google Business Profile API foi documentada apenas como limitação, porque a leitura de `FoodMenus` exige autorização OAuth da conta proprietária do estabelecimento e não atende ao fluxo geral do Sextou.
* Foram incluídos estados de ausência de dados e uso de máscara de campos para evitar que a especificação pressuponha disponibilidade universal e para reduzir custo e latência.

## 2026-08-15 — Nota do estabelecimento e faixa de preço

* A nota geral foi definida com os campos `RATING` e `USER_RATING_COUNT`, pois ambos são disponibilizados pelo Places SDK for Android.
* O pedido de “nota para o preço” foi documentado como faixa de preço usando `PRICE_LEVEL` e `PRICE_RANGE`, porque a Places API não oferece uma nota de custo-benefício.
* A faixa de preço não foi dividida entre comida e bebida, pois os campos do Google descrevem o estabelecimento como um todo.
* Foram especificados estados de ausência de dados e cache temporário para evitar valores inventados ou apresentados como atuais quando a API não os retornar.

## 2026-08-15 — Bombando Agora

* A feature foi adicionada em `.codex/features.md`, conforme solicitado, sem alterar `.codex/funcionalidades.md`.
* A Places API foi mantida apenas como fonte de identificação, localização e funcionamento do estabelecimento, porque seus campos públicos para Android não expõem movimento ou lotação atual.
* Scraping da interface do Google Maps foi descartado por não constituir uma integração oficial ou um contrato de API confiável.
* O movimento foi definido a partir de check-ins e sinais consentidos dos próprios usuários do Sextou, agregados por um serviço remoto. Essa dependência é necessária porque o banco local não agrega eventos de diferentes usuários.
* Foram adotadas faixas qualitativas, mínimo de cinco participantes, identificadores rotativos e ausência de rastreamento em segundo plano para reduzir riscos de privacidade.
* O padrão inicial para “Bombando agora” foi definido como dez sinais únicos em 30 minutos e movimento 50% acima da média do local para o mesmo período. Os limites foram tornados configuráveis por dependerem de validação futura com dados reais.
* A proposta segue a skill local `architecture`: Compose e ViewModel na apresentação, UseCase e contratos no domínio, Retrofit no módulo `networking` e cache Room no módulo `local`.

## 2026-08-15 — Briefings de tasks e subagentes das features

* Foram criados 13 prompts independentes em `.codex/subagents/`, um para cada
  feature atualmente descrita em `.codex/features.md`, com contrato de saída
  para Issue, critérios de aceite, dependências, fora de escopo e referências.
* A análise reutiliza os modelos já existentes no módulo `domain`, como
  `PlaceSummary`, `PlaceDetails`, `GeoPoint`, `PlaceAmenities`, `PlaceOpeningHours`,
  `PriceRange` e `PlacePhotoReference`, e propõe entidades Room somente para o
  estado próprio do usuário ou cache mínimo aprovado.
* A integração foi alinhada à Places SDK for Android (New), Maps SDK for
  Android e Routes API. A especificação de tags foi corrigida: o contrato
  Android usa `Place.Field.LIVE_MUSIC`/`GOOD_FOR_CHILDREN`; karaokê requer fonte
  própria ou curadoria, pois não há campo estruturado garantido.
* A rota foi dividida entre Intent/Google Maps URLs no MVP e Routes API para
  trajeto desenhado no app. Directions API e Distance Matrix API legadas não
  foram recomendadas.
* O cache de conteúdo Google foi descrito como temporário e condicionado aos
  termos vigentes, sem tratar a janela de 7–15 dias da especificação como uma
  autorização universal de armazenamento.
* Não foi adicionada nova tecnologia ao projeto; portanto, não houve débito de
  skill para registrar em `skill-debts.md`.
