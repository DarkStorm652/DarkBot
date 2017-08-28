# DarkBot Reborn

This project is a partial re-write of [DarkBot](https://github.com/DarkStorm652/DarkBot) by *DarkStorm652*. It is updated for Minecraft 1.12, easily migratable to further patches, and features an all-new XRay Mining Task.

## What's new

#### 1.12 support! (aka MCProtocolLib)
This library replaces the protocol implementations in DarkBot. It is highly maintained, very stable and already contains a lot of functionality that DarkBot requires. Migrating to it means more stability, simpler bot code, and of course fast, easy updates to new protocol versions.

#### XRay Mining Task
This implementation is very different from the original Mining Task. The bot will find the closest diamond block and attempt to reach it by digging straight tunnels and stair tunnels up and down. 

The core movements and actions are expressed in matrices, which are relative coordinates from the current player position. The task detects the direction in the world which will get it closer to the target block and then transforms the matrices, producing exact world positions. This technique allows for much easier and cleaner logic.

#### A lot of misc changes and bug fixes
Classes representing objects from the protocol have been replaced with ones from MCProtocolLib where possible because the latter are more up-to-date and mean less code to maintain.

## Removed functionality:
* All protocols in favor of MCProtocolLib implementation.
* GUI
* SpamBot
* Various CLIBotWrapper functionality (e.g., account lists, random usernames, proxies)
* Chop Trees Task (causes fatal bugs)

## Further work
Some more work is required to make the bot stable and truly usable in multiplayer. Below are some points that I have noticed during my development:
* Player movement, collision and stair/slab/etc. climbing
* Inventory handling
* Some of the protocol's events and packets are untested and might not work
* Server Map Data Packet handling
* Server chunk NBT metadata and biome data

Make sure to check the code for `//TODO` comments.

---

Greatest thanks to *DarkStorm652* and *Steveice10* for [DarkBot](https://github.com/DarkStorm652/DarkBot) and [MCProtocolLib](https://github.com/Steveice10/MCProtocolLib), respectively. This update wouldn't have been possible without either of their projects.

Finally, feel free to contribute back to this project. I won't have much time to do so myself, but I'll appreciate anything that improves its usability.
