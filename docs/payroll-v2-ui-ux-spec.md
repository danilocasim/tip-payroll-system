# Payroll V2 UI/UX Design Spec

> Companion document to:
>
> - `docs/payroll-v2-architecture.md`
> - `docs/payroll-v2-implementation-plan.md`
>
> Purpose: define the visual language, design system, page behavior, and user-facing experience for the V2 admin and employee portals.

---

## 1. Design North Star

The V2 payroll experience should feel:

- **professional** enough for day-to-day business operations
- **sleek and premium** in the way modern Apple-inspired interfaces feel calm, clear, and intentional
- **trustworthy** for payroll and employee data
- **simple to use** for real staff, not just technical users
- **fast to scan** for managers and **easy to understand** for employees

This is not a playful product. It should feel **refined, quiet, and confident**.

---

## 2. Core Visual Principles

### 2.1 Clarity first

- Every page should make the primary task obvious within 3 seconds.
- Visual hierarchy should be strong enough that users know where to look without thinking.
- Data-heavy screens must still feel breathable.

### 2.2 Quiet luxury, not loud enterprise

- Use white space, restrained color, soft shadows, and subtle blur instead of heavy borders and noisy gradients.
- Avoid dashboard clutter, dense outlines, and aggressive contrast blocks.
- Premium comes from restraint.

### 2.3 Apple-inspired, not Apple-copied

- Borrow the **approach**: calm surfaces, precise spacing, large radii, subtle layering, clear typography, and purposeful motion.
- Do **not** mimic Apple marketing pages or rely on decorative effects that hurt usability.

### 2.4 Trust through consistency

- Payroll is sensitive. Numbers, labels, statuses, and system feedback must always look stable and predictable.
- Error states should be respectful and clear, never alarming without reason.

### 2.5 Mobile matters

- Employee flows should feel first-class on mobile.
- Managers will likely work on desktop most often, but admin screens must remain usable on tablets and smaller laptops.

---

## 3. Product Personality

### Admin portal personality

- disciplined
- polished
- efficient
- data-aware
- managerial, not bureaucratic

### Employee portal personality

- reassuring
- straightforward
- human
- calm
- readable

### Shared personality

- modern
- premium
- minimal
- warm rather than cold

---

## 4. Color Direction

The requested visual base is **white + yellow**. To make that professional and accessible, yellow should be used as a **premium accent** and **action color**, while deep graphite handles contrast and readability.

### 4.1 Primary palette

| Token                   | Hex       | Use                                                 |
| ----------------------- | --------- | --------------------------------------------------- |
| `canvas`                | `#FFFCF5` | Global page background, warm white                  |
| `surface`               | `#FFFFFF` | Cards, dialogs, elevated panels                     |
| `surface-tint`          | `#FFF7DB` | Soft highlighted surface, hero glow, selected cards |
| `primary-yellow`        | `#F4C542` | Primary accent, selected states, premium highlights |
| `primary-yellow-strong` | `#E3B11F` | Hover/active primary state                          |
| `primary-yellow-soft`   | `#FFE9A6` | Subtle fills, badges, non-critical highlights       |
| `ink-900`               | `#171717` | Primary text                                        |
| `ink-700`               | `#383838` | Secondary text / icons                              |
| `ink-500`               | `#6B6B6B` | Tertiary text / metadata                            |
| `line`                  | `#EAE4D7` | Dividers, subtle borders                            |
| `line-strong`           | `#D7CFBF` | Stronger field borders / table boundaries           |

### 4.2 Supporting semantic palette

| Token     | Hex       | Use                                             |
| --------- | --------- | ----------------------------------------------- |
| `success` | `#1F8A70` | Confirmed / healthy states                      |
| `warning` | `#C98B12` | Warning states that still fit the yellow family |
| `error`   | `#C64747` | Validation and destructive messaging            |
| `info`    | `#4D7EA8` | Informational callouts                          |

### 4.3 Color rules

- Yellow is **never** used for long-form body text on white.
- Primary yellow buttons must use **dark text**, not white text.
- Main text remains graphite or near-black for trust and legibility.
- Surfaces should feel warm and premium, not stark clinical white.
- High-priority destructive actions should use red, not yellow.

### 4.4 Portal-specific use of color

#### Admin portal

