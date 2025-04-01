plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "ru.resodostudios.cashsense.core.datastore.proto"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

androidComponents.beforeVariants {
    android.sourceSets.register(it.name) {
        val buildDir = layout.buildDirectory.get().asFile
        java.srcDir(buildDir.resolve("generated/sources/proto/${it.name}/java"))
        kotlin.srcDir(buildDir.resolve("generated/sources/proto/${it.name}/kotlin"))
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}