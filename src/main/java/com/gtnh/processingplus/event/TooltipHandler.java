package com.gtnh.processingplus.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import com.gtnh.processingplus.GTNHProcessingPlus;
import com.gtnh.processingplus.blocks.GTNHPPBlocks;

import gregtech.api.GregTechAPI;

/**
 * Appends an "Added by: Processing Plus" line to every item/block this mod registers directly
 * (casings, metaitems, etc.) plus every multiblock controller it registers on GT5U's shared
 * gt.blockmachines item (identified by MTE ID, since those don't carry our own registry domain).
 * New blocks/items need no extra wiring; new multiblocks just need their MTE ID added to
 * {@link #MULTIBLOCK_IDS} below.
 */
public class TooltipHandler {

    private static final String CREDIT_LINE = EnumChatFormatting.GRAY + "Added by: "
        + EnumChatFormatting.GOLD
        + "Processing Plus";

    // MTE IDs this mod registers via GTNHPPBlocks.registerMachines() — keep in sync with that method.
    private static final Set<Integer> MULTIBLOCK_IDS = new HashSet<>(
        Arrays.asList(
            GTNHPPBlocks.CIDC_ID, // Controlled Isotopic Doping Chamber
            GTNHPPBlocks.HPR_ID, // Hybrid Phase Reactor
            GTNHPPBlocks.SPU_ID, // Subatomic Patterning Unit
            GTNHPPBlocks.SCD_ID, // Supercritical Dryer
            GTNHPPBlocks.DAF_ID, // Dual Atmosphere Furnace
            GTNHPPBlocks.CSTR_ID, // Continuous Stirred Tank Reactor
            31504, // RTG — Radioisotope Thermoelectric Generator
            31505, // AAR — Ammonia Atmosphere Reactor
            31506, // PCV — Polycondensation Vessel
            31507, // CRV — Ceramic Reaction Vessel
            31508, // SPC — Spectral Photolithography Chamber
            31509, // CSC — Cryogenic Separation Column
            31510, // BOF — Basic Oxygen Furnace
            31511, // HPSF — High Pressure Sintering Furnace
            31512, // SPC Bio-Lithography Module
            31513, // SPC Cryo-Stabilization Module
            31514, // SPC Quantum Alignment Module
            31515, // HTRF — High Temperature Reaction Furnace
            31516 // CAC — Cryogenic Annealing Cryostat
        ));

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null || stack.getItem() == null) return;

        if (isOwnRegisteredItem(stack) || isOwnMultiblock(stack)) {
            event.toolTip.add(CREDIT_LINE);
        }
    }

    private boolean isOwnRegisteredItem(ItemStack stack) {
        Item item = stack.getItem();
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
        return id != null && GTNHProcessingPlus.MODID.equals(id.modId);
    }

    private boolean isOwnMultiblock(ItemStack stack) {
        return stack.getItem() == Item.getItemFromBlock(GregTechAPI.sBlockMachines)
            && MULTIBLOCK_IDS.contains(stack.getItemDamage());
    }
}
