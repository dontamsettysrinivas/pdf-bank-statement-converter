plugins {
    kotlin("jvm") version "1.9.0"
    id("java")
    id("maven-publish")
}

group = "com.pdfconverter"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    
    // Apache PDFBox for PDF processing
    implementation("org.apache.pdfbox:pdfbox:2.0.30")
    
    // CSV handling
    implementation("com.opencsv:opencsv:5.8")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.pdfconverter"
            artifactId = "pdf-bank-statement-converter"
            version = "0.1.0"
            from(components["java"])
        }
    }
}
