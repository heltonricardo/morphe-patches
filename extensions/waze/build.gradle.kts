extension {
    name = "extensions/waze.mpe"
}

android {
    namespace = "app.morphe.extension.waze"

    defaultConfig {
        minSdk = 32
    }
}

configurations.configureEach {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    exclude(group = "org.jetbrains", module = "annotations")
}
