package com.shipcombat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeaMonster
{
    // BIRDS (Range 1, Speed 4)
    TERN(         "Tern",          new int[]{ 15228 }, 13237, 1, 4),
    OSPREY(       "Osprey",        new int[]{ 15222 }, -1,    1, 4),
    FRIGATEBIRD(  "Frigatebird",   new int[]{ 15226 }, -1,    1, 4),
    ALBATROSS(    "Albatross",     new int[]{ 15224 }, -1,    1, 4),

    // RAYS (Range 1, Speed 4)
    EAGLE_RAY(    "Eagle ray",     new int[]{ 15214 }, -1,    1, 4),
    BUTTERFLY_RAY("Butterfly ray", new int[]{ 15216 }, -1,    1, 4),
    STINGRAY(     "Stingray",      new int[]{ 15218 }, -1,    1, 4),
    MANTA_RAY(    "Manta ray",     new int[]{ 15220 }, -1,    1, 4),

    // MAMMALS/MOGRES
    DOLPHIN(      "Dolphin",       new int[]{ 15234 },         -1, 1, 4),
    MOGRE(        "Mogre",         new int[]{ 15230, 15232 },  -1, 1, 6),
    NARWHAL(      "Narwhal",       new int[]{ 15202 },         -1, 1, 4),
    ORCA(         "Orca",          new int[]{ 15204 },         -1, 1, 4),

    // SHARKS (Range 1, Speed 4)
    BULL_SHARK(   "Bull shark",    new int[]{ 15194 }, -1,    1, 4),
    HAMMERHEAD(   "Hammerhead",    new int[]{ 15196 }, -1,    1, 4),
    TIGER_SHARK(  "Tiger shark",   new int[]{ 15198 }, -1,    1, 4),
    GREAT_WHITE(  "Great white",   new int[]{ 15200 }, -1,    1, 4),

    // KRAKENS (Range 1, Speed 5)
    PYGMY_KRAKEN(   "Pygmy kraken",    new int[]{ 15206 }, -1, 1, 5),
    SPINED_KRAKEN(  "Spined kraken",   new int[]{ 15208 }, -1, 1, 5),
    ARMOURED_KRAKEN("Armoured kraken", new int[]{ 15210 }, -1, 1, 5),
    VEILED_KRAKEN(  "Veiled kraken",   new int[]{ 15576 }, -1, 1, 5),
    VAMPYRE_KRAKEN( "Vampyre kraken",  new int[]{ 15212 }, -1, 1, 5);

    private final String displayName;
    private final int[] npcIds;
    private final int attackAnimId;
    private final int attackRangeTiles;
    private final int attackSpeedTicks;

    public static SeaMonster fromCorpseId(int npcId)
    {
        // Check if the NPC ID - 1 matches an alive ID
        return fromNpcId(npcId - 1);
    }

    public static SeaMonster fromNpcId(int npcId)
    {
        for (SeaMonster m : values())
        {
            for (int id : m.npcIds)
            {
                if (id == npcId) return m;
            }
        }
        return null;
    }

    public boolean isAttackAnimation(int animId)
    {
        return animId != -1 && this.attackAnimId == animId;
    }
}