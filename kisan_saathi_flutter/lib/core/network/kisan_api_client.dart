import 'dart:async';
import 'dart:convert';
import 'dart:io';

import '../config/api_config.dart';

/// Structured exception thrown when a backend call fails.
class ApiException implements Exception {
  const ApiException({
    required this.message,
    this.statusCode,
  });

  final String message;
  final int? statusCode;

  @override
  String toString() =>
      'ApiException(statusCode: $statusCode, message: $message)';
}

/// Lightweight, zero-dependency JSON HTTP client built on `dart:io`.
///
/// Supports Bearer JWT authentication, automatic JSON encoding/decoding,
/// and rural-network timeout handling. Can be replaced with `dio` or `http`
/// without changing any UI screen code.
class KisanApiClient {
  KisanApiClient({
    this.baseUrl = ApiConfig.baseUrl,
    this.authToken,
  });

  final String baseUrl;
  String? authToken;

  final HttpClient _httpClient = HttpClient()
    ..connectionTimeout = ApiConfig.timeout;

  Map<String, String> get _defaultHeaders => <String, String>{
        HttpHeaders.contentTypeHeader: 'application/json; charset=utf-8',
        HttpHeaders.acceptHeader: 'application/json',
        if (authToken != null && authToken!.isNotEmpty)
          HttpHeaders.authorizationHeader: 'Bearer $authToken',
      };

  Uri _buildUri(String path, [Map<String, String>? queryParameters]) {
    final Uri base = Uri.parse('$baseUrl$path');
    if (queryParameters == null || queryParameters.isEmpty) {
      return base;
    }
    return base.replace(queryParameters: queryParameters);
  }

  Future<Map<String, dynamic>> getJson(
    String path, {
    Map<String, String>? queryParameters,
  }) async {
    try {
      final HttpClientRequest request = await _httpClient
          .getUrl(_buildUri(path, queryParameters))
          .timeout(ApiConfig.timeout);
      _defaultHeaders.forEach(request.headers.set);

      final HttpClientResponse response =
          await request.close().timeout(ApiConfig.timeout);
      final String body = await response.transform(utf8.decoder).join();

      if (response.statusCode >= 200 && response.statusCode < 300) {
        final dynamic decoded = jsonDecode(body);
        if (decoded is Map<String, dynamic>) return decoded;
        return <String, dynamic>{'data': decoded};
      }

      throw ApiException(
        statusCode: response.statusCode,
        message: 'GET $path failed: $body',
      );
    } on TimeoutException {
      throw const ApiException(
        message: 'Network timeout. Check mobile signal and retry.',
      );
    } on SocketException {
      throw const ApiException(
        message: 'Unable to reach server. Offline fallback active.',
      );
    }
  }

  Future<Map<String, dynamic>> postJson(
    String path, {
    required Map<String, dynamic> body,
  }) async {
    try {
      final HttpClientRequest request =
          await _httpClient.postUrl(_buildUri(path)).timeout(ApiConfig.timeout);
      _defaultHeaders.forEach(request.headers.set);
      request.add(utf8.encode(jsonEncode(body)));

      final HttpClientResponse response =
          await request.close().timeout(ApiConfig.timeout);
      final String responseBody = await response.transform(utf8.decoder).join();

      if (response.statusCode >= 200 && response.statusCode < 300) {
        if (responseBody.isEmpty) return <String, dynamic>{};
        final dynamic decoded = jsonDecode(responseBody);
        if (decoded is Map<String, dynamic>) return decoded;
        return <String, dynamic>{'data': decoded};
      }

      throw ApiException(
        statusCode: response.statusCode,
        message: 'POST $path failed: $responseBody',
      );
    } on TimeoutException {
      throw const ApiException(
        message: 'Request timed out on slow connection.',
      );
    } on SocketException {
      throw const ApiException(
        message: 'No internet connection available.',
      );
    }
  }

  void dispose() {
    _httpClient.close(force: true);
  }
}
