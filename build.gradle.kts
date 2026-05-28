buildscript {

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.newrelic.agent.android:agent-gradle-plugin:7.7.5")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
