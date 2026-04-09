plugins {

   id("io.quarkus") version "3.33"
}

dependencies {
   implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.33"))
   implementation("org.orph2020.pst:pst-lib")
   implementation("io.quarkus:quarkus-picocli")
   implementation("io.quarkus:quarkus-rest-client-jackson")
   implementation("io.quarkus:quarkus-rest-client-oidc-filter")
   testImplementation("io.quarkus:quarkus-junit5")
   testImplementation("io.rest-assured:rest-assured")

}