# Analytics

Isolar analytics em um módulo próprio e usar `com.example.app.analytics` como
pacote raiz. As features devem conhecer somente o contrato público
`AppAnalytics` e os modelos de evento e identificação. Manter managers, trackers
e detalhes de cada provedor como `internal`.

## Uso dos assets

Usar os arquivos de `assets` como templates de implementação, não apenas como
referência conceitual. Copiar o conjunto correspondente para o projeto e
preservar os packages `com.example.app` declarados nos arquivos.

Para implementar Analytics, usar em conjunto todos os arquivos abaixo:

| Asset | Destino e uso obrigatório |
| --- | --- |
| `assets/repository/analytics/AppAnalytics.kt` | Copiar para o módulo de analytics. Expor este contrato para as features. |
| `assets/repository/analytics/AnalyticsManager.kt` | Copiar para o módulo de analytics. Registrar como implementação interna de `AppAnalytics` e distribuir chamadas aos trackers. |
| `assets/repository/analytics/events/AnalyticsEvent.kt` | Copiar para o pacote de eventos. Criar eventos por meio de `AnalyticsEvent` ou `Event`. |
| `assets/repository/analytics/events/AnalyticsIdentification.kt` | Copiar para o pacote de eventos. Representar usuário e propriedades com `AnalyticsIdentification` ou `Identification`. |
| `assets/repository/analytics/trackers/Analytics.kt` | Copiar para o pacote de trackers. Usar como contrato interno de todo provedor. |
| `assets/repository/analytics/trackers/FirebaseAnalyticsTracker.kt` | Copiar para o pacote de trackers. Enviar eventos e identificação ao Firebase em produção. |
| `assets/repository/analytics/trackers/LogcatAnalyticsTracker.kt` | Copiar para o pacote de trackers. Inspecionar eventos localmente em desenvolvimento. |
| `assets/repository/analytics/di/AnalyticsModule.kt` | Copiar para o pacote de DI. Registrar `AppAnalytics` e selecionar o tracker pelo tipo de build. |

Não recriar manualmente essas classes quando o asset correspondente puder ser
copiado. Se o projeto já possuir uma implementação equivalente, comparar o
contrato existente com o asset e adaptar sem manter duas abstrações concorrentes.

O agrupamento físico de Analytics dentro de `assets/repository/` organiza estes
templates. Ao copiar, manter Analytics em seu módulo próprio e no pacote
`com.example.app.analytics`.

## Automação

Executar os scripts a partir da raiz da skill.

Usar `install_assets.py` para copiar os assets, criar os diretórios dos módulos e
substituir `com.example.app` pelo pacote selecionado.

Simular a instalação do conjunto de Analytics:

```bash
python3 scripts/install_assets.py analytics \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --dry-run
```

Após revisar os caminhos, repetir sem `--dry-run`. Usar `all` para instalar
Repository e Analytics juntos. O script recusa sobrescrever arquivos. Usar
`--force` somente após comparar a implementação existente e confirmar
explicitamente a substituição.

## Estrutura

```text
com.example.app.analytics
├── AppAnalytics
├── AnalyticsManager
├── di
│   └── AnalyticsModule
├── events
│   ├── AnalyticsEvent
│   └── AnalyticsIdentification
└── trackers
    ├── Analytics
    ├── FirebaseAnalyticsTracker
    └── LogcatAnalyticsTracker
```

