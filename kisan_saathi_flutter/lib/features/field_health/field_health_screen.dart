import 'dart:ui' as ui;

import 'package:flutter/material.dart';

import '../../core/theme/app_theme.dart';
import '../../core/theme/design_tokens.dart';
import '../../core/widgets/kisan_components.dart';
import '../../data/models/kisan_models.dart';
import '../../data/repositories/kisan_repository.dart';

/// Field Health Screen — NASA HLS 30m NDVI Crop Vigor & SMAP Soil Moisture.
class FieldHealthScreen extends StatefulWidget {
  const FieldHealthScreen({
    super.key,
    required this.repository,
  });

  final IKisanRepository repository;

  @override
  State<FieldHealthScreen> createState() => _FieldHealthScreenState();
}

class _FieldHealthScreenState extends State<FieldHealthScreen> {
  List<NdviObservationPoint> _ndviSeries = const <NdviObservationPoint>[];
  SmapFieldHealthMetrics? _smapMetrics;
  int _selectedIndex = 4; // Default highlight at peak Heading / active comparison
  bool _showBestBenchmark = false;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadSatelliteData();
  }

  Future<void> _loadSatelliteData() async {
    setState(() => _isLoading = true);
    final List<NdviObservationPoint> ndvi =
        await widget.repository.fetchNdviSeries(fieldId: 'bara_khet');
    final SmapFieldHealthMetrics smap =
        await widget.repository.fetchSmapMetrics(fieldId: 'bara_khet');
    if (!mounted) return;
    setState(() {
      _ndviSeries = ndvi;
      _smapMetrics = smap;
      _selectedIndex = ndvi.length > 2 ? 2 : 0; // Tillering stage focus
      _isLoading = false;
    });
  }

  void _updateSelectedIndexFromOffset(double dx, double chartWidth) {
    if (_ndviSeries.isEmpty || chartWidth <= 0) return;
    const double leftPad = 36.0;
    const double rightPad = 12.0;
    final double usable = (chartWidth - leftPad - rightPad).clamp(1.0, chartWidth);
    final double ratio = ((dx - leftPad) / usable).clamp(0.0, 1.0);
    final int idx = (ratio * (_ndviSeries.length - 1)).round();
    if (idx != _selectedIndex) {
      setState(() => _selectedIndex = idx);
    }
  }

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;

    if (_isLoading || _smapMetrics == null || _ndviSeries.isEmpty) {
      return Center(
        child: CircularProgressIndicator(color: colors.brandPrimary),
      );
    }

    final SmapFieldHealthMetrics smap = _smapMetrics!;
    final NdviObservationPoint selectedPoint = _ndviSeries[_selectedIndex];
    final double delta = selectedPoint.deltaVsBaseline;
    final String deltaPrefix = delta >= 0 ? '+' : '';

    return RefreshIndicator(
      color: colors.brandPrimary,
      onRefresh: _loadSatelliteData,
      child: ListView(
        padding: const EdgeInsets.fromLTRB(
          DesignTokens.space16,
          DesignTokens.space16,
          DesignTokens.space16,
          DesignTokens.space32,
        ),
        children: <Widget>[
          // 1. Executive Plot & Satellite Provenance Header
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Text(
                      'EARTH OBSERVATION TELEMETRY',
                      style: KisanTypography.microLabel(
                        color: colors.textSecondary,
                      ),
                    ),
                    const SizedBox(height: DesignTokens.space4),
                    Text(
                      'Bara Khet · 4.0 Ac Rainfed',
                      style: KisanTypography.headline(color: colors.textPrimary),
                    ),
                    const SizedBox(height: DesignTokens.space2),
                    Text(
                      smap.satellitePassLabel,
                      style: KisanTypography.body(color: colors.textSecondary)
                          .copyWith(fontSize: 12.5),
                    ),
                  ],
                ),
              ),
              KisanStatusBadge(
                label: smap.isOptimal ? 'OPTIMAL VIGOR' : 'WATCH MOISTURE',
                tone: smap.isOptimal
                    ? KisanBadgeTone.success
                    : KisanBadgeTone.act,
                icon: Icons.satellite_alt_outlined,
              ),
            ],
          ),
          const SizedBox(height: DesignTokens.space16),

          // 2. NDVI (Crop Vigor) Custom Line Chart Card
          KisanCard(
            padding: const EdgeInsets.all(DesignTokens.space16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Text(
                          'CROP VIGOR (NDVI · HLS 30M)',
                          style: KisanTypography.microLabel(
                            color: colors.textSecondary,
                          ),
                        ),
                        const SizedBox(height: DesignTokens.space4),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.baseline,
                          textBaseline: TextBaseline.alphabetic,
                          children: <Widget>[
                            Text(
                              selectedPoint.currentNdvi.toStringAsFixed(2),
                              style: KisanTypography.displayMetric(
                                color: colors.textPrimary,
                              ),
                            ),
                            const SizedBox(width: DesignTokens.space8),
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: DesignTokens.space8,
                                vertical: DesignTokens.space2,
                              ),
                              decoration: BoxDecoration(
                                color: delta >= 0
                                    ? colors.brandSubtle
                                    : colors.accentSubtle,
                                borderRadius: BorderRadius.circular(
                                  DesignTokens.radiusXs,
                                ),
                                border: Border.all(
                                  color: delta >= 0
                                      ? colors.brandBorder
                                      : colors.accentBorder,
                                ),
                              ),
                              child: Text(
                                '$deltaPrefix${delta.toStringAsFixed(2)} vs Baseline',
                                style: KisanTypography.microLabel(
                                  color: delta >= 0
                                      ? colors.brandPrimary
                                      : colors.accentAmber,
                                ).copyWith(fontSize: 10.5),
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: <Widget>[
                        UrduText(
                          selectedPoint.stageUr,
                          style: KisanTypography.urduLabel(
                            color: colors.textPrimary,
                            fontSize: 14,
                          ),
                        ),
                        Text(
                          '${selectedPoint.stageEn} · ${selectedPoint.dateLabel}',
                          style: KisanTypography.microLabel(
                            color: colors.textSecondary,
                          ).copyWith(letterSpacing: 0.1),
                        ),
                      ],
                    ),
                  ],
                ),

                const SizedBox(height: DesignTokens.space16),

                // Interactive Line Chart Canvas
                LayoutBuilder(
                  builder: (BuildContext context, BoxConstraints constraints) {
                    return GestureDetector(
                      behavior: HitTestBehavior.opaque,
                      onTapDown: (TapDownDetails d) =>
                          _updateSelectedIndexFromOffset(
                        d.localPosition.dx,
                        constraints.maxWidth,
                      ),
                      onHorizontalDragUpdate: (DragUpdateDetails d) =>
                          _updateSelectedIndexFromOffset(
                        d.localPosition.dx,
                        constraints.maxWidth,
                      ),
                      child: SizedBox(
                        height: 210,
                        width: double.infinity,
                        child: CustomPaint(
                          painter: _NdviLineChartPainter(
                            points: _ndviSeries,
                            selectedIndex: _selectedIndex,
                            showBestBenchmark: _showBestBenchmark,
                            currentLineColor: DesignTokens.forestGreen,
                            baselineLineColor: colors.textSecondary,
                            benchmarkLineColor: DesignTokens.wheatAmberBright,
                            gridColor: colors.border,
                            labelColor: colors.textSecondary,
                            cardColor: colors.cardSurface,
                          ),
                        ),
                      ),
                    );
                  },
                ),

                const SizedBox(height: DesignTokens.space12),
                Divider(color: colors.border, height: 1),
                const SizedBox(height: DesignTokens.space12),

                // Legend & Optional Best Season Toggle
                Wrap(
                  spacing: DesignTokens.space16,
                  runSpacing: DesignTokens.space8,
                  crossAxisAlignment: WrapCrossAlignment.center,
                  alignment: WrapAlignment.spaceBetween,
                  children: <Widget>[
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        _ChartLegendSwatch(
                          color: DesignTokens.forestGreen,
                          label: 'Current (2026–27)',
                          isDashed: false,
                        ),
                        const SizedBox(width: DesignTokens.space16),
                        _ChartLegendSwatch(
                          color: colors.textSecondary,
                          label: '5-Yr Baseline (${selectedPoint.historicalBaselineNdvi.toStringAsFixed(2)})',
                          isDashed: true,
                        ),
                      ],
                    ),
                    InkWell(
                      borderRadius:
                          BorderRadius.circular(DesignTokens.radiusXs),
                      onTap: () => setState(
                        () => _showBestBenchmark = !_showBestBenchmark,
                      ),
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: DesignTokens.space8,
                          vertical: DesignTokens.space4,
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: <Widget>[
                            Icon(
                              _showBestBenchmark
                                  ? Icons.check_box_outlined
                                  : Icons.check_box_outline_blank_rounded,
                              size: 16,
                              color: colors.accentAmber,
                            ),
                            const SizedBox(width: DesignTokens.space4),
                            Text(
                              '2024 Record',
                              style: KisanTypography.microLabel(
                                color: colors.accentAmber,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          const SizedBox(height: DesignTokens.space16),

          // 3. Soil Moisture (SMAP) Calibrated Gauge Card
          KisanCard(
            padding: const EdgeInsets.all(DesignTokens.space16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Text(
                          'SOIL MOISTURE · NASA SMAP L4',
                          style: KisanTypography.microLabel(
                            color: colors.textSecondary,
                          ),
                        ),
                        const SizedBox(height: DesignTokens.space4),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.baseline,
                          textBaseline: TextBaseline.alphabetic,
                          children: <Widget>[
                            Text(
                              '${smap.rootzoneMoisturePct}%',
                              style: KisanTypography.displayMetric(
                                color: colors.textPrimary,
                              ),
                            ),
                            const SizedBox(width: DesignTokens.space8),
                            Text(
                              '(${smap.volumetricM3M3.toStringAsFixed(2)} m³/m³)',
                              style: KisanTypography.body(
                                color: colors.textSecondary,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    UrduText(
                      'زمین کا وتر (نمی)',
                      style: KisanTypography.urduLabel(
                        color: colors.brandPrimary,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: DesignTokens.space16),

                // Minimalist Calibrated SMAP Progress Gauge
                SizedBox(
                  height: 44,
                  child: CustomPaint(
                    painter: _SmapCalibratedGaugePainter(
                      valuePct: smap.rootzoneMoisturePct.toDouble(),
                      optimalMinPct: smap.optimalMinPct.toDouble(),
                      optimalMaxPct: smap.optimalMaxPct.toDouble(),
                      trackColor: colors.subtleSurface,
                      borderColor: colors.border,
                      optimalZoneColor: colors.brandSubtle,
                      optimalBorderColor: colors.brandBorder,
                      markerColor: DesignTokens.forestGreen,
                      labelColor: colors.textSecondary,
                    ),
                  ),
                ),
                const SizedBox(height: DesignTokens.space8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    Text(
                      'Wilting <12%',
                      style: KisanTypography.microLabel(
                        color: colors.textTertiary,
                      ).copyWith(fontSize: 10),
                    ),
                    Text(
                      'Optimal Wattar (${smap.optimalMinPct}–${smap.optimalMaxPct}%)',
                      style: KisanTypography.microLabel(
                        color: colors.brandPrimary,
                      ).copyWith(fontSize: 10.5),
                    ),
                    Text(
                      'Saturated >32%',
                      style: KisanTypography.microLabel(
                        color: colors.textTertiary,
                      ).copyWith(fontSize: 10),
                    ),
                  ],
                ),

                const SizedBox(height: DesignTokens.space16),
                Divider(color: colors.border, height: 1),
                const SizedBox(height: DesignTokens.space16),

                // Sub-metrics 2x2 Grid
                Row(
                  children: <Widget>[
                    Expanded(
                      child: _SubMetricTile(
                        label: 'SURFACE (0–5 CM)',
                        value: '${smap.surfaceMoisturePct}%',
                        detail: 'Seedbed Layer',
                      ),
                    ),
                    const SizedBox(width: DesignTokens.space12),
                    Expanded(
                      child: _SubMetricTile(
                        label: 'ROOTZONE (0–100 CM)',
                        value: '${smap.rootzoneMoisturePct}%',
                        detail: 'Deep Reserve',
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: DesignTokens.space12),
                Row(
                  children: <Widget>[
                    Expanded(
                      child: _SubMetricTile(
                        label: 'EVAPORATIVE STRESS',
                        value: smap.evaporativeStressIndex.toStringAsFixed(2),
                        detail: 'No Canopy Stress',
                      ),
                    ),
                    const SizedBox(width: DesignTokens.space12),
                    Expanded(
                      child: _SubMetricTile(
                        label: 'SPATIAL FOOTPRINT',
                        value: '30m',
                        detail: smap.resolutionLabel,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          const SizedBox(height: DesignTokens.space16),

          // 4. Plain-Language Agronomic Interpretation Card
          KisanCard(
            backgroundColor: colors.subtleSurface,
            padding: const EdgeInsets.all(DesignTokens.space16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    const KisanStatusBadge(
                      label: 'AGRONOMIC INTERPRETATION',
                      tone: KisanBadgeTone.info,
                      icon: Icons.analytics_outlined,
                    ),
                    UrduText(
                      'خلاصہ رپورٹ',
                      style: KisanTypography.urduLabel(
                        color: colors.textSecondary,
                        fontSize: 13,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: DesignTokens.space12),
                UrduText(
                  smap.interpretationUr,
                  style: KisanTypography.urduBody(
                    color: colors.textPrimary,
                    fontSize: 15,
                  ),
                ),
                const SizedBox(height: DesignTokens.space8),
                Text(
                  smap.interpretationEn,
                  style: KisanTypography.body(color: colors.textSecondary)
                      .copyWith(fontSize: 13),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _ChartLegendSwatch extends StatelessWidget {
  const _ChartLegendSwatch({
    required this.color,
    required this.label,
    required this.isDashed,
  });

  final Color color;
  final String label;
  final bool isDashed;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: <Widget>[
        SizedBox(
          width: 18,
          height: 10,
          child: CustomPaint(
            painter: _LegendLinePainter(color: color, isDashed: isDashed),
          ),
        ),
        const SizedBox(width: DesignTokens.space8),
        Text(
          label,
          style: KisanTypography.microLabel(color: colors.textSecondary)
              .copyWith(letterSpacing: 0.1, fontSize: 11),
        ),
      ],
    );
  }
}

class _LegendLinePainter extends CustomPainter {
  const _LegendLinePainter({required this.color, required this.isDashed});
  final Color color;
  final bool isDashed;

  @override
  void paint(Canvas canvas, Size size) {
    final Paint paint = Paint()
      ..color = color
      ..strokeWidth = 2.0
      ..strokeCap = StrokeCap.round;
    final double y = size.height / 2;
    if (!isDashed) {
      canvas.drawLine(Offset(0, y), Offset(size.width, y), paint);
    } else {
      canvas.drawLine(Offset(0, y), Offset(6, y), paint);
      canvas.drawLine(Offset(11, y), Offset(size.width, y), paint);
    }
  }

  @override
  bool shouldRepaint(covariant _LegendLinePainter oldDelegate) =>
      oldDelegate.color != color || oldDelegate.isDashed != isDashed;
}

class _SubMetricTile extends StatelessWidget {
  const _SubMetricTile({
    required this.label,
    required this.value,
    required this.detail,
  });

  final String label;
  final String value;
  final String detail;

  @override
  Widget build(BuildContext context) {
    final KisanSemanticColors colors = context.kisanColors;
    return Container(
      padding: const EdgeInsets.all(DesignTokens.space12),
      decoration: BoxDecoration(
        color: colors.subtleSurface,
        borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
        border: Border.all(
          color: colors.border,
          width: DesignTokens.borderWidth,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            label,
            style: KisanTypography.microLabel(color: colors.textSecondary)
                .copyWith(fontSize: 10),
          ),
          const SizedBox(height: DesignTokens.space4),
          Text(
            value,
            style: KisanTypography.metricValue(
              color: colors.textPrimary,
              fontSize: 17,
            ),
          ),
          const SizedBox(height: DesignTokens.space2),
          Text(
            detail,
            style: KisanTypography.body(color: colors.textSecondary)
                .copyWith(fontSize: 11.5),
          ),
        ],
      ),
    );
  }
}

/// Executive custom line chart painter for NDVI comparison:
/// - Smooth cubic curve for Current Season (`#15803D`)
/// - Crisp dashed curve for 5-Yr Historical Baseline (`#64748B`)
/// - Optional dotted curve for Record Benchmark (`#D97706`)
class _NdviLineChartPainter extends CustomPainter {
  const _NdviLineChartPainter({
    required this.points,
    required this.selectedIndex,
    required this.showBestBenchmark,
    required this.currentLineColor,
    required this.baselineLineColor,
    required this.benchmarkLineColor,
    required this.gridColor,
    required this.labelColor,
    required this.cardColor,
  });

  final List<NdviObservationPoint> points;
  final int selectedIndex;
  final bool showBestBenchmark;
  final Color currentLineColor;
  final Color baselineLineColor;
  final Color benchmarkLineColor;
  final Color gridColor;
  final Color labelColor;
  final Color cardColor;

  @override
  void paint(Canvas canvas, Size size) {
    if (points.length < 2) return;

    const double leftPad = 36.0;
    const double rightPad = 12.0;
    const double topPad = 12.0;
    const double bottomPad = 28.0;

    final double plotWidth = size.width - leftPad - rightPad;
    final double plotHeight = size.height - topPad - bottomPad;

    const double minNdvi = 0.0;
    const double maxNdvi = 0.90;

    double yForValue(double v) {
      final double normalized =
          ((v - minNdvi) / (maxNdvi - minNdvi)).clamp(0.0, 1.0);
      return topPad + plotHeight * (1.0 - normalized);
    }

    double xForIndex(int i) {
      return leftPad + (i / (points.length - 1)) * plotWidth;
    }

    // 1. Horizontal Y-Axis Gridlines & Labels (0.2, 0.4, 0.6, 0.8)
    final Paint gridPaint = Paint()
      ..color = gridColor
      ..strokeWidth = 1.0;

    for (final double tick in const <double>[0.2, 0.4, 0.6, 0.8]) {
      final double y = yForValue(tick);
      canvas.drawLine(Offset(leftPad, y), Offset(size.width - rightPad, y), gridPaint);

      final TextPainter tp = TextPainter(
        text: TextSpan(
          text: tick.toStringAsFixed(1),
          style: TextStyle(
            color: labelColor,
            fontSize: 10,
            fontWeight: FontWeight.w500,
            fontFeatures: const <ui.FontFeature>[
              ui.FontFeature.tabularFigures(),
            ],
          ),
        ),
        textDirection: TextDirection.ltr,
      )..layout();
      tp.paint(canvas, Offset(0, y - tp.height / 2));
    }

    // 2. X-Axis Stage Labels
    for (int i = 0; i < points.length; i++) {
      // Render alternate or compact labels so there is zero overlap
      if (i % 2 != 0 && i != selectedIndex && i != points.length - 1) continue;
      final double x = xForIndex(i);
      final bool isSelected = i == selectedIndex;
      final TextPainter tp = TextPainter(
        text: TextSpan(
          text: points[i].stageEn,
          style: TextStyle(
            color: isSelected ? currentLineColor : labelColor,
            fontSize: 10,
            fontWeight: isSelected ? FontWeight.w700 : FontWeight.w500,
          ),
        ),
        textDirection: TextDirection.ltr,
      )..layout();

      final double clampedX =
          (x - tp.width / 2).clamp(leftPad, size.width - rightPad - tp.width);
      tp.paint(canvas, Offset(clampedX, size.height - bottomPad + 8));
    }

    List<Offset> buildOffsets(double Function(NdviObservationPoint p) selector) {
      return List<Offset>.generate(
        points.length,
        (int i) => Offset(xForIndex(i), yForValue(selector(points[i]))),
      );
    }

    Path buildSmoothPath(List<Offset> pts) {
      final Path path = Path()..moveTo(pts.first.dx, pts.first.dy);
      for (int i = 0; i < pts.length - 1; i++) {
        final Offset p0 = pts[i];
        final Offset p1 = pts[i + 1];
        final double controlX = (p0.dx + p1.dx) / 2;
        path.cubicTo(controlX, p0.dy, controlX, p1.dy, p1.dx, p1.dy);
      }
      return path;
    }

    // 3. Optional 2024 Record Benchmark Curve (Dotted Amber)
    if (showBestBenchmark) {
      final Path benchPath =
          buildSmoothPath(buildOffsets((NdviObservationPoint p) => p.bestSeasonNdvi));
      _drawDashedPath(
        canvas,
        benchPath,
        Paint()
          ..color = benchmarkLineColor
          ..style = PaintingStyle.stroke
          ..strokeWidth = 1.5
          ..strokeCap = StrokeCap.round,
        dashWidth: 2.5,
        dashSpace: 4.5,
      );
    }

    // 4. Historical Baseline Curve (Dashed Muted Slate)
    final List<Offset> baselineOffsets = buildOffsets(
      (NdviObservationPoint p) => p.historicalBaselineNdvi,
    );
    final Path baselinePath = buildSmoothPath(baselineOffsets);
    _drawDashedPath(
      canvas,
      baselinePath,
      Paint()
        ..color = baselineLineColor.withValues(alpha: 0.75)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.6
        ..strokeCap = StrokeCap.round,
      dashWidth: 5.0,
      dashSpace: 4.0,
    );

    // 5. Current Season Curve + Subtle Single-Hue Wash
    final List<Offset> currentOffsets = buildOffsets(
      (NdviObservationPoint p) => p.currentNdvi,
    );
    final Path currentPath = buildSmoothPath(currentOffsets);

    final Path fillPath = Path.from(currentPath)
      ..lineTo(currentOffsets.last.dx, topPad + plotHeight)
      ..lineTo(currentOffsets.first.dx, topPad + plotHeight)
      ..close();

    canvas.drawPath(
      fillPath,
      Paint()
        ..color = currentLineColor.withValues(alpha: 0.07)
        ..style = PaintingStyle.fill,
    );

    canvas.drawPath(
      currentPath,
      Paint()
        ..color = currentLineColor
        ..style = PaintingStyle.stroke
        ..strokeWidth = 2.5
        ..strokeCap = StrokeCap.round,
    );

    // 6. Selected Stage Vertical Crosshair & Precision Node Markers
    final Offset selectedCurrent = currentOffsets[selectedIndex];
    final Offset selectedBaseline = baselineOffsets[selectedIndex];

    canvas.drawLine(
      Offset(selectedCurrent.dx, topPad),
      Offset(selectedCurrent.dx, topPad + plotHeight),
      Paint()
        ..color = gridColor
        ..strokeWidth = 1.0,
    );

    // Baseline marker dot
    canvas.drawCircle(
      selectedBaseline,
      3.5,
      Paint()..color = baselineLineColor,
    );

    // Current season active marker ring
    canvas.drawCircle(selectedCurrent, 5.5, Paint()..color = currentLineColor);
    canvas.drawCircle(selectedCurrent, 2.5, Paint()..color = cardColor);
  }

  void _drawDashedPath(
    Canvas canvas,
    Path source,
    Paint paint, {
    required double dashWidth,
    required double dashSpace,
  }) {
    for (final ui.PathMetric metric in source.computeMetrics()) {
      double distance = 0.0;
      while (distance < metric.length) {
        final double next = (distance + dashWidth).clamp(0.0, metric.length);
        canvas.drawPath(metric.extractPath(distance, next), paint);
        distance += dashWidth + dashSpace;
      }
    }
  }

  @override
  bool shouldRepaint(covariant _NdviLineChartPainter oldDelegate) {
    return oldDelegate.selectedIndex != selectedIndex ||
        oldDelegate.showBestBenchmark != showBestBenchmark ||
        oldDelegate.points != points ||
        oldDelegate.gridColor != gridColor;
  }
}

/// Calibrated linear soil moisture bar highlighting the 18–30% optimal Barani
/// wattar band and current SMAP reading marker.
class _SmapCalibratedGaugePainter extends CustomPainter {
  const _SmapCalibratedGaugePainter({
    required this.valuePct,
    required this.optimalMinPct,
    required this.optimalMaxPct,
    required this.trackColor,
    required this.borderColor,
    required this.optimalZoneColor,
    required this.optimalBorderColor,
    required this.markerColor,
    required this.labelColor,
  });

  final double valuePct;
  final double optimalMinPct;
  final double optimalMaxPct;
  final Color trackColor;
  final Color borderColor;
  final Color optimalZoneColor;
  final Color optimalBorderColor;
  final Color markerColor;
  final Color labelColor;

  @override
  void paint(Canvas canvas, Size size) {
    const double maxScale = 40.0; // 0% to 40% volumetric moisture
    const double trackHeight = 14.0;
    final double trackTop = (size.height - trackHeight) / 2;

    double xForPct(double pct) =>
        (pct.clamp(0.0, maxScale) / maxScale) * size.width;

    final RRect fullTrack = RRect.fromRectAndRadius(
      Rect.fromLTWH(0, trackTop, size.width, trackHeight),
      const Radius.circular(DesignTokens.radiusXs),
    );

    // Base track + 1dp border
    canvas.drawRRect(fullTrack, Paint()..color = trackColor);
    canvas.drawRRect(
      fullTrack,
      Paint()
        ..color = borderColor
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.0,
    );

    // Optimal Barani Wattar Band (18% - 30%)
    final double optStart = xForPct(optimalMinPct);
    final double optEnd = xForPct(optimalMaxPct);
    final Rect optRect =
        Rect.fromLTWH(optStart, trackTop, optEnd - optStart, trackHeight);
    canvas.drawRect(optRect, Paint()..color = optimalZoneColor);
    canvas.drawRect(
      optRect,
      Paint()
        ..color = optimalBorderColor
        ..style = PaintingStyle.stroke
        ..strokeWidth = 1.0,
    );

    // Current SMAP Reading Needle + Pill Cap
    final double markerX = xForPct(valuePct);
    canvas.drawLine(
      Offset(markerX, trackTop - 7),
      Offset(markerX, trackTop + trackHeight + 7),
      Paint()
        ..color = markerColor
        ..strokeWidth = 3.0
        ..strokeCap = StrokeCap.round,
    );
    canvas.drawCircle(
      Offset(markerX, trackTop + trackHeight / 2),
      5.5,
      Paint()..color = markerColor,
    );
    canvas.drawCircle(
      Offset(markerX, trackTop + trackHeight / 2),
      2.2,
      Paint()..color = Colors.white,
    );
  }

  @override
  bool shouldRepaint(covariant _SmapCalibratedGaugePainter oldDelegate) {
    return oldDelegate.valuePct != valuePct ||
        oldDelegate.trackColor != trackColor;
  }
}
