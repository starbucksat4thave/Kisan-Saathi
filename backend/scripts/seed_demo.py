from pathlib import Path
import json


def main() -> None:
    data = {
        "farmer": {
            "name": "Rashid",
            "village": "Dhudial",
            "tehsil": "Chakwal",
            "area_acre": 4.0,
        },
        "seasons": ["rabi_2023_24", "rabi_2024_25", "rabi_2025_26"],
    }
    out = Path(__file__).resolve().parents[2] / "seed" / "demo_farmer.json"
    out.write_text(json.dumps(data, indent=2), encoding="utf-8")
    print(f"Seeded {out}")


if __name__ == "__main__":
    main()
