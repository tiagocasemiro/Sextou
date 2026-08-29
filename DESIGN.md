---
version: alpha
name: Sextou
description: "Dark-first design language for a fast, approachable nightlife guide. Semantic product roles sit above a documented primitive token scale."
colors:
  # Semantic MD3 roles from the Sextou theme handoff.
  primary: "#FE9A00"
  on-primary: "#000000"
  primary-container: "#7F2D12"
  on-primary-container: "#FFF6EA"
  primary-fixed: "#FFD2A0"
  primary-fixed-dim: "#FFB56D"
  on-primary-fixed: "#2A1700"
  on-primary-fixed-variant: "#6E3E00"
  inverse-primary: "#A85D00"
  secondary: "#FF5722"
  on-secondary: "#000000"
  secondary-container: "#6F2F19"
  on-secondary-container: "#FFDAD0"
  secondary-fixed: "#FFDBCF"
  secondary-fixed-dim: "#FFB5A5"
  on-secondary-fixed: "#3B0A00"
  on-secondary-fixed-variant: "#73341F"
  tertiary: "#00D492"
  on-tertiary: "#002117"
  tertiary-container: "#004D39"
  on-tertiary-container: "#7AF3C4"
  tertiary-fixed: "#8DF5C7"
  tertiary-fixed-dim: "#70D8AD"
  on-tertiary-fixed: "#002117"
  on-tertiary-fixed-variant: "#00513D"
  error: "#FFB4AB"
  on-error: "#690005"
  error-container: "#93000A"
  on-error-container: "#FFDAD6"
  error-fixed: "#FFDAD6"
  error-fixed-dim: "#FFB4AB"
  on-error-fixed: "#410002"
  on-error-fixed-variant: "#733437"
  background: "#111111"
  on-background: "#F2EDE4"
  surface-tint: "#FE9A00"
  surface: "#111111"
  surface-dim: "#101010"
  surface-bright: "#3A3A3A"
  surface-container-lowest: "#0C0C0C"
  surface-container-low: "#1A1A1A"
  surface-container: "#1E1E1E"
  surface-container-high: "#292929"
  surface-container-highest: "#333333"
  on-surface: "#F2EDE4"
  on-surface-variant: "#C8BFB0"
  outline: "#988F80"
  outline-variant: "#4D463C"
  inverse-surface: "#F2EDE4"
  inverse-on-surface: "#33302B"
  scrim: "#000000"
  shadow: "#000000"
  success: "#00D492"
  neutral-muted: "#9A9080"
  text-primary: "#F2EDE4"
  text-secondary: "#9A9080"

  # Product labels shown in the semantic handoff's overview palette.
  product-primary-main: "#FE9A00"
  product-primary-strong: "#FFB900"
  product-brand-accent: "#FFD230"
  product-on-primary: "#000000"
  product-secondary-main: "#FF5722"
  product-secondary-hover: "#FF7043"
  product-secondary-container: "#2A2A2A"
  product-surface-main: "#111111"
  product-surface: "#1C1C1C"
  product-surface-elevated: "#2A2A2A"
  product-surface-variant: "#262626"
  product-surface-container: "#1E1E1E"
  product-success-main: "#00D492"
  product-success-strong: "#00BC7D"
  product-error-main: "#FF5722"
  product-closed-container: "#3F3F47"
  product-closed-indicator: "#9F9FA9"
  product-closed-content: "#D4D4D8"
  product-neutral-muted: "#9A9080"

  # Primitive Orange scale from the granular token handoff.
  orange-50: "#FFF7ED"
  orange-100: "#FFEDD5"
  orange-200: "#FED7AA"
  orange-300: "#FDBA74"
  orange-400: "#FB923C"
  orange-500: "#F97316"
  orange-600: "#EA580C"
  orange-700: "#C2410C"
  orange-800: "#9A3412"
  orange-900: "#7C2D12"
  orange-950: "#431407"

  # Primitive Slate scale from the granular token handoff.
  slate-50: "#F8FAFC"
  slate-100: "#F1F5F9"
  slate-200: "#E2E8F0"
  slate-300: "#CBD5E1"
  slate-400: "#94A3B8"
  slate-500: "#64748B"
  slate-600: "#475569"
  slate-700: "#334155"
  slate-800: "#1E293B"
  slate-900: "#0F172A"
  slate-950: "#020617"

  # Sparse primitive status scales present in the granular token handoff.
  emerald-50: "#ECFDF5"
  emerald-200: "#A7F3D0"
  emerald-500: "#10B981"
  emerald-700: "#047857"
  emerald-900: "#064E3B"
  rose-50: "#FFF1F2"
  rose-200: "#FECDD3"
  rose-500: "#F43F5E"
  rose-700: "#BE123C"
  rose-900: "#881337"
  white: "#FFFFFF"
  black: "#000000"

  # Primitive Orange alpha samples; use them as state layers, not as solid fills.
  alpha-orange-08: "rgba(249, 115, 22, 0.08)"
  alpha-orange-16: "rgba(249, 115, 22, 0.16)"
  alpha-orange-32: "rgba(249, 115, 22, 0.32)"
  alpha-orange-64: "rgba(249, 115, 22, 0.64)"
  alpha-orange-85: "rgba(249, 115, 22, 0.85)"

