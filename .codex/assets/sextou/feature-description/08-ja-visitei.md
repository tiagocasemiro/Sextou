# Subagente — Já Visitei o Estabelecimento

## Missão

Escrever a task que cria um histórico pessoal de locais visitados, permitindo
que o usuário reconheça experiências já realizadas e evite confundir memória
com dado público do Google.

## Briefing de negócio

O usuário marca um local quando esteve nele. O histórico pode ser consultado
offline e deve registrar quando a ação aconteceu. Uma visita pode ser registrada
novamente sem criar duplicatas acidentais; a decisão sobre múltiplas visitas
deve ser explícita no board.

## Dados e APIs

- Google é usado apenas para o `placeId` da ação.
- Domínio recomendado: `VisitedPlace(placeId, visitedAt)` para uma primeira
  visita; se o produto quiser histórico completo, `Visit(placeId, visitedAt)`
  com chave composta/local ID.

## Banco e arquitetura

- `VisitEntity` em `local`, com `VisitDao` para marcar, consultar por ID,
  observar IDs e, se aprovado, listar eventos por data.
- `VisitRepository.Local`, `MarkPlaceVisitedUseCase` e
  `ObserveVisitedPlacesUseCase`.
- A marca “já fui” deve ser um flag local no modelo combinado da listagem/mapa;
  não modificar `PlaceSummary` retornado pelo Google.
- Invariante: place ID obrigatório; timestamp do dispositivo deve ser salvo em
  formato comparável; não permitir que falha remota desfaça a marcação local.

## Critérios de aceite para a task

- Marcar funciona offline e torna o estado observável em lista, mapa e detalhe.
- Repetir marcação segue a política definida: idempotência para “já visitei” ou
  novo evento para histórico completo.
- Dados incompletos do Google não impedem consultar o histórico.
- O usuário pode remover/corrigir o registro se essa ação fizer parte do MVP.
- Testes cobrem primeiro registro, repetição, consulta vazia e falha local.

## Fora de escopo

Detecção automática por GPS/geofence, prova de presença, avaliação do local e
sincronização social.

## Referências

- [Place IDs](https://developers.google.com/maps/documentation/places/web-service/place-id?utm_campaign=gmp_git_agentskills_v1)
- [Room](https://developer.android.com/training/data-storage/room?utm_campaign=gmp_git_agentskills_v1)

