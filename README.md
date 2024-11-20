# HungTeen's Lib (HTLib) [![](https://cf.way2muchnoise.eu/full_794622_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/hungteens-lib)

[![](https://cf.way2muchnoise.eu/versions/794622.svg)](https://www.curseforge.com/minecraft/mc-mods/hungteens-lib)

A lib made by HungTeen for more easily developing minecraft mod.

## Features

* **Multiplatform**: HTLib support Forge, Fabric, NeoForge since 1.21.1+.
* **Registry System**: there are more flexible registries like Simple, Codec, Vanilla, Custom.
* **Resource Helper**: provide a bunch of resource/registry helper for mod development.
* **Data Pack**: provide many codec utils for data pack development.

### Neoforge Only

* **Block Suit**: register a series of wood relate registries with simple code, including boat, sign, etc.
* **Entity Suit**: register a series of entity relate registries with simple code, including type, attribute, etc.

## Warning

* Since 1.21.1, HTLib will support Forge, Fabric, NeoForge.
* Since 1.21.1, the custom raid system has been split into a new mod
  called [Custom Raid](https://github.com/HungTeen/Custom-Raid).

## Current Progress

| Version |   Status   | Stable | Latest |
|:-------:|:----------:|:------:|:------:|
| 1.19.2  |  Stopped   | 0.9.1  | 0.9.1  |
| 1.19.3  |  Stopped   | 0.9.2  | 0.9.2  |
| 1.19.4  |  Stopped   | 0.9.3  | 0.9.3  |
| 1.20.1  |  Stopped   | 1.0.2  | 1.0.2  |
| 1.21.1  | Developing | 1.1.0  | 1.1.1  |


## Getting Started
Visit [CurseMaven](https://www.cursemaven.com) to find more information about how to set up your workspace.
```groovy
repositories {
    maven {
        url "https://www.cursemaven.com"
        content {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    // visit https://www.curseforge.com/minecraft/mc-mods/hungteens-lib/files/all?page=1&pageSize=20 to get the latest version's htlib file id.
    // For Fabric.
    modImplementation "curse.maven:htlib-794622:${htlib_id}"
    // For Neoforge.
    implementation "curse.maven:htlib-794622:${htlib_id}"
    // For Forge.
    implementation fg.deobf("curse.maven:htlib-794622:${htlib_id}")
}
```