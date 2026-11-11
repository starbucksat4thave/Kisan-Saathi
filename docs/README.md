# Kisan Saathi MVP

## Setup
1. Copy `.env.example` to `.env` and fill values.
2. Run `make dev`.
3. Run `make openapi` to regenerate API contract.

## Earthdata
Provide `EARTHDATA_USERNAME` and `EARTHDATA_PASSWORD` in `.env` for live NASA downloads.

## Pipeline
Run `make pipeline REGION=chakwal`.

## Demo mode
Set `DEMO_MODE=true` and run `make demo`.

## Agronomy config
Agronomists can update values in `config/*.yaml`; keep `status` and `source` fields accurate.
