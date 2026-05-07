package com.gtnh.processingplus.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGTNHPPCasings extends ItemBlock {

    public ItemBlockGTNHPPCasings(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.gtnhpp.casings." + stack.getItemDamage();
    }
}
