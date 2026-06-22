plugins {
    id("net.fabricmc.fabric-loom")
    alias(libs.plugins.shadow)
}

val shade: Configuration by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    shadeModule(projects.signedvelocityBackendCommon)
    shadeModule(projects.signedvelocityShared)
}

fun DependencyHandlerScope.shadeModule(module: ProjectDependency) {
    shade(module) {
        isTransitive = false
    }
    implementation(module) {
        isTransitive = false
    }
}

tasks {
    shadowJar {
        configurations = listOf(shade)
    }
    processResources {
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    jar {
        archiveFileName.set("${rootProject.name}-Fabric-${project.version}.jar")
        destinationDirectory.set(file("${rootProject.projectDir}/build"))
    }
}

// Minecraft 26.1.2 ships Java 25 bytecode and Loom requires a JDK 25, so this
// module overrides the Java 21 toolchain/target used by the rest of the project.
java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(25)
}
