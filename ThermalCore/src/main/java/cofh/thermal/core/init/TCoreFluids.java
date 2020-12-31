package cofh.thermal.core.init;

import cofh.thermal.core.fluid.*;

public class TCoreFluids {

    private TCoreFluids() {

    }

    public static void register() {

        //        RedstoneFluid.create("redstone_fluid");
        //        GlowstoneFluid.create("glowstone_fluid");
        //        EnderFluid.create("ender_fluid");

        SapFluid.create();
        SyrupFluid.create();
        ResinFluid.create();
        TreeOilFluid.create();
        LatexFluid.create();

        //        SeedOilFluid.create();
        //        BiocrudeFluid.create();
        //        BiofuelFluid.create();

        CreosoteFluid.create();
        CrudeOilFluid.create();
        HeavyOilFluid.create();
        LightOilFluid.create();
        RefinedFuelFluid.create();
    }

}
