import 'package:flutter/foundation.dart';

enum CropSeasonType {
  rabi('Rabi 2026–27', 'ربیع ۲۰۲۶–۲۷ · گندم', 'Wheat · Akbar-19'),
  kharif('Kharif 2027', 'خریف ۲۰۲۷ · مونگ / جوار', 'Mungbean · Chakwal-M6');

  const CropSeasonType(this.labelEn, this.labelUr, this.cropSummary);
  final String labelEn;
  final String labelUr;
  final String cropSummary;
}

enum StageStatus {
  done,
  current,
  upcoming,
  missed;

  static StageStatus fromString(String value) {
    return StageStatus.values.firstWhere(
      (StageStatus e) => e.name.toLowerCase() == value.toLowerCase(),
      orElse: () => StageStatus.upcoming,
    );
  }
}

enum AdvisorySeverity {
  info,
  act,
  alert;

  static AdvisorySeverity fromString(String value) {
    return AdvisorySeverity.values.firstWhere(
      (AdvisorySeverity e) => e.name.toLowerCase() == value.toLowerCase(),
      orElse: () => AdvisorySeverity.info,
    );
  }
}

@immutable
class FarmerProfile {
  const FarmerProfile({
    required this.id,
    required this.nameEn,
    required this.nameUr,
    required this.villageEn,
    required this.villageUr,
    required this.tehsilEn,
    required this.plotNameEn,
    required this.plotNameUr,
    required this.areaAcres,
    required this.irrigationType,
    required this.barkatSavedMaunds,
  });

  final String id;
  final String nameEn;
  final String nameUr;
  final String villageEn;
  final String villageUr;
  final String tehsilEn;
  final String plotNameEn;
  final String plotNameUr;
  final double areaAcres;
  final String irrigationType;
  final double barkatSavedMaunds;

  factory FarmerProfile.fromJson(Map<String, dynamic> json) {
    return FarmerProfile(
      id: json['id'] as String? ?? 'farmer_01',
      nameEn: json['name_en'] as String? ?? 'Rashid Minhas',
      nameUr: json['name_ur'] as String? ?? 'راشد بھائی',
      villageEn: json['village_en'] as String? ?? 'Dhudial',
      villageUr: json['village_ur'] as String? ?? 'ڈھڈیال',
      tehsilEn: json['tehsil_en'] as String? ?? 'Chakwal',
      plotNameEn: json['plot_name_en'] as String? ?? 'Bara Khet',
      plotNameUr: json['plot_name_ur'] as String? ?? 'بڑا کھیت',
      areaAcres: (json['area_acres'] as num?)?.toDouble() ?? 4.0,
      irrigationType: json['irrigation_type'] as String? ?? 'Rainfed (Barani)',
      barkatSavedMaunds:
          (json['barkat_saved_maunds'] as num?)?.toDouble() ?? 6.0,
    );
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
        'id': id,
        'name_en': nameEn,
        'name_ur': nameUr,
        'village_en': villageEn,
        'village_ur': villageUr,
        'tehsil_en': tehsilEn,
        'plot_name_en': plotNameEn,
        'plot_name_ur': plotNameUr,
        'area_acres': areaAcres,
        'irrigation_type': irrigationType,
        'barkat_saved_maunds': barkatSavedMaunds,
      };

  FarmerProfile copyWith({double? barkatSavedMaunds}) {
    return FarmerProfile(
      id: id,
      nameEn: nameEn,
      nameUr: nameUr,
      villageEn: villageEn,
      villageUr: villageUr,
      tehsilEn: tehsilEn,
      plotNameEn: plotNameEn,
      plotNameUr: plotNameUr,
      areaAcres: areaAcres,
      irrigationType: irrigationType,
      barkatSavedMaunds: barkatSavedMaunds ?? this.barkatSavedMaunds,
    );
  }
}

@immutable
class WeatherSoilSummary {
  const WeatherSoilSummary({
    required this.temperatureC,
    required this.conditionLabel,
    required this.soilMoisturePct,
    required this.soilMoistureVolumetric,
    required this.wattarStatus,
    required this.rainForecastMm,
    required this.rainWindowLabel,
    required this.cumulativeGdd,
  });

  final int temperatureC;
  final String conditionLabel;
  final int soilMoisturePct;
  final double soilMoistureVolumetric;
  final String wattarStatus;
  final int rainForecastMm;
  final String rainWindowLabel;
  final int cumulativeGdd;

