---
name: android-design-system-components
description: >
  Skill para criar componentes de uma biblioteca de design system para Android,
  cobrindo desde o planejamento arquitetural até a implementação em Kotlin com Jetpack Compose.
  Garante que cada componente siga as premissas, regras de pacotes e contratos públicos,
  defaults e padrões de qualidade definidos pela biblioteca, produzindo handoff técnico claro
  e código alinhado ao design system.
metadata:
  category: design-system
  capabilities:
    - architecture
    - developer
  files:
    - references/architecture.md
    - references/developer.md
  tags:
    - design-system
    - android
    - jetpack-compose
    - kotlin
    - component-development
    - architecture
---

# Android Design System — Skill

Skill responsável por habilitar a criação de componentes no padrão de uma biblioteca de design system para Android. Combina duas capacidades complementares — **arquitetura** e **desenvolvimento** — para entregar componentes consistentes, testáveis e aderentes às convenções da biblioteca, desde o planejamento até a implementação final em Jetpack Compose.

A skill garante que todo componente:
- Respeite a estrutura de pacotes sob `components` e mantenha a API pública organizada por componente.
- Tenha API pública estável, previsível e desacoplada da implementação.
- Centralize estilos, estados e behaviors em `*Defaults`.
- Reutilize tokens do `MaterialTheme` e componentes existentes da biblioteca.
- Possua previews funcionais cobrindo behaviors e estados principais.

## Capacidades

### 1. architecture — Arquitetura de componente

Arquivo: [`references/architecture.md`](./references/architecture.md)

Planeja a estrutura do componente antes da implementação, analisando premissas, regras e arquitetura da biblioteca. Esta capacidade é responsável por:

- Definir a estrutura de pacotes do componente sob `components`, organizando cada API pública conforme a complexidade do componente.
- Especificar os arquivos mínimos do componente (`ExampleComponent.kt` e `ExampleComponentDefaults.kt`) e suas responsabilidades.
- Modelar **behaviors**, **estados** e **styles** do componente, garantindo que toda combinação `behavior × estado × style` seja suportada.
- Definir o contrato do composable público: kdoc, ordenação de parâmetros, implementação direta e uso de tipos do `*Defaults`.
- Produzir um **handoff MD** detalhado em `./handoff/{component}-handoff.md` com tarefas, evidências e tudo que o Developer precisa para implementar.

### 2. developer — Desenvolvimento de componente

Arquivo: [`references/developer.md`](./references/developer.md)

Implementa o componente em Kotlin + Jetpack Compose a partir do handoff produzido pela capacidade de arquitetura. Esta capacidade é responsável por:

- Transformar o handoff MD em código funcional, executando fielmente a especificação e documentando desvios em `known_deviations` quando necessário.
- Aplicar os imports e convenções padrão do Compose Material (`MaterialTheme`).
- Implementar o composable público com kdoc no formato definido, API estável (obrigatórios primeiro, `modifier` como primeiro opcional) e implementação visual direta.
- Implementar o componente diretamente na API pública, com state hoisting, callbacks semânticos, acessibilidade e uso de constantes/classes do `*Defaults`.
- Centralizar styles, estados, behaviors, constantes e utilitários (`remember` customizados) no `object *Defaults`, sem lógica de negócio nem implementação visual.
- Reutilizar componentes já existentes da biblioteca e resolver tokens citados no handoff via `MaterialTheme`.
- Criar previews funcionais e parametrizados cobrindo behaviors e estados principais, usando `PreviewParameterProvider` quando aplicável.
