package com.tom.createores.block;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import com.simibubi.create.foundation.block.ITE;

import com.tom.createores.Registration;
import com.tom.createores.block.MultiblockPart.MultiblockGhostPart;

public class IOBlock extends BaseEntityBlock implements MultiblockGhostPart, ITE<IOBlockEntity> {
	public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

	public IOBlock(Properties pr) {
		super(pr);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(new Property[]{FACING, TYPE});
		super.createBlockStateDefinition(builder);
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
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player pPlayer) {
		super.playerWillDestroy(level, pos, state, pPlayer);
		destroyParent(level, pos, state, pPlayer);
	}

	public static enum Type implements StringRepresentable {
		ITEM_IN   (() -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY),
		ITEM_OUT  (() -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY),
		FLUID_IN  (() -> CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY),
		FLUID_OUT (() -> CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY),
		ENERGY_IN (() -> CapabilityEnergy.ENERGY),
		ENERGY_OUT(() -> CapabilityEnergy.ENERGY),
		;
		private final String name;
		private final Supplier<Capability<?>> cap;

		private Type(Supplier<Capability<?>> cap) {
			name = name().toLowerCase();
			this.cap = cap;
		}

		public Capability<?> getCap() {
			return cap.get();
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	@Override
	public Class<IOBlockEntity> getTileEntityClass() {
		return IOBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends IOBlockEntity> getTileEntityType() {
		return Registration.IO_TILE.get();
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		return onActivate(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		return pickBlock(state, target, level, pos, player);
	}
}
