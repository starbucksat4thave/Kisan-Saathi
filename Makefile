.PHONY: dev seed pipeline test demo clean

test:
	gradle :app:testDebugUnitTest --tests '*Rule*'

demo:
	FIRESTORE_EMULATOR_HOST="127.0.0.1:8085" node --test firestore.test.js

build:
	gradle :app:assembleDebug

clean:
	gradle clean
