# Gun's Skyblock Mod

Gun's Skyblock mod is a client-side that adds much needed quality of life features such as item prices on hover, a custom scoreboard that shows bank and powder on top of everything else that is normally shown and slot locking which stays after the game is closed and reopened.

---

### 1. Custom Scoreboard
* **What it does:** Replaces the scoreboard with a cleaner, more colourful scoreboard that shows bank balance as well. It also shows all 3 of the mining powders when in a mining area (where powder is obtainable) 
* **Activation:** To make sure it doesn't get in the way of playing on other servers it **only** shows when you are on hypixel.net and playing **Skyblock**.
* **Debug:** Tapping `F3` both opens the debug screen and now hides the custom scoreboard so that you can see if something is missing from the custom scoreboard.

### 2. Item Prices
* **Bazaar Prices:** Hovering over an item that is on the bazaar shows it's **Insta buy** and **Insta sell** prices inside the tooltip. These prices are at most a few minutes old as the mod fetches the prices every few minutes
* **Auction Prices:** Hovering over an items that is **only** on the auction house gives the lbin (lowest buy it now) price. Like the bazaar these prices are also at most a few minutes old
* **Stack Prices** When holding shift (not crouch but shift on your keyboard) the price shows the price of all the items in that slot (e.g: 1 item could cost 5k whereas while holding shift ti would show 36 (or how many items are in that slot) would cost 180k)

### 3. Slot Locking
* **How to Lock a Slot:** Hovering over an item and tapping `L` on your keyboard locks that slot. 
* **What this does:** If a slot is locked it means it can't be moved (apart from hovering over it and tapping a hotbar slot number) or dropped.
* **Activation** To make sure it doesn't get in the way of playing on other servers it **only** shows when you are on hypixel.net and playing **Skyblock** ..
* **Saved Slots:** The locked slots are written to a file when they are locked, deleted when unlocked and read when the game boots. Since this file doesn't need minecraft to be running to store data it means that the mod remembers which slots are locked (and locks them) no matter how times you open and close the game. This comes at the low price of under a kilobyte to store these locked slots. 

### 4. Custom Enderchest UI

* **3 page enderchest** When you run `/ec` the mod automatically goes through the first 3 enderchest pages and creates a custom 3 page UI
* **Completely Allowed** The mod doesn't let you take the items from the page in one click. You click on the page to make it active then click again to get your item. This means that it is purely visual and doesn't actuallly help you. Therefore it is allowed on Hypixel

---

## Requirements

- Minecraft Java Edition 26.2
- Fabric API 0.154.0+26.2
- Loom 1.17
- Fabric Loader
- Java 21