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
	private final ModelPart lleg;
	private final ModelPart rleg;

	public FleshBlobModel(ModelPart root) {
		this.base = root.getChild("base");
		this.head = this.base.getChild("head");
		this.body = this.base.getChild("body");
		this.larm = this.base.getChild("larm");
		this.rarm = this.base.getChild("rarm");
		this.lleg = this.base.getChild("lleg");
		this.rleg = this.base.getChild("rleg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-4.0F, -6.0F, 1.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 4.0F, -1.0F, -0.262F, -0.0421F, -0.3378F));

		PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -1.5708F, 0.48F, 0.0F));

		PartDefinition larm = base.addOrReplaceChild("larm", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -4.0F, -3.0F, 0.0F, 0.0F, -0.9163F));

		PartDefinition cube_r2 = larm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(46, 0).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5438F, -1.0332F, 4.0F, -1.1187F, 0.1547F, -0.2663F));

		PartDefinition rarm = base.addOrReplaceChild("rarm", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -6.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -3.0F, -4.0F, 0.0F, 0.0F, 0.9599F));

		PartDefinition lleg = base.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -4.8F, 6.0F, 0.0F, 0.0F, 1.0036F));

		PartDefinition rleg = base.addOrReplaceChild("rleg", CubeListBuilder.create(), PartPose.offset(1.0F, -6.0F, 0.0F));

		PartDefinition cube_r3 = rleg.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -12.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.0F, -0.8F, -0.7849F, 0.0308F, -1.5399F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.throbAnimation, FleshBlobAnimations.throb, ageInTicks);
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