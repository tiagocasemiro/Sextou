# Subagente — Visualização em Mapa Interativo

## Missão

Escrever a task que permite alternar entre a descoberta em lista e a posição
geográfica dos estabelecimentos em um mapa, mantendo seleção, câmera e estado
do usuário coerentes.

## Briefing de negócio

O mapa ajuda o usuário a entender concentração, distância e vizinhança antes de
escolher um local. Cada marcador precisa representar um resultado permitido
pela listagem; tocar/selecionar um marcador deve levar ao mesmo contexto de
detalhes do item da lista. Locais da lista negra não devem ser desenhados.
Favoritos, visitados e “quero ir” podem alterar o estado sem alterar a origem
geográfica do lugar.

## Dados e APIs

- Maps SDK for Android (New), `GoogleMap`, `GoogleMapOptions`, `MapView` ou
  integração equivalente com Compose; marcadores, câmera e eventos de clique.
- `Place.location`/modelo `GeoPoint` fornece latitude e longitude. O mapa não
  deve buscar detalhes novamente por marcador sem uma decisão do domínio.
- Usar Map ID e estilo em nuvem apenas como configuração técnica aprovada; não
  decidir identidade visual nesta task. Registrar
  `MapsApiSettings.addInternalUsageAttributionId(context,
  "gmp_git_agentskills_v1")` na inicialização.
- Modelos Google/Android: `GoogleMap`, `MarkerOptions`, `CameraPosition` e
  `LatLng`; modelos do domínio: `GeoPoint`, `PlaceSummary` e um futuro
  `PlaceMapItem`/`PlaceUserFlags`.

## Domínio e persistência

- `ObservePlaceMapUseCase` combina resultados da busca com flags locais de
  favoritos, visita, “quero ir” e bloqueio.
- Persistência própria: consultar `FavoriteEntity`, `WantToGoEntity`,
  `VisitEntity` e `BlacklistEntity` por `placeId`; o mapa não grava marcadores.
- `PlaceMapItem` deve manter `placeId`, `position`, `title` opcional e flags do
  usuário. Coordenada ausente torna o item inelegível para o mapa, mas não deve
  apagar o resultado da lista.

## Critérios de aceite para a task

- O mapa inicializa somente com permissão/localização disponível conforme o
  contrato de localização definido; sem isso, usa a região da busca ou estado
  vazio.
- Cada item elegível tem um marcador e a seleção do marcador identifica o
  mesmo `placeId` da lista.
- A lista negra é respeitada em toda atualização, inclusive ao combinar cache e
  novas flags locais.
- Alterar favorito/visita/“quero ir” atualiza os flags do marcador sem duplicar
  lugares.
- Mapa vazio, erro de carregamento e ausência de coordenada têm estados
  explícitos; não há crash por dados incompletos.
- Testar no domínio o join entre lugares e flags, incluindo IDs repetidos e
  bloqueados.

## Fora de escopo

Rotas desenhadas, Street View, clustering avançado, GeoJSON, tráfego, 3D e
decisões finais de estilo/ícone.

## Referências

- [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview?utm_campaign=gmp_git_agentskills_v1)
- [Marcadores](https://developers.google.com/maps/documentation/android-sdk/marker?utm_campaign=gmp_git_agentskills_v1)
- [Map IDs e estilo em nuvem](https://developers.google.com/maps/documentation/android-sdk/cloud-customization?utm_campaign=gmp_git_agentskills_v1)
- [Place data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)

