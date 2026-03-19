package com.shipcombat;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldEntityConfig;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ShipBoundariesOverlay extends Overlay
{
    @Inject private Client client;
    @Inject private ShipCombatPlugin plugin;
    @Inject private ShipCombatConfig config;

    @Inject
    public ShipBoundariesOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(0.4f);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showShipSweep()) return null;

        WorldEntity boat = plugin.getBoatWorldEntity();
        if (boat == null) return null;

        renderMergedDaisy(graphics, boat);
        return null;
    }

    private void renderMergedDaisy(Graphics2D graphics, WorldEntity we)
    {
        LocalPoint centerLp = we.getTargetLocation();
        if (centerLp == null) return;

        WorldEntityConfig wec = we.getConfig();
        int tileSize = Perspective.LOCAL_TILE_SIZE;
        int halfTile = tileSize / 2;

        int tilesWide = wec.getBoundsWidth() / tileSize;
        int tilesLong = wec.getBoundsHeight() / tileSize;

        Area combinedArea = new Area();

        for (int step = 0; step < 16; step++)
        {
            int loopAngle = step * 128;

            for (int x = 0; x < tilesWide; x++)
            {
                for (int y = 0; y < tilesLong; y++)
                {
                    // Synchronized center logic: matches the combat overlay math
                    int modelX = (x * tileSize) - (wec.getBoundsWidth() / 2) + halfTile;
                    int modelY = (y * tileSize) - (wec.getBoundsHeight() / 2) + halfTile;

                    float[] xs = {modelX - halfTile, modelX + halfTile, modelX + halfTile, modelX - halfTile};
                    float[] ys = {modelY - halfTile, modelY - halfTile, modelY + halfTile, modelY + halfTile};
                    float[] zs = {0, 0, 0, 0};

                    int[] cxs = new int[4], cys = new int[4];
                    Perspective.modelToCanvas(client, client.getTopLevelWorldView(), 4,
                            centerLp.getX(), centerLp.getY(), 0, loopAngle, xs, ys, zs, cxs, cys);

                    if (cxs[0] != 0 || cys[0] != 0)
                    {
                        combinedArea.add(new Area(new Polygon(cxs, cys, 4)));
                    }
                }
            }
        }

        if (!combinedArea.isEmpty())
        {
            graphics.setColor(config.shipSweepFillColor());
            graphics.fill(combinedArea);
            graphics.setStroke(new BasicStroke(1.5f));
            graphics.setColor(config.shipSweepBorderColor());
            graphics.draw(combinedArea);
        }
    }
}