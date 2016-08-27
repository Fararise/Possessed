package net.fararise.possessed.client.gui;

import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.item.PossessiveHelmet;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GameOverlayGUI extends Gui {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            EntityPlayer player = GameOverlayGUI.MINECRAFT.thePlayer;
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if ((head != null && head.getItem() instanceof PossessiveHelmet) || PossessHandler.isPossessing(player)) {
                PossessCapability possessCapability = PossessCapability.Implementation.get(player);
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                int width = 150;
                GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                this.drawTexturedModalRect(2, 12, 0, 0, width, 10);
                this.drawGradientRect(3, 13, 3 + possessCapability.getPossessiveCharge() * (width - 2) / PossessCapability.Implementation.MAXIMUM_CHARGE, 13 + 8, 0xFF97E100, 0xFF00E09B);
                GlStateManager.enableTexture2D();
                GameOverlayGUI.MINECRAFT.fontRendererObj.drawString(I18n.translateToLocal("gui.possessive_charge.name"), 3, 2, 0xFFFFFF);
                double minutes = possessCapability.getPossessiveCharge() / 20.0 / 60.0 / 2.0;
                String minutesString = String.valueOf((int) minutes);
                String secondsString = String.valueOf((int) ((minutes - (int) minutes) * 60));
                while (minutesString.length() < 2) {
                    minutesString = "0" + minutesString;
                }
                while (secondsString.length() < 2) {
                    secondsString = "0" + secondsString;
                }
                GameOverlayGUI.MINECRAFT.fontRendererObj.drawString(minutesString + ":" + secondsString, 155, 13, 0xFFFFFF);
            }
        }
    }
}
