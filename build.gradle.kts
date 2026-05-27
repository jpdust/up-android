buildscript {

    // The following section is needed only if pluginMangement is not used in settings.gradle
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
    alias(libs.plugins.sonarqube)
}


// ---------------------------------------------------------------------------
// SonarQube analysis configuration
//
// Set sonar.host.url and sonar.token via gradle.properties or environment
// variables (SONAR_HOST_URL / SONAR_TOKEN) — never commit credentials here.
//
// Run analysis:  ./gradlew jacocoDebugCoverageReport sonar
// ---------------------------------------------------------------------------
sonar {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "jpdust")
        property("sonar.projectKey", "jpdust_up-android")
        property("sonar.projectName", "up-android")

        // Compiled class files — point at the debug variant for analysis
        property("sonar.java.binaries", "${rootDir}/app/build/tmp/kotlin-classes/debug")

        // JaCoCo XML reports — one path per build type, comma-separated.
        // Sonar merges coverage from all listed files.
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            listOf("debug", "release").joinToString(",") {
                "${rootDir}/app/build/reports/jacoco/$it/jacoco.xml"
            }
        )

        // Exclude generated / framework code from analysis and coverage
        property(
            "sonar.exclusions",
            listOf(
                "**/R.class", "**/R\$*.class",
                "**/BuildConfig.*", "**/Manifest*.*",
                "android/**/*.*"
            ).joinToString(",")
        )
        property(
            "sonar.coverage.exclusions",
            listOf(
                "**/R.class", "**/R\$*.class",
                "**/BuildConfig.*", "**/Manifest*.*",
                "**/*Test*.*", "android/**/*.*"
            ).joinToString(",")
        )

        // Android Lint report
        property(
            "sonar.androidLint.reportPaths",
            "${rootDir}/app/build/reports/lint-results-debug.xml"
        )
    }
}
