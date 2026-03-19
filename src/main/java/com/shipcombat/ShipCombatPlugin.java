package com.shipcombat;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WorldEntityDespawned;
import net.runelite.api.events.WorldEntitySpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = "Ship Combat",
        description = "Sailing ship combat helper: cannon arc, overhead tick counter, and monster attack range indicators",
        tags = {"sailing", "ship", "combat", "cannon", "sea", "monster", "kraken", "timer", "range"}
)
public class ShipCombatPlugin extends Plugin
{
    static final int BOAT_WORLD_ENTITY_CONFIG_ID = 2;
    private static final int CANNON_FIRE_GRAPHICS_ID = 3538;
    private static final int CANNON_ANIM_OPERATING = 13323;
    private static final int CANNON_ANIM_READY     = 13324;

    @Inject private Client client;
    @Inject private ShipCombatOverlay overlay;
    @Inject private ShipBoundariesOverlay boundariesOverlay;
    @Inject private OverlayManager overlayManager;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private final Set<WorldEntity> activeBoats = new HashSet<>();
    private final List<GameObject> allTrackedCannons = new ArrayList<>();

    @Getter
    private final Map<NPC, TrackedMonster> trackedMonsters = new LinkedHashMap<>();

    @Getter
    private final Map<NPC, TrackedCorpse> trackedCorpses = new LinkedHashMap<>();

    @Getter
    private int cannonTicksRemaining = 0;

    @Getter
    private boolean playerAtCannon = false;

