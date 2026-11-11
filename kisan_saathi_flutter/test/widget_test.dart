import 'package:flutter_test/flutter_test.dart';
import 'package:kisan_saathi/main.dart';

void main() {
  testWidgets('Kisan Saathi renders Roadmap, Voice AI, and Field Health tabs',
      (WidgetTester tester) async {
    await tester.pumpWidget(const KisanSaathiApp());
    await tester.pumpAndSettle();

    expect(find.text('Kisan Saathi'), findsOneWidget);
    expect(find.text('SEASONAL ROADMAP'), findsOneWidget);

    await tester.tap(find.textContaining('Voice AI'));
    await tester.pumpAndSettle();
    expect(find.textContaining('Tap to Speak'), findsOneWidget);

    await tester.tap(find.textContaining('Field Health'));
    await tester.pumpAndSettle();
    expect(find.text('CROP VIGOR (NDVI · HLS 30M)'), findsOneWidget);
  });
}
