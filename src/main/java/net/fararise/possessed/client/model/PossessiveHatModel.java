package net.fararise.possessed.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class PossessiveHatModel extends ModelBiped {
    private ModelRenderer hat1;
    private ModelRenderer hat2;
    private ModelRenderer hat3;
    private ModelRenderer hat4;

    public PossessiveHatModel() {
        super();
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.hat1 = new ModelRenderer(this, 0, 0);
        this.hat1.setRotationPoint(0.0F, 22.0F, 0.0F);
        this.hat1.addBox(-4.0F, 0.0F, -4.0F, 8, 2, 8, 0.0F);
        this.hat2 = new ModelRenderer(this, 32, 0);
        this.hat2.setRotationPoint(-3.0F, 0.0F, -3.0F);
        this.hat2.addBox(0.0F, -5.0F, 0.0F, 6, 5, 6, 0.0F);
        this.setRotateAngle(this.hat2, -0.136659280431156F, 0.0F, 0.091106186954104F);
        this.hat3 = new ModelRenderer(this, 0, 10);
        this.hat3.setRotationPoint(1.0F, -4.0F, 1.0F);
        this.hat3.addBox(0.0F, -5.0F, 0.0F, 4, 5, 4, 0.0F);
        this.setRotateAngle(this.hat3, -0.091106186954104F, 0.0F, 0.136659280431156F);
        this.hat4 = new ModelRenderer(this, 0, 0);
        this.hat4.setRotationPoint(2.0F, -3.0F, 2.0F);
        this.hat4.addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(this.hat4, -0.045553093477052F, 0.0F, 0.136659280431156F);
        this.hat1.addChild(this.hat2);
        this.hat2.addChild(this.hat3);
        this.hat3.addChild(this.hat4);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, age, yaw, pitch, scale, entity);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, scale * -0.2F, 0.0F);
        if (entity.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        this.bipedHead.postRender(scale);
        GlStateManager.translate(0.0F, scale * -35.0F, 0.0F);
        GlStateManager.scale(1.2F, 1.2F, 1.2F);
        this.hat1.render(scale);
        GlStateManager.popMatrix();
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
