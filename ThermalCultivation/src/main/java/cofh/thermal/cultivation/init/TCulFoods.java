package cofh.thermal.cultivation.init;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;

import static cofh.core.util.references.CoreReferences.CLARITY;
import static cofh.core.util.references.CoreReferences.PANACEA;
import static net.minecraft.potion.Effects.FIRE_RESISTANCE;
import static net.minecraft.potion.Effects.SPEED;

public class TCulFoods {

    private TCulFoods() {

    }

    public static final Food BELL_PEPPER = new Food.Builder().hunger(1).saturation(0.3F).build();
    public static final Food CHOCOLATE_CAKE = new Food.Builder().hunger(2).saturation(0.2F).build();
    public static final Food COFFEE = new Food.Builder().hunger(0).saturation(0.2F)
            .effect(() -> new EffectInstance(SPEED, 200, 0), 1.0F)
            .setAlwaysEdible()
            .build();
    public static final Food COFFEE_CHERRY = new Food.Builder().hunger(1).saturation(0.1F).build();
    public static final Food CORN = new Food.Builder().hunger(3).saturation(0.6F).build();
    public static final Food DOUGH = new Food.Builder().hunger(2).saturation(0.3F).build();
    public static final Food EGGPLANT = new Food.Builder().hunger(2).saturation(0.3F).build();
    public static final Food FROST_MELON_SLICE = new Food.Builder().hunger(2).saturation(0.3F)
            .effect(() -> new EffectInstance(FIRE_RESISTANCE, 200, 0), 1.0F)
            .setAlwaysEdible()
            .build();
    public static final Food GREEN_BEAN = new Food.Builder().hunger(1).saturation(0.3F).build();
    public static final Food ONION = new Food.Builder().hunger(1).saturation(0.3F).build();
    public static final Food PEANUT = new Food.Builder().hunger(2).saturation(0.3F).build();
    public static final Food RADISH = new Food.Builder().hunger(1).saturation(0.3F).build();
    public static final Food SPICE_CAKE = new Food.Builder().hunger(2).saturation(0.4F)
            .effect(() -> new EffectInstance(PANACEA, 100, 0), 1.0F)
            .setAlwaysEdible()
            .build();
    public static final Food SPINACH = new Food.Builder().hunger(1).saturation(0.3F).build();
    public static final Food STRAWBERRY = new Food.Builder().hunger(2).saturation(0.1F).build();
    public static final Food TOMATO = new Food.Builder().hunger(2).saturation(0.3F).build();
    public static final Food XP_STEW = new Food.Builder().hunger(6).saturation(0.6F)
            .effect(() -> new EffectInstance(CLARITY, 1200, 0), 1.0F)
            .setAlwaysEdible()
            .build();

}
