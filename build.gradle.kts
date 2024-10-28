plugins {
  java
  id("org.springframework.boot") version "3.3.0"
  id("io.spring.dependency-management") version "1.1.4"

  // Code quality plugins
  checkstyle
  jacoco
  id("org.sonarqube") version "4.4.1.3373"
}

group = "uk.nhs.hee.tis.trainee"
version = "1.6.0"

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

val mapstructVersion = "1.5.5.Final"
val mongockVersion = "5.4.2"
val sentryVersion = "7.10.0"

dependencies {
  // Spring Boot starters
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
  implementation("org.springframework.boot:spring-boot-starter-web")

  // AWS-XRay
  implementation("com.amazonaws:aws-xray-recorder-sdk-spring:2.16.0")

  // Lombok
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  // MapStruct
  implementation("org.mapstruct:mapstruct:$mapstructVersion")
  annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

  // Mongock
  implementation("io.mongock:mongock-springboot:${mongockVersion}")
  implementation("io.mongock:mongodb-springdata-v4-driver:${mongockVersion}")

  // Sentry reporting
  implementation("io.sentry:sentry-spring-boot-starter-jakarta:$sentryVersion")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
    vendor.set(JvmVendorSpec.ADOPTIUM)
  }
}

checkstyle {
  config = resources.text.fromArchiveEntry(configurations.checkstyle.get().first(), "google_checks.xml")
}

sonarqube {
  properties {
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.login", System.getenv("SONAR_TOKEN"))
    property("sonar.organization", "health-education-england")

    property("sonar.projectKey", "Health-Education-England_tis-trainee-reference")

    property("sonar.java.checkstyle.reportPaths",
      "build/reports/checkstyle/main.xml,build/reports/checkstyle/test.xml")
  }
}

testing {
  suites {
    configureEach {
      if (this is JvmTestSuite) {
        useJUnitJupiter()
        dependencies {
          implementation(project())
          implementation("org.springframework.boot:spring-boot-starter-test")
        }
      }
    }

    val test by getting(JvmTestSuite::class) {
      dependencies {
        annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
      }
    }

    register<JvmTestSuite>("integrationTest") {
      dependencies {
        implementation("org.springframework.boot:spring-boot-testcontainers")
        implementation("org.testcontainers:junit-jupiter")
        implementation("org.testcontainers:mongodb")
      }

      targets {
        all {
          testTask.configure {
            shouldRunAfter(test)
            systemProperty("spring.profiles.active", "test")
          }
        }
      }
    }

    // Include implementation dependencies.
    val integrationTestImplementation by configurations.getting {
      extendsFrom(configurations.implementation.get())
    }
  }
}

tasks.named("check") {
  dependsOn(testing.suites.named("integrationTest"))
}

tasks.jacocoTestReport {
  reports {
    html.required.set(true)
    xml.required.set(true)
  }
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}
