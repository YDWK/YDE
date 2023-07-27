[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![codecov](https://codecov.io/gh/YDWK/YDWK/branch/master/graph/badge.svg?token=LKIA8T6N6J)](https://codecov.io/gh/YDWK/YDWK)
[![yde](https://img.shields.io/badge/YDE--Version-v1.1.0-blue)](https://github.com/YDWK/YDE/releases/tag/v1.1.0)

# YDE 

YDE contains most of if not all of the discord entitles which are used in [ydwk](https://github.com/YDWK/YDWK) and these discord entities try as much as possible to adhere to the way discord has some but have some minor changes here and there such as with some minor changes to name or where some variables are located in some files but these changes do not make it significantly different.

## :package: Installation

### Grovy DLS gradle
```groovy
repositories {
    mavenCentral()
}
dependencies {
    implementation "io.github.realyusufismail:yde:${project.version}"
}
```

### Kotlin DLS gradle
```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("io.github.realyusufismail:yde:${project.version}")
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.realyusufismail</groupId>
    <artifactId>yde</artifactId>
    <version>${project.version}</version>
</dependency>
```