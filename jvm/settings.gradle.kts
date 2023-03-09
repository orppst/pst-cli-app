pluginManagement {
    includeBuild("../../build-logic")

}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        mavenLocal()
        /*
        add this repository to pick up the SNAPSHOT version of the IVOA base library - in the future  this
        will not be necessary when this library is released as a non-SNAPSHOT version.
         */
        maven {
            url= uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }

    }

}

includeBuild("../../pst-lib")

rootProject.name="pst-cli-jvm"