import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'core/network/kisan_api_client.dart';
import 'core/theme/app_theme.dart';
import 'data/repositories/kisan_repository.dart';
import 'data/services/kisan_voice_service.dart';
import 'features/shell/kisan_app_shell.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const KisanSaathiApp());
}

class KisanSaathiApp extends StatefulWidget {
  const KisanSaathiApp({super.key});

  @override
  State<KisanSaathiApp> createState() => _KisanSaathiAppState();
}

class _KisanSaathiAppState extends State<KisanSaathiApp> {
  late final KisanApiClient _apiClient;
  late final IKisanRepository _repository;
  late final IKisanVoiceService _voiceService;
  ThemeMode _themeMode = ThemeMode.light;

  @override
  void initState() {
    super.initState();
    _apiClient = KisanApiClient();
    _repository = KisanRepositoryFactory.create(apiClient: _apiClient);
    _voiceService = KisanVoiceServiceFactory.create(apiClient: _apiClient);
  }

  @override
  void dispose() {
    _apiClient.dispose();
    super.dispose();
  }

  void _toggleThemeMode() {
    setState(() {
      _themeMode =
          _themeMode == ThemeMode.light ? ThemeMode.dark : ThemeMode.light;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Kisan Saathi',
      debugShowCheckedModeBanner: false,
      theme: KisanTheme.light,
      darkTheme: KisanTheme.dark,
      themeMode: _themeMode,
      localizationsDelegates: const <LocalizationsDelegate<dynamic>>[
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: const <Locale>[
        Locale('en', 'US'),
        Locale('ur', 'PK'),
      ],
      home: KisanAppShell(
        repository: _repository,
        voiceService: _voiceService,
        themeMode: _themeMode,
        onToggleTheme: _toggleThemeMode,
      ),
    );
  }
}
