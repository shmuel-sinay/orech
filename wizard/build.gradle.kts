plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// אשף הגדרה חלופי: מחליף את com.google.android.setupwizard. בלי תלויות חיצוניות כדי שהקובץ יישאר קטן.
android {
    namespace = "com.google.android.setupwizard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.google.android.setupwizard"
        minSdk = 29
        targetSdk = 33
        versionCode = 90001
        versionName = "orech-1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
