import 'package:flutter/material.dart';

import '../theme/app_theme.dart';
import '../theme/design_tokens.dart';

/// Flat, crisp executive surface container with a 1dp subtle border and
/// clean 8dp-grid padding. Zero noisy elevation by default.
class KisanCard extends StatelessWidget {
  const KisanCard({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(DesignTokens.space16),
    this.backgroundColor,
    this.borderColor,
    this.borderRadius = DesignTokens.radiusMd,
    this.elevation = DesignTokens.elevationNone,
    this.onTap,
    this.semanticLabel,
  });

  final Widget child;
  final EdgeInsetsGeometry padding;
  final Color? backgroundColor;
  final Color? borderColor;
  final double borderRadius;
  final double elevation;
  final VoidCallback? onTap;
  final String? semanticLabel;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    final BorderRadius radius = BorderRadius.circular(borderRadius);

    Widget surface = Material(
      color: backgroundColor ?? colors.cardSurface,
      elevation: elevation,
      shadowColor: Colors.black.withValues(alpha: 0.06),
      surfaceTintColor: Colors.transparent,
      shape: RoundedRectangleBorder(
        borderRadius: radius,
        side: BorderSide(
          color: borderColor ?? colors.border,
          width: DesignTokens.borderWidth,
        ),
      ),
      clipBehavior: Clip.antiAlias,
      child: onTap != null
          ? InkWell(
              onTap: onTap,
              borderRadius: radius,
              child: Padding(padding: padding, child: child),
            )
          : Padding(padding: padding, child: child),
    );

    if (semanticLabel != null) {
      surface = Semantics(
        label: semanticLabel,
        button: onTap != null,
        child: surface,
      );
    }

    return surface;
  }
}

enum KisanButtonVariant { primary, secondary, subtle }

/// Accessible 48dp+ button component with solid Deep Forest Green primary styling
/// and crisp 1dp-bordered secondary alternative.
class KisanButton extends StatelessWidget {
  const KisanButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.urduLabel,
    this.icon,
    this.trailingIcon,
    this.variant = KisanButtonVariant.primary,
    this.isFullWidth = false,
    this.isLoading = false,
  });

  final String label;
  final String? urduLabel;
  final VoidCallback? onPressed;
  final IconData? icon;
  final IconData? trailingIcon;
  final KisanButtonVariant variant;
  final bool isFullWidth;
  final bool isLoading;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    final bool isDark = Theme.of(context).brightness == Brightness.dark;

    final Color bg;
    final Color fg;
    final Color borderColor;

    switch (variant) {
      case KisanButtonVariant.primary:
        bg = isDark ? DesignTokens.forestGreen : colors.brandPrimary;
        fg = Colors.white;
        borderColor = Colors.transparent;
      case KisanButtonVariant.secondary:
        bg = colors.cardSurface;
        fg = colors.textPrimary;
        borderColor = colors.border;
      case KisanButtonVariant.subtle:
        bg = colors.brandSubtle;
        fg = colors.brandPrimary;
        borderColor = colors.brandBorder;
    }

    final Widget buttonContent = ConstrainedBox(
      constraints: const BoxConstraints(
        minHeight: DesignTokens.minTouchTarget,
        minWidth: DesignTokens.minTouchTarget,
      ),
      child: Material(
        color: onPressed == null ? colors.subtleSurface : bg,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(DesignTokens.radiusSm + 2),
          side: borderColor == Colors.transparent
              ? BorderSide.none
              : BorderSide(color: borderColor, width: DesignTokens.borderWidth),
        ),
        clipBehavior: Clip.antiAlias,
        child: InkWell(
          onTap: isLoading ? null : onPressed,
          child: Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: DesignTokens.space16,
              vertical: DesignTokens.space8,
            ),
            child: Row(
              mainAxisSize: isFullWidth ? MainAxisSize.max : MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                if (isLoading) ...<Widget>[
                  SizedBox(
                    width: 18,
                    height: 18,
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                      color: fg,
                    ),
                  ),
                  const SizedBox(width: DesignTokens.space8),
                ] else if (icon != null) ...<Widget>[
                  Icon(
                    icon,
                    size: 18,
                    color: onPressed == null ? colors.textTertiary : fg,
                  ),
                  const SizedBox(width: DesignTokens.space8),
                ],
                Flexible(
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      if (urduLabel != null) ...<Widget>[
                        Text(
                          urduLabel!,
                          textDirection: TextDirection.rtl,
                          style: KisanTypography.urduLabel(
                            color: onPressed == null ? colors.textTertiary : fg,
                            fontSize: 13.5,
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.symmetric(
                            horizontal: DesignTokens.space8,
                          ),
                          child: Container(
                            width: 1,
                            height: 14,
                            color: (onPressed == null
                                    ? colors.textTertiary
                                    : fg)
                                .withValues(alpha: 0.35),
                          ),
                        ),
                      ],
                      Flexible(
                        child: Text(
                          label,
                          overflow: TextOverflow.ellipsis,
                          style: KisanTypography.bodyMedium(
                            color: onPressed == null ? colors.textTertiary : fg,
                          ).copyWith(fontWeight: FontWeight.w600),
                        ),
                      ),
                    ],
                  ),
                ),
                if (trailingIcon != null && !isLoading) ...<Widget>[
                  const SizedBox(width: DesignTokens.space8),
                  Icon(
                    trailingIcon,
                    size: 16,
                    color: onPressed == null ? colors.textTertiary : fg,
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );

    return isFullWidth
        ? SizedBox(width: double.infinity, child: buttonContent)
        : buttonContent;
  }
}

