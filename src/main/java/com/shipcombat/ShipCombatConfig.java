package com.shipcombat;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("shipcombat")
public interface ShipCombatConfig extends Config
{
    @ConfigSection(name = "Cannon",         description = "Cannon firing arc and tick counter",             position = 0)
    String sectionCannon = "cannon";

    @ConfigSection(name = "Ship",           description = "Ship hull highlight and rotation sweep",         position = 1)
    String sectionShip = "ship";

    @ConfigSection(name = "Monster Ranges", description = "Attack range indicators for sea monsters",       position = 2)
    String sectionMonsters = "monsters";

    @ConfigSection(name = "Corpse Timer",   description = "Countdown until sea monster corpses despawn",     position = 3)
    String sectionCorpse = "corpse";

    // ---- Cannon range arc -----------------------------------------------

    @ConfigItem(keyName = "showCannonRange", name = "Show cannon firing arc",
            description = "Highlight the 180° firing zone. This arc is anchored to your cannon facility and rotates with the ship.",
            section = sectionCannon, position = 0)
    default boolean showCannonRange() { return true; }

    @Range(min = 1, max = 30)
    @ConfigItem(keyName = "cannonRangeTiles", name = "Cannon range (tiles)",
            description = "The horizontal distance your cannonballs travel. 13 tiles is recommended for most tier cannons.",
            section = sectionCannon, position = 1)
    default int cannonRangeTiles() { return 13; }

    @Alpha
    @ConfigItem(keyName = "cannonRangeFillColor", name = "Arc fill color",
            description = "Color of the interior of the firing arc.",
            section = sectionCannon, position = 2)
    default Color cannonRangeFillColor() { return new Color(255, 140, 0, 40); }

    @Alpha
    @ConfigItem(keyName = "cannonRangeColor", name = "Arc border color",
            description = "Color of the arc's outer edge.",
            section = sectionCannon, position = 3)
    default Color cannonRangeColor() { return new Color(255, 140, 0, 200); }

    @ConfigItem(keyName = "onlyShowArcWhenManning", name = "Only show arc when manning",
            description = "Hide the firing arc unless you are standing at the cannon facility.",
            section = sectionCannon, position = 4)
    default boolean onlyShowArcWhenManning() { return false; }

    // ---- Cannon tick counter --------------------------------------------

    @ConfigItem(keyName = "showCannonTick", name = "Show cannon tick counter",
            description = "Show the number of ticks remaining until the cannon can fire again, above your character.",
            section = sectionCannon, position = 5)
    default boolean showCannonTick() { return true; }

    @Alpha
    @ConfigItem(keyName = "cannonCooldownColor", name = "Tick counter color",
            description = "Color of the cooldown text shown above your character.",
            section = sectionCannon, position = 6)
    default Color cannonCooldownColor() { return new Color(255, 80, 0); }

    // ---- Ship highlights --------------------------------------------

    @ConfigItem(keyName = "showShipTiles", name = "Highlight current hull",
            description = "Highlight the specific tiles your ship occupies right now. Monsters touching these tiles can attack immediately.",
            section = sectionShip, position = 0)
    default boolean showShipTiles() { return true; }

    @Alpha
    @ConfigItem(keyName = "shipTileColor", name = "Ship tile color",
            description = "Color used to highlight your current ship hull footprint.",
            section = sectionShip, position = 1)
    default Color shipTileColor() { return new Color(80, 180, 255, 60); }

    @ConfigItem(keyName = "showShipSweep", name = "Show rotation sweep (Daisy)",
            description = "Show the 'Daisy' pattern representing the total area your ship could occupy if it rotated. Monsters inside this zone are potential threats.",
            section = sectionShip, position = 2)
    default boolean showShipSweep() { return true; }

    @Alpha
    @ConfigItem(keyName = "shipSweepFillColor", name = "Sweep fill color",
            description = "Color of the interior of the rotation sweep area.",
            section = sectionShip, position = 3)
    default Color shipSweepFillColor() { return new Color(0, 255, 255, 15); }

    @Alpha
    @ConfigItem(keyName = "shipSweepBorderColor", name = "Sweep border color",
            description = "Color of the outline of the rotation sweep area.",
            section = sectionShip, position = 4)
    default Color shipSweepBorderColor() { return new Color(0, 255, 255, 80); }

    // ---- Monster ranges -------------------------------------------------

    @ConfigItem(keyName = "showMonsterRanges", name = "Show monster attack ranges",
            description = "Draw attack range rings around sea monsters. Green = Safe, Yellow = Inside Sweep, Red = Touching Hull.",
            section = sectionMonsters, position = 0)
    default boolean showMonsterRanges() { return true; }

    @Alpha
    @ConfigItem(keyName = "rangeColorSafe", name = "Safe color",
            description = "Ring color when the monster is completely outside your ship's rotation sweep.",
            section = sectionMonsters, position = 1)
    default Color rangeColorSafe() { return new Color(0, 220, 80, 160); }

    @Alpha
    @ConfigItem(keyName = "rangeColorDanger", name = "Danger color (Inside Sweep)",
            description = "Ring color when the monster enters your 'Daisy' sweep. It can hit you if you rotate toward it.",
            section = sectionMonsters, position = 2)
    default Color rangeColorDanger() { return new Color(255, 200, 0, 220); }

    @Alpha
    @ConfigItem(keyName = "rangeColorImminent", name = "Immediate color (Touching Hull)",
            description = "Ring color when the monster is close enough to bite your current hull and its attack is ready.",
            section = sectionMonsters, position = 3)
    default Color rangeColorImminent() { return new Color(255, 30, 30, 180); }

    @ConfigItem(keyName = "showMonsterTicks", name = "Show monster attack ticks",
            description = "Show a countdown above each monster's head until its next attack.",
            section = sectionMonsters, position = 4)
    default boolean showMonsterTicks() { return true; }

    @ConfigItem(keyName = "highlightMonsterTiles", name = "Highlight monster tiles",
            description = "Draw a filled tile highlight under each sea monster using its current threat color.",
            section = sectionMonsters, position = 5)
    default boolean highlightMonsterTiles() { return true; }

    // ---- Corpse despawn timer -------------------------------------------

    @ConfigItem(keyName = "showCorpseTimer", name = "Show corpse despawn timer",
            description = "Show a countdown above monster corpses. All sea corpses despawn after 200 ticks (2 minutes).",
            section = sectionCorpse, position = 0)
    default boolean showCorpseTimer() { return true; }

    @ConfigItem(keyName = "corpseTimerInSeconds", name = "Show time in seconds",
            description = "Toggle between showing game ticks (0.6s) or rounded seconds.",
            section = sectionCorpse, position = 1)
    default boolean corpseTimerInSeconds() { return true; }

    @Alpha
    @ConfigItem(keyName = "corpseTimerColor", name = "Timer color",
            description = "Color of the despawn countdown text.",
            section = sectionCorpse, position = 2)
    default Color corpseTimerColor() { return new Color(255, 255, 255, 255); }

}