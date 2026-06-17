plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

subprojects {
    plugins.apply(rootProject.libs.plugins.ktlint.get().pluginId)
    plugins.apply(rootProject.libs.plugins.detekt.get().pluginId)
}

detekt {
    config.setFrom(files("${rootDir}/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
}

ktlint {
    version.set("1.3.0")
}