- Base: warm white + white surfaces + graphite text
- Accent: stronger yellow for active nav, filters, chart highlights, primary actions
- Tone: more contrast and structure than employee portal

#### Employee portal

- Base: slightly softer warm white and lighter surfaced cards
- Accent: yellow used more gently for key pay summary cards and highlights
- Tone: calmer and less dense than admin portal

---

## 5. Typography

### 5.1 Recommended font strategy

Use a system-first Apple-inspired sans stack for the cleanest premium feel:

```css
font-family:
  -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Inter",
  "Segoe UI", sans-serif;
```

This keeps the interface close to the Apple-style feel while remaining practical and cross-platform.

### 5.2 Type behavior

- Headings: medium to semibold, never overly bold
- Body: regular with generous line height
- Labels: medium weight
- Numeric payroll values: use **tabular numbers** for alignment

### 5.3 Recommended scale

| Token  | Size | Use                         |
| ------ | ---- | --------------------------- |
| `xs`   | 12px | captions, metadata          |
| `sm`   | 14px | table support text, hints   |
| `base` | 16px | body copy                   |
| `lg`   | 18px | lead text, important labels |
| `xl`   | 20px | card titles                 |
| `2xl`  | 24px | section headings            |
| `3xl`  | 30px | page headings               |
| `4xl`  | 36px | auth hero headline          |

### 5.4 Typography rules

- Keep headings short and controlled.
- Avoid all caps except for tiny overline labels.
- Use concise payroll language; do not sound robotic.
- Amounts should always align cleanly in tables and summary cards.

---

## 6. Spacing, Radius, Shadow, and Depth

### 6.1 Spacing

Use an 8px-based spacing rhythm.

- 8px: tight controls
- 12px: label-to-input spacing
- 16px: internal card spacing (small)
- 24px: card spacing / filter bars
- 32px: section spacing
- 48px+: major page spacing

### 6.2 Radius

| Token         | Value | Use                     |
| ------------- | ----- | ----------------------- |
| `radius-sm`   | 12px  | inputs, buttons         |
| `radius-md`   | 16px  | cards, drawers          |
| `radius-lg`   | 20px  | auth cards, hero panels |
| `radius-pill` | 999px | badges, chips           |

### 6.3 Shadow

Use soft, diffused shadows only.

| Token       | Value                                |
| ----------- | ------------------------------------ |
| `shadow-sm` | `0 2px 10px rgba(23, 23, 23, 0.04)`  |
| `shadow-md` | `0 8px 28px rgba(23, 23, 23, 0.08)`  |
| `shadow-lg` | `0 18px 48px rgba(23, 23, 23, 0.12)` |

### 6.4 Blur and translucency

Use blur sparingly for:

- sticky headers
- sidebar overlays on mobile
- premium auth hero panels

Do not overuse glassmorphism on data-heavy admin screens.

---

## 7. Motion Language

Motion should feel polished and nearly invisible.

### 7.1 Motion principles

- subtle over dramatic
- fast enough to feel responsive
- never delay a task
- motion should reinforce hierarchy and feedback

### 7.2 Recommended durations

- micro interactions: `160ms`
- panel / modal transitions: `200ms`
- page section entrances: `220ms`

### 7.3 Recommended motion patterns

- fade-in + slight translateY on page blocks
- scale `0.98 → 1` on modal/dialog entry
- gentle hover lift on actionable cards
- button press scale `1 → 0.98`
- skeleton shimmer for loading

### 7.4 Motion anti-patterns

- bouncy spring animations on enterprise tasks
- parallax or large hero effects in the app shell
- long loading animations that make the product feel slow

---

## 8. Layout System

### 8.1 Admin shell

#### Structure

- left sidebar on desktop
- top bar with page title, breadcrumbs, and session menu
- content width optimized for data, not marketing-style full bleed
- mobile: sidebar becomes sheet/drawer

#### Visual behavior

- sidebar in soft white with subtle tint and strong active item state
- active nav item uses pale yellow fill + dark text + icon emphasis
- top bar uses slight blur and border for premium depth

#### Why this works

- efficient for repeated workflows
- matches dashboard best practices
- keeps major actions visible without overwhelming the user

### 8.2 Employee shell

#### Structure

