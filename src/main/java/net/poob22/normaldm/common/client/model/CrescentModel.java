package net.poob22.normaldm.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.poob22.normaldm.common.client.model.animation.CrescentAnimations;
import net.poob22.normaldm.common.server.entity.living.CrescentEntity;
import org.jetbrains.annotations.NotNull;

public class CrescentModel<T extends CrescentEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart lleg;
	private final ModelPart rleg;
	private final ModelPart head;
	private final ModelPart upper_jaw;

	public CrescentModel(ModelPart root) {
		this.base = root.getChild("base");
		this.lleg = this.base.getChild("lleg");
		this.rleg = this.base.getChild("rleg");
		this.head = this.base.getChild("head");
		this.upper_jaw = this.head.getChild("upper_jaw");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition lleg = base.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(0, 28).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -4.5F, 0.0F));

		PartDefinition rleg = base.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(8, 28).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -4.5F, 0.0F));

		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 0.0F));

		PartDefinition upper_jaw = head.addOrReplaceChild("upper_jaw", CubeListBuilder.create().texOffs(0, 14).addBox(-5.0F, -4.0F, -10.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 5.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public @NotNull ModelPart root() {
		return base;
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(pNetHeadYaw, pHeadPitch, pAgeInTicks);
		this.animateWalk(CrescentAnimations.walk, pLimbSwing, pLimbSwingAmount, 2.0F, 1.0F);
		this.animate(pEntity.chargingAnimation, CrescentAnimations.charge_up, pAgeInTicks, 1.0F);
		this.animate(pEntity.shootingAnimation, CrescentAnimations.shooting, pAgeInTicks, 1.0F);
	}

	private void applyHeadRotation(float netHeadYaw, float netHeadPitch, float ageInTicks)
	{
		netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
		netHeadPitch = Mth.clamp(netHeadPitch, -25.0F, 45.0F);

		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = netHeadPitch * ((float)Math.PI / 180F);
	}
}