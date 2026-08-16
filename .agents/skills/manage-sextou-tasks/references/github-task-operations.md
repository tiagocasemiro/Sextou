# Referência das tasks no GitHub

## Contexto fixo

- Usuário autenticado esperado: `tiagocasemiro`
- Repositório: `tiagocasemiro/Sextou`
- Remote Git esperado: `git@github.com:tiagocasemiro/Sextou.git`
- Board: GitHub Project `Sextou`
- Project owner: `tiagocasemiro` (`owner_type: user`)
- Project number: `4`
- Project database ID: `25160942`
- Project node ID: `PVT_kwHOAFvq3c4Bf-zu`
- Project URL: `https://github.com/users/tiagocasemiro/projects/4`
- Repository URL: `https://github.com/tiagocasemiro/Sextou`
- MCP remoto: `https://api.githubcopilot.com/mcp/`
- MCP toolsets necessários: `default,projects`

Não gravar o valor de `GITHUB_PAT_TOKEN` nesta skill. As permissões verificadas em 2026-08-15 foram `repo`, `project` e `admin:repo_hook`.

## Modelo de dados

Uma task é uma Issue de `tiagocasemiro/Sextou`. O card do board é um item do Project que referencia essa Issue. Portanto:

- título, descrição, comentários, labels, responsáveis, milestone e estado aberto/fechado pertencem à Issue;
- coluna/status pertence ao campo `Status` do item no Project;
- o identificador da Issue (`issue_number`) difere do identificador do item (`item_id`/`node_id`).

## Tipo e título da task

Os tipos válidos são `Épico`, `História`, `Tarefa` e `Bug`. O tipo não é
enviado como tipo nativo do GitHub; ele deve ser representado no início do
título usando o formato canônico:

```text
<Tipo> - <Título>
```

Exemplo: uma task do tipo `Épico` com o título-base `Feed principal` deve ser
criada como `Épico - Feed principal`.

### Orientação Jira para escolher o tipo

- `Épico`: iniciativa ou entrega ampla, geralmente uma funcionalidade
  significativa composta por várias histórias, tarefas ou bugs. Deve agrupar
  trabalho relacionado a um objetivo maior.
- `História`: funcionalidade ou trabalho de desenvolvimento expresso como um
  objetivo do usuário e com valor direto para ele. Quando aplicável, escrever
  o título-base a partir de “como usuário, quero..., para...”.
- `Tarefa`: trabalho específico a executar, como atividade técnica,
  investigação, configuração ou trabalho administrativo. Não precisa expressar
  uma necessidade do usuário.
- `Bug`: problema que prejudica ou impede o funcionamento do produto, incluindo
  comportamento incorreto, funcionalidade quebrada ou discrepância de
  interface. Não classificar uma nova capacidade como `Bug`.

Regra de decisão: iniciativa que será dividida em vários itens é `Épico`;
objetivo de usuário ou capacidade nova é `História`; ação técnica ou
administrativa independente é `Tarefa`; defeito em comportamento existente é
`Bug`. Essa é a semântica do Jira adaptada ao Sextou, que a registra no prefixo
do título porque o GitHub deste repositório não oferece tipos nativos de Issue.

Regras:

- exigir o tipo antes de criar uma task; se ele não vier na solicitação,
  solicitar a informação antes da mutação;
- pesquisar duplicatas usando o título completo com prefixo e também termos
  equivalentes do título-base;
- não duplicar o prefixo quando o título já estiver normalizado;
- ao editar um título, preservar o prefixo atual, exceto quando a mudança de
  tipo for solicitada explicitamente;
- nunca inventar tipos ou alterar a grafia canônica, inclusive os acentos dos
  tipos definidos.

