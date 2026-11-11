from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_health():
    res = client.get("/health")
    assert res.status_code == 200
    assert res.json()["status"] == "ok"


def test_demo_roadmap_and_advisory():
    fields = client.get("/v1/fields").json()
    field_id = fields[0]["id"]

    roadmap = client.get(f"/v1/fields/{field_id}/roadmap").json()
    assert roadmap[0]["key"] == "sowing"

    advisories = client.get(f"/v1/fields/{field_id}/advisories").json()
    assert advisories[0]["key"] == "wattar_ok_sow_now"
    assert advisories[0]["sources"] == ["SMAP", "IMERG", "Forecast"]


def test_ask_fallback_is_honest():
    res = client.post("/v1/ask", json={"text": "unknown question"})
    assert res.status_code == 200
    assert res.json()["grounding"] == []
