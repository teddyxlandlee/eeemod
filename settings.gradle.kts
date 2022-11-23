pluginManagement {
    repositories {
        maven("https://covid-trump.github.io/mvn") {
            name = "COVID-Trump"
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

