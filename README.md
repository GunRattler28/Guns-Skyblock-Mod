# Hypixel Skyblock Price & HUD Mod

A lightweight, performance-focused client-side Fabric mod designed to enhance your Hypixel Skyblock experience with dynamic price evaluation tooltips and a sleek, non-intrusive custom scoreboard overlay.

---

## 🛠 How to Use the Mod

This mod runs automatically in the background once installed. Here is how you interact with its core features in-game:

### 1. Custom Scoreboard Overlay
* **What it does:** Replaces the cluttered vanilla sidebar scoreboard with a clean, compact HUD showing essential server data (Purse, Bank, Powder, current Area, and active Objectives). 
* **Activation:** The custom HUD automatically renders on the right side of your screen **only** when you are actively connected to `hypixel.net` and playing the **Skyblock** gamemode.
* **Auto-Hide:** The overlay automatically hides itself when you open the vanilla debug screen (**F3**) to prevent text overlapping.

### 2. Dynamic Price Tooltips
* **Bazaar Pricing:** Hovering over any item compatible with the Bazaar displays its current **Insta-Buy** and **Insta-Sell** values directly inside the item's tooltip.
* **LBIN Pricing:** If an item is not traded on the Bazaar, the tooltip will dynamically fall back to showing its Lowest Bin (**LBIN**) auction house price.
* **Price Prioritization:** For items featuring both data pools, the mod strictly prioritizes **Bazaar prices** over LBIN to give you the most accurate commodity valuation.

### 3. Stack Scaling (Shift Modifier Key)
* **Default View:** By default, tooltips display the individual per-item unit price.
* **Checking Stacks:** Hold down the **[LEFT SHIFT]** or **[RIGHT SHIFT]** key while hovering over a stacked item. The tooltip will instantly multiply the price to reflect the exact value of the entire item stack.

---

## 📦 Installation & Requirements

1. Ensure you are running the modern **Fabric Loader** for Minecraft 26.2.
2. Drop the compiled `.jar` file along with the required **Fabric API** dependency into your `.minecraft/mods` folder.
3. Launch the game and connect to Hypixel!

- Minecraft Java Edition Verision 26.2
- Fabric Loader
- Fabric API 0.154.0+26.2
- JAVA 21+

## 🔗 Downloads & Live Project
The official production builds, updates, and documentation are hosted transparently on our project dashboard:
* **Download via Modrinth:** [[https://modrinth.com/mod/guns-skyblock-mod](https://modrinth.com/mod/guns-skyblock-mod))]
