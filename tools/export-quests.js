#!/usr/bin/env node
/*
 * Exports the live dev-world quest book into BetterQuesting's DefaultQuests resource layout,
 * so the quests ship inside the mod jar (auto-loaded by BetterQuesting on a world's first load,
 * via QuestLoader.registry() -> config/betterquesting/DefaultQuests/).
 *
 * Source for day-to-day editing is still in the live save:
 *   run/client/saves/I hate this so much/betterquesting/QuestDatabase.json
 * Edit quests there (in-game GUI, or scripted edits), then re-run this script to regenerate
 * the resource files that actually get shipped:
 *   src/main/resources/assets/gtnhpp/quest/DefaultQuests/{QuestLines,Quests}/ProcessingPlus/
 *
 * Usage: node tools/export-quests.js
 * Every run re-reads the generated files fresh from disk and diffs them against the source
 * before reporting success — run it after every quest change you intend to ship.
 */
const fs = require("fs");
const path = require("path");

const REPO_ROOT = path.join(__dirname, "..");
const SRC = path.join(REPO_ROOT, "run/client/saves/I hate this so much/betterquesting/QuestDatabase.json");
const OUT_ROOT = path.join(REPO_ROOT, "src/main/resources/assets/gtnhpp/quest/DefaultQuests");
const LINE_DIR_NAME = "ProcessingPlus";

function sanitize(name) {
  return name.replace(/[^a-zA-Z0-9]+/g, "_").replace(/^_+|_+$/g, "").slice(0, 40) || "quest";
}

