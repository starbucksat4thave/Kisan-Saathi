from datetime import date

import pytest

from pipeline.fetchers import FETCHERS, fetch


class DummyResponse:
    def __init__(self, payload: dict):
        self._payload = payload

    def raise_for_status(self):
        return None

    def json(self):
        return self._payload


@pytest.mark.parametrize("fetcher_name", list(FETCHERS.keys()))
def test_fetchers_build_url_and_parse_json(monkeypatch, fetcher_name):
    captured = {}

    def fake_get(url, timeout):
        captured["url"] = url
        return DummyResponse({"ok": True, "url": url})

    monkeypatch.setattr("requests.get", fake_get)

    result = fetch(fetcher_name, lat=32.93, lon=72.86, on_date=date(2026, 11, 11))
    assert result["fetcher"] == fetcher_name
    assert result["payload"]["ok"] is True
    assert "http" in captured["url"]
