plugins {
    id("java")
}

group = "io.github.pfguilherme"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.liquibase:liquibase-core:4.29.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.projectlombok:lombok:1.18.38")

    compileOnly("org.projectlombok:lombok:1.18.38")

    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.test {
    useJUnitPlatform()
}