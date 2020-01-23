plugins {
  java
  id("com.palantir.graal") version "0.6.0-69-ga9559b9"
}

repositories {
  jcenter()
  maven("https://dl.bintray.com/vaccovecrana/vacco-oss")
}

group = "io.vacco.zetema"
version = "0.0.1"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation("io.vacco.ufn:ufn:0.0.1")
  implementation("dnsjava:dnsjava:2.1.9")
  implementation("org.slf4j:slf4j-simple:1.7.30")
  testImplementation("junit:junit:4.12")
}

graal {
  mainClass("io.vacco.dns.DServer")
  outputName("zetema")
  option("-H:-UseServiceLoaderFeature")
  option("--no-fallback")
  option("--allow-incomplete-classpath")
  option("--report-unsupported-elements-at-runtime")
  // option("--static")
}

