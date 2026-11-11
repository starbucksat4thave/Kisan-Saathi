import 'package:flutter/cupertino.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

void main() {
  runApp(const KisanSaathiApp());
}

class KisanSaathiApp extends StatelessWidget {
  const KisanSaathiApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const CupertinoApp(
      debugShowCheckedModeBanner: false,
      locale: Locale('ur'),
      supportedLocales: [Locale('ur'), Locale('en')],
      localizationsDelegates: [
        GlobalCupertinoLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ],
      home: Directionality(
        textDirection: TextDirection.rtl,
        child: CupertinoPageScaffold(
          navigationBar: CupertinoNavigationBar(
            middle: Text('کسان ساتھی'),
          ),
          child: Center(child: Text('MVP scaffold')),
        ),
      ),
    );
  }
}
