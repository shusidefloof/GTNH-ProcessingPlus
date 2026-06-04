package com.gtnh.processingplus.nei;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import com.gtnh.processingplus.machines.spc.MachineType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;

@SideOnly(Side.CLIENT)
public class SPCMachineIcons {

    private static final int MAX_LOW_TIER = 5; // LV–IV use Machine_{TIER}_{Name}

    public static @Nullable ItemStack getStack(MachineType type, int tier) {
        String name = itemListName(type, tier);
        if (name == null) return null;
        try {
            return ItemList.valueOf(name)
                .get(1);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private static @Nullable String itemListName(MachineType type, int tier) {
        switch (type) {
            case CHEMICAL_REACTOR:
                return tiered("ChemicalReactor", tier);
            case ELECTROLYZER:
                return tiered("Electrolyzer", tier);
            case CHEMICAL_BATH:
                return tiered("ChemicalBath", tier);
            case MIXER:
                return tiered("Mixer", tier);
            case CENTRIFUGE:
                return tier <= MAX_LOW_TIER ? low("Centrifuge", tier) : null;
            case ASSEMBLER:
                return tier <= MAX_LOW_TIER ? low("Assembler", tier) : null;
            case LASER_ENGRAVER:
                return tier <= MAX_LOW_TIER ? low("LaserEngraver", tier) : "PrecisionLaserEngraver" + highTier(tier);
            case ELECTRIC_FURNACE:
                return tier <= MAX_LOW_TIER ? low("E_Furnace", tier) : "ElectricFurnace" + highTier(tier);
            case FORMING_PRESS:
                return tier <= MAX_LOW_TIER ? low("Press", tier) : "FormingPress" + highTier(tier);
            default:
                return null;
        }
    }

    private static String tiered(String base, int tier) {
        return tier <= MAX_LOW_TIER ? low(base, tier) : base + highTier(tier);
    }

    private static String low(String base, int tier) {
        return "Machine_" + lowTier(tier) + "_" + base;
    }

    private static String lowTier(int t) {
        switch (t) {
            case 1:
                return "LV";
            case 2:
                return "MV";
            case 3:
                return "HV";
            case 4:
                return "EV";
            default:
                return "IV";
        }
    }

    private static String highTier(int t) {
        switch (t) {
            case 6:
                return "LuV";
            case 7:
                return "ZPM";
            case 8:
                return "UV";
            case 9:
                return "UHV";
            case 10:
                return "UEV";
            case 11:
                return "UIV";
            default:
                return "UHV";
        }
    }
}
