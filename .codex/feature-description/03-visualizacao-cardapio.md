# Subagente — Visualização do Cardápio

## Missão

Escrever a task que apresenta o que o Google sabe sobre oferta gastronômica e
modalidades de atendimento de um estabelecimento e oferece uma saída externa
para o cardápio completo quando existir.

## Briefing de negócio

O usuário precisa saber se o local combina com o tipo de rolê — por exemplo,
se serve jantar, cerveja ou comida vegetariana — antes de sair. A feature deve
ser honesta: Places não entrega itens, descrições e preços de um cardápio
completo de estabelecimentos arbitrários. O Sextou mostra apenas atributos
retornados e encaminha ao site oficial ou ao Google Maps.

## Dados e APIs

- `FetchPlaceRequest`/Place Details por `placeId` no Places SDK for Android
  (New), com `PRICE_LEVEL`, `PRICE_RANGE`, `SERVES_BREAKFAST`, `SERVES_BRUNCH`,
  `SERVES_LUNCH`, `SERVES_DINNER`, `SERVES_DESSERT`, `SERVES_COFFEE`,
  `SERVES_BEER`, `SERVES_WINE`, `SERVES_COCKTAILS`, `SERVES_VEGETARIAN_FOOD`,
  `MENU_FOR_CHILDREN`, `DINE_IN`, `TAKEOUT`, `DELIVERY`, `CURBSIDE_PICKUP`,
  `PHOTO_METADATAS`, `WEBSITE_URI` e `GOOGLE_MAPS_URI`.
- Fotos são `PhotoMetadata`/`PlacePhotoReference` e precisam de atribuição;
  nunca rotular fotos genéricas como itens do menu.
- Modelos do projeto: `PlaceDetails`, `PlaceAmenities`, `PriceRange`, `Money`,
  `PlacePhotoReference`, `PlacePhoto` e `GoogleMapsLinks`.
- Para cardápio itemizado futuro, fonte deve ser base própria/cadastro do
  estabelecimento; Google Business Profile exige autorização da conta do
  proprietário e não entra no MVP.

## Domínio e persistência

- `GetPlaceMenuSummaryUseCase` seleciona atributos disponíveis e `OpenMenuUri`
  prioriza `websiteUri`, depois `googleMapsUri`.
- O modelo `MenuSummary` pode conter `priceLevel`, `priceRange`, lista de
  `FoodServiceAttribute` e `externalMenuUri`; não armazenar texto de UI no
  domínio.
- Cache temporário, se aprovado, deve ser mínimo, datado e associado ao
  `placeId`; atualizar online antes de exibir preço/modalidade potencialmente
  mutável. Não persistir itens que a API não forneceu.

## Critérios de aceite para a task

- Exibe somente atributos presentes e conhecidos; valores desconhecidos são
  omitidos, não convertidos para “não oferece”.
- Sem atributo gastronômico, o resultado informa ausência de dados e ainda
  oferece link externo quando existente.
- O link externo usa site oficial antes do Google Maps; sem ambos, a ação não é
  disponibilizada.
- Preço e fotos respeitam moeda, atribuição e origem Google.
- Falha online pode usar cache elegível; cache vencido não é tratado como
  informação atual.
- Testes cobrem combinações de atributos, ausência de links, falha e sucesso.

## Fora de escopo

Itens/preços/disponibilidade de pratos, checkout, pedidos, scraping de sites,
classificação visual do cardápio e cadastro de parceiros.

## Referências

- [Places SDK data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Place.Field](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Field?utm_campaign=gmp_git_agentskills_v1)
- [Place model](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place?utm_campaign=gmp_git_agentskills_v1)
- [Place Photos](https://developers.google.com/maps/documentation/places/android-sdk/place-photos?utm_campaign=gmp_git_agentskills_v1)
- [Food menus na Business Profile API](https://developers.google.com/my-business/reference/rest/v4/accounts.locations/getFoodMenus?utm_campaign=gmp_git_agentskills_v1)

