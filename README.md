# DarkBot Reborn

This project is a partial re-write of [DarkBot](https://github.com/DarkStorm652/DarkBot) by *DarkStorm652*. It is updated for Minecraft 1.12, easily migratable to further patches, and features an all-new XRay Mining Task.

## What's new

#### 1.12 support! (aka MCProtocolLib)
This library replaces the protocol implementations in DarkBot. It is highly maintained, very stable and already contains much functionality that DarkBot requires. Migrating to it means more stability, simpler bot code, and of course fast, easy updates to new protocol versions.

#### XRay Mining Task
This implementation is very different from the original Mining Task. The bot will find the closest diamond block and attempt to reach it by digging straight tunnels and stair tunnels up and down. 

The core movements and actions are expressed in matrices, which are relative coordinates from the current player position. The task detects the direction in the world which will get it closer to the target block and then transforms the matrices, producing exact world positions. This technique allows for much easier and cleaner logic.

#### A lot of misc changes and bug fixes
Classes representing objects from the protocol have been replaced with ones from MCProtocolLib where possible because the latter are more up-to-date and mean less code to maintain.

## Removed functionality
* All protocols in favor of MCProtocolLib implementation
* ChestInventory in favor of a generic (but stripped down) GenericInventory
* GUI
* SpamBot
* Various CLIBotWrapper functionality (e.g., account lists, random usernames, proxies)
* Chop Trees Task (causes fatal bugs)

## Notes on building
Make sure to use [my forked MCProtocolLib](https://github.com/ViRb3/MCProtocolLib/tree/1.12-1-hotfix) as it is reverted for Minecraft 1.12 and contains cherry picks that fix fatal errors.

## Further work
Some more work is required to make the bot stable and truly usable in multiplayer. Below are some points that I have noticed during my development:
* Player movement, collision and stair/slab/etc. climbing
* Inventory handling
* Some of the protocol's events and packets are untested and might not work
* Server Map Data Packet handling
* Server chunk NBT metadata and biome data
* The XRay Mining Task contains a lot of 'safety measures' that will blacklist the current target block and search for a new one. For example, unpluggable lava and bedrock. It is always better to handle these situations instead of evading them.
*  The XRay Mining Task (maybe other actions as well) try to break blocks without having direct vision on them. This works great in vanilla servers but is prevented in most others, especially with Anti-Cheats installed.

Make sure to check the code for `//TODO` comments.

---

Greatest thanks to *DarkStorm652* and *Steveice10* for [DarkBot](https://github.com/DarkStorm652/DarkBot) and [MCProtocolLib](https://github.com/Steveice10/MCProtocolLib), respectively. This update would not have been possible without either of their projects.

Finally, feel free to contribute back to this project. I will not have much time to do so myself, but I will appreciate anything that improves its usability.
