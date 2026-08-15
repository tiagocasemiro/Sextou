# Subagente — Lista para Conhecer (Quero Ir)

## Missão

Escrever a task que permite guardar lugares desejados para um próximo fim de
semana e consultá-los sem depender de conexão.

## Briefing de negócio

“Quero ir” é intenção futura e pode coexistir com favorito, visita e lista
negra. A operação deve ser pessoal, offline-first e não deve fazer o usuário
perder o item porque os dados do Google estão temporariamente indisponíveis.

## Dados e APIs

- O vínculo externo é somente `placeId` obtido da descoberta/detalhes.
- Domínio: `WantToGo(placeId, addedAt)`; opcionalmente uma referência de
  resumo carregada sob demanda.

## Banco e arquitetura

- `WantToGoEntity(placeId PRIMARY KEY, addedAt)` em `local`, com DAO de
  observação, inserção e remoção.
- `WantToGoRepository.Local`, `ToggleWantToGoUseCase` e
  `ObserveWantToGoUseCase`.
- A lista pode guardar metadados mínimos para contexto, mas deve separar o
  estado do usuário do cache de Places e obedecer os limites de armazenamento.
- Permitir coexistência com favorito; relação com `BlacklistEntity` deve ser
  definida como regra de produto (recomendação: manter o registro, ocultar da
  descoberta enquanto bloqueado).

## Critérios de aceite para a task

- Adicionar/remover é idempotente e disponível sem rede.
- A consulta retorna os IDs em ordem definida pelo produto, por exemplo mais
  recentes primeiro, e não duplica entradas.
- O item continua na lista quando detalhes online falham; o estado de conteúdo
  fica “indisponível/desatualizado”.
- A lista negra não apaga silenciosamente o planejamento pessoal; se a regra
  escolhida for ocultar, ela é explícita e testada.
- Testes cobrem offline, vazio, coexistência e remoção.

## Fora de escopo

Agendamento de lembretes, compartilhamento, reserva e recomendação automática de
fim de semana.

## Referências

- [Place IDs](https://developers.google.com/maps/documentation/places/web-service/place-id?utm_campaign=gmp_git_agentskills_v1)
- [Room](https://developer.android.com/training/data-storage/room?utm_campaign=gmp_git_agentskills_v1)

