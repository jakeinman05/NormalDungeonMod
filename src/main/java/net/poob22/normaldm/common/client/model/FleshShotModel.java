package net.poob22.normaldm.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

public class FleshShotModel<T extends Projectile> extends EntityModel<T> {
	private final ModelPart base;

	public FleshShotModel(ModelPart root) {
		this.base = root.getChild("base");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}