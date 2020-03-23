#! /bin/bash

# clean
rm -f ./src/*.class
rm -f *.jar

# build .class files
javac ./src/*.java

# compile .jar file
jar cfm spDesktop.jar META-INF/MANIFEST.MF ./src/*.class

# make desktop / ADB runnables executable
chmod +x spDesktop.jar
chmod +x platform-tools-mac/adb
chmod +x platform-tools-windows/adb.exe

# run
java -jar spDesktop.jar
