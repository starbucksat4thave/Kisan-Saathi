import 'package:flutter/material.dart';

import '../../core/theme/app_theme.dart';
import '../../core/theme/design_tokens.dart';
import '../../core/widgets/kisan_components.dart';
import '../../data/models/kisan_models.dart';
import '../../data/repositories/kisan_repository.dart';

/// Home Screen — Seasonal Crop Roadmap & Minimalist Advisory Feed.
class HomeScreen extends StatefulWidget {
  const HomeScreen({
    super.key,
    required this.repository,
  });

  final IKisanRepository repository;

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  CropSeasonType _selectedSeason = CropSeasonType.rabi;
  FarmerProfile? _profile;
  WeatherSoilSummary? _telemetry;
  List<CropStageNode> _stages = const <CropStageNode>[];
  List<AdvisoryItem> _advisories = const <AdvisoryItem>[];
  String? _selectedStageId;
  String? _playingAdvisoryId;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    final FarmerProfile profile = await widget.repository.fetchFarmerProfile();
    final WeatherSoilSummary telemetry = await widget.repository
        .fetchWeatherSoilSummary(season: _selectedSeason);
    final List<CropStageNode> stages =
        await widget.repository.fetchRoadmapStages(season: _selectedSeason);
    final List<AdvisoryItem> advisories =
        await widget.repository.fetchAdvisories(season: _selectedSeason);

    if (!mounted) return;
    final CropStageNode currentStage = stages.firstWhere(
      (CropStageNode s) => s.status == StageStatus.current,
      orElse: () => stages.first,
    );

