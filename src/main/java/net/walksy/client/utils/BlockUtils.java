// Thanks to the devs at Wurst for this file - it saved me a bit of time :D

package net.walksy.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.walksy.client.WalksyClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BlockUtils {
	private static final MinecraftClient client = WalksyClient.getClient();
	
	private static List<String> ids = new ArrayList<>();
	public static List<String> getIds() {
		return ids;
	}
	private static void addId(String id) {
		ids.add(id);
	}



	public static ActionResult interact(BlockPos pos, Direction dir) {
		Vec3d vec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		return interact(vec,dir);
	}

	public static ActionResult interact(Vec3d vec3d, Direction dir) {
		Vec3i vec3i = new Vec3i((int) vec3d.x, (int) vec3d.y, (int) vec3d.z);
		BlockPos pos = new BlockPos(vec3i);
		BlockHitResult result = new BlockHitResult(vec3d, dir,pos,false);
		return WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player,WalksyClient.getClient().player.getActiveHand(),result);
	}

	public static void initialiseIdList() {
		for (Identifier id : Registry.ITEM.getIds()) {
			addId(id.toString());
		}
	}
	public static boolean hasBlock(BlockPos pos)
	{
		return !WalksyClient.getClient().world.getBlockState(pos).isAir();
	}

	public static Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to)
	{
		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()),
				Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()),
				Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

		Stream<BlockPos> stream = Stream.iterate(min, pos -> {

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			x++;

			if(x > max.getX())
			{
				x = min.getX();
				y++;
			}

			if(y > max.getY())
			{
				y = min.getY();
				z++;
			}

			if(z > max.getZ())
				throw new IllegalStateException("Stream limit didn't work.");

			return new BlockPos(x, y, z);
		});

		int limit = (max.getX() - min.getX() + 1)
				* (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);

		return stream.limit(limit);
	}

	public static BlockState getState(BlockPos pos) {
		return client.world.getBlockState(pos);
	}
	
	public static Block getBlock(BlockPos pos) {
		return getState(pos).getBlock();
	}
	
	// Get the minecraft friendly id.
	public static int getId(BlockPos pos) {
		return Block.getRawIdFromState(getState(pos));
	}
	
	public static String getName(BlockPos pos) {
		return getName(getBlock(pos));
	}
	
	public static String getName(Block block) {
		return Registry.BLOCK.getId(block).toString();
	}
	
	public static Block getBlockFromName(String name) {
		try {
			return Registry.BLOCK.get(new Identifier(name));
			
		} catch(InvalidIdentifierException e) {
			return Blocks.AIR;
		}
	}
	
	public static float getHardness(BlockPos pos) {
		return getState(pos).calcBlockBreakingDelta(client.player, client.world, pos);
	}

	private static void addToArrayIfHasBlock(ArrayList<BlockPos> array, BlockPos pos)
	{
		if (hasBlock(pos) && !isBlockReplaceable(pos))
			array.add(pos);
	}
	public static ArrayList<BlockPos> getClickableNeighbors(BlockPos pos)
	{
		ArrayList<BlockPos> blocks = new ArrayList<>();
		addToArrayIfHasBlock(blocks, pos.add( 1,  0,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0,  1,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0,  0,  1));
		addToArrayIfHasBlock(blocks, pos.add(-1,  0,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0, -1,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0,  0, -1));
		return blocks;
	}

	public static boolean placeBlock(BlockPos pos)
	{
		return placeBlock(pos, getDefaultBlockState());
	}

	public static boolean placeBlock(BlockPos pos, BlockState state) {
		if (hasBlock(pos))
			return false;

		if (!BlockUtils.canPlace(state, pos))
			return false;

		// if there is no clickable neighbors
		ArrayList<BlockPos> neighbors = BlockUtils.getClickableNeighbors(pos);
		if (neighbors.isEmpty())
			return false;

		// find the correct neighbor to click on
		BlockPos neighborToClick = null;
		Direction directionToClick = null;
		Vec3d faceCenterToClick = null;
		for (BlockPos neighbor : neighbors)
		{
			BlockState block = BlockUtils.getBlockState(neighbor);
			Direction correctFace = null;

			// iterate through 6 faces to find the correct face
			for (Direction face : Direction.values())
			{
				if (pos.equals(neighbor.add(face.getVector())))
				{
					correctFace = face;
					break;
				}
			}

			Vec3d faceCenter = Vec3d.ofCenter(neighbor).add(Vec3d.of(correctFace.getVector()).multiply(0.5));

			BlockHitResult hit = WalksyClient.getClient().world.raycastBlock(RotationUtils.getEyesPos(), faceCenter, neighbor, BlockUtils.getBlockState(neighbor).getOutlineShape(WalksyClient.getClient().world, neighbor), BlockUtils.getBlockState(neighbor));
			if (hit == null)
			{
				neighborToClick = neighbor;
				directionToClick = correctFace;
				faceCenterToClick = faceCenter;
				break;
			}
		}

		// if no viable neighbor found
		if (neighborToClick == null)
			return false;

		//CWHACK.getRotationFaker().setServerLookPos(faceCenterToClick);

		ActionResult result = WalksyClient.getClient().interactionManager.interactBlock(WalksyClient.getClient().player, Hand.MAIN_HAND, new BlockHitResult(faceCenterToClick, directionToClick, neighborToClick, false));

		if (result == ActionResult.SUCCESS)
		{
			WalksyClient.getClient().player.swingHand(Hand.MAIN_HAND);
			return true;
		}

		return false;
	}
	public static boolean canPlace(BlockState state, BlockPos pos)
	{
		return WalksyClient.getClient().world.canPlace(state, pos, null);
	}

	public static BlockState getDefaultBlockState()
	{
		return Blocks.STONE.getDefaultState();
	}

	public static boolean isBlockReplaceable(BlockPos pos)
	{
		return getBlockState(pos).getMaterial().isReplaceable();
	}


	public static boolean isAnchorCharged(BlockPos anchor)
	{
		if (!isBlock(Blocks.RESPAWN_ANCHOR, anchor))
			return false;
		try
		{
			return BlockUtils.getBlockState(anchor).get(RespawnAnchorBlock.CHARGES) != 0;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}

	public static boolean isAnchorUncharged(BlockPos anchor) {
		if (!isBlock(Blocks.RESPAWN_ANCHOR, anchor)) {
			return false;
		} else {
			try {
				return (Integer) BlockUtils.getBlockState(anchor).get(RespawnAnchorBlock.CHARGES) == 0;
			} catch (IllegalArgumentException var2) {
				return false;
			}
		}
	}

	public static BlockState getBlockState(BlockPos pos) {
		return WalksyClient.getClient().world.getBlockState(pos);
	}
	public static boolean isBlock(Block block, BlockPos pos) {
		return getBlockState(pos).getBlock() == block;
	}
	
	private static VoxelShape getOutlineShape(BlockPos pos) {
		return getState(pos).getOutlineShape(client.world, pos);
	}
	
	public static Box getBoundingBox(BlockPos pos) {
		return getOutlineShape(pos).getBoundingBox().offset(pos);
	}
	
	public static boolean canBeClicked(BlockPos pos) {
		return getOutlineShape(pos) != VoxelShapes.empty();
	}
	
	public static ArrayList<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
		ArrayList<BlockPos> blocks = new ArrayList<>();
		
		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
		
		// Oh boy, O(n^3) :D
		for(int x = min.getX(); x <= max.getX(); x++)
			for(int y = min.getY(); y <= max.getY(); y++)
				for(int z = min.getZ(); z <= max.getZ(); z++)
					blocks.add(new BlockPos(x, y, z));
				
		return blocks;
	}

	public static Vec3d blockPos(BlockPos pos) {
		return new Vec3d(
			pos.getX(),
			pos.getY(),
			pos.getZ()
		);
	}
}