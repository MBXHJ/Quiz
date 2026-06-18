plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("maven-publish")
}

android {
    namespace = "com.quizapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.quizapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.apache.poi)

    debugImplementation(libs.androidx.ui.tooling)
}

// ── GitHub Packages Maven 发布 ──
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.quizapp"
                artifactId = "quizapp"
                version = android.defaultConfig.versionName
                artifact(tasks.named("assembleDebug").map {
                    file("build/outputs/apk/debug/app-debug.apk")
                }) {
                    classifier = "debug"
                    extension = "apk"
                }
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/MBXHJ/Quiz")
                credentials {
                    username = project.findProperty("gpr.user") as? String ?: System.getenv("GPR_USER")
                    password = project.findProperty("gpr.key") as? String ?: System.getenv("GPR_KEY")
                }
            }
        }
    }
}
