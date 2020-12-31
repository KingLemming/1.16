package cofh.ensorcellation.enchantment;

import cofh.core.enchantment.EnchantmentCoFH;
import cofh.core.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.Random;

import static cofh.core.util.constants.Constants.ARMOR_SLOTS;
import static cofh.core.util.references.EnsorcReferences.*;
import static net.minecraft.enchantment.Enchantments.THORNS;

public class DisplacementEnchantment extends EnchantmentCoFH {

    public static int chance = 20;
    public static boolean mobsAffectPlayers = false;

    public DisplacementEnchantment() {

        super(Rarity.RARE, EnchantmentType.ARMOR_CHEST, ARMOR_SLOTS);
        maxLevel = 3;
    }

    @Override
    public int getMinEnchantability(int level) {

        return 5 + 10 * (level - 1);
    }

    @Override
    protected int maxDelegate(int level) {

        return super.getMinEnchantability(level) + 50;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        Item item = stack.getItem();
        return enable && (super.canApplyAtEnchantingTable(stack) || item instanceof HorseArmorItem || item.isShield(stack, null));
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {

        return super.canApplyTogether(ench) && ench != THORNS && ench != FIRE_REBUKE && ench != FROST_REBUKE;
    }

    // region HELPERS
    @Override
    public void onUserHurt(LivingEntity user, Entity attacker, int level) {

        if (!(attacker instanceof LivingEntity)) {
            return;
        }
        Map.Entry<EquipmentSlotType, ItemStack> stack = EnchantmentHelper.getRandomItemWithEnchantment(DISPLACEMENT, user);
        if (shouldHit(level, user.getRNG())) {
            onHit(user, attacker, level);
            if (stack != null) {
                (stack.getValue()).damageItem(2, user, (entity) -> entity.sendBreakAnimation(stack.getKey()));
            }
        }
    }

    public static void onHit(LivingEntity user, Entity attacker, int level) {

        if (!(attacker instanceof LivingEntity)) {
            return;
        }
        if (user instanceof PlayerEntity || !(attacker instanceof PlayerEntity) || mobsAffectPlayers) {
            Random rand = user.getRNG();
            int radius = 16 * level;
            int bound = radius * 2 + 1;
            BlockPos pos = new BlockPos(attacker.getPosX(), attacker.getPosY(), attacker.getPosZ());
            BlockPos randPos = pos.add(-radius + rand.nextInt(bound), rand.nextInt(8), -radius + rand.nextInt(bound));
            if (attacker.world instanceof ServerWorld && attacker.isNonBoss() && Utils.teleportEntityTo(attacker, randPos)) {
                for (int j = 0; j < 3 * level; ++j) {
                    Utils.spawnParticles(attacker.world, ParticleTypes.PORTAL, attacker.getPosX() + rand.nextDouble(), attacker.getPosY() + 1.0D + rand.nextDouble(), attacker.getPosZ() + rand.nextDouble(), 1, 0, 0, 0, 0);
                    Utils.spawnParticles(attacker.world, ParticleTypes.PORTAL, randPos.getX() + rand.nextDouble(), randPos.getY() + 1.0D + rand.nextDouble(), randPos.getZ() + rand.nextDouble(), 1, 0, 0, 0, 0);
                }
            }
        }
    }

    public static boolean shouldHit(int level, Random rand) {

        return rand.nextInt(100) < chance * level;
    }
    // endregion
}
