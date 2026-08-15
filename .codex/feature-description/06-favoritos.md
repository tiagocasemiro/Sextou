# Subagente — Favoritar Estabelecimentos

## Missão

Escrever a task que permite ao usuário guardar locais de interesse recorrente e
consultá-los rapidamente, inclusive sem rede.

## Briefing de negócio

Favoritos são uma relação pessoal do usuário, não uma classificação pública. A
ação deve ser idempotente, refletir-se nos contextos que exibem o lugar e
preservar o vínculo mesmo quando o detalhe do Google estiver indisponível.

## Dados e APIs

- Google apenas fornece/valida o `placeId`; não é necessário fazer chamada ao
  Maps ao marcar ou desmarcar.
- Modelo de domínio: `Favorite(placeId, favoritedAt)` e, se necessário,
  `FavoritePlace(placeId, cachedSummary?)` separado do estado do usuário.

## Banco e arquitetura

- Criar `FavoriteEntity(placeId PRIMARY KEY, favoritedAt)` em módulo `local`,
  `FavoriteDao` com `observeAll(): Flow<List<...>>`, `contains(placeId)` e
  inserção/remoção idempotentes.
- Contrato: `FavoriteRepository.Local`; adapter `FavoriteLocalImpl`; UseCases
  `ToggleFavoriteUseCase` e `ObserveFavoritesUseCase`.
- A lista offline pode exibir apenas snapshot elegível do lugar; não usar o
  favorito como autorização para manter indefinidamente conteúdo do Google.
- Invariante: um único favorito por `placeId`; a data de criação não muda em
  toggle repetido que mantém o estado.

## Critérios de aceite para a task

- Marcar grava uma vez; desmarcar remove; repetir a mesma ação não duplica nem
  falha.
- A aba de favoritos observa o banco e atualiza sem nova busca obrigatória.
- Falha de rede não impede marcar, desmarcar ou consultar IDs.
- Place ID vazio é rejeitado como entrada inválida de domínio.
- Testes cobrem inserção, remoção, idempotência, fluxo vazio e sobrevivência
  offline.

## Fora de escopo

Sincronização de conta, listas compartilhadas, ranking público, layout e
download ilimitado dos detalhes Google.

## Referências

- [Place IDs](https://developers.google.com/maps/documentation/places/web-service/place-id?utm_campaign=gmp_git_agentskills_v1)
- [Room](https://developer.android.com/training/data-storage/room?utm_campaign=gmp_git_agentskills_v1)
- [Maps service terms](https://cloud.google.com/maps-platform/terms/maps-service-terms?utm_campaign=gmp_git_agentskills_v1)

