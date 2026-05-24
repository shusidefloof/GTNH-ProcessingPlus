package com.gtnh.processingplus.machines;

import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;

/**
 * Primitive Air Intake — passively generates 200 mB of Air per second (~40k mB per 4000 ticks).
 * Slightly below the CSC ASU mode's 50k mB requirement so the machine buffers between runs
 * rather than running continuously. Intended as an early alternative to piping in Air.
 */
public class MTE_PrimitiveAirIntake extends MTEHatchInput {

    private static final int AIR_PER_SECOND = 200;

    public MTE_PrimitiveAirIntake(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 4);
    }

    @Override
    public MTE_PrimitiveAirIntake newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTE_PrimitiveAirIntake(0, mName, mName);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        if (!aBaseMetaTileEntity.isServerSide() || aTimer % 20 != 0) return;
        fill(ForgeDirection.UNKNOWN, Materials.Air.getFluid(AIR_PER_SECOND), true);
    }
}
