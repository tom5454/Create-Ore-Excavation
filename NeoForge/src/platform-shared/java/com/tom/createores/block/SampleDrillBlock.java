package com.tom.createores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.block.IBE;

import com.tom.createores.Registration;
import com.tom.createores.block.entity.SampleDrillBlockEntity;

public class SampleDrillBlock extends Block implements IBE<SampleDrillBlockEntity> {
	public static final MapCodec<SampleDrillBlock> CODEC = simpleCodec(SampleDrillBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	public SampleDrillBlock(Properties p_49224_) {
		super(p_49224_);
	}

	@Override
	public Class<SampleDrillBlockEntity> getBlockEntityClass() {
		return SampleDrillBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SampleDrillBlockEntity> getBlockEntityType() {
		return Registration.SAMPLE_DRILL_TILE.get();
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack p_316304_, BlockState p_316362_, Level level,
			BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (hit.getDirection() == Direction.UP && player.getItemInHand(hand).getItem() instanceof BacktankItem)
			return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

		if (!level.isClientSide && level.getBlockEntity(pos) instanceof SampleDrillBlockEntity be) {
			if (be.clickedWithItem(player, hand))
				return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState p_60503_, Level level, BlockPos pos, Player player,
			BlockHitResult p_60508_) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof SampleDrillBlockEntity be) {
			be.clicked(player);
		}
		return InteractionResult.SUCCESS;
	}
}