typography:
  # Semantic app roles use Inter in the semantic/theme handoff.
  brand:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: 900
    lineHeight: 36px
    letterSpacing: -1.8px
  display-large:
    fontFamily: Inter
    fontSize: 57px
    fontWeight: 400
    lineHeight: 64px
  display-medium:
    fontFamily: Inter
    fontSize: 45px
    fontWeight: 400
    lineHeight: 52px
  display-small:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: 400
    lineHeight: 44px
  headline-large:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: 400
    lineHeight: 40px
  headline-medium:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: 400
    lineHeight: 36px
  headline-small:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: 400
    lineHeight: 32px
  title-large:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: 400
    lineHeight: 28px
  title-medium:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: 500
    lineHeight: 24px
  title-small:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 500
    lineHeight: 20px
  body-large:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
  body-medium:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
  body-small:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: 400
    lineHeight: 16px
  label-large:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 500
    lineHeight: 20px
  label-medium:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
  label-small:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: 500
    lineHeight: 16px
  emphasized-display-large:
    fontFamily: Inter
    fontSize: 57px
    fontWeight: 800
    lineHeight: 64px
  emphasized-display-medium:
    fontFamily: Inter
    fontSize: 45px
    fontWeight: 800
    lineHeight: 52px
  emphasized-display-small:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: 800
    lineHeight: 44px
  emphasized-headline-large:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: 800
    lineHeight: 40px
  emphasized-headline-medium:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: 800
    lineHeight: 36px
  emphasized-headline-small:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: 800
    lineHeight: 32px
  emphasized-title-large:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: 700
    lineHeight: 28px
  emphasized-title-medium:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: 700
    lineHeight: 24px
  emphasized-title-small:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 700
    lineHeight: 20px
  emphasized-body-large:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: 500
    lineHeight: 24px
  emphasized-body-medium:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 500
    lineHeight: 20px
  emphasized-body-small:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
  emphasized-label-large:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 600
    lineHeight: 20px
  emphasized-label-medium:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: 600
    lineHeight: 16px
  emphasized-label-small:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: 600
    lineHeight: 16px
  # Primitive specimen roles use Plus Jakarta Sans on the light token sheet.
  primitive-text-xs:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 12px
    fontWeight: 400
    lineHeight: 16px
  primitive-text-sm:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
  primitive-text-base:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
  primitive-text-lg:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 18px
    fontWeight: 400
    lineHeight: 28px
  primitive-text-xl:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 20px
    fontWeight: 400
    lineHeight: 28px
  primitive-text-2xl:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 24px
    fontWeight: 600
    lineHeight: 32px
  primitive-text-3xl:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 30px
    fontWeight: 700
    lineHeight: 36px
  primitive-text-5xl:
    fontFamily: "Plus Jakarta Sans"
    fontSize: 48px
    fontWeight: 800
    lineHeight: 48px

