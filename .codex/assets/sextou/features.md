Aqui está a especificação técnica das funcionalidades do Sextou, desenhada exclusivamente para Android e baseada na integração entre a Google Places API e o seu Banco de Dados Local (como Room ou SQLite). [1, 2]
------------------------------
# 🛠️ Especificação de Features: App “Sextou”

## 🪟 Funcionalidades Principais (Core Features)

### 📋 Listagem de Estabelecimentos
Exibe os locais de entretenimento e comida pronta em formato de lista rolável para o usuário.

* Integração Google Maps: Busca inicial via Text Search ou Nearby Search usando o parâmetro includedTypes com as categorias selecionadas (bar, restaurant, meal_takeaway, convenience_store, etc.). Retorna nome, foto principal, nota, endereço e status de abertura. [3, 4]
* Persistência Local: O app armazena temporariamente os dados retornados (cache de 7 a 15 dias) para evitar requisições repetidas à API e economizar custos de infraestrutura.

## 🗺️ Visualização em Mapa Interativo
Exibe os estabelecimentos em um mapa interativo do Google, facilitando a navegação geográfica.

* Integração Google Maps: Utilização do Google Maps SDK para Android. A API plota os pinos (markers) na tela utilizando as coordenadas de latitude e longitude (location) obtidas na busca do Places. [5, 6, 7]
* Persistência Local: O banco local faz a ponte para colorir ou modificar os pinos com base no comportamento do usuário (ex: exibir um pino com ícone de coração se o local estiver favoritado no banco de dados).

## 🍽️ Visualização do Cardápio
Permite ao usuário consultar, na tela de detalhes do estabelecimento, um resumo das opções de comida e bebida informadas pelo Google Maps e acessar a fonte externa quando houver um cardápio mais completo.

* **Resumo no Sextou:** Exibe faixa de preço e indicadores como café da manhã, brunch, almoço, jantar, sobremesa, café, cerveja, vinho, coquetéis, comida vegetariana e cardápio infantil. Também informa se o local oferece consumo no estabelecimento, retirada, entrega ou retirada na calçada.
* **Integração Google Maps:** Ao abrir os detalhes, o app consulta o estabelecimento pelo `place_id` usando o Places SDK for Android e solicita somente os campos necessários: `PRICE_LEVEL`, `PRICE_RANGE`, `SERVES_BREAKFAST`, `SERVES_BRUNCH`, `SERVES_LUNCH`, `SERVES_DINNER`, `SERVES_DESSERT`, `SERVES_COFFEE`, `SERVES_BEER`, `SERVES_WINE`, `SERVES_COCKTAILS`, `SERVES_VEGETARIAN_FOOD`, `MENU_FOR_CHILDREN`, `DINE_IN`, `TAKEOUT`, `DELIVERY`, `CURBSIDE_PICKUP`, `PHOTO_METADATAS`, `WEBSITE_URI` e `GOOGLE_MAPS_URI`. A máscara de campos deve ser restrita para controlar latência e cobrança.
* **Fotos:** Quando disponíveis, as fotos do estabelecimento podem complementar a visualização. Como o Places não identifica quais fotos representam o cardápio, elas devem aparecer como “fotos do local”, sem serem apresentadas como uma lista oficial de pratos.
* **Cardápio completo:** A ação “Ver cardápio completo” abre primeiro o site oficial (`websiteUri`), quando informado, ou a página do estabelecimento no Google Maps (`googleMapsUri`) como alternativa. O botão não deve ser exibido quando nenhum dos dois endereços estiver disponível.
* **Dados indisponíveis:** Campos ausentes ou com valor desconhecido devem ser omitidos. Se nenhum atributo de alimentação estiver disponível, a tela informa “Cardápio não informado pelo estabelecimento” e ainda oferece o acesso externo, caso exista.
* **Persistência Local:** Armazena o resumo vinculado ao `place_id` apenas como cache temporário, respeitando as políticas de armazenamento e atribuição do Google Maps Platform. Ao existir conexão, o app prioriza uma consulta atualizada antes de exibir informações sensíveis a mudança, como faixa de preço e modalidades de atendimento.
* **Limitação da fonte:** A Places API não disponibiliza ao Sextou os itens, descrições e preços de cardápios completos de estabelecimentos arbitrários. A Google Business Profile API possui dados itemizados, mas seu acesso exige autorização OAuth da conta proprietária de cada local; por isso, ela não faz parte desta funcionalidade principal.

