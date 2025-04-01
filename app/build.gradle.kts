/*buildscript {
    //ext.kotlin_version = '2.1.0' // Or a newer version like '2.1.10'
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath( "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.gms:google-services:4.4.0") // Check for the latest version
    }
}*/
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.8.1")



// Do not hardcode this version, instead make it rely on the global variable you declared on top

//No
//classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20"
//Yes
        classpath( "org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.40.1")
    }
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    //id("org.jetbrains.kotlin.android") version "2.0.0" apply false

}

android {
    namespace = "com.example.wisewallet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wisewallet"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding=true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx.v190) // Or libs.androidx.core.ktx if renamed
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)
    implementation (libs.ccp.v261)
    implementation(libs.okhttp)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)


}