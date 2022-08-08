import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes as ops

import java.nio.file.Files

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
    id("java")
}

group = "wiki.mcbbs.mod"
version = "2.0.2"

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public") {
        name = "Aliyun Mirror"
    }
    maven(url = "https://covid-trump.github.io/mvn/") {
        name = "COVID-Trump"
    }
    maven(url = "https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
    }
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

dependencies {
    compileOnly(files("libs/fakerl2.jar"))
    compileOnly("xland.mcmod:enchlevel-langpatch:1.0.1")
    compileOnly("org.apache.commons:commons-lang3:3.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

object Constants {
    const val pkg = "WmILjFiPRJHZhS7zflHaI"
    const val modId = "eeemod"
    const val mainClass = "wiki/mcbbs/mod/eee/EEE"
}

task("generateModClass") {
    val rootDir = tasks.processResources.get().destinationDir
    ResourceGroovyMethods.deleteDir(rootDir.resolve(Constants.pkg))
    Files.createDirectories(rootDir.resolve(Constants.pkg).toPath())

    var cw = ClassWriter(3)
    cw.visit(ops.V1_6, ops.ACC_PUBLIC, "${Constants.pkg}/A", null, "java/lang/Object", null)
    cw.visitAnnotation("Lnet/minecraftforge/fml/common/Mod;", true)
        .visit("value", Constants.modId)
    cw.visitAnnotation("Lnet/minecraftforge/api/distmarker/OnlyIn;", true)
        .visitEnum("value", "Lnet/minecraftforge/api/distmarker/Dist;", "CLIENT")

    cw.visitMethod(ops.ACC_PUBLIC, "<init>", "()V", null, null).run {
        visitVarInsn(ops.ALOAD, 0)
        visitMethodInsn(ops.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }
    cw.visitSource(null, "ASM Generated")
    rootDir.resolve("${Constants.pkg}/A.class").writeBytes(cw.toByteArray())

    cw = ClassWriter(3)
    cw.visit(ops.V1_8, ops.ACC_PUBLIC, "${Constants.pkg}/B", null, "java/lang/Object",
            arrayOf("java/lang/Runnable"))
    cw.visitAnnotation("Lnet/minecraftforge/fml/common/Mod\$EventBusSubscriber;", true).run {
        visit("modid", Constants.modId)
        visitEnum("bus", "Lnet/minecraftforge/fml/common/Mod\$EventBusSubscriber\$Bus;",
            "MOD")
        visitArray("value").run {
            visitEnum(null,
            "Lnet/minecraftforge/api/distmarker/Dist;", "CLIENT")
            visitEnd()
        }
    }
    cw.visitMethod(ops.ACC_PUBLIC, "<init>", "()V", null, null).run {
        visitVarInsn(ops.ALOAD, 0)
        visitMethodInsn(ops.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }

    cw.visitMethod(ops.ACC_PUBLIC, "run", "()V", null, null).run {
        visitMethodInsn(ops.INVOKESTATIC, Constants.mainClass, "init", "()V", false)
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }

    cw.visitMethod(ops.ACC_PUBLIC + ops.ACC_STATIC, "CLIENT",
        "(Lnet/minecraftforge/fml/event/lifecycle/FMLClientSetupEvent;)V",
        null, null).run {
        visitAnnotation("Lnet/minecraftforge/eventbus/api/SubscribeEvent;", true)
        visitVarInsn(ops.ALOAD, 0)
        visitTypeInsn(ops.NEW, "${Constants.pkg}/B")
        visitInsn(ops.DUP)
        visitMethodInsn(ops.INVOKESPECIAL, "${Constants.pkg}/B", "<init>", "()V", false)
        visitMethodInsn(ops.INVOKEVIRTUAL, "net/minecraftforge/fml/event/lifecycle/FMLClientSetupEvent",
            "enqueueWork", "(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;", false)
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }
    rootDir.resolve("${Constants.pkg}/B.class").writeBytes(cw.toByteArray())
}

tasks.processResources {
    dependsOn("generateModClass")
    inputs.property("version", project.version)
    filesMatching(listOf("fabric.mod.json", "quilt.mod.json5")) {
        expand("version" to project.version)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
