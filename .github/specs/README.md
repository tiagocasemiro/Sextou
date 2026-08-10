# Estrutura de diretórios

O projeto é organizado em módulos que refletem Clean Architecture e MVVM. O
pacote-base usado nos exemplos é `com.example.app`; ele deve ser substituído
pelo pacote real da aplicação.

```text
.
├── domain/
│   └── src/main/java/com/example/app/domain/
│       ├── Result.kt
│       └── <feature>/
│           ├── repository/
│           │   └── <Feature>Repository.kt
│           └── usecase/
│               └── <Action>UseCase.kt
├── networking/
│   └── src/main/java/com/example/app/
│       ├── networking/
│       │   └── adapter/
│       │       └── <Feature>RemoteImpl.kt
│       └── repository/
│           ├── DomainMapper.kt
│           ├── FetchData.kt
│           └── NetworkResult.kt
├── features/
│   └── src/main/java/com/example/app/
│       ├── navigation/
│       │   ├── AppNavHost.kt
│       │   ├── AppRoutes.kt
│       │   └── <Screen>Route.kt
│       └── features/
│           └── <feature>/
│               └── <screen>/
│                   ├── <Screen>Destination.kt
│                   ├── <Screen>Navigation.kt
│                   ├── <Screen>Screen.kt
│                   ├── <Screen>UiEvent.kt
│                   ├── <Screen>UiState.kt
│                   ├── <Screen>ViewModel.kt
│                   └── components/
│                       └── <Screen>Content.kt
└── analytics/
    └── src/main/java/com/example/app/analytics/
        ├── AppAnalytics.kt
        ├── AnalyticsManager.kt
        ├── di/
        │   └── AnalyticsModule.kt
        ├── events/
        │   ├── AnalyticsEvent.kt
        │   └── AnalyticsIdentification.kt
        └── trackers/
            ├── Analytics.kt
            ├── FirebaseAnalyticsTracker.kt
            └── LogcatAnalyticsTracker.kt
```

## Responsabilidade e dependências

```text
Compose Screen → ViewModel → UseCase → Repository contract → Remote adapter/API or database
```

- `domain` contém modelos de domínio, contratos de `Repository` e `UseCase`.
- `networking` implementa os contratos remotos, gateways, DTOs e conversões de
  respostas de infraestrutura para `Result` de domínio.
- `features` contém a UI Compose, `ViewModel`, estado, eventos, componentes e
  navegação por feature e tela.
- `analytics` é um módulo próprio, com o contrato público, gerenciador,
  trackers, eventos e injeção de dependências.

A UI não acessa `UseCase` ou `Repository` diretamente, e uma `ViewModel` não
acessa `Repository` diretamente. As dependências seguem da UI para o domínio;
as implementações de infraestrutura dependem dos contratos do domínio.