  factory WeatherSoilSummary.fromJson(Map<String, dynamic> json) {
    return WeatherSoilSummary(
      temperatureC: (json['temperature_c'] as num?)?.toInt() ?? 22,
      conditionLabel: json['condition_label'] as String? ?? 'Clear',
      soilMoisturePct: (json['soil_moisture_pct'] as num?)?.toInt() ?? 24,
      soilMoistureVolumetric:
          (json['soil_moisture_volumetric'] as num?)?.toDouble() ?? 0.24,
      wattarStatus: json['wattar_status'] as String? ?? 'Optimal Wattar',
      rainForecastMm: (json['rain_forecast_mm'] as num?)?.toInt() ?? 12,
      rainWindowLabel: json['rain_window_label'] as String? ?? 'Thu · 48h',
      cumulativeGdd: (json['cumulative_gdd'] as num?)?.toInt() ?? 145,
    );
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
        'temperature_c': temperatureC,
        'condition_label': conditionLabel,
        'soil_moisture_pct': soilMoisturePct,
        'soil_moisture_volumetric': soilMoistureVolumetric,
        'wattar_status': wattarStatus,
        'rain_forecast_mm': rainForecastMm,
        'rain_window_label': rainWindowLabel,
        'cumulative_gdd': cumulativeGdd,
      };
}

@immutable
class CropStageNode {
  const CropStageNode({
    required this.id,
    required this.order,
    required this.titleEn,
    required this.titleUr,
    required this.dateWindow,
    required this.gddTarget,
    required this.status,
    required this.summaryEn,
  });

  final String id;
  final int order;
  final String titleEn;
  final String titleUr;
  final String dateWindow;
  final String gddTarget;
  final StageStatus status;
  final String summaryEn;

  factory CropStageNode.fromJson(Map<String, dynamic> json) {
    return CropStageNode(
      id: json['id'] as String? ?? '',
      order: (json['order'] as num?)?.toInt() ?? 1,
      titleEn: json['title_en'] as String? ?? '',
      titleUr: json['title_ur'] as String? ?? '',
      dateWindow: json['date_window'] as String? ?? '',
      gddTarget: json['gdd_target'] as String? ?? '',
      status: StageStatus.fromString(json['status'] as String? ?? 'upcoming'),
      summaryEn: json['summary_en'] as String? ?? '',
    );
  }
}

@immutable
class AdvisoryItem {
  const AdvisoryItem({
    required this.id,
    required this.stageId,
    required this.severity,
    required this.titleUr,
    required this.titleEn,
    required this.bodyUr,
    required this.bodyEn,
    required this.actByDate,
    required this.sources,
    required this.barkatMaundGain,
    this.pastSeasonNoteUr,
    this.isCompleted = false,
  });

  final String id;
  final String stageId;
  final AdvisorySeverity severity;
  final String titleUr;
  final String titleEn;
  final String bodyUr;
  final String bodyEn;
  final String actByDate;
  final List<String> sources;
  final double barkatMaundGain;
  final String? pastSeasonNoteUr;
  final bool isCompleted;

  factory AdvisoryItem.fromJson(Map<String, dynamic> json) {
    return AdvisoryItem(
      id: json['id'] as String? ?? '',
      stageId: json['stage_id'] as String? ?? '',
      severity: AdvisorySeverity.fromString(
        json['severity'] as String? ?? 'info',
      ),
      titleUr: json['title_ur'] as String? ?? '',
      titleEn: json['title_en'] as String? ?? '',
      bodyUr: json['body_ur'] as String? ?? '',
      bodyEn: json['body_en'] as String? ?? '',
      actByDate: json['act_by_date'] as String? ?? '',
      sources: (json['sources'] as List<dynamic>?)
              ?.map((dynamic e) => e.toString())
              .toList() ??
          const <String>['SMAP', 'IMERG'],
      barkatMaundGain: (json['barkat_maund_gain'] as num?)?.toDouble() ?? 1.5,
      pastSeasonNoteUr: json['past_season_note_ur'] as String?,
      isCompleted: json['is_completed'] as bool? ?? false,
    );
  }

  AdvisoryItem copyWith({bool? isCompleted}) {
    return AdvisoryItem(
      id: id,
      stageId: stageId,
      severity: severity,
      titleUr: titleUr,
      titleEn: titleEn,
      bodyUr: bodyUr,
      bodyEn: bodyEn,
      actByDate: actByDate,
      sources: sources,
      barkatMaundGain: barkatMaundGain,
      pastSeasonNoteUr: pastSeasonNoteUr,
      isCompleted: isCompleted ?? this.isCompleted,
    );
  }
}

