/// Central backend configuration for Kisan Saathi.
///
/// While your backend is under development, keep [useMockBackend] set to `true`
/// so the entire UI/UX works interactively with seeded Pothohar farm data.
/// Once your REST API is deployed, flip [useMockBackend] to `false` and set
/// [baseUrl] to your server address.
abstract final class ApiConfig {
  /// Switch to `false` once your backend server is live.
  static const bool useMockBackend = true;

  /// Base URL of your backend API:
  /// - Android Emulator -> localhost on PC: `http://10.0.2.2:8000/api/v1`
  /// - Physical Android Phone (same Wi-Fi): `http://192.168.1.X:8000/api/v1`
  /// - Production Cloud Server: `https://api.kisansaathi.pk/api/v1`
  static const String baseUrl = 'http://10.0.2.2:8000/api/v1';

  /// Standard request timeout for rural 3G/4G Pothohar connectivity.
  static const Duration timeout = Duration(seconds: 20);

  // --- REST API Endpoints ---
  static const String farmerProfile = '/farmer/profile';
  static const String fieldTelemetry = '/fields/telemetry';
  static const String seasonalRoadmap = '/roadmap/stages';
  static const String advisories = '/roadmap/advisories';
  static const String completeAdvisory = '/roadmap/advisories/complete';
  static const String voiceAsk = '/voice/ask';
  static const String voiceTranscribe = '/voice/transcribe';
  static const String fieldHealthNdvi = '/earth-observation/ndvi';
  static const String fieldHealthSmap = '/earth-observation/smap';
}
