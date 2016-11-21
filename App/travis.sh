android list targets
echo no | android create avd --force -n test -t android-$ANDROID_API_LEVEL --abi $ANDROID_ABI
emulator -avd test -no-skin -no-audio -no-window &
android-wait-for-emulator
adb shell input keyevent 82 &
./gradlew build connectedCheck