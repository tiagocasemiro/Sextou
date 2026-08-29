import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.isFile }?.inputStream()?.use { input ->
        load(input)
    }
}

fun readSecretProperty(name: String): String = providers.gradleProperty(name)
    .orElse(localProperties.getProperty(name).orEmpty())
    .get()

fun String.toBuildConfigLiteral(): String = "\"${
    replace("\\", "\\\\").replace("\"", "\\\"")
}\""

val placesApiKey = readSecretProperty("PLACES_API_KEY")
val mapsApiKey = readSecretProperty("MAPS_API_KEY")

android {
    namespace = "com.sextou"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sextou"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "PLACES_API_KEY", placesApiKey.toBuildConfigLiteral())
        buildConfigField("String", "MAPS_API_KEY", mapsApiKey.toBuildConfigLiteral())
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":design-system"))
    implementation(project(":domain"))
    implementation(project(":local"))
    implementation(project(":networking"))
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:6.1.0")
    implementation("io.insert-koin:koin-android:4.0.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}
