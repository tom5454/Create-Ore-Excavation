package com.tom.createores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.block.IBE;

import com.tom.createores.Registration;
import com.tom.createores.block.entity.SampleDrillBlockEntity;

public class SampleDrillBlock extends Block implements IBE<SampleDrillBlockEntity> {

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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		if (hit.getDirection() == Direction.UP && player.getItemInHand(hand).getItem() instanceof BacktankItem)
			return InteractionResult.PASS;
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof SampleDrillBlockEntity be) {
			be.clicked(player);
		}
		return InteractionResult.SUCCESS;
	}
}
