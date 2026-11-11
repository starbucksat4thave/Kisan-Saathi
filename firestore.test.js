const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const RASHID_UID = "rashid_chakwal_123";
const OTHER_UID = "other_farmer_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read farmer profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(RASHID_UID).get());
});

test("Authenticated farmer: can create and read own profile", async () => {
  const rashidDb = testEnv.authenticatedContext(RASHID_UID).firestore();
  const profileRef = rashidDb.collection("users").doc(RASHID_UID);

  await assertSucceeds(
    profileRef.set({
      userId: RASHID_UID,
      name: "Rashid Minhas",
      language: "ur",
      village: "Dhudial",
      tehsil: "Chakwal",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(profileRef.get());
});

test("Authenticated farmer: cannot access another farmer's field", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(RASHID_UID).collection("fields").doc("field_1").set({
      userId: RASHID_UID,
      name: "Bara khet",
      area_acre: 4.0,
      centroid_lat: 32.93,
      centroid_lon: 72.86,
      createdAt: new Date(),
    });
  });

  const otherDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  await assertFails(otherDb.collection("users").doc(RASHID_UID).collection("fields").doc("field_1").get());
});

test("Authenticated farmer: can create a field under their profile", async () => {
  const rashidDb = testEnv.authenticatedContext(RASHID_UID).firestore();
  const fieldRef = rashidDb.collection("users").doc(RASHID_UID).collection("fields").doc("field_1");

  await assertSucceeds(
    fieldRef.set({
      userId: RASHID_UID,
      name: "Bara khet",
      area_acre: 4.0,
      centroid_lat: 32.93,
      centroid_lon: 72.86,
      water_access: "rainfed",
      soil_texture: "loam",
      createdAt: new Date(),
    })
  );
});

test("Authenticated farmer: can create and read diary entry", async () => {
  const rashidDb = testEnv.authenticatedContext(RASHID_UID).firestore();
  const diaryRef = rashidDb.collection("users").doc(RASHID_UID).collection("diary").doc("entry_1");

  await assertSucceeds(
    diaryRef.set({
      userId: RASHID_UID,
      season_id: "rabi_2026_27",
      date: "2026-11-06",
      type: "sowing",
      detail: "Wheat seed Akbar-19 sowed in moist soil",
      source: "voice",
      createdAt: new Date(),
    })
  );
});

test("Leaderboard: any authenticated user can read leaderboard", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("leaderboard").doc(RASHID_UID).set({
      farmerId: RASHID_UID,
      nickname: "Rashid Bhai",
      village: "Dhudial",
      score: 340,
      streak_weeks: 3,
      updatedAt: new Date(),
    });
  });

  const otherDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  await assertSucceeds(otherDb.collection("leaderboard").doc(RASHID_UID).get());
  await assertSucceeds(otherDb.collection("leaderboard").get());
});
