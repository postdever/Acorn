import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm")
    `maven-publish`
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    // Minecraft 26.1+ is deobfuscated, so mappings are no longer required and mod
    // dependencies use the standard implementation/compileOnly/runtimeOnly configs.
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    runtimeOnly("me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")

    // Melinoe from local Maven - run ./gradlew publishToMavenLocal in Melinoe project first.
    // Requires a Melinoe build compiled against Minecraft 26.1.2.
    compileOnly("me.melinoe:Melinoe:${property("melinoe_version")}") {
        exclude(group = "dev.firstdark.discordrpc")
    }

    implementation("com.github.stivais:Commodore:${property("commodore_version")}")
}

loom {
    runConfigs.named("client") {
        isIdeConfigGenerated = true
        // Loom 1.17 exposes vmArgs as an immutable list; use the vmArgs(...) method to append.
        // Loom now wires the Mixin javaagent into dev run configs itself, so the old manual
        // sponge-mixin -javaagent injection is no longer needed.
        vmArgs(
            "-Dmixin.debug.export=true",
            "-Ddevauth.enabled=true",
            "-Ddevauth.account=main",
            "-XX:+AllowEnhancedClassRedefinition"
        )
    }

    runConfigs.named("server") {
        isIdeConfigGenerated = false
    }
}

tasks {
    processResources {
        val expandProps = mapOf(
            "mod_version" to project.property("mod_version") as String,
            "minecraft_version" to project.property("minecraft_version") as String,
            "loader_version" to project.property("loader_version") as String,
            "mod_id" to project.property("mod_id") as String,
            "mod_name" to project.property("mod_name") as String,
            "mod_description" to project.property("mod_description") as String,
            "fabric_api_version" to project.property("fabric_api_version") as String,
            "fabric_kotlin_version" to project.property("fabric_kotlin_version") as String
        )

        inputs.properties(expandProps)

        filesMatching("fabric.mod.json") {
            expand(expandProps)
        }
    }

    compileKotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
            freeCompilerArgs.add("-Xlambdas=class") // Commodore
        }
    }

    compileJava {
        sourceCompatibility = "25"
        targetCompatibility = "25"
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}

base {
    archivesName.set(project.property("archives_base_name") as String)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}
