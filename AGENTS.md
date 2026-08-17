# Instruções do projeto Sextou

## Regras obrigatórias

Antes de planejar, implementar ou revisar uma mudança não trivial, leia as
regras aplicáveis em `.codex/rule/`:

- `.codex/rule/rule-general.md` para convenções gerais do projeto;
- `.codex/rule/rule-architecture.md` para mudanças de arquitetura ou código;
- `.codex/rule/rule-git.md` para operações Git.

Use os documentos de produto em `.codex/` somente quando forem relevantes ao
escopo da tarefa. Não trate documentos de planejamento como autorização para
implementar trabalho fora do pedido do usuário.

## Skills locais

As skills deste repositório ficam em `.agents/skills/`. Consulte a descrição
do `SKILL.md` para escolher a skill que corresponde à tarefa:

- [architecture](.agents/skills/architecture/SKILL.md): criar e manter o app
  Android usando MVVM, Clean Architecture e Jetpack Compose;
- [manage-sextou-tasks](.agents/skills/manage-sextou-tasks/SKILL.md): gerenciar
  Issues e o board do GitHub Project do Sextou.
- [create-ui-component](.agents/skills/create-ui-component/SKILL.md):
  planejar e implementar componentes visuais reutilizáveis em Kotlin com
  Jetpack Compose, incluindo arquitetura do componente, defaults, estados,
  acessibilidade e previews.
- [handoff-ui-component](.agents/skills/handoff-ui-component/SKILL.md): extrair
  informações de fontes visuais ou técnicas e produzir handoffs de componentes
  no formato padronizado em `.handoff/`.
- [design-system](.agents/skills/design-system/SKILL.md): criar e manter o
  módulo compartilhado de tokens, tema e componentes Jetpack Compose; use-a
  junto da skill de componentes quando a mudança também afetar o módulo
  `design-system` do Sextou.

### Como usar uma skill local

1. Identifique a skill pela descrição e pelo escopo da tarefa. Se o usuário
   mencionar uma skill explicitamente, ela deve ser usada.
2. Leia integralmente o `SKILL.md` antes de planejar, implementar ou revisar a
   mudança. Não aplique uma skill apenas com base no nome ou no resumo.
3. Resolva referências relativas a partir da pasta da skill. Leia os arquivos
   em `references/` somente quando forem necessários para a tarefa e prefira
   executar ou adaptar scripts fornecidos em `scripts/` em vez de reescrever
   procedimentos determinísticos.
4. Quando mais de uma skill se aplicar, use somente o conjunto mínimo
   necessário, informe a ordem de uso e resolva eventuais conflitos pela
   especificidade da skill local e pelas instruções do usuário.
5. A skill orienta o trabalho dentro do escopo pedido; não autoriza mudanças
   adicionais. Preserve arquivos e alterações existentes que não pertençam à
   tarefa.
6. Se uma skill necessária estiver ausente, incompleta ou não puder ser lida,
   registre o bloqueio brevemente e continue com a alternativa mais segura.
7. Se a aplicação da skill produzir uma decisão relevante ou uma alteração
   material, registre a decisão em `assets/agent-decision.md` conforme a regra
   geral do projeto.

### Precedência da skill de arquitetura

Ao executar tarefas de arquitetura:

1. Use a skill local `architecture` como fonte principal das convenções e
   decisões deste projeto.
2. Consulte uma skill global de arquitetura apenas quando a skill local não
   tratar do assunto necessário.
3. Em caso de divergência, priorize a regra específica da skill local.

## Design de interface

Se `DESIGN.md` existir, leia-o integralmente antes de planejar ou executar
qualquer tarefa de UI e siga suas definições durante a implementação.
