# Gun's Skyblock Mod

A lightweight, performance-focused client-side Fabric mod designed to enhance your Hypixel Skyblock experience with dynamic price evaluation tooltips, a sleek, non-intrusive custom scoreboard overlay, and persistent, fail-safe item protection.

---

## How to Use the Mod

This mod runs automatically in the background once installed. Here is how you interact with its core features in-game:

### 1. Custom Scoreboard Overlay
* **What it does:** Replaces the cluttered vanilla sidebar scoreboard with a clean, compact HUD showing essential server data (Purse, Bank, Powder, current Area, and active Objectives). 
* **Activation:** The custom HUD automatically renders on the right side of your screen **only** when you are actively connected to `hypixel.net` and playing the **Skyblock** gamemode.
* **Auto-Hide:** The overlay automatically hides itself when you open the vanilla debug screen (`F3`) to prevent text overlapping.

### 2. Dynamic Price Tooltips
* **Bazaar Pricing:** Hovering over any item compatible with the Bazaar displays its current **Insta-Buy** and **Insta-Sell** values directly inside the item's tooltip.
* **LBIN Pricing:** If an item is not traded on the Bazaar, the tooltip will dynamically fall back to showing its Lowest Bin (**LBIN**) auction house price.
* **Price Prioritization:** For items featuring both data pools, the mod strictly prioritizes **Bazaar prices** over LBIN to give you the most accurate commodity valuation.

### 3. Stack Scaling (Shift Modifier Key)
* **Default View:** By default, tooltips display the individual per-item unit price.
* **Checking Stacks:** Hold down the **[LEFT SHIFT]** or **[RIGHT SHIFT]** key while hovering over a stacked item. The tooltip will instantly multiply the price to reflect the exact value of the entire item stack.

### 4. Persistent Slot Locking (Skyblock Exclusive)
* **Locking a Slot:** Open any container menu or your inventory and hover your mouse over a slot. Press the **[L]** key to lock or unlock that slot. Locked slots will instantly display a distinct locked overlay texture.
* **Drop & Interaction Protection:** While a slot is locked, the mod completely blocks you from dropping the item (**Q** drops), dragging it out of the container, moving it via hotkeys, or losing it if you close an interface while holding the item. 
* **Smart Environment Detection:** Safety features automatically activate **only** when your connection to Hypixel Skyblock is validated, leaving your vanilla survival worlds or other minigames uninhibited.
* **Local Persistence:** Your locked slots are automatically written straight to disk, saving your locked preferences perfectly across game reboots.

---

## Configuration File

Your slot configurations are saved locally in plain text format so they can be remembered every time you launch the game.
* **File Path:** `config/locked_slots.txt` *(located inside your .minecraft folder)*
* **Format Example:**
  ```
  Inventory:0   //Hotbar slot 1
  Inventory:1   //Hotbar slot 2
  Inventory:2   //Hotbar slot 3
  Inventory:3   //Hotbar slot 4
  Inventory:4   //Hotbar slot 5
  Inventory:5   //Hotbar slot 6
  ```
