#!/bin/sh
#cd ~/Android/Sdk/platform-tools
cd /mnt/c/Users/denfu/AppData/Local/Android/Sdk/platform-tools
#cd C:\\Users\\denfu\\\AppData\\\Local\\Android\\Sdk\\platform-tools

./adb.exe root
./adb.exe remount
#.\\adb.exe root
#.\\adb.exe remount

wget https://busybox.net/downloads/binaries/1.27.1-i686/busybox
./adb.exe push busybox /data/data/busybox
./adb.exe shell "mv /data/data/busybox /system/bin/busybox && chmod 755 /system/bin/busybox && /system/bin/busybox --install /system/bin"
#.\\adb.exe push busybox /data/data/busybox
#.\\adb.exe shell "mv /data/data/busybox /system/bin/busybox && chmod 755 /system/bin/busybox && /system/bin/busybox --install /system/bin"

rm busybox

echo "The testing apparatus has been set up."
echo "Install the completed MACLocation application."
echo "Then run test_emulator.sh to test the application."

#cmd /k