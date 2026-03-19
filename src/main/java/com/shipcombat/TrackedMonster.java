package com.shipcombat;

import lombok.Getter;
import lombok.Setter;

public class TrackedMonster
{
    @Getter
    private final SeaMonster type;

    @Getter
    @Setter
    private int ticksUntilNextAttack = 0;

    public TrackedMonster(SeaMonster type)
    {
        this.type = type;
    }

    public boolean isAttackImminent()
    {
        return ticksUntilNextAttack <= 1;
    }
}