/// Accessible 48dp touch-target chip with subtle surface fill and crisp text.
/// Supports bilingual Urdu Nastaliq or clean English labels.
class KisanChip extends StatelessWidget {
  const KisanChip({
    super.key,
    required this.label,
    required this.onTap,
    this.isSelected = false,
    this.isUrdu = false,
    this.leadingIcon,
    this.subtitle,
  });

  final String label;
  final String? subtitle;
  final VoidCallback onTap;
  final bool isSelected;
  final bool isUrdu;
  final IconData? leadingIcon;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    final Color bg = isSelected ? colors.brandSubtle : colors.cardSurface;
    final Color border = isSelected ? colors.brandPrimary : colors.border;
    final Color fg = isSelected ? colors.brandPrimary : colors.textPrimary;

    return ConstrainedBox(
      constraints: const BoxConstraints(
        minHeight: DesignTokens.minTouchTarget,
        minWidth: DesignTokens.minTouchTarget,
      ),
      child: Center(
        child: Material(
          color: bg,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
            side: BorderSide(
              color: border,
              width: isSelected
                  ? DesignTokens.borderWidthEmphasis
                  : DesignTokens.borderWidth,
            ),
          ),
          clipBehavior: Clip.antiAlias,
          child: InkWell(
            onTap: onTap,
            child: ConstrainedBox(
              constraints: const BoxConstraints(
                minHeight: DesignTokens.minTouchTarget - 4,
              ),
              child: Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: DesignTokens.space12,
                  vertical: DesignTokens.space8,
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    if (leadingIcon != null) ...<Widget>[
                      Icon(
                        leadingIcon,
                        size: 16,
                        color: isSelected
                            ? colors.brandPrimary
                            : colors.textSecondary,
                      ),
                      const SizedBox(width: DesignTokens.space8),
                    ],
                    Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: isUrdu
                          ? CrossAxisAlignment.end
                          : CrossAxisAlignment.start,
                      children: <Widget>[
                        Text(
                          label,
                          textDirection:
                              isUrdu ? TextDirection.rtl : TextDirection.ltr,
                          style: isUrdu
                              ? KisanTypography.urduLabel(
                                  color: fg,
                                  fontSize: 13,
                                )
                              : KisanTypography.bodyMedium(color: fg).copyWith(
                                  fontWeight: isSelected
                                      ? FontWeight.w600
                                      : FontWeight.w500,
                                  fontSize: 13,
                                ),
                        ),
                        if (subtitle != null)
                          Text(
                            subtitle!,
                            style: KisanTypography.microLabel(
                              color: isSelected
                                  ? colors.brandPrimary
                                  : colors.textSecondary,
                            ).copyWith(letterSpacing: 0.1, fontSize: 10.5),
                          ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

enum KisanBadgeTone { info, act, alert, success, neutral }

/// Minimalist semantic status badge replacing loud, colorful warning banners.
/// Uses subtle background tint + 1dp hairline border + crisp micro-typography.
class KisanStatusBadge extends StatelessWidget {
  const KisanStatusBadge({
    super.key,
    required this.label,
    required this.tone,
    this.icon,
  });

  final String label;
  final KisanBadgeTone tone;
  final IconData? icon;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    final Color bg;
    final Color fg;
    final Color border;
    final IconData defaultIcon;

    switch (tone) {
      case KisanBadgeTone.info:
        bg = colors.infoSubtle;
        fg = colors.infoForeground;
        border = colors.infoBorder;
        defaultIcon = Icons.info_outline_rounded;
      case KisanBadgeTone.act:
        bg = colors.accentSubtle;
        fg = colors.accentAmber;
        border = colors.accentBorder;
        defaultIcon = Icons.bolt_rounded;
      case KisanBadgeTone.alert:
        bg = colors.alertSubtle;
        fg = colors.alertForeground;
        border = colors.alertBorder;
        defaultIcon = Icons.warning_amber_rounded;
      case KisanBadgeTone.success:
        bg = colors.brandSubtle;
        fg = colors.brandPrimary;
        border = colors.brandBorder;
        defaultIcon = Icons.check_circle_outline_rounded;
      case KisanBadgeTone.neutral:
        bg = colors.subtleSurface;
        fg = colors.textSecondary;
        border = colors.border;
        defaultIcon = Icons.circle_outlined;
    }

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: DesignTokens.space8,
        vertical: DesignTokens.space4,
      ),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(DesignTokens.radiusXs),
        border: Border.all(color: border, width: DesignTokens.borderWidth),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          Icon(icon ?? defaultIcon, size: 13, color: fg),
          const SizedBox(width: DesignTokens.space4),
          Text(
            label,
            style: KisanTypography.microLabel(color: fg),
          ),
        ],
      ),
    );
  }
}

/// Dedicated RTL Urdu Nastaliq text widget ensuring proper vertical strut and
/// zero glyph clipping on Android.
class UrduText extends StatelessWidget {
  const UrduText(
    this.text, {
    super.key,
    this.style,
    this.textAlign = TextAlign.right,
    this.maxLines,
    this.overflow,
  });

  final String text;
  final TextStyle? style;
  final TextAlign textAlign;
  final int? maxLines;
  final TextOverflow? overflow;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Text(
        text,
        textAlign: textAlign,
        maxLines: maxLines,
        overflow: overflow,
        style: style ?? KisanTypography.urduBody(color: colors.textPrimary),
      ),
    );
  }
}

/// Accessible 48x48dp minimalist icon button with 1dp border container.
class KisanIconAction extends StatelessWidget {
  const KisanIconAction({
    super.key,
    required this.icon,
    required this.tooltip,
    required this.onTap,
    this.isActive = false,
  });

  final IconData icon;
  final String tooltip;
  final VoidCallback onTap;
  final bool isActive;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Tooltip(
      message: tooltip,
      child: SizedBox(
        width: DesignTokens.minTouchTarget,
        height: DesignTokens.minTouchTarget,
        child: Center(
          child: Material(
            color: isActive ? colors.brandSubtle : colors.cardSurface,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
              side: BorderSide(
                color: isActive ? colors.brandPrimary : colors.border,
                width: DesignTokens.borderWidth,
              ),
            ),
            clipBehavior: Clip.antiAlias,
            child: InkWell(
              onTap: onTap,
              child: SizedBox(
                width: 40,
                height: 40,
                child: Icon(
                  icon,
                  size: 19,
                  color: isActive ? colors.brandPrimary : colors.textSecondary,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
