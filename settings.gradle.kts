rootProject.name = "tis-trainee-reference"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }

  versionCatalogs {
    create("libs") {
      from("uk.nhs.tis.trainee:version-catalog:0.0.3")
    }
  }
}
