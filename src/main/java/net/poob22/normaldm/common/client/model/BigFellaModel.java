package net.poob22.normaldm.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.poob22.normaldm.common.client.model.animation.BigFellaAnimations;
import net.poob22.normaldm.common.server.entity.living.BigFellaEntity;

public class BigFellaModel<T extends BigFellaEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart larm;
	private final ModelPart rarm;
	private final ModelPart belly;
	private final ModelPart lleg;
	private final ModelPart rleg;

	public BigFellaModel(ModelPart root) {
		this.base = root.getChild("base");
		this.body = this.base.getChild("body");
		this.head = this.body.getChild("head");
		this.larm = this.body.getChild("larm");
		this.rarm = this.body.getChild("rarm");
		this.belly = this.body.getChild("belly");
		this.lleg = this.body.getChild("lleg");
		this.rleg = this.body.getChild("rleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 50).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.0F, 1.0F));

		PartDefinition larm = body.addOrReplaceChild("larm", CubeListBuilder.create().texOffs(40, 50).addBox(0.0F, -2.0F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -16.0F, -0.5F, 0.0F, 0.0F, -0.3927F));

		PartDefinition rarm = body.addOrReplaceChild("rarm", CubeListBuilder.create().texOffs(60, 50).addBox(-5.0F, -2.0F, -2.6F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -16.0F, -0.4F, 0.0F, 0.0F, 0.3927F));

		PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -22.0F, -12.0F, 28.0F, 27.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lleg = body.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(40, 65).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 5.0F, 0.5F));

		PartDefinition rleg = body.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(60, 65).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 5.0F, 0.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return base;
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(pNetHeadYaw);
		this.animateWalk(BigFellaAnimations.walk, pLimbSwing, pLimbSwingAmount, 2.35F, 3.0F);
		this.animate(pEntity.getAnimationState(), BigFellaAnimations.slam, pAgeInTicks);
	}

	private void applyHeadRotation(float netHeadYaw)
	{
		netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);

		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
	}
}