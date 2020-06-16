package net.gegy1000.terrarium.client.gui.widget;

import com.google.common.collect.ImmutableList;
import net.gegy1000.terrarium.Terrarium;
import net.gegy1000.terrarium.client.gui.customization.SelectPresetGui;
import net.gegy1000.terrarium.server.world.TerrariumWorldType;
import net.gegy1000.terrarium.server.world.generator.customization.TerrariumPreset;
import net.gegy1000.terrarium.server.world.generator.customization.TerrariumPresetRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class PresetList extends GuiListExtended {
    private static final ResourceLocation FALLBACK_ICON = new ResourceLocation(Terrarium.ID, "textures/preset/fallback.png");
    private static final ResourceLocation ICON_OVERLAY = new ResourceLocation("textures/gui/world_selection.png");

    private final SelectPresetGui parent;

    private final ImmutableList<PresetEntry> entries;

    private int selectedIndex = -1;

    public PresetList(Minecraft mc, SelectPresetGui parent, TerrariumWorldType worldType) {
        super(mc, parent.width, parent.height, 32, parent.height - 64, 36);
        this.parent = parent;

        TerrariumPreset defaultPreset = worldType.getPreset();

        ImmutableList.Builder<PresetEntry> entryBuilder = ImmutableList.builder();
        entryBuilder.add(new PresetEntry(mc, defaultPreset));

        for (TerrariumPreset preset : TerrariumPresetRegistry.getPresets()) {
            if (preset != defaultPreset && preset.getWorldType().equals(worldType.getIdentifier())) {
                entryBuilder.add(new PresetEntry(mc, preset));
            }
        }

        this.entries = entryBuilder.build();

        this.selectPreset(0);
    }

    public void selectPreset(int index) {
        if (index >= 0 && index < this.entries.size()) {
            PresetEntry entry = this.entries.get(index);
            this.parent.selectPreset(entry.preset);
            this.selectedIndex = index;
        } else {
            this.selectedIndex = -1;
        }
    }

    public void applyPreset() {
        this.parent.applyPreset();
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 50;
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 20;
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    protected int getSize() {
        return this.entries.size();
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedIndex;
    }

    public class PresetEntry implements IGuiListEntry {
        private final Minecraft mc;

        final TerrariumPreset preset;
        private final ResourceLocation icon;

        private long lastClickTime;

        public PresetEntry(Minecraft mc, TerrariumPreset preset) {
            this.mc = mc;
            this.preset = preset;

            this.icon = preset.getIcon();
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            this.mc.fontRenderer.drawString(this.preset.getLocalizedName(), x + 32 + 3, y + 1, 0xFFFFFF);

            String description = TextFormatting.DARK_GRAY + this.preset.getLocalizedDescription();
            this.mc.fontRenderer.drawSplitString(description, x + 32 + 3, y + this.mc.fontRenderer.FONT_HEIGHT + 3, listWidth - 40, 0xFFFFFF);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            TextureManager textureManager = this.mc.getTextureManager();
            textureManager.bindTexture(this.computeIcon());

            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);

            if (this.mc.gameSettings.touchscreen || isSelected) {
                Gui.drawRect(x, y, x + 32, y + 32, 0xA0909090);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                textureManager.bindTexture(ICON_OVERLAY);
                Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, mouseX - x < 32 ? 32.0F : 0.0F, 32, 32, 256.0F, 256.0F);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            PresetList.this.selectPreset(slotIndex);

            if (relativeX < 32 || System.currentTimeMillis() - this.lastClickTime < 250) {
                PresetList.this.applyPreset();
                return true;
            }

            this.lastClickTime = System.currentTimeMillis();
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        }

        private ResourceLocation computeIcon() {
            if (this.mc.getTextureManager().getTexture(this.icon) != null) {
                return this.icon;
            }
            try (IResource resource = this.mc.getResourceManager().getResource(this.icon)) {
                if (resource != null) {
                    return this.icon;
                }
            } catch (IOException e) {
                // fall through
            }
            return FALLBACK_ICON;
        }
    }
}
