package com.gtnh.processingplus.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gregtech.api.enums.Mods;

import com.gtnh.processingplus.GTNHProcessingPlus;

/**
 * Ships the "Processing Plus" quest book inside the mod jar so players don't need to manually
 * drop a QuestDatabase.json into their save. Bundled quest resources are copied into
 * BetterQuesting's own {@code config/betterquesting/DefaultQuests/} folder on preInit, and a key
 * is added to {@code QuestLinesOrder.txt}. BetterQuesting only reads DefaultQuests on a world's
 * first load (see {@code SaveLoadHandler.loadConfig()}), so this never touches a world that
 * already has its own quest save.
 */
public class QuestLoader {

    private static final String MOD_RESOURCE_ID = "gtnhpp";

    private static final File CONFIG_ORDER_FILE = new File(
        "config/" + Mods.BetterQuesting.ID + "/DefaultQuests/QuestLinesOrder.txt");
    private static final File CONFIG_QUESTS_DIR = new File("config/" + Mods.BetterQuesting.ID + "/DefaultQuests");
    private static final String RESOURCE_QUESTS_PREFIX = "assets/" + MOD_RESOURCE_ID + "/quest/DefaultQuests/";
    // Bundled alongside the quest files themselves (tools/export-quests.js writes it in the same
    // run as QuestLine.json, from the exact same questLineIDHigh/Low values) — never hardcode
    // this key separately, it must always match what's actually in QuestLine.json or
    // BetterQuesting stores a null IQuestLine entry, which crashes the quest-book GUI.
    private static final String RESOURCE_ORDER_FILE = "QuestLinesOrder.txt";

    public static void registry() {
        try {
            // IMPORTANT: copy quest files first, then inject the order key(s).
            // Injecting a key without the corresponding QuestLine.json causes
            // BetterQuesting to store a null IQuestLine entry, which crashes the
            // quest-book GUI with NullPointerException at refreshChapterVisibility.
            boolean filesCopied = copyDefaultQuestsFromJar();
            syncQuestLinesOrder(filesCopied);
        } catch (Exception e) {
            GTNHProcessingPlus.LOG.error("[QuestLoader] Quest injection failed", e);
        }
    }

    /**
     * Merges our bundled QuestLinesOrder.txt line(s) into the player's (shared, possibly
     * multi-mod) config file, replacing any stale line left behind by an older/broken version of
     * this mod. Matched by the ": <name>" suffix, since that's stable across rebuilds even when
     * the encoded UUID key on the left isn't (see encodeQuestLineKey in tools/export-quests.js —
     * a rebuilt jar can legitimately produce a different key for the same questline).
     *
     * @param injectKeys true → ensure our key(s) are present, removing outdated ones (files are
     *                   confirmed present)
     *                   false → remove our key(s) entirely (files unavailable; prevents null
     *                   IQuestLine entry in BetterQuesting which causes NPE in the quest-book GUI)
     */
    public static void syncQuestLinesOrder(boolean injectKeys) throws IOException {
        List<String> ourKeys = injectKeys ? readOurOrderKeys() : new ArrayList<>();
        List<String> lines = readFileLines();

        List<String> ourNameSuffixes = new ArrayList<>();
        for (String key : ourKeys) {
            int colon = key.indexOf(": ");
            if (colon >= 0) ourNameSuffixes.add(key.substring(colon));
        }

        List<String> next = new ArrayList<>();
        boolean changed = false;
        for (String line : lines) {
            boolean isStaleCopyOfOurs = !ourKeys.contains(line)
                && ourNameSuffixes.stream()
                    .anyMatch(line::endsWith);
            if (isStaleCopyOfOurs) {
                changed = true;
                continue;
            }
            next.add(line);
        }

        for (String key : ourKeys) {
            if (!next.contains(key)) {
                next.add(key);
                changed = true;
            }
        }

        if (!changed) {
            GTNHProcessingPlus.LOG.info("[QuestLoader] QuestLinesOrder.txt is already up-to-date.");
            return;
        }

        writeFileLines(next);
        GTNHProcessingPlus.LOG.info("[QuestLoader] Updated QuestLinesOrder.txt with key(s): " + ourKeys);
    }

