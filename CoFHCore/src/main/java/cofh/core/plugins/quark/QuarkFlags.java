package cofh.core.plugins.quark;

import cofh.core.util.FlagManager;

import static cofh.core.util.Utils.isModLoaded;
import static cofh.core.util.constants.Constants.ID_QUARK;

public class QuarkFlags {

    private QuarkFlags() {

    }

    public static void setup() {

        if (!isModLoaded(ID_QUARK)) {
            new FlagManager(ID_QUARK);
        }
    }

}
