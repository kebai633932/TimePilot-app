// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("com.android.library") version "8.3.2" apply false  // 添加 library 插件
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    // 添加其他常用插件
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false  // Kotlin JVM 插件
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false  // Kotlin 序列化
}

// 构建脚本依赖配置（如果需要）
buildscript {
    dependencies {
        // 这里可以添加构建脚本本身需要的依赖
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")  // 示例：开源许可证插件
        classpath("com.google.gms:google-services:4.4.0")  // 示例：Google Services 插件
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")  // 示例：Firebase Crashlytics
    }
}