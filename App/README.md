# Aken Ajalukku - Android application
The Android application is the end user facing application. The purpose of the Android application is to allow the user
to see points of interest (PoIs) on a map, navigate to them and view more details about them when close enough to their location.

It should communicate to the backend CMS through an API in order to get a list of PoIs. This allows the administrators
to change the available PoIs without having to update the application itself.

# Building
For building, we use Gradle. It is highly recommended to import this project into [Android Studio](https://developer.android.com/studio/index.html) and let it handle the building. Start with getting the gradle wrapper using `gradle wrapper`.

To build the debug version, use
```
gradlew assembleDebug
```
Release version can be built using `gradlew assembleRelease`, but it is automatically signed and we will not provide the keystore file or the passwords to do it.

# License
This project is released under the GNU GPLv3 license, for more information see `LICENSE.txt`