// Mirrors BetterQuesting's UuidConverter.encodeUuid: 8 bytes big-endian of the "high" long,
// then 8 bytes big-endian of the "low" long, standard base64 (with padding), URL-safe alphabet.
//
// IMPORTANT: must convert via String(x) -> BigInt, not BigInt(x) directly. high/low are JS
// numbers (IEEE754 doubles) that can't hold a 64-bit long exactly; JSON.stringify writes them
// using the *shortest round-trip decimal string* (e.g. "-7161013413323522000"), which is what
// Java's exact-integer long parser will actually read back from QuestLine.json — and that text
// is NOT always the same integer as BigInt(x), which instead recovers the double's own precise
// binary value (e.g. -7161013413323522048, off by 48 here). Encoding from the wrong one silently
// points QuestLinesOrder.txt at a UUID BetterQuesting can never find, which crashes the
// quest-book GUI with a null-questline NullPointerException.
function encodeQuestLineKey(high, low) {
  const buf = Buffer.alloc(16);
  buf.writeBigInt64BE(BigInt(String(high)), 0);
  buf.writeBigInt64BE(BigInt(String(low)), 8);
  return buf.toString("base64").replace(/\+/g, "-").replace(/\//g, "_");
}

function loadSource() {
  const data = JSON.parse(fs.readFileSync(SRC, "utf-8"));
  const db = data["questDatabase:9"];
  const chapter = data["questLines:9"]["0:10"];
  const placements = chapter["quests:9"];
  const questSettings = data["questSettings:10"];
  return { db, chapter, placements, questSettings };
}

function exportQuests() {
  const { db, chapter, placements, questSettings } = loadSource();

  const placementByUuid = new Map();
  for (const key of Object.keys(placements)) {
    const p = placements[key];
    placementByUuid.set(p["questIDHigh:4"] + "_" + p["questIDLow:4"], p);
  }

  const questLinesDir = path.join(OUT_ROOT, "QuestLines", LINE_DIR_NAME);
  const questsDir = path.join(OUT_ROOT, "Quests", LINE_DIR_NAME);

  // Clear stale output so removed/renamed quests don't leave orphan files behind.
  fs.rmSync(questLinesDir, { recursive: true, force: true });
  fs.rmSync(questsDir, { recursive: true, force: true });
  fs.mkdirSync(questLinesDir, { recursive: true });
  fs.mkdirSync(questsDir, { recursive: true });

  const questLineJson = {
    "properties:10": chapter["properties:10"],
    "questLineIDHigh:4": chapter["questLineIDHigh:4"],
    "questLineIDLow:4": chapter["questLineIDLow:4"],
  };
  fs.writeFileSync(
    path.join(questLinesDir, "QuestLine.json"),
    JSON.stringify(questLineJson, null, 2) + "\n",
    "utf-8",
  );

  // Key BetterQuesting uses to find the QuestLine.json above — computed here, from the exact
  // same questLineIDHigh/Low values just written, so it can never drift out of sync with them.
  const questLineKey = encodeQuestLineKey(chapter["questLineIDHigh:4"], chapter["questLineIDLow:4"]);
  const questLineOrderLine = `${questLineKey}: ${chapter["properties:10"]["betterquesting:10"]["name:8"]}`;
  fs.writeFileSync(path.join(OUT_ROOT, "QuestLinesOrder.txt"), questLineOrderLine + "\n", "utf-8");

  // Required by QuestCommandDefaults.load(): it bails out (loading nothing, no error dialog)
  // if DefaultQuests/QuestSettings.json is missing. Content is the live save's own
  // questSettings:10 object, unwrapped (the non-legacy loader reads it directly, no
  // "questSettings" wrapper key). loadWorldSettings=true means this is applied as-is to every
  // fresh player world, so force editMode off here even though the dev save keeps it on —
  // players shouldn't start with the quest editor unlocked; devs can still toggle it manually
  // in their own save.
  const shippedQuestSettings = JSON.parse(JSON.stringify(questSettings));
  shippedQuestSettings["betterquesting:10"]["editMode:1"] = 0;
  fs.writeFileSync(
    path.join(OUT_ROOT, "QuestSettings.json"),
    JSON.stringify(shippedQuestSettings, null, 2) + "\n",
    "utf-8",
  );

  const manifest = [];
  for (const id of Object.keys(db)) {
    const quest = db[id];
    const uuidKey = quest["questIDHigh:4"] + "_" + quest["questIDLow:4"];
    const placement = placementByUuid.get(uuidKey);
    if (!placement) throw new Error("No placement for quest " + id);

    const name = quest["properties:10"]?.["betterquesting:10"]?.["name:8"] || "quest_" + id;
    const fileName = sanitize(name) + "-" + quest["questIDLow:4"] + ".json";

    fs.writeFileSync(path.join(questsDir, fileName), JSON.stringify(quest, null, 2) + "\n", "utf-8");

    const placementJson = {
      "sizeX:3": placement["sizeX:3"],
      "sizeY:3": placement["sizeY:3"],
      "x:3": placement["x:3"],
      "y:3": placement["y:3"],
      "questIDHigh:4": placement["questIDHigh:4"],
      "questIDLow:4": placement["questIDLow:4"],
    };
    fs.writeFileSync(
      path.join(questLinesDir, fileName),
      JSON.stringify(placementJson, null, 2) + "\n",
      "utf-8",
    );

    manifest.push({ id, fileName, questIDHigh: quest["questIDHigh:4"], questIDLow: quest["questIDLow:4"] });
  }

  return { questLineJson, questSettings: shippedQuestSettings, questLineOrderLine, manifest };
}

function verify(expected) {
  const { db, chapter, placements, questSettings } = loadSource();
  const shippedQuestSettings = JSON.parse(JSON.stringify(questSettings));
  shippedQuestSettings["betterquesting:10"]["editMode:1"] = 0;
  const expectedOrderLine =
    encodeQuestLineKey(chapter["questLineIDHigh:4"], chapter["questLineIDLow:4"]) +
    `: ${chapter["properties:10"]["betterquesting:10"]["name:8"]}`;
  const placementByUuid = new Map();
  for (const key of Object.keys(placements)) {
    const p = placements[key];
    placementByUuid.set(p["questIDHigh:4"] + "_" + p["questIDLow:4"], p);
  }

  const questLinesDir = path.join(OUT_ROOT, "QuestLines", LINE_DIR_NAME);
  const questsDir = path.join(OUT_ROOT, "Quests", LINE_DIR_NAME);

  let ok = 0;
  let bad = 0;

  const questLineOnDisk = JSON.parse(
    fs.readFileSync(path.join(questLinesDir, "QuestLine.json"), "utf-8"),
  );
  if (JSON.stringify(questLineOnDisk) === JSON.stringify(expected.questLineJson)) {
    ok++;
  } else {
    bad++;
    console.error("MISMATCH: QuestLine.json");
  }

  const settingsOnDisk = JSON.parse(fs.readFileSync(path.join(OUT_ROOT, "QuestSettings.json"), "utf-8"));
  if (JSON.stringify(settingsOnDisk) === JSON.stringify(shippedQuestSettings)) {
    ok++;
  } else {
    bad++;
    console.error("MISMATCH: QuestSettings.json");
  }

  const orderLineOnDisk = fs.readFileSync(path.join(OUT_ROOT, "QuestLinesOrder.txt"), "utf-8").trim();
  if (orderLineOnDisk === expectedOrderLine && orderLineOnDisk === expected.questLineOrderLine) {
    ok++;
  } else {
    bad++;
    console.error("MISMATCH: QuestLinesOrder.txt", orderLineOnDisk, "!=", expectedOrderLine);
  }

  for (const entry of expected.manifest) {
    const questOnDisk = JSON.parse(fs.readFileSync(path.join(questsDir, entry.fileName), "utf-8"));
    const sourceQuest = db[entry.id];
    if (JSON.stringify(questOnDisk) !== JSON.stringify(sourceQuest)) {
      bad++;
      console.error("MISMATCH: quest", entry.id, entry.fileName);
      continue;
    }

    const placementOnDisk = JSON.parse(
      fs.readFileSync(path.join(questLinesDir, entry.fileName), "utf-8"),
    );
    const srcPlacement = placementByUuid.get(entry.questIDHigh + "_" + entry.questIDLow);
    const expectedPlacement = {
      "sizeX:3": srcPlacement["sizeX:3"],
      "sizeY:3": srcPlacement["sizeY:3"],
      "x:3": srcPlacement["x:3"],
      "y:3": srcPlacement["y:3"],
      "questIDHigh:4": srcPlacement["questIDHigh:4"],
      "questIDLow:4": srcPlacement["questIDLow:4"],
    };
    if (JSON.stringify(placementOnDisk) !== JSON.stringify(expectedPlacement)) {
      bad++;
      console.error("MISMATCH: placement", entry.id, entry.fileName);
      continue;
    }

    ok++;
  }

  const questFileCount = fs.readdirSync(questsDir).length;
  const lineFileCount = fs.readdirSync(questLinesDir).length;

  return { ok, bad, questFileCount, lineFileCount, expectedCount: expected.manifest.length };
}

const expected = exportQuests();
const result = verify(expected);

console.log(
  `Exported ${expected.manifest.length} quests. Verified OK: ${result.ok}, BAD: ${result.bad}.`,
);
console.log(
  `Quests dir: ${result.questFileCount} files (expect ${result.expectedCount}). ` +
    `QuestLines dir: ${result.lineFileCount} files (expect ${result.expectedCount + 1}).`,
);

if (
  result.bad > 0 ||
  result.questFileCount !== result.expectedCount ||
  result.lineFileCount !== result.expectedCount + 1
) {
  console.error("Export verification FAILED — resource files do not match the live save.");
  process.exit(1);
}

console.log("Export verified: resource files match the live save exactly.");
