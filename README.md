# ♟️ Chess App — CI/CD with Jenkins and Docker

Welcome to the **Chess App** project!  
This repository demonstrates how to automate the build and delivery process of an **Android application** using **Jenkins**, **Gradle**, and **Docker**.  
It showcases a real-world CI/CD (Continuous Integration and Continuous Deployment) pipeline setup for Android app development.

---

## 📁 Project Structure

chess-app/
│
├── Dockerfile # Docker environment setup for CI/CD
├── Jenkinsfile # Jenkins pipeline definition
├── app/
│ ├── build.gradle # Gradle build configuration
│ └── src/
│ └── main/
│ └── java/
│ └── com/example/chessapp/
│ └── MainActivity.kt # Main Android app entry point
└── gradle/
└── wrapper/



---

## 🚀 Overview

This project automates your Android app’s **build, test, and packaging** process.  
Using Jenkins and Docker, it ensures every commit to the main branch triggers a consistent, repeatable build pipeline.

The automation flow includes:
1. **Code Checkout** — Pull latest code from the GitHub repository  
2. **Gradle Build** — Build both Debug and Release APKs  
3. **Artifact Archival** — Store generated APKs as downloadable Jenkins artifacts  
4. **Success/Failure Notifications** — Indicate build result in Jenkins console  

---

## 🧱 CI/CD Pipeline Explanation

The Jenkins pipeline handles the following key stages:

| Stage | Purpose |
|--------|----------|
| **Checkout** | Fetches the latest code from GitHub (`main` branch). |
| **Build Debug APK** | Builds a debug version of the app for testing. |
| **Build Release APK** | Builds a release version for production distribution. |
| **Archive Artifacts** | Saves both APKs to Jenkins for download. |

The pipeline runs automatically when new code is pushed, ensuring the latest version is always available for QA or deployment.

---

## ⚙️ Environment Configuration

To ensure smooth Android builds, the following environment variables are defined inside Jenkins:

| Variable | Description |
|-----------|--------------|
| `JAVA_HOME` | Path to Java 17 installation. |
| `ANDROID_HOME` | Path to Android SDK directory. |
| `PATH` | Includes Android build tools, emulator, and Gradle. |

These paths must match your system setup to avoid build failures.

---

## 🐳 Docker Setup

Docker provides a **consistent and isolated build environment** for Jenkins.  
It ensures that all dependencies — like Java, Android SDK, and Gradle — are installed and configured identically every time.

### Example Dockerfile Summary

- Base Image: **OpenJDK 17**
- Installs: **Git, Gradle, Android SDK Command-Line Tools**
- Sets environment variables for Jenkins to use Android tools
- Defines working directory for builds

### Build and Run Commands

```bash
docker build -t chessapp-jenkins .
docker run -p 8080:8080 chessapp-jenkins


[ Developer ]
     │
     ▼
[ Push Code to GitHub ]
     │
     ▼
[ Jenkins Pipeline Triggered ]
     │
     ├── Checkout Source
     ├── Build Debug APK
     ├── Build Release APK
     └── Archive Artifacts
     │
     ▼
[ APKs Ready for Download in Jenkins ]

🧩 Application Source
Main File: app/src/main/java/com/example/chessapp/MainActivity.kt
This file contains the main activity that launches the Chess App interface.


💡 Developer Notes
Make sure Android SDK and Java 17 paths are correct in Jenkins or Docker.
Set up Jenkins credentials properly for GitHub repository access.
You can extend the pipeline with extra stages such as:
Code Linting
Unit Testing
Automated Deployment to Play Store
For production builds, include a signing configuration using a keystore file.
