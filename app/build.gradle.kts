plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.project.skypass"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.skypass"
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    flavorDimensions += "env"
    productFlavors {
        create("production") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"https://airline.azkazk11.my.id/api/v1/\"",
            )
            buildConfigField(
                type = "String",
                name = "KEY",
                value = "\"..\"",
            )
            buildConfigField(
                type = "String",
                name = "CLIENT_ID_OAUTH2",
                value = "\"597747131491-lgld5ii4fd97g7in8ek462ei74f9iven.apps.googleusercontent.com\"",
            )
        }
        create("integration") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"https://airline.azkazk11.my.id/api/v1/\"",
            )
            buildConfigField(
                type = "String",
                name = "KEY",
                value = "\"..\"",
            )
            buildConfigField(
                type = "String",
                name = "CLIENT_ID_OAUTH2",
                value = "\"597747131491-lgld5ii4fd97g7in8ek462ei74f9iven.apps.googleusercontent.com\"",
            )
        }
    }
    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // coil image loader
    implementation(libs.coil)

    // lifecycle
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)

    // fragment
    implementation(libs.fragment.ktx)

    // navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // room database
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // coroutine
    implementation(libs.coroutine.core)
    implementation(libs.coroutine.android)

    // networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.interceptor)
    implementation(libs.paging)

    // shimmer
    implementation(libs.shimmer)

    // otp view
    implementation(libs.pinview)

    // ticket view
    implementation(libs.ticketview)

    // koin
    implementation(libs.koin.android)

    // testing
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.turbine)
    testImplementation(libs.core.testing)

    // appintro
    implementation(libs.app.intro)

    // dotsIndicator
    implementation(libs.dots.indicator)

    // toast styleable
    implementation(libs.styleable.toast)

    // lottie
    // implementation(libs.lottie)
    implementation("com.airbnb.android:lottie:6.4.1")

    // groupie
    implementation(libs.groupie)
    implementation("com.github.lisawray.groupie:groupie-viewbinding:2.10.1")

    // SeatBookView
    implementation(libs.seat.book)

    // oauth
    implementation(libs.google.oauth)

    // calendar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(libs.view.calendar)
}
