import '../../core/config/api_config.dart';
import '../../core/network/kisan_api_client.dart';
import '../models/kisan_models.dart';

/// Contract between Flutter UI screens and your data source (Mock or REST API).
abstract class IKisanRepository {
  Future<FarmerProfile> fetchFarmerProfile();

  Future<WeatherSoilSummary> fetchWeatherSoilSummary({
    required CropSeasonType season,
  });

  Future<List<CropStageNode>> fetchRoadmapStages({
    required CropSeasonType season,
  });

  Future<List<AdvisoryItem>> fetchAdvisories({
    required CropSeasonType season,
  });

  Future<double> markAdvisoryCompleted({
    required String advisoryId,
    String? note,
  });

  Future<List<NdviObservationPoint>> fetchNdviSeries({
    required String fieldId,
  });

  Future<SmapFieldHealthMetrics> fetchSmapMetrics({
    required String fieldId,
  });
}

/// Factory returning either [MockKisanRepository] (while backend is being built)
/// or [RemoteKisanRepository] (when `ApiConfig.useMockBackend == false`).
abstract final class KisanRepositoryFactory {
  static IKisanRepository create({KisanApiClient? apiClient}) {
    if (ApiConfig.useMockBackend) {
      return MockKisanRepository();
    }
    return RemoteKisanRepository(
      apiClient: apiClient ?? KisanApiClient(),
      fallback: MockKisanRepository(),
    );
  }
}

/// Production REST implementation calling your backend endpoints with graceful
/// rural-offline fallback.
class RemoteKisanRepository implements IKisanRepository {
  RemoteKisanRepository({
    required this.apiClient,
    required this.fallback,
  });

  final KisanApiClient apiClient;
  final MockKisanRepository fallback;

  @override
  Future<FarmerProfile> fetchFarmerProfile() async {
    try {
      final Map<String, dynamic> json =
          await apiClient.getJson(ApiConfig.farmerProfile);
      return FarmerProfile.fromJson(json);
    } catch (_) {
      return fallback.fetchFarmerProfile();
    }
  }

  @override
  Future<WeatherSoilSummary> fetchWeatherSoilSummary({
    required CropSeasonType season,
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.getJson(
        ApiConfig.fieldTelemetry,
        queryParameters: <String, String>{'season': season.name},
      );
      return WeatherSoilSummary.fromJson(json);
    } catch (_) {
      return fallback.fetchWeatherSoilSummary(season: season);
    }
  }

  @override
  Future<List<CropStageNode>> fetchRoadmapStages({
    required CropSeasonType season,
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.getJson(
        ApiConfig.seasonalRoadmap,
        queryParameters: <String, String>{'season': season.name},
      );
      final List<dynamic> items = json['stages'] as List<dynamic>? ??
          json['data'] as List<dynamic>? ??
          const <dynamic>[];
      return items
          .whereType<Map<String, dynamic>>()
          .map(CropStageNode.fromJson)
          .toList();
    } catch (_) {
      return fallback.fetchRoadmapStages(season: season);
    }
  }

  @override
  Future<List<AdvisoryItem>> fetchAdvisories({
    required CropSeasonType season,
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.getJson(
        ApiConfig.advisories,
        queryParameters: <String, String>{'season': season.name},
      );
      final List<dynamic> items = json['advisories'] as List<dynamic>? ??
          json['data'] as List<dynamic>? ??
          const <dynamic>[];
      return items
          .whereType<Map<String, dynamic>>()
          .map(AdvisoryItem.fromJson)
          .toList();
    } catch (_) {
      return fallback.fetchAdvisories(season: season);
    }
  }

  @override
  Future<double> markAdvisoryCompleted({
    required String advisoryId,
    String? note,
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.postJson(
        ApiConfig.completeAdvisory,
        body: <String, dynamic>{
          'advisory_id': advisoryId,
          if (note != null && note.isNotEmpty) 'note': note,
        },
      );
      return (json['barkat_maund_added'] as num?)?.toDouble() ?? 1.5;
    } catch (_) {
      return fallback.markAdvisoryCompleted(advisoryId: advisoryId, note: note);
    }
  }