rounded:
  none: 0px
  extra-small: 4px
  small: 8px
  medium: 12px
  large: 16px
  large-increased: 20px
  product-large: 24px
  extra-large: 28px
  extra-large-increased: 32px
  extra-extra-large: 48px
  input: 14px
  search-bar: 16px
  input-action: 10px
  full: 9999px

spacing:
  none: 0px
  micro: 2px
  xs: 4px
  sm: 8px
  md: 12px
  base: 16px
  lg: 24px
  xl: 32px
  xxl: 48px
  xxxl: 64px
  huge: 128px
  "sp-0.5": 2px
  "sp-1": 4px
  "sp-2": 8px
  "sp-3": 12px
  "sp-4": 16px
  "sp-6": 24px
  "sp-8": 32px
  "sp-12": 48px
  "sp-16": 64px
  "sp-32": 128px

components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    typography: "{typography.emphasized-title-medium}"
    rounded: "{rounded.large}"
    padding: "{spacing.md}"
    height: 48px
  button-secondary:
    backgroundColor: "{colors.secondary}"
    textColor: "{colors.on-secondary}"
    typography: "{typography.emphasized-title-medium}"
    rounded: "{rounded.large}"
    padding: "{spacing.md}"
    height: 48px
  button-outline:
    backgroundColor: transparent
    textColor: "{colors.primary}"
    typography: "{typography.emphasized-title-medium}"
    rounded: "{rounded.medium}"
    padding: "{spacing.base}"
    height: 40px
  button-ghost:
    backgroundColor: transparent
    textColor: "{colors.on-surface}"
    typography: "{typography.emphasized-title-medium}"
    rounded: "{rounded.medium}"
    padding: "{spacing.base}"
    height: 40px
  input-text:
    backgroundColor: "{colors.surface-container}"
    textColor: "{colors.on-surface}"
    typography: "{typography.body-large}"
    rounded: "{rounded.input}"
    padding: "{spacing.base}"
    height: 48px
  search-bar:
    backgroundColor: "{colors.product-surface-variant}"
    textColor: "{colors.on-surface}"
    typography: "{typography.body-medium}"
    rounded: "{rounded.search-bar}"
    padding: "{spacing.base}"
    height: 56px
  card:
    backgroundColor: "{colors.surface-container}"
    textColor: "{colors.on-surface}"
    typography: "{typography.body-medium}"
    rounded: "{rounded.large}"
    padding: "{spacing.base}"
  status-open:
    backgroundColor: "{colors.success}"
    textColor: "{colors.on-primary}"
    typography: "{typography.emphasized-label-small}"
    rounded: "{rounded.full}"
    padding: "{spacing.sm}"
    height: 28px
---

# Design System: Sextou

**Project ID:** Figma file `pASFSURvaP2uIMXDZOFNk5`

**Canonical theme:** dark Sextou / MD3 semantic architecture

**Version shown in the handoff:** `v1.0.24`

## Overview

Sextou is a nightlife discovery product: quick, familiar, energetic, and
grounded in the visual language of a Brazilian boteco. The canonical interface
is dark-first. It should feel like a well-lit sign and a cold drink in a
night-time street scene: warm, legible, direct, and inviting rather than
luxury-minimal or futuristic.

The visual vocabulary is built from a near-black canvas, warm ivory text,
sunset orange actions, hot tomato secondary emphasis, and a lively mint-green
success accent. Tonal charcoal surfaces create hierarchy while low-alpha cream
borders keep the interface readable in low light. The result is dense enough
for fast scanning, but never visually noisy.

The source material has two deliberate layers:

- The semantic/theme handoff (`19:785`) is the product language. Prefer its
  roles and descriptions when choosing a value for a screen or component.
- The primitive handoff (`19:1195`) is the atomic scale. Use it to preserve
  rhythm and consistency when a semantic role does not yet exist; do not apply
  the light reference board's white/slate canvas to the product by default.

