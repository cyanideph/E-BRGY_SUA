# Plan: Design System Integration Feature

## Plan ID: plan-design-system-001
## Feature: Design System integration — extract color/typography constants to SuaDesignSystem.kt, consistent elevation and spacing
## Priority: P (All user stories)
## Estimated Effort: 2-3 days

## Plan Description
Ensure all UI screens consistently use the existing design system constants (Color.kt, Theme.kt, Type.kt) instead of hardcoded values. This enforces visual consistency across all screens and enables future theming enhancements (dark mode, custom themes).

## Plan Components
1. **Color Token Usage**: Replace hardcoded Color(r, g, b) values with MaterialTheme.colorScheme.* tokens and design system Color.kt values
2. **Typography Token Usage**: Replace custom TextStyle definitions with MaterialTheme.typography.* references
3. **Elevation and Spacing**: Standardize Card elevation, padding, and margin values across all screens
4. **Consistency Verification**: Audit all UI screens for design system compliance

## Plan Components Detail

### Color Token Usage
- Replace Color(0xFF...) hardcodes with MaterialTheme.colorScheme.primary, surface, onBackground, etc.
- Use design system Color.kt values where specific brand colors are needed (DeepOceanBlue, SouthernSeaTeal, WarmSunGold, WarmCoastalWhite, DeepNavy, NaturalGreen, WarmAmber, RestrainedCoralRed, DarkBackground)
- Ensure all themable elements use MaterialTheme.colorScheme.* rather than hardcoded colors
- Maintain dark mode compatibility through Material3 color scheme tokens

### Typography Token Usage
- Replace inline TextStyle definitions with MaterialTheme.typography.h1, body1, button, etc.
- Use Type.kt typography constants for any custom typography needs
- Ensure consistent font sizes, weights, and line heights across all screens
- Maintain WCAG 2.1 AA contrast ratios through token-based colors

### Elevation and Spacing
- Standardize Card elevation: Modifier.elevation(2.dp) for typical cards, Modifier.elevation(4.dp) for prominent cards
- Standardize padding: Modifier.padding(16.dp) for major sections, Modifier.padding(8.dp) for subtle spacing
- Standardize margin: Modifier.margin(16.dp) for major sections, Modifier.margin(8.dp) for subtle spacing
- Ensure all screens use the same elevation and spacing values

### Consistency Verification
- Audit LoginScreen, AdvisoriesScreen, EmergencyReportScreen, AdminDashboard
- Generate report of any remaining hardcoded colors/typography
- Create task list for any remaining inconsistencies
- Plan follow-up sprint for complete design system migration

## Plan Tasks
- T094: Design system: add custom illustrations for empty states and onboarding
- T095: Design system: refine typography scale and expand color semantic system

## Dependencies
- Requires existing Color.kt, Theme.kt, Type.kt files (already present in project)
- Requires access to all UI screen source files
- Requires familiarity with Material3 theming patterns
- Must follow Constitution VII (accessibility) - consistent design ensures accessibility compliance
- Must follow Constitution III (Appwrite as exclusive backend) - design system doesn't affect backend
- Must follow Constitution X (documentation) - design system changes must be documented

## Acceptance Criteria
- No Color(0xFF...) hardcodes in UI composables — all use MaterialTheme.colorScheme.* or design system vals
- All TextStyle uses MaterialTheme.typography.*
- Consistent elevation (2dp/4dp) across all Card components
- Consistent padding/margin (16dp/8dp) across all screens
- Audit report generated with <5 remaining hardcoded color/typography instances
- All new screens (splash, onboarding, etc.) follow design system from inception

## Dependencies
- Requires existing Color.kt (30+ brand colors), Theme.kt (LightColorScheme with proper tokens), Type.kt (typography)
- Requires access to all UI screen source files
- Requires familiarity with Material3 theming patterns
- Must follow Constitution VII (accessibility) - consistent design ensures accessibility compliance
- Must follow Constitution III (Appwrite as exclusive backend) - design system doesn't affect backend
- Must follow Constitution X (documentation) - design system changes must be documented