## ⭐ Nota do Estabelecimento
Exibe a avaliação média atribuída pelos usuários do Google ao estabelecimento para ajudar na comparação entre os locais.

* **Exibição:** Mostra a nota de `1,0` a `5,0`, com uma casa decimal, acompanhada da quantidade de avaliações e da identificação de que a informação vem do Google. Exemplo: “4,6 ★ no Google (328 avaliações)”.
* **Integração Google Maps:** O app solicita os campos `RATING` e `USER_RATING_COUNT` do Places SDK for Android. A nota é geral e representa a experiência no estabelecimento; ela não deve ser apresentada como uma avaliação exclusiva da comida, bebida, atendimento ou ambiente.
* **Dados indisponíveis:** Quando `RATING` estiver ausente, a interface exibe “Ainda sem avaliação no Google”. A quantidade de avaliações só deve ser mostrada quando `USER_RATING_COUNT` estiver disponível.
* **Persistência Local:** A nota e a quantidade de avaliações podem ser mantidas apenas como cache temporário associado ao `place_id`. Sempre que houver conexão, a tela de detalhes deve priorizar os valores atualizados.

## 💲 Faixa de Preço de Comidas e Bebidas
Apresenta uma estimativa do nível de preço do estabelecimento para ajudar o usuário a escolher um local compatível com seu orçamento.

* **Exibição:** Mostra o nível retornado pelo Google em uma escala visual de preço, de gratuito a muito caro, e exibe os valores monetários mínimo e máximo quando uma faixa estiver disponível. A moeda retornada pela API deve ser preservada.
* **Integração Google Maps:** O app solicita `PRICE_LEVEL` e `PRICE_RANGE` pelo Places SDK for Android. Esses campos descrevem o estabelecimento como um todo e não distinguem os preços de comidas dos preços de bebidas.
* **Limitação da fonte:** A Places API não fornece uma nota de custo-benefício nem uma avaliação de preço em estrelas. Por isso, a interface deve usar os termos “nível de preço” ou “faixa de preço” e nunca apresentar o dado como opinião dos clientes.
* **Dados indisponíveis:** Quando os dois campos estiverem ausentes, a interface exibe “Faixa de preço não informada”. Se apenas um deles existir, somente a informação disponível deve ser mostrada.
* **Persistência Local:** Os valores podem ser armazenados apenas como cache temporário associado ao `place_id`, com atualização prioritária quando houver conexão.

## ⭐️ Favoritar Estabelecimentos
Permite ao usuário salvar os botecos e trailers que ele mais frequenta e ama.

* Integração Google Maps: A API do Google fornece apenas o identificador único do local (place_id).
* Persistência Local: Foco 100% no banco de dados local. O app cria uma tabela Favoritos e salva o place_id, o nome do local e a data em que foi favoritado. Quando o usuário abre a aba de favoritos, o app lê esses IDs do banco local e exibe a lista instantaneamente.

## 📍 Lista para Conhecer (Quero Ir)
Funciona como um bookmark de locais desejados para os próximos fins de semana.

* Integração Google Maps: Utiliza o place_id do estabelecimento visualizado.
* Persistência Local: O banco local armazena esses IDs em uma tabela Lista_Para_Conhecer. Permite que o usuário consulte seus planos de rolê mesmo se estiver totalmente offline ou sem sinal de internet no subúrbio.

