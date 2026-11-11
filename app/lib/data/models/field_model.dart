import 'package:freezed_annotation/freezed_annotation.dart';

part 'field_model.freezed.dart';
part 'field_model.g.dart';

@freezed
class FieldModel with _$FieldModel {
  const factory FieldModel({
    required String id,
    required String farmerId,
    required String name,
    required double areaAcre,
    required Map<String, dynamic> geometry,
    required List<double> centroid,
    required String waterAccess,
    required String soilTexture,
  }) = _FieldModel;

  factory FieldModel.fromJson(Map<String, dynamic> json) => _$FieldModelFromJson(json);
}
