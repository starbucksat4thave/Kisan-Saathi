import 'dart:async';

import '../../core/config/api_config.dart';
import '../../core/network/kisan_api_client.dart';
import '../models/kisan_models.dart';

/// Contract for Voice AI transcription & agronomy question answering.
abstract class IKisanVoiceService {
  List<VoicePromptSuggestion> getQuickPrompts();

  Future<String> simulateOrTranscribeVoice();

  Future<VoiceConversationEntry> askAgronomyAssistant({
    required String queryUr,
    String village = 'Dhudial, Chakwal',
    String cropStage = 'Sowing · 24% Wattar',
  });
}

abstract final class KisanVoiceServiceFactory {
  static IKisanVoiceService create({KisanApiClient? apiClient}) {
    if (ApiConfig.useMockBackend) {
      return MockKisanVoiceService();
    }
    return RemoteKisanVoiceService(
      apiClient: apiClient ?? KisanApiClient(),
      fallback: MockKisanVoiceService(),
    );
  }
}

class RemoteKisanVoiceService implements IKisanVoiceService {
  RemoteKisanVoiceService({
    required this.apiClient,
    required this.fallback,
  });

  final KisanApiClient apiClient;
  final MockKisanVoiceService fallback;

  @override
  List<VoicePromptSuggestion> getQuickPrompts() => fallback.getQuickPrompts();

  @override
  Future<String> simulateOrTranscribeVoice() async {
    return fallback.simulateOrTranscribeVoice();
  }

  @override
  Future<VoiceConversationEntry> askAgronomyAssistant({
    required String queryUr,
    String village = 'Dhudial, Chakwal',
    String cropStage = 'Sowing · 24% Wattar',
  }) async {
    try {
      final Map<String, dynamic> json = await apiClient.postJson(
        ApiConfig.voiceAsk,
        body: <String, dynamic>{
          'query': queryUr,
          'village': village,
          'crop_stage': cropStage,
          'language': 'ur',
        },
      );
      return VoiceConversationEntry.fromJson(json);
    } catch (_) {
      return fallback.askAgronomyAssistant(
        queryUr: queryUr,
        village: village,
        cropStage: cropStage,
      );
    }
  }
}

class MockKisanVoiceService implements IKisanVoiceService {
  int _sampleCursor = 0;

  static const List<String> _sampleVoiceCaptures = <String>[
    'کھیت میں وتر کیسا ہے، کیا کل گندم کی بوائی شروع کر دیں؟',
    'بارانی گندم میں یوریا کھاد کس وقت اور کتنی مقدار میں ڈالنی چاہیے؟',
    'پتوں پر سست تیلہ نظر آئے تو کب اسپرے کرنا چاہیے؟',
  ];

  @override
  List<VoicePromptSuggestion> getQuickPrompts() {
    return const <VoicePromptSuggestion>[
      VoicePromptSuggestion(
        id: 'p_wattar',
        labelUr: 'بوائی کے لیے وتر چیک کریں',
        labelEn: 'Check sowing wattar',
        queryUr: 'کھیت میں وتر کیسا ہے، کیا کل گندم کی بوائی شروع کر دیں؟',
      ),
      VoicePromptSuggestion(
        id: 'p_urea',
        labelUr: 'یوریا کھاد کا صحیح وقت',
        labelEn: 'Urea timing after rain',
        queryUr:
            'بارانی گندم میں یوریا کھاد کس وقت اور کتنی مقدار میں ڈالنی چاہیے؟',
      ),
      VoicePromptSuggestion(
        id: 'p_aphid',
        labelUr: 'سست تیلے کا معائنہ',
        labelEn: 'Aphid spray threshold',
        queryUr: 'پتوں پر سست تیلہ نظر آئے تو کب اسپرے کرنا چاہیے؟',
      ),
      VoicePromptSuggestion(
        id: 'p_rain',
        labelUr: 'جمعرات کی بارش کا مشورہ',
        labelEn: 'Thursday rain forecast',
        queryUr: 'جمعرات کی متوقع بارش سے پہلے کھیت میں کیا احتیاط کریں؟',
      ),
    ];
  }

