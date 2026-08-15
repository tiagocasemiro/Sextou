# Subagentes de features do Sextou

Cada arquivo desta pasta representa um subagente de planejamento para uma
feature descrita em `.codex/features.md`. O arquivo é um prompt operacional:
ao ser executado, o subagente deve transformar o briefing em uma task de board
sem inventar decisões de interface que ainda não foram desenhadas.

## Contrato de saída para o board

O subagente deve devolver uma Issue contendo:

1. **Título:** verbo no infinitivo + resultado de negócio.
2. **Contexto e objetivo:** problema do usuário e valor entregue.
3. **Escopo funcional:** comportamento observável, regras e estados de dados.
4. **Critérios de aceite:** cenários testáveis, incluindo vazio, erro,
   indisponibilidade e offline quando aplicável.
5. **Dependências:** outras features, permissões, APIs ou migrações.
6. **Domínio:** modelos, invariantes e UseCases esperados.
7. **Dados:** contratos `Repository`, fonte remota/local/cache e política de
   atualização.
8. **Fora de escopo:** especialmente decisões visuais e integrações futuras.
9. **Referências:** links oficiais do Google Maps Platform e links do projeto.

## Regras compartilhadas

- Usar Places API/SDK for Android (New) e Maps SDK for Android (New); não usar
  APIs legadas de Places, Directions ou Distance Matrix.
- Solicitar somente os campos necessários por caso de uso. `Place ID` é o
  vínculo do produto com o Google; dados próprios do Sextou pertencem à base
  local/remota do produto.
- Tratar campo ausente como `UNKNOWN`/“não informado”, nunca como `NO`.
- Não apresentar dados do Google como opinião do Sextou. Exibir atribuição e
  links exigidos pelo produto Google quando o conteúdo aparecer.
- O cache de conteúdo do Google deve obedecer aos termos vigentes; `placeId`
  não deve ser confundido com cache livre de todos os atributos do lugar.
- Seguir MVVM + Clean Architecture + Compose: `View → ViewModel → UseCase →
  Repository`; `Room` e entidades locais ficam em um módulo `local` Android
  Library, e gateways do Google ficam em `networking`.
- Toda regra de domínio deve ter testes unitários independentes de Android,
  rede e banco. Cada teste deve verificar um comportamento.
- A UI deve ser descrita por estados e ações, não por layout, cores, ícones ou
  composição final.

## Mapa de subagentes

| Subagente | Feature |
| --- | --- |
| `01-listagem-estabelecimentos.md` | Listagem de Estabelecimentos |
| `02-mapa-interativo.md` | Visualização em Mapa Interativo |
| `03-visualizacao-cardapio.md` | Visualização do Cardápio |
| `04-nota-estabelecimento.md` | Nota do Estabelecimento |
| `05-faixa-preco.md` | Faixa de Preço |
| `06-favoritos.md` | Favoritar Estabelecimentos |
| `07-quero-ir.md` | Lista para Conhecer |
| `08-ja-visitei.md` | Já Visitei o Estabelecimento |
| `09-lista-negra.md` | Não Voltar no Estabelecimento |
| `10-tags-entretenimento.md` | Filtros Rápidos de Entretenimento |
| `11-rota-automatica.md` | Rota Automática para o Rolê |
| `12-aberto-agora.md` | Alerta “Tá Aberto agora?” |
| `13-notas-pessoais.md` | Notas e Anotações Pessoais |

