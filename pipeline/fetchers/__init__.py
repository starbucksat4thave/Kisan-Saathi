from dataclasses import dataclass
from datetime import date
from typing import Callable

import requests


@dataclass(frozen=True)
class FetchSpec:
    name: str
    url_builder: Callable[[float, float, date], str]


FETCHERS: dict[str, FetchSpec] = {
    "smap": FetchSpec("SPL4SMGP v8", lambda lat, lon, d: f"https://example-nasa.local/smap?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "smap_hr": FetchSpec("SPL2SMAP_S", lambda lat, lon, d: f"https://example-nasa.local/smap_hr?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "imerg": FetchSpec("GPM_3IMERGDF V07", lambda lat, lon, d: f"https://example-nasa.local/imerg?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "hls": FetchSpec("HLSL30/HLSS30 v2", lambda lat, lon, d: f"https://example-nasa.local/hls?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "modis": FetchSpec("MOD13Q1/MCD12Q2", lambda lat, lon, d: f"https://example-nasa.local/modis?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "ecostress": FetchSpec("ECO_L2T_LSTE v002", lambda lat, lon, d: f"https://example-nasa.local/ecostress?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "power": FetchSpec("NASA POWER daily", lambda lat, lon, d: f"https://power.larc.nasa.gov/api/temporal/daily/point?parameters=T2M_MAX,T2M_MIN,PRECTOTCORR,GWETROOT,ALLSKY_SFC_SW_DWN,RH2M,WS2M&community=AG&latitude={lat}&longitude={lon}&start=20050101&end={d.strftime('%Y%m%d')}&format=JSON"),
    "dem": FetchSpec("NASADEM_HGT v001", lambda lat, lon, d: f"https://example-nasa.local/dem?lat={lat}&lon={lon}"),
    "grace": FetchSpec("GRACEDADM_CLSM025GL_7D v3", lambda lat, lon, d: f"https://example-nasa.local/grace?lat={lat}&lon={lon}&date={d.isoformat()}"),
    "forecast": FetchSpec("Open-Meteo", lambda lat, lon, d: f"https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&daily=precipitation_sum,temperature_2m_max,temperature_2m_min,wind_speed_10m_max&hourly=precipitation_probability&timezone=Asia/Karachi"),
    "soil": FetchSpec("SoilGrids 250m", lambda lat, lon, d: f"https://example-nasa.local/soil?lat={lat}&lon={lon}"),
}


def fetch(name: str, lat: float, lon: float, on_date: date) -> dict:
    spec = FETCHERS[name]
    url = spec.url_builder(lat, lon, on_date)
    response = requests.get(url, timeout=20)
    response.raise_for_status()
    return {
        "fetcher": name,
        "dataset": spec.name,
        "retrieved_at": on_date.isoformat(),
        "source_url": url,
        "payload": response.json(),
    }
