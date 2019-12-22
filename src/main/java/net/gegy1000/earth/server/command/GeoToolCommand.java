package net.gegy1000.earth.server.command;

import net.gegy1000.earth.TerrariumEarth;
import net.gegy1000.earth.server.capability.EarthWorld;
import net.gegy1000.earth.server.message.EarthMapGuiMessage;
import net.gegy1000.earth.server.message.EarthPanoramaMessage;
import net.gegy1000.terrarium.server.TerrariumUserTracker;
import net.gegy1000.terrarium.server.world.coordinate.Coordinate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GeoToolCommand extends CommandBase {
    @Override
    public String getName() {
        return "geotool";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return DeferredTranslator.translateString(sender, "commands.earth.geotool.usage");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);

        EarthWorld earth = player.world.getCapability(TerrariumEarth.worldCap(), null);
        if (earth != null) {
            ContainerUi.Builder builder = ContainerUi.builder(player)
                    .withTitle(DeferredTranslator.translate(player, new TextComponentTranslation("container.earth.geotool.name")))
                    .withElement(Items.COMPASS, TextFormatting.BOLD + "Where am I?", () -> this.handleLocate(player, earth));

            if (TerrariumUserTracker.usesTerrarium(player)) {
                builder = builder
                        .withElement(Items.ENDER_PEARL, TextFormatting.BOLD + "Go to place", () -> this.handleTeleport(player, earth))
                        .withElement(Items.PAINTING, TextFormatting.BOLD + "Display Panorama", () -> this.handlePanorama(player));
            }

            ContainerUi ui = builder.build();
            player.displayGUIChest(ui.createInventory());
        } else {
            throw DeferredTranslator.createException(player, "commands.earth.wrong_world");
        }
    }

    private void handleLocate(EntityPlayerMP player, EarthWorld earth) {
        Coordinate coordinate = Coordinate.atBlock(player.posX, player.posZ)
                .to(earth.getCrs());

        double longitude = coordinate.getX();
        double latitude = coordinate.getZ();

        if (TerrariumUserTracker.usesTerrarium(player)) {
            TerrariumEarth.NETWORK.sendTo(new EarthMapGuiMessage(latitude, longitude, EarthMapGuiMessage.Type.LOCATE), player);
        } else {
            String location = TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + String.format("%.5f, %.5f", latitude, longitude);
            player.sendMessage(DeferredTranslator.translate(player, new TextComponentTranslation("geotool.earth.locate.success", location)));
        }
    }

    private void handleTeleport(EntityPlayerMP player, EarthWorld earth) {
        Coordinate coordinate = Coordinate.atBlock(player.posX, player.posZ)
                .to(earth.getCrs());

        double longitude = coordinate.getX();
        double latitude = coordinate.getZ();

        TerrariumEarth.NETWORK.sendTo(new EarthMapGuiMessage(latitude, longitude, EarthMapGuiMessage.Type.TELEPORT), player);
    }

    private void handlePanorama(EntityPlayerMP player) {
        TerrariumEarth.NETWORK.sendTo(new EarthPanoramaMessage(), player);
    }
}
