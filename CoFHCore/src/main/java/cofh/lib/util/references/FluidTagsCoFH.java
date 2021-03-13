package cofh.lib.util.references;

import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

import static cofh.lib.util.constants.Constants.ID_FORGE;
import static cofh.lib.util.constants.Constants.ID_THERMAL;

public class FluidTagsCoFH {

    public static final IOptionalNamedTag<Fluid> HONEY = forgeTag("honey");
    public static final IOptionalNamedTag<Fluid> POTION = forgeTag("potion");
    public static final IOptionalNamedTag<Fluid> XP = forgeTag("xp");

    public static final IOptionalNamedTag<Fluid> LATEX = forgeTag("latex");

    public static final IOptionalNamedTag<Fluid> CREOSOTE = forgeTag("creosote");

    // region HELPERS
    private static IOptionalNamedTag<Fluid> thermalTag(String name) {

        return FluidTags.createOptional(new ResourceLocation(ID_THERMAL, name));
    }

    private static IOptionalNamedTag<Fluid> forgeTag(String name) {

        return FluidTags.createOptional(new ResourceLocation(ID_FORGE, name));
    }
    // endregion
}
