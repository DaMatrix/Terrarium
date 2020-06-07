package net.gegy1000.terrarium.server.world.composer.decoration;

import net.gegy1000.gengen.api.CubicPos;
import net.gegy1000.gengen.api.writer.ChunkPopulationWriter;
import net.gegy1000.terrarium.server.capability.TerrariumWorld;
import net.gegy1000.terrarium.server.world.data.ColumnDataCache;

public interface DecorationComposer {
    void composeDecoration(TerrariumWorld terrarium, ColumnDataCache dataCache, CubicPos pos, ChunkPopulationWriter writer);
}
