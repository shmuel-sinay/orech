plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// גרסת מוכר: אפליקציה נפרדת (applicationId משלה, כדי שהרשאת root תינתן לה בלבד).
// משתמשת במנוע התיקונים של :app בלי לשנות אותו - אותם קבצי קוד, אותם assets.
android {
    namespace = "com.orech.seller"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.orech.seller"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
        // רק גיבוב של ה-PIN נשמר באפליקציה, לא הקוד עצמו.
        buildConfigField("String", "PIN_SHA256", "\"0a6ac7af764df6ddd78b15dcc676dd10865d942e576b2c3b3cf88581e31d2c23\"")
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets["main"].java.srcDir("../app/src/main/java")
    sourceSets["main"].assets.srcDir("../app/src/main/assets")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// המסך הקיים של :app תלוי ב-R של :app, ולכן לא נכלל כאן.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    exclude("**/MainActivity.kt")
}

dependencies {
    implementation("com.android.tools.smali:smali:3.0.3")
    implementation("com.android.tools.smali:smali-baksmali:3.0.3")
    implementation("com.android.tools.smali:smali-dexlib2:3.0.3")
    implementation("com.android.tools.build:apksig:8.5.0")
    implementation("io.github.reandroid:ARSCLib:1.3.5")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
