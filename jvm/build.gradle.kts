// note that at the moment will see how far this can go without being a "quarkus" application.
plugins {
   java
}
repositories {
   mavenCentral()
   mavenLocal()
}

dependencies {
   implementation(platform("org.orph2020.pst.platforms:pst-base"))

}