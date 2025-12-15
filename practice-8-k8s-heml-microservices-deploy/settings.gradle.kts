plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "practice-8-k8s-heml-microservices-deploy"

include(
    ":auth-service",
    ":telemetry-service",
    ":race-service"
)
