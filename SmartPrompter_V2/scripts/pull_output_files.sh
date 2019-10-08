#!/usr/bin/env bash

adb pull storage/self/primary/Documents/sp_alarms
adb shell rm storage/self/primary/Documents/sp_alarms/*

adb pull storage/self/primary/Documents/sp_archive
adb shell rm storage/self/primary/Documents/sp_archive/*

adb pull storage/self/primary/Documents/sp_audio
adb shell rm storage/self/primary/Documents/sp_audio/*

adb pull storage/self/primary/Documents/sp_photos
adb shell rm storage/self/primary/Documents/sp_photos/*


adb pull storage/self/primary/Documents/sp_logs
adb shell rm storage/self/primary/Documents/sp_logs/*