import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

// EndlessIDs replaces NotEnoughIDs — prevent NEID from landing on the classpath via any transitive dep
configurations.all {
    exclude(group = "com.github.GTNewHorizons", module = "NotEnoughIds")
}

// Strips META-INF/versions/17/** (Java 17 multi-release entries) from the GT5 deobf jar.
// CodeChickenCore's ASM 5 scanner cannot parse Java 17 class files and aborts scanning the
// entire jar, which prevents GT5's NEI config handler from loading and breaks all machine
// recipe display in NEI (only vanilla crafting grid works).
// Re-run this task after bumping the GT5-Unofficial version in dependencies.gradle.
tasks.register("stripGT5ForNEI") {
    group = "gtnh"
    description = "Regenerates libs/GT5-Unofficial-stripped.jar (no Java 17 multi-release entries) for NEI compatibility"
    notCompatibleWithConfigurationCache("Resolves configurations at execution time to locate the GT5 deobf jar")

    val outFile = layout.projectDirectory.file("libs/GT5-Unofficial-stripped.jar").asFile
    outputs.file(outFile)

    doLast {
        val gt5Jar = configurations.getByName("compileClasspath")
            .resolvedConfiguration.resolvedArtifacts
            .find { it.moduleVersion.id.module.name == "GT5-Unofficial" && it.file.name.endsWith("-deobf.jar") }
            ?.file ?: error("GT5-Unofficial deobf jar not found on compileClasspath")

        outFile.parentFile.mkdirs()

        var kept = 0; var stripped = 0
        val srcZip = ZipFile(gt5Jar)
        val dstZip = ZipOutputStream(outFile.outputStream())

        val entries = srcZip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name.startsWith("META-INF/versions/")) { stripped++; continue }
            dstZip.putNextEntry(ZipEntry(entry.name))
            srcZip.getInputStream(entry).copyTo(dstZip)
            dstZip.closeEntry()
            kept++
        }

        dstZip.close()
        srcZip.close()

        println("[stripGT5ForNEI] Done — kept $kept, stripped $stripped Java-17 entries. ${outFile.length() / 1024 / 1024} MB")
    }
}
