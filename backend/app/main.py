from datetime import UTC, date, datetime, timedelta
from fastapi import FastAPI

from app.schemas.contracts import (
    AdvisoryCard,
    Badge,
    BarkatEvent,
    DiaryEntry,
    Farmer,
    FieldModel,
    LeaderboardEntry,
    MemoryRef,
    RoadmapNode,
)

app = FastAPI(title="Kisan Saathi API", version="0.1.0")

farmer = Farmer(
    name="Rashid",
    phone="+923001112233",
    village="Dhudial",
    tehsil="Chakwal",
    level="mehnati_kisan",
    barkat_total_maund=6.0,
    nickname="Rashid Bhai",
)

field = FieldModel(
    farmer_id=farmer.id,
    name="Bara khet",
    area_acre=4.0,
    geometry={"type": "Polygon", "coordinates": []},
    centroid=[72.86, 32.93],
    water_access="rainfed",
    soil_texture="loam",
)

roadmap = [
    RoadmapNode(
        key="sowing",
        order=1,
        title_ur="بوائی",
        title_en="Sowing",
        window_start=date(2026, 10, 25),
        window_end=date(2026, 11, 15),
        state="current",
        icon="seeding",
        advisory_keys=["wattar_ok_sow_now"],
    )
]

advisories = [
    AdvisoryCard(
        key="wattar_ok_sow_now",
        node_key="sowing",
        severity="act",
        title_ur="آج بوائی کا اچھا وقت ہے",
        body_ur="مٹی میں نمی مناسب ہے، اگلے 3 دن میں بوائی کریں۔",
        body_en="Soil moisture is suitable. Sow within 3 days.",
        audio="audio/ur/wattar_ok_sow_now.mp3",
        act_by=date(2026, 11, 12),
        sources=["SMAP", "IMERG", "Forecast"],
        confidence="high",
        memory_ref=MemoryRef(
            season="rabi_2023_24",
            text_ur="پچھلی بار تاخیر سے بوائی ہوئی، گرمی نے دانہ کم کیا۔",
            text_en="Last time late sowing increased grain loss due to heat.",
        ),
        barkat_if_done={"maund_estimate": 1.5, "basis": "wheat_sowing_timing", "placeholder": True},
        created_at=datetime.now(UTC),
    )
]

badges = [Badge(badge_key="wattar_wala", earned_at=datetime.now(UTC))]
barkat_events = [
    BarkatEvent(
        farmer_id=farmer.id,
        advisory_id=advisories[0].id,
        maund_estimate=1.5,
        loss_factor_id="wheat_sowing_timing",
        created_at=datetime.now(UTC),
    )
]


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.post("/v1/auth/otp/request")
def otp_request() -> dict:
    return {"status": "sent", "demo_otp": "123456"}


@app.post("/v1/auth/otp/verify")
def otp_verify() -> dict:
    return {"token": "demo-token", "farmer_id": str(farmer.id)}


@app.get("/v1/me", response_model=Farmer)
def get_me() -> Farmer:
    return farmer


@app.patch("/v1/me", response_model=Farmer)
def patch_me(payload: dict) -> Farmer:
    for key, value in payload.items():
        if hasattr(farmer, key):
            setattr(farmer, key, value)
    return farmer


@app.get("/v1/fields", response_model=list[FieldModel])
def get_fields() -> list[FieldModel]:
    return [field]


@app.post("/v1/fields", response_model=FieldModel)
def create_field(payload: FieldModel) -> FieldModel:
    return payload


@app.get("/v1/fields/{field_id}", response_model=FieldModel)
def get_field(field_id: str) -> FieldModel:
    return field


@app.get("/v1/fields/{field_id}/roadmap", response_model=list[RoadmapNode])
def get_roadmap(field_id: str, season: str = "current") -> list[RoadmapNode]:
    return roadmap


@app.get("/v1/fields/{field_id}/advisories", response_model=list[AdvisoryCard])
def get_advisories(field_id: str, date: str | None = None) -> list[AdvisoryCard]:
    return advisories


@app.post("/v1/diary", response_model=DiaryEntry)
def post_diary(payload: DiaryEntry) -> DiaryEntry:
    return payload


@app.get("/v1/seasons/{season_id}/diary", response_model=list[DiaryEntry])
def get_diary(season_id: str) -> list[DiaryEntry]:
    return []


@app.get("/v1/fields/{field_id}/history")
def get_history(field_id: str) -> dict:
    return {
        "seasons": [
            {"season": "rabi_2023_24", "lesson": "late sowing + March heat"},
            {"season": "rabi_2024_25", "lesson": "good timing + better yield"},
            {"season": "rabi_2025_26", "lesson": "harvest rain loss"},
        ]
    }


@app.get("/v1/fields/{field_id}/health")
def get_field_health(field_id: str) -> dict:
    return {
        "status": "weaker_than_last_year",
        "ndvi": [{"date": "2026-11-01", "value": 0.42}],
        "neighbor_median": [{"date": "2026-11-01", "value": 0.47}],
    }


@app.get("/v1/fields/{field_id}/summer-plan")
def get_summer_plan(field_id: str) -> dict:
    return {
        "options": [
            {"crop": "fallow", "score": 40},
            {"crop": "mungbean", "score": 74},
            {"crop": "sorghum_fodder", "score": 68},
        ]
    }


@app.post("/v1/advisories/{advisory_id}/done")
def advisory_done(advisory_id: str) -> dict:
    return {"status": "logged", "points": 20, "barkat_estimate": 1.5}


@app.get("/v1/barkat")
def get_barkat() -> dict:
    return {
        "total": farmer.barkat_total_maund,
        "events": barkat_events,
        "scene_stage": "seedling",
        "label": "اندازہ",
    }


@app.get("/v1/badges", response_model=list[Badge])
def get_badges() -> list[Badge]:
    return badges


@app.get("/v1/leaderboard", response_model=list[LeaderboardEntry])
def get_leaderboard(scope: str = "village") -> list[LeaderboardEntry]:
    return [LeaderboardEntry(village="Dhudial", nickname="Rashid Bhai", score=340, streak_weeks=3, rank=2)]


@app.post("/v1/ask")
def ask(payload: dict) -> dict:
    question = payload.get("text", "")
    if "یوریا" in question or "urea" in question.lower():
        return {
            "answer": "Tillering stage par nami ke sath urea timing behtar hoti hai.",
            "grounding": ["rabi_2024_25", "docs/guides/wheat.md"],
        }
    return {
        "answer": "Mujhe is sawal ka grounded jawab nahi mila. Barah-e-karam agri officer se rabta karein.",
        "grounding": [],
    }


@app.post("/v1/escalate")
def escalate() -> dict:
    return {"status": "queued", "queue": "mock_agri_officer", "eta_hours": 24}


@app.get("/v1/partner/summary")
def partner_summary(district: str = "Chakwal") -> dict:
    return {
        "district": district,
        "alerts_sent": 24,
        "actions_confirmed": 11,
        "barkat_total_maund_estimate": 16.5,
        "hotspots": ["frost", "dry_spell"],
    }


@app.get("/v1/time-travel")
def time_travel(target_date: date) -> dict:
    return {"simulated_date": target_date, "next_node": "harvest", "window": str(target_date + timedelta(days=2))}
