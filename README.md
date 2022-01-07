# Android-WhatToEat
Refactor the university project.


## Environment
* MacOS 11.4
* Android Studio Arctic Fox | 2020.3.1 Patch 2
* Android Gradle Plugin 7.0.2
* Gradle 7.0.2
* JDK 11.0.13


## Configurations
create the `local.properties` file, it can include various secret parameters.
```java-properties
## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
#Mon Jun 21 13:07:38 CST 2021
sdk.dir=YOUR_ANDROID_SDK_PATH

URL=YOUR_URL
MAPS_API_KEY=YOUR_MAPS_API_KEY
```

To use Google Services Gradle Plugin, need `google-services.json`, follow the [Add Firebase to your Android project](https://firebase.google.com/docs/android/setup)。


## Reports
* [Build Scan](https://scans.gradle.com/s/m6h5xzgbec2du/console-log?anchor=254)
* [Lint](https://htmlpreview.github.io/?https://github.com/henryhuang1219/Android-WhatToEat/blob/master/app/reports/lint-results-debug.html)
* [Analytics](https://analytics.google.com/analytics/web/?authuser=0#/p289131996/reports/dashboard?r=firebase-overview)