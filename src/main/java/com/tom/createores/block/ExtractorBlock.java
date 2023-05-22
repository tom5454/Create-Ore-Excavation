package com.tom.createores.block;

import static com.tom.createores.block.MultiblockPart.MultiblockMainPart.MultiblockPartType.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.simibubi.create.foundation.block.IBE;

import com.tom.createores.Registration;
import com.tom.createores.block.entity.ExtractorBlockEntity;

public class ExtractorBlock extends MultiblockController implements IBE<ExtractorBlockEntity> {

	private static final MultiblockPartType[][][] LAYOUT = new MultiblockPartType[][][] {
		{
			{BLANK,   BLANK,     BLANK},
			{KINETIC, BLANK,     BLANK},
			{BLANK,   FLUID_OUT, BLANK}
		},
		{
			{BLANK, BLANK, BLANK},
			{BLANK, MAIN,  BLANK},
			{BLANK, BLANK, BLANK}
		}
	};

	public ExtractorBlock(Properties pr) {
		super(pr);
	}

	@Override
	public Class<ExtractorBlockEntity> getBlockEntityClass() {
		return ExtractorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ExtractorBlockEntity> getBlockEntityType() {
		return Registration.EXTRACTOR_TILE.get();
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
	public InteractionResult onActivate(BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult pHit) {
		return level.getBlockEntity(pos, Registration.EXTRACTOR_TILE.get()).map(te -> te.onClick(player, hand)).orElse(InteractionResult.PASS);
	}
}