- simpler header-first layout
- no heavy admin sidebar density
- stacked summary sections and clear cards
- mobile-first flow with vertical rhythm

#### Visual behavior

- more white space than admin
- fewer simultaneous actions
- content grouped by task rather than by system modules

#### Why this works

- employees should not have to “learn a system” to understand their payroll
- the portal should feel like a calm self-service experience

---

## 9. Page-by-Page UX Specification

## 9.1 Admin Login Page

### Purpose

Authenticate managers into the admin portal with a premium first impression.

### Layout

- split layout on desktop
  - left: login form card
  - right: softly lit brand panel with product promise
- centered stacked card on mobile

### Content

- title: `Manager Portal`
- subtitle: short explanation of access level and payroll responsibility
- email input
- password input with show/hide toggle
- submit button
- inline alert region for invalid credentials and role mismatch

### Visual notes

- background: warm white gradient with very subtle yellow bloom
- card: white surface, 20px radius, medium soft shadow
- primary CTA: yellow button with dark text

### UX notes

- error copy should be direct and respectful
- role mismatch must clearly say this account is not allowed in the manager portal

## 9.2 Employee Login Page

### Purpose

Authenticate employees into self-service with less complexity and more reassurance.

### Layout

- centered card by default
- optional supporting panel on larger screens with payroll reassurance copy

### Content

- title: `Employee Portal`
- short supporting message about viewing payroll and profile data
- email input
- password input
- submit button
- forgot-password link if implemented later

### Visual notes

- softer and lighter than the admin login page
- yellow appears as a gentle accent instead of a stronger enterprise action cue

## 9.3 Admin Dashboard

### Purpose

Help managers immediately understand payroll status and act quickly.

### Structure

1. page heading + breadcrumbs
2. KPI card row
3. quick actions row
4. recent payroll runs or key operational panels
5. employee and payroll management entry points

### KPI cards

- Total Employees
- Current Payroll Total
- Latest Payroll Run Status
- Average Net Pay or another agreed business metric

### Visual rules

- cards are white with subtle borders and shadows
- one featured KPI can use a pale yellow-tinted surface
- charts use restrained accent color, not rainbow dashboards

## 9.4 Admin Employees Page

### Purpose

Manage employee records quickly and confidently.

### Structure

- heading and primary CTA (`Add Employee`)
- search and filter toolbar
- data table
- row actions menu

### Table behavior

- sticky column headers where practical
- compact but readable row height
- right-aligned currency columns
- status badges for employment state
- hover state should be subtle, not heavy gray fills

### Important UX rule

Make actions obvious but never noisy. Use a row menu for secondary actions and a strong primary page CTA for create.

## 9.5 Admin Employee Create/Edit Form

### Purpose

Make a multi-field admin task feel organized and low-friction.

### Structure

- grouped card sections:
  - Personal Information
  - Work Information
  - Compensation
  - Login Access (optional)
- sticky action footer on long forms for desktop

### Form design rules

- two-column layout on desktop when fields pair naturally
- one-column layout on mobile
- helper text below complex fields only
- inline validation, never generic-only form errors

## 9.6 Admin Payroll Runs Page

### Purpose

Let managers create, review, and monitor payroll runs.

### Structure

- page heading + create payroll run CTA
- list/table of runs
- statuses with chips
- filter by pay period and campus scope if needed

### Visual notes

- statuses use restrained semantic color
- current or draft payroll run can use yellow-tinted emphasis

## 9.7 Admin Payroll Run Detail Page

### Purpose

Make payroll review feel trustworthy and reviewable before final action.

### Structure

- header with run metadata and status
- summary cards
- employee payroll records table
- alerts for issues or missing data

### UX rule

Amounts must be easy to compare vertically. Use tabular numbers and strong column alignment.

## 9.8 Employee Dashboard

### Purpose

Give employees immediate confidence that they are in the right place and can quickly see their latest payroll state.

### Structure

- greeting and lightweight header
- latest pay summary card
- recent payroll records list preview
- quick links to profile and payroll history

### Visual notes

- more breathing room than admin dashboard
- summary card can use warm yellow tint very softly
- simplify wording; avoid internal admin terms

## 9.9 Employee Profile Page

### Purpose

Show personal and work profile data clearly.

### Structure

- identity card
- work information card
- account / portal information card

