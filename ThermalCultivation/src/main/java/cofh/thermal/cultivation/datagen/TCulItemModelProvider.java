package cofh.thermal.cultivation.datagen;

import cofh.core.datagen.ItemModelProviderCoFH;
import cofh.core.registries.DeferredRegisterCoFH;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import static cofh.core.util.constants.Constants.ID_THERMAL;
import static cofh.thermal.core.ThermalCore.BLOCKS;
import static cofh.thermal.core.ThermalCore.ITEMS;
import static cofh.thermal.core.util.RegistrationHelper.block;
import static cofh.thermal.core.util.RegistrationHelper.seeds;
import static cofh.thermal.cultivation.init.TCulIDs.*;

public class TCulItemModelProvider extends ItemModelProviderCoFH {

    public TCulItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {

        super(generator, ID_THERMAL, existingFileHelper);
    }

    @Override
    public String getName() {

        return "Thermal Cultivation: Item Models";
    }

    @Override
    protected void registerModels() {

        registerBlockItemModels();

        DeferredRegisterCoFH<Item> reg = ITEMS;

        // CROPS
        generated(reg.getSup(ID_BARLEY), CROPS);
        generated(reg.getSup(ID_BELL_PEPPER), CROPS);
        generated(reg.getSup(ID_COFFEE), CROPS);
        generated(reg.getSup(ID_CORN), CROPS);
        generated(reg.getSup(ID_EGGPLANT), CROPS);
        generated(reg.getSup(ID_GREEN_BEAN), CROPS);
        // generated(reg.getSup(ID_HOPS), CROPS);
        generated(reg.getSup(ID_ONION), CROPS);
        generated(reg.getSup(ID_PEANUT), CROPS);
        generated(reg.getSup(ID_RADISH), CROPS);
        generated(reg.getSup(ID_RICE), CROPS);
        generated(reg.getSup(ID_SADIROOT), CROPS);
        generated(reg.getSup(ID_SPINACH), CROPS);
        generated(reg.getSup(ID_STRAWBERRY), CROPS);
        generated(reg.getSup(ID_TEA), CROPS);
        generated(reg.getSup(ID_TOMATO), CROPS);

        // SEEDS
        generated(reg.getSup(seeds(ID_BARLEY)), SEEDS);
        generated(reg.getSup(seeds(ID_BELL_PEPPER)), SEEDS);
        generated(reg.getSup(seeds(ID_COFFEE)), SEEDS);
        generated(reg.getSup(seeds(ID_CORN)), SEEDS);
        generated(reg.getSup(seeds(ID_EGGPLANT)), SEEDS);
        generated(reg.getSup(seeds(ID_FROST_MELON)), SEEDS);
        generated(reg.getSup(seeds(ID_GREEN_BEAN)), SEEDS);
        // generated(reg.getSup(seeds(ID_HOPS)), SEEDS);
        generated(reg.getSup(seeds(ID_ONION)), SEEDS);
        generated(reg.getSup(seeds(ID_PEANUT)), SEEDS);
        generated(reg.getSup(seeds(ID_RADISH)), SEEDS);
        generated(reg.getSup(seeds(ID_RICE)), SEEDS);
        generated(reg.getSup(seeds(ID_SADIROOT)), SEEDS);
        generated(reg.getSup(seeds(ID_SPINACH)), SEEDS);
        generated(reg.getSup(seeds(ID_STRAWBERRY)), SEEDS);
        generated(reg.getSup(seeds(ID_TEA)), SEEDS);
        generated(reg.getSup(seeds(ID_TOMATO)), SEEDS);

        // FOODS
        //        generated(reg.getSup("dough"), FOODS);
        //        generated(reg.getSup("flour"), FOODS);
        generated(reg.getSup(ID_FROST_MELON_SLICE), FOODS);
        generated(reg.getSup(ID_CHOCOLATE_CAKE), FOODS);
        generated(reg.getSup(ID_SPICE_CAKE), FOODS);
        generated(reg.getSup(ID_XP_STEW), FOODS);
    }

    private void registerBlockItemModels() {

        DeferredRegisterCoFH<Block> reg = BLOCKS;

        blockItem(reg.getSup(block(ID_BARLEY)));
        blockItem(reg.getSup(block(ID_BELL_PEPPER)));
        blockItem(reg.getSup(block(ID_COFFEE)));
        blockItem(reg.getSup(block(ID_CORN)));
        blockItem(reg.getSup(block(ID_EGGPLANT)));
        blockItem(reg.getSup(block(ID_GREEN_BEAN)));
        blockItem(reg.getSup(block(ID_HOPS)));
        blockItem(reg.getSup(block(ID_ONION)));
        blockItem(reg.getSup(block(ID_PEANUT)));
        blockItem(reg.getSup(block(ID_RADISH)));
        blockItem(reg.getSup(block(ID_RICE)));
        blockItem(reg.getSup(block(ID_SADIROOT)));
        blockItem(reg.getSup(block(ID_SPINACH)));
        blockItem(reg.getSup(block(ID_STRAWBERRY)));
        blockItem(reg.getSup(block(ID_TEA)));
        blockItem(reg.getSup(block(ID_TOMATO)));
    }

}
