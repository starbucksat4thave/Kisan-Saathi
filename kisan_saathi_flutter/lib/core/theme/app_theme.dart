import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'design_tokens.dart';

/// Semantic theme extension exposing Kisan Saathi's executive surface, border,
/// and status badge tokens cleanly across Light and Dark modes.
@immutable
class KisanSemanticColors extends ThemeExtension<KisanSemanticColors> {
  const KisanSemanticColors({
    required this.canvas,
    required this.cardSurface,
    required this.subtleSurface,
    required this.border,
    required this.borderStrong,
    required this.textPrimary,
    required this.textSecondary,
    required this.textTertiary,
    required this.brandPrimary,
    required this.brandPrimaryPressed,
    required this.brandSubtle,
    required this.brandBorder,
    required this.accentAmber,
    required this.accentSubtle,
    required this.accentBorder,
    required this.infoForeground,
    required this.infoSubtle,
    required this.infoBorder,
    required this.alertForeground,
    required this.alertSubtle,
    required this.alertBorder,
  });

  final Color canvas;
  final Color cardSurface;
  final Color subtleSurface;
  final Color border;
  final Color borderStrong;
  final Color textPrimary;
  final Color textSecondary;
  final Color textTertiary;

  final Color brandPrimary;
  final Color brandPrimaryPressed;
  final Color brandSubtle;
  final Color brandBorder;

  final Color accentAmber;
  final Color accentSubtle;
  final Color accentBorder;

  final Color infoForeground;
  final Color infoSubtle;
  final Color infoBorder;

  final Color alertForeground;
  final Color alertSubtle;
  final Color alertBorder;

  static const KisanSemanticColors light = KisanSemanticColors(
    canvas: DesignTokens.lightBackground,
    cardSurface: DesignTokens.lightSurface,
    subtleSurface: DesignTokens.lightSurfaceSubtle,
    border: DesignTokens.lightBorder,
    borderStrong: DesignTokens.lightBorderStrong,
    textPrimary: DesignTokens.slatePrimary,
    textSecondary: DesignTokens.slateSecondary,
    textTertiary: DesignTokens.slateTertiary,
    brandPrimary: DesignTokens.forestGreen,
    brandPrimaryPressed: DesignTokens.forestGreenDark,
    brandSubtle: DesignTokens.forestGreenSubtle,
    brandBorder: DesignTokens.forestGreenBorder,
    accentAmber: DesignTokens.wheatAmber,
    accentSubtle: DesignTokens.wheatAmberSubtle,
    accentBorder: DesignTokens.wheatAmberBorder,
    infoForeground: DesignTokens.infoBlue,
    infoSubtle: DesignTokens.infoBlueSubtle,
    infoBorder: DesignTokens.infoBlueBorder,
    alertForeground: DesignTokens.alertCrimson,
    alertSubtle: DesignTokens.alertCrimsonSubtle,
    alertBorder: DesignTokens.alertCrimsonBorder,
  );

  static const KisanSemanticColors dark = KisanSemanticColors(
    canvas: DesignTokens.darkBackground,
    cardSurface: DesignTokens.darkSurface,
    subtleSurface: DesignTokens.darkSurfaceSubtle,
    border: DesignTokens.darkBorder,
    borderStrong: DesignTokens.darkBorderStrong,
    textPrimary: DesignTokens.darkTextPrimary,
    textSecondary: DesignTokens.darkTextSecondary,
    textTertiary: DesignTokens.darkTextTertiary,
    brandPrimary: DesignTokens.darkForestGreen,
    brandPrimaryPressed: DesignTokens.darkForestGreenCta,
    brandSubtle: DesignTokens.darkForestGreenSubtle,
    brandBorder: DesignTokens.darkForestGreenBorder,
    accentAmber: DesignTokens.darkWheatAmber,
    accentSubtle: DesignTokens.darkWheatAmberSubtle,
    accentBorder: DesignTokens.darkWheatAmberBorder,
    infoForeground: DesignTokens.darkInfoBlue,
    infoSubtle: DesignTokens.darkInfoSubtle,
    infoBorder: DesignTokens.darkInfoBorder,
    alertForeground: DesignTokens.darkAlertCrimson,
    alertSubtle: DesignTokens.darkAlertSubtle,
    alertBorder: DesignTokens.darkAlertBorder,
  );

