# 2048 GAME

A simple graphical version of the classic **2048 game** built in Java 8.0 using Gradle as the build system.
Inspiration taken from _https://play2048.co/_

## 🧱 Project Structure

- **Language:** Java
- **Build Tool:** Gradle
- **Dependencies:**
  - `org.processing:core:3.3.7`
  - 
## 📁 Directory Structure
```
2048-Game
|--src
|    |--main
|          |--java
|                 |TwentyFortyEight
|                                 |--App.java
|                                 |--Cell.java
|--build.gradle
```

## ✅ Prerequisites

- Java JDK 8 or higher
- Gradle 5.6.3 or higher (or use the Gradle Wrapper)

## 🔧 Build and Run the Project
Insert commands from root directory
```
gradle build
gradle run #(4x4 grid by default)
gradle run --args="n" #(nxn grid is generated)
```

## 🛠 Main Class
The entry point of the application is:
```
TwentyFortyEight.App
```
## 🎮 Game Controls

Use the following keyboard keys to play the game:

- **UP** – Move **Up**
- **DOWN** – Move **Down**
- **LEFT** – Move **Left**
- **RIGHT** – Move **Right**
- **R** – Restart the game after game over