Essa classificação segue as definições de work types do Jira documentadas pela
Atlassian: [tipos de work item](https://support.atlassian.com/jira-software-cloud/docs/set-up-issue-types-in-team-managed-projects/),
[work types e hierarquia](https://support.atlassian.com/jira-cloud-administration/docs/what-are-issue-types/).

## Entrada padrão no board

Toda Issue adicionada ao Project sem uma coluna explicitamente informada deve
entrar em `Backlog`. Depois de `add_project_item`, definir explicitamente
`Status: Backlog` e confirmar o resultado com `list_project_items` e
`issue_read/get`. Não confiar no status padrão automático do GitHub Project.
Uma coluna diferente só pode ser usada quando tiver sido indicada na
solicitação.

Apesar dessa separação, o Project possui uma automação confirmada em 2026-08-15: mover um card para `Done` fecha a Issue vinculada com `state: closed` e `state_reason: completed`. A resposta imediata de `projects_write/update_project_item` pode ainda mostrar `state: open`; verificar depois com chamadas independentes. Não foi confirmado se mover de `Done` para outro status reabre a Issue.

## Campos atuais do Project

Consultar novamente com `projects_list/list_project_fields` antes de depender de IDs em mutações importantes.

| Campo | ID | Tipo |
| --- | ---: | --- |
| Title | `378388630` | title |
| Assignees | `378388631` | assignees |
| Status | `378388632` | single_select |
| Labels | `378388633` | labels |
| Linked pull requests | `378388634` | linked_pull_requests |
| Milestone | `378388635` | milestone |
| Repository | `378388636` | repository |
| Reviewers | `378388637` | reviewers |
| Parent issue | `378388638` | parent_issue |
| Sub-issues progress | `378388639` | sub_issues_progress |

## Estrutura e vocabulário do board

O fluxo canônico, na ordem das colunas do board, é:

```text
Backlog -> Read to work -> In Progress -> Validation -> Wait publish -> Done
```

| Nome canônico | Sinônimos aceitos na solicitação | Option ID | Descrição do board | Uso no fluxo | Efeito confirmado na Issue |
| --- | --- | --- | --- | --- | --- |
| Backlog | `backlog`, `a analisar`, `não refinada` | `ca840eda` | Tarefa antes da analise de negócio e técnica. | Entrada; aguarda análise e refinamento | Nenhum efeito automático confirmado |
| Read to work | `pronta para trabalhar`, `pronto para iniciar`, `refinada` | `f75ad846` | tarefas já refinadas e prontas para iniciar o trabalho. | Task refinada e pronta para execução | Nenhum efeito automático confirmado |
| In Progress | `em andamento`, `em execução`, `doing` | `47fc9ee4` | Tarefas em progresso | Trabalho direto da task em execução | Nenhum efeito automático confirmado |
| Validation | `validação`, `validar`, `em validação` | `97cf6f22` | Validação do trabalho feito na tarefa | Implementação concluída, aguardando conferência | Nenhum efeito automático confirmado |
| Wait publish | `aguardando publicação`, `aguardando playstore`, `aguardando conclusão externa` | `cdead2a3` | Aguardando publicação na playstore ou qualquer conclusão que não envolva o trabalho direto no app. | Validação concluída; aguarda publicação ou conclusão externa ao desenvolvimento | Nenhum efeito automático confirmado |
| Done | `conclusão`, `concluída`, `finalizada`, `concluído` | `98236657` | Tarefa concluida | Task completamente concluída | Fecha como `completed` |

Normalizar sinônimos para o nome canônico antes de chamar `projects_write`. Usar os nomes exatamente como retornados por `list_project_fields`; em particular, preservar `Read to work` e `Wait publish`.

Regras de transição:

- Ao criar e vincular uma task sem coluna solicitada, definir explicitamente `Status: Backlog` e confirmar por leitura; não depender do padrão automático do Project.
- Mover de `Backlog` para `Read to work` quando a análise de negócio e técnica terminar e a task estiver refinada.
- Mover de `Read to work` para `In Progress` quando o trabalho começar.
- Mover de `In Progress` para `Validation` quando o trabalho direto terminar e estiver pronto para conferência.
- Mover de `Validation` para `In Progress` quando a validação solicitar ajustes; registrar a necessidade de retorno antes de fazer a transição.
- Mover de `Validation` para `Wait publish` quando a validação terminar e restar publicação na Play Store ou outra conclusão externa ao trabalho direto no app.
- Mover de `Wait publish` para `Done` quando a publicação ou a conclusão externa terminar; esperar o fechamento automático da Issue.
- Permitir saltos ou movimentos regressivos somente quando solicitados explicitamente.
- Ao sair de `Done`, não presumir reabertura automática da Issue; ler a Issue e informar o resultado.

## Transporte MCP de fallback

Executar a partir da pasta da skill:

```bash
scripts/github_mcp_call.sh get_me '{}'
scripts/github_mcp_call.sh projects_list '{"method":"list_projects","owner":"tiagocasemiro","owner_type":"user","per_page":50}'
```

O segundo argumento deve ser um objeto JSON válido. O script retorna o conteúdo textual da ferramenta; quando esse conteúdo também for JSON, processar com `jq` conforme necessário.

## Ler e pesquisar

Confirmar identidade:

```json
{}
```

Usar com `get_me`.

Pesquisar possíveis duplicatas antes de criar:

```json
{
  "query": "\"TÍTULO\" in:title is:issue",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "perPage": 20,
  "fields": ["number", "title", "body", "state", "html_url", "created_at"]
}
```

Usar com `search_issues`. Comparar título e intenção; não confiar apenas em `total_count`.

Ler uma Issue com `issue_read/get`:

```json
{
  "method": "get",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "issue_number": 123
}
```

Ler comentários com `issue_read/get_comments`:

```json
{
  "method": "get_comments",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "issue_number": 123,
  "perPage": 100
}
```

Inspecionar `tools/list` se o MCP renomear métodos.

Listar cards e seus status:

```json
{
  "method": "list_project_items",
  "owner": "tiagocasemiro",
  "owner_type": "user",
  "project_number": 4,
  "field_names": ["Status"],
  "per_page": 50
}
```

Paginar usando `pageInfo.hasNextPage` e `pageInfo.nextCursor` no argumento `after`. Manter `field_names`, filtros e quantidade por página idênticos.

## Criar uma task

1. Identificar o tipo, normalizar o título para `<Tipo> - <Título>` e pesquisar
   duplicatas pelo título completo e pelo título-base.
2. Criar a Issue com `issue_write`:

```json
{
  "method": "create",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "title": "Épico - TÍTULO",
  "body": "DESCRIÇÃO"
}
```

3. Capturar `number` na resposta. Se o formato da resposta não trouxer esse campo diretamente, pesquisar a Issue recém-criada em vez de criar outra.
4. Adicionar ao board com `projects_write`:

```json
{
  "method": "add_project_item",
  "owner": "tiagocasemiro",
  "owner_type": "user",
  "project_number": 4,
  "item_owner": "tiagocasemiro",
  "item_repo": "Sextou",
  "item_type": "issue",
  "issue_number": 123
}
```

5. Se solicitado, alterar `Status` depois da vinculação.
6. Verificar via `list_project_items` e retornar links da Issue e do Project.

## Comentar

Usar `add_issue_comment` com:

```json
{
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "issue_number": 123,
  "body": "COMENTÁRIO"
}
```

Preservar Markdown. Não editar ou apagar comentários existentes por inferência.
Verificar o comentário com `issue_read/get_comments` e comparar o corpo e a URL retornados.

## Editar uma task

Usar `issue_write` com `method: update`. Enviar somente os campos que devem mudar.
Ao alterar o título, enviar o título completo com o prefixo canônico e manter o
tipo existente, salvo solicitação explícita de mudança de tipo:

```json
{
  "method": "update",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "issue_number": 123,
  "title": "Tarefa - NOVO TÍTULO",
  "body": "NOVA DESCRIÇÃO",
  "assignees": ["tiagocasemiro"],
  "labels": ["LABEL"]
}
```

Omitir chaves que não foram pedidas para evitar sobrescrita. Consultar labels, milestones ou tipos existentes antes de atribuir valores.

Fechar como concluída:

```json
{
  "method": "update",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "issue_number": 123,
  "state": "closed",
  "state_reason": "completed"
}
```

Reabrir:

```json
{
  "method": "update",
  "owner": "tiagocasemiro",
  "repo": "Sextou",
  "issue_number": 123,
  "state": "open"
}
```

## Mudar o status no board

Preferir localizar o item por Issue, sem depender do `item_id` armazenado:

```json
{
  "method": "update_project_item",
  "owner": "tiagocasemiro",
  "owner_type": "user",
  "project_number": 4,
  "item_owner": "tiagocasemiro",
  "item_repo": "Sextou",
  "issue_number": 123,
  "updated_field": {
    "name": "Status",
    "value": "In Progress"
  }
}
```

Valores válidos atuais: `Backlog`, `Read to work`, `In Progress`, `Validation`, `Wait publish` e `Done`. Após atualizar, reler o item incluindo `field_names: ["Status"]`.

Ao mover para `Done`, esperar também `issue_read/get` retornar `state: closed` e `state_reason: completed`, devido à automação atual do Project. Não confiar apenas no objeto retornado pela mutação: ele pode representar o estado da Issue antes da automação. Se a Issue não aparecer fechada na primeira leitura, consultar novamente antes de declarar falha ou sucesso parcial.

Ao mover para `Backlog`, `Read to work`, `In Progress`, `Validation` ou `Wait publish`, reler a Issue e informar se ela permaneceu fechada. Não reabrir automaticamente sem solicitação explícita até que a regra de automação inversa seja conhecida.

Para várias tasks com o mesmo status, preferir `update_project_items` com até 50 entradas em `items` e um único `updated_field` no topo.

## Remover do board

Esta ação não exclui a Issue, apenas o item do Project. Confirmar intenção quando não estiver explícita. Primeiro obter o `item_id` com `list_project_items`; depois usar:

```json
{
  "method": "delete_project_item",
  "owner": "tiagocasemiro",
  "owner_type": "user",
  "project_number": 4,
  "item_id": 123456
}
```

## Recuperação de falhas

- `401/403`: confirmar `get_me`, carregamento de `GITHUB_PAT_TOKEN` e scopes `repo`/`project`.
- Ferramenta `projects_*` ausente: confirmar `X-MCP-Toolsets: default,projects`.
- Issue criada, mas falha ao vincular: pesquisar a Issue recém-criada e executar apenas `add_project_item`.
- Item já existe no board: listar itens e atualizar o existente; não duplicar.
- Campo/opção inválido: executar `list_project_fields` e usar o nome/ID retornado.
- Resposta MCP inesperada: inspecionar `tools/list` e o `inputSchema` da ferramenta, sem adivinhar parâmetros.
- Resultado mutável: verificar sempre com chamadas de leitura independentes antes de declarar sucesso; após `Done`, conferir tanto o Project quanto a Issue por causa da automação e da possível consistência eventual.
