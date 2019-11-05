package net.gegy1000.earth.server.world.data.op;

import net.gegy1000.earth.server.world.cover.Cover;
import net.gegy1000.earth.server.world.geography.Landform;
import net.gegy1000.terrarium.server.world.pipeline.data.DataOp;
import net.gegy1000.terrarium.server.world.pipeline.data.raster.BitRaster;
import net.gegy1000.terrarium.server.world.pipeline.data.raster.EnumRaster;
import net.gegy1000.terrarium.server.world.pipeline.data.raster.ShortRaster;

import java.util.concurrent.CompletableFuture;

public final class WaterOps {
    public static DataOp<EnumRaster<Landform>> applyWaterMask(DataOp<EnumRaster<Landform>> landforms, DataOp<BitRaster> ocean) {
        return DataOp.of(view -> {
            CompletableFuture<EnumRaster<Landform>> landformFuture = landforms.apply(view);
            CompletableFuture<BitRaster> oceanFuture = ocean.apply(view);

            return CompletableFuture.allOf(landformFuture, oceanFuture)
                    .thenApply(v -> {
                        EnumRaster<Landform> landformRaster = landformFuture.join();
                        BitRaster oceanRaster = oceanFuture.join();

                        landformRaster.transform((source, x, y) -> {
                            if (oceanRaster.get(x, y)) {
                                return Landform.SEA;
                            } else if (source.isWater()) {
                                return Landform.LAND;
                            }
                            return source;
                        });

                        return landformRaster;
                    });
        });
    }

    public static DataOp<ShortRaster> produceWaterLevel(DataOp<EnumRaster<Landform>> landforms, int seaLevel) {
        return landforms.map((landformRaster, view) -> {
            ShortRaster waterLevelRaster = ShortRaster.create(view);

            landformRaster.iterate((landform, x, y) -> {
                if (landform == Landform.SEA) {
                    waterLevelRaster.set(x, y, (short) seaLevel);
                } else {
                    waterLevelRaster.set(x, y, Short.MIN_VALUE);
                }
            });

            return waterLevelRaster;
        });
    }

    // TODO: Carve smoothed edges
    public static DataOp<ShortRaster> applyToHeight(DataOp<ShortRaster> height, DataOp<EnumRaster<Landform>> landforms, DataOp<ShortRaster> waterLevel, int seaDepth) {
        return DataOp.of(view -> {
            CompletableFuture<ShortRaster> heightFuture = height.apply(view);
            CompletableFuture<EnumRaster<Landform>> landformFuture = landforms.apply(view);
            CompletableFuture<ShortRaster> waterLevelFuture = waterLevel.apply(view);

            return CompletableFuture.allOf(heightFuture, landformFuture, waterLevelFuture)
                    .thenApply(v -> {
                        ShortRaster heightRaster = heightFuture.join();
                        EnumRaster<Landform> landformRaster = landformFuture.join();
                        ShortRaster waterLevelRaster = waterLevelFuture.join();

                        heightRaster.transform((source, x, y) -> {
                            Landform landform = landformRaster.get(x, y);
                            if (landform == Landform.SEA) {
                                int level = waterLevelRaster.get(x, y);
                                double carvedHeight = level - seaDepth;
                                return (short) Math.round(carvedHeight);
                            }

                            return source;
                        });

                        return heightRaster;
                    });
        });
    }

    // TODO: Properly select cover type for filled in space!
    public static DataOp<EnumRaster<Cover>> applyToCover(DataOp<EnumRaster<Cover>> cover, DataOp<EnumRaster<Landform>> landforms) {
        return DataOp.of(view -> {
            CompletableFuture<EnumRaster<Cover>> coverFuture = cover.apply(view);
            CompletableFuture<EnumRaster<Landform>> landformFuture = landforms.apply(view);

            return CompletableFuture.allOf(coverFuture, landformFuture)
                    .thenApply(v -> {
                        EnumRaster<Cover> coverRaster = coverFuture.join();
                        EnumRaster<Landform> landformRaster = landformFuture.join();

                        coverRaster.transform((source, x, y) -> {
                            Landform landform = landformRaster.get(x, y);
                            if (landform.isWater()) {
                                return Cover.WATER;
                            } else if (source == Cover.WATER) {
                                return Cover.NONE;
                            }
                            return source;
                        });

                        return coverRaster;
                    });
        });
    }
}
