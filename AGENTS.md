# Instruções do projeto Sextou

## Regras obrigatórias

Antes de planejar, implementar ou revisar uma mudança não trivial, leia as
regras aplicáveis em `.github/rule/`:

- `.github/rule/rule-general.md` para convenções gerais do projeto;
- `.github/rule/rule-architecture.md` para mudanças de arquitetura ou código;
- `.github/rule/rule-git.md` para operações Git.

Use os documentos de produto em `.github/` somente quando forem relevantes ao
escopo da tarefa. Não trate documentos de planejamento como autorização para
implementar trabalho fora do pedido do usuário.

## Skills locais

As skills deste repositório ficam em `.agents/skills/`. Quando uma tarefa
corresponder à descrição de uma skill, leia integralmente o respectivo
`SKILL.md` antes de agir e siga suas instruções.

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