### ✅ Já Visitei o Estabelecimento
Permite ao usuário marcar os botecos, trailers e restaurantes onde ele já esteve, criando um histórico pessoal de locais conferidos.
*   **Integração Google Maps:** Utiliza o `place_id` fornecido pela API do Google para identificar o local de forma única na tela de detalhes ou diretamente no card da listagem.
*   **Persistência Local (Room/SQLite):** O aplicativo cria uma tabela chamada `Historico_Visitas`. Quando o usuário ativa essa função, o `place_id` é salvo com o registro da data. Na interface, o card ganha um indicador visual (ex: um check verde escrito "Já fui!").

### 🚫 Não Voltar no Estabelecimento
Funciona como uma "lista negra" privada do usuário para sinalizar locais com atendimento ruim, cerveja quente ou que não valem o custo-benefício.
*   **Integração Google Maps:** Vincula a ação de bloqueio ao `place_id` do estabelecimento correspondente.
*   **Persistência Local (Room/SQLite):** O aplicativo armazena o ID em uma tabela chamada `Lista_Negra`.
*   **Regra de Negócio Local:** Toda vez que o aplicativo carregar os resultados vindos da API do Google Maps, o código fará uma checagem rápida no banco local. Se o `place_id` constar na `Lista_Negra`, o estabelecimento é **totalmente ocultado** do mapa e da listagem do usuário, garantindo um feed livre de ciladas.


------------------------------
## 💡 Sugestão de Funcionalidades Secundárias (Foco em Entretenimento e Lazer)

### 🎙️ Filtros Rápidos de Entretenimento (Tags do Sextou)
Botões no topo da tela para filtrar locais com Karaokê, Música ao Vivo ou Parquinhos.

* Integração Google Maps: O app solicita os campos estruturados `LIVE_MUSIC`, `GOOD_FOR_CHILDREN`, `OUTDOOR_SEATING`, `GOOD_FOR_GROUPS`, `GOOD_FOR_WATCHING_SPORTS` e `ALLOWS_DOGS` no Places SDK for Android (New). Karaokê não possui um campo estruturado garantido; deve vir de cadastro ou curadoria própria. Reviews não devem ser mineradas e persistidas como dataset compartilhado sem revisão de conformidade.
* Persistência Local: Após a primeira varredura do Google, o banco de dados local armazena as tags geradas vinculadas ao place_id. Assim, quando outro usuário aplicar o filtro "Karaokê", o app faz a busca no banco local de forma ultra rápida.

## 🚗 Rota Automática para o Rolê
Permite ao usuário traçar a rota da sua localização atual até o boteco ou trailer escolhido.

* Integração Google Maps: No MVP, o app utiliza uma Intent/Google Maps URL para abrir o Google Maps com o `place_id` e destino selecionados. Se o Sextou precisar desenhar o trajeto internamente, deve usar a Routes API (`computeRoutes`); a Directions API legada não deve ser usada em uma implementação nova.
* Persistência Local: Nenhuma (operação puramente em tempo real).

## 🕒 Alerta de "Tá Aberto agora?" (Status em Tempo Real)
Garante que o usuário não dê "com a cara na porta" ao ir atrás de um podrão ou birosca de madrugada.

* Integração Google Maps: Retorna o campo current_opening_hours.open_now (booleano) e a string com os horários de funcionamento detalhados do dia da semana atual.
* Persistência Local: Armazena o horário padrão de funcionamento no banco de dados para consulta offline, mas sempre prioriza a checagem online se houver conexão.

## 📝 Notas e Anotações Pessoais do Boteco
Permite ao usuário escrever lembretes privados sobre o local (Ex: "Pedir a batata com calabresa, a de frango não vale a pena" ou "O parquinho fica nos fundos").

