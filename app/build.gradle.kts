plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.orech.patcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.orech.patcher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // פירוק/הרכבה מחדש של dex - ספרייה, לא כלי שורת פקודה.
    implementation("com.android.tools.smali:smali:3.0.3")
    implementation("com.android.tools.smali:smali-baksmali:3.0.3")
    implementation("com.android.tools.smali:smali-dexlib2:3.0.3")

    // חתימת APK/JAR מחדש - אותה ספרייה ש-AGP עצמו משתמש בה.
    implementation("com.android.tools.build:apksig:8.5.0")

    // עריכת משאבים בינאריים (resources.arsc / AXML) בתוך APK - בלי apktool/PC.
    implementation("io.github.reandroid:ARSCLib:1.3.5")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
