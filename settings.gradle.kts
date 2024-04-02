pluginManagement {
    repositories {
       // Formerly COVID-Trump maven("https://mvn.7c7.icu/") {
            name = "7c7maven"
        }
        maven (url = "https://maven.aliyun.com/repository/public") {
            name = "Aliyun Mirror"
        }
        maven (url = "https://maven.aliyun.com/repository/gradle-plugin") {
            name = "Aliyun Mirror"
        }
        mavenLocal()    // will be removed
    }
}

rootProject.name = "eeemod"

