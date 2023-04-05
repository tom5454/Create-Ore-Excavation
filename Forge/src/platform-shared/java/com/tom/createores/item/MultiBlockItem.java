package com.tom.createores.item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.tom.createores.block.MultiblockPart.MultiblockMainPart;
import com.tom.createores.block.MultiblockPart.MultiblockMainPart.MultiblockPartType;

public class MultiBlockItem extends BlockItem {

	public MultiBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
		MultiblockMainPart part = (MultiblockMainPart) getBlock();
		Direction facing = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos place = pContext.getClickedPos().offset(part.getStart(facing));
		Vec3i s = part.getSize(facing);
		for(int x = 0;x<s.getX();x++) {
			for(int y = 0;y<s.getY();y++) {
				for(int z = 0;z<s.getZ();z++) {
					BlockPos pos = place.offset(x, y, z);
					MultiblockPartType type = part.getPartTypeAt(facing, x, y, z);
					if(type != null) {
						if(pContext.getLevel().isOutsideBuildHeight(pos))return false;
						BlockPlaceContext p = BlockPlaceContext.at(pContext, pos.below(), Direction.UP);
						if(p.replacingClickedOnBlock()) {
							p = BlockPlaceContext.at(pContext, pos, Direction.UP);
							if(!p.replacingClickedOnBlock())return false;
						}
						if(!p.canPlace())return false;
						if(!super.canPlace(p, pState))return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
		MultiblockMainPart part = (MultiblockMainPart) getBlock();
		Level lvl = pContext.getLevel();
		Direction facing = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos place = pContext.getClickedPos().offset(part.getStart(facing));
		Vec3i s = part.getSize(facing);
		Map<BlockPos, PlaceAction> placeActions = new HashMap<>();
		for(int x = 0;x<s.getX();x++) {
			for(int y = 0;y<s.getY();y++) {
				for(int z = 0;z<s.getZ();z++) {
					BlockPos pos = place.offset(x, y, z);
					MultiblockPartType type = part.getPartTypeAt(facing, x, y, z);
					if(type == MultiblockPartType.MAIN)lvl.setBlock(pos, pState, 11);
					else {
						Direction f = part.getGhostDirection(facing, x, y, z);
						BlockState pl = type.getBlockState(part.getBlockRotation(facing, type)).setValue(BlockStateProperties.FACING, f);
						placeActions.put(pos, new PlaceAction(placeActions, pos, pl, f));
					}
				}
			}
		}
		List<PlaceAction> pa = new ArrayList<>(placeActions.values());
		pa.forEach(PlaceAction::computeOrder);
		pa.sort(Comparator.naturalOrder());
		pa.forEach(p -> p.place(lvl));
		return true;
	}

	private static class PlaceAction implements Comparable<PlaceAction> {
		private final Map<BlockPos, PlaceAction> placeActions;
		private final BlockPos pos;
		private final BlockState state;
		private final Direction parentDir;
		private int placeOrder = -1;

		public PlaceAction(Map<BlockPos, PlaceAction> placeActions, BlockPos pos, BlockState state, Direction parentDir) {
			this.placeActions = placeActions;
			this.pos = pos;
			this.state = state;
			this.parentDir = parentDir;
		}

		public void computeOrder() {
			if(placeOrder == -1) {
				placeOrder = 0;
				BlockPos p = pos.relative(parentDir);
				PlaceAction a = placeActions.get(p);
				if(a != null) {
					a.computeOrder();
					placeOrder = a.placeOrder + 1;
				}
			}
		}

		@Override
		public int compareTo(PlaceAction o) {
			return Integer.compare(placeOrder, o.placeOrder);
		}

		public void place(Level lvl) {
			lvl.setBlock(pos, state, 11);
		}
	}
}
