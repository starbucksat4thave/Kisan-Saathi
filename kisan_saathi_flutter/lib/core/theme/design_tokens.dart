import 'package:flutter/material.dart';

/// Core design tokens for Kisan Saathi.
///
/// Inspired by Apple Human Interface Guidelines and Linear/Stripe design systems:
/// - Grounded, calm, high-contrast agricultural utility
/// - Strict 8dp spatial rhythm
/// - Crisp 1dp borders over noisy drop shadows
/// - Minimum 48dp accessible touch targets for field usability
abstract final class DesignTokens {
  // ---------------------------------------------------------------------------
  // 1. COLOR PALETTE — LIGHT (Muted & Earthy Executive Palette)
  // ---------------------------------------------------------------------------

  /// Neutral Off-White primary app canvas (`#F8FAFC`)
  static const Color lightBackground = Color(0xFFF8FAFC);

  /// Alternate subtle off-white (`#F9FAFB`)
  static const Color lightBackgroundAlt = Color(0xFFF9FAFB);

  /// Pure White surface containers & cards (`#FFFFFF`)
  static const Color lightSurface = Color(0xFFFFFFFF);

  /// Muted inset container fill (`#F1F5F9`)
  static const Color lightSurfaceSubtle = Color(0xFFF1F5F9);

  /// Crisp 1dp subtle border (`#E2E8F0`)
  static const Color lightBorder = Color(0xFFE2E8F0);

  /// Stronger neutral border for interactive hover/focus (`#CBD5E1`)
  static const Color lightBorderStrong = Color(0xFFCBD5E1);

  /// Primary Brand: Deep Forest Green (`#15803D`)
  static const Color forestGreen = Color(0xFF15803D);

  /// Primary Brand Pressed / Dark Shade (`#166534`)
  static const Color forestGreenDark = Color(0xFF166534);

  /// Subtle green tint for active chips & completed node fills (`#F0FDF4`)
  static const Color forestGreenSubtle = Color(0xFFF0FDF4);

  /// Crisp green border for active subtle containers (`#BBF7D0`)
  static const Color forestGreenBorder = Color(0xFFBBF7D0);

  /// Secondary / Accent: Warm Wheat / Amber (`#B45309`)
  static const Color wheatAmber = Color(0xFFB45309);

  /// Secondary / Accent Highlight (`#D97706`)
  static const Color wheatAmberBright = Color(0xFFD97706);

  /// Subtle warm wheat tint for `ACT` / Barkat badges (`#FFFBEB`)
  static const Color wheatAmberSubtle = Color(0xFFFFFBEB);

  /// Crisp warm wheat border (`#FDE68A`)
  static const Color wheatAmberBorder = Color(0xFFFDE68A);

  /// Text Primary: Deep Slate (`#0F172A`) — WCAG AAA high contrast
  static const Color slatePrimary = Color(0xFF0F172A);

  /// Text Secondary: Muted Slate (`#64748B`)
  static const Color slateSecondary = Color(0xFF64748B);

  /// Text Tertiary / Captions (`#94A3B8`)
  static const Color slateTertiary = Color(0xFF94A3B8);

  // Semantic Advisory Status Tones (Muted & Grounded, never neon)
  /// INFO Status: Calm Slate-Blue (`#0369A1`)
  static const Color infoBlue = Color(0xFF0369A1);
  static const Color infoBlueSubtle = Color(0xFFF0F9FF);
  static const Color infoBlueBorder = Color(0xFFBAE6FD);

  /// ALERT Status: Grounded Crimson-Terracotta (`#B91C1C`)
  static const Color alertCrimson = Color(0xFFB91C1C);
  static const Color alertCrimsonSubtle = Color(0xFFFEF2F2);
  static const Color alertCrimsonBorder = Color(0xFFFECACA);

  // ---------------------------------------------------------------------------
  // 2. COLOR PALETTE — DARK (Executive Night / Pre-Dawn Field Mode)
  // ---------------------------------------------------------------------------

  static const Color darkBackground = Color(0xFF0B1120);
  static const Color darkSurface = Color(0xFF111827);
  static const Color darkSurfaceSubtle = Color(0xFF1E293B);
  static const Color darkBorder = Color(0xFF1F2937);
  static const Color darkBorderStrong = Color(0xFF334155);

  static const Color darkForestGreen = Color(0xFF22C55E);
  static const Color darkForestGreenCta = Color(0xFF15803D);
  static const Color darkForestGreenSubtle = Color(0xFF052E16);
  static const Color darkForestGreenBorder = Color(0xFF14532D);

  static const Color darkWheatAmber = Color(0xFFF59E0B);
  static const Color darkWheatAmberSubtle = Color(0xFF291504);
  static const Color darkWheatAmberBorder = Color(0xFF78350F);

  static const Color darkTextPrimary = Color(0xFFF8FAFC);
  static const Color darkTextSecondary = Color(0xFF94A3B8);
  static const Color darkTextTertiary = Color(0xFF64748B);

  static const Color darkInfoBlue = Color(0xFF38BDF8);
  static const Color darkInfoSubtle = Color(0xFF082F49);
  static const Color darkInfoBorder = Color(0xFF0369A1);