    // -----------------------------------------------------------------------
    // Entry Point
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        net.runelite.client.externalplugins.ExternalPluginManager.loadBuiltin(ShipCombatPlugin.class);
        net.runelite.client.RuneLite.main(args);
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        overlayManager.add(boundariesOverlay);
        log.info("Ship Combat started.");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        overlayManager.remove(boundariesOverlay);
        resetState();
        log.info("Ship Combat stopped.");
    }

    private void resetState()
    {
        activeBoats.clear();
        allTrackedCannons.clear();
        trackedMonsters.clear();
        trackedCorpses.clear();
        cannonTicksRemaining = 0;
        playerAtCannon = false;
    }

    // -----------------------------------------------------------------------
    // Logic Helpers
    // -----------------------------------------------------------------------

    public WorldEntity getBoatWorldEntity()
    {
        Player local = client.getLocalPlayer();
        if (local == null) return null;

        WorldView playerWv = local.getWorldView();
        if (playerWv == client.getTopLevelWorldView()) return null;

        for (WorldEntity we : activeBoats)
        {
            if (we.getWorldView() == playerWv) return we;
        }
        return null;
    }

    public List<GameObject> getTrackedCannons()
    {
        Player local = client.getLocalPlayer();
        if (local == null) return new ArrayList<>();

        WorldView playerWv = local.getWorldView();
        List<GameObject> active = new ArrayList<>();

        for (GameObject obj : allTrackedCannons)
        {
            if (obj.getWorldView() == playerWv) active.add(obj);
        }
        return active;
    }

    public CannonType getActiveCannonType()
    {
        List<GameObject> cannons = getTrackedCannons();
        if (!cannons.isEmpty())
        {
            return CannonType.fromObjectId(cannons.get(0).getId());
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Events
    // -----------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.HOPPING || event.getGameState() == GameState.LOGIN_SCREEN)
        {
            resetState();
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onWorldEntitySpawned(WorldEntitySpawned event)
    {
        WorldEntity we = event.getWorldEntity();
        if (we.getConfig().getId() == BOAT_WORLD_ENTITY_CONFIG_ID)
        {
            activeBoats.add(we);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onWorldEntityDespawned(WorldEntityDespawned event)
    {
        activeBoats.remove(event.getWorldEntity());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject obj = event.getGameObject();
        if (CannonType.isCannonObject(obj.getId()))
        {
            allTrackedCannons.add(obj);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject obj = event.getGameObject();
        if (CannonType.isCannonObject(obj.getId()))
        {
            allTrackedCannons.remove(obj);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNpcSpawned(NpcSpawned event)
    {
        NPC npc = event.getNpc();
        SeaMonster type = SeaMonster.fromNpcId(npc.getId());
        if (type != null)
        {
            trackedMonsters.put(npc, new TrackedMonster(type));
            return;
        }

        SeaMonster corpseType = SeaMonster.fromCorpseId(npc.getId());
        if (corpseType != null)
        {
            trackedCorpses.put(npc, new TrackedCorpse(npc, corpseType.getDisplayName()));
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNpcDespawned(NpcDespawned event)
    {
        trackedMonsters.remove(event.getNpc());
        trackedCorpses.remove(event.getNpc());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        GraphicsObject gfx = event.getGraphicsObject();
        if (gfx.getId() != CANNON_FIRE_GRAPHICS_ID) return;

        Player local = client.getLocalPlayer();
        if (local == null || getBoatWorldEntity() == null) return;

        LocalPoint playerLocal = local.getLocalLocation();
        LocalPoint gfxLocal = gfx.getLocation();

        int dx = gfxLocal.getX() - playerLocal.getX();
        int dy = gfxLocal.getY() - playerLocal.getY();
        int distTiles = (int) Math.sqrt(dx * dx + dy * dy) / 128;

        if (distTiles <= 6)
        {
            CannonType activeCannonType = getActiveCannonType();
            int speed = activeCannonType != null ? activeCannonType.getAttackSpeedTicks() : 7;
            cannonTicksRemaining = speed + 1;
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();

        if (actor instanceof Player && actor == client.getLocalPlayer())
        {
            int anim = actor.getAnimation();
            boolean wasAtCannon = playerAtCannon;
            playerAtCannon = (anim == CANNON_ANIM_OPERATING || anim == CANNON_ANIM_READY);

            if (wasAtCannon && !playerAtCannon)
            {
                cannonTicksRemaining = 0;
            }
            return;
        }

        if (actor instanceof NPC)
        {
            NPC npc = (NPC) actor;
            TrackedMonster tracked = trackedMonsters.get(npc);
            if (tracked == null) return;

            int animId = npc.getAnimation();
            if (animId != -1 && tracked.getType().isAttackAnimation(animId))
            {
                tracked.setTicksUntilNextAttack(tracked.getType().getAttackSpeedTicks() + 1);
            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGameTick(GameTick ignored)
    {
        if (cannonTicksRemaining > 0)
        {
            cannonTicksRemaining--;
        }

        List<GameObject> currentCannons = getTrackedCannons();
        if (getBoatWorldEntity() != null && !currentCannons.isEmpty())
        {
            Player local = client.getLocalPlayer();
            LocalPoint playerLp = local != null ? local.getLocalLocation() : null;
            boolean nearCannon = false;
            if (playerLp != null)
            {
                for (GameObject cannon : currentCannons)
                {
                    LocalPoint cannonLp = cannon.getLocalLocation();
                    int dx = playerLp.getSceneX() - cannonLp.getSceneX();
                    int dy = playerLp.getSceneY() - cannonLp.getSceneY();
                    boolean cardinallyAdjacent = (Math.abs(dx) == 1 && dy == 0) || (dx == 0 && Math.abs(dy) == 1);
                    if (cardinallyAdjacent)
                    {
                        nearCannon = true;
                        break;
                    }
                }
            }
            if (!nearCannon && playerAtCannon)
            {
                cannonTicksRemaining = 0;
            }
            playerAtCannon = nearCannon;
        }
        else
        {
            playerAtCannon = false;
        }

        for (TrackedMonster tracked : trackedMonsters.values())
        {
            if (tracked.getTicksUntilNextAttack() > 0)
            {
                tracked.setTicksUntilNextAttack(tracked.getTicksUntilNextAttack() - 1);
            }
        }

        trackedCorpses.values().removeIf(corpse -> !corpse.tick());
    }

    @Provides
    @SuppressWarnings("unused")
    public ShipCombatConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ShipCombatConfig.class);
    }
}