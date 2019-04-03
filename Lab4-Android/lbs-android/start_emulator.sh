#!/bin/sh
#cd ~/Android/Sdk/emulator
cd /mnt/c/Users/denfu/AppData/Local/Android/Sdk/emulator
#cd C:\\Users\\denfu\\\AppData\\\Local\\Android\\Sdk\\emulator

# depends on a virtual device named LBS existing
# configure using the 'Nexus 4' hardware profile
# and Android API 28 target, x86 ABI

./emulator.exe -avd LBS -writable-system &
#.\\emulator.exe -avd LBS -writable-system &

echo "Starting emulator... This may take some time, be patient."
echo "Once the emulated device has booted, run the configure_emulator.sh script to set up the testing apparatus."

#cmd /k
