# Subagente — Rota Automática para o Rolê

## Missão

Escrever a task que encaminha o usuário da origem atual ao estabelecimento,
começando por uma solução segura e simples e deixando rota desenhada no app
como evolução explícita.

## Briefing de negócio

Depois de escolher um local, o usuário precisa saber como chegar. No MVP, a
melhor experiência pode ser abrir o Google Maps já com destino e place ID,
delegando navegação, trânsito e instruções ao app especializado. Se o produto
exigir polilinha dentro do Sextou, a task deve criar contrato próprio e usar a
Routes API moderna.

## Dados e APIs

- MVP recomendado: Android `Intent`/Google Maps URLs com `destination_place_id`
  e/ou coordenada/endereço; nenhuma chamada Maps API nem persistência.
- Evolução in-app: Routes API `computeRoutes`, com origem, destino, `travelMode`,
  `routingPreference`/tráfego, `distanceMeters`, `duration`,
  `polyline.encodedPolyline` e eventualmente `localizedValues`.
- Nunca usar Directions API ou `DirectionsService` legados. Não usar Distance
  Matrix; para vários destinos, avaliar `computeRouteMatrix`.
- Domínio: `RouteRequest(origin: GeoPoint, destination: PlaceDestination,
  travelMode)`, `RouteSummary(distanceMeters, durationSeconds,
  encodedPolyline?, source)`.
- Origem depende de permissão/localização Android; localização do dispositivo
  não é dado persistente desta feature.

## Banco e arquitetura

- Intent externa: nenhum Repository local/remoto; UseCase valida `placeId` ou
  coordenada e produz comando de abertura para a camada Android.
- Rota in-app: `RoutesRepository.Remote` em `networking`; API key restrita e
  field mask obrigatória; não guardar rota como cache sem necessidade.

## Critérios de aceite para a task

- Destino usa o lugar selecionado, não apenas texto livre ambíguo.
- Sem localização/permissão, usuário recebe estado acionável e não há crash.
- Se Google Maps não estiver instalado, URL/browser ou mensagem de fallback é
  tratada conforme decisão do produto.
- Para rota in-app, resposta parcial/erro, modo de transporte e unidade de
  distância são tratados; polilinha não é exibida se ausente.
- Testes cobrem validação do destino, intent/fallback por adapter e parsing de
  rota, isolando Android e rede.

## Fora de escopo

Navegação curva a curva, Waze, histórico de trajetos, roteiros de múltiplas
paradas e ETA offline.

## Referências

- [Google Maps URLs](https://developers.google.com/maps/documentation/urls/get-started?utm_campaign=gmp_git_agentskills_v1)
- [Routes API](https://developers.google.com/maps/documentation/routes/overview?utm_campaign=gmp_git_agentskills_v1)
- [Compute routes](https://developers.google.com/maps/documentation/routes/compute_route_directions?utm_campaign=gmp_git_agentskills_v1)
- [Maps SDK Android](https://developers.google.com/maps/documentation/android-sdk/overview?utm_campaign=gmp_git_agentskills_v1)
