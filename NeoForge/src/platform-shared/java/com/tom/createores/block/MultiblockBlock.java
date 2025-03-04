package com.tom.createores.block;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;

import com.tom.createores.Registration;
import com.tom.createores.block.MultiblockPart.MultiblockGhostPart;
import com.tom.createores.block.entity.MultiblockBlockEntity;

public class MultiblockBlock extends Block implements MultiblockGhostPart, IBE<MultiblockBlockEntity> {
	public static final MapCodec<MultiblockBlock> CODEC = simpleCodec(MultiblockBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}

	public MultiblockBlock(Properties pr) {
		super(pr);
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING});
		super.createBlockStateDefinition(builder);
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
	public Class<MultiblockBlockEntity> getBlockEntityClass() {
		return MultiblockBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MultiblockBlockEntity> getBlockEntityType() {
		return Registration.GHOST_TILE.get();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
			Player player) {
		return pickBlock(level, pos, state);
	}
}
