package net.gegy1000.earth.client.terrain;

import net.gegy1000.earth.server.world.biome.BiomeClassifier;
import net.gegy1000.earth.server.world.cover.Cover;
import net.gegy1000.earth.server.world.cover.CoverMarkers;

import java.awt.Color;

public final class TerrainColorizer {
    private static final Color GRASS_COLOR = new Color(126, 208, 61);
    private static final Color FOREST_COLOR = new Color(47, 111, 45);

    private static final Color WATER_COLOR = new Color(51, 122, 255);
    private static final Color SLOPE_COLOR = Color.GRAY;
    private static final Color SNOW_COLOR = Color.WHITE;

    public static Color get(Cover cover, int slope, float minTemperature) {
        if (cover == Cover.WATER) {
            return WATER_COLOR;
        }

        if (slope > 60) {
            return SLOPE_COLOR;
        }

        if (cover == Cover.PERMANENT_SNOW || minTemperature < BiomeClassifier.FREEZE_MIN_TEMPERATURE) {
            return SNOW_COLOR;
        }

        if (cover.is(CoverMarkers.FOREST)) {
            return FOREST_COLOR;
        } else {
            return GRASS_COLOR;
        }
    }
}
