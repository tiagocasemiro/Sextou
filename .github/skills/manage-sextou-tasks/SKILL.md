---
name: manage-sextou-tasks
description: Gerenciar as tasks do projeto Sextou no GitHub usando Issues e o GitHub Project correspondente. Usar ao listar, pesquisar, criar, comentar, editar, atribuir, rotular, fechar ou reabrir issues; adicionar issues ao board; consultar campos do board; ou mover cards entre Todo, In Progress e Done.
---

# Gerenciar tasks do Sextou

Tratar cada task como uma Issue de `tiagocasemiro/Sextou` vinculada ao GitHub Project `Sextou` nº 4.

Antes de qualquer operação, ler [references/github-task-operations.md](references/github-task-operations.md). Usar os identificadores registrados ali como ponto de partida, mas consultar o GitHub novamente se uma operação falhar ou se campos/opções puderem ter mudado.

## Estrutura do board

Usar o fluxo `Todo → In Progress → Done`:

- `Todo`: task ainda não iniciada. Interpretar também “to do” e “a fazer”.
- `In Progress`: task em execução. Interpretar também “Do”, “Doing” e “em andamento”.
- `Done`: task concluída. Interpretar também “conclusão”, “concluída” e “finalizada”; neste Project, essa mudança fecha a Issue como `completed` por automação.

Usar sempre os nomes canônicos `Todo`, `In Progress` e `Done` nas chamadas MCP. Fazer transições regressivas somente quando forem pedidas explicitamente e verificar separadamente se o estado aberto/fechado da Issue mudou.

## Escolher o acesso

1. Preferir as ferramentas nativas do MCP GitHub quando estiverem registradas na sessão.
2. Se elas não aparecerem, executar `scripts/github_mcp_call.sh <ferramenta> '<argumentos-json>'` a partir desta pasta.
3. Nunca imprimir, persistir ou inserir o PAT em comandos, arquivos, logs ou respostas. O script usa `GITHUB_PAT_TOKEN` e possui fallback para o ambiente do gerenciador de usuário.
4. Exigir os toolsets `default,projects`; sem `projects`, não tentar manipular o board por outros meios improvisados.

## Fluxo obrigatório

1. Confirmar o alvo pelo remote Git quando a solicitação vier deste workspace. O esperado é `tiagocasemiro/Sextou`.
2. Executar `get_me` antes da primeira operação para confirmar a identidade autenticada.
3. Para criação, pesquisar primeiro por título/termos equivalentes com `search_issues` e evitar duplicatas exatas ou semanticamente óbvias.
4. Criar a Issue com `issue_write` e capturar seu número.
5. Adicionar a Issue ao Project nº 4 com `projects_write/add_project_item`.
6. Se o usuário especificar uma coluna, consultar `list_project_fields` e alterar o campo `Status`. Se não especificar, manter o padrão do board.
7. Após mudar o status, aguardar a mutação responder e fazer leituras independentes com `list_project_items` e `issue_read/get`; automações do Project podem alterar a Issue depois da resposta inicial.
8. Entregar os links, o status do board e o estado final da Issue.

## Regras de mutação

- Preservar exatamente título e descrição fornecidos, salvo pedido de revisão textual.
- Não criar outra Issue quando uma etapa posterior falhar. Localizar a Issue já criada e retomar a vinculação/edição.
- Tratar o status do board e o estado da Issue como campos distintos, considerando a automação existente:
  - mover para `Done` fecha automaticamente a Issue como `completed` neste Project;
  - não presumir que mover para `Todo` ou `In Progress` reabra a Issue, pois esse comportamento ainda não foi confirmado;
  - não presumir que fechar a Issue mova o card para `Done`; verificar ambos os estados e informar todas as mudanças observadas.
- Interpretar “mover para conclusão/concluído” como `Status: Done`; avisar que isso também fechará a Issue pela automação atual.
- Interpretar “mover para Do/Doing” como `Status: In Progress`, nunca como um valor literal inexistente no GitHub.
- Ao fechar uma Issue, enviar `state_reason`: usar `completed` para trabalho concluído, `not_planned` para cancelamento e `duplicate` apenas com a Issue original identificada.
- Pedir confirmação antes de excluir uma Issue do Project ou realizar outra ação destrutiva que não esteja explícita no pedido.
- Paginar completamente listagens quando o usuário pedir todos os itens.
- Para status, preferir `updated_field` por nome (`{"name":"Status","value":"In Progress"}`); usar IDs apenas quando necessário.

## Operações

Consultar a referência para os argumentos prontos das operações:

- leitura e pesquisa de tasks;
- criação e vinculação ao board;
- comentários e respostas;
- edição de título, descrição, responsáveis, labels, milestone e estado;
- leitura e alteração de `Status`;
- remoção do board e tratamento de falhas parciais.

Usar o script somente como transporte MCP. Tomar decisões de produto e validar resultados no fluxo desta skill.
