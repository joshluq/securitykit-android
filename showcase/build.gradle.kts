import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.pluginkit.android.application)
    alias(libs.plugins.pluginkit.android.compose)
    alias(libs.plugins.pluginkit.android.hilt)
    alias(libs.plugins.pluginkit.quality)
    alias(libs.plugins.pluginkit.android.testing)
}

configure<ApplicationExtension> {
    namespace = "es.joshluq.securitykit.showcase"

    defaultConfig {
        applicationId = "es.joshluq.securitykit.showcase"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":securitykit"))
}