When a value is not explicitly defined here, choose the nearest existing token
and keep the decision semantic. New one-off colors, radii, spacing values, or
shadows need a documented reason before they become part of the system.

### Source notes

This document follows the Stitch DESIGN.md format and section order described
in the [Stitch DESIGN.md overview](https://stitch.withgoogle.com/docs/design-md/overview).
The semantic/theme source is the [Sextou semantic token handoff in Figma](https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=19-785&t=8ENyNFXeXt7dnf3v-4),
and the primitive source is the [Sextou primitive token handoff in Figma](https://www.figma.com/design/pASFSURvaP2uIMXDZOFNk5/Sextou?node-id=19-1195&t=8ENyNFXeXt7dnf3v-4).

## Colors

The semantic palette is organized as an MD3 dark theme with product-specific
aliases. `primary` is the warm orange reserved for the clearest action or
brand signal. `secondary` is a hotter tomato orange for supporting emphasis.
`tertiary` and `success` are a mint green used for positive status and
confirmation. `background` and `surface` are near-black so the eye can stay on
content, while surface containers step upward through charcoal tones.

Key semantic roles:

- **Sunset Orange (`{colors.primary}`, #FE9A00):** primary actions, brand
  marks, focus accents, and the strongest visual call to action.
- **Brand Gold (`{colors.product-primary-strong}`, #FFB900) and Citrus
  Accent (`{colors.product-brand-accent}`, #FFD230):** high-energy wordmark
  and brand highlights; use sparingly so the primary action stays dominant.
- **Ink (`{colors.on-primary}`, #000000):** text and icon color on the primary
  orange surface.
- **Burnt Orange (`{colors.primary-container}`, #7F2D12):** deeper primary
  container for selected or emphasized states.
- **Hot Tomato (`{colors.secondary}`, #FF5722):** secondary actions, flame
  accents, and energetic supporting highlights.
- **Nightlife Mint (`{colors.tertiary}`, #00D492):** positive feedback,
  availability, and success signals.
- **Mint Strong (`{colors.product-success-strong}`, #00BC7D):** deeper positive
  fills and selected success states.
- **Warm Ivory (`{colors.on-surface}`, #F2EDE4):** primary content on dark
  surfaces; it is softer and more atmospheric than pure white.
- **Muted Taupe (`{colors.text-secondary}`, #9A9080):** metadata, supporting
  copy, placeholders, captions, and low-emphasis navigation labels.
- **Near-black (`{colors.background}`, #111111):** canonical app background and
  the base surface for night-time legibility.
- **Charcoal Tonal Surfaces (`{colors.surface-container}`, #1E1E1E;
  `{colors.surface-container-high}`, #292929; #333333 highest):** containment
  layers for cards, controls, and elevated content without bright panels.
- **Product Surface (`{colors.product-surface}`, #1C1C1C), Product Elevated
  (`{colors.product-surface-elevated}`, #2A2A2A), and Product Image Surface
  (`{colors.product-surface-variant}`, #262626):** app-specific tonal layers
  shown by the component showcase.
- **Soft Coral Error (`{colors.error}`, #FFB4AB):** readable error content in
  the dark theme; use the paired error container roles for error surfaces.
- **Cream Outline (`{colors.outline-variant}`, #4D463C):** quiet separators
  and structural boundaries; use the low-alpha border treatment for subtle
  dividers where appropriate.
- **Closed-state neutrals (`{colors.product-closed-container}`, #3F3F47;
  `{colors.product-closed-indicator}`, #9F9FA9; #D4D4D8 content):** muted
  status treatment for unavailable or closed places.

The semantic handoff also shows a product overview palette with labels that
overlap MD3 names. Those values are preserved under `product-*` tokens in the
front matter. In particular, the product overview's `secondary-container`
swatch is **#2A2A2A**, while the MD3 coverage role `secondary-container` is
**#6F2F19**; the latter remains normative for the MD3 role. Likewise,
product `surface-variant` is **#262626**, distinct from the MD3
`surface-container-high` value **#292929**.

### Primitive color scales

The granular handoff contains the following raw palettes. These values are
building blocks, not automatic semantic assignments:

| Scale | Values |
| --- | --- |
| Orange | `orange-50` #FFF7ED, `orange-100` #FFEDD5, `orange-200` #FED7AA, `orange-300` #FDBA74, `orange-400` #FB923C, `orange-500` #F97316, `orange-600` #EA580C, `orange-700` #C2410C, `orange-800` #9A3412, `orange-900` #7C2D12, `orange-950` #431407 |
| Slate | `slate-50` #F8FAFC, `slate-100` #F1F5F9, `slate-200` #E2E8F0, `slate-300` #CBD5E1, `slate-400` #94A3B8, `slate-500` #64748B, `slate-600` #475569, `slate-700` #334155, `slate-800` #1E293B, `slate-900` #0F172A, `slate-950` #020617 |
| Emerald | `emerald-50` #ECFDF5, `emerald-200` #A7F3D0, `emerald-500` #10B981, `emerald-700` #047857, `emerald-900` #064E3B |
| Rose | `rose-50` #FFF1F2, `rose-200` #FECDD3, `rose-500` #F43F5E, `rose-700` #BE123C, `rose-900` #881337 |
| Alpha samples | Orange at 8%, 16%, 32%, 64%, and 85% opacity (`alpha-orange-08` through `alpha-orange-85`) |

Do not confuse the primitive Orange `#F97316` with the product primary
`#FE9A00`. The former is the granular reference scale; the latter is the
semantic brand role.

## Typography

Semantic app typography uses **Inter**. The handoff demonstrates a heavy,
confident display voice and a highly readable body voice: Extra Bold/Black for
brand and section emphasis, Bold for titles and actions, Regular for long-form
copy, and Semi Bold for compact labels. The brand wordmark is a 36px Black
display treatment with tight `-1.8px` tracking. Small uppercase labels use
generous tracking to remain legible against dark surfaces.

The semantic MD3 scale is available in regular and emphasized variants:

| Role family | Size / line height | Regular | Emphasized |
| --- | --- | --- | --- |
| Display large | 57px / 64px | 400 | 800 |
| Display medium | 45px / 52px | 400 | 800 |
| Display small | 36px / 44px | 400 | 800 |
| Headline large | 32px / 40px | 400 | 800 |
| Headline medium | 28px / 36px | 400 | 800 |
| Headline small | 24px / 32px | 400 | 800 |
| Title large | 22px / 28px | 400 | 700 |
| Title medium | 16px / 24px | 500 | 700 |
| Title small | 14px / 20px | 500 | 700 |
| Body large | 16px / 24px | 400 | 500 |
| Body medium | 14px / 20px | 400 | 500 |
| Body small | 12px / 16px | 400 | 500 |
| Label large | 14px / 20px | 500 | 600 |
| Label medium | 12px / 16px | 500 | 600 |
| Label small | 11px / 16px | 500 | 600 |

Use the display and headline roles for section hierarchy, title roles for
establishment names and action labels, body roles for descriptions and
metadata, and label roles for status, category, and navigation text. Uppercase
is an emphasis treatment for short labels and headings, not a default for
paragraphs.

The primitive sheet previews a separate **Plus Jakarta Sans** scale from 12px
through 48px (`primitive-text-xs` through `primitive-text-5xl`). Treat that
family as the atomic specimen documented by the source board; the semantic
Inter roles take precedence for Sextou product UI. Do not switch the whole app
to Plus Jakarta Sans without an explicit product decision.

## Layout

Use a compact 4px base scale with 2px micro-alignment and an 8px reading
rhythm. The semantic handoff calls out the following practical steps:

| Purpose | Token | Value |
| --- | --- | ---: |
| Micro alignment | `micro` / `sp-0.5` | 2px |
| Compact inset | `xs` / `sp-1` | 4px |
| Base rhythm | `sm` / `sp-2` | 8px |
| Control inset | `md` / `sp-3` | 12px |
| Component inset | `base` / `sp-4` | 16px |
| Section gap | `lg` / `sp-6` | 24px |
| Content gap | `xl` / `sp-8` | 32px |
| Frame padding | `xxl` / `sp-12` | 48px |
| Major section | `xxxl` / `sp-16` | 64px |
| Display separation | `huge` / `sp-32` | 128px |

Prefer full-width content inside a clear container, with 16px component insets
and 24px section separation as the default mobile reading rhythm. Use 32px
gaps when separating groups and 48px or 64px only for frame-level breathing
room. On wider layouts, preserve a readable content column and cap broad
surfaces near the 1200px reference width shown by the token boards instead of
stretching text across the viewport.

The semantic handoff board itself is a 1200px documentation canvas with 48px
outer padding and large 80px section gaps. The primitive board is a light
`#FCFCFC` documentation canvas with 48px padding. These are reference-sheet
layouts, not mandatory product screen backgrounds.

Align related content to a consistent vertical edge. Use whitespace to group
brand, search, results, filters, and bottom navigation; avoid arbitrary
absolute positioning when a responsive row or column expresses the same
relationship.

## Elevation & Depth

Sextou communicates depth primarily through **tonal layers and quiet
borders**, not through persistent heavy shadows. Feed cards use tonal fills
only and explicitly have no shadow. A surface should feel anchored by its
background contrast, a 1px low-alpha outline, and clear spacing before a
shadow is introduced.

The semantic MD3 state/elevation roles are:

- Level 0: `0dp`, tonal surface.
- Level 1: `1dp`.
- Level 2: `3dp`.
- Level 3: `6dp`.
- Level 4: `8dp`.
- Level 5: `12dp`.
- Hover state layer: 8%.
- Focus state layer: 12%.
- Pressed state layer: 12%.
- Dragged state layer: 16%.
- Disabled content: 38%; disabled container: 12%.

The primitive handoff provides shadow samples for surfaces that truly need
separation: `shadow-sm` (`0px 1px 1px rgba(0,0,0,0.05)`), `shadow-md`
(`0px 1px 3px 0px rgba(0,0,0,0.1), 0px 1px 2px -1px rgba(0,0,0,0.1)`),
`shadow-lg` (`0px 10px 15px -3px rgba(0,0,0,0.1), 0px 4px 6px -4px rgba(0,0,0,0.1)`),
`shadow-xl` (`0px 20px 25px -5px rgba(0,0,0,0.1), 0px 8px 10px -6px rgba(0,0,0,0.1)`),
`shadow-2xl` (`0px 25px 50px -12px rgba(0,0,0,0.25)`), and
`custom-max` (`0px 35px 60px -15px rgba(0,0,0,0.3)`). Use them progressively
for transient overlays, dialogs, or floating controls; do not apply the
largest shadow to ordinary cards.

Motion should be quick and intentional. Prefer the semantic handoff's
standard 300ms and emphasized 500ms durations, with short 50/100/150/200ms
and medium 250/300/350/400ms steps as needed. The primary easing curves are:

- Emphasized: `cubic-bezier(0.2,0,0,1)`.
- Emphasized decelerate: `cubic-bezier(0.05,0.7,0.1,1)`.
- Emphasized accelerate: `cubic-bezier(0.3,0,0.8,0.15)`.
- Standard decelerate: `cubic-bezier(0,0,0,1)`.
- Standard accelerate: `cubic-bezier(0.3,0,1,1)`.

## Shapes

The shape language is friendly but controlled: moderately rounded cards and
controls, with fully pill-shaped status and category elements. The semantic
reference highlights 4px, 12px, 24px, and full-radius roles; the MD3 coverage
adds intermediate 8px, 16px, 20px, 28px, 32px, and 48px levels for a complete
scale.

- Use 4px for compact markers and micro geometry.
- Use 8px for small controls and compact groups.
- Use 12px for standard controls, outlines, and medium containers.
- Use 14px for text inputs and 16px for search bars and primary buttons.
- Use 16px to 24px for cards and major surfaces, depending on the component's
  visual weight.
- Use 9999px for pills, status badges, chips, and compact tags.
- Keep a single radius family coherent within one screen; do not mix sharp,
  softly rounded, and pill treatments without a semantic reason.

## Components

Component styling is token-backed and stateless. Prefer established Material 3
behavior for focus, pressed, disabled, ripple, and accessibility semantics,
while applying Sextou's semantic colors and shapes.

### Buttons

Primary buttons use sunset orange with black content and are the strongest
action on the screen. Secondary buttons use hot tomato with black content.
Outline and ghost buttons keep the dark surface visible, use warm ivory or
orange text, and rely on a quiet outline or state layer rather than a filled
panel. The reference sizes are 48px Large, 40px Medium, and 32px Small, with
24px, 20px, and 16px horizontal padding respectively. Button corners are
generously rounded for Large and moderately rounded for compact variants.
Icons are optional, aligned to a 20px visual box, and never replace a useful
text label when the action would otherwise be ambiguous.

### Inputs and search

Inputs live on a charcoal tonal surface, use warm ivory for entered text, and
use muted taupe for placeholders and supporting copy. Text inputs use a 14px
radius and 48px height. Search bars use a 16px radius and 56px height, with
the search affordance and filter action kept visually distinct. Focus is
communicated with the primary orange and a 2px emphasis rather than a large
glow. Error states use the error roles and preserve the same outer geometry.

### Cards and result content

Cards are dark tonal containers with 16px or 24px corners, 16px to 24px inner
padding, and a clear hierarchy between establishment name, status, metadata,
distance, and price. The feed-card rule is explicit: use tonal fills only and
no shadow. Image cards may use a restrained black scrim when text needs to
remain readable, but the image should not become a decorative gradient field.

### Status, navigation, and iconography

Open or positive status uses the mint semantic roles; closed, unavailable, or
secondary states should reduce saturation and contrast without becoming
invisible. Status badges are pill-shaped, compact, and paired with a clear
label rather than color alone. Bottom navigation uses the same near-black
surface family, with the active destination distinguished by orange and the
inactive destinations by muted taupe.

Icons are simple, high-contrast silhouettes or strokes in warm ivory, muted
taupe, orange, or tomato. Keep icon boxes explicit (18px, 20px, 24px, or
28px where the component calls for it) and preserve a minimum 48px interactive
touch target even when the visible glyph is compact. Decorative icons may have
no description; informative or actionable icons need an accessible label.

### Motion behavior

Use state layers for hover, focus, pressed, dragged, and disabled states. Keep
transitions short enough for a nightlife discovery flow: feedback should feel
immediate, while emphasized screen or container changes can use the 500ms
curve described above. Avoid motion that shifts surrounding content or makes
the primary action harder to find.

## Do's and Don'ts

- Do default to the dark semantic theme and preserve warm ivory text against
  near-black surfaces.
- Do reserve the primary orange for the most important action or brand signal
  on a screen.
- Do use semantic roles first and primitive scales second; keep primitives out
  of feature-specific ad hoc styling.
- Do maintain WCAG AA contrast for normal text (4.5:1) and never communicate
  status through color alone.
- Do use tonal contrast, whitespace, and quiet borders before adding a shadow.
- Do keep the 4px/8px spacing rhythm and use the named rounded scale.
- Do keep new UI visually compatible with a fast, friendly Brazilian nightlife
  guide: direct labels, familiar controls, and visible affordances.
- Don't use the primitive Orange `#F97316` as a replacement for semantic
  primary `#FE9A00`.
- Don't use the primitive sheet's light `#FCFCFC` canvas or slate panels as the
  default product theme.
- Don't add decorative neon colors, persistent gradients, glassmorphism, or
  high-contrast shadows that compete with content.
- Don't mix unrelated corner radii, invent one-off spacing, or hardcode colors
  when an existing token expresses the intent.
- Don't place large shadows on feed cards or use uppercase for long-form body
  copy.
- Don't rely on an icon's color alone for meaning, and don't shrink an
  interactive target below 48px just to preserve a compact glyph.