    /** Reads our bundled QuestLinesOrder.txt resource straight out of the mod jar. */
    private static List<String> readOurOrderKeys() throws IOException {
        File jarFile = resolveJarFile();
        if (jarFile == null) return new ArrayList<>();

        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry(RESOURCE_QUESTS_PREFIX + RESOURCE_ORDER_FILE);
            if (entry == null) return new ArrayList<>();
            try (InputStream in = jar.getInputStream(entry)) {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (!trimmed.isEmpty()) lines.add(trimmed);
                    }
                }
                return lines;
            }
        }
    }

    /**
     * Copies quest resource files from the mod jar into the BetterQuesting DefaultQuests directory.
     *
     * @return true if the jar was found and files were managed (even if all were already up-to-date);
     *         false if not running from a jar (e.g. dev environment) — caller should not inject the
     *         order key in this case.
     */
    public static boolean copyDefaultQuestsFromJar() throws IOException {
        File jarFile = resolveJarFile();
        if (jarFile == null) {
            GTNHProcessingPlus.LOG.info("[QuestLoader] Not running from a jar file — skipping quest copy.");
            return false;
        }

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;

                String name = entry.getName();
                if (!name.startsWith(RESOURCE_QUESTS_PREFIX)) continue;

                String relativePath = name.substring(RESOURCE_QUESTS_PREFIX.length());
                // Handled separately by syncQuestLinesOrder() — this file is shared with other
                // mods' own default quest lines, so it must be merged, never blindly overwritten.
                if (relativePath.equals(RESOURCE_ORDER_FILE)) continue;
                File targetFile = new File(CONFIG_QUESTS_DIR, relativePath);

                boolean shouldCopy = true;
                if (targetFile.exists()) {
                    try (InputStream compareStream = jar.getInputStream(entry)) {
                        if (compareFileContent(compareStream, targetFile)) {
                            shouldCopy = false;
                        }
                    }
                }

                if (shouldCopy) {
                    targetFile.getParentFile()
                        .mkdirs();
                    try (InputStream freshStream = jar.getInputStream(entry);
                        OutputStream out = new FileOutputStream(targetFile)) {
                        copyStream(freshStream, out);
                        GTNHProcessingPlus.LOG.info("[QuestLoader] Copied/Updated: " + targetFile.getName());
                    }
                }
            }
        }
        return true;
    }

    /**
     * Resolves the jar file containing this class using two independent methods.
     * <ol>
     * <li>ProtectionDomain / CodeSource — direct, works in most Forge production setups.</li>
     * <li>Class resource URL — reliable fallback that also handles percent-encoded paths
     * (e.g. spaces in the mods folder path).</li>
     * </ol>
     *
     * @return the jar {@link File}, or {@code null} if not running from a jar.
     */
    private static File resolveJarFile() {
        try {
            ProtectionDomain pd = QuestLoader.class.getProtectionDomain();
            if (pd != null) {
                CodeSource cs = pd.getCodeSource();
                if (cs != null) {
                    URL loc = cs.getLocation();
                    if (loc != null) {
                        try {
                            File f = new File(loc.toURI());
                            if (f.isFile() && f.getName()
                                .endsWith(".jar")) {
                                return f;
                            }
                        } catch (URISyntaxException ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}

        URL classUrl = QuestLoader.class.getResource(
            "/" + QuestLoader.class.getName()
                .replace('.', '/') + ".class");
        if (classUrl == null) return null;

        String urlStr = classUrl.toString();
        if (!urlStr.startsWith("jar:file:")) return null;

        int bangIdx = urlStr.indexOf("!/");
        if (bangIdx < 0) return null;

        try {
            File f = new File(new URI(urlStr.substring("jar:".length(), bangIdx)));
            return (f.isFile() && f.getName()
                .endsWith(".jar")) ? f : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static boolean compareFileContent(InputStream in1, File file2) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash1;
            try (DigestInputStream dis1 = new DigestInputStream(in1, digest)) {
                while (dis1.read() != -1) {}
                hash1 = digest.digest();
            }

            digest.reset();

            byte[] hash2;
            try (InputStream fis = new FileInputStream(file2);
                DigestInputStream dis2 = new DigestInputStream(fis, digest)) {
                while (dis2.read() != -1) {}
                hash2 = digest.digest();
            }

            return Arrays.equals(hash1, hash2);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    public static List<String> readFileLines() throws IOException {
        List<String> lines = new ArrayList<>();
        if (!CONFIG_ORDER_FILE.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(CONFIG_ORDER_FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) lines.add(trimmed);
            }
        }
        return lines;
    }

    public static void writeFileLines(List<String> lines) throws IOException {
        CONFIG_ORDER_FILE.getParentFile()
            .mkdirs();
        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(CONFIG_ORDER_FILE), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
