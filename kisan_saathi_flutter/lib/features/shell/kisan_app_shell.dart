import 'package:flutter/material.dart';

import '../../core/config/api_config.dart';
import '../../core/theme/app_theme.dart';
import '../../core/theme/design_tokens.dart';
import '../../core/widgets/kisan_components.dart';
import '../../data/repositories/kisan_repository.dart';
import '../../data/services/kisan_voice_service.dart';
import '../ask/ask_screen.dart';
import '../field_health/field_health_screen.dart';
import '../home/home_screen.dart';

class KisanAppShell extends StatefulWidget {
  const KisanAppShell({
    super.key,
    required this.repository,
    required this.voiceService,
    required this.themeMode,
    required this.onToggleTheme,
  });

  final IKisanRepository repository;
  final IKisanVoiceService voiceService;
  final ThemeMode themeMode;
  final VoidCallback onToggleTheme;

  @override
  State<KisanAppShell> createState() => _KisanAppShellState();
}

class _KisanAppShellState extends State<KisanAppShell> {
  int _currentIndex = 0;

  void _showBackendInfoSheet() {
    final KisanSemanticColors colors = context.kisanColors;
    showModalBottomSheet<void>(
      context: context,
      builder: (BuildContext sheetContext) {
        return Padding(
          padding: const EdgeInsets.all(DesignTokens.space24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              KisanStatusBadge(
                label: ApiConfig.useMockBackend
                    ? 'MOCK DATA MODE (OFFLINE READY)'
                    : 'LIVE REST API CONNECTED',
                tone: ApiConfig.useMockBackend
                    ? KisanBadgeTone.info
                    : KisanBadgeTone.success,
              ),
              const SizedBox(height: DesignTokens.space12),
              Text(
                'Backend Connection Architecture',
                style: KisanTypography.title(color: colors.textPrimary),
              ),
              const SizedBox(height: DesignTokens.space8),
              Text(
                'Configured Base URL: ${ApiConfig.baseUrl}\n'
                'When your backend server is ready, open lib/core/config/api_config.dart '
                'and set useMockBackend = false.',
                style: KisanTypography.body(color: colors.textSecondary),
              ),
              const SizedBox(height: DesignTokens.space20),
              KisanButton(
                label: 'Got it',
                isFullWidth: true,
                onPressed: () => Navigator.of(sheetContext).pop(),
              ),
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    final bool isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(64),
        child: Container(
          decoration: BoxDecoration(
            color: colors.cardSurface,
            border: Border(
              bottom: BorderSide(
                color: colors.border,
                width: DesignTokens.borderWidth,
              ),
            ),
          ),
          child: SafeArea(
            bottom: false,
            child: Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: DesignTokens.space16,
              ),
              child: Row(
                children: <Widget>[
                  Container(
                    width: 34,
                    height: 34,
                    decoration: BoxDecoration(
                      color: DesignTokens.forestGreen,
                      borderRadius:
                          BorderRadius.circular(DesignTokens.radiusSm),
                    ),
                    child: const Icon(
                      Icons.spa_outlined,
                      size: 19,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(width: DesignTokens.space12),
                  Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Row(
                          children: <Widget>[
                            Text(
                              'Kisan Saathi',
                              style: KisanTypography.title(
                                color: colors.textPrimary,
                              ).copyWith(fontSize: 15.5),
                            ),
                            const SizedBox(width: DesignTokens.space8),
                            UrduText(
                              'کسان ساتھی',
                              style: KisanTypography.urduLabel(
                                color: colors.brandPrimary,
                                fontSize: 13.5,
                              ),
                            ),
                          ],
                        ),
                        Text(
                          'Pothohar Barani Companion',
                          style: KisanTypography.microLabel(
                            color: colors.textSecondary,
                          ).copyWith(fontSize: 10.5, letterSpacing: 0.2),
                        ),
                      ],
                    ),
                  ),
                  InkWell(
                    borderRadius: BorderRadius.circular(DesignTokens.radiusXs),
                    onTap: _showBackendInfoSheet,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(
                        horizontal: DesignTokens.space4,
                        vertical: DesignTokens.space8,
                      ),
                      child: KisanStatusBadge(
                        label: ApiConfig.useMockBackend ? 'DEMO API' : 'LIVE',
                        tone: ApiConfig.useMockBackend
                            ? KisanBadgeTone.neutral
                            : KisanBadgeTone.success,
                        icon: Icons.cloud_outlined,
                      ),
                    ),
                  ),
                  const SizedBox(width: DesignTokens.space8),
                  KisanIconAction(
                    icon: isDark
                        ? Icons.light_mode_outlined
                        : Icons.dark_mode_outlined,
                    tooltip: isDark
                        ? 'Switch to Light Mode'
                        : 'Switch to Dark Mode',
                    onTap: widget.onToggleTheme,
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
      body: IndexedStack(
        index: _currentIndex,
        children: <Widget>[
          HomeScreen(repository: widget.repository),
          AskScreen(voiceService: widget.voiceService),
          FieldHealthScreen(repository: widget.repository),
        ],
      ),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: colors.cardSurface,
          border: Border(
            top: BorderSide(
              color: colors.border,
              width: DesignTokens.borderWidth,
            ),
          ),
        ),
        child: SafeArea(
          top: false,
          child: SizedBox(
            height: 68,
            child: Row(
              children: <Widget>[
                Expanded(
                  child: _BottomNavTab(
                    labelEn: 'Roadmap',
                    labelUr: 'روڈ میپ',
                    icon: Icons.route_outlined,
                    activeIcon: Icons.route_rounded,
                    isSelected: _currentIndex == 0,
                    onTap: () => setState(() => _currentIndex = 0),
                  ),
                ),
                Expanded(
                  child: _BottomNavTab(
                    labelEn: 'Voice AI',
                    labelUr: 'پوچھیں',
                    icon: Icons.mic_none_rounded,
                    activeIcon: Icons.mic_rounded,
                    isSelected: _currentIndex == 1,
                    isPrimaryVoice: true,
                    onTap: () => setState(() => _currentIndex = 1),
                  ),
                ),
                Expanded(
                  child: _BottomNavTab(
                    labelEn: 'Field Health',
                    labelUr: 'صحت کھیت',
                    icon: Icons.analytics_outlined,
                    activeIcon: Icons.analytics_rounded,
                    isSelected: _currentIndex == 2,
                    onTap: () => setState(() => _currentIndex = 2),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _BottomNavTab extends StatelessWidget {
  const _BottomNavTab({
    required this.labelEn,
    required this.labelUr,
    required this.icon,
    required this.activeIcon,
    required this.isSelected,
    required this.onTap,
    this.isPrimaryVoice = false,
  });

  final String labelEn;
  final String labelUr;
  final IconData icon;
  final IconData activeIcon;
  final bool isSelected;
  final bool isPrimaryVoice;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    final Color fg = isSelected ? colors.brandPrimary : colors.textSecondary;

    return Semantics(
      selected: isSelected,
      button: true,
      label: '$labelEn ($labelUr)',
      child: InkWell(
        onTap: onTap,
        child: ConstrainedBox(
          constraints: const BoxConstraints(
            minHeight: DesignTokens.minTouchTarget,
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: DesignTokens.space12,
                  vertical: DesignTokens.space4,
                ),
                decoration: BoxDecoration(
                  color: isSelected
                      ? colors.brandSubtle
                      : (isPrimaryVoice
                          ? colors.subtleSurface
                          : Colors.transparent),
                  borderRadius: BorderRadius.circular(DesignTokens.radiusPill),
                  border: isSelected
                      ? Border.all(
                          color: colors.brandBorder,
                          width: DesignTokens.borderWidth,
                        )
                      : null,
                ),
                child: Icon(
                  isSelected ? activeIcon : icon,
                  size: 20,
                  color: fg,
                ),
              ),
              const SizedBox(height: DesignTokens.space4),
              Text(
                '$labelEn · $labelUr',
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: KisanTypography.microLabel(color: fg).copyWith(
                  fontSize: 10.5,
                  letterSpacing: 0.1,
                  fontWeight: isSelected ? FontWeight.w700 : FontWeight.w500,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
