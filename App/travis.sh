set -e
chmod 777 ./gradlew
./gradlew assemble
echo no | android create avd --force -n test -t android-$ANDROID_EMULATOR_API_LEVEL --abi google_apis/armeabi-v7a
emulator -avd test -no-skin -no-audio -no-window -wipe-data &
android-wait-for-emulator
adb shell input keyevent 82 &
./gradlew connectedCheck
