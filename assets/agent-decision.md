# Decisões do agente

## 2026-08-15 — Visualização do cardápio

* A funcionalidade principal foi especificada como um resumo dos atributos de alimentação fornecidos pela Places API, pois essa API não expõe itens e preços de cardápios completos de estabelecimentos arbitrários.
* O acesso ao cardápio completo foi definido como navegação para o site oficial ou para a página do local no Google Maps.
* A Google Business Profile API foi documentada apenas como limitação, porque a leitura de `FoodMenus` exige autorização OAuth da conta proprietária do estabelecimento e não atende ao fluxo geral do Sextou.
* Foram incluídos estados de ausência de dados e uso de máscara de campos para evitar que a especificação pressuponha disponibilidade universal e para reduzir custo e latência.

## 2026-08-15 — Nota do estabelecimento e faixa de preço

* A nota geral foi definida com os campos `RATING` e `USER_RATING_COUNT`, pois ambos são disponibilizados pelo Places SDK for Android.
* O pedido de “nota para o preço” foi documentado como faixa de preço usando `PRICE_LEVEL` e `PRICE_RANGE`, porque a Places API não oferece uma nota de custo-benefício.
* A faixa de preço não foi dividida entre comida e bebida, pois os campos do Google descrevem o estabelecimento como um todo.
* Foram especificados estados de ausência de dados e cache temporário para evitar valores inventados ou apresentados como atuais quando a API não os retornar.