@immutable
class VoicePromptSuggestion {
  const VoicePromptSuggestion({
    required this.id,
    required this.labelUr,
    required this.labelEn,
    required this.queryUr,
  });

  final String id;
  final String labelUr;
  final String labelEn;
  final String queryUr;
}

@immutable
class VoiceConversationEntry {
  const VoiceConversationEntry({
    required this.id,
    required this.isFarmer,
    required this.textUr,
    required this.summaryEn,
    required this.timestampLabel,
    this.sources = const <String>[],
    this.confidencePct,
  });

  final String id;
  final bool isFarmer;
  final String textUr;
  final String summaryEn;
  final String timestampLabel;
  final List<String> sources;
  final int? confidencePct;

  factory VoiceConversationEntry.fromJson(Map<String, dynamic> json) {
    return VoiceConversationEntry(
      id: json['id'] as String? ?? '',
      isFarmer: json['is_farmer'] as bool? ?? false,
      textUr: json['text_ur'] as String? ?? '',
      summaryEn: json['summary_en'] as String? ?? '',
      timestampLabel: json['timestamp_label'] as String? ?? 'Just now',
      sources: (json['sources'] as List<dynamic>?)
              ?.map((dynamic e) => e.toString())
              .toList() ??
          const <String>[],
      confidencePct: (json['confidence_pct'] as num?)?.toInt(),
    );
  }
}

@immutable
class NdviObservationPoint {
  const NdviObservationPoint({
    required this.stageEn,
    required this.stageUr,
    required this.dateLabel,
    required this.currentNdvi,
    required this.historicalBaselineNdvi,
    required this.bestSeasonNdvi,
  });

  final String stageEn;
  final String stageUr;
  final String dateLabel;
  final double currentNdvi;
  final double historicalBaselineNdvi;
  final double bestSeasonNdvi;

  double get deltaVsBaseline => currentNdvi - historicalBaselineNdvi;

  factory NdviObservationPoint.fromJson(Map<String, dynamic> json) {
    return NdviObservationPoint(
      stageEn: json['stage_en'] as String? ?? '',
      stageUr: json['stage_ur'] as String? ?? '',
      dateLabel: json['date_label'] as String? ?? '',
      currentNdvi: (json['current_ndvi'] as num?)?.toDouble() ?? 0.0,
      historicalBaselineNdvi:
          (json['historical_baseline_ndvi'] as num?)?.toDouble() ?? 0.0,
      bestSeasonNdvi: (json['best_season_ndvi'] as num?)?.toDouble() ?? 0.0,
    );
  }
}

@immutable
class SmapFieldHealthMetrics {
  const SmapFieldHealthMetrics({
    required this.rootzoneMoisturePct,
    required this.surfaceMoisturePct,
    required this.volumetricM3M3,
    required this.optimalMinPct,
    required this.optimalMaxPct,
    required this.evaporativeStressIndex,
    required this.satellitePassLabel,
    required this.resolutionLabel,
    required this.interpretationUr,
    required this.interpretationEn,
  });

  final int rootzoneMoisturePct;
  final int surfaceMoisturePct;
  final double volumetricM3M3;
  final int optimalMinPct;
  final int optimalMaxPct;
  final double evaporativeStressIndex;
  final String satellitePassLabel;
  final String resolutionLabel;
  final String interpretationUr;
  final String interpretationEn;

  bool get isOptimal =>
      rootzoneMoisturePct >= optimalMinPct &&
      rootzoneMoisturePct <= optimalMaxPct;

  factory SmapFieldHealthMetrics.fromJson(Map<String, dynamic> json) {
    return SmapFieldHealthMetrics(
      rootzoneMoisturePct:
          (json['rootzone_moisture_pct'] as num?)?.toInt() ?? 24,
      surfaceMoisturePct:
          (json['surface_moisture_pct'] as num?)?.toInt() ?? 21,
      volumetricM3M3: (json['volumetric_m3m3'] as num?)?.toDouble() ?? 0.24,
      optimalMinPct: (json['optimal_min_pct'] as num?)?.toInt() ?? 18,
      optimalMaxPct: (json['optimal_max_pct'] as num?)?.toInt() ?? 30,
      evaporativeStressIndex:
          (json['evaporative_stress_index'] as num?)?.toDouble() ?? -0.08,
      satellitePassLabel:
          json['satellite_pass_label'] as String? ?? 'HLS + SMAP · 36h ago',
      resolutionLabel: json['resolution_label'] as String? ?? '30m Optical',
      interpretationUr: json['interpretation_ur'] as String? ?? '',
      interpretationEn: json['interpretation_en'] as String? ?? '',
    );
  }
}