    setState(() {
      _profile = profile;
      _telemetry = telemetry;
      _stages = stages;
      _advisories = advisories;
      _selectedStageId = currentStage.id;
      _isLoading = false;
    });
  }

  void _onSeasonChanged(CropSeasonType season) {
    if (_selectedSeason == season) return;
    setState(() => _selectedSeason = season);
    _loadData();
  }

  Future<void> _confirmCompleteAdvisory(AdvisoryItem item) async {
    final KisanSemanticColors colors = context.kisanColors;
    final TextEditingController noteController = TextEditingController();

    final bool? confirmed = await showModalBottomSheet<bool>(
      context: context,
      isScrollControlled: true,
      builder: (BuildContext sheetContext) {
        return Padding(
          padding: EdgeInsets.only(
            left: DesignTokens.space24,
            right: DesignTokens.space24,
            top: DesignTokens.space24,
            bottom: MediaQuery.of(sheetContext).viewInsets.bottom +
                DesignTokens.space24,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  const KisanStatusBadge(
                    label: 'LOG FIELD ACTION',
                    tone: KisanBadgeTone.success,
                    icon: Icons.task_alt_rounded,
                  ),
                  Text(
                    '+${item.barkatMaundGain.toStringAsFixed(1)} Maunds Est.',
                    style: KisanTypography.microLabel(
                      color: colors.accentAmber,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: DesignTokens.space12),
              UrduText(
                item.titleUr,
                style: KisanTypography.urduTitle(color: colors.textPrimary),
              ),
              const SizedBox(height: DesignTokens.space4),
              Text(
                item.titleEn,
                style: KisanTypography.body(color: colors.textSecondary),
              ),
              const SizedBox(height: DesignTokens.space16),
              TextField(
                controller: noteController,
                decoration: InputDecoration(
                  hintText: 'Optional field note (e.g., 50 kg Akbar-19 drilled)',
                  hintStyle: KisanTypography.body(color: colors.textTertiary),
                  filled: true,
                  fillColor: colors.subtleSurface,
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: DesignTokens.space16,
                    vertical: DesignTokens.space12,
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
                    borderSide: BorderSide(color: colors.border),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
                    borderSide: BorderSide(
                      color: colors.brandPrimary,
                      width: DesignTokens.borderWidthEmphasis,
                    ),
                  ),
                ),
              ),
              const SizedBox(height: DesignTokens.space20),
              Row(
                children: <Widget>[
                  Expanded(
                    child: KisanButton(
                      label: 'Cancel',
                      variant: KisanButtonVariant.secondary,
                      onPressed: () => Navigator.of(sheetContext).pop(false),
                    ),
                  ),
                  const SizedBox(width: DesignTokens.space12),
                  Expanded(
                    flex: 2,
                    child: KisanButton(
                      label: 'Confirm Done',
                      urduLabel: 'مکمل کر لیا',
                      icon: Icons.check_rounded,
                      onPressed: () => Navigator.of(sheetContext).pop(true),
                    ),
                  ),
                ],
              ),
            ],
          ),
        );
      },
    );

    if (confirmed == true) {
      await widget.repository.markAdvisoryCompleted(
        advisoryId: item.id,
        note: noteController.text.trim(),
      );
      await _loadData();
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          behavior: SnackBarBehavior.floating,
          backgroundColor: DesignTokens.slatePrimary,
          content: Text(
            'Action logged to season diary · +${item.barkatMaundGain.toStringAsFixed(1)} maunds protected',
            style: KisanTypography.body(color: Colors.white),
          ),
        ),
      );
    }
  }

  void _showBarkatBasisSheet() {
    final FarmerProfile? profile = _profile;
    if (profile == null) return;
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
              const KisanStatusBadge(
                label: 'BARKAT YIELD PROTECTION',
                tone: KisanBadgeTone.act,
                icon: Icons.verified_outlined,
              ),
              const SizedBox(height: DesignTokens.space12),
              Row(
                crossAxisAlignment: CrossAxisAlignment.baseline,
                textBaseline: TextBaseline.alphabetic,
                children: <Widget>[
                  Text(
                    '${profile.barkatSavedMaunds.toStringAsFixed(1)} Maunds',
                    style:
                        KisanTypography.displayMetric(color: colors.textPrimary),
                  ),
                  const SizedBox(width: DesignTokens.space8),
                  Text(
                    'protected this cycle',
                    style: KisanTypography.body(color: colors.textSecondary),
                  ),
                ],
              ),
              const SizedBox(height: DesignTokens.space12),
              UrduText(
                'مجموعی برکت: بروقت وتر پر بوائی، بارش کے بعد کھاد اور غیر ضروری اسپرے سے بچت کے ذریعے محفوظ کی گئی پیداوار کا تخمینہ۔',
                style: KisanTypography.urduBody(color: colors.textPrimary),
              ),
              const SizedBox(height: DesignTokens.space8),
              Text(
                'Calculated from BARI Chakwal rainfed loss-avoidance models across your 4.0-acre plot.',
                style: KisanTypography.body(color: colors.textSecondary),
              ),
              const SizedBox(height: DesignTokens.space20),
              KisanButton(
                label: 'Close',
                variant: KisanButtonVariant.secondary,
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

    if (_isLoading && _profile == null) {
      return Center(
        child: CircularProgressIndicator(color: colors.brandPrimary),
      );
    }

    final FarmerProfile profile = _profile!;
    final WeatherSoilSummary telemetry = _telemetry!;

    return RefreshIndicator(
      color: colors.brandPrimary,
      onRefresh: _loadData,
      child: ListView(
        padding: const EdgeInsets.fromLTRB(
          DesignTokens.space16,
          DesignTokens.space16,
          DesignTokens.space16,
          DesignTokens.space32,
        ),
        children: <Widget>[
          // 1. Farmer Header & Barkat Metric Pill
          _FarmerGreetingHeader(
            profile: profile,
            onTapBarkat: _showBarkatBasisSheet,
          ),
          const SizedBox(height: DesignTokens.space16),

          // 2. Weather & Soil Moisture Quick-Stat Bar
          _WeatherSoilStatBar(telemetry: telemetry),
          const SizedBox(height: DesignTokens.space24),

          // 3. Season Switcher (Rabi / Kharif)
          _SeasonSegmentedControl(
            selectedSeason: _selectedSeason,
            onChanged: _onSeasonChanged,
          ),
          const SizedBox(height: DesignTokens.space24),

          // 4. Priority Advisories Section
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Text(
                'FIELD ADVISORIES',
                style: KisanTypography.microLabel(color: colors.textSecondary),
              ),
              UrduText(
                'آج کے زرعی مشورے',
                style: KisanTypography.urduLabel(
                  color: colors.textSecondary,
                  fontSize: 13,
                ),
              ),
            ],
          ),
          const SizedBox(height: DesignTokens.space12),
          ..._advisories.map(
            (AdvisoryItem item) => Padding(
              padding: const EdgeInsets.only(bottom: DesignTokens.space12),
              child: _MinimalistAdvisoryCard(
                advisory: item,
                isPlayingAudio: _playingAdvisoryId == item.id,
                onToggleAudio: () {
                  setState(() {
                    _playingAdvisoryId =
                        _playingAdvisoryId == item.id ? null : item.id;
                  });
                },
                onMarkDone: () => _confirmCompleteAdvisory(item),
              ),
            ),
          ),

          const SizedBox(height: DesignTokens.space16),

          // 5. Seasonal Crop Roadmap (Vertical Timeline Node System)
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(
                    'SEASONAL ROADMAP',
                    style:
                        KisanTypography.microLabel(color: colors.textSecondary),
                  ),
                  const SizedBox(height: DesignTokens.space2),
                  Text(
                    _selectedSeason.cropSummary,
                    style: KisanTypography.title(color: colors.textPrimary),
                  ),
                ],
              ),
              UrduText(
                _selectedSeason.labelUr,
                style: KisanTypography.urduLabel(
                  color: colors.brandPrimary,
                  fontSize: 13.5,
                ),
              ),
            ],
          ),
          const SizedBox(height: DesignTokens.space12),
          KisanCard(
            padding: const EdgeInsets.symmetric(
              horizontal: DesignTokens.space16,
              vertical: DesignTokens.space12,
            ),
            child: Column(
              children: List<Widget>.generate(_stages.length, (int index) {
                final CropStageNode node = _stages[index];
                final bool isLast = index == _stages.length - 1;
                return _RoadmapTimelineNodeTile(
                  node: node,
                  isSelected: _selectedStageId == node.id,
                  isLast: isLast,
                  onTap: () => setState(() => _selectedStageId = node.id),
                );
              }),
            ),
          ),
        ],
      ),
    );
  }
}

