package com.tom.createores.block;

import static com.tom.createores.block.MultiblockPart.MultiblockMainPart.MultiblockPartType.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;

import com.tom.createores.Registration;
import com.tom.createores.block.entity.DrillBlockEntity;

public class DrillBlock extends MultiblockController implements IBE<DrillBlockEntity> {
	public static final MapCodec<DrillBlock> CODEC = simpleCodec(DrillBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	private static final MultiblockPartType[][][] LAYOUT = new MultiblockPartType[][][] {
		{
			{BLANK,   FLUID_IN, BLANK},
			{KINETIC, BLANK,    BLANK},
			{BLANK,   BLANK,    BLANK}
		},
		{
			{BLANK, BLANK,    BLANK},
			{BLANK, MAIN,     BLANK},
			{BLANK, ITEM_OUT, BLANK}
		}
	};

	public DrillBlock(Properties pr) {
		super(pr);
	}

	@Override
	public Class<DrillBlockEntity> getBlockEntityClass() {
		return DrillBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends DrillBlockEntity> getBlockEntityType() {
		return Registration.DRILL_TILE.get();
	}

	@Override
	public MultiblockPartType[][][] getMultiblockLayout() {
		return LAYOUT;
	}

	@Override
	public Vec3i getStart(Direction facing) {
		return new Vec3i(-1, 0, -1);
	}

	@Override
	public Direction getBlockRotation(Direction facing, MultiblockPartType part) {
		if(part == KINETIC)return facing.getCounterClockWise();
		return facing;
	}

	@Override
	public ItemInteractionResult onActivate(BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult pHit) {
		return level.getBlockEntity(pos, Registration.DRILL_TILE.get()).map(te -> te.onClick(player, hand)).orElse(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
	}

	@Override
	public InteractionResult onActivate(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult pHit) {
		return level.getBlockEntity(pos, Registration.DRILL_TILE.get()).map(te -> te.onClick(player)).orElse(InteractionResult.PASS);
	}
}