  @override
  Future<List<NdviObservationPoint>> fetchNdviSeries({
    required String fieldId,
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.getJson(
        ApiConfig.fieldHealthNdvi,
        queryParameters: <String, String>{'field_id': fieldId},
      );
      final List<dynamic> items = json['points'] as List<dynamic>? ??
          json['data'] as List<dynamic>? ??
          const <dynamic>[];
      return items
          .whereType<Map<String, dynamic>>()
          .map(NdviObservationPoint.fromJson)
          .toList();
    } catch (_) {
      return fallback.fetchNdviSeries(fieldId: fieldId);
    }
  }

  @override
  Future<SmapFieldHealthMetrics> fetchSmapMetrics({
    required String fieldId,
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.getJson(
        ApiConfig.fieldHealthSmap,
        queryParameters: <String, String>{'field_id': fieldId},
      );
      return SmapFieldHealthMetrics.fromJson(json);
    } catch (_) {
      return fallback.fetchSmapMetrics(fieldId: fieldId);
    }
  }
}

/// Seeded Pothohar Barani agricultural dataset used while `useMockBackend` is true.
class MockKisanRepository implements IKisanRepository {
  final Set<String> _completedIds = <String>{};
  double _barkatTotal = 6.0;

  @override
  Future<FarmerProfile> fetchFarmerProfile() async {
    return FarmerProfile(
      id: 'farmer_chakwal_01',
      nameEn: 'Rashid Minhas',
      nameUr: 'راشد بھائی',
      villageEn: 'Dhudial',
      villageUr: 'ڈھڈیال',
      tehsilEn: 'Chakwal',
      plotNameEn: 'Bara Khet',
      plotNameUr: 'بڑا کھیت',
      areaAcres: 4.0,
      irrigationType: 'Rainfed · Barani',
      barkatSavedMaunds: _barkatTotal,
    );
  }

  @override
  Future<WeatherSoilSummary> fetchWeatherSoilSummary({
    required CropSeasonType season,
  }) async {
    if (season == CropSeasonType.kharif) {
      return const WeatherSoilSummary(
        temperatureC: 33,
        conditionLabel: 'Pre-Monsoon',
        soilMoisturePct: 21,
        soilMoistureVolumetric: 0.21,
        wattarStatus: 'Ready for Mung',
        rainForecastMm: 18,
        rainWindowLabel: 'Sat · 72h',
        cumulativeGdd: 310,
      );
    }
    return const WeatherSoilSummary(
      temperatureC: 21,
      conditionLabel: 'Clear · Mild',
      soilMoisturePct: 24,
      soilMoistureVolumetric: 0.24,
      wattarStatus: 'Optimal Wattar',
      rainForecastMm: 12,
      rainWindowLabel: 'Thu · 48h',
      cumulativeGdd: 145,
    );
  }

