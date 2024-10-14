plugins {

   id("io.quarkus") version "3.13.3"
}

dependencies {
   implementation(platform("org.orph2020.pst.platforms:quarkus-base"))
   implementation(platform("org.orph2020.pst.platforms:pst-base"))
   implementation("org.orph2020.pst:pst-lib")
   implementation("io.quarkus:quarkus-picocli")
   implementation("io.quarkus:quarkus-rest-client-reactive-jackson")
   implementation("io.quarkus:quarkus-oidc-client-reactive-filter")
   testImplementation("io.quarkus:quarkus-junit5")
   testImplementation("io.rest-assured:rest-assured")

}