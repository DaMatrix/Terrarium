package net.gegy1000.earth.server.world.composer;

import net.gegy1000.earth.server.world.cover.Cover;
import net.gegy1000.earth.server.world.cover.CoverConfig;
import net.gegy1000.gengen.api.ChunkPrimeWriter;
import net.gegy1000.gengen.api.CubicPos;
import net.gegy1000.terrarium.server.world.pipeline.composer.surface.SurfaceComposer;
import net.gegy1000.terrarium.server.world.pipeline.data.ColumnData;
import net.gegy1000.terrarium.server.world.pipeline.data.DataKey;
import net.gegy1000.terrarium.server.world.pipeline.data.raster.EnumRaster;

public class EarthCarvingComposer implements SurfaceComposer {
    private final DataKey<EnumRaster<Cover>> coverKey;

    public EarthCarvingComposer(DataKey<EnumRaster<Cover>> coverKey) {
        this.coverKey = coverKey;
    }

    @Override
    public void composeSurface(ColumnData data, CubicPos pos, ChunkPrimeWriter writer) {
        data.get(this.coverKey).ifPresent(coverRaster -> {
            Cover focus = coverRaster.get(8, 8);

            CoverConfig config = focus.getConfig();
            config.carvers().forEach(carver -> carver.carve(pos, writer, data));
        });
    }
}
