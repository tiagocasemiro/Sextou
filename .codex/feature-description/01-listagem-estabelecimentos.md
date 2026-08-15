# Subagente — Listagem de Estabelecimentos

## Missão

Escrever a task que permite ao usuário descobrir bares, restaurantes, trailers,
cafés e outros pontos de alimentação/entretenimento próximos ou pesquisados.
O resultado deve ser uma coleção útil para decidir onde ir, com dados de
identificação e contexto suficientes, sem prometer disponibilidade que o Google
não retornou.

## Briefing de negócio

O usuário abre o Sextou para encontrar opções reais para o rolê. A busca deve
combinar localização, texto e categorias do produto, retornando resultados
ordenáveis por distância ou relevância. Locais bloqueados pelo usuário não
podem aparecer. A listagem deve continuar útil com cache válido quando a rede
falhar, mas precisa sinalizar quando a informação estiver desatualizada.

## Dados e APIs

- Places SDK for Android (New): `SearchNearbyRequest` para proximidade e
  `SearchByTextRequest` para texto; usar `includedTypes`/`includedType`,
  `excludedTypes`, `includedPrimaryTypes`, `rankPreference`, `maxResultCount`,
  `openNow`, `minRating`, `locationBias` e `regionCode` somente quando a regra
  da busca exigir.
- Campos de resumo sugeridos: `ID`, `DISPLAY_NAME`, `FORMATTED_ADDRESS`,
  `LOCATION`, `PRIMARY_TYPE`, `PRIMARY_TYPE_DISPLAY_NAME`, `TYPES`,
  `BUSINESS_STATUS`, `RATING`, `USER_RATING_COUNT`, `PRICE_LEVEL` e
  `GOOGLE_MAPS_URI`. Fotos devem ser buscadas separadamente quando realmente
  forem exibidas.
- Modelo Google: `Place`; modelos do projeto já existentes:
  `PlaceSummary`, `GeoPoint`, `BusinessStatus`, `PlaceTextSearchRequest` e
  `NearbySearchRequest`.

## Domínio e persistência

- Criar/usar `SearchPlacesUseCase` e `PlacesRepository.Remote`.
- Para cache, criar `PlacesRepository.Cache`/`Local` somente se a política e o
  caso de uso justificarem. A entidade pode ser `PlaceSummaryCacheEntity`, com
  `placeId`, payload mínimo permitido, consulta/origem e `expiresAt`.
- O filtro de `ListaNegraEntity` deve ser aplicado no domínio antes de expor a
  lista; a listagem não decide isso na ViewModel.
- Invariantes: consulta não vazia para texto; raio e quantidade dentro dos
  limites da API; IDs não vazios; ausência de nota/preço não elimina o lugar.

## Critérios de aceite para a task

- Busca próxima retorna apenas categorias/configuração solicitadas e converte
  respostas para modelos de domínio.
- Busca textual aceita erro, vazio, ausência de localização e paginação/limite
  definidos pelo produto.
- Resultados presentes na lista negra são omitidos também quando vierem do
  cache.
- Com cache válido e sem rede, a última lista elegível é exibida como
  desatualizada; cache expirado não é tratado como dado atual.
- Campo ausente é representado como nulo/`UNKNOWN`, sem inferência negativa.
- Há testes unitários para validação, sucesso, vazio, falha e filtragem local.

## Fora de escopo

Layout final, ordenação visual, paginação infinita não definida, cardápio
itemizado, recomendações personalizadas e cadastro próprio de estabelecimentos.

## Referências

- [Places SDK for Android — data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Nearby Search (New)](https://developers.google.com/maps/documentation/places/android-sdk/nearby-search?utm_campaign=gmp_git_agentskills_v1)
- [Text Search (New)](https://developers.google.com/maps/documentation/places/android-sdk/text-search?utm_campaign=gmp_git_agentskills_v1)
- [Place.Field](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Field?utm_campaign=gmp_git_agentskills_v1)
- [Política de uso e armazenamento](https://cloud.google.com/maps-platform/terms/maps-service-terms?utm_campaign=gmp_git_agentskills_v1)