  @override
  Future<List<CropStageNode>> fetchRoadmapStages({
    required CropSeasonType season,
  }) async {
    if (season == CropSeasonType.kharif) {
      return const <CropStageNode>[
        CropStageNode(
          id: 'kh_prep',
          order: 1,
          titleEn: 'Stubble Mulch & Moisture Lock',
          titleUr: 'گندم کے ناڑھ کی سنبھال اور وتر',
          dateWindow: 'Jun 15 – Jun 30',
          gddTarget: '0 GDD',
          status: StageStatus.done,
          summaryEn: 'Shallow tillage completed to conserve pre-monsoon moisture.',
        ),
        CropStageNode(
          id: 'kh_sow',
          order: 2,
          titleEn: 'Mungbean Sowing (Chakwal-M6)',
          titleUr: 'مونگ کی بوائی (چکوال ایم-۶)',
          dateWindow: 'Jul 01 – Jul 15',
          gddTarget: '180 GDD',
          status: StageStatus.current,
          summaryEn: '60-day nitrogen-fixing pulse replacing bare summer fallow.',
        ),
        CropStageNode(
          id: 'kh_canopy',
          order: 3,
          titleEn: 'Canopy Cover & Weed Check',
          titleUr: 'جڑی بوٹی کی صفائی اور زمین ڈھانپنا',
          dateWindow: 'Jul 25 – Aug 10',
          gddTarget: '520 GDD',
          status: StageStatus.upcoming,
          summaryEn: 'Protects topsoil from heavy August monsoon sheet erosion.',
        ),
        CropStageNode(
          id: 'kh_pod',
          order: 4,
          titleEn: 'Pod Picking & Residue Incorporation',
          titleUr: 'پھلیوں کی چنائی اور زمین کی زرخیزی',
          dateWindow: 'Sep 05 – Sep 20',
          gddTarget: '1,050 GDD',
          status: StageStatus.upcoming,
          summaryEn: 'Saves ~30% basal nitrogen cost for upcoming Rabi wheat.',
        ),
      ];
    }

    return const <CropStageNode>[
      CropStageNode(
        id: 'land_prep',
        order: 1,
        titleEn: 'Land Prep & Moisture Lock',
        titleUr: 'زمین کی تیاری اور وتر کی حفاظت',
        dateWindow: 'Oct 15 – Oct 31',
        gddTarget: '0 GDD',
        status: StageStatus.done,
        summaryEn: 'Deep planking completed after late monsoon moisture.',
      ),
      CropStageNode(
        id: 'sowing',
        order: 2,
        titleEn: 'Sowing · Optimal Wattar Window',
        titleUr: 'بوائی (وتر کا سنہری وقت)',
        dateWindow: 'Nov 01 – Nov 15',
        gddTarget: '0–150 GDD',
        status: StageStatus.current,
        summaryEn: 'Drill Akbar-19 at 50 kg/acre while SMAP moisture is 24%.',
      ),
      CropStageNode(
        id: 'tillering',
        order: 3,
        titleEn: 'Tillering & Rain-Timed Urea',
        titleUr: 'شاخیں نکلنا اور بارش کے بعد یوریا',
        dateWindow: 'Dec 05 – Dec 25',
        gddTarget: '450 GDD',
        status: StageStatus.upcoming,
        summaryEn: 'Top-dress 1 bag urea strictly after light winter shower.',
      ),
      CropStageNode(
        id: 'scouting',
        order: 4,
        titleEn: 'Aphid & Yellow Rust Scouting',
        titleUr: 'سست تیلہ اور پیلی کنگی کا معائنہ',
        dateWindow: 'Feb 05 – Feb 25',
        gddTarget: '1,150 GDD',
        status: StageStatus.upcoming,
        summaryEn: 'Field-border scouting before considering any chemical spray.',
      ),
      CropStageNode(
        id: 'grain_fill',
        order: 5,
        titleEn: 'Grain Filling & Heat Buffer',
        titleUr: 'دانہ بھرائی اور مارچ کی تپش',
        dateWindow: 'Mar 12 – Mar 30',
        gddTarget: '1,550 GDD',
        status: StageStatus.upcoming,
        summaryEn: 'Monitor terminal heat stress via NASA POWER Tmax.',
      ),
      CropStageNode(
        id: 'harvest',
        order: 6,
        titleEn: 'Pre-Storm Harvest Window',
        titleUr: 'بارش سے پہلے بروقت کٹائی',
        dateWindow: 'Apr 10 – Apr 24',
        gddTarget: '2,000 GDD',
        status: StageStatus.upcoming,
        summaryEn: 'Harvest & thresh ahead of April convective hailstorms.',
      ),
    ];
  }

