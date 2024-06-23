import org.objectweb.asm.Handle
import xland.gradle.forgeInitInjector.TargetMethodGen
import org.objectweb.asm.Opcodes as ops

buildscript {
    repositories {
        maven (url = "https://maven.aliyun.com/repository/public") {
            name = "Aliyun Mirror"
        }
    }
    dependencies {
        classpath("org.ow2.asm:asm:9.3")
    }
}

plugins {
    java
    id("xland.gradle.forge-init-injector") version "1.1.1"
}

group = "wiki.mcbbs.mod"
version = "2.3.1"

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public") {
        name = "Aliyun Mirror"
    }
    maven(url = "https://mvn.7c7.icu") {
        name = "COVID-Trump"
    }
    maven(url = "https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
    }
    mavenCentral()
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

dependencies {
    compileOnly("xland.mcmod:enchlevel-langpatch:1.2.0:api")
    //compileOnly("org.apache.commons:commons-lang3:3.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

forgeInitInjector {
    modId = "eeemod"
    stubPackage = "StbnzKslE0xJZ9V\$4syOI"
    neoFlag("pre_20_5", "post_20_5")
    //setClientEntrypoint("wiki/mcbbs/mod/eee/EEE")
    subscriptions.addClassPredicate("xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatch",
        TargetMethodGen { _, _, _, _ ->
            Handle(ops.H_INVOKESTATIC, "wiki/mcbbs/mod/eee/EEE", "init", "()V", false)
    })

    // Legacy not required - no longer register patches!
}

tasks.processResources {
    //dependsOn("generateModClass")
    inputs.property("version", project.version)
    filesMatching(listOf("fabric.mod.json", "quilt.mod.json5", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand("version" to project.version)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
