package com.shipcombat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CannonType
{
    BRONZE(  "Bronze cannon",  59446, 7),
    IRON(    "Iron cannon",    59447, 7),
    STEEL(   "Steel cannon",   59448, 7),
    MITHRIL( "Mithril cannon", 59449, 7),
    ADAMANT( "Adamant cannon", 59450, 7),
    RUNE(    "Rune cannon",    59451, 7),
    DRAGON(  "Dragon cannon",  59452, 7);

    private final String displayName;
    private final int objectId;
    private final int attackSpeedTicks;

    public static boolean isCannonObject(int objectId)
    {
        for (CannonType ct : values())
        {
            if (ct.objectId == objectId) return true;
        }
        return false;
    }

    public static CannonType fromObjectId(int objectId)
    {
        for (CannonType ct : values())
        {
            if (ct.objectId == objectId) return ct;
        }
        return null;
    }
}