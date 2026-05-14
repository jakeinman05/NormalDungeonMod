package net.poob22.normaldm.common.client.model.crescent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.poob22.normaldm.common.client.model.animation.CrescentAnimations;
import net.poob22.normaldm.common.server.entity.living.CrescentEntity;
import org.jetbrains.annotations.NotNull;

public class TallCrescentModel<T extends CrescentEntity> extends HierarchicalModel<T> {
	private final ModelPart base;
	private final ModelPart lleg;
	private final ModelPart l_lower;
	private final ModelPart rleg;
	private final ModelPart r_lower;
	private final ModelPart head;
	private final ModelPart upper_jaw;

	public TallCrescentModel(ModelPart root) {
		this.base = root.getChild("base");
		this.lleg = this.base.getChild("lleg");
		this.l_lower = this.lleg.getChild("l_lower");
		this.rleg = this.base.getChild("rleg");
		this.r_lower = this.rleg.getChild("r_lower");
		this.head = this.base.getChild("head");
		this.upper_jaw = this.head.getChild("upper_jaw");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition lleg = base.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(26, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -26.0F, 0.0F));

		PartDefinition l_lower = lleg.addOrReplaceChild("l_lower", CubeListBuilder.create().texOffs(0, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition rleg = base.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(16, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -26.0F, 0.0F));

		PartDefinition r_lower = rleg.addOrReplaceChild("r_lower", CubeListBuilder.create().texOffs(8, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition head = base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -25.5F, 0.0F));

		PartDefinition upper_jaw = head.addOrReplaceChild("upper_jaw", CubeListBuilder.create().texOffs(0, 14).addBox(-5.0F, -4.0F, -10.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 5.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateWalk(CrescentAnimations.tall_walk, pLimbSwing, pLimbSwingAmount, 1.0F, 1.0F);
		this.animate(pEntity.chargingAnimation, CrescentAnimations.tall_charge_up, pAgeInTicks, 1.0F);
		this.animate(pEntity.shootingAnimation, CrescentAnimations.tall_shooting, pAgeInTicks, 1.0F);
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return base;
	}
}