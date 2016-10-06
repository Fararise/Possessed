package net.fararise.possessed.client.gui;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.capability.PossessCapability;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessionExperience;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PossessExperienceGUI extends GuiScreen {
    private static final int ENTITIES_PER_PAGE = 8;

    private PossessCapability capability;
    private Map<String, Entity> entities = new HashMap<>();
    private int page;
    private int maxPages;

    private GuiButton left;
    private GuiButton right;

    public PossessExperienceGUI(EntityPlayer player) {
        super();
        this.capability = PossessCapability.Implementation.get(player);
        DifficultyInstance difficulty = player.worldObj.getDifficultyForLocation(player.getPosition());
        for (Map.Entry<String, Integer> entry : this.capability.getExperience().getAllExperience().entrySet()) {
            Entity entity = EntityList.createEntityByName(entry.getKey(), player.worldObj);
            if (entity instanceof EntityLiving) {
                EntityLiving living = (EntityLiving) entity;
                living.onInitialSpawn(difficulty, null);
                living.rotationYaw = 45.0F;
                living.renderYawOffset = living.rotationYaw;
                living.rotationYawHead = living.rotationYaw;
                if (living instanceof EntityAgeable) {
                    ((EntityAgeable) living).setGrowingAge(0);
                }
                this.entities.put(entry.getKey(), living);
            }
        }
        this.maxPages = (int) Math.ceil((float) this.entities.size() / ENTITIES_PER_PAGE);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.left = new GuiButton(0, 10, this.height / 2 - 10, 20, 20, "<"));
        this.buttonList.add(this.right = new GuiButton(1, this.width - 30, this.height / 2 - 10, 20, 20, ">"));
        this.updateButtonState();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        String title = I18n.translateToLocal("gui.possess_experience.name");
        this.fontRendererObj.drawString(title, this.width / 2 - this.fontRendererObj.getStringWidth(title) / 2, 5, 0xFFFFFF);

        ScaledResolution resolution = new ScaledResolution(this.mc);
        int scaleFactor = resolution.getScaleFactor();

        int index = 0;
        int startIndex = this.page * ENTITIES_PER_PAGE;
        int endIndex = startIndex + ENTITIES_PER_PAGE;

        int entityX = 30 * scaleFactor;
        int entityY = 14 * scaleFactor;

        for (Map.Entry<String, Entity> entry : this.entities.entrySet()) {
            if (index >= startIndex && index < endIndex) {
                String entityName = I18n.translateToLocal("entity." + entry.getKey() + ".name");
                this.fontRendererObj.drawString(entityName, entityX - this.fontRendererObj.getStringWidth(entityName) / 2, entityY, 0xFFFFFF);

                int experience = this.capability.getExperience().getExperience(entry.getKey());

                int barX = entityX - (15 * scaleFactor);
                int barY = entityY + (38 * scaleFactor);
                this.drawGradientRect(barX, barY, barX + (30 * scaleFactor), barY + (2 * scaleFactor), 0xFF606060, 0xFF606060);
                this.drawGradientRect(barX + 1, barY + 1, barX + (int) (((experience * 29.8F) / PossessionExperience.MAXIMUM_EXPERIENCE) * scaleFactor), barY + (2 * scaleFactor) - 1, 0xFFA616FF, 0xFF5D0EA5);

                String percent = (experience * 100 / PossessionExperience.MAXIMUM_EXPERIENCE) + "%";
                this.fontRendererObj.drawString(percent, entityX - this.fontRendererObj.getStringWidth(percent) / 2, entityY + (41 * scaleFactor), 0xFFFFFF);

                Entity entity = entry.getValue();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableColorMaterial();
                GlStateManager.pushMatrix();
                GlStateManager.translate(entityX, entityY + (32.0F * scaleFactor), 50.0F);
                float dimension;
                if (entity.width > entity.height) {
                    dimension = entity.width;
                } else {
                    dimension = (entity.height + entity.getEyeHeight()) / 2.0F;
                }
                float scale = 20.0F / dimension * scaleFactor;
                if (entity instanceof EntityLivingBase) {
                    List<EntityPossessHandler> handlers = PossessHandler.getPossessHandlers((EntityLivingBase) entity);
                    for (EntityPossessHandler handler : handlers) {
                        if (handler.getGUIScale() != 1.0F) {
                            scale *= handler.getGUIScale();
                        }
                        GlStateManager.translate(0.0F, handler.getGUITranslationY() * scaleFactor, 0.0F);
                    }
                }
                GlStateManager.scale(-scale, scale, scale);
                GlStateManager.rotate(20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
                RenderHelper.enableStandardItemLighting();
                GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
                RenderManager renderManager = this.mc.getRenderManager();
                renderManager.setPlayerViewY(180.0F);
                renderManager.setRenderShadow(false);
                renderManager.doRenderEntity(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, false);
                renderManager.setRenderShadow(true);
                GlStateManager.popMatrix();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

                entityY += 50 * scaleFactor;

                if (entityY + (30 * scaleFactor) > this.height) {
                    entityY = 14 * scaleFactor;
                    entityX += 50 * scaleFactor;
                }
            }

            index++;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0) {
            this.page--;
        } else if (button.id == 1) {
            this.page++;
        }
        this.updateButtonState();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void updateButtonState() {
        this.left.enabled = this.page > 0;
        this.right.enabled = this.page < this.maxPages - 1;
    }
}
