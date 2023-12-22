import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
//    alias(libs.plugins.secrets.gradle.plugin) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.com.benmanes.versions)
    alias(libs.plugins.com.dagger.hilt) apply (false)
//    alias(libs.plugins.com.google.services) apply false
//    alias(libs.plugins.com.firebase.crashlytics) apply false
    cleanup
    base
}

tasks {
    withType<DependencyUpdatesTask>().configureEach {
        rejectVersionIf {
            candidate.version.isStableVersion().not()
        }
    }
}