import org.sonarqube.gradle.SonarExtension

buildscript {

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    // Force commons-io to 2.20.0 across every dependency resolved in this
    // buildscript classpath. This covers both the New Relic plugin and the
    // SonarQube plugin (and all their transitive deps), ensuring the
    // BoundedInputStream.builder() API is always available at runtime.
    // NOTE: this force only applies to classpath deps declared HERE. Plugins
    // applied via the plugins {} block use a separate classpath not covered by
    // this block — which is why SonarQube is declared here instead.
    configurations.all {
        resolutionStrategy {
            force("commons-io:commons-io:2.20.0")
        }
    }

    dependencies {
        classpath("com.newrelic.agent.android:agent-gradle-plugin:7.7.5")
        // Declared here (not in plugins {}) so the resolutionStrategy.force above
        // applies to its transitive deps, including commons-io via commons-compress.
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:7.3.0.8198")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

apply(plugin = "org.sonarqube")


// Ensure commons-io is never resolved below 2.20.0 in any subproject.
// The SonarQube plugin uses the builder() API (commons-io 2.7+). Android tools
// only request 2.16.1; this guard prevents a future transitive change from
// silently downgrading the resolved version.
allprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "commons-io" && requested.name == "commons-io") {
                useVersion("2.20.0")
                because("Pin commons-io >= 2.7 (builder() API) required by SonarQube; Android tools only request 2.16.1")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SonarQube analysis configuration
//
// Set sonar.host.url and sonar.token via gradle.properties or environment
// variables (SONAR_HOST_URL / SONAR_TOKEN) — never commit credentials here.
//
// Run analysis:  ./gradlew jacocoDebugCoverageReport sonar
// ---------------------------------------------------------------------------
configure<SonarExtension> {
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