Usar os oito arquivos listados em [Uso dos assets](#uso-dos-assets) e preservar
os packages iniciados por `com.example.app.analytics`.

## Contrato público

Expor somente estas operações para as features:

```kotlin
interface AppAnalytics {
    fun track(event: AnalyticsEvent)
    fun user(identification: AnalyticsIdentification)
}
```

- Usar `track()` para eventos de tela, interação, sucesso ou erro.
- Usar `user()` para definir o identificador e as propriedades associadas ao
  usuário.
- Injetar e simular `AppAnalytics`, sem depender diretamente de Firebase,
  Logcat ou outro provedor.

## Eventos

Representar eventos com `AnalyticsEvent`, contendo um identificador e um payload.
O evento genérico `Event` forma o identificador no padrão
`<category>_<action>_<label>` e produz as propriedades `Category`, `Action`,
`Label` e, quando informado, `Value`:

```kotlin
appAnalytics.track(
    Event(
        category = "button",
        action = "click",
        label = "continue"
    )
)
```

Definir categorias, ações e labels de negócio no projeto consumidor. Não
incorporar aos assets nomes de telas, jornadas, produtos ou domínios específicos
de um aplicativo.

## Identificação

Representar a identificação com um usuário e propriedades adicionais:

```kotlin
appAnalytics.user(
    Identification(
        user = userId,
        properties = mapOf("account_type" to accountType)
    )
)
```

Não enviar senhas, tokens, documentos, dados de pagamento ou outras informações
sensíveis como identificador, propriedade ou payload.

## Manager e tolerância a falhas

Usar `AnalyticsManager` como implementação de `AppAnalytics`. O manager recebe
uma lista de trackers e encaminha cada chamada para todos eles.

Tratar a falha de cada tracker isoladamente para que:

- uma indisponibilidade do provedor não interrompa a ação do usuário;
- os demais trackers continuem recebendo o evento;
- a falha seja registrada localmente;
- `handleExceptionThrown()` possa executar o tratamento específico do tracker
  para falhas de eventos.

Analytics deve permanecer como efeito colateral observável e nunca determinar o
resultado de uma regra de negócio ou operação de Repository.

## Trackers

Implementar cada provedor por meio da interface interna `Analytics`:

```kotlin
internal interface Analytics {
    val name: String
    fun track(event: AnalyticsEvent)
    fun user(identification: AnalyticsIdentification)
    fun handleExceptionThrown(throwable: Throwable, event: AnalyticsEvent)
}
```

Usar:

- `LogcatAnalyticsTracker` em builds de desenvolvimento, permitindo inspecionar
  IDs, payloads e propriedades sem enviar dados ao provedor remoto.
- `FirebaseAnalyticsTracker` em builds de produção, convertendo o payload para
  `Bundle`, enviando eventos e configurando usuário e propriedades.

Novos provedores devem implementar `Analytics` e ser adicionados à lista recebida
por `AnalyticsManager`, sem alterar as features.

## Injeção de dependências

Registrar uma única instância de `AppAnalytics`. Selecionar o tracker pelo tipo de
build no ponto de composição da aplicação:

```kotlin
val modules = analyticsModules(isDebug = BuildConfig.DEBUG)
```

Em debug, o asset registra o tracker de Logcat; em release, registra Firebase.
Adicionar os módulos retornados à inicialização do Koin junto aos demais módulos
da aplicação.

## Dependências

No módulo de analytics:

- aplicar o plugin Android Library;
- adicionar Firebase Analytics pela BOM adotada pelo projeto;
- adicionar Koin para o registro de `AppAnalytics`;
- fazer as features dependerem do módulo de analytics, nunca de uma
  implementação de tracker.

## Testes

Nas features, usar um mock de `AppAnalytics` e verificar eventos relevantes:

```kotlin
val analytics: AppAnalytics = mockk(relaxed = true)

verify {
    analytics.track(any())
}
```

Testar `AnalyticsManager` separadamente para garantir que todos os trackers sejam
acionados e que a exceção de um tracker não impeça a execução dos seguintes.

## Checklist de Analytics

- [ ] O pacote raiz é `com.example.app.analytics`.
- [ ] As features dependem somente de `AppAnalytics` e dos modelos de eventos.
- [ ] Managers e trackers são `internal`.
- [ ] Eventos seguem `<category>_<action>_<label>`.
- [ ] Constantes específicas do produto ficam no projeto consumidor.
- [ ] Identificadores e payloads não contêm dados sensíveis.
- [ ] Falhas de analytics não interrompem o fluxo funcional.
- [ ] O tracker de desenvolvimento não envia eventos remotamente.
- [ ] `AppAnalytics` está registrado como singleton na injeção de dependências.
- [ ] Features usam mocks de `AppAnalytics` nos testes.

## Tecnologia de referência

- Firebase
