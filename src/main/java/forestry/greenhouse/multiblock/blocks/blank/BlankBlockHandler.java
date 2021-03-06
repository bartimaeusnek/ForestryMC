/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.greenhouse.multiblock.blocks.blank;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.core.IErrorState;
import forestry.greenhouse.api.greenhouse.IBlankBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockHandler;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockStorage;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;

public class BlankBlockHandler implements IGreenhouseBlockHandler<IBlankBlock, IBlankBlock> {

	private static final BlankBlockHandler INSTANCE = new BlankBlockHandler();

	public static BlankBlockHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRemoveBlock(IGreenhouseBlockStorage storage, IBlankBlock blockToRemove) {
		BlockPos pos = blockToRemove.getPos();
		blockToRemove.invalidate(false);
	}

	@Override
	public IBlankBlock createBlock(IGreenhouseBlockStorage storage, IBlankBlock root, EnumFacing rootFacing, BlockPos position) {
		IBlankBlock blankBlock = new BlankBlock(storage.getProvider(), position, rootFacing, root);
		blankBlock.validate();
		return blankBlock;
	}

	@Override
	public IBlankBlock getBlock(IGreenhouseBlockStorage storage, BlockPos position) {
		IGreenhouseBlock logicBlock = storage.getBlock(position);
		if (logicBlock instanceof IBlankBlock) {
			return (IBlankBlock) logicBlock;
		}
		return null;
	}

	@Override
	public IErrorState checkNeighborBlocks(IGreenhouseBlockStorage storage, IBlankBlock blockToCheck, List<IGreenhouseBlock> newBlocksToCheck) {
		BlockPos position = blockToCheck.getPos();
		if (storage.setBlock(position, blockToCheck)) {
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				IErrorState errorState = checkBlockFacing(storage, blockToCheck, position, facing, newBlocksToCheck);
				if (errorState != null) {
					return errorState;
				}
			}
		}
		return null;
	}

	@Override
	public boolean onCheckPosition(IGreenhouseBlockStorage storage, IBlankBlock rootBlock, BlockPos position, EnumFacing facing, IGreenhouseBlock block, List<IGreenhouseBlock> newBlocksToCheck) {
		if (block == null) {
			newBlocksToCheck.add(createBlock(storage, rootBlock, facing.getOpposite(), position));
		} else if (block instanceof IBlankBlock) {
			// Check is the internal block in the list
			IBlankBlock faceBlock = (IBlankBlock) block;
			faceBlock.setFaceTested(facing.getOpposite(), true);
			rootBlock.setFaceTested(facing, true);
		}
		return false;
	}

	@Override
	public Class<? extends IBlankBlock> getBlockClass() {
		return IBlankBlock.class;
	}

	private IErrorState checkBlockFacing(IGreenhouseBlockStorage storage, IBlankBlock blockToCheck, BlockPos rootPos, EnumFacing facing, List<IGreenhouseBlock> newBlocksToCheck) {
		if (!blockToCheck.isFaceTested(facing)) {
			BlockPos facingPosition = rootPos.offset(facing);
			IGreenhouseProvider provider = storage.getProvider();

			IErrorState errorState = provider.checkPosition(facingPosition);
			if (errorState != null) {
				return errorState;
			}

			IGreenhouseBlock logicBlock = storage.getBlock(facingPosition);
			for (IGreenhouseBlockHandler handler : provider.getHandlers()) {
				if (handler.onCheckPosition(storage, blockToCheck, facingPosition, facing, logicBlock, newBlocksToCheck)) {
					break;
				}
			}
		}
		return null;
	}
}
