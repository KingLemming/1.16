package cofh.thermal.core.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static cofh.thermal.core.common.ThermalFlags.*;

public class ThermalConfig {

    private static boolean registered = false;

    public static void register() {

        if (registered) {
            return;
        }
        FMLJavaModLoadingContext.get().getModEventBus().register(ThermalConfig.class);
        registered = true;

        genServerConfig();
        genClientConfig();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    private ThermalConfig() {

    }

    // region CONFIG SPEC
    private static final ForgeConfigSpec.Builder SERVER_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec serverSpec;

    private static final ForgeConfigSpec.Builder CLIENT_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec clientSpec;

    private static void genServerConfig() {

        //        SERVER_CONFIG.push("Machines");
        //
        //        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("Global Options");

        keepEnergy = SERVER_CONFIG
                .comment("If TRUE, most Thermal Blocks will retain Energy when dropped. This setting does not control ALL blocks.")
                .define("Blocks Retain Energy", true);
        keepItems = SERVER_CONFIG
                .comment("If TRUE, most Thermal Blocks will retain Inventory Contents when dropped. This setting does not control ALL blocks.")
                .define("Blocks Retain Inventory", false);
        keepFluids = SERVER_CONFIG
                .comment("If TRUE, most Thermal Blocks will retain Tank Contents when dropped. This setting does not control ALL blocks.")
                .define("Blocks Retain Tank Contents", false);
        keepAugments = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Augments when dropped.")
                .define("Blocks Retain Augments", true);
        keepRSControl = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Redstone Control configuration when dropped.")
                .define("Blocks Retain Redstone Control", true);
        keepSideConfig = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Side configuration when dropped.")
                .define("Blocks Retain Side Configuration", true);
        keepTransferControl = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Transfer Control configuration when dropped.")
                .define("Blocks Retain Transfer Control", true);

        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("Features");

        flagVanillaBlocks = SERVER_CONFIG
                .comment("If TRUE, various 'Vanilla+' Blocks and Recipes are enabled.")
                .define("Vanilla+", true);
        flagRockwool = SERVER_CONFIG
                .comment("If TRUE, Rockwool Blocks and Recipes are enabled.")
                .define("Rockwool", true);

        flagMobBasalz = SERVER_CONFIG
                .comment("If TRUE, the Basalz Mob is enabled.")
                .define("Basalz", true);
        flagMobBlitz = SERVER_CONFIG
                .comment("If TRUE, the Blitz Mob is enabled.")
                .define("Blitz", true);
        flagMobBlizz = SERVER_CONFIG
                .comment("If TRUE, the Blizz Mob is enabled.")
                .define("Blizz", true);

        SERVER_CONFIG.pop();

        genWorldConfig();

        serverSpec = SERVER_CONFIG.build();

        refreshServerConfig();
    }

    private static void genClientConfig() {

        CLIENT_CONFIG.push("Sounds");

        flagMobAmbientSounds = CLIENT_CONFIG
                .comment("If TRUE, 'Thermal Series' Mobs will have ambient sounds.")
                .define("Ambient Mob Sounds", true);

        CLIENT_CONFIG.pop();

        clientSpec = CLIENT_CONFIG.build();

        refreshClientConfig();
    }

    private static void genWorldConfig() {

        SERVER_CONFIG.push("World Generation");

        flagGenApatite = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Apatite from generating.")
                .define("Apatite", true);
        flagGenCinnabar = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Cinnabar from generating.")
                .define("Cinnabar", true);
        flagGenNiter = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Niter from generating.")
                .define("Niter", true);
        flagGenSulfur = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Sulfur from generating.")
                .define("Sulfur", true);

        flagGenCopper = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Copper from generating.")
                .define("Copper", true);
        flagGenTin = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Tin from generating.")
                .define("Tin", true);
        flagGenLead = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Lead from generating.")
                .define("Lead", true);
        flagGenSilver = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Silver from generating.")
                .define("Silver", true);
        flagGenNickel = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Nickel from generating.")
                .define("Nickel", true);

        flagGenOil = SERVER_CONFIG
                .comment("Set to FALSE to prevent 'Thermal Series' Oil Sands from generating.")
                .define("Oil", true);

        SERVER_CONFIG.pop();
    }

    private static void genMachineConfig() {

    }

    private static void refreshServerConfig() {

        setFlag(FLAG_VANILLA_BLOCKS, flagVanillaBlocks.get());
        setFlag(FLAG_ROCKWOOL, flagRockwool.get());

        setFlag(FLAG_MOB_BASALZ, flagMobBasalz.get());
        setFlag(FLAG_MOB_BLITZ, flagMobBlitz.get());
        setFlag(FLAG_MOB_BLIZZ, flagMobBlizz.get());

        refreshWorldConfig();
    }

    private static void refreshWorldConfig() {

        setFlag(FLAG_GEN_APATITE, () -> getFlag(FLAG_RESOURCE_APATITE).getAsBoolean() && flagGenApatite.get());
        setFlag(FLAG_GEN_CINNABAR, () -> getFlag(FLAG_RESOURCE_CINNABAR).getAsBoolean() && flagGenCinnabar.get());
        setFlag(FLAG_GEN_NITER, () -> getFlag(FLAG_RESOURCE_NITER).getAsBoolean() && flagGenNiter.get());
        setFlag(FLAG_GEN_SULFUR, () -> getFlag(FLAG_RESOURCE_SULFUR).getAsBoolean() && flagGenSulfur.get());

        setFlag(FLAG_GEN_COPPER, () -> getFlag(FLAG_RESOURCE_COPPER).getAsBoolean() && flagGenCopper.get());
        setFlag(FLAG_GEN_TIN, () -> getFlag(FLAG_RESOURCE_TIN).getAsBoolean() && flagGenTin.get());
        setFlag(FLAG_GEN_LEAD, () -> getFlag(FLAG_RESOURCE_LEAD).getAsBoolean() && flagGenLead.get());
        setFlag(FLAG_GEN_SILVER, () -> getFlag(FLAG_RESOURCE_SILVER).getAsBoolean() && flagGenSilver.get());
        setFlag(FLAG_GEN_NICKEL, () -> getFlag(FLAG_RESOURCE_NICKEL).getAsBoolean() && flagGenNickel.get());

        setFlag(FLAG_GEN_OIL, () -> getFlag(FLAG_RESOURCE_OIL).getAsBoolean() && flagGenOil.get());
    }

    private static void refreshClientConfig() {

        mobAmbientSounds = flagMobAmbientSounds.get();
    }

    // region GLOBALS
    public static final byte[] DEFAULT_MACHINE_SIDES_RAW = new byte[]{0, 0, 0, 0, 0, 0};
    public static final byte[] DEFAULT_CELL_SIDES_RAW = new byte[]{0, 0, 0, 0, 0, 0};
    // endregion

    // region SERVER VARIABLES
    public static int deviceAugments = 2;
    public static int dynamoAugments = 4;
    public static int machineAugments = 4;
    public static int storageAugments = 2;

    public static int toolAugments = 4;

    public static boolean permanentLava = true;
    public static boolean permanentWater = true;

    public static BooleanValue keepEnergy;
    public static BooleanValue keepItems;
    public static BooleanValue keepFluids;

    public static BooleanValue keepAugments;
    public static BooleanValue keepRSControl;
    public static BooleanValue keepSideConfig;
    public static BooleanValue keepTransferControl;

    private static BooleanValue flagVanillaBlocks;
    private static BooleanValue flagRockwool;

    private static BooleanValue flagMobBasalz;
    private static BooleanValue flagMobBlitz;
    private static BooleanValue flagMobBlizz;

    private static BooleanValue flagGenApatite;
    private static BooleanValue flagGenCinnabar;
    private static BooleanValue flagGenNiter;
    private static BooleanValue flagGenSulfur;

    private static BooleanValue flagGenCopper;
    private static BooleanValue flagGenTin;
    private static BooleanValue flagGenLead;
    private static BooleanValue flagGenSilver;
    private static BooleanValue flagGenNickel;

    private static BooleanValue flagGenOil;

    private static BooleanValue freezePermanentLava;
    private static BooleanValue freezePermanentWater;
    // endregion

    // region CLIENT VARIABLES
    public static boolean jeiBucketTanks = true;

    public static boolean mobAmbientSounds = true;

    public static BooleanValue flagMobAmbientSounds;
    // endregion

    // region CONFIGURATION
    @SubscribeEvent
    public static void configLoading(final net.minecraftforge.fml.config.ModConfig.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public static void configReloading(final net.minecraftforge.fml.config.ModConfig.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
