plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(platform("software.amazon.awssdk:bom:2.31.67"))
    implementation("software.amazon.awssdk:s3")
    implementation("com.mysql:mysql-connector-j:9.0.0")
    implementation("com.itextpdf:itext7-core:9.6.0")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.13")}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "25.0.2"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("com.berrybyte.login.LoginMain")
}