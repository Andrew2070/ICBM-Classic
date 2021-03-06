package icbm.classic.content.machines.launcher.screen;

import icbm.classic.lib.network.packet.PacketTile;
import icbm.classic.lib.transform.vector.Pos;
import icbm.classic.lib.LanguageUtility;
import icbm.classic.prefab.gui.GuiContainerBase;
import icbm.classic.ICBMClassic;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;

@SideOnly(Side.CLIENT)
public class GuiLauncherScreen extends GuiContainerBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMClassic.DOMAIN, ICBMClassic.GUI_DIRECTORY + "mc_gui_empty_large.png");

    private TileLauncherScreen tileEntity;
    private GuiTextField target_xCoord_field;
    private GuiTextField target_yCoord_field;
    private GuiTextField target_zCoord_field;
    private GuiTextField target_freq_field;
    private GuiTextField lock_height_field;

    private int containerWidth;
    private int containerHeight;

    public GuiLauncherScreen(EntityPlayer player, TileLauncherScreen tileEntity)
    {
        super(new ContainerLaunchScreen(player, tileEntity));
        this.tileEntity = tileEntity;
        ySize = 166;
    }

    /** Adds the buttons (and other controls) to the screen in question. */
    @Override
    public void initGui()
    {
        super.initGui();
        this.target_xCoord_field = new GuiTextField(0, fontRenderer, 110, 37, 45, 12);
        this.target_zCoord_field = new GuiTextField(1, fontRenderer, 110, 52, 45, 12);
        this.target_yCoord_field = new GuiTextField(2, fontRenderer, 110, 67, 45, 12);
        this.lock_height_field = new GuiTextField(3, fontRenderer, 110, 82, 45, 12);
        this.target_freq_field = new GuiTextField(5, fontRenderer, 110, 97, 45, 12);

        this.target_freq_field.setMaxStringLength(4);
        this.target_xCoord_field.setMaxStringLength(6);
        this.target_zCoord_field.setMaxStringLength(6);
        this.target_yCoord_field.setMaxStringLength(3);
        this.lock_height_field.setMaxStringLength(3);

        this.target_freq_field.setText(this.tileEntity.getFrequency() + "");
        this.lock_height_field.setText(this.tileEntity.lockHeight + "");

        if (this.tileEntity.getTarget() == null)
        {
            this.target_xCoord_field.setText(Math.round(this.tileEntity.getPos().getX()) + "");
            this.target_zCoord_field.setText(Math.round(this.tileEntity.getPos().getZ()) + "");
            this.target_yCoord_field.setText("0");
        }
        else
        {
            this.target_xCoord_field.setText(Math.round(this.tileEntity.getTarget().x()) + "");
            this.target_zCoord_field.setText(Math.round(this.tileEntity.getTarget().z()) + "");
            this.target_yCoord_field.setText(Math.round(this.tileEntity.getTarget().y()) + "");
        }
    }

    /** Call this method from you GuiScreen to process the keys into textbox. */
    @Override
    public void keyTyped(char par1, int par2) throws IOException
    {
        super.keyTyped(par1, par2);
        this.target_xCoord_field.textboxKeyTyped(par1, par2);
        this.target_zCoord_field.textboxKeyTyped(par1, par2);

        if (tileEntity.getTier().ordinal() >= 1)
        {
            this.target_yCoord_field.textboxKeyTyped(par1, par2);
            this.lock_height_field.textboxKeyTyped(par1, par2);

            if (tileEntity.getTier().ordinal() > 1)
            {
                this.target_freq_field.textboxKeyTyped(par1, par2);
            }
        }

        try
        {
            Pos newTarget = new Pos(parseInt(this.target_xCoord_field.getText()), max(parseInt(this.target_yCoord_field.getText()), 0), parseInt(this.target_zCoord_field.getText()));

            this.tileEntity.setTarget(newTarget);
            ICBMClassic.packetHandler.sendToServer(new PacketTile("target_C>S", 2, this.tileEntity).addData(this.tileEntity.getTarget().xi(), this.tileEntity.getTarget().yi(), this.tileEntity.getTarget().zi()));
        }
        catch (NumberFormatException e)
        {

        }

        try
        {
            short newFrequency = (short) Math.max(Short.parseShort(this.target_freq_field.getText()), 0);

            this.tileEntity.setFrequency(newFrequency);
            ICBMClassic.packetHandler.sendToServer(new PacketTile("frequency_C>S", 1, this.tileEntity).addData(this.tileEntity.getFrequency()));
        }
        catch (NumberFormatException e)
        {

        }

        try
        {
            short newGaoDu = (short) Math.max(Math.min(Short.parseShort(this.lock_height_field.getText()), Short.MAX_VALUE), 3);

            this.tileEntity.lockHeight = newGaoDu;
            ICBMClassic.packetHandler.sendToServer(new PacketTile("lock_height_C>S", 3, this.tileEntity).addData(this.tileEntity.lockHeight));
        }
        catch (NumberFormatException e)
        {

        }
    }

    /** Args: x, y, buttonClicked */
    @Override
    public void mouseClicked(int par1, int par2, int par3) throws IOException
    {
        super.mouseClicked(par1, par2, par3);
        this.target_xCoord_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
        this.target_zCoord_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);

        if (tileEntity.getTier().ordinal() >= 1)
        {
            this.target_yCoord_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
            this.lock_height_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);

            if (tileEntity.getTier().ordinal() > 1)
            {
                this.target_freq_field.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
            }
        }

    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.target_xCoord_field.drawTextBox();
        this.target_zCoord_field.drawTextBox();

        // Draw the air detonation GUI
        if (tileEntity.getTier().ordinal() >= 1)
        {
            this.target_yCoord_field.drawTextBox();
            this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherscreen.detHeight"), 12, 68, 4210752);

            this.lock_height_field.drawTextBox();
            this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherscreen.lockHeight"), 12, 83, 4210752);

            if (tileEntity.getTier().ordinal() > 1)
            {
                this.target_freq_field.drawTextBox();
                this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.freq"), 12, 98, 4210752);
            }
        }

        this.fontRenderer.drawString("", 45, 6, 4210752);
        this.fontRenderer.drawString("\u00a77" + LanguageUtility.getLocal("gui.launcherscreen.name"), 30, 6, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherscreen.target"), 12, 25, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.XCoord"), 25, 40, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.ZCoord"), 25, 55, 4210752);

        int inaccuracy = 30;

        if (this.tileEntity.launcherBase != null)
        {
            if (this.tileEntity.launcherBase.supportFrame != null)
            {
                inaccuracy = this.tileEntity.launcherBase.supportFrame.getInaccuracy();
            }
        }

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.launcherscreen.inaccuracy").replaceAll("%p", "" + inaccuracy), 12, 113, 4210752);

        // Shows the status of the missile launcher
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.status") + " " + this.tileEntity.getStatus(), 12, 125, 4210752);
        this.fontRenderer.drawString("Energy: " + this.tileEntity.getEnergy() + "/" + this.tileEntity.getEnergyBufferSize(), 12, 150, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.target_xCoord_field.isFocused())
        {
            this.target_xCoord_field.setText(Math.round(this.tileEntity.getTarget().x()) + "");
        }
        if (!this.target_zCoord_field.isFocused())
        {
            this.target_zCoord_field.setText(Math.round(this.tileEntity.getTarget().z()) + "");
        }
        if (!this.target_yCoord_field.isFocused())
        {
            this.target_yCoord_field.setText(Math.round(this.tileEntity.getTarget().y()) + "");
        }

        if (!this.lock_height_field.isFocused())
        {
            this.lock_height_field.setText(this.tileEntity.lockHeight + "");
        }

        if (!this.target_freq_field.isFocused())
        {
            this.target_freq_field.setText(this.tileEntity.getFrequency() + "");
        }
    }
}
