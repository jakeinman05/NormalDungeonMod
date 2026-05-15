package net.poob22.normaldm.common.client.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.poob22.normaldm.common.client.model.animation.FleshGuyAnimations;
import net.poob22.normaldm.common.server.entity.living.FleshGuyEntity;
import org.jetbrains.annotations.NotNull;

public class FleshGuyModel<T extends FleshGuyEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart body;
	private final ModelPart rarm;
	private final ModelPart larm;
	private final ModelPart head;
	private final ModelPart rleg;
	private final ModelPart lleg;

	public FleshGuyModel(ModelPart root) {
		this.base = root.getChild("base");
		this.body = this.base.getChild("body");
		this.rarm = this.body.getChild("rarm");
		this.larm = this.body.getChild("larm");
		this.head = this.body.getChild("head");
		this.rleg = this.base.getChild("rleg");
		this.lleg = this.base.getChild("lleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(-12.0F, 24.0F, -7.0F));

		PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(12.0F, -12.0F, 7.0F));

		PartDefinition rarm = body.addOrReplaceChild("rarm", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -11.0F, 0.0F));

		PartDefinition larm = body.addOrReplaceChild("larm", CubeListBuilder.create().texOffs(46, 0).addBox(0.0F, -1.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -11.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition rleg = base.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(0, 32).addBox(2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, -12.0F, 7.0F));

		PartDefinition lleg = base.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(16, 32).addBox(-6.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(14.0F, -12.0F, 7.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return base;
	}

	@Override
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float pNetHeadYaw, float pHeadPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(FleshGuyAnimations.walk, limbSwing, limbSwingAmount, 1.0F, 10.0F);

		float progress = Mth.lerp(ageInTicks - entity.tickCount, entity.prevArmProgress, entity.armProgress);
		this.larm.xRot = this.larm.xRot - (Mth.DEG_TO_RAD * 80 * progress);
		this.rarm.xRot = this.rarm.xRot - (Mth.DEG_TO_RAD * 80 * progress);

		this.applyHeadRotation(pNetHeadYaw, pHeadPitch);
	}

	private void applyHeadRotation(float netHeadYaw, float netHeadPitch)
	{
		netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
		netHeadPitch = Mth.clamp(netHeadPitch, -25.0F, 45.0F);

		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = netHeadPitch * ((float)Math.PI / 180F);
	}
}