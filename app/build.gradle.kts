plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.healthcareproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.healthcareproject"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // AI와 HTTP 통신을 위한 Retrofit 라이브러리 추가.
    // 버전 관련 내용은 libs.versions.toml 참고
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}