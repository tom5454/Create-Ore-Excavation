package com.tom.createores.block;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import com.tom.createores.Registration;
import com.tom.createores.util.IOBlockType;

public interface MultiblockPart extends IWrenchable {

	public static NonNullUnaryOperator<BlockBehaviour.Properties> props() {
		return p -> p.strength(10).dynamicShape().noOcclusion();
	}

	public static NonNullUnaryOperator<BlockBehaviour.Properties> propsGhost() {
		return props().andThen(p -> p.noLootTable());
	}

	InteractionResult onActivate(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit);

	ItemStack pickBlock(BlockGetter level, BlockPos pos, BlockState state);

	public static interface MultiblockGhostPart extends MultiblockPart {

		public static UseOnContext makeCtx(UseOnContext context, BlockPos pos) {
			return new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(),
					new BlockHitResult(
							new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
							context.getClickedFace(), pos, false
							)
					);
		}

		@Override
		public default InteractionResult onWrenched(BlockState state, UseOnContext context) {
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() instanceof MultiblockPart) {
				MultiblockPart d = (MultiblockPart) state.getBlock();
				if (d instanceof MultiblockGhostPart)return InteractionResult.PASS;
				else return d.onWrenched(state, makeCtx(context, pos));
			}
			return InteractionResult.PASS;
		}

		@Override
		public default InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() != this && state.getBlock() instanceof MultiblockPart) {
				MultiblockPart d = (MultiblockPart) state.getBlock();
				if (d instanceof MultiblockGhostPart)return InteractionResult.PASS;
				else return d.onSneakWrenched(state, makeCtx(context, pos));
			}
			return InteractionResult.PASS;
		}

		Direction getParentDir(BlockState state);

		public default void destroyParent(Level level, BlockPos pos, BlockState state, Player pPlayer) {
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() instanceof MultiblockPart && !(state.getBlock() instanceof MultiblockGhostPart)) {
				level.destroyBlock(pos, !pPlayer.isCreative());
			}
		}

		public default boolean hasParent(BlockState state, LevelReader level, BlockPos pos) {
			BlockState p = level.getBlockState(pos.relative(getParentDir(state), 1));
			return p.getBlock() instanceof MultiblockPart;
		}

		@Override
		public default InteractionResult onActivate(BlockState state, Level level, BlockPos pos, Player pPlayer, InteractionHand pHand,
				BlockHitResult pHit) {
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() != this && state.getBlock() instanceof MultiblockPart) {
				MultiblockPart d = (MultiblockPart) state.getBlock();
				if (d instanceof MultiblockGhostPart)return InteractionResult.PASS;
				else return d.onActivate(state, level, pos, pPlayer, pHand, new BlockHitResult(
						new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
						pHit.getDirection(), pos, false
						));
			}
			return InteractionResult.PASS;
		}

		@Override
		public default ItemStack pickBlock(BlockGetter level, BlockPos pos, BlockState state) {
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() != this && state.getBlock() instanceof MultiblockPart) {
				MultiblockPart d = (MultiblockPart) state.getBlock();
				if (d instanceof MultiblockGhostPart)return ItemStack.EMPTY;
				else return d.pickBlock(level, pos, state);
			}
			return ItemStack.EMPTY;
		}
	}

	public static interface MultiblockMainPart extends MultiblockPart {
		MultiblockPartType[][][] getMultiblockLayout();

		@Override
		public default InteractionResult onWrenched(BlockState state, UseOnContext context) {
			return InteractionResult.PASS;
		}

		public default Vec3i getSize(Direction facing) {
			MultiblockPartType[][][] layout = getMultiblockLayout();
			int x = layout[0].length;
			int y = layout.length;
			int z = layout[0][0].length;
			return facing.getAxis() == Axis.Z ? new Vec3i(x, y, z) : new Vec3i(z, y, x);
		}

		Vec3i getStart(Direction facing);

		public default MultiblockPartType getPartTypeAt(Direction facing, int x, int y, int z) {
			Vec3i size = getSize(facing);
			switch (facing) {
			case EAST:
			{
				int t = x;
				x = z;
				z = size.getX() - t - 1;
			}
			break;

			case NORTH:
				break;

			case SOUTH:
				x = size.getX() - x - 1;
				z = size.getZ() - z - 1;
				break;

			case WEST:
			{
				int t = x;
				x = size.getZ() - z - 1;
				z = t;
			}
			break;

			case DOWN:
			case UP:
			default:
				return null;
			}
			MultiblockPartType[][][] layout = getMultiblockLayout();
			return layout[y][z][x];
		}

		public default Direction getGhostDirection(Direction facing, int xIn, int yIn, int zIn) {
			MultiblockPartType[][][] layout = getMultiblockLayout();
			for(int x = 0;x<layout[0][0].length;x++) {
				for(int y = 0;y<layout.length;y++) {
					for(int z = 0;z<layout[0].length;z++) {
						if(layout[y][z][x] == MultiblockPartType.MAIN) {
							if (y == yIn) {
								if(x == xIn) {
									if(z > zIn) {
										return Direction.SOUTH;
									} else {
										return Direction.NORTH;
									}
								} else if(x > xIn) {
									return Direction.EAST;
								} else {
									return Direction.WEST;
								}
							} else if(y > yIn) {
								return Direction.UP;
							} else {
								return Direction.DOWN;
							}
						}
					}
				}
			}
			return null;
		}

		public default Direction getBlockRotation(Direction facing, MultiblockPartType part) {
			return facing;
		}

		public static enum MultiblockPartType {
			BLANK(() -> Registration.GHOST_BLOCK.get().defaultBlockState()),
			MAIN(),
			KINETIC(f -> Registration.KINETIC_INPUT.getDefaultState().setValue(KineticInputBlock.SHAFT_FACING, f)),
			ITEM_IN   (IOBlockType.ITEM_IN   ),
			ITEM_OUT  (IOBlockType.ITEM_OUT  ),
			FLUID_IN  (IOBlockType.FLUID_IN  ),
			FLUID_OUT (IOBlockType.FLUID_OUT ),
			;
			private final Function<Direction, BlockState> stateSupplier;

			private MultiblockPartType() {
				this.stateSupplier = null;
			}

			private MultiblockPartType(Supplier<BlockState> place) {
				this.stateSupplier = __ -> place.get();
			}

			private MultiblockPartType(Function<Direction, BlockState> place) {
				this.stateSupplier = place;
			}

			private MultiblockPartType(IOBlockType type) {
				this(() -> Registration.IO_BLOCK.get().defaultBlockState().setValue(IOBlock.TYPE, type));
			}

			public BlockState getBlockState(Direction facing) {
				return stateSupplier.apply(facing);
			}
		}
	}
}
