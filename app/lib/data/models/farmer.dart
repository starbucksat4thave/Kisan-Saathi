import 'package:freezed_annotation/freezed_annotation.dart';

part 'farmer.freezed.dart';
part 'farmer.g.dart';

@freezed
class Farmer with _$Farmer {
  const factory Farmer({
    required String id,
    required String name,
    required String phone,
    @Default('ur') String language,
    required String village,
    required String tehsil,
    required String level,
    @Default(0) double barkatTotalMaund,
    @Default(true) bool leaderboardOptIn,
    required String nickname,
  }) = _Farmer;

  factory Farmer.fromJson(Map<String, dynamic> json) => _$FarmerFromJson(json);
}
