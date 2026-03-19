package com.shipcombat;

import lombok.Getter;
import net.runelite.api.NPC;

public class TrackedCorpse
{
    static final int DESPAWN_TICKS = 200;

    @Getter
    private final NPC npc;

    @Getter
    private final String monsterName;

    @Getter
    private int ticksRemaining;

    public TrackedCorpse(NPC npc, String monsterName)
    {
        this.npc          = npc;
        this.monsterName  = monsterName;
        this.ticksRemaining = DESPAWN_TICKS;
    }

    public boolean tick()
    {
        if (ticksRemaining > 0)
        {
            ticksRemaining--;
        }
        return ticksRemaining > 0;
    }

    public int getSecondsRemaining()
    {
        return (int) Math.ceil(ticksRemaining * 0.6);
    }
}