* Integração Google Maps: Nenhuma.
* Persistência Local: Totalmente gerenciado pelo banco local (Room). O app cria uma tabela Notas_Usuario associando o texto criado ao place_id correspondente.

------------------------------
## Referências oficiais

* [Campos de dados da Places API](https://developers.google.com/maps/documentation/places/web-service/data-fields?utm_campaign=gmp_git_agentskills_v1)
* [Campos do modelo `Place` no Places SDK for Android](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Field?utm_campaign=gmp_git_agentskills_v1)
* [Campos de dados do Places SDK for Android](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
* [Modelo `Place` e limites de nota e preço](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place?utm_campaign=gmp_git_agentskills_v1)
* [Uso de máscaras de campos](https://developers.google.com/maps/documentation/places/web-service/choose-fields?utm_campaign=gmp_git_agentskills_v1)
* [Cardápios na Google Business Profile API](https://developers.google.com/my-business/reference/rest/v4/accounts.locations/getFoodMenus?utm_campaign=gmp_git_agentskills_v1)

## Análise para escrita das tasks no board

Esta seção transforma a especificação em briefings executáveis. Cada feature
possui um subagente próprio em `.codex/subagents/`; o subagente deve gerar a
Issue usando o briefing, sem fechar decisões visuais antes do design.

### Premissas técnicas e de negócio compartilhadas

- O `placeId` é o vínculo estável entre o Sextou e um lugar do Google. Os dados
  exclusivos do produto — favoritos, listas, visitas, bloqueios, notas,
  cardápios próprios, eventos e promoções — pertencem à base do Sextou.
- O app usa Places SDK for Android (New) para busca/detalhes/fotos e Maps SDK
  for Android para o mapa. Rotas internas, se necessárias, usam Routes API;
  Directions API e Distance Matrix API legadas não devem ser escolhidas para
  novas tasks.
- Campos ausentes retornam estado desconhecido/não informado. Ausência nunca é
  interpretada como `false`, zero ou estabelecimento fechado.
- Toda chamada Places deve solicitar somente os campos necessários. Qualquer
  cache de conteúdo Google precisa seguir os termos vigentes, ter validade e
  atribuição; o time não deve fixar “7–15 dias” sem validar a política para o
  campo específico. `placeId` e dados próprios do Sextou devem ser tratados
  separadamente.
- A arquitetura prevista é `Compose → ViewModel → UseCase → Repository`, com
  contratos/modelos no `domain`, gateways Google em `networking` e Room,
  entidades, DAOs, migrations e adapters locais em um módulo `local` Android
  Library. Regras de domínio recebem testes unitários.
- As chaves de produção devem ser restritas por pacote/SHA-1 e APIs habilitadas,
  nunca gravadas no código. Protótipos podem usar Maps Demo Key, que não é uma
  chave de produção e possui limite diário.

### Mapa das 13 tasks/subagentes

| # | Task sugerida | Valor de negócio | API principal | Persistência/modelo principal | Dependências |
| --- | --- | --- | --- | --- | --- |
| 1 | Descobrir estabelecimentos por localização, categoria e texto | Reduz o tempo para decidir onde ir | `SearchNearbyRequest`, `SearchByTextRequest` | `PlaceSummary`, `GeoPoint`; cache mínimo e `BlacklistEntity` | Localização, lista negra |
| 2 | Visualizar estabelecimentos no mapa | Permite entender distância e concentração | Maps SDK for Android, marcadores e câmera | `PlaceMapItem`, flags locais; sem persistir marcadores | Listagem, flags locais |
| 3 | Consultar resumo gastronômico e cardápio externo | Evita deslocamento sem saber o que o local oferece | Place Details + fields de alimentação/fotos/links | `PlaceDetails`, `PlaceAmenities`, `PriceRange`, `PlacePhotoReference` | Detalhes, nota/preço opcionais |
| 4 | Exibir nota geral do Google | Apoia comparação com sinal de reputação | `RATING`, `USER_RATING_COUNT` | `GoogleRating`; cache temporário | Detalhe/listagem |
| 5 | Exibir nível/faixa de preço | Ajuda a escolher dentro do orçamento | `PRICE_LEVEL`, `PRICE_RANGE` | `Money`, `PriceRange`, `GooglePriceCacheEntity` | Detalhe/listagem |
| 6 | Salvar favoritos | Permite acesso pessoal rápido e offline | Apenas `placeId` do Places | `FavoriteEntity`, `FavoriteRepository.Local` | Nenhuma |
| 7 | Guardar lugares para conhecer | Preserva planos para próximos fins de semana | Apenas `placeId` do Places | `WantToGoEntity`, `WantToGoRepository.Local` | Nenhuma |
| 8 | Registrar lugares visitados | Cria memória pessoal do rolê | Apenas `placeId` do Places | `VisitEntity`, `VisitRepository.Local` | Nenhuma |
| 9 | Ocultar lugares indesejados | Mantém a descoberta livre de ciladas | Apenas `placeId` do Places | `BlacklistEntity`, filtro no domínio | Listagem e mapa |
| 10 | Filtrar atrações de entretenimento | Permite escolher o tipo de noite | `LIVE_MUSIC`, `GOOD_FOR_CHILDREN` e dados próprios | `EntertainmentTagEntity`, `EntertainmentTag` | Listagem, curadoria própria |
| 11 | Abrir/calcular rota até o local | Converte escolha em deslocamento | Intent/Google Maps URLs; Routes API se in-app | `RouteRequest`, `RouteSummary`; normalmente sem banco | Localização, place selecionado |
| 12 | Informar se está aberto agora | Evita viagem perdida | `CURRENT_OPENING_HOURS`, `OPENING_HOURS` e secundários | `PlaceOpeningHours`, `OperatingStatus`; snapshot regular limitado | Detalhes, relógio/fuso |
| 13 | Criar notas pessoais privadas | Preserva contexto para a próxima visita | Nenhuma | `UserNoteEntity`, `UserPlaceNote`, Room | Place ID já conhecido |

### Briefings consolidados por feature

#### 1. Listagem de Estabelecimentos

**Descrição de negócio:** encontrar opções próximas ou pesquisadas de forma
rápida, respeitando categorias, distância/relevância e a lista negra. O usuário
deve conseguir decidir com nome, endereço, posição, status comercial e sinais
de avaliação/preço quando disponíveis, sem o app afirmar que o lugar está
aberto ou que um dado ausente é negativo.

**Contrato para a task:** `SearchPlacesUseCase` combina
`PlacesRepository.Remote` com cache/local e filtra `BlacklistEntity`. A busca
próxima usa `includedTypes`, `excludedTypes`, `rankPreference`, raio e limite;
a textual usa `query`, tipo, `openNow`, `minRating` e `locationBias`. O domínio
reutiliza `PlaceSummary`, `GeoPoint`, `BusinessStatus`,
`NearbySearchRequest` e `PlaceTextSearchRequest`.

**Aceite mínimo:** sucesso, vazio, falha, sem localização, cache válido,
cache vencido e lugar bloqueado são estados testáveis. Resultados sem nota,
preço ou coordenada continuam elegíveis para a lista, mas não para o mapa
quando a coordenada for indispensável.

#### 2. Visualização em Mapa Interativo

**Descrição de negócio:** mostrar a distribuição geográfica dos resultados e
abrir o mesmo contexto de detalhes ao selecionar um marcador. O mapa é uma
forma de exploração; não é uma segunda fonte de resultados.

**Contrato para a task:** Maps SDK for Android, `GoogleMap`, `LatLng`,
`MarkerOptions`, câmera e eventos. `ObservePlaceMapUseCase` combina
`PlaceSummary`/`GeoPoint` com `FavoriteEntity`, `WantToGoEntity`, `VisitEntity`
e `BlacklistEntity`, produzindo `PlaceMapItem`/flags. Registrar a atribuição
interna `gmp_git_agentskills_v1` na inicialização do SDK.

**Aceite mínimo:** não desenhar bloqueados, não duplicar IDs, suportar ausência
de permissão/localização e não quebrar com posição ausente. Estilo final,
clustering e ícones ficam fora do escopo.

#### 3. Visualização do Cardápio

**Descrição de negócio:** comunicar se o estabelecimento serve tipos de comida
e bebida e quais modalidades de atendimento estão informadas. Quando houver
site oficial, ele é o caminho para o cardápio completo; quando não houver,
usar o link do Google Maps. A Places API não deve ser descrita como fonte de
itens, descrições ou preços de pratos.

**Contrato para a task:** detalhes por `placeId` com `PRICE_LEVEL`,
`PRICE_RANGE`, campos `SERVES_*`, `MENU_FOR_CHILDREN`, `DINE_IN`, `TAKEOUT`,
`DELIVERY`, `CURBSIDE_PICKUP`, `PHOTO_METADATAS`, `WEBSITE_URI` e
`GOOGLE_MAPS_URI`. Modelos: `PlaceDetails`, `PlaceAmenities`, `PriceRange`,
`Money`, `PlacePhotoReference`, `PlacePhoto` e `GoogleMapsLinks`.

**Aceite mínimo:** omitir ausentes/desconhecidos, mostrar estado sem dados,
priorizar site sobre Maps, preservar moeda e atribuir fotos. Cardápio próprio,
pedidos e dados itemizados de parceiro são outras tasks.

#### 4. Nota do Estabelecimento

**Descrição de negócio:** dar um sinal de reputação geral para comparação,
identificado como Google. A nota não representa exclusivamente comida,
bebida, atendimento ou ambiente.

**Contrato para a task:** solicitar `RATING` e `USER_RATING_COUNT`; usar
`PlaceSummary`/`PlaceDetails` ou `GoogleRating(value, userRatingCount,
retrievedAt, source)`. Cache temporário pode usar
`GoogleRatingCacheEntity(placeId, rating, userRatingCount, fetchedAt,
expiresAt)` quando aprovado.

**Aceite mínimo:** uma casa decimal, quantidade apenas se disponível,
“ainda sem avaliação” sem nota, limites 1–5 e ausência sem conversão para zero.

#### 5. Faixa de Preço

**Descrição de negócio:** informar nível e/ou faixa monetária do local para
planejamento de orçamento, deixando claro que o dado representa o
estabelecimento inteiro e não preço de cada prato ou uma opinião de
custo-benefício.

**Contrato para a task:** solicitar `PRICE_LEVEL` e `PRICE_RANGE`; reutilizar
`PriceRange`/`Money` e criar `GooglePriceCacheEntity` somente se necessário.
Normalizar níveis sem transformar desconhecido em gratuito e preservar
`currencyCode`, `units` e `nanos`.

**Aceite mínimo:** nível e faixa independentes, moeda original, estado sem
informação e validação de faixa. Cardápio itemizado e gasto por pessoa ficam
fora.

#### 6. Favoritar Estabelecimentos

**Descrição de negócio:** permitir que a pessoa guarde locais queridos e os
consulte instantaneamente offline. O favorito é privado e não altera a
reputação do Google.

**Contrato para a task:** `Favorite(placeId, favoritedAt)`,
`FavoriteEntity(placeId PRIMARY KEY, favoritedAt)`, `FavoriteDao`,
`FavoriteRepository.Local`, `ToggleFavoriteUseCase` e
`ObserveFavoritesUseCase`. O nome exibido, se usado como snapshot, não pode
substituir a fonte atual do Places.

**Aceite mínimo:** toggle idempotente, fluxo observável, place ID obrigatório,
funcionamento sem rede e testes de inserção/remoção/vazio.

#### 7. Lista para Conhecer (Quero Ir)

**Descrição de negócio:** guardar planos de rolê futuros sem depender da rede.
Pode coexistir com favorito e visita; a relação com a lista negra deve ser
uma regra explícita, não efeito colateral oculto.

**Contrato para a task:** `WantToGo(placeId, addedAt)`,
`WantToGoEntity`, `WantToGoDao`, `WantToGoRepository.Local`,
`ToggleWantToGoUseCase` e `ObserveWantToGoUseCase`.

**Aceite mínimo:** add/remove offline e idempotente, ordem determinística,
detalhe indisponível sem perda do plano e testes de coexistência.

#### 8. Já Visitei o Estabelecimento

**Descrição de negócio:** registrar uma experiência pessoal para reconhecer
locais já conferidos. O board deve decidir se a primeira marca é suficiente ou
se cada visita será um evento histórico.

**Contrato para a task:** `VisitedPlace` ou `Visit`, `VisitEntity`, `VisitDao`,
`VisitRepository.Local`, `MarkPlaceVisitedUseCase` e
`ObserveVisitedPlacesUseCase`. O indicador é flag local no modelo de
apresentação, não mutação do `PlaceSummary` do Google.

**Aceite mínimo:** marcar offline, regra de repetição definida, consulta sem
detalhes Google e testes de primeira marca/repetição/vazio.

#### 9. Não Voltar no Estabelecimento

**Descrição de negócio:** remover do feed privado lugares que o usuário não
quer reencontrar. Isso não é denúncia pública nem alteração do Google.

**Contrato para a task:** `BlacklistedPlace(placeId, blockedAt)`,
`BlacklistEntity`, `BlacklistDao`, `BlacklistRepository.Local`,
`ToggleBlacklistUseCase` e filtro no UseCase que compõe descoberta/mapa/cache.
Regra central: `placeId` bloqueado não aparece em lista nem marcador.

**Aceite mínimo:** bloqueio/desbloqueio offline e idempotente, filtragem em
todas as fontes, retorno após desbloqueio em nova atualização e testes de
combinação com cache.

#### 10. Filtros Rápidos de Entretenimento

**Descrição de negócio:** deixar a pessoa procurar pelo tipo de noite, sem
inventar atrações. Atributos estruturados do Google e tags próprias precisam
ter origem e confiança identificáveis.

**Contrato para a task:** usar `Place.Field.LIVE_MUSIC`,
`Place.Field.GOOD_FOR_CHILDREN`, `OUTDOOR_SEATING`, `GOOD_FOR_GROUPS`,
`GOOD_FOR_WATCHING_SPORTS` e `ALLOWS_DOGS` quando aplicável. O modelo é
`EntertainmentTag(type, value, source, confidence, observedAt)`; `KARAOKE`
exige curadoria/cadastro próprio. `places.amenityOptions` não deve ser usado
como contrato Android.

**Aceite mínimo:** ausente/desconhecido não é positivo, cada filtro define sua
fonte, tags próprias são separadas das derivadas do Google, e reviews não são
mineradas como dataset compartilhado sem revisão de conformidade.

#### 11. Rota Automática para o Rolê

**Descrição de negócio:** transformar a escolha em deslocamento. O MVP pode
abrir o Google Maps com destino identificado por `placeId`; somente se houver
necessidade de desenhar o trajeto no Sextou deve-se implementar Routes API.

**Contrato para a task:** `RouteRequest`, `RouteSummary`, Intent/Google Maps
URLs e, na evolução in-app, `computeRoutes` com `distanceMeters`, `duration`,
`polyline.encodedPolyline`, `travelMode` e máscara de campos. Não usar
Directions/Distance Matrix legadas e não persistir rota sem necessidade.

**Aceite mínimo:** destino não ambíguo, localização/permissão ausente,
Google Maps ausente e fallback tratados; rota in-app deve tratar erro,
unidades, modo de transporte e polilinha ausente.

#### 12. Alerta “Tá Aberto agora?”

**Descrição de negócio:** reduzir viagens perdidas com uma informação atual,
sem garantir que o atendimento real esteja disponível. Horário regular offline
é diferente de status atual.

**Contrato para a task:** `CURRENT_OPENING_HOURS`, `OPENING_HOURS`,
`SECONDARY_OPENING_HOURS`, `CURRENT_SECONDARY_OPENING_HOURS`, `UTC_OFFSET` e
`TIME_ZONE`; modelos `PlaceOpeningHours`, `OpeningPeriod`, `WeekTime`,
`SpecialDay` e `OperatingStatus(isOpen: Boolean?, source, checkedAt)`.

**Aceite mínimo:** online aberto/fechado/desconhecido com horário de verificação,
offline sem afirmação de status atual, virada de meia-noite, múltiplos
períodos, dias especiais e fuso cobertos por testes.

#### 13. Notas e Anotações Pessoais

**Descrição de negócio:** guardar lembretes privados associados ao lugar para
melhorar a próxima visita, totalmente independente do conteúdo do Google.

**Contrato para a task:** `UserPlaceNote(id, placeId, text, createdAt,
updatedAt)`, `UserNoteEntity`, `UserNoteDao`, `UserNoteRepository.Local`,
UseCases de CRUD e observação. Texto não vazio, limite de tamanho, associação
por place ID e política de uma/múltiplas notas devem ser definidos.

**Aceite mínimo:** CRUD offline, validação sem alteração parcial, privacidade
local, timestamps coerentes e nenhum envio ao Maps. Criptografia,
sincronização e publicação são escopos futuros.

### Referências oficiais usadas nos briefings

- [Google Maps Platform — termos](https://cloud.google.com/maps-platform/terms?utm_campaign=gmp_git_agentskills_v1)
- [Maps service-specific terms](https://cloud.google.com/maps-platform/terms/maps-service-terms?utm_campaign=gmp_git_agentskills_v1)
- [Places API (New)](https://developers.google.com/maps/documentation/places/web-service/op-overview?utm_campaign=gmp_git_agentskills_v1)
- [Places SDK for Android — data fields](https://developers.google.com/maps/documentation/places/android-sdk/data-fields?utm_campaign=gmp_git_agentskills_v1)
- [Place.Field](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Field?utm_campaign=gmp_git_agentskills_v1)
- [Place model](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place?utm_campaign=gmp_git_agentskills_v1)
- [Nearby Search](https://developers.google.com/maps/documentation/places/android-sdk/nearby-search?utm_campaign=gmp_git_agentskills_v1)
- [Text Search](https://developers.google.com/maps/documentation/places/android-sdk/text-search?utm_campaign=gmp_git_agentskills_v1)
- [Place details](https://developers.google.com/maps/documentation/places/android-sdk/place-details?utm_campaign=gmp_git_agentskills_v1)
- [Place Photos](https://developers.google.com/maps/documentation/places/android-sdk/place-photos?utm_campaign=gmp_git_agentskills_v1)
- [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview?utm_campaign=gmp_git_agentskills_v1)
- [Routes API](https://developers.google.com/maps/documentation/routes/overview?utm_campaign=gmp_git_agentskills_v1)
- [Compute routes](https://developers.google.com/maps/documentation/routes/compute_route_directions?utm_campaign=gmp_git_agentskills_v1)
- [Google Maps URLs](https://developers.google.com/maps/documentation/urls/get-started?utm_campaign=gmp_git_agentskills_v1)
- [API key restrictions](https://developers.google.com/maps/api-security-best-practices?utm_campaign=gmp_git_agentskills_v1)
- [Room](https://developer.android.com/training/data-storage/room?utm_campaign=gmp_git_agentskills_v1)
