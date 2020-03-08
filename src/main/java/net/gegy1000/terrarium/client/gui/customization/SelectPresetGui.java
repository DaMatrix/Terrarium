package net.gegy1000.terrarium.client.gui.customization;

import net.gegy1000.terrarium.client.gui.widget.PresetList;
import net.gegy1000.terrarium.server.world.TerrariumWorldType;
import net.gegy1000.terrarium.server.world.generator.customization.TerrariumPreset;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.function.Consumer;

public class SelectPresetGui extends GuiScreen {
    private static final int SELECT_BUTTON = 0;
    private static final int CANCEL_BUTTON = 1;

    private final Consumer<TerrariumPreset> acceptPreset;
    private final GuiScreen backward;
    private final TerrariumWorldType worldType;

    private GuiButton selectButton;

    private PresetList presetList;
    private TerrariumPreset selectedPreset;

    public SelectPresetGui(Consumer<TerrariumPreset> acceptPreset, GuiScreen backward, TerrariumWorldType worldType) {
        this.acceptPreset = acceptPreset;
        this.backward = backward;
        this.worldType = worldType;
    }

    @Override
    public void initGui() {
        this.presetList = new PresetList(this.mc, this, this.worldType);

        this.selectButton = this.addButton(new GuiButton(SELECT_BUTTON, this.width / 2 - 154, this.height - 28, 150, 20, I18n.format("gui.done")));
        this.addButton(new GuiButton(CANCEL_BUTTON, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.cancel")));

        this.selectButton.enabled = false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.presetList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == SELECT_BUTTON) {
                this.applyPreset();
            } else {
                this.mc.displayGuiScreen(this.backward);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        this.presetList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, I18n.format("gui.terrarium.select_preset.name"), this.width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.presetList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.presetList.mouseReleased(mouseX, mouseY, state);
    }

    public void selectPreset(TerrariumPreset preset) {
        this.selectedPreset = preset;
        this.selectButton.enabled = preset != null;
    }

    public void applyPreset() {
        if (this.selectedPreset != null) {
            this.acceptPreset.accept(this.selectedPreset);
        } else {
            this.mc.displayGuiScreen(this.backward);
        }
    }
}