  @override
  KisanSemanticColors copyWith({
    Color? canvas,
    Color? cardSurface,
    Color? subtleSurface,
    Color? border,
    Color? borderStrong,
    Color? textPrimary,
    Color? textSecondary,
    Color? textTertiary,
    Color? brandPrimary,
    Color? brandPrimaryPressed,
    Color? brandSubtle,
    Color? brandBorder,
    Color? accentAmber,
    Color? accentSubtle,
    Color? accentBorder,
    Color? infoForeground,
    Color? infoSubtle,
    Color? infoBorder,
    Color? alertForeground,
    Color? alertSubtle,
    Color? alertBorder,
  }) {
    return KisanSemanticColors(
      canvas: canvas ?? this.canvas,
      cardSurface: cardSurface ?? this.cardSurface,
      subtleSurface: subtleSurface ?? this.subtleSurface,
      border: border ?? this.border,
      borderStrong: borderStrong ?? this.borderStrong,
      textPrimary: textPrimary ?? this.textPrimary,
      textSecondary: textSecondary ?? this.textSecondary,
      textTertiary: textTertiary ?? this.textTertiary,
      brandPrimary: brandPrimary ?? this.brandPrimary,
      brandPrimaryPressed: brandPrimaryPressed ?? this.brandPrimaryPressed,
      brandSubtle: brandSubtle ?? this.brandSubtle,
      brandBorder: brandBorder ?? this.brandBorder,
      accentAmber: accentAmber ?? this.accentAmber,
      accentSubtle: accentSubtle ?? this.accentSubtle,
      accentBorder: accentBorder ?? this.accentBorder,
      infoForeground: infoForeground ?? this.infoForeground,
      infoSubtle: infoSubtle ?? this.infoSubtle,
      infoBorder: infoBorder ?? this.infoBorder,
      alertForeground: alertForeground ?? this.alertForeground,
      alertSubtle: alertSubtle ?? this.alertSubtle,
      alertBorder: alertBorder ?? this.alertBorder,
    );
  }

  @override
  KisanSemanticColors lerp(
    covariant ThemeExtension<KisanSemanticColors>? other,
    double t,
  ) {
    if (other is! KisanSemanticColors) return this;
    return KisanSemanticColors(
      canvas: Color.lerp(canvas, other.canvas, t)!,
      cardSurface: Color.lerp(cardSurface, other.cardSurface, t)!,
      subtleSurface: Color.lerp(subtleSurface, other.subtleSurface, t)!,
      border: Color.lerp(border, other.border, t)!,
      borderStrong: Color.lerp(borderStrong, other.borderStrong, t)!,
      textPrimary: Color.lerp(textPrimary, other.textPrimary, t)!,
      textSecondary: Color.lerp(textSecondary, other.textSecondary, t)!,
      textTertiary: Color.lerp(textTertiary, other.textTertiary, t)!,
      brandPrimary: Color.lerp(brandPrimary, other.brandPrimary, t)!,
      brandPrimaryPressed:
          Color.lerp(brandPrimaryPressed, other.brandPrimaryPressed, t)!,
      brandSubtle: Color.lerp(brandSubtle, other.brandSubtle, t)!,
      brandBorder: Color.lerp(brandBorder, other.brandBorder, t)!,
      accentAmber: Color.lerp(accentAmber, other.accentAmber, t)!,
      accentSubtle: Color.lerp(accentSubtle, other.accentSubtle, t)!,
      accentBorder: Color.lerp(accentBorder, other.accentBorder, t)!,
      infoForeground: Color.lerp(infoForeground, other.infoForeground, t)!,
      infoSubtle: Color.lerp(infoSubtle, other.infoSubtle, t)!,
      infoBorder: Color.lerp(infoBorder, other.infoBorder, t)!,
      alertForeground: Color.lerp(alertForeground, other.alertForeground, t)!,
      alertSubtle: Color.lerp(alertSubtle, other.alertSubtle, t)!,
      alertBorder: Color.lerp(alertBorder, other.alertBorder, t)!,
    );
  }
}

extension KisanThemeContext on BuildContext {
  KisanSemanticColors get kisanColors =>
      Theme.of(this).extension<KisanSemanticColors>() ??
      KisanSemanticColors.light;
}

