package icbm.classic.content.explosive.handlers;

import icbm.classic.api.IWorldPosition;
import icbm.classic.api.items.IWorldPosItem;
import icbm.classic.lib.transform.vector.Location;
import icbm.classic.lib.transform.vector.Pos;
import icbm.classic.content.entity.EntityExplosive;
import icbm.classic.content.entity.missile.EntityMissile;
import icbm.classic.content.explosive.blast.BlastEnderman;
import icbm.classic.content.explosive.tile.TileEntityExplosive;
import icbm.classic.prefab.tile.EnumTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ExEnder extends Explosion
{
    public ExEnder()
    {
        super("ender", EnumTier.THREE);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (entityPlayer.inventory.getCurrentItem() != null)
        {
            if (entityPlayer.inventory.getCurrentItem().getItem() instanceof IWorldPosItem)
            {
                IWorldPosition link = ((IWorldPosItem) entityPlayer.inventory.getCurrentItem().getItem()).getLocation(entityPlayer.inventory.getCurrentItem());

                if (link instanceof Location)
                {
                    TileEntity tileEntity = world.getTileEntity(pos);

                    if (tileEntity instanceof TileEntityExplosive)
                    {
                        ((Location) link).writeIntNBT(((TileEntityExplosive) tileEntity).nbtData);

                        if (!world.isRemote)
                        {
                            entityPlayer.sendMessage(new TextComponentString("Synced coordinate with " + this.getExplosiveName())); //TODO translate
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean onInteract(EntityMissile missileObj, EntityPlayer entityPlayer, EnumHand hand)
    {
        ItemStack itemStack = entityPlayer.getHeldItem(hand);
        if (itemStack != null)
        {
            if (itemStack.getItem() instanceof IWorldPosItem)
            {
                IWorldPosition link = ((IWorldPosItem) itemStack.getItem()).getLocation(itemStack);

                if (link instanceof Location)
                {
                    ((Location) link).writeIntNBT(missileObj.nbtData);
                    if (!missileObj.world.isRemote)
                    {
                        entityPlayer.sendMessage(new TextComponentString("Synced coordinate with " + this.getMissileName()));
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void doCreateExplosion(World world, BlockPos pos, Entity entity, float scale)
    {
        Pos teleportTarget = null;

        //Get save data
        NBTTagCompound tag = null;
        if (entity instanceof EntityExplosive)
        {
            tag = ((EntityExplosive) entity).getExplosiveData();
        }
        else if (entity instanceof EntityMissile)
        {
            tag = ((EntityMissile) entity).getExplosiveData();
        }

        //Get target from data
        if (tag != null && tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z"))
        {
            teleportTarget = new Pos(tag);
        }

        new BlastEnderman(world, entity, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 30 * scale, teleportTarget).explode();
    }
}
