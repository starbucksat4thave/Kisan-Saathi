from engines.rules import WattarInput, frost_alert, spray_window, wattar_sow_now


def test_wattar_triggers_in_moisture_band():
    result = wattar_sow_now(WattarInput(surface_moisture=0.26, rain_last_7d_mm=0, forecast_rain_48h_mm=0))
    assert result.fire is True
    assert result.confidence == "high"


def test_wattar_triggers_from_recent_rain_even_if_moisture_low():
    result = wattar_sow_now(WattarInput(surface_moisture=0.12, rain_last_7d_mm=15, forecast_rain_48h_mm=5))
    assert result.fire is True


def test_frost_alert_window():
    assert frost_alert(1.5, 1).fire is True
    assert frost_alert(1.5, 3).fire is False


def test_spray_window_rule():
    assert spray_window(20, 10, 28).fire is True
    assert spray_window(45, 10, 28).fire is False
