package com.tom.createores.block;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import com.tom.createores.Registration;
import com.tom.createores.block.MultiblockPart.MultiblockGhostPart;
import com.tom.createores.block.entity.KineticInputBlockEntity;

public class KineticInputBlock extends KineticBlock implements MultiblockGhostPart, IBE<KineticInputBlockEntity> {
	public static final DirectionProperty SHAFT_FACING = DirectionProperty.create("shaft");
	public static final MapCodec<KineticInputBlock> CODEC = simpleCodec(KineticInputBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	public KineticInputBlock(Properties pr) {
		super(pr);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING, SHAFT_FACING});
		super.createBlockStateDefinition(builder);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(SHAFT_FACING).getAxis();
	}

	@Override
	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return true;
	}

	@Override
	public Direction getParentDir(BlockState state) {
		return state.getValue(FACING);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return hasParent(state, level, pos);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
			LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		return !canSurvive(pState, pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : pState;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player pPlayer) {
		var st = super.playerWillDestroy(level, pos, state, pPlayer);
		destroyParent(level, pos, state, pPlayer);
		return st;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack item, BlockState state, Level level,
			BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return onActivate(state, level, pos, player, hand, hit);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hit) {
		return onActivate(state, level, pos, player, hit);
	}

	@Override
	public Class<KineticInputBlockEntity> getBlockEntityClass() {
		return KineticInputBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends KineticInputBlockEntity> getBlockEntityType() {
		return Registration.KINETIC_INPUT_TILE.get();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(SHAFT_FACING);
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.MEDIUM;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
			Player player) {
		return pickBlock(level, pos, state);
	}
}
