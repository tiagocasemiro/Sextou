# Subagente — Faixa de Preço

## Missão

Escrever a task que comunica a estimativa de preço do estabelecimento para
ajudar o usuário a escolher um rolê compatível com seu orçamento.

## Briefing de negócio

Preço é um sinal de planejamento, não uma promessa de valor de cada prato. A
feature deve preservar a moeda e diferenciar nível de preço de faixa monetária.
Quando só um dos dados estiver disponível, mostrar apenas esse dado.

## Dados e APIs

- Places SDK for Android (New): `PRICE_LEVEL` e `PRICE_RANGE` em `Place`.
- Modelo Google: `Place.priceLevel` e `Place.priceRange`/`Money`; modelos do
  projeto: `PriceRange`, `Money`, `PlaceSummary` e `PlaceDetails`.
- O preço representa o estabelecimento em geral; não separa comida de bebida e
  não é nota de custo-benefício.

## Domínio e persistência

- `GetPlacePriceUseCase` normaliza níveis e faixa sem arredondar ou trocar a
  moeda.
- `GooglePriceCacheEntity(placeId, priceLevel, currencyCode, startUnits,
  startNanos, endUnits, endNanos, fetchedAt, expiresAt)` apenas se o cache for
  permitido para o fluxo; dados do próprio Sextou podem ter entidade separada
  no futuro.
- Invariantes: `start <= end` quando ambos existem; nanos dentro do formato
  monetário; nível desconhecido não vira “grátis”.

## Critérios de aceite para a task

- Nível e faixa são exibidos de forma independente e com moeda original.
- Sem ambos, o estado é “não informado”; ausência não é convertida em zero.
- A comunicação não chama o dado de “avaliação” nem promete preço de item.
- Atualização online tem prioridade sobre cache; falha não apaga dado local
  ainda elegível.
- Testes cobrem nível isolado, faixa isolada, ambos, nulos e valores inválidos.

## Fora de escopo

Cardápio itemizado, cálculo de gasto por pessoa, avaliações de custo-benefício e
promoções.

## Referências

- [Place data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Place model — price fields](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place?utm_campaign=gmp_git_agentskills_v1)
- [Field masks](https://developers.google.com/maps/documentation/places/web-service/choose-fields?utm_campaign=gmp_git_agentskills_v1)

