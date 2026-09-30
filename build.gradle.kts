buildscript {

    repositories {
        mavenCentral()
    }

    dependencies {
        // Must resolve to the exact same version as the `newrelic-android-agent`
        // runtime dependency in app/build.gradle.kts (both share the `newrelic`
        // version.ref in gradle/libs.versions.toml). The agent embeds a version
        // marker at instrumentation time; a mismatch between this plugin and the
        // SDK it instruments makes the agent silently stop reporting events.
        // This has broken event reporting twice before (PR #126) from bumping
        // one without the other — do not hardcode this version independently.
        classpath(libs.newrelic.agent.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
