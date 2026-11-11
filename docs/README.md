# Kisan Saathi (کسان ساتھی)
### Voice-First Mobile Companion for Rainfed Smallholder Farmers in Pothohar, Pakistan
*Built for NASA Space Apps Challenge — "Field Shift: Adapting Farms with NASA Data"*

## Overview
Kisan Saathi bridges cutting-edge NASA Earth observations (SMAP, GPM IMERG, HLS, NASA POWER, ECOSTRESS, NASADEM) with local farmer realities in the rainfed (barani) plateau of Pothohar (Chakwal, Rawalpindi, Attock, Jhelum). Through a playful, Duolingo-style seasonal roadmap, clear Urdu voice guidance, past season memory personalization, and the **Barkat (برکت)** food-loss conservation tracker, farmers are empowered to take right-time actions that save maunds of wheat from being lost to drought, disease, heat waves, or unseasonal downpours.

## Key Capabilities
1. **Duolingo-Style Seasonal Roadmap:** Step-by-step guidance through Sowing, Tillering, Pest Scouting, Grain Fill, and Harvest with clear node statuses (Done, Current, Upcoming, Missed).
2. **Authentic NASA Data Provenance:** Every advisory card displays sensor tags ([SMAP], [IMERG], [HLS], [NASA POWER], [Forecast]) with plain-language explanations.
3. **Barkat (برکت) Visual Scene:** Interactive growing harvest scene (0 to 60+ maunds) depicting thriving cattle, tube wells, tractors, and family prosperity. Transparent estimation sheet with full source attribution.
4. **Historical Season Memory ("پچھلی بار..."):** Personalized memories connecting current weather patterns with past seasons (e.g. 2023-24 late sowing vs 2024-25 Wattar Wala success).
5. **AquaCrop 20-Year Summer Replay:** Compares traditional summer fallow against Mungbean (nitrogen fixing) and Sorghum Fodder, with customizable priority weight sliders (Water, Income, Soil Health, Cattle Feed).
6. **Field Health Satellite Tracker:** 30m NDVI vegetation index curves comparing current wheat vigor with previous seasons and regional Pothohar averages.
7. **Ask Kisan Saathi (Urdu AI Voice Companion):** Powered by Firebase AI Logic (`gemini-3.8-flash`) grounded in regional agronomy extension guides.
8. **Time-Travel Demonstration Mode:** Effortlessly advance through the season timeline to observe each advisory node triggering in real-time.

## Quick Start
```bash
make test       # Run test suites
make demo       # Launch demo environment with seeded Chakwal farmer data
```
