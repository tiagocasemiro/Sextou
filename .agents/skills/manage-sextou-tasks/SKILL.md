---
name: manage-sextou-tasks
description: Gerenciar as tasks do projeto Sextou no GitHub usando Issues e o GitHub Project correspondente. Usar ao listar, pesquisar, criar, comentar, editar, atribuir, rotular, fechar ou reabrir issues; normalizar tipos e títulos de tasks; adicionar issues ao board; consultar campos do board; ou mover cards entre Backlog, Read to work, In Progress, Validation, Wait publish e Done.
---

# Gerenciar tasks do Sextou

Tratar cada task como uma Issue de `tiagocasemiro/Sextou` vinculada ao GitHub Project `Sextou` nº 4.

Antes de qualquer operação, ler [references/github-task-operations.md](references/github-task-operations.md). Usar os identificadores registrados ali como ponto de partida, mas consultar o GitHub novamente se uma operação falhar ou se campos/opções puderem ter mudado.

## Tipos e títulos das tasks

Usar somente estes tipos de task: `Épico`, `História`, `Tarefa` e `Bug`.
O tipo deve aparecer como prefixo do título, no formato `<Tipo> - <Título>`:

```text
Épico - Feed principal
História - Filtrar estabelecimentos por categoria
Tarefa - Criar o repositório de favoritos
Bug - Corrigir persistência da tabela de favoritos
```

Seguir a semântica padrão do Jira ao escolher o tipo:

- `Épico`: iniciativa ou entrega ampla, normalmente uma funcionalidade
  significativa, composta por várias `Histórias`, `Tarefas` ou `Bugs`. Usar
  para agrupar um objetivo maior; não usar para uma atividade isolada.
- `História`: funcionalidade, necessidade ou trabalho de desenvolvimento
  expressado como um objetivo do usuário e que entrega valor diretamente para
  ele. Preferir a perspectiva “como usuário, quero..., para...”.
- `Tarefa`: trabalho específico que precisa ser feito, especialmente uma
  atividade técnica, investigação, configuração ou trabalho administrativo que
  não precise ser descrito como objetivo do usuário.
- `Bug`: problema existente que prejudica ou impede uma função do produto,
  como comportamento incorreto, funcionalidade quebrada ou discrepância de
  interface. Não usar para solicitar uma capacidade nova.

Para escolher entre os tipos, usar esta ordem: trabalho amplo que será
dividido em vários itens → `Épico`; objetivo de usuário ou nova capacidade →
`História`; ação técnica/administrativa independente → `Tarefa`; defeito em
algo que deveria funcionar → `Bug`. A semântica segue o Jira, mas o Sextou
registra o tipo somente no prefixo porque o repositório não possui tipos nativos
de Issue habilitados.

O prefixo é a forma canônica de representar o tipo no Sextou e não deve ser
substituído por um tipo nativo do GitHub. Ao criar uma task, identificar o tipo
antes de pesquisar duplicatas e criar a Issue. Se o tipo não for informado,
solicitar essa informação antes da criação. Se o título já tiver um prefixo
canônico, não duplicá-lo; ao editar o título, preservar o prefixo atual salvo
quando o usuário pedir explicitamente a mudança de tipo.

## Entrada padrão no board

`Backlog` é o ponto de entrada de toda Issue no board. Ao adicionar uma Issue
ao Project sem uma instrução explícita de coluna, definir `Status: Backlog` e
confirmar a mudança por leitura independente. Nunca depender somente do padrão
automático do GitHub Project. Usar outra coluna apenas quando o usuário
informar explicitamente a coluna de entrada.

## Estrutura do board

Usar o fluxo canônico:

```text
Backlog → Read to work → In Progress → Validation → Wait publish → Done
```

As descrições configuradas no board definem o significado de cada coluna:

- `Backlog`: “Tarefa antes da analise de negócio e técnica.” É o ponto de entrada para uma task que ainda precisa ser analisada e refinada.
- `Read to work`: “tarefas já refinadas e prontas para iniciar o trabalho.” Mover para cá somente quando as análises necessárias terminarem e a execução estiver clara.
- `In Progress`: “Tarefas em progresso”. Usar enquanto o trabalho direto da task estiver sendo executado.
- `Validation`: “Validação do trabalho feito na tarefa”. Usar quando a implementação terminou e precisa ser conferida.
- `Wait publish`: “Aguardando publicação na playstore ou qualquer conclusão que não envolva o trabalho direto no app.” Usar depois da validação, quando restar publicação ou uma conclusão externa ao desenvolvimento.
- `Done`: “Tarefa concluida”. Usar somente quando a task estiver completamente concluída; neste Project, essa mudança fecha a Issue como `completed` por automação.

Usar sempre os nomes canônicos acima nas chamadas MCP. Interpretar solicitações equivalentes em português e normalizá-las para esses nomes, sem inventar opções alternativas. Fazer transições regressivas ou saltos somente quando forem pedidos explicitamente; se a validação exigir ajustes, retornar a `In Progress` e verificar o estado da Issue separadamente.

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
6. Se o usuário especificar uma coluna, consultar `list_project_fields` e alterar o campo `Status`. Se não especificar, alterar explicitamente para `Backlog`, o ponto de entrada padrão do board.
7. Após mudar o status, aguardar a mutação responder e fazer leituras independentes com `list_project_items` e `issue_read/get`; automações do Project podem alterar a Issue depois da resposta inicial.
8. Entregar os links, o status do board e o estado final da Issue.

## Regras de mutação

- Preservar exatamente título e descrição fornecidos, salvo pedido de revisão textual.
- Não criar outra Issue quando uma etapa posterior falhar. Localizar a Issue já criada e retomar a vinculação/edição.
- Tratar o status do board e o estado da Issue como campos distintos, considerando a automação existente:
  - mover para `Done` fecha automaticamente a Issue como `completed` neste Project;
  - não presumir que mover para `Backlog`, `Read to work`, `In Progress`, `Validation` ou `Wait publish` reabra a Issue, pois esse comportamento ainda não foi confirmado;
  - não presumir que fechar a Issue mova o card para `Done`; verificar ambos os estados e informar todas as mudanças observadas.
- Interpretar “mover para conclusão/concluído” como `Status: Done`; avisar que isso também fechará a Issue pela automação atual.
- Interpretar “pronto para trabalhar” como `Status: Read to work`, “em andamento” como `Status: In Progress`, “validar” como `Status: Validation` e “aguardando publicação” como `Status: Wait publish`.
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
