from __future__ import annotations

import argparse
import json
from datetime import date
from pathlib import Path

from pipeline.fetchers import FETCHERS, fetch


def run(region: str, demo: bool = False) -> Path:
    out_dir = Path(__file__).resolve().parents[1] / "seed" / "cache"
    out_dir.mkdir(parents=True, exist_ok=True)
    features = []
    for name in FETCHERS:
        if demo:
            features.append({"fetcher": name, "dataset": FETCHERS[name].name, "demo_cached": True})
            continue
        record = fetch(name, 32.93, 72.86, date.today())
        record["region"] = region
        features.append(record)

    out = out_dir / f"field_features_{region}.json"
    out.write_text(json.dumps(features, indent=2), encoding="utf-8")
    return out


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--region", default="chakwal")
    parser.add_argument("--demo", action="store_true")
    args = parser.parse_args()
    output = run(region=args.region, demo=args.demo)
    print(f"Wrote {output}")
