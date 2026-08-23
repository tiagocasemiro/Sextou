# Tipos de estabelecimento

Categorias para identificar estabelecimentos pelos tipos oficiais da Places
API (New).

Possíveis mesclas no produto, mantendo os tipos específicos da API:

- Bares: Boteco, Bar, Pub, Bar e restaurante, Bar de coquetéis, Bar
  esportivo e Bar de cerveja / chopp.
- Restaurantes: Restaurante, Churrascaria, Hamburgueria, Hamburgueria de
  fast-food, Pizzaria, Brunch e Café da manhã.
- Bebidas: Adega e Depósito de bebidas.
- Cafés e sobremesas: Café, Doceria / confeitaria e Sorveteria.
- Entretenimento noturno: Karaokê, Balada e Música ao vivo.
- Cervejas: Cervejaria, Brewpub e Bar de cerveja / chopp. Essa mescla é
  opcional caso seja necessário diferenciar produtor, local de produção e
  local de consumo.

Adega de vinhos / local de degustação (`wine_bar`), Vinícola (`winery`),
Cervejaria (`brewery`) e Brewpub (`brewpub`) devem permanecer separados caso o
produto precise distinguir comércio, produtor e local de consumo.

## Categorias principais

Boteco → `bar`

Bar → `bar`

Bar e restaurante → `bar_and_grill`

Karaokê → `karaoke`

Adega → `wine_bar`, `winery`

Depósito de bebidas → `liquor_store`, `warehouse_store`, `wholesaler`

Churrascaria → `steak_house`, `barbecue_restaurant`

Restaurante → `restaurant`

Hamburgueria → `hamburger_restaurant`

Hamburgueria de fast-food → `fast_food_restaurant`

Pizzaria → `pizza_restaurant`

Chopperia → `beer_garden`, `brewery`, `brewpub`

Café → `cafe`, `coffee_shop`

Padaria → `bakery`

Pub → `pub`

Bar de coquetéis → `cocktail_bar`

Bar esportivo → `sports_bar`

Balada → `night_club`

Música ao vivo → `live_music_venue`

Café da manhã → `breakfast_restaurant`, `brunch_restaurant`

Doceria / confeitaria → `confectionery`, `dessert_shop`, `pastry_shop`,
`cake_shop`

Sorveteria → `ice_cream_shop`