/// Material 3 Light & Dark themes for Kisan Saathi (`Theme.kt` equivalent).
abstract final class KisanTheme {
  static const ColorScheme lightColorScheme = ColorScheme(
    brightness: Brightness.light,
    primary: DesignTokens.forestGreen,
    onPrimary: Colors.white,
    primaryContainer: DesignTokens.forestGreenSubtle,
    onPrimaryContainer: DesignTokens.forestGreenDark,
    secondary: DesignTokens.wheatAmber,
    onSecondary: Colors.white,
    secondaryContainer: DesignTokens.wheatAmberSubtle,
    onSecondaryContainer: DesignTokens.wheatAmber,
    tertiary: DesignTokens.infoBlue,
    onTertiary: Colors.white,
    tertiaryContainer: DesignTokens.infoBlueSubtle,
    onTertiaryContainer: DesignTokens.infoBlue,
    error: DesignTokens.alertCrimson,
    onError: Colors.white,
    errorContainer: DesignTokens.alertCrimsonSubtle,
    onErrorContainer: DesignTokens.alertCrimson,
    surface: DesignTokens.lightSurface,
    onSurface: DesignTokens.slatePrimary,
    surfaceContainerHighest: DesignTokens.lightSurfaceSubtle,
    onSurfaceVariant: DesignTokens.slateSecondary,
    outline: DesignTokens.lightBorder,
    outlineVariant: DesignTokens.lightBorderStrong,
  );

  static const ColorScheme darkColorScheme = ColorScheme(
    brightness: Brightness.dark,
    primary: DesignTokens.darkForestGreen,
    onPrimary: Color(0xFF052E16),
    primaryContainer: DesignTokens.darkForestGreenSubtle,
    onPrimaryContainer: DesignTokens.darkForestGreen,
    secondary: DesignTokens.darkWheatAmber,
    onSecondary: Color(0xFF291504),
    secondaryContainer: DesignTokens.darkWheatAmberSubtle,
    onSecondaryContainer: DesignTokens.darkWheatAmber,
    tertiary: DesignTokens.darkInfoBlue,
    onTertiary: Color(0xFF082F49),
    tertiaryContainer: DesignTokens.darkInfoSubtle,
    onTertiaryContainer: DesignTokens.darkInfoBlue,
    error: DesignTokens.darkAlertCrimson,
    onError: Color(0xFF350A0A),
    errorContainer: DesignTokens.darkAlertSubtle,
    onErrorContainer: DesignTokens.darkAlertCrimson,
    surface: DesignTokens.darkSurface,
    onSurface: DesignTokens.darkTextPrimary,
    surfaceContainerHighest: DesignTokens.darkSurfaceSubtle,
    onSurfaceVariant: DesignTokens.darkTextSecondary,
    outline: DesignTokens.darkBorder,
    outlineVariant: DesignTokens.darkBorderStrong,
  );

  static ThemeData get light => _buildTheme(
        colorScheme: lightColorScheme,
        semantics: KisanSemanticColors.light,
        overlayStyle: SystemUiOverlayStyle.dark,
      );

  static ThemeData get dark => _buildTheme(
        colorScheme: darkColorScheme,
        semantics: KisanSemanticColors.dark,
        overlayStyle: SystemUiOverlayStyle.light,
      );

  static ThemeData _buildTheme({
    required ColorScheme colorScheme,
    required KisanSemanticColors semantics,
    required SystemUiOverlayStyle overlayStyle,
  }) {
    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: semantics.canvas,
      splashFactory: InkSparkle.splashFactory,
      materialTapTargetSize: MaterialTapTargetSize.padded,
      visualDensity: VisualDensity.standard,
      extensions: <ThemeExtension<dynamic>>[semantics],
      appBarTheme: AppBarTheme(
        backgroundColor: semantics.cardSurface,
        foregroundColor: semantics.textPrimary,
        elevation: DesignTokens.elevationNone,
        scrolledUnderElevation: DesignTokens.elevationNone,
        centerTitle: false,
        systemOverlayStyle: overlayStyle,
        surfaceTintColor: Colors.transparent,
      ),
      dividerTheme: DividerThemeData(
        color: semantics.border,
        thickness: DesignTokens.borderWidth,
        space: DesignTokens.borderWidth,
      ),
      bottomSheetTheme: BottomSheetThemeData(
        backgroundColor: semantics.cardSurface,
        surfaceTintColor: Colors.transparent,
        elevation: DesignTokens.elevationMax,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(
            top: Radius.circular(DesignTokens.radiusLg),
          ),
        ),
      ),
      textTheme: TextTheme(
        headlineMedium: KisanTypography.headline(color: semantics.textPrimary),
        titleMedium: KisanTypography.title(color: semantics.textPrimary),
        bodyMedium: KisanTypography.body(color: semantics.textPrimary),
        bodySmall: KisanTypography.body(color: semantics.textSecondary),
        labelSmall: KisanTypography.microLabel(color: semantics.textSecondary),
      ),
    );
  }
}
