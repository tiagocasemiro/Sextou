# Subagente — Nota do Estabelecimento

## Missão

Escrever a task que mostra a reputação geral do estabelecimento no Google para
apoiar a decisão do usuário, sem transformar a nota em avaliação específica da
comida ou do serviço do Sextou.

## Briefing de negócio

A nota e a quantidade de avaliações funcionam como sinal de confiança e
comparação. Devem ser claramente identificadas como Google, respeitar a escala
retornada e permanecer neutras quando o Google não possuir avaliação.

## Dados e APIs

- Places SDK for Android (New), campos `RATING` e `USER_RATING_COUNT` em busca
  ou detalhes conforme a tela.
- Modelo Google: `Place.rating` e `Place.userRatingCount`; modelo do projeto:
  `PlaceSummary`/`PlaceDetails` e futuro `GoogleRating` com `value: Double?`,
  `userRatingCount: Int?`, `retrievedAt` e `source`.
- Não solicitar `REVIEWS` apenas para renderizar a média. Se avaliações
  completas forem solicitadas depois, tratar atribuição, idioma e limite do
  fornecedor.

## Domínio e persistência

- `GetGoogleRatingUseCase` encaminha o dado remoto e pode selecionar cache
  temporário elegível.
- `GoogleRatingCacheEntity(placeId, rating, userRatingCount, fetchedAt,
  expiresAt)` é dado derivado do Google e deve seguir os termos vigentes;
  `placeId` é a chave de vínculo.
- Invariantes: nota entre 1 e 5 quando presente; quantidade não negativa;
  ausência de nota não implica zero.

## Critérios de aceite para a task

- Mostra nota com uma casa decimal e quantidade somente quando cada campo
  correspondente estiver disponível.
- Rotula a origem como Google e não usa linguagem de “nota da comida” ou
  “custo-benefício”.
- Sem nota, comunica que ainda não há avaliação; sem quantidade, não inventa
  contagem.
- Online prioriza valor atual; cache só aparece dentro da validade definida.
- Testes cobrem limites, nulos, formatação e falha de consulta.

## Fora de escopo

Avaliação própria do Sextou, análise de sentimento, agregação de reviews e
ranking editorial.

## Referências

- [Place data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Place rating and review fields](https://developers.google.com/maps/documentation/places/android-sdk/place-details?utm_campaign=gmp_git_agentskills_v1)
- [Place model](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place?utm_campaign=gmp_git_agentskills_v1)

