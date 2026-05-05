package net.poob22.normaldm.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.poob22.normaldm.common.client.model.animation.BarrelNoseAnimations;
import net.poob22.normaldm.common.server.entity.living.BarrelNoseEntity;
import org.jetbrains.annotations.NotNull;

public class BarrelNoseModel<T extends BarrelNoseEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart rleg;
	private final ModelPart lleg;

	public BarrelNoseModel(ModelPart root) {
		this.base = root.getChild("base");
		this.head = this.base.getChild("head");
		this.nose = this.head.getChild("nose");
		this.rleg = this.base.getChild("rleg");
		this.lleg = this.base.getChild("lleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(19, 17).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		PartDefinition stuffed_s_r1 = nose.addOrReplaceChild("stuffed_s_r1", CubeListBuilder.create().texOffs(22, 23).addBox(-3.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition ready_r1 = nose.addOrReplaceChild("ready_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-7.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition rleg = base.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, 2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -7.0F, 0.0F));

		PartDefinition lleg = base.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(8, 20).addBox(-1.0F, 2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -7.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
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
	public void setupAnim(@NotNull T nose, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
		this.animateWalk(BarrelNoseAnimations.walk, limbSwing, limbSwingAmount, 2.0F, 1.0F);
		this.animate(nose.ShootAnimationState, BarrelNoseAnimations.shoot, ageInTicks, 1.0F);
	}

	private void applyHeadRotation(float netHeadYaw, float netHeadPitch, float ageInTicks)
	{
		netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
		netHeadPitch = Mth.clamp(netHeadPitch, -25.0F, 45.0F);

		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = netHeadPitch * ((float)Math.PI / 180F);
	}
}