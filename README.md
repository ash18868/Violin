# 🎮 Violin — A Minecraft Forge Mod

A custom **Minecraft Forge mod** developed in Java, showcasing the creation of new entities, behaviors, and custom audio. The centerpiece of this project is the addition of **Herobrine** as a hostile mob, along with **unique music discs** that drop when he is defeated.

This project demonstrates my ability to:

* Work with complex APIs (Minecraft Forge)
* Implement custom entities with AI and rendering
* Integrate custom sound assets
* Structure and maintain a Gradle‑based Java project

---

## 🔎 Overview

* **Entity Added:** Herobrine — a custom hostile mob with unique AI behaviors
* **Drops:** Rare, custom **music discs** that integrate with the Minecraft sound system
* **Focus:** Showcases Java OOP, Forge event handling, model/render systems, and asset pipelines

---

## ✨ Feature Highlights

* **Custom Entity:** Herobrine with unique model, renderer, and AI logic
* **Loot System:** Custom music discs as rare drops
* **Sound Integration:** Music discs tied to registered custom sound events
* **Gradle Project Setup:** Fully buildable with Gradle wrapper, no global install required

---

## 🧱 Tech Stack

* **Language:** Java (17+)
* **Framework:** Minecraft Forge
* **Build Tool:** Gradle (wrapper included)
* **Assets:** Custom textures, models, and sound files

---

## 🧑‍💼 For Reviewers (What this demonstrates)

* Ability to extend a large, complex codebase (Minecraft + Forge)
* Competence with **Java OOP** and event‑driven programming
* Integration of **custom assets** (models, textures, sounds)
* Delivering a **polished mod** that runs end‑to‑end

---

## 🗂️ Project Structure

```
Violin/
 ┣ gradle/                # Gradle wrapper and build scripts
 ┣ src/main/java/net/leonard/violin/
 ┃ ┣ entity/              # Custom entities (Herobrine, etc.)
 ┃ ┣ item/                # Custom items (music discs)
 ┃ ┣ sound/               # Sound registration
 ┃ ┣ event/               # Event handling for behaviors
 ┃ ┗ Violin.java          # Main mod class
 ┣ build.gradle           # Build configuration
 ┣ settings.gradle
 ┗ violin.png             # Project logo/preview
```

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Minecraft Forge MDK (matching version)
* Gradle (wrapper included)

### Clone & Build

```bash
git clone https://github.com/ash18868/Violin.git
cd Violin
./gradlew clean build   # Windows: gradlew.bat clean build
```

### Install in Minecraft

1. Locate your Minecraft `mods/` folder
2. Copy the generated `.jar` from `build/libs/`
3. Launch Minecraft with the matching Forge profile

---

## 📚 Learning Notes

* Implemented custom AI behaviors and renderers
* Registered custom sounds and items within the Forge event system
* Structured Gradle builds for reproducible mod packaging

---

## 👋 Contact

**Leonard Weaver**

**LinkedIn:** [linkedin.com/in/leonard-weaver-9083472a5](https://www.linkedin.com/in/leonard-weaver-9083472a5)

**Email:** leonard.weaver45@gmail.com
