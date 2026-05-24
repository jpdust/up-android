plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("jacoco")
}

dependencyLocking {
    configurations.configureEach {
        if (name.endsWith("RuntimeClasspath") ||
            name.endsWith("CompileClasspath") ||
            name.endsWith("AnnotationProcessorClasspath") ||
            name == "classpath"
        ) {
            resolutionStrategy.activateDependencyLocking()
        }
    }
}

jacoco {
    toolVersion = "0.8.12"
}

android {
    namespace = "com.unstampedpages.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.unstampedpages.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 202605241 // Set to the highest code in Play Console — workflow will increment from here
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Release signing reads from environment variables injected by CI.
    // Local builds without these vars will produce an unsigned release AAB.
    signingConfigs {
        val keystorePath = System.getenv("KEYSTORE_PATH")
        if (!keystorePath.isNullOrBlank()) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            isDebuggable = true
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            signingConfigs.findByName("release")?.let { signingConfig = it }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Browser support
    implementation(libs.androidx.browser)

    // JSON parsing
    implementation(libs.gson)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.json)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.navigation.testing)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// ---------------------------------------------------------------------------
// Classes that should be excluded from coverage measurement.
// Covers generated code (R, BuildConfig, Compose internals, KSP stubs).
// ---------------------------------------------------------------------------
val jacocoExclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*\$Lambda\$*.*",
    "**/*\$inlined\$*.*",
    "**/ComposableSingletons*.*",
    "**/*_Factory*.*",
    "**/*\$*\$*.*"
)

// ---------------------------------------------------------------------------
// Register a JacocoReport task for every build type so that both debug and
// release coverage can be generated independently. Output XML files land at:
//   build/reports/jacoco/{buildType}/jacoco.xml
// which is the path declared in sonar.coverage.jacoco.xmlReportPaths.
//
// Unit-test exec data:         outputs/unit_test_code_coverage/…/test*.exec
// Instrumented-test exec data: outputs/code_coverage/…/connected/**/*.ec
// Both are included; missing files are silently skipped by fileTree.
// ---------------------------------------------------------------------------
android.buildTypes.configureEach {
    val buildTypeName = name
    val capitalizedBuildType = buildTypeName.replaceFirstChar { it.uppercase() }

    tasks.register<JacocoReport>("jacoco${capitalizedBuildType}CoverageReport") {
        group = "Reporting"
        description = "Generate JaCoCo coverage report for the $buildTypeName build type."
        dependsOn("test${capitalizedBuildType}UnitTest")

        reports {
            xml.required.set(true)
            xml.outputLocation.set(
                layout.buildDirectory.file("reports/jacoco/$buildTypeName/jacoco.xml")
            )
            html.required.set(true)
            html.outputLocation.set(
                layout.buildDirectory.dir("reports/jacoco/$buildTypeName/html")
            )
        }

        // AGP 9.x outputs compiled Kotlin classes to:
        //   intermediates/built_in_kotlinc/{buildType}/compile{BuildType}Kotlin/classes
        // The JaCoCo-instrumented copy under intermediates/classes must NOT be used here —
        // the report task needs the original pre-instrumentation bytecode.
        val kotlinClasses = fileTree(
            layout.buildDirectory.dir(
                "intermediates/built_in_kotlinc/$buildTypeName/compile${capitalizedBuildType}Kotlin/classes"
            )
        ) { exclude(jacocoExclusions) }

        classDirectories.setFrom(files(kotlinClasses))
        sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

        // Collect execution data from both unit tests and instrumented tests.
        // fileTree silently ignores paths that do not exist yet.
        executionData.setFrom(
            fileTree(layout.buildDirectory) {
                include(
                    "outputs/unit_test_code_coverage/${buildTypeName}UnitTest/" +
                        "test${capitalizedBuildType}UnitTest.exec",
                    "outputs/code_coverage/${buildTypeName}AndroidTest/connected/**/*.ec"
                )
            }
        )
    }
}

// Convenience task: generate coverage reports for all build types at once.
tasks.register("jacocoAllCoverageReports") {
    group = "Reporting"
    description = "Generate JaCoCo coverage reports for all build types."
    dependsOn(
        android.buildTypes.map { buildType ->
            "jacoco${buildType.name.replaceFirstChar { it.uppercase() }}CoverageReport"
        }
    )
}