class _FarmerGreetingHeader extends StatelessWidget {
  const _FarmerGreetingHeader({
    required this.profile,
    required this.onTapBarkat,
  });

  final FarmerProfile profile;
  final VoidCallback onTapBarkat;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: <Widget>[
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              UrduText(
                'السلام علیکم، ${profile.nameUr}',
                textAlign: TextAlign.left,
                style: KisanTypography.urduHeadline(
                  color: colors.textPrimary,
                  fontSize: 19,
                ),
              ),
              const SizedBox(height: DesignTokens.space2),
              Text(
                '${profile.villageEn}, ${profile.tehsilEn} · ${profile.plotNameEn} (${profile.areaAcres.toStringAsFixed(1)} Ac)',
                style: KisanTypography.body(color: colors.textSecondary)
                    .copyWith(fontSize: 13),
              ),
            ],
          ),
        ),
        const SizedBox(width: DesignTokens.space12),
        ConstrainedBox(
          constraints: const BoxConstraints(
            minHeight: DesignTokens.minTouchTarget,
          ),
          child: KisanCard(
            onTap: onTapBarkat,
            backgroundColor: colors.accentSubtle,
            borderColor: colors.accentBorder,
            borderRadius: DesignTokens.radiusSm,
            padding: const EdgeInsets.symmetric(
              horizontal: DesignTokens.space12,
              vertical: DesignTokens.space8,
            ),
            semanticLabel: 'Barkat yield saved',
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Icon(
                  Icons.workspace_premium_outlined,
                  size: 16,
                  color: colors.accentAmber,
                ),
                const SizedBox(width: DesignTokens.space8),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    Text(
                      'BARKAT',
                      style: KisanTypography.microLabel(
                        color: colors.accentAmber,
                      ).copyWith(fontSize: 10),
                    ),
                    Text(
                      '${profile.barkatSavedMaunds.toStringAsFixed(1)} Maund',
                      style: KisanTypography.metricValue(
                        color: colors.textPrimary,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _WeatherSoilStatBar extends StatelessWidget {
  const _WeatherSoilStatBar({required this.telemetry});

  final WeatherSoilSummary telemetry;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return KisanCard(
      padding: const EdgeInsets.symmetric(
        horizontal: DesignTokens.space12,
        vertical: DesignTokens.space16,
      ),
      child: IntrinsicHeight(
        child: Row(
          children: <Widget>[
            Expanded(
              child: _StatCell(
                label: 'WEATHER',
                value: '${telemetry.temperatureC}°C',
                sublabel: telemetry.conditionLabel,
                icon: Icons.wb_sunny_outlined,
                accentColor: colors.textPrimary,
              ),
            ),
            VerticalDivider(color: colors.border, width: DesignTokens.space16),
            Expanded(
              child: _StatCell(
                label: 'SMAP SOIL',
                value: '${telemetry.soilMoisturePct}%',
                sublabel: telemetry.wattarStatus,
                icon: Icons.water_drop_outlined,
                accentColor: colors.brandPrimary,
              ),
            ),
            VerticalDivider(color: colors.border, width: DesignTokens.space16),
            Expanded(
              child: _StatCell(
                label: 'RAIN (48H)',
                value: '${telemetry.rainForecastMm} mm',
                sublabel: telemetry.rainWindowLabel,
                icon: Icons.grain_rounded,
                accentColor: colors.infoForeground,
              ),
            ),
            VerticalDivider(color: colors.border, width: DesignTokens.space16),
            Expanded(
              child: _StatCell(
                label: 'HEAT UNITS',
                value: '${telemetry.cumulativeGdd}',
                sublabel: 'GDD Cumul.',
                icon: Icons.thermostat_outlined,
                accentColor: colors.accentAmber,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatCell extends StatelessWidget {
  const _StatCell({
    required this.label,
    required this.value,
    required this.sublabel,
    required this.icon,
    required this.accentColor,
  });

  final String label;
  final String value;
  final String sublabel;
  final IconData icon;
  final Color accentColor;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Row(
          children: <Widget>[
            Icon(icon, size: 13, color: colors.textSecondary),
            const SizedBox(width: DesignTokens.space4),
            Flexible(
              child: Text(
                label,
                overflow: TextOverflow.ellipsis,
                style: KisanTypography.microLabel(color: colors.textSecondary)
                    .copyWith(fontSize: 10),
              ),
            ),
          ],
        ),
        const SizedBox(height: DesignTokens.space8),
        Text(
          value,
          style: KisanTypography.metricValue(
            color: accentColor,
            fontSize: 17,
          ),
        ),
        const SizedBox(height: DesignTokens.space2),
        Text(
          sublabel,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: KisanTypography.body(color: colors.textSecondary)
              .copyWith(fontSize: 11.5),
        ),
      ],
    );
  }
}

class _SeasonSegmentedControl extends StatelessWidget {
  const _SeasonSegmentedControl({
    required this.selectedSeason,
    required this.onChanged,
  });

  final CropSeasonType selectedSeason;
  final ValueChanged<CropSeasonType> onChanged;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Container(
      padding: const EdgeInsets.all(DesignTokens.space4),
      decoration: BoxDecoration(
        color: colors.subtleSurface,
        borderRadius: BorderRadius.circular(DesignTokens.radiusMd),
        border: Border.all(color: colors.border, width: DesignTokens.borderWidth),
      ),
      child: Row(
        children: CropSeasonType.values.map((CropSeasonType season) {
          final bool selected = season == selectedSeason;
          return Expanded(
            child: ConstrainedBox(
              constraints: const BoxConstraints(
                minHeight: DesignTokens.minTouchTarget - 4,
              ),
              child: Material(
                color: selected ? colors.cardSurface : Colors.transparent,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
                  side: selected
                      ? BorderSide(
                          color: colors.border,
                          width: DesignTokens.borderWidth,
                        )
                      : BorderSide.none,
                ),
                clipBehavior: Clip.antiAlias,
                child: InkWell(
                  onTap: () => onChanged(season),
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: DesignTokens.space12,
                      vertical: DesignTokens.space8,
                    ),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        Text(
                          season.labelEn,
                          style: KisanTypography.bodyMedium(
                            color: selected
                                ? colors.textPrimary
                                : colors.textSecondary,
                          ).copyWith(
                            fontWeight:
                                selected ? FontWeight.w700 : FontWeight.w500,
                            fontSize: 13,
                          ),
                        ),
                        Text(
                          season.cropSummary,
                          style: KisanTypography.microLabel(
                            color: selected
                                ? colors.brandPrimary
                                : colors.textTertiary,
                          ).copyWith(letterSpacing: 0.1, fontSize: 10.5),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

class _MinimalistAdvisoryCard extends StatelessWidget {
  const _MinimalistAdvisoryCard({
    required this.advisory,
    required this.isPlayingAudio,
    required this.onToggleAudio,
    required this.onMarkDone,
  });

  final AdvisoryItem advisory;
  final bool isPlayingAudio;
  final VoidCallback onToggleAudio;
  final VoidCallback onMarkDone;

  KisanBadgeTone get _tone {
    if (advisory.isCompleted) return KisanBadgeTone.success;
    switch (advisory.severity) {
      case AdvisorySeverity.info:
        return KisanBadgeTone.info;
      case AdvisorySeverity.act:
        return KisanBadgeTone.act;
      case AdvisorySeverity.alert:
        return KisanBadgeTone.alert;
    }
  }

  String get _badgeLabel {
    if (advisory.isCompleted) return 'COMPLETED';
    switch (advisory.severity) {
      case AdvisorySeverity.info:
        return 'INFO · GUIDANCE';
      case AdvisorySeverity.act:
        return 'ACT · RECOMMENDED';
      case AdvisorySeverity.alert:
        return 'ALERT · WEATHER';
    }
  }

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    return KisanCard(
      borderColor:
          advisory.isCompleted ? colors.brandBorder : colors.border,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          // Top Badge Row + Deadline + Listen Button
          Row(
            children: <Widget>[
              KisanStatusBadge(label: _badgeLabel, tone: _tone),
              const SizedBox(width: DesignTokens.space8),
              Text(
                'By ${advisory.actByDate}',
                style: KisanTypography.microLabel(color: colors.textSecondary),
              ),
              const Spacer(),
              KisanIconAction(
                icon: isPlayingAudio
                    ? Icons.stop_circle_outlined
                    : Icons.volume_up_outlined,
                tooltip: isPlayingAudio ? 'Stop audio' : 'Listen in Urdu',
                isActive: isPlayingAudio,
                onTap: onToggleAudio,
              ),
            ],
          ),
          const SizedBox(height: DesignTokens.space12),

          // Urdu Primary Headline & Body
          UrduText(
            advisory.titleUr,
            style: KisanTypography.urduTitle(
              color: colors.textPrimary,
              fontSize: 16.5,
            ),
          ),
          const SizedBox(height: DesignTokens.space4),
          Text(
            advisory.titleEn,
            style: KisanTypography.bodyMedium(color: colors.textPrimary),
          ),
          const SizedBox(height: DesignTokens.space8),
          UrduText(
            advisory.bodyUr,
            style: KisanTypography.urduBody(
              color: colors.textSecondary,
              fontSize: 14,
            ),
          ),
          const SizedBox(height: DesignTokens.space8),
          Text(
            advisory.bodyEn,
            style: KisanTypography.body(color: colors.textSecondary)
                .copyWith(fontSize: 13),
          ),

          // Past Season Personalized Memory Callout
          if (advisory.pastSeasonNoteUr != null) ...<Widget>[
            const SizedBox(height: DesignTokens.space12),
            Container(
              padding: const EdgeInsets.all(DesignTokens.space12),
              decoration: BoxDecoration(
                color: colors.subtleSurface,
                borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
                border: Border.all(
                  color: colors.border,
                  width: DesignTokens.borderWidth,
                ),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Icon(
                    Icons.history_rounded,
                    size: 16,
                    color: colors.textSecondary,
                  ),
                  const SizedBox(width: DesignTokens.space8),
                  Expanded(
                    child: UrduText(
                      advisory.pastSeasonNoteUr!,
                      style: KisanTypography.urduBody(
                        color: colors.textPrimary,
                        fontSize: 13,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],

          const SizedBox(height: DesignTokens.space16),
          Divider(color: colors.border, height: 1),
          const SizedBox(height: DesignTokens.space12),

          // Provenance Micro-Chips + Action CTA
          Wrap(
            spacing: DesignTokens.space8,
            runSpacing: DesignTokens.space8,
            crossAxisAlignment: WrapCrossAlignment.center,
            alignment: WrapAlignment.spaceBetween,
            children: <Widget>[
              Wrap(
                spacing: DesignTokens.space8,
                runSpacing: DesignTokens.space4,
                children: advisory.sources.map((String src) {
                  return Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: DesignTokens.space8,
                      vertical: DesignTokens.space4,
                    ),
                    decoration: BoxDecoration(
                      color: colors.subtleSurface,
                      borderRadius: BorderRadius.circular(DesignTokens.radiusXs),
                    ),
                    child: Text(
                      src,
                      style: KisanTypography.microLabel(
                        color: colors.textSecondary,
                      ).copyWith(fontSize: 10, letterSpacing: 0.2),
                    ),
                  );
                }).toList(),
              ),
              if (!advisory.isCompleted)
                KisanButton(
                  label: 'Log Action',
                  urduLabel: 'عمل مکمل',
                  icon: Icons.check_rounded,
                  onPressed: onMarkDone,
                )
              else
                Text(
                  'Saved +${advisory.barkatMaundGain.toStringAsFixed(1)} Maund',
                  style: KisanTypography.microLabel(color: colors.brandPrimary),
                ),
            ],
          ),
        ],
      ),
    );
  }
}

class _RoadmapTimelineNodeTile extends StatelessWidget {
  const _RoadmapTimelineNodeTile({
    required this.node,
    required this.isSelected,
    required this.isLast,
    required this.onTap,
  });

  final CropStageNode node;
  final bool isSelected;
  final bool isLast;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    final Color nodeColor;
    final Widget nodeIndicator;

    switch (node.status) {
      case StageStatus.done:
        nodeColor = colors.brandPrimary;
        nodeIndicator = Container(
          width: 24,
          height: 24,
          decoration: BoxDecoration(
            color: colors.brandPrimary,
            shape: BoxShape.circle,
          ),
          child: const Icon(Icons.check_rounded, size: 14, color: Colors.white),
        );
      case StageStatus.current:
        nodeColor = colors.brandPrimary;
        nodeIndicator = Container(
          width: 24,
          height: 24,
          decoration: BoxDecoration(
            color: colors.brandSubtle,
            shape: BoxShape.circle,
            border: Border.all(color: colors.brandPrimary, width: 2),
          ),
          child: Center(
            child: Container(
              width: 8,
              height: 8,
              decoration: BoxDecoration(
                color: colors.brandPrimary,
                shape: BoxShape.circle,
              ),
            ),
          ),
        );
      case StageStatus.missed:
        nodeColor = colors.accentAmber;
        nodeIndicator = Container(
          width: 24,
          height: 24,
          decoration: BoxDecoration(
            color: colors.accentSubtle,
            shape: BoxShape.circle,
            border: Border.all(color: colors.accentAmber, width: 1.5),
          ),
          child: Icon(
            Icons.priority_high_rounded,
            size: 13,
            color: colors.accentAmber,
          ),
        );
      case StageStatus.upcoming:
        nodeColor = colors.borderStrong;
        nodeIndicator = Container(
          width: 24,
          height: 24,
          decoration: BoxDecoration(
            color: colors.cardSurface,
            shape: BoxShape.circle,
            border: Border.all(color: colors.borderStrong, width: 1.5),
          ),
          child: Center(
            child: Text(
              '${node.order}',
              style: KisanTypography.microLabel(color: colors.textSecondary)
                  .copyWith(fontSize: 10),
            ),
          ),
        );
    }

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
      child: ConstrainedBox(
        constraints: const BoxConstraints(
          minHeight: DesignTokens.minTouchTarget + 16,
        ),
        child: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              // Left Vertical Timeline Rail
              SizedBox(
                width: 32,
                child: Column(
                  children: <Widget>[
                    const SizedBox(height: DesignTokens.space8),
                    nodeIndicator,
                    if (!isLast)
                      Expanded(
                        child: Container(
                          width: 1.5,
                          margin: const EdgeInsets.symmetric(
                            vertical: DesignTokens.space4,
                          ),
                          color: node.status == StageStatus.done
                              ? colors.brandPrimary.withValues(alpha: 0.45)
                              : colors.border,
                        ),
                      ),
                  ],
                ),
              ),
              const SizedBox(width: DesignTokens.space12),

              // Right Stage Details
              Expanded(
                child: Container(
                  margin: EdgeInsets.only(
                    bottom: isLast ? DesignTokens.space4 : DesignTokens.space12,
                  ),
                  padding: const EdgeInsets.all(DesignTokens.space12),
                  decoration: BoxDecoration(
                    color: isSelected
                        ? colors.subtleSurface
                        : Colors.transparent,
                    borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
                    border: Border.all(
                      color: isSelected ? colors.borderStrong : Colors.transparent,
                      width: DesignTokens.borderWidth,
                    ),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Row(
                        children: <Widget>[
                          Text(
                            node.dateWindow,
                            style: KisanTypography.microLabel(
                              color: node.status == StageStatus.current
                                  ? colors.brandPrimary
                                  : colors.textSecondary,
                            ),
                          ),
                          const SizedBox(width: DesignTokens.space8),
                          Text(
                            '· ${node.gddTarget}',
                            style: KisanTypography.microLabel(
                              color: colors.textTertiary,
                            ),
                          ),
                          const Spacer(),
                          if (node.status == StageStatus.current)
                            const KisanStatusBadge(
                              label: 'CURRENT',
                              tone: KisanBadgeTone.success,
                              icon: Icons.my_location_rounded,
                            ),
                        ],
                      ),
                      const SizedBox(height: DesignTokens.space4),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: <Widget>[
                          Expanded(
                            child: Text(
                              node.titleEn,
                              style: KisanTypography.bodyMedium(
                                color: nodeColor == colors.borderStrong
                                    ? colors.textPrimary
                                    : colors.textPrimary,
                              ).copyWith(fontWeight: FontWeight.w600),
                            ),
                          ),
                          const SizedBox(width: DesignTokens.space8),
                          Flexible(
                            child: UrduText(
                              node.titleUr,
                              style: KisanTypography.urduLabel(
                                color: colors.textPrimary,
                                fontSize: 13.5,
                              ),
                            ),
                          ),
                        ],
                      ),
                      if (isSelected || node.status == StageStatus.current) ...<Widget>[
                        const SizedBox(height: DesignTokens.space4),
                        Text(
                          node.summaryEn,
                          style: KisanTypography.body(
                            color: colors.textSecondary,
                          ).copyWith(fontSize: 12.5),
                        ),
                      ],
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
