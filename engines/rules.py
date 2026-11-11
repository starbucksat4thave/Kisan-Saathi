from __future__ import annotations

from dataclasses import dataclass


@dataclass
class WattarInput:
    surface_moisture: float
    rain_last_7d_mm: float
    forecast_rain_48h_mm: float


@dataclass
class RuleResult:
    fire: bool
    confidence: str
    reason: str


def wattar_sow_now(data: WattarInput) -> RuleResult:
    moisture_band = 0.20 <= data.surface_moisture <= 0.35
    rain_supported = data.rain_last_7d_mm >= 10 and data.forecast_rain_48h_mm <= 20
    fire = moisture_band or rain_supported
    return RuleResult(
        fire=fire,
        confidence="high" if moisture_band else "medium" if fire else "low",
        reason="Use guided soil-ball test to confirm before sowing.",
    )


def frost_alert(tmin_c: float, month: int) -> RuleResult:
    fire = month in {12, 1, 2} and tmin_c <= 2
    return RuleResult(fire=fire, confidence="high" if fire else "low", reason="Frost threshold is DRAFT.")


def spray_window(rain_prob_6h: float, wind_kmh: float, tmax_c: float) -> RuleResult:
    fire = rain_prob_6h < 40 and wind_kmh < 15 and tmax_c < 30
    return RuleResult(fire=fire, confidence="medium" if fire else "low", reason="Scout before any spray.")
