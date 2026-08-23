# Subagente — Não Voltar no Estabelecimento

## Missão

Escrever a task que permite ao usuário ocultar permanentemente, até desfazer,
locais que não deseja receber como sugestão.

## Briefing de negócio

O usuário quer controlar o próprio feed sem publicar uma denúncia ou alterar a
reputação do estabelecimento. Depois do bloqueio, o local não deve voltar em
listas ou marcadores produzidos pelo Sextou, independentemente da nota ou
popularidade retornada pelo Google.

## Dados e APIs

- Google só fornece o `placeId` usado como chave.
- Domínio: `BlacklistedPlace(placeId, blockedAt)` e `PlaceVisibilityDecision`.

## Banco e arquitetura

- `BlacklistEntity(placeId PRIMARY KEY, blockedAt)` e `BlacklistDao` no módulo
  `local`, com `observeIds(): Flow<Set<String>>`, `contains` e toggle.
- `BlacklistRepository.Local`, `ToggleBlacklistUseCase` e um UseCase de
  composição da descoberta que filtra antes de lista/mapa/cache.
- Não guardar motivo como dado público; um campo privado opcional pode ser
  considerado depois. A lista negra não deve apagar favoritos/visitas/notas sem
  decisão explícita.
- A regra é `placeId ∈ blacklist ⇒ não aparece em feed/marcadores`; o detalhe
  aberto diretamente por ação explícita pode ter política própria, documentada
  no board.

## Critérios de aceite para a task

- Bloquear/desbloquear funciona offline e é idempotente.
- Resultados novos, cacheados e já carregados são filtrados pelo mesmo UseCase.
- O local bloqueado não aparece na lista nem no mapa após a mudança.
- Desbloquear permite que ele volte em uma nova consulta/atualização, sem
  fabricar dados antigos.
- Testes cobrem bloqueio, desbloqueio, combinação com cache e múltiplas fontes.

## Fora de escopo

Denúncia ao Google, moderação, compartilhamento de lista negra, classificação
pública e exclusão definitiva do lugar nos dados remotos.

## Referências

- [Place IDs](https://developers.google.com/maps/documentation/places/web-service/place-id?utm_campaign=gmp_git_agentskills_v1)
- [Room](https://developer.android.com/training/data-storage/room?utm_campaign=gmp_git_agentskills_v1)
- [Maps service terms](https://cloud.google.com/maps-platform/terms/maps-service-terms?utm_campaign=gmp_git_agentskills_v1)

