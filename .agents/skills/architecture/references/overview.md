# Visão geral da arquitetura

Adotar MVVM, Clean Architecture e Jetpack Compose como definições principais:

- MVVM (Model-View-ViewModel)
- Clean Architecture
- Jetpack Compose

## Camadas

### UI Layer

- Compose
- ViewModel
- State/UI State

### Domain Layer

- UseCases
- Regras de negócio

### Data Layer

- Repository
- Remote API, implementada no módulo `networking`
- Banco local e cache persistente, implementados no módulo `local`
- Cache

## Módulos e direção das dependências

Separar cada tecnologia de infraestrutura em um módulo Android Library próprio:

```text
app ───────────────→ features ──→ domain
 │                                  ↑
 ├──→ networking ───────────────────┤
 └──→ local ────────────────────────┘
```

- `domain` contém modelos, UseCases e contratos de Repository; não depende de
  Android, `networking` ou `local`.
- `features` depende de `domain`, nunca de implementações de dados.
- `networking` é o único proprietário de Retrofit, gateways, DTOs e adapters
  remotos.
- `local` é o único proprietário de Room, bancos, DAOs, entidades, migrations,
  adapters locais e DI dessa infraestrutura.
- `app` é o composition root: depende dos módulos de infraestrutura para
  registrar seus módulos de DI, mas não implementa acesso remoto ou local.
- `networking` e `local` podem depender de `domain` para implementar os
  contratos, mas não dependem entre si.

## Fluxo simplificado

```text
UI (Compose Screen)
↓
ViewModel
↓
UseCase
↓
Repository (API / Database)

```

## Stack de referência

- Kotlin
- Jetpack Compose
- Coroutines
- Flow / StateFlow
- Koin
- Retrofit
- Room
- Navigation Compose
- JUnit
- Konsist
- Robolectric
- coil
- Gson
- Paging 3
- Media3
