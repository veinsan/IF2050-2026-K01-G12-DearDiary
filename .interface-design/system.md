# DearDiary — Interface Design System

Extracted from `src/main/resources/dedi/view/login.fxml` and `src/main/resources/dedi/css/login.css`.
Stack: JavaFX 24 (FXML + JavaFX CSS — `-fx-*` properties, not standard CSS).

## Spacing

- Base: 4px
- Scale: 4, 8, 12, 16, 20, 24, 32
- Outer panel padding: 56–64px

## Radius

- Controls (fields, buttons): **10px**
- Containers (cards, panels): **20px**

Split-card panels use directional radius:
`20px 0 0 20px` (left), `0 20px 20px 0` (right).

## Depth

Strategy: **hybrid** — borders on inputs, shadows on cards/buttons.

| Token       | `-fx-effect`                                              |
| ----------- | --------------------------------------------------------- |
| Card shadow | `dropshadow(gaussian, rgba(0,0,0,0.45), 40, 0, 0, 14)`    |
| Focus glow  | `dropshadow(gaussian, rgba(30,58,138,0.18), 6, 0, 0, 0)`  |
| Hover lift  | `dropshadow(gaussian, rgba(30,58,138,0.35), 10, 0, 0, 3)` |

## Patterns

### Primary button
- Padding: `13px 16px`
- Radius: `10px`
- Font: `13px` bold
- Fill: `#1e3a8a` → hover `#1e40af` (with hover-lift shadow) → pressed `#172554`
- Cursor: `hand`

### Form field (TextField, PasswordField)
- Padding: `12px 14px`
- Radius: `10px`
- Border: `1.5px solid #e2e8f0` → hover `#cbd5e1` → focus `#1e3a8a` + focus glow
- Background: `#f8fafc` → focus `#ffffff`
- Font: `13px`, prompt `#94a3b8`

### Card
- Radius: `20px`
- Surface: `#ffffff`
- Effect: card shadow (above)

### Split-panel layout
- HBox container, max `880 × 560`
- Brand panel (left): navy gradient, `prefWidth 440`, padding `60 56`
- Form panel (right): white, `prefWidth 440`, padding `60 64`
- Inner form column: `maxWidth 312`

## Color

| Role               | Token                                                |
| ------------------ | ---------------------------------------------------- |
| Primary            | `#1e3a8a`                                            |
| Primary hover      | `#1e40af`                                            |
| Primary pressed    | `#172554`                                            |
| Brand panel bg     | `linear-gradient(to bottom right, #1e3a8a, #0f172a)` |
| App background     | `linear-gradient(to bottom right, #0b1220, #111c34)` |
| Text — primary     | `#0f172a`                                            |
| Text — secondary   | `#475569`                                            |
| Text — muted       | `#64748b`                                            |
| Text — placeholder | `#94a3b8`                                            |
| Border — default   | `#e2e8f0`                                            |
| Border — hover     | `#cbd5e1`                                            |
| Surface — base     | `#ffffff`                                            |
| Surface — subtle   | `#f8fafc`                                            |
| Danger             | `#dc2626`                                            |

## Typography

- Family: `"Segoe UI", "SF Pro Text", "Helvetica Neue", sans-serif`
- Weights: 400 default, 700 emphasis

| Use                           | Size | Weight |
| ----------------------------- | ---- | ------ |
| Field label, brand footer     | 11   | 700/400 |
| Error message                 | 12   | 400    |
| Body, controls, subtitle      | 13   | 400    |
| Hero subtitle                 | 14   | 400    |
| Brand mark                    | 18   | 700    |
| Section title (h2)            | 26   | 700    |
| Hero title                    | 38   | 700    |

## Notes for new screens

- Reuse `.form-field` and `.primary-button` style classes — don't add new field/button variants without updating this file.
- DB-facing identifiers stay in Indonesian (per `CLAUDE.md`); UI copy is English.
- KNF01: no remote-network UI states (offline banners, sync indicators) — the app is local-only.
