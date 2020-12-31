package cofh.ensorcellation.event;

import cofh.core.util.Utils;
import cofh.core.util.constants.NBTTags;
import cofh.core.util.helpers.MathHelper;
import cofh.ensorcellation.enchantment.*;
import cofh.ensorcellation.enchantment.override.FrostWalkerEnchantmentImp;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static cofh.core.util.Utils.getHeldEnchantmentLevel;
import static cofh.core.util.constants.Constants.*;
import static cofh.core.util.references.EnsorcIDs.ID_REACH;
import static cofh.core.util.references.EnsorcIDs.ID_VITALITY;
import static cofh.core.util.references.EnsorcReferences.*;
import static net.minecraft.enchantment.EnchantmentHelper.getMaxEnchantmentLevel;
import static net.minecraft.enchantment.Enchantments.EFFICIENCY;
import static net.minecraft.enchantment.Enchantments.FROST_WALKER;
import static net.minecraft.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.item.Items.*;

@Mod.EventBusSubscriber(modid = ID_ENSORCELLATION)
public class CommonEvents {

    private CommonEvents() {

    }

    // region LIVING EVENTS
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void handleLivingAttackEvent(LivingAttackEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();
        // MAGIC EDGE
        if (attacker instanceof LivingEntity) {
            int encMagicEdge = getHeldEnchantmentLevel((LivingEntity) attacker, MAGIC_EDGE);
            if (encMagicEdge > 0 && !source.isMagicDamage() && source.damageType.equals(DAMAGE_PLAYER)) {
                event.setCanceled(true);
                entity.attackEntityFrom(event.getSource().setDamageBypassesArmor().setMagicDamage(), event.getAmount() + MagicEdgeEnchantment.getExtraDamage(encMagicEdge));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLivingDamageEvent(LivingDamageEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if (attacker instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) attacker;
            // CURSE OF MERCY
            int encMercy = getHeldEnchantmentLevel(living, CURSE_MERCY);
            if (encMercy > 0 && event.getAmount() > entity.getHealth()) {
                event.setAmount(Math.max(0.0F, entity.getHealth() - 1.0F));
            }
        }
    }

    @SubscribeEvent
    public static void handleLivingDeathEvent(LivingDeathEvent event) {

        if (event.isCanceled()) {
            return;
        }
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if (attacker instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) attacker;
            // LEECH
            int encLeech = getHeldEnchantmentLevel(living, LEECH);
            if (encLeech > 0) {
                (living).heal(encLeech);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleLivingDropsEvent(LivingDropsEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();
        if (!(attacker instanceof PlayerEntity) || !event.isRecentlyHit()) {
            return;
        }
        PlayerEntity player = (PlayerEntity) attacker;
        // HUNTER
        int encHunter = getHeldEnchantmentLevel(player, HUNTER);
        if (encHunter > 0 && entity instanceof AnimalEntity) {

            LootTable loottable = entity.world.getServer().getLootTableManager().getLootTableFromLocation(entity.getLootTableResourceLocation());
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) entity.world))
                    .withRandom(entity.world.rand)
                    .withParameter(LootParameters.THIS_ENTITY, entity)
                    .withParameter(LootParameters.field_237457_g_, entity.getPositionVec())
                    .withParameter(LootParameters.DAMAGE_SOURCE, source)
                    .withNullableParameter(LootParameters.KILLER_ENTITY, source.getTrueSource())
                    .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, source.getImmediateSource());
            builder = builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
            loottable.generate(builder.build(LootParameterSets.ENTITY));

            for (int i = 0; i < encHunter; ++i) {
                if (player.getRNG().nextInt(100) < HunterEnchantment.chance) {
                    for (ItemStack stack : loottable.generate(builder.build(LootParameterSets.ENTITY))) {
                        ItemEntity drop = new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), stack);
                        event.getDrops().add(drop);
                    }
                }
            }
        }
        // OUTLAW
        int encDamageVillager = getHeldEnchantmentLevel(player, DAMAGE_VILLAGER);
        if (encDamageVillager > 0 && DamageVillagerEnchantment.validTarget(entity)) {
            int emeraldDrop = MathHelper.nextInt(0, encDamageVillager);
            if (emeraldDrop > 0) {
                ItemStack stack = new ItemStack(EMERALD, emeraldDrop);
                ItemEntity drop = new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), stack);
                event.getDrops().add(drop);
            }
        }
        // VORPAL
        int encVorpal = getHeldEnchantmentLevel(player, VORPAL);
        if (encVorpal > 0) {
            ItemStack itemSkull = ItemStack.EMPTY;
            if (entity.world.rand.nextInt(100) < VorpalEnchantment.headBase + VorpalEnchantment.headLevel * encVorpal) {
                if (entity instanceof ServerPlayerEntity) {
                    PlayerEntity target = (ServerPlayerEntity) event.getEntity();
                    itemSkull = new ItemStack(PLAYER_HEAD);
                    CompoundNBT tag = new CompoundNBT();
                    tag.putString(NBTTags.TAG_SKULL_OWNER, target.getName().getString());
                    itemSkull.setTag(tag);
                } else if (entity instanceof SkeletonEntity) {
                    itemSkull = new ItemStack(SKELETON_SKULL);
                } else if (entity instanceof WitherSkeletonEntity) {
                    itemSkull = new ItemStack(WITHER_SKELETON_SKULL);
                } else if (entity instanceof ZombieEntity) {
                    itemSkull = new ItemStack(ZOMBIE_HEAD);
                } else if (entity instanceof CreeperEntity) {
                    itemSkull = new ItemStack(CREEPER_HEAD);
                }
            }
            if (itemSkull.isEmpty()) {
                return;
            }
            ItemEntity drop = new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), itemSkull);
            drop.setPickupDelay(10);
            event.getDrops().add(drop);
        }
    }

    @SubscribeEvent
    public static void handleLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {

        LivingEntity entity = event.getEntityLiving();

        // REACH
        int encReach = getMaxEnchantmentLevel(REACH, entity);
        ModifiableAttributeInstance reachAttr = entity.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (reachAttr != null) {
            reachAttr.removeModifier(UUID_ENCH_REACH_DISTANCE);
            if (encReach > 0) {
                reachAttr.applyNonPersistentModifier(new AttributeModifier(UUID_ENCH_REACH_DISTANCE, ID_REACH, encReach, ADDITION));
            }
        }
        // VITALITY
        int encVitality = getMaxEnchantmentLevel(VITALITY, entity);
        ModifiableAttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(UUID_ENCH_VITALITY_HEALTH);
            if (encVitality > 0) {
                healthAttr.applyNonPersistentModifier(new AttributeModifier(UUID_ENCH_VITALITY_HEALTH, ID_VITALITY, encVitality * VitalityEnchantment.health, ADDITION));
            }
        }
        // Shield must be ACTIVE; see handleLivingUpdateEvent in ShieldEnchEvents.
        //        // BULWARK
        //        int encBulwark = getMaxEnchantmentLevel(BULWARK, entity);
        //        ModifiableAttributeInstance knockbackResAttr = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        //        if (knockbackResAttr != null) {
        //            knockbackResAttr.removeModifier(UUID_ENCH_BULWARK_KNOCKBACK_RESISTANCE);
        //            if (encBulwark > 0) {
        //                knockbackResAttr.applyNonPersistentModifier(new AttributeModifier(UUID_ENCH_BULWARK_KNOCKBACK_RESISTANCE, ID_REACH, 1.0D, ADDITION));
        //            }
        //        }
        //        // PHALANX
        //        int encPhalanx = getMaxEnchantmentLevel(PHALANX, entity);
        //        ModifiableAttributeInstance moveSpeedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        //        if (moveSpeedAttr != null) {
        //            moveSpeedAttr.removeModifier(UUID_ENCH_PHALANX_MOVEMENT_SPEED);
        //            if (encPhalanx > 0) {
        //                moveSpeedAttr.applyNonPersistentModifier(new AttributeModifier(UUID_ENCH_PHALANX_MOVEMENT_SPEED, ID_PHALANX, PhalanxEnchantment.SPEED * encPhalanx, MULTIPLY_TOTAL));
        //            }
        //        }
    }

    @SubscribeEvent
    public static void handleLivingExperienceDropEvent(LivingExperienceDropEvent event) {

        if (event.isCanceled()) {
            return;
        }
        PlayerEntity player = event.getAttackingPlayer();

        if (player != null) {
            // CURSE OF FOOLISHNESS
            int encFool = getMaxEnchantmentLevel(CURSE_FOOL, player);
            if (encFool > 0) {
                event.setDroppedExperience(0);
                event.setCanceled(true);
                return;
            }
            // EXP BOOST
            int encExpBoost = getMaxEnchantmentLevel(EXP_BOOST, player);
            if (encExpBoost > 0) {
                event.setDroppedExperience(ExpBoostEnchantment.getExp(event.getDroppedExperience(), encExpBoost, player.world.rand));
            }
        }
    }

    @SubscribeEvent
    public static void handleLivingHurtEvent(LivingHurtEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if (!(attacker instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity) attacker;

        int encDamageEnder = getHeldEnchantmentLevel(living, DAMAGE_ENDER);
        if (encDamageEnder > 0 && DamageEnderEnchantment.validTarget(entity)) {
            event.setAmount(event.getAmount() + DamageEnderEnchantment.getExtraDamage(encDamageEnder));
        }
        // TODO: Revisit if Ravagers and Witches are reclassified in future.
        int encDamageIllager = getHeldEnchantmentLevel(living, DAMAGE_ILLAGER);
        if (encDamageIllager > 0 && DamageIllagerEnchantment.validTarget(entity)) {
            event.setAmount(event.getAmount() + DamageIllagerEnchantment.getExtraDamage(encDamageIllager));
        }
        int encDamageVillager = getHeldEnchantmentLevel(living, DAMAGE_VILLAGER);
        if (encDamageVillager > 0 && DamageVillagerEnchantment.validTarget(entity)) {
            event.setAmount(event.getAmount() + DamageVillagerEnchantment.getExtraDamage(encDamageVillager));
        }
        // CAVALIER
        int encCavalier = getHeldEnchantmentLevel(living, CAVALIER);
        if (encCavalier > 0 && living.getRidingEntity() != null) {
            event.setAmount(event.getAmount() * (1 + CavalierEnchantment.damageMult * MathHelper.nextInt(1, encCavalier)));
        }
        // FROST ASPECT
        int encFrostAspect = getHeldEnchantmentLevel(living, FROST_ASPECT);
        if (encFrostAspect > 0) {
            FrostAspectEnchantment.onHit(entity, encFrostAspect);
            // Target check is for additional damage, not effect in general.
            if (FrostAspectEnchantment.validTarget(entity)) {
                event.setAmount(event.getAmount() + FrostAspectEnchantment.getExtraDamage(encFrostAspect));
            }
        }
        // MAGIC EDGE
        int encMagicEdge = getHeldEnchantmentLevel(living, MAGIC_EDGE);
        if (encMagicEdge > 0 && source.isMagicDamage()) {
            MagicEdgeEnchantment.onHit(entity, encMagicEdge);
        }
        // VORPAL
        int encVorpal = getHeldEnchantmentLevel(living, VORPAL);
        if (encVorpal > 0 && entity.world.rand.nextInt(100) < VorpalEnchantment.critBase + VorpalEnchantment.critLevel * encVorpal) {
            event.setAmount(event.getAmount() * VorpalEnchantment.critDamage);
            VorpalEnchantment.onHit(entity, encVorpal);
        }
        // INSTIGATING
        int encInstigating = getHeldEnchantmentLevel(living, INSTIGATING);
        if (encInstigating > 0 && entity.getHealth() >= entity.getMaxHealth()) {
            event.setAmount(event.getAmount() * (1 + encInstigating));
        }
    }

    @SubscribeEvent
    public static void handleLivingUpdateEvent(LivingUpdateEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        // FROST WALKER
        int encFrostWalker = getMaxEnchantmentLevel(FROST_WALKER, entity);
        if (encFrostWalker > 0) {
            FrostWalkerEnchantment.freezeNearby(entity, entity.world, entity.getPosition(), encFrostWalker);
            FrostWalkerEnchantmentImp.freezeNearby(entity, entity.world, entity.getPosition(), encFrostWalker);
        }
    }

    @SubscribeEvent
    public static void handleItemUseFinish(LivingEntityUseItemEvent.Finish event) {

        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof PlayerEntity) || entity instanceof FakePlayer) {
            return;
        }
        Food food = event.getItem().getItem().getFood();
        if (food != null) {
            // GOURMAND
            int encGourmand = getMaxEnchantmentLevel(GOURMAND, entity);
            if (encGourmand > 0 && food != null) {
                int foodLevel = food.getHealing();
                float foodSaturation = food.getSaturation();

                FoodStats playerStats = ((PlayerEntity) entity).getFoodStats();
                int playerFood = playerStats.getFoodLevel();

                playerStats.addStats(foodLevel + encGourmand, foodSaturation + encGourmand * 0.1F);
                playerStats.setFoodLevel(Math.min(playerFood + encGourmand, MAX_FOOD_LEVEL));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleItemFishedEvent(ItemFishedEvent event) {

        if (event.isCanceled()) {
            return;
        }
        FishingBobberEntity hook = event.getHookEntity();
        Entity angler = hook.func_234616_v_();
        if (!(angler instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) angler;
        // ANGLER
        int encAngler = getHeldEnchantmentLevel(player, ANGLER);
        if (encAngler > 0) {
            ItemStack fishingRod = player.getHeldItemMainhand();

            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) hook.world))
                    .withParameter(LootParameters.field_237457_g_, hook.getPositionVec())
                    .withParameter(LootParameters.TOOL, fishingRod)
                    .withRandom(hook.world.rand)
                    .withLuck((float) hook.luck + player.getLuck());
            builder.withParameter(LootParameters.KILLER_ENTITY, player).withParameter(LootParameters.THIS_ENTITY, hook);
            LootTable loottable = hook.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
            List<ItemStack> list = loottable.generate(builder.build(LootParameterSets.FISHING));

            for (int i = 0; i < encAngler; ++i) {
                if (player.getRNG().nextInt(100) < AnglerEnchantment.chance) {
                    list.addAll(loottable.generate(builder.build(LootParameterSets.FISHING)));
                }
            }
            for (ItemStack stack : list) {
                ItemEntity drop = new ItemEntity(hook.world, hook.getPosX(), hook.getPosY(), hook.getPosZ(), stack);
                double d0 = player.getPosX() - hook.getPosX();
                double d1 = player.getPosY() - hook.getPosY();
                double d2 = player.getPosZ() - hook.getPosZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                drop.setMotion(d0 * 0.1D, d1 * 0.1D + Math.sqrt(d3) * 0.08D, d2 * 0.1D);
                hook.world.addEntity(drop);
                if (stack.getItem().isIn(ItemTags.FISHES)) {
                    player.addStat(Stats.FISH_CAUGHT, 1);
                }
            }
        }
        // EXP BOOST
        int encExpBoost = getMaxEnchantmentLevel(EXP_BOOST, player);
        if (encExpBoost > 0) {
            hook.world.addEntity(new ExperienceOrbEntity(player.world, player.getPosX(), player.getPosY() + 0.5D, player.getPosZ() + 0.5D, ExpBoostEnchantment.getExp(0, encExpBoost, player.world.rand)));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void handlePickupXpEvent(PlayerXpEvent.PickupXp event) {

        PlayerEntity player = event.getPlayer();
        ExperienceOrbEntity orb = event.getOrb();

        // CURSE OF FOOLISHNESS
        int encFool = getMaxEnchantmentLevel(CURSE_FOOL, player);
        if (encFool > 0) {
            orb.xpValue = 0;
            orb.remove();
            event.setCanceled(true);
        }
    }
    // endregion

    // region PLAYER INTERACTION
    @SubscribeEvent
    public static void handlePlayerRightClickEvent(PlayerInteractEvent event) {

        if (event.isCanceled()) {
            return;
        }
        if (!(event instanceof PlayerInteractEvent.RightClickItem || event instanceof PlayerInteractEvent.RightClickBlock || event instanceof PlayerInteractEvent.RightClickEmpty)) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        if (player.fishingBobber == null || Utils.isClientWorld(player.world)) {
            return;
        }
        FishingBobberEntity hook = player.fishingBobber;
        Entity entity = hook.func_234607_k_();

        if (entity instanceof PlayerEntity && !PilferingEnchantment.allowPlayerStealing) {
            return;
        }
        int encPilfer = getHeldEnchantmentLevel(player, PILFERING);
        if (encPilfer > 0 && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            ItemStack armor = stealArmor(living);
            if (armor.isEmpty()) {
                return;
            }
            ItemEntity armorEntity = new ItemEntity(living.world, living.getPosX(), living.getPosY() + 0.5D, living.getPosZ(), armor);
            armorEntity.setOwnerId(player.getUniqueID());
            armorEntity.setPickupDelay(5);
            armorEntity.world.addEntity(armorEntity);
            armorEntity.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
        }
    }
    // endregion

    // region BLOCK BREAKING
    @SubscribeEvent
    public static void handleBlockBreakEvent(BlockEvent.BreakEvent event) {

        if (event.isCanceled()) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (event.getExpToDrop() > 0) {
            // CURSE OF FOOLISHNESS
            int encFool = getMaxEnchantmentLevel(CURSE_FOOL, player);
            if (encFool > 0) {
                event.setExpToDrop(0);
                return;
            }
            // EXP BOOST
            int encExpBoost = getMaxEnchantmentLevel(EXP_BOOST, player);
            if (encExpBoost > 0) {
                event.setExpToDrop(ExpBoostEnchantment.getExp(event.getExpToDrop(), encExpBoost, player.world.rand));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleBreakSpeedEvent(PlayerEvent.BreakSpeed event) {

        if (event.isCanceled()) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        // AIR AFFINITY
        int encAirAffinity = getMaxEnchantmentLevel(AIR_AFFINITY, player);
        if (encAirAffinity > 0 && !player.isOnGround()) {
            event.setNewSpeed(Math.max(event.getNewSpeed(), event.getOriginalSpeed() * 5.0F));
        }
        // EXCAVATING
        int encExcavating = getHeldEnchantmentLevel(player, EXCAVATING);
        if (encExcavating > 0) {
            if (!player.isSecondaryUseActive()) {
                event.setNewSpeed(event.getNewSpeed() / 1 + encExcavating);
            }
            int encEfficiency = getHeldEnchantmentLevel(player, EFFICIENCY);
            if (encEfficiency > 1) {
                event.setNewSpeed(event.getNewSpeed() / encEfficiency);
            }
        }
    }
    // endregion

    @SubscribeEvent
    public static void handleTickEndEvent(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            FireRebukeEnchantment.setFireToMobs();
        }
    }

    // region HELPERS
    private static ItemStack stealArmor(LivingEntity living) {

        ItemStack stack = ItemStack.EMPTY;
        for (EquipmentSlotType slot : ARMOR_SLOTS) {
            if (living.getItemStackFromSlot(slot).isEmpty()) {
                continue;
            }
            stack = living.getItemStackFromSlot(slot);
            living.setItemStackToSlot(slot, ItemStack.EMPTY);
            break;
        }
        return stack;
    }
    // endregion
}
