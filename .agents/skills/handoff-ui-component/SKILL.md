---
name: handoff-ui-component
description: Criar handoffs de componentes visuais a partir de dados extraídos de qualquer fonte, incluindo Figma MCP, XML, screenshots, documentação ou código existente. Usar quando for necessário analisar uma fonte de design e produzir a especificação do componente no template da skill, salvando o resultado em `.handoff/handoff-NOME-DO-COMPONENTE.md` na raiz do projeto.
---

# Handoff de componente visual

Produzir um handoff completo e agnóstico de plataforma para um componente
visual, descrevendo o que deve ser implementado e não uma implementação
específica em Kotlin, Swift, React Native ou outra tecnologia.

## Fluxo obrigatório

1. Confirmar a raiz do projeto compartilhado. A saída deve ficar em
   `.handoff/`, nunca dentro da pasta da skill ou em um diretório genérico de
   handoffs.
2. Identificar a fonte de informação e preservar sua referência. A fonte pode
   ser um MCP do Figma, arquivo XML, screenshot, documentação, código,
   especificação textual ou qualquer combinação dessas fontes.
3. Ler integralmente [`references/extract-data.md`](./references/extract-data.md)
   e aplicar suas regras de extração.
4. Ler integralmente [`references/template.md`](./references/template.md) e
   usar suas seções como o formato final do documento.
5. Extrair somente informações sustentadas pela fonte. Registrar lacunas,
   ambiguidades e dados não observáveis sem inventar valores ou comportamentos.
6. Preencher o template com as informações extraídas. Manter o handoff
   orientado ao comportamento, visual, interação, estados, dimensões,
   acessibilidade e critérios de construção; não transformar o documento em
   código ou em uma API de uma plataforma específica.
7. Normalizar o nome do componente para `kebab-case`, sem o prefixo
   `handoff-`, e criar ou atualizar exatamente o arquivo
   `.handoff/handoff-<nome-do-componente>.md` na raiz do projeto. Exemplo:
   `SextouSearchBar` gera `.handoff/handoff-sextou-search-bar.md`.
8. Validar que o arquivo contém todas as seções do template, a fonte está
   identificada, não restaram placeholders de instrução e o caminho do
   arquivo está correto.

## Regras de extração

- Tratar Figma MCP, XML, screenshots, documentação, código e outras fontes
  como entradas equivalentes; adaptar a leitura ao formato disponível.
- Usar tokens quando a fonte informar tokens ou quando o valor vier de um
  tema. Não converter um valor visual observado em uma decisão de implementação.
- Separar properties, styles, accessibility, layouts, states, constraints,
  anatomy, description, animation e build conforme o template.
- Cobrir as combinações relevantes de layouts, estados e styles. Se uma seção
  não se aplicar, escrever exatamente `Não se aplica`.
- Não criar properties para styles, callbacks ou acessibilidade quando a
  informação já pertence a uma seção própria.
- Expressar as medidas do handoff em pixels e, quando necessário, registrar a
  unidade original da fonte junto da evidência.
- Descrever fontes e evidências usadas, distinguindo fatos observados de
  inferências necessárias.

## Contrato do arquivo de saída

- Diretório: `.handoff/` na raiz do projeto.
- Nome: `handoff-<nome-do-componente>.md`.
- `<nome-do-componente>`: nome estável do componente em `kebab-case`.
- Formato: exatamente as seções e a ordem de
  [`references/template.md`](./references/template.md), sem manter blocos de
  instrução, placeholders ou nomes de template não resolvidos.
- Ao atualizar um componente existente, editar apenas o handoff correspondente
  e preservar os demais arquivos da pasta `.handoff`.

## Integração com a implementação

Entregar o handoff para a skill de componentes visuais
[`android-design-system-components`](../create-ui-component/SKILL.md) quando a
próxima etapa for planejar ou implementar o componente Android. O handoff não
autoriza mudanças fora do componente solicitado e não substitui as regras
específicas do módulo `design-system`.
