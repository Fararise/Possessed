package net.fararise.possessed.client;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.client.gui.PossessExperienceGUI;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.network.PossessClickEmptyMessage;
import net.fararise.possessed.server.network.StopPossessingMessage;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class ClientEventHandler {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        PossessivePlayer possessivePlayer = PossessHandler.get(player);
        if (possessivePlayer != null) {
            float partialTicks = event.getPartialRenderTick();
            float animation = Math.min(PossessivePlayer.POSSESS_ANIMATION_LENGTH, Math.max(0.0F, possessivePlayer.getPossessAnimation() + (possessivePlayer.isPossessing() ? partialTicks : -partialTicks)));
            RenderManager renderManager = ClientEventHandler.MINECRAFT.getRenderManager();
            boolean renderShadow = renderManager.isRenderShadow();
            EntityLivingBase entity = possessivePlayer.getPossessing();
            renderManager.setRenderShadow(false);
            GlStateManager.pushMatrix();
            int light = !entity.isBurning() ? entity.getBrightnessForRender(partialTicks) : 0xF000F0;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 0x10000, light / 0x10000);
            if (!possessivePlayer.isAnimating()) {
                if (ClientEventHandler.MINECRAFT.currentScreen != null) {
                    possessivePlayer.update(player, true);
                }
                event.setCanceled(true);
            }
            if (!(entity.isDead && entity.deathTime > 20)) {
                renderManager.doRenderEntity(entity, event.getX(), event.getY(), event.getZ(), entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, partialTicks, true);
            }
            GlStateManager.popMatrix();
            renderManager.setRenderShadow(renderShadow);
            if (possessivePlayer.isAnimating()) {
                GlStateManager.pushMatrix();
                float renderAnimation = 1.0F - animation / PossessivePlayer.POSSESS_ANIMATION_LENGTH;
                GlStateManager.translate((possessivePlayer.getOriginalX() - (player.prevPosX + (player.posX - player.prevPosX) * partialTicks)) * renderAnimation, (possessivePlayer.getOriginalY() - (player.prevPosY + (player.posY - player.prevPosY) * partialTicks)) * renderAnimation, (possessivePlayer.getOriginalZ() - (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks)) * renderAnimation);
                float scale = renderAnimation * 0.5F + 0.5F;
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.color(1.0F, 1.0F, 1.0F, renderAnimation);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        PossessivePlayer possessivePlayer = PossessHandler.get(player);
        if (possessivePlayer != null) {
            if (possessivePlayer.isAnimating()) {
                GlStateManager.popMatrix();
            }
        }
    }

    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.FogDensity event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null && (possessivePlayer.getPossessing().canBreatheUnderwater() || possessivePlayer.getPossessing() instanceof EntityGuardian)) {
                if (ActiveRenderInfo.getBlockStateAtEntityViewpoint(ClientEventHandler.MINECRAFT.theWorld, player, (float) event.getRenderPartialTicks()).getMaterial() != Material.WATER) {
                    GlStateManager.setFog(GlStateManager.FogMode.EXP);
                    event.setDensity(0.125F);
                    event.setCanceled(true);
                } else {
                    GlStateManager.setFog(GlStateManager.FogMode.EXP);
                    event.setDensity(0.02F);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onClickAir(PlayerInteractEvent.RightClickEmpty event) {
        EntityPlayer player = event.getEntityPlayer();
        PossessivePlayer possessivePlayer = PossessHandler.get(player);
        if (possessivePlayer != null) {
            if (possessivePlayer.isPossessing() && possessivePlayer.getPossessAnimation() >= PossessivePlayer.POSSESS_ANIMATION_LENGTH) {
                for (EntityPossessHandler handler : PossessHandler.getPossessHandlers(possessivePlayer.getPossessing())) {
                    handler.onClickAir(possessivePlayer, player);
                }
                Possessed.getNetworkWrapper().sendToServer(new PossessClickEmptyMessage());
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = ClientEventHandler.MINECRAFT.thePlayer;
        if (ClientProxy.STOP_POSSESSING_KEY.isPressed() && PossessHandler.isPossessing(player)) {
            PossessHandler.possess(player, null);
            Possessed.getNetworkWrapper().sendToServer(new StopPossessingMessage());
        } else if (ClientProxy.POSSESS_EXPERIENCE.isPressed() && MINECRAFT.currentScreen == null) {
            MINECRAFT.displayGuiScreen(new PossessExperienceGUI(player));
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        EntityPlayer player = ClientEventHandler.MINECRAFT.thePlayer;
        if (player.getHeldItem(EnumHand.MAIN_HAND) == null) {
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null) {
                event.setCanceled(true);
            }
        }
    }
}