  @override
  Future<String> simulateOrTranscribeVoice() async {
    await Future<void>.delayed(const Duration(milliseconds: 650));
    final String transcript =
        _sampleVoiceCaptures[_sampleCursor % _sampleVoiceCaptures.length];
    _sampleCursor++;
    return transcript;
  }

  @override
  Future<VoiceConversationEntry> askAgronomyAssistant({
    required String queryUr,
    String village = 'Dhudial, Chakwal',
    String cropStage = 'Sowing · 24% Wattar',
  }) async {
    await Future<void>.delayed(const Duration(milliseconds: 850));

    if (queryUr.contains('وتر') || queryUr.contains('بوائی')) {
      return const VoiceConversationEntry(
        id: 'ai_wattar',
        isFarmer: false,
        textUr:
            'راشد بھائی، ناسا SMAP سیٹلائٹ کے مطابق آپ کے بڑے کھیت میں اس وقت 24 فیصد وتر موجود ہے جو گندم (اکبر-۱۹) کے اگاؤ کے لیے بہترین ہے۔ جمعرات کی شام 12 ملی میٹر بارش سے پہلے ڈرل کے ذریعے بوائی مکمل کر لیں۔',
        summaryEn:
            'SMAP moisture is 24% (optimal). Complete Akbar-19 seed drilling at 50 kg/acre before Thursday’s 12 mm rain.',
        timestampLabel: 'Grounded · SMAP + IMERG',
        sources: <String>['SMAP 0.24 m³/m³', 'IMERG 48h', 'BARI Chakwal'],
      );
    }

    if (queryUr.contains('یوریا') || queryUr.contains('کھاد')) {
      return const VoiceConversationEntry(
        id: 'ai_urea',
        isFarmer: false,
        textUr:
            'بارانی زمین میں خشک مٹی پر یوریا کھاد ہرگز نہ ڈالیں ورنہ گیس بن کر ضائع ہو جاتی ہے۔ جب شاخیں نکلنے کے مرحلے (450 GDD) پر کم از کم 5 ملی میٹر ہلکی بارش ہو جائے تو فوراً بعد ایک بوری یوریا فی ایکڑ چھٹہ دیں۔',
        summaryEn:
            'Avoid dry-soil broadcasting. Apply 1 bag/acre urea strictly after a >=5 mm shower during tillering (450 GDD).',
        timestampLabel: 'Grounded · NASA POWER',
        sources: <String>['NASA POWER GDD', 'Soil Loam Guide'],
      );
    }

    if (queryUr.contains('تیلہ') || queryUr.contains('اسپرے')) {
      return const VoiceConversationEntry(
        id: 'ai_aphid',
        isFarmer: false,
        textUr:
            'اسپرے میں جلد بازی نہ کریں۔ کھیت کے چار کونوں سے ۲۰ پودے چیک کریں۔ اگر فی سٹہ ۱۰ سے کم سست تیلے ہوں تو کیمیائی اسپرے کی ضرورت نہیں—لیڈی برڈ بیٹل (دوست کیڑے) انہیں قدرتی طور پر صاف کر دیتے ہیں اور آپ کا خرچ بچتا ہے۔',
        summaryEn:
            'Scout 20 tillers across 4 corners. Skip chemical spray if count is under 10 aphids/tiller to preserve beneficial ladybirds.',
        timestampLabel: 'Grounded · IPM Threshold',
        sources: <String>['Pest Warning Punjab', 'HLS Canopy'],
      );
    }

    return const VoiceConversationEntry(
      id: 'ai_general',
      isFarmer: false,
      textUr:
          'جمعرات کی متوقع بارش (12 ملی میٹر) سے پہلے بیج اور ڈی اے پی کھاد ڈرل کے ذریعے زمین میں پہنچا دیں تاکہ نمی محفوظ رہے اور بیج یکساں اگے۔ مزید رہنمائی کے لیے کھیت کا وتر صبح کے وقت ضرور چیک کریں۔',
      summaryEn:
          'Drill seed + basal DAP ahead of Thursday’s 12 mm shower to lock rootzone moisture and ensure uniform stand.',
      timestampLabel: 'Grounded · Pothohar Advisory',
      sources: <String>['GPM IMERG', 'PMD Chakwal'],
    );
  }
}
