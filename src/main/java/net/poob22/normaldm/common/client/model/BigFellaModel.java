package net.poob22.normaldm.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.poob22.normaldm.common.server.entity.living.BigFellaEntity;

public class BigFellaModel<T extends BigFellaEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart lleg;
	private final ModelPart rleg;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart larm;
	private final ModelPart rarm;

	public BigFellaModel(ModelPart root) {
		this.base = root.getChild("base");
		this.lleg = this.base.getChild("lleg");
		this.rleg = this.base.getChild("rleg");
		this.body = this.base.getChild("body");
		this.head = this.body.getChild("head");
		this.larm = this.body.getChild("larm");
		this.rarm = this.body.getChild("rarm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition lleg = base.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(40, 65).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, -8.0F, 0.5F));

		PartDefinition rleg = base.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(60, 65).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, -8.0F, 0.5F));

		PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -22.0F, -12.0F, 28.0F, 27.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 50).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.0F, 1.0F));

		PartDefinition larm = body.addOrReplaceChild("larm", CubeListBuilder.create().texOffs(40, 50).addBox(0.0F, -2.0F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, -16.0F, -0.5F, 0.0F, 0.0F, -0.3927F));

		PartDefinition rarm = body.addOrReplaceChild("rarm", CubeListBuilder.create().texOffs(60, 50).addBox(-5.0F, -2.0F, -2.6F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -16.0F, -0.4F, 0.0F, 0.0F, 0.3927F));

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
		this.applyHeadRotation(pNetHeadYaw, pHeadPitch, pAgeInTicks);
	}

	private void applyHeadRotation(float netHeadYaw, float netHeadPitch, float ageInTicks)
	{
		netHeadPitch = Mth.clamp(netHeadPitch, -25.0F, 45.0F);

		this.head.xRot = netHeadPitch * ((float)Math.PI / 180F);
	}
}