package net.poob22.normaldm.common.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AnimationState;
import net.poob22.normaldm.common.client.model.animation.FleshBlobAnimations;
import net.poob22.normaldm.common.server.entity.living.FleshBlobEntity;

public class FleshBlobModel<T extends FleshBlobEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart larm;
	private final ModelPart rarm;
	private final ModelPart rleg;
	private final ModelPart lleg;

	public FleshBlobModel(ModelPart root) {
		this.base = root.getChild("base");
		this.head = this.base.getChild("head");
		this.body = this.base.getChild("body");
		this.larm = this.body.getChild("larm");
		this.rarm = this.body.getChild("rarm");
		this.rleg = this.body.getChild("rleg");
		this.lleg = this.body.getChild("lleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(-12.5F, 24.0F, -9.4F));

		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(12.0F, -17.0F, 7.5F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -7.0F, -3.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 14.7F, 2.5F, -0.2182F, 0.3491F, 0.5672F));

		PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(12.0F, -13.0F, 7.5F));

		PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -6.0F, -2.0F, 7.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 10.3F, 0.0F, -1.6474F, -1.21F, -0.0392F));

		PartDefinition larm = body.addOrReplaceChild("larm", CubeListBuilder.create(), PartPose.offset(-3.5F, -3.0F, 0.0F));

		PartDefinition cube_r3 = larm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 26).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 15.0F, -2.5F, 0.0F, 0.0F, 1.5708F));

		PartDefinition rarm = body.addOrReplaceChild("rarm", CubeListBuilder.create(), PartPose.offset(3.5F, -3.0F, 0.0F));

		PartDefinition cube_r4 = rarm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(22, 26).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 9.8F, -0.5F, -0.7476F, 1.4247F, -1.465F));

		PartDefinition rleg = body.addOrReplaceChild("rleg", CubeListBuilder.create(), PartPose.offset(-1.5F, 4.0F, 0.0F));

		PartDefinition cube_r5 = rleg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 26).addBox(0.0F, -9.0F, -2.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 6.0F, 6.5F, -0.1309F, -0.0023F, 1.5535F));

		PartDefinition lleg = body.addOrReplaceChild("lleg", CubeListBuilder.create(), PartPose.offset(1.5F, 4.0F, 0.0F));

		PartDefinition cube_r6 = lleg.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(22, 14).addBox(-1.0F, -9.0F, -2.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 7.0F, 0.5F, 0.0786F, -0.3405F, 1.0337F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(((FleshBlobEntity)entity).throbAnimation, FleshBlobAnimations.throb, ageInTicks);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return base;
	}
}