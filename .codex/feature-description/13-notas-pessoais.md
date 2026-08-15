# Subagente — Notas e Anotações Pessoais

## Missão

Escrever a task que permite registrar lembretes privados associados a um
estabelecimento, para que o usuário preserve contexto útil para a próxima
visita.

## Briefing de negócio

O usuário quer guardar observações próprias — pedido favorito, localização do
parquinho ou alerta de atendimento — sem publicar conteúdo e sem depender do
Google. A nota deve continuar disponível com o lugar offline e ser claramente
separada de reviews, atributos e editorial do estabelecimento.

## Dados e APIs

- Nenhuma API Google Maps é necessária para criar, editar, ler ou apagar a
  nota. O `placeId` obtido anteriormente é apenas a chave de associação.
- Domínio: `UserPlaceNote(id, placeId, text, createdAt, updatedAt)`.
- Regras de negócio: texto não vazio após normalização, limite de tamanho
  definido pelo produto, uma ou várias notas por lugar explicitamente decidido,
  ordenação determinística e timestamps consistentes.

## Banco e arquitetura

- `UserNoteEntity(id PRIMARY KEY, placeId, text, createdAt, updatedAt)` no
  módulo `local`; índice por `placeId` e DAO com `Flow`/operações `suspend`.
- `UserNoteRepository.Local`, `CreateUserNoteUseCase`,
  `UpdateUserNoteUseCase`, `DeleteUserNoteUseCase` e
  `ObserveUserNotesUseCase`.
- A nota não deve ser copiada para request Google, cache de Places, review ou
  qualquer feed público. Criptografia/backup/sincronização ficam como decisão
  de segurança e produto, não presumidas.

## Critérios de aceite para a task

- Criar, editar, consultar e excluir nota funciona totalmente offline.
- Texto vazio/inválido é rejeitado sem gravar alteração parcial.
- Só o usuário local consegue ver a nota dentro do escopo atual do app.
- Atualização conserva o ID e `createdAt`, mudando somente `updatedAt` e texto.
- Perda de dados local gera estado de erro recuperável; não há chamada ao Maps.
- Testes cobrem validação, CRUD, ordenação, associação por `placeId` e vazio.

## Fora de escopo

Login, sincronização em nuvem, colaboração, moderação, análise de sentimento e
publicação de notas.

## Referências

- [Room](https://developer.android.com/training/data-storage/room?utm_campaign=gmp_git_agentskills_v1)
- [Arquitetura do projeto](../features.md)

