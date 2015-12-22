/*
 * This file ("ItemBooklet.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://github.com/Ellpeck/ActuallyAdditions/blob/master/README.md
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015 Ellpeck
 */

package ellpeck.actuallyadditions.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ellpeck.actuallyadditions.ActuallyAdditions;
import ellpeck.actuallyadditions.achievement.TheAchievements;
import ellpeck.actuallyadditions.blocks.IHudDisplay;
import ellpeck.actuallyadditions.booklet.BookletUtils;
import ellpeck.actuallyadditions.booklet.EntrySet;
import ellpeck.actuallyadditions.booklet.GuiBooklet;
import ellpeck.actuallyadditions.booklet.InitBooklet;
import ellpeck.actuallyadditions.booklet.page.BookletPage;
import ellpeck.actuallyadditions.inventory.GuiHandler;
import ellpeck.actuallyadditions.items.base.ItemBase;
import ellpeck.actuallyadditions.util.AssetUtil;
import ellpeck.actuallyadditions.util.ModUtil;
import ellpeck.actuallyadditions.util.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

public class ItemBooklet extends ItemBase implements IHudDisplay{

    @SideOnly(Side.CLIENT)
    public static EntrySet forcedEntry;

    public ItemBooklet(String name){
        super(name);
        this.setMaxStackSize(16);
        this.setMaxDamage(0);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        player.openGui(ActuallyAdditions.instance, GuiHandler.GuiTypes.BOOK.ordinal(), world, (int)player.posX, (int)player.posY, (int)player.posZ);

        if(!world.isRemote){
            player.triggerAchievement(TheAchievements.OPEN_BOOKLET.ach);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
        if(player.isSneaking()){
            Block block = world.getBlock(x, y, z);
            ItemStack blockStack = new ItemStack(block, 1, world.getBlockMetadata(x, y, z));
            if(blockStack != null){
                BookletPage page = BookletUtils.getFirstPageForStack(blockStack);
                if(page != null){
                    if(world.isRemote){
                        forcedEntry = new EntrySet(page, page.getChapter(), page.getChapter().entry, InitBooklet.entries.indexOf(page.getChapter().entry)/GuiBooklet.CHAPTER_BUTTONS_AMOUNT+1);
                    }
                    this.onItemRightClick(stack, world, player);
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool){
        list.add(StringUtil.localize("tooltip."+ModUtil.MOD_ID_LOWER+"."+this.getBaseName()+".desc"));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack){
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconReg){
        this.itemIcon = iconReg.registerIcon(ModUtil.MOD_ID_LOWER+":"+this.getBaseName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass){
        return this.itemIcon;
    }

    @Override
    public void displayHud(Minecraft minecraft, EntityPlayer player, ItemStack stack, MovingObjectPosition posHit, Profiler profiler, ScaledResolution resolution){
        if(posHit != null){
            Block block = minecraft.theWorld.getBlock(posHit.blockX, posHit.blockY, posHit.blockZ);
            if(block != null && !block.isAir(minecraft.theWorld, posHit.blockX, posHit.blockY, posHit.blockZ)){
                ItemStack blockStack = new ItemStack(block, 1, minecraft.theWorld.getBlockMetadata(posHit.blockX, posHit.blockY, posHit.blockZ));
                if(blockStack != null){
                    int height = resolution.getScaledHeight()/5*3;
                    if(player.isSneaking()){
                        BookletPage page = BookletUtils.getFirstPageForStack(blockStack);
                        if(page != null){
                            String strg1 = page.getChapter().getLocalizedName();
                            String strg2 = "Page "+page.getID();
                            String strg3 = "Right-Click to open...";

                            AssetUtil.renderStackToGui(page.getChapter().displayStack != null ? page.getChapter().displayStack : new ItemStack(InitItems.itemBooklet), resolution.getScaledWidth()/2-10, height+41, 1F);
                            minecraft.fontRenderer.drawStringWithShadow(EnumChatFormatting.YELLOW+""+EnumChatFormatting.ITALIC+strg1, resolution.getScaledWidth()/2-minecraft.fontRenderer.getStringWidth(strg1)/2, height+20, StringUtil.DECIMAL_COLOR_WHITE);
                            minecraft.fontRenderer.drawStringWithShadow(EnumChatFormatting.YELLOW+""+EnumChatFormatting.ITALIC+strg2, resolution.getScaledWidth()/2-minecraft.fontRenderer.getStringWidth(strg2)/2, height+30, StringUtil.DECIMAL_COLOR_WHITE);
                            minecraft.fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD+strg3, resolution.getScaledWidth()/2-minecraft.fontRenderer.getStringWidth(strg3)/2, height+60, StringUtil.DECIMAL_COLOR_WHITE);
                        }
                        else{
                            String strg = EnumChatFormatting.DARK_RED+"No Info available! Sorry :(";
                            minecraft.fontRenderer.drawStringWithShadow(strg, resolution.getScaledWidth()/2-minecraft.fontRenderer.getStringWidth(strg)/2, height+60, StringUtil.DECIMAL_COLOR_WHITE);
                        }
                    }
                    else{
                        String strg = EnumChatFormatting.DARK_GREEN+""+EnumChatFormatting.ITALIC+"Sneak!";
                        minecraft.fontRenderer.drawStringWithShadow(strg, resolution.getScaledWidth()/2-minecraft.fontRenderer.getStringWidth(strg)/2, height+60, StringUtil.DECIMAL_COLOR_WHITE);
                    }
                }
            }
        }
    }
}
