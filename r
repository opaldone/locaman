#!/bin/bash

cd $(dirname "${BASH_SOURCE[0]}")

declare pro=$("pwd")
declare aa="/mnt/terik/a_my/java/android_sdk/platform-tools/adb"
declare app="click.opaldone.locaman"

declare par=$1

if [[ ! $par ]]; then
    clear
    # $aa logcat -v tag
    $aa logcat -v tag \
        *:S  \
        WM-SystemFgDispatcher:I \
        WM-SystemFgDispatcher:W \
        WM-SystemFgDispatcher:E \
        NotificationService:E \
        AndroidRuntime:W \
        AndroidRuntime:E \
        ActivityManager:W \
        ActivityManager:E \
        some:D

        # libPowerHal:I \

    exit 0
fi

if [[ $par == "j" ]]; then
    clear
    $aa logcat -b all -c
    exit 0
fi

if [[ $par == "bd" ]]; then
    cd "$pro"

    declare err=$(./gradlew assembleDebug 2>&1>/dev/null)

    if [[ ! -z $err ]]; then
        echo "$err"
        printf '─%.s' $(seq 50)
        echo
        exit 0
    fi

    echo "OK"
    printf '─%.s' $(seq 50)
    echo
    exit 0
fi

if [[ $par == "in" ]]; then
    declare apk="$pro/app/build/outputs/apk/debug/app-debug.apk"
    declare act="$app/$app.MainActivity"

    # $aa -s "59TS7TW49XDQ8TEY" install $apk
    # $aa -s "59TS7TW49XDQ8TEY" shell am start -n $act -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

    $aa install $apk
    $aa shell am start -n $act -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
    exit 0
fi

if [[ $par == "st" ]]; then
    $aa shell am force-stop $app
    exit 0
fi

if [[ $par == "re" ]]; then
    $aa shell pm uninstall --user 0 $app
    exit 0
fi

if [[ $par ]]; then
    echo -e "The parameter \"$par\" is wrong"
    exit -1
fi