  static const Color darkAlertCrimson = Color(0xFFF87171);
  static const Color darkAlertSubtle = Color(0xFF350A0A);
  static const Color darkAlertBorder = Color(0xFF7F1D1D);

  // ---------------------------------------------------------------------------
  // 3. SPATIAL SYSTEM (Strict 8dp Grid + Micro 4dp Sub-Grid)
  // ---------------------------------------------------------------------------

  static const double space2 = 2.0;
  static const double space4 = 4.0;
  static const double space8 = 8.0;
  static const double space12 = 12.0;
  static const double space16 = 16.0;
  static const double space20 = 20.0;
  static const double space24 = 24.0;
  static const double space32 = 32.0;
  static const double space40 = 40.0;
  static const double space48 = 48.0;

  // ---------------------------------------------------------------------------
  // 4. BORDERS, ELEVATION & RADII
  // ---------------------------------------------------------------------------

  /// Crisp 1dp border stroke across cards, chips, and inputs
  static const double borderWidth = 1.0;

  /// Active focus or timeline ring stroke
  static const double borderWidthEmphasis = 1.5;

  /// Flat surface elevation (0dp default; max 2dp for floating sheets)
  static const double elevationNone = 0.0;
  static const double elevationSubtle = 1.0;
  static const double elevationMax = 2.0;

  /// Corner radii aligned to clean executive hardware aesthetics
  static const double radiusXs = 6.0;
  static const double radiusSm = 8.0;
  static const double radiusMd = 12.0;
  static const double radiusLg = 16.0;
  static const double radiusPill = 999.0;

  // ---------------------------------------------------------------------------
  // 5. ACCESSIBILITY & TOUCH TARGETS
  // ---------------------------------------------------------------------------

  /// Minimum interactive tap area (48x48dp) for comfortable outdoor farmer use
  static const double minTouchTarget = 48.0;

  /// Primary voice microphone CTA diameter
  static const double voiceMicButtonSize = 84.0;

  // ---------------------------------------------------------------------------
  // 6. FONT FAMILIES
  // ---------------------------------------------------------------------------

  static const String fontUrdu = 'NotoNastaliqUrdu';
  static const String fontUrduSans = 'NotoSansArabic';
}

/// Dual-script typography system pairing crisp tabular Sans-Serif for English &
/// telemetry metrics with authentic `NotoNastaliqUrdu` for localized Urdu copy.
abstract final class KisanTypography {
  static const List<FontFeature> _tabular = <FontFeature>[
    FontFeature.tabularFigures(),
  ];

  // --- English & Numeric Telemetry Styles ---

  static TextStyle displayMetric({Color? color}) => TextStyle(
        fontSize: 28,
        fontWeight: FontWeight.w700,
        letterSpacing: -0.6,
        height: 1.2,
        color: color,
        fontFeatures: _tabular,
      );

  static TextStyle headline({Color? color}) => TextStyle(
        fontSize: 20,
        fontWeight: FontWeight.w700,
        letterSpacing: -0.3,
        height: 1.3,
        color: color,
      );

  static TextStyle title({Color? color}) => TextStyle(
        fontSize: 16,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.2,
        height: 1.35,
        color: color,
      );

  static TextStyle body({Color? color}) => TextStyle(
        fontSize: 14,
        fontWeight: FontWeight.w400,
        height: 1.5,
        color: color,
      );

  static TextStyle bodyMedium({Color? color}) => TextStyle(
        fontSize: 14,
        fontWeight: FontWeight.w500,
        height: 1.45,
        color: color,
      );

  static TextStyle metricValue({Color? color, double fontSize = 18}) =>
      TextStyle(
        fontSize: fontSize,
        fontWeight: FontWeight.w700,
        letterSpacing: -0.3,
        height: 1.2,
        color: color,
        fontFeatures: _tabular,
      );

  static TextStyle microLabel({Color? color}) => TextStyle(
        fontSize: 11,
        fontWeight: FontWeight.w600,
        letterSpacing: 0.5,
        height: 1.2,
        color: color,
        fontFeatures: _tabular,
      );

  // --- Urdu Nastaliq Styles (Generous line-height prevents glyph clipping) ---

  static TextStyle urduHeadline({Color? color, double fontSize = 20}) =>
      TextStyle(
        fontFamily: DesignTokens.fontUrdu,
        fontSize: fontSize,
        fontWeight: FontWeight.w700,
        height: 1.9,
        color: color,
      );

  static TextStyle urduTitle({Color? color, double fontSize = 17}) => TextStyle(
        fontFamily: DesignTokens.fontUrdu,
        fontSize: fontSize,
        fontWeight: FontWeight.w700,
        height: 1.85,
        color: color,
      );

  static TextStyle urduBody({Color? color, double fontSize = 15}) => TextStyle(
        fontFamily: DesignTokens.fontUrdu,
        fontSize: fontSize,
        fontWeight: FontWeight.w400,
        height: 1.95,
        color: color,
      );

  static TextStyle urduLabel({Color? color, double fontSize = 13}) => TextStyle(
        fontFamily: DesignTokens.fontUrdu,
        fontSize: fontSize,
        fontWeight: FontWeight.w600,
        height: 1.75,
        color: color,
      );
}
