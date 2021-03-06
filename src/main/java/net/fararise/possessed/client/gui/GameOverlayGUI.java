package net.fararise.possessed.client.gui;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.client.update.UpdateHandler;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.item.ItemPossessiveHelmet;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessionExperience;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class GameOverlayGUI extends Gui {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private static final ResourceLocation UPDATE_TEXTURE = new ResourceLocation(Possessed.MODID, "textures/items/possessive_hat.png");
    private static int UPDATE_ANIMATION_LENGTH = 140;
    private static int UPDATE_ANIMATION_TRANSITION = 10;

    private static boolean shouldDisplayUpdate = false;

    private int updateAnimation;
    private boolean displayingUpdate;

    private int prevExperience;

    public static void displayUpdate() {
        GameOverlayGUI.shouldDisplayUpdate = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = GameOverlayGUI.MINECRAFT.thePlayer;
        if (player != null && player.ticksExisted > 40 && GameOverlayGUI.MINECRAFT.currentScreen == null) {
            if (GameOverlayGUI.shouldDisplayUpdate && !this.displayingUpdate) {
                this.displayingUpdate = true;
                this.updateAnimation = 0;
                GameOverlayGUI.shouldDisplayUpdate = false;
            } else if (this.displayingUpdate) {
                if (++this.updateAnimation > GameOverlayGUI.UPDATE_ANIMATION_LENGTH) {
                    this.displayingUpdate = false;
                }
            }
        }
        if (player != null && event.phase == TickEvent.Phase.START) {
            PossessCapability possessCapability = PossessCapability.Implementation.get(player);
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            if (possessivePlayer != null) {
                this.prevExperience = possessCapability.getExperience().getExperience(possessivePlayer.getPossessing().getClass());
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            ScaledResolution resolution = new ScaledResolution(GameOverlayGUI.MINECRAFT);
            float partialTicks = event.getPartialTicks();
            GlStateManager.disableLighting();
            EntityPlayer player = GameOverlayGUI.MINECRAFT.thePlayer;
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            FontRenderer fontRenderer = GameOverlayGUI.MINECRAFT.fontRendererObj;
            PossessivePlayer possessivePlayer = PossessHandler.get(player);
            boolean possessing = possessivePlayer != null;
            if ((head != null && head.getItem() instanceof ItemPossessiveHelmet) || possessing) {
                PossessCapability possessCapability = PossessCapability.Implementation.get(player);
                GlStateManager.disableTexture2D();
                int width = 150;
                GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                this.drawTexturedModalRect(2, 12, 0, 0, width, 10);
                this.drawGradientRect(3, 13, (int) (3 + possessCapability.getPossessTime() * (width - 2) / PossessCapability.Implementation.BASE_TIME), 13 + 8, 0xFF97E100, 0xFF00E09B);
                GlStateManager.enableTexture2D();
                fontRenderer.drawString(I18n.translateToLocal("gui.possess_time.name"), 3, 2, 0xFFFFFF);
                fontRenderer.drawString((int) (possessCapability.getPossessTime() * 100 / PossessCapability.Implementation.BASE_TIME) + "%", 155, 13, 0xFFFFFF);
                if (possessing) {
                    GlStateManager.disableTexture2D();
                    int experience = possessCapability.getExperience().getExperience(possessivePlayer.getPossessing().getClass());
                    GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                    this.drawTexturedModalRect(2, 24, 0, 0, width, 7);
                    int scaledProgress = (int) ((this.prevExperience + ((float) experience - this.prevExperience) * partialTicks) * (width - 2)) / PossessionExperience.MAXIMUM_EXPERIENCE;
                    this.drawGradientRect(3, 25, 3 + scaledProgress, 30, 0xFFA616FF, 0xFF5D0EA5);
                }
            }
            if (this.displayingUpdate) {
                int width = resolution.getScaledWidth();
                String updateText = I18n.translateToLocalFormatted("notification.update_available.name", UpdateHandler.getUpdateAvailable());
                int updateTextWidth = fontRenderer.getStringWidth(updateText);
                float maxUpdateX = updateTextWidth + 20.0F;
                float updateX = maxUpdateX;
                float animation = this.updateAnimation + partialTicks;
                if (animation <= GameOverlayGUI.UPDATE_ANIMATION_TRANSITION) {
                    updateX = (animation / GameOverlayGUI.UPDATE_ANIMATION_TRANSITION) * maxUpdateX;
                } else if (animation > GameOverlayGUI.UPDATE_ANIMATION_LENGTH - GameOverlayGUI.UPDATE_ANIMATION_TRANSITION) {
                    updateX = ((GameOverlayGUI.UPDATE_ANIMATION_LENGTH - animation) / GameOverlayGUI.UPDATE_ANIMATION_TRANSITION) * maxUpdateX;
                }
                GlStateManager.disableTexture2D();
                this.drawRectangle(width - updateX - 6.0F, 0.0F, updateTextWidth + updateX + 6.0F, 20, 0xAA606060);
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GameOverlayGUI.MINECRAFT.getTextureManager().bindTexture(GameOverlayGUI.UPDATE_TEXTURE);
                this.drawTexturedRectangle(width - updateX + updateTextWidth, 0.0F, 20, 20);
                fontRenderer.drawString(updateText, width - updateX - 2.0F, 6.5F, 0xFFFFFF, true);
            }
        }
    }

    protected void drawRectangle(float x, float y, float width, float height, int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = (color >> 24 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(x, y + height, 0.0).endVertex();
        buffer.pos(x + width, y + height, 0.0).endVertex();
        buffer.pos(x + width, y, 0.0).endVertex();
        buffer.pos(x, y, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
    }

    protected void drawTexturedRectangle(float x, float y, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0.0).tex(0.0, 1.0).endVertex();
        buffer.pos(x + width, y + height, 0.0).tex(1.0, 1.0).endVertex();
        buffer.pos(x + width, y, 0.0).tex(1.0, 0.0).endVertex();
        buffer.pos(x, y, 0.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
    }
}
