# stripped-1.8.9

A stripped, open-source, cleaned-up and up-to-date version of Minecraft 1.8.9 focused on performance, modding, and readability.

## About

This is a deobfuscated and refactored version of Minecraft 1.8.9 with unnecessary client code, telemetry, unused features, and legacy compatibility stripped out. Useful for base client development, modding projects, or studying Minecraft internals.

## Features

- Fully deobfuscated and renamed mappings
- Removed:
    - Realms integration
    - Twitch streaming code
    - Unused assets and legacy rendering junk
    - Unused classes (demo, old menu logic, etc.)
    - In game music (menu, creative mode, etc.)
    - Server HUD restrictions (debug menu shortening)
    - Snooper implementation & code
    - 3D anaglyph, touchscreen & invert mouse
- Improved:
  - Updated all libraries to modern ones(lwjgl, guava, netty, etc.)
- Retains full vanilla functionality (multiplayer, singleplayer, LAN)

## Goals

- Keep vanilla features intact without the clutter
- Stay true to the behavior of Minecraft 1.8.9

## Ideal Use Cases

- Client base for clients, utility mods & more
- Lightweight testing environment
- Debugging, rendering hacks, shader injection, etc.

## How to Build

Make sure you have JDK 21 and Maven installed.

To build the project:

mvn clean install

The compiled JAR will be located in the target/ directory.

## Contributing

Pull requests and issues welcome. If you have suggestions for more cleanup or modernization, open an issue or PR.

## License

Unlicense License. See LICENSE for details.