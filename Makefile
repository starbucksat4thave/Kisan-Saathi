.PHONY: dev seed pipeline test demo openapi

dev:
	docker compose up --build

seed:
	python backend/scripts/seed_demo.py

pipeline:
	python -m pipeline.run --region $${REGION:-chakwal}

test:
	pytest backend/tests engines/tests pipeline/tests

demo:
	python backend/scripts/seed_demo.py && python -m pipeline.run --region chakwal --demo

openapi:
	python backend/scripts/generate_openapi.py
