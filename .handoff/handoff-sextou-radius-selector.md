---
component: SextouRadiusSelector
stage: plan
created_at: 2026-09-11
next_agent: create-ui-component
source_type: especificação textual e tema local
source_reference: plano Carregamento diário e seleção de raio; DESIGN.md
---

# Propriedades

- Nome: título e rótulos
- Tipo: texto
- Descrição: título do diálogo, opções, valor personalizado, Cancelar e Buscar.

- Nome: opções
- Tipo: estrutura de dados
- Descrição: lista de opções de seleção única. O mapa fornece 500 m, 1 km, 2 km, 5 km, 10 km e Personalizado.

- Nome: valor e limites
- Tipo: número decimal
- Descrição: valor personalizado, mínimo, máximo e intervalo. O mapa fornece 0,5 a 50 km em intervalos de 0,5 km.

# Styles

Contrato: superfície e conteúdo (cor), título e opções (tipografia), forma (curvatura), separação (espaçamento), controles (cores semânticas Material).
Variante padrão: product-surface-elevated, text-primary, headline-small, body-large, extra-large, spacing.sm e spacing.lg; controles primary/on-primary e estados desabilitados Material. Escolha de papéis inferida de DESIGN.md; não há mockup específico na fonte.

# Acessibilidade

Modal com título, ordem de leitura título → opções → valor e slider → ações.
Grupo de seleção única, cada linha anuncia rótulo e seleção. Slider anuncia valor em km e permite ajuste acessível em passos. Alvos mínimos de 48px; conteúdo rolável, texto sem truncamento e ações alcançáveis com fonte ampliada. Estado desabilitado anunciado pelos controles.

# Layouts

Não se aplica

# States

- Padrão: opção fixa selecionada; slider oculto.
- Personalizado: slider e valor legível visíveis. Primeira abertura no mapa: 3 km.
- Falha de confirmação: mensagem opcional em colors.error dentro do diálogo, permitindo nova tentativa.
- Desabilitado: seleção, ajuste e confirmação bloqueados durante busca; conteúdo preservado.

# Constraints

Largura limitada pela janela modal e altura pelo espaço disponível, sem dimensões totais fixadas pela fonte. Conteúdo rolável em todos os estados. Separação pelo token spacing.sm (8px); contorno do modal e insets seguem Material com tokens Sextou. Linhas de seleção com alvo mínimo de 48px. Valor personalizado acrescenta altura ao conteúdo rolável.

# Anatomy

Sempre visíveis: título, opções e ações Cancelar/Buscar.
Configuráveis: textos, lista de opções e seleção. Slider e valor aparecem ao selecionar Personalizado.
Slots: Não se aplica

# Description

- Developer: componente controlado externamente, sem rede ou persistência; emitir seleção, ajuste, confirmação e cancelamento separadamente.
- Design: superfície tonal escura, texto legível e destaque laranja na seleção e ação primária; adotar comportamento Material para foco e pressão.
- Product: alterações no diálogo são provisórias. Confirmar busca no centro capturado ao abrir; cancelar descarta alterações. Restaurar última seleção confirmada na sessão. O consumidor controla estas regras.

# Animation

Não se aplica

## Build

Entregar componente stateless com Defaults, opções e limites parametrizados, sem modelos de domínio. Previews padrão, personalizado e desabilitado, incluindo tema Sextou. A fonte não define animação própria nem geometria exata do modal: usar primitivas Material e documentar essa inferência em assets/agent-decision.md.