### UX rule

If fields are read-only in MVP, say so clearly rather than making them look editable.

## 9.10 Employee Payroll History Page

### Purpose

Help employees find and understand their payroll records without needing admin support.

### Structure

- page heading
- date filter / pay period filter
- simplified record list or table
- empty state if no records exist

### Visual notes

- prioritize legibility over density
- on mobile, favor stacked card/list layouts over a cramped wide table

## 9.11 Employee Payroll Record Detail Page

### Purpose

Make each payroll record easy to understand at a glance.

### Structure

- pay period header
- breakdown sections:
  - Hours Worked
  - Hourly Rate
  - Gross Pay
  - Bonus
  - Deductions
  - Net Pay

### Visual notes

- net pay gets strongest emphasis
- deductions should be visually distinct but not alarming

---

## 10. Component Design Rules

## 10.1 Buttons

### Primary

- fill: yellow
- text: dark graphite
- shape: 12px radius
- shadow: subtle only

### Secondary

- white or tinted surface
- dark text
- visible but subtle border

### Tertiary

- ghost style for low-priority actions

### Rules

- one dominant primary CTA per screen area
- avoid multiple yellow buttons competing in the same view

## 10.2 Cards

- white surface by default
- 16–20px radius
- thin warm border + soft shadow
- optional tinted header strip only when it aids hierarchy

## 10.3 Inputs and selects

- white background
- subtle line border
- yellow focus ring should be paired with dark outline for clarity
- error state uses red border + descriptive message

## 10.4 Tables

- white surface inside card or section container
- warm dividers, not stark black lines
- hover state uses soft tint, not heavy gray fill
- support horizontal scroll cleanly on narrower screens

## 10.5 Empty states

- friendly tone
- one clear next step
- minimal iconography
- avoid illustrations that feel childish or off-brand

## 10.6 Alerts and banners

- inline alerts should feel calm and readable
- success uses muted green
- warning uses warm amber
- destructive alerts use red sparingly and clearly

---

## 11. Accessibility & Real-User Usability Rules

- WCAG AA minimum contrast for all text and key UI controls
- never rely on yellow alone to communicate meaning
- keyboard access for all navigation, tables, dialogs, and form submission paths
- `aria-invalid`, `aria-describedby`, and semantic labels on forms
- table headers must use semantic `<th scope="col">`
- session-expired and forbidden states should explain what happened and what to do next
- keep employee-facing copy plain and human

### Payroll-specific readability rules

- use comma-separated and fixed-decimal currency formatting
- align figures consistently
- use clear labels like `Net Pay`, `Bonus`, `Deductions`
- avoid unexplained abbreviations in employee portal screens

---

## 12. Design System Package Guidance for Next.js

### Shared in `packages/ui`

- button
- input
- select
- label
- card
- badge
- alert
- dialog
- table primitives
- skeleton
- empty-state
- sidebar shell primitives
- page header primitive

### Keep local to feature folders

- payroll run wizard
- employee create/edit forms
- admin KPI cards
- employee payroll summary cards
- portal-specific nav items and shells

### Why

This preserves a clean design system without turning `packages/ui` into a dumping ground.

---

## 13. Updated Visual Recommendation Summary

### Final recommendation

- **Design style:** Apple-inspired minimal enterprise UI
- **Primary palette:** warm white + premium yellow + graphite
- **Secondary palette:** muted warm neutrals with restrained semantic colors
- **Admin portal:** structured, refined, more data-dense
- **Employee portal:** calmer, lighter, more mobile-first
- **Motion:** subtle and nearly invisible
- **Components:** shadcn/ui foundation with custom token layer

This is the best visual direction for the product because it balances:

- professionalism
- premium look and feel
- ease of use for real staff
- implementation realism in Next.js + Tailwind + shadcn/ui

---

## 14. Implementation impact on existing docs

This document should be treated as the visual source of truth for:

- the `User Interface` section in `docs/payroll-v2-architecture.md`
- the `5.9 UI/UX direction` section in `docs/payroll-v2-implementation-plan.md`
- the design-token work in the future `packages/ui` package

Recommended follow-up:

1. Keep this doc as the canonical design reference.
2. Update architecture and implementation docs to point here.
3. Use this spec to build the Next.js design system before page implementation.
