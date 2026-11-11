from __future__ import annotations

from datetime import date, datetime
from typing import Literal
from uuid import UUID, uuid4

from pydantic import BaseModel, Field


class MemoryRef(BaseModel):
    season: str
    text_ur: str
    text_en: str


class BarkatIfDone(BaseModel):
    maund_estimate: float
    basis: str
    placeholder: bool = True


class Farmer(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    name: str
    phone: str
    language: str = "ur"
    village: str
    tehsil: str
    level: str
    barkat_total_maund: float = 0.0
    leaderboard_opt_in: bool = True
    nickname: str


class Geometry(BaseModel):
    type: str = "Polygon"
    coordinates: list = Field(default_factory=list)


class FieldModel(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    farmer_id: UUID
    name: str
    area_acre: float
    geometry: Geometry
    centroid: list[float]
    water_access: Literal["rainfed", "well"]
    soil_texture: Literal["loam", "sandy_loam", "clay_loam", "unknown"]


class NasaEvent(BaseModel):
    type: Literal[
        "late_rain",
        "dry_spell",
        "heat_wave",
        "frost_night",
        "heavy_rain",
        "early_maturity",
    ]
    start: datetime
    end: datetime
    value: float
    unit: str
    source: Literal["POWER", "IMERG", "SMAP", "ECOSTRESS", "HLS"]


class DiaryEntry(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    season_id: UUID
    date: date
    type: Literal[
        "sowing",
        "fertilizer",
        "spray",
        "irrigation",
        "weeding",
        "harvest",
        "sale",
        "problem",
        "note",
    ]
    detail: dict = Field(default_factory=dict)
    source: Literal["voice", "tap", "satellite_confirmed"]
    audio_note: str | None = None
    matched_advisory_id: UUID | None = None


class Season(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    field_id: UUID
    crop: str = "wheat"
    variety: str | None = None
    season: str
    sowing_date: date
    harvest_date: date | None = None
    yield_maund: float | None = None
    status: Literal["active", "closed"] = "active"
    events: list[NasaEvent] = Field(default_factory=list)
    diary: list[DiaryEntry] = Field(default_factory=list)
    notes: str = ""


class RoadmapNode(BaseModel):
    key: str
    order: int
    title_ur: str
    title_en: str
    window_start: date
    window_end: date
    state: Literal["done", "current", "upcoming", "missed"]
    icon: str
    advisory_keys: list[str]


class AdvisoryCard(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    key: str
    node_key: str
    severity: Literal["info", "act", "alert"]
    title_ur: str
    body_ur: str
    body_en: str
    audio: str
    act_by: date
    sources: list[str]
    confidence: Literal["high", "medium", "low"]
    memory_ref: MemoryRef | None = None
    barkat_if_done: BarkatIfDone | None = None
    created_at: datetime


class BarkatEvent(BaseModel):
    id: UUID = Field(default_factory=uuid4)
    farmer_id: UUID
    advisory_id: UUID
    maund_estimate: float
    loss_factor_id: str
    placeholder: bool = True
    created_at: datetime


class Badge(BaseModel):
    badge_key: str
    earned_at: datetime


class LeaderboardEntry(BaseModel):
    village: str
    nickname: str
    score: int
    streak_weeks: int
    rank: int
