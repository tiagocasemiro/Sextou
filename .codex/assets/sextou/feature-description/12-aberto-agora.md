# Subagente — Alerta “Tá Aberto agora?”

## Missão

Escrever a task que informa se o estabelecimento aparenta estar aberto no
momento da consulta e mostra horários relevantes, sem transformar um dado
eventualmente desatualizado em garantia de atendimento.

## Briefing de negócio

Evitar uma viagem perdida é uma decisão de alto valor. O estado atual deve ser
priorizado online e diferenciado de horário regular offline. Horários especiais,
cozinha, entrega e retirada podem divergir do horário principal e precisam de
semântica própria.

## Dados e APIs

- Places SDK for Android (New): `CURRENT_OPENING_HOURS`, `OPENING_HOURS`,
  `SECONDARY_OPENING_HOURS`, `CURRENT_SECONDARY_OPENING_HOURS`, além de
  `UTC_OFFSET`/`TIME_ZONE` quando necessários.
- Busca pode usar `openNow` para reduzir resultados, mas a tela de detalhe deve
  consultar detalhes para o estado e os períodos relevantes.
- Modelos do projeto: `PlaceOpeningHours`, `OpeningPeriod`, `WeekTime`,
  `SpecialDay`, `PlaceDetails`; domínio recomendado:
  `OperatingStatus(isOpen: Boolean?, source, checkedAt)`.
- `null` significa desconhecido/indisponível; `false` significa fechado apenas
  quando explicitamente retornado pelo fornecedor.

## Domínio e persistência

- `GetCurrentOperatingStatusUseCase` consulta online primeiro; em offline pode
  usar `OpeningHoursSnapshotEntity` somente para horário regular e deve marcar a
  resposta como `REGULAR_OFFLINE`, nunca como status atual confiável.
- Persistir `placeId`, períodos normalizados, fuso, `fetchedAt` e `expiresAt`
  conforme a política. Não persistir `openNow` como verdade para uso futuro.
- Considerar virada de dia, horários que atravessam meia-noite, dias especiais
  e múltiplos períodos nos testes.

## Critérios de aceite para a task

- Online mostra aberto/fechado/desconhecido com horário de verificação.
- Offline não afirma “aberto agora”; mostra regularidade disponível e sua
  limitação.
- Horários ausentes, especiais e secundários não causam interpretação negativa.
- Fuso e mudança de data são considerados ao calcular o dia consultado.
- Testes cobrem períodos normais, meia-noite, múltiplos períodos, especial,
  desconhecido, online e offline.

## Fora de escopo

Garantia de cozinha aberta, lotação, confirmação pelo estabelecimento,
notificações e rastreamento contínuo em background.

## Referências

- [Opening hours e current opening hours](https://developers.google.com/maps/documentation/places/android-sdk/place-details?utm_campaign=gmp_git_agentskills_v1)
- [Places SDK data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Place model](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place?utm_campaign=gmp_git_agentskills_v1)

