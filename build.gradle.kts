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
    implementation("com.mysql:mysql-connector-j:9.0.0")
    implementation("org.openjfx:javafx:25.0.2")
}

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