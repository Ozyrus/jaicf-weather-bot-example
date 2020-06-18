plugins {
    application
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    kotlin("plugin.serialization") version "1.3.70"
}

group = "com.justai.jaicf"
version = "1.0.0"

val jaicf = "0.4.0"
val slf4j = "1.7.30"
val ktor = "1.3.1"

application {
    mainClassName = "com.justai.jaicf.template.ServerKt"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven(uri("https://jitpack.io"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.justai.jaicf:telegram:$jaicf")
    implementation("org.slf4j:slf4j-simple:$slf4j")
    implementation("org.slf4j:slf4j-log4j12:$slf4j")
    implementation("com.justai.jaicf:caila:$jaicf")
    implementation("com.justai.jaicf:core:$jaicf")
    implementation("com.justai.jaicf:google-actions:$jaicf")
    implementation("com.justai.jaicf:mongo:$jaicf")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test {
        useJUnitPlatform()
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.create("stage") {
    dependsOn("shadowJar")
}
