package com.tom.createores.item;

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
					System.out.println(pos + " " + type);
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
		for(int x = 0;x<s.getX();x++) {
			for(int y = 0;y<s.getY();y++) {
				for(int z = 0;z<s.getZ();z++) {
					BlockPos pos = place.offset(x, y, z);
					MultiblockPartType type = part.getPartTypeAt(facing, x, y, z);
					if(type == MultiblockPartType.MAIN)lvl.setBlock(pos, pState, 11);
					else {
						BlockState pl = type.getBlockState(part.getBlockRotation(facing, type)).setValue(BlockStateProperties.FACING, part.getGhostDirection(facing, x, y, z));
						lvl.setBlock(pos, pl, 11);
					}
				}
			}
		}
		return true;
	}
}
