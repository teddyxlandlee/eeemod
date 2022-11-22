import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
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
version = "2.0.6"

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
    mavenCentral()
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

dependencies {
    //compileOnly(files("libs/fakerl2.jar"))
    compileOnly("xland.mcmod:enchlevel-langpatch:1.2.0:api")
    //compileOnly("org.apache.commons:commons-lang3:3.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
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
    cw.visit(ops.V1_6, ops.ACC_PUBLIC + ops.ACC_SUPER, "${Constants.pkg}/A", null, "java/lang/Object", null)
    cw.visitAnnotation("Lnet/minecraftforge/fml/common/Mod;", true)
        .visit("value", Constants.modId)
    cw.visitAnnotation("Lnet/minecraftforge/api/distmarker/OnlyIn;", true)
        .visitEnum("value", "Lnet/minecraftforge/api/distmarker/Dist;", "CLIENT")

    cw.visitMethod(ops.ACC_PUBLIC, "<init>", "()V", null, null).run {
        visitVarInsn(ops.ALOAD, 0)
        visitMethodInsn(ops.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        // Abnormal START -- langPatchConf3 compat
        visitMethodInsn(ops.INVOKESTATIC, "${Constants.pkg}/A", "activate", "()V", false)
        // Abnormal END
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }

    // Abnormal START -- langPatchConf3 compat
    cw.visitMethod(ops.ACC_STATIC + ops.ACC_PRIVATE + ops.ACC_SYNTHETIC, "activate", "()V", null, null).run {
        val b01 = Label()
        val b02 = Label()
        val b03 = Label()
        visitJumpInsn(ops.GOTO, b01)
        visitLabel(b03)
        visitLineNumber(1001, b03)
        visitInsn(ops.POP) // pop the exception
        visitInsn(ops.RETURN)
        visitLabel(b01)
        visitLineNumber(1002, b01)
        visitLdcInsn("xland.mcmod.enchlevellangpatch.ext.conf3.forge.LangPatchConfigOverrideEvent")
        visitMethodInsn(ops.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false)
        visitInsn(ops.POP)
        visitLabel(b02)
        visitLineNumber(1003, b02)
        visitTryCatchBlock(b01, b02, b03, "java/lang/ClassNotFoundException")
        visitMethodInsn(ops.INVOKESTATIC, "net/minecraftforge/fml/javafmlmod/FMLJavaModLoadingContext", "get",
            "()Lnet/minecraftforge/fml/javafmlmod/FMLJavaModLoadingContext", false)
        visitMethodInsn(ops.INVOKEVIRTUAL, "net/minecraftforge/fml/javafmlmod/FMLJavaModLoadingContext", "getModEventBus",
            "()Lnet/minecraftforge/eventbus/api/IEventBus;", false)
        visitLdcInsn(org.objectweb.asm.Type.getObjectType("${Constants.pkg}/C"))
        visitMethodInsn(ops.INVOKEINTERFACE, "net/minecraftforge/eventbus/api/IEventBus", "register",
            "(Ljava/lang/Object;)V", true)
    }
    // Abnormal END

    cw.visitSource(null, "ASM Generated")
    rootDir.resolve("${Constants.pkg}/A.class").writeBytes(cw.toByteArray())


    cw = ClassWriter(3)
    cw.visit(ops.V1_8, ops.ACC_PUBLIC + ops.ACC_SUPER, "${Constants.pkg}/B", null, "java/lang/Object",
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
        visitTypeInsn(ops.NEW, "${Constants.pkg}/B")
        visitInsn(ops.DUP)
        visitMethodInsn(ops.INVOKESPECIAL, "${Constants.pkg}/B", "<init>", "()V", false)
        visitVarInsn(ops.ALOAD, 0)
        visitInsn(ops.SWAP)
        visitMethodInsn(ops.INVOKEVIRTUAL, "net/minecraftforge/fml/event/lifecycle/FMLClientSetupEvent",
            "enqueueWork", "(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;", false)
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }
    cw.visitSource(null, "ASM Generated")
    rootDir.resolve("${Constants.pkg}/B.class").writeBytes(cw.toByteArray())


    // Abnormal START -- langPatchConfig3
    cw = ClassWriter(3)
    cw.visit(ops.V1_8, ops.ACC_PUBLIC + ops.ACC_SUPER, "${Constants.pkg}/C", null, "java/lang/Object",
        arrayOf("java/lang/Runnable"))
    cw.visitAnnotation("Lnet/minecraftforge/api/distmarker/OnlyIn;", true)
        .visitEnum("value", "Lnet/minecraftforge/api/distmarker/Dist;", "CLIENT")

    cw.visitMethod(ops.ACC_PUBLIC + ops.ACC_STATIC, "langPatchConfig3",
        "(Lxland/mcmod/enchlevellangpatch/ext/conf3/forge/LangPatchConfigOverrideEvent;)V",
        null, null).run {
        visitAnnotation("Lnet/minecraftforge/eventbus/api/SubscribeEvent;", true)
        visitVarInsn(ops.ALOAD, 0)
        visitMethodInsn(ops.INVOKEVIRTUAL, "xland/mcmod/enchlevellangpatch/ext/conf3/forge/LangPatchConfigOverrideEvent",
            "terminateAll", "()V", false)
        visitInsn(ops.RETURN)
        visitAnnotation("Lnet/minecraftforge/eventbus/api/SubscribeEvent;", true)
        visitMaxs(-1, -1)
    }
    cw.visitMethod(ops.ACC_PUBLIC, "<init>", "()V", null, null).run {
        visitVarInsn(ops.ALOAD, 0)
        visitMethodInsn(ops.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        visitInsn(ops.RETURN)
        visitMaxs(-1, -1)
    }
    cw.visitSource(null, "ASM Generated")
    rootDir.resolve("${Constants.pkg}/C.class").writeBytes(cw.toByteArray())
    // Abnormal END
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
