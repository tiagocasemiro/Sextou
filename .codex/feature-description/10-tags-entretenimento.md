# Subagente — Filtros Rápidos de Entretenimento

## Missão

Escrever a task que permite filtrar a descoberta por sinais de lazer, como
música ao vivo, ambiente para crianças e karaokê, com transparência sobre a
origem e a confiança de cada tag.

## Briefing de negócio

O usuário quer escolher não apenas comida, mas o tipo de noite. O Google pode
fornecer alguns atributos estruturados, porém não garante um campo de karaokê.
O Sextou deve evitar afirmar que uma casa tem uma atração apenas porque uma
palavra apareceu em uma avaliação. Tags geradas por curadoria/cadastro próprio
devem ser separadas das tags derivadas do Google.

## Dados e APIs

- Places SDK for Android (New), detalhes com `LIVE_MUSIC`, `GOOD_FOR_CHILDREN`,
  `OUTDOOR_SEATING`, `GOOD_FOR_GROUPS`, `GOOD_FOR_WATCHING_SPORTS`, `ALLOWS_DOGS`
  e demais atributos necessários ao filtro.
- `REVIEWS` pode ser retornado para exibição de reviews conforme política, mas
  não deve ser varrido e armazenado como dataset compartilhado para fabricar
  tags sem revisão de conformidade.
- O campo correto no SDK Android é `Place.Field.LIVE_MUSIC`/
  `Place.Field.GOOD_FOR_CHILDREN`; `places.amenityOptions` não é contrato para
  este app.
- Domínio: `EntertainmentTag(type, value, source, confidence, observedAt)` e
  `PlaceEntertainmentTags(placeId, tags)`. Tipos iniciais: `LIVE_MUSIC`,
  `CHILD_FRIENDLY`; `KARAOKE` exige fonte própria/curadoria.

## Banco e arquitetura

- `EntertainmentTagEntity(placeId, tagType, value, source, confidence,
  observedAt, expiresAt)` em `local` para tags próprias ou cache aprovado.
- `EntertainmentTagRepository.Local`/`Cache`; `GetEntertainmentTagsUseCase`
  combina dados próprios com atributos Google conforme precedência definida.
- Preferir atributos estruturados e cadastro do estabelecimento. Se heurística
  de texto for aprovada, ela deve ser efêmera, explicável, testada e nunca
  transformar review em verdade persistente para todos os usuários.

## Critérios de aceite para a task

- Cada filtro tem uma definição de elegibilidade e fonte identificável.
- `UNKNOWN`/ausente não entra como positivo.
- Música ao vivo e ambiente infantil podem vir de atributos estruturados; “sem
  informação” não elimina automaticamente o lugar.
- Karaokê só aparece quando houver dado próprio/curado aprovado.
- Cache não mascara atualização nem mistura tags de um place ID diferente.
- Testes cobrem tag positiva, negativa, desconhecida, expiração e combinação de
  fontes.

## Fora de escopo

IA, mineração massiva de reviews, classificação definitiva de reputação,
editorial de eventos e layout dos chips/filtros.

## Referências

- [Places SDK data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Place.Field](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Field?utm_campaign=gmp_git_agentskills_v1)
- [Reviews e atribuição](https://developers.google.com/maps/documentation/places/android-sdk/place-details?utm_campaign=gmp_git_agentskills_v1)
- [Maps service terms](https://cloud.google.com/maps-platform/terms/maps-service-terms?utm_campaign=gmp_git_agentskills_v1)

