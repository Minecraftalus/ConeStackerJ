# ConeStackerJ

## How to play
1) Download and run the appropriate installer for your system
    * Windows - ConeStackerJ-version.msi
    * Linux - ConeStackerJ_version.deb / ConeStackerJ_version.rpm
    * MacOS - ConeStackerJ-version.dmg
2) Install and launch the application

## Building from source
1) Install [Java JDK 21+](https://adoptium.net/temurin/releases?version=21&os=any&arch=any)
2) Clone this repo using ```git clone https://github.com/Minecraftalus/ConeStackerJ.git```
3) Run ```cd ConeStackerJ```
4) Choose build type
    * Full installer
        * Install necessary build tools
            * Windows - [Wix Toolset](https://github.com/wixtoolset/wix/releases)
            * Linux - Run ```sudo dnf install rpm-build``` or ```sudo apt-get install rpm```
            * MacOS - Run ```xcode-select --install```
        * Run ```./gradlew jpackage```
        * The installer will be located in ConeStackerJ/build/jpackage
    * Simple build
        * Run ```./gradlew build```
        * The jarfile will be located in ConeStackerJ/build/libs
        * Run ```java -jar ./build/libs/ConeStackerJ-all.jar```

## Info
ConeStackerJ is a fangame of [Cone Stacker](https://github.com/gavinskycastle/ConeStacker), but written in Java