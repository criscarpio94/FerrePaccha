plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    //Pluggin de servicios de google
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ferrepaccha"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.ferrepaccha"
        minSdk = 27

        //target sdk
        targetSdk = 37

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //DEPENDENCIAS PARA PROYECTO

    //Navegacion entre pantallas
    implementation("androidx.navigation:navigation-compose:2.8.5")

    //Imagenes desde internet (Firebase Storage)
    implementation("io.coil-kt:coil-compose:2.6.0")

    //Iconos
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    //platafomra de firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    //librerias base de datos y logins
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    //libreria para almacenar y tomar fotografias
    implementation("com.google.firebase:firebase-storage-ktx")

}