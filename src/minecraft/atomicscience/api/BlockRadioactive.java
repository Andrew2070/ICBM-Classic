package atomicscience.api;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import atomicscience.api.poison.PoisonRadiation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRadioactive extends Block
{
	public static int RECOMMENDED_ID = 3768;
	public boolean canSpread = true;
	public float radius = 5;
	public int amplifier = 2;
	public boolean canWalkPoison = true;

	private Icon iconTop;
	private Icon iconBottom;

	public BlockRadioactive(int id, Material material)
	{
		super(id, material);
		this.setTickRandomly(true);
		this.setHardness(0.2F);
		this.setLightValue(0.1F);
	}

	public BlockRadioactive(int id)
	{
		this(id, Material.rock);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		return side == 1 ? this.iconTop : (side == 0 ? iconBottom : this.field_94336_cN);
	}

	@SideOnly(Side.CLIENT)
	public void func_94332_a(IconRegister iconRegister)
	{
		super.func_94332_a(iconRegister);
		this.iconTop = iconRegister.func_94245_a(this.func_94330_A() + "_top");
		this.iconBottom = iconRegister.func_94245_a(this.func_94330_A() + "_bottom");
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (world.rand.nextFloat() > 0.8)
		{
			this.updateTick(world, x, y, z, world.rand);
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		if (!world.isRemote)
		{
			AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - this.radius, y - this.radius, z - this.radius, x + this.radius, y + this.radius, z + this.radius);
			List<EntityLiving> entitiesNearby = world.getEntitiesWithinAABB(EntityLiving.class, bounds);

			for (EntityLiving entity : entitiesNearby)
			{
				PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), (EntityLiving) entity, amplifier);
			}

			if (this.canSpread)
			{
				for (int i = 0; i < 4; ++i)
				{
					int newX = x + rand.nextInt(3) - 1;
					int newY = y + rand.nextInt(5) - 3;
					int newZ = z + rand.nextInt(3) - 1;
					int var10 = world.getBlockId(newX, newY + 1, newZ);

					if (rand.nextFloat() > 0.4 && (world.getBlockId(newX, newY, newZ) == Block.tilledField.blockID || world.getBlockId(newX, newY, newZ) == Block.grass.blockID))
					{
						world.setBlockMetadataWithNotify(newX, newY, newZ, this.blockID, 2);
					}
				}

				if (rand.nextFloat() > 0.85)
				{
					world.setBlockMetadataWithNotify(x, y, z, Block.mycelium.blockID, 2);
				}
			}
		}
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World par1World, int x, int y, int z, Entity par5Entity)
	{
		if (par5Entity instanceof EntityLiving && this.canWalkPoison)
		{
			PoisonRadiation.INSTANCE.poisonEntity(new Vector3(x, y, z), (EntityLiving) par5Entity);
		}
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}
}