  @override
  Future<List<AdvisoryItem>> fetchAdvisories({
    required CropSeasonType season,
  }) async {
    if (season == CropSeasonType.kharif) {
      return <AdvisoryItem>[
        AdvisoryItem(
          id: 'adv_kh_1',
          stageId: 'kh_sow',
          severity: AdvisorySeverity.act,
          titleUr: 'مونگ کی بوائی ہفتے کی بارش سے پہلے مکمل کریں',
          titleEn: 'Complete Mungbean drilling ahead of Saturday shower',
          bodyUr:
              'کھیت میں وتر 21% موجود ہے۔ چکوال ایم-۶ بیج 8 کلو فی ایکڑ ڈرل کریں تاکہ مونسون کی بارش میں مٹی کا کٹاؤ رکے اور زمین میں قدرتی نائٹروجن شامل ہو۔',
          bodyEn:
              'Rootzone moisture is 21%. Drill Chakwal-M6 at 8 kg/acre to prevent monsoon topsoil runoff and fix atmospheric nitrogen.',
          actByDate: 'Jul 10',
          sources: const <String>['SMAP Rootzone', 'IMERG 72h', 'BARI Chakwal'],
          barkatMaundGain: 2.0,
          pastSeasonNoteUr:
              'پچھلی گرمیوں میں خالی کھیت چھوڑنے سے جولائی کی تیز بارش میں اوپری زرخیز مٹی بہہ گئی تھی۔',
          isCompleted: _completedIds.contains('adv_kh_1'),
        ),
      ];
    }

    return <AdvisoryItem>[
      AdvisoryItem(
        id: 'adv_rabi_act_1',
        stageId: 'sowing',
        severity: AdvisorySeverity.act,
        titleUr: 'زمین کا وتر بہترین ہے — ۳ دن کے اندر گندم کی بوائی مکمل کریں',
        titleEn: 'Soil moisture optimal (24%) — complete wheat sowing by Nov 10',
        bodyUr:
            'ناسا SMAP سیٹلائٹ کے مطابق آپ کے کھیت میں نمی 0.24 m³/m³ ہے جو اگاؤ کے لیے بہترین ہے۔ جمعرات کو 12 ملی میٹر ہلکی بارش کا امکان ہے، اس سے پہلے اکبر-۱۹ بیج ڈرل کر لیں۔',
        bodyEn:
            'NASA SMAP rootzone moisture reads 0.24 m³/m³. Drill Akbar-19 before Thursday’s 12 mm shower for uniform emergence.',
        actByDate: 'Nov 10',
        sources: const <String>['NASA SMAP', 'GPM IMERG', 'PMD Chakwal'],
        barkatMaundGain: 1.5,
        pastSeasonNoteUr:
            'یاد دہانی (۲۰۲۳–۲۴): ۲۵ نومبر کی تاخیر سے بوائی کرنے پر مارچ کی گرمی نے دانہ کمزور کر دیا تھا۔',
        isCompleted: _completedIds.contains('adv_rabi_act_1'),
      ),
      AdvisoryItem(
        id: 'adv_rabi_alert_2',
        stageId: 'harvest',
        severity: AdvisorySeverity.alert,
        titleUr: 'جمعرات کی شام بارش کا امکان — کھلی کھاد اور بیج محفوظ رکھیں',
        titleEn: '12 mm rainfall window approaching Thursday evening',
        bodyUr:
            'بوائی کے وقت ڈی اے پی کھاد بیج کے ساتھ ڈرل کے ذریعے زمین میں دیں؛ اوپر چھٹہ دینے سے بارش کے بہاؤ میں کھاد ضائع ہونے کا خدشہ ہے۔',
        bodyEn:
            'Band-place basal DAP with seed drill rather than surface broadcast prior to Thursday evening rain.',
        actByDate: 'Nov 09',
        sources: const <String>['GPM IMERG', 'Open-Meteo'],
        barkatMaundGain: 1.0,
        isCompleted: _completedIds.contains('adv_rabi_alert_2'),
      ),
      AdvisoryItem(
        id: 'adv_rabi_info_3',
        stageId: 'scouting',
        severity: AdvisorySeverity.info,
        titleUr: 'بیج کو پھپھوندی کش دوا (Seed Treatment) ضرور لگائیں',
        titleEn: 'Apply fungicide seed dressing prior to drilling',
        bodyUr:
            'کانگیاری اور ابتدائی جڑ گلن سے بچاؤ کے لیے فی کلو بیج ۲ گرام ٹیبوکونازول لگا کر سایہ میں خشک کریں۔ اس سے آئندہ اسپرے کا خرچ بچتا ہے۔',
        bodyEn:
            'Treat seed with Tebuconazole (2g/kg) before sowing to prevent loose smut and early root rot in rainfed loam.',
        actByDate: 'Nov 08',
        sources: const <String>['BARI Extension', 'Soil Loam Profile'],
        barkatMaundGain: 0.8,
        isCompleted: _completedIds.contains('adv_rabi_info_3'),
      ),
    ];
  }

  @override
  Future<double> markAdvisoryCompleted({
    required String advisoryId,
    String? note,
  }) async {
    if (!_completedIds.contains(advisoryId)) {
      _completedIds.add(advisoryId);
      _barkatTotal += 1.5;
      return 1.5;
    }
    return 0.0;
  }

  @override
  Future<List<NdviObservationPoint>> fetchNdviSeries({
    required String fieldId,
  }) async {
    return const <NdviObservationPoint>[
      NdviObservationPoint(
        stageEn: 'Sowing',
        stageUr: 'بوائی',
        dateLabel: 'Nov 05',
        currentNdvi: 0.14,
        historicalBaselineNdvi: 0.12,
        bestSeasonNdvi: 0.14,
      ),
      NdviObservationPoint(
        stageEn: 'Emergence',
        stageUr: 'اگاؤ',
        dateLabel: 'Nov 25',
        currentNdvi: 0.25,
        historicalBaselineNdvi: 0.21,
        bestSeasonNdvi: 0.26,
      ),
      NdviObservationPoint(
        stageEn: 'Tillering',
        stageUr: 'شاخیں',
        dateLabel: 'Dec 20',
        currentNdvi: 0.46,
        historicalBaselineNdvi: 0.39,
        bestSeasonNdvi: 0.48,
      ),
      NdviObservationPoint(
        stageEn: 'Jointing',
        stageUr: 'گانٹھ بننا',
        dateLabel: 'Jan 22',
        currentNdvi: 0.61,
        historicalBaselineNdvi: 0.52,
        bestSeasonNdvi: 0.63,
      ),
      NdviObservationPoint(
        stageEn: 'Heading',
        stageUr: 'سٹہ نکلنا',
        dateLabel: 'Feb 18',
        currentNdvi: 0.73,
        historicalBaselineNdvi: 0.64,
        bestSeasonNdvi: 0.76,
      ),
      NdviObservationPoint(
        stageEn: 'Anthesis',
        stageUr: 'دانہ بھرائی',
        dateLabel: 'Mar 15',
        currentNdvi: 0.76,
        historicalBaselineNdvi: 0.68,
        bestSeasonNdvi: 0.79,
      ),
      NdviObservationPoint(
        stageEn: 'Maturity',
        stageUr: 'پکائی',
        dateLabel: 'Apr 12',
        currentNdvi: 0.38,
        historicalBaselineNdvi: 0.34,
        bestSeasonNdvi: 0.39,
      ),
    ];
  }

  @override
  Future<SmapFieldHealthMetrics> fetchSmapMetrics({
    required String fieldId,
  }) async {
    return const SmapFieldHealthMetrics(
      rootzoneMoisturePct: 24,
      surfaceMoisturePct: 21,
      volumetricM3M3: 0.24,
      optimalMinPct: 18,
      optimalMaxPct: 30,
      evaporativeStressIndex: -0.06,
      satellitePassLabel: 'NASA HLS 30m + SMAP L4 · Updated 14h ago',
      resolutionLabel: '30m Spatial · Chakwal Grid',
      interpretationUr:
          'آپ کے کھیت (بڑا کھیت) کی ہریالی (NDVI) ڈھڈیال کے تاریخی اوسط سے 12% بہتر ہے، اور جڑوں کی گہرائی میں وتر 24% یعنی تسلی بخش حد میں موجود ہے۔ فی الحال کسی اضافی آبپاشی یا کیمیائی اسپرے کی ضرورت نہیں۔',
      interpretationEn:
          'Canopy vigor tracks +0.08 NDVI above the 5-year Pothohar median. Rootzone SMAP moisture (24%) sits squarely inside the 18–30% optimal barani wattar band.',
    );
  }
}
