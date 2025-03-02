package com.tom.createores.client;
// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.block.entity.IDrill;

public class DrillRenderer<T extends BlockEntity & IDrill> extends SafeBlockEntityRenderer<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CreateOreExcavation.MODID, "drill"), "main");
	private static final ResourceLocation SHAFT = new ResourceLocation(CreateOreExcavation.MODID, "textures/entity/shaft.png");
	private final ModelPart head;
	private final ModelPart shaft;
	private final ModelPart rubble;

	public DrillRenderer(BlockEntityRendererProvider.Context dispatcher) {
		ModelPart root = dispatcher.bakeLayer(LAYER_LOCATION);
		this.head = root.getChild("head");
		this.shaft = root.getChild("shaft");
		this.rubble = root.getChild("rubble");
	}

	public static LayerDefinition createModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		head.addOrReplaceChild("Bit4_r1", CubeListBuilder.create().texOffs(20, 26).addBox(-1.0F, 6.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(12, 16).addBox(-1.5F, 4.5F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(12, 10).addBox(-2.0F, 2.5F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 24).addBox(-2.5F, 0.5F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 16).addBox(-5.0F, -2.5F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 16).addBox(3.0F, -2.5F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 10).addBox(-2.0F, -2.5F, -5.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 10).addBox(-2.0F, -2.5F, 3.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-3.0F, -2.5F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

		partdefinition.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition rubble  = partdefinition.addOrReplaceChild("rubble", CubeListBuilder.create(), PartPose.offset(1.0F, 24.0F, 0.0F));

		rubble.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-2.2F, -2.8F, -1.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, -0.2486F, -0.1618F, 0.1217F));

		rubble.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 13).addBox(-8.0F, -3.0F, -1.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, -0.0598F, 0.0156F, -0.1786F));

		rubble.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 6).addBox(-9.0F, -3.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.3412F, 0.1265F, -0.0937F));

		rubble.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.3414F, -0.0741F, 0.2054F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderSafe(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource,
			int pPackedLight, int pPackedOverlay) {
		pPoseStack.pushPose();
		float f = pBlockEntity.getFacing().toYRot();
		pPoseStack.translate(0.5D, 0.5D, 0.5D);
		pPoseStack.mulPose(Axis.YP.rotationDegrees(-f));
		pPoseStack.translate(0, pBlockEntity.getYOffset(), 0);
		pPoseStack.scale(1, -1, -1);

		pPoseStack.pushPose();
		pPoseStack.translate(0, pBlockEntity.getDrillOffset(), 0);
		pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pBlockEntity.getPrevRotation(), pBlockEntity.getRotation())));

		if (pBlockEntity.shouldRenderShaft())
			shaft.render(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(SHAFT)), pPackedLight, pPackedOverlay);

		ItemStack drill = pBlockEntity.getDrill();
		if (!drill.isEmpty()) {
			ResourceLocation rl = BuiltInRegistries.ITEM.getKey(drill.getItem());
			ResourceLocation tex = new ResourceLocation(rl.getNamespace(), "textures/entity/drill/" + rl.getPath() + ".png");
			head.render(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(tex)), pPackedLight, pPackedOverlay);
		}

		pPoseStack.popPose();

		BlockPos below = pBlockEntity.getBelow();
		BlockState state = pBlockEntity.getLevel().getBlockState(below);
		if(pBlockEntity.shouldRenderRubble() && !state.isAir() && !(pBlockEntity.getLevel() instanceof PonderLevel)) {
			try {
				TextureAtlasSprite particle = PlatformClient.getBlockTexture(state, pBlockEntity.getLevel(), below);
				rubble.render(pPoseStack, particle.wrap(pBufferSource.getBuffer(RenderType.cutout())), pPackedLight, pPackedOverlay);
			} catch (Exception e) {}
		}
		pPoseStack.popPose();
	}
}