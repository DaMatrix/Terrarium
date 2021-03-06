package net.gegy1000.terrarium.server.world.generator.customization.widget;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class CustomizationCategory {
    private final String identifier;
    private final ImmutableList<CustomizationWidget> widgets;

    public CustomizationCategory(String identifier, List<CustomizationWidget> widgets) {
        this.identifier = identifier;
        this.widgets = ImmutableList.copyOf(widgets);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format("category.terrarium." + this.identifier + ".name");
    }

    public ImmutableList<CustomizationWidget> getWidgets() {
        return this.widgets;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomizationCategory && ((CustomizationCategory) obj).identifier.equals(this.identifier);
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }
}
