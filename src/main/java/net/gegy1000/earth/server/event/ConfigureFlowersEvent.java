package net.gegy1000.earth.server.event;

import net.gegy1000.earth.server.world.cover.Cover;
import net.gegy1000.earth.server.world.ecology.GrowthPredictors;
import net.gegy1000.earth.server.world.ecology.vegetation.FlowerDecorator;
import net.gegy1000.terrarium.server.capability.TerrariumWorld;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class ConfigureFlowersEvent extends Event {
    private final TerrariumWorld terrarium;
    private final Cover cover;
    private final GrowthPredictors predictors;
    private final FlowerDecorator flowers;

    public ConfigureFlowersEvent(TerrariumWorld terrarium, Cover cover, GrowthPredictors predictors, FlowerDecorator flowers) {
        this.terrarium = terrarium;
        this.cover = cover;
        this.predictors = predictors;
        this.flowers = flowers;
    }

    public TerrariumWorld getTerrarium() {
        return this.terrarium;
    }

    public Cover getCover() {
        return this.cover;
    }

    public GrowthPredictors getPredictors() {
        return this.predictors;
    }

    public FlowerDecorator getFlowers() {
        return this.flowers;
    }
}
