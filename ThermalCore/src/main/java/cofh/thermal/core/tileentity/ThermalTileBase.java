package cofh.thermal.core.tileentity;

import cofh.core.energy.EnergyStorageCoFH;
import cofh.core.fluid.FluidStorageCoFH;
import cofh.core.fluid.ManagedTankInv;
import cofh.core.inventory.ItemStorageCoFH;
import cofh.core.inventory.ManagedItemInv;
import cofh.core.item.IAugmentableItem;
import cofh.core.network.packet.client.TileControlPacket;
import cofh.core.network.packet.client.TileRedstonePacket;
import cofh.core.network.packet.client.TileStatePacket;
import cofh.core.tileentity.TileCoFH;
import cofh.core.util.TimeTracker;
import cofh.core.util.Utils;
import cofh.core.util.control.*;
import cofh.core.util.helpers.AugmentDataHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.thermal.core.common.ThermalConfig;
import cofh.thermal.core.util.IThermalInventory;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cofh.core.util.GuiHelper.*;
import static cofh.core.util.StorageGroup.ACCESSIBLE;
import static cofh.core.util.StorageGroup.INTERNAL;
import static cofh.core.util.constants.Constants.*;
import static cofh.core.util.constants.NBTTags.*;
import static cofh.core.util.references.CoreReferences.HOLDING;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public abstract class ThermalTileBase extends TileCoFH implements ISecurableTile, IRedstoneControllableTile, INamedContainerProvider, IThermalInventory {

    protected static final int MIN_PROCESS_TICK = 5;
    protected static final int BASE_PROCESS_TICK = 20;
    protected static final int BASE_ENERGY = 50000;

    protected TimeTracker timeTracker = new TimeTracker();
    protected ManagedItemInv inventory = new ManagedItemInv(this, TAG_ITEM_INV);
    protected ManagedTankInv tankInv = new ManagedTankInv(this, TAG_TANK_INV);
    protected EnergyStorageCoFH energyStorage = new EnergyStorageCoFH(0);

    protected SecurityControlModule securityControl = new SecurityControlModule(this);
    protected RedstoneControlModule redstoneControl = new RedstoneControlModule(this);

    protected List<ItemStorageCoFH> augments = new ArrayList<>();
    protected Set<String> augmentTypes = new ObjectOpenHashSet<>();

    protected ListNBT enchantments = new ListNBT();

    public boolean isActive;
    public boolean wasActive;
    protected FluidStack renderFluid = FluidStack.EMPTY;

    public ThermalTileBase(TileEntityType<?> tileEntityTypeIn) {

        super(tileEntityTypeIn);
    }

    // region BASE PARAMETERS
    protected int getBaseEnergyStorage() {

        return BASE_ENERGY;
    }

    protected int getBaseEnergyXfer() {

        return BASE_PROCESS_TICK * 10;
    }

    protected int getBaseProcessTick() {

        return BASE_PROCESS_TICK;
    }

    protected int getMinProcessTick() {

        return MIN_PROCESS_TICK;
    }
    // endregion

    // TODO: Does this need to exist?
    @Override
    public void remove() {

        super.remove();

        energyCap.invalidate();
        itemCap.invalidate();
        fluidCap.invalidate();
    }

    // region HELPERS
    @Override
    public int invSize() {

        return inventory.getSlots();
    }

    @Override
    public int augSize() {

        return augments.size();
    }

    public ManagedItemInv getItemInv() {

        return inventory;
    }

    public ManagedTankInv getTankInv() {

        return tankInv;
    }

    protected void initHandlers() {

        inventory.initHandlers();
        tankInv.initHandlers();
    }

    protected void updateActiveState(boolean curActive) {

        // TODO: Config time delay
        if (!wasActive && curActive != isActive || wasActive && (timeTracker.hasDelayPassed(world, 40) || timeTracker.notSet())) {
            wasActive = false;
            if (getBlockState().hasProperty(ACTIVE)) {
                world.setBlockState(pos, getBlockState().with(ACTIVE, isActive));
            }
            TileStatePacket.sendToClient(this);
        }
    }

    protected boolean cacheRenderFluid() {

        return false;
    }

    @Override
    public List<? extends ItemStorageCoFH> inputSlots() {

        return inventory.getInputSlots();
    }

    @Override
    public List<? extends FluidStorageCoFH> inputTanks() {

        return tankInv.getInputTanks();
    }

    protected List<? extends ItemStorageCoFH> outputSlots() {

        return inventory.getOutputSlots();
    }

    protected List<? extends FluidStorageCoFH> outputTanks() {

        return tankInv.getOutputTanks();
    }

    protected List<? extends ItemStorageCoFH> internalSlots() {

        return inventory.getInternalSlots();
    }

    protected List<? extends FluidStorageCoFH> internalTanks() {

        return tankInv.getInternalTanks();
    }

    @Override
    public void neighborChanged(Block blockIn, BlockPos fromPos) {

        if (world != null && redstoneControl.isControllable()) {
            redstoneControl.setPower(world.getRedstonePowerFromNeighbors(pos));
            TileRedstonePacket.sendToClient(this);
        }
    }

    @Override
    public void onPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        super.onPlacedBy(worldIn, pos, state, placer, stack);

        enchantments = stack.getEnchantmentTagList();

        updateAugmentState();
        onControlUpdate();
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState) {

        if (!keepItems()) {
            for (int i = 0; i < invSize() - augSize(); ++i) {
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
            }
        }
        if (!ThermalConfig.keepAugments.get()) {
            for (int i = invSize() - augSize(); i < invSize(); ++i) {
                Utils.dropItemStackIntoWorldWithRandomness(inventory.getStackInSlot(i), worldIn, pos);
            }
        }
    }

    @Override
    public ItemStack createItemStackTag(ItemStack stack) {

        CompoundNBT nbt = stack.getOrCreateChildTag(TAG_BLOCK_ENTITY);
        if (keepEnergy()) {
            getEnergyStorage().writeWithParams(nbt);
        }
        if (keepItems()) {
            getItemInv().writeSlotsToNBT(nbt, 0, invSize() - augSize());
        }
        if (ThermalConfig.keepAugments.get() && augSize() > 0) {
            getItemInv().writeSlotsToNBTUnordered(nbt, TAG_AUGMENTS, invSize() - augSize());
            if (stack.getItem() instanceof IAugmentableItem) {
                List<ItemStack> items = augments.stream().map(ItemStorageCoFH::getItemStack).flatMap(Stream::of).collect(Collectors.toList());
                ((IAugmentableItem) stack.getItem()).updateAugmentState(stack, items);
            }
        }
        if (keepFluids()) {
            getTankInv().write(nbt);
        }
        if (ThermalConfig.keepRSControl.get()) {
            redstoneControl().write(nbt);
        }
        if (ThermalConfig.keepSideConfig.get() && this instanceof IReconfigurableTile) {
            ((IReconfigurableTile) this).reconfigControl().write(nbt);
        }
        if (ThermalConfig.keepTransferControl.get() && this instanceof ITransferControllableTile) {
            ((ITransferControllableTile) this).transferControl().write(nbt);
        }
        if (hasSecurity()) {
            securityControl().write(nbt);
        }
        if (!nbt.isEmpty()) {
            stack.setTagInfo(TAG_BLOCK_ENTITY, nbt);
        }
        if (!enchantments.isEmpty()) {
            stack.getOrCreateTag().put(TAG_ENCHANTMENTS, enchantments);
        }
        return super.createItemStackTag(stack);
    }

    protected boolean keepEnergy() {

        return ThermalConfig.keepEnergy.get();
    }

    protected boolean keepFluids() {

        return ThermalConfig.keepFluids.get();
    }

    protected boolean keepItems() {

        return ThermalConfig.keepItems.get();
    }
    // endregion

    // region GUI
    public ItemStorageCoFH getSlot(int slot) {

        return inventory.getSlot(slot);
    }

    public FluidStorageCoFH getTank(int tank) {

        return tankInv.getTank(tank);
    }

    public EnergyStorageCoFH getEnergyStorage() {

        return energyStorage;
    }

    public FluidStack getRenderFluid() {

        return renderFluid;
    }

    public int getScaledDuration() {

        return getScaledDuration(DURATION);
    }

    public int getScaledDuration(int scale) {

        return isActive ? scale : 0;
    }

    public int getScaledProgress() {

        return getScaledProgress(PROGRESS);
    }

    public int getScaledProgress(int scale) {

        return isActive ? scale : 0;
    }

    public int getScaledSpeed() {

        return getScaledSpeed(SPEED);
    }

    public int getScaledSpeed(int scale) {

        return isActive ? scale : 0;
    }

    public int getCurSpeed() {

        return -1;
    }

    public int getMaxSpeed() {

        return -1;
    }

    public double getEfficiency() {

        return -1.0D;
    }

    @Override
    public boolean clearEnergy(int coil) {

        return energyStorage.clear();
    }

    @Override
    public boolean clearSlot(int slot) {

        if (slot >= inventory.getSlots()) {
            return false;
        }
        if (inventory.getSlot(slot).clear()) {
            onInventoryChange(slot);
            return true;
        }
        return false;
    }

    @Override
    public boolean clearTank(int tank) {

        if (tank >= tankInv.getTanks()) {
            return false;
        }
        if (tankInv.getTank(tank).clear()) {
            onTankChange(tank);
            return true;
        }
        return false;
    }
    // endregion

    // region NETWORK
    @Override
    public PacketBuffer getControlPacket(PacketBuffer buffer) {

        super.getControlPacket(buffer);

        securityControl.writeToBuffer(buffer);
        redstoneControl.writeToBuffer(buffer);

        buffer.writeFluidStack(renderFluid);

        return buffer;
    }

    @Override
    public PacketBuffer getGuiPacket(PacketBuffer buffer) {

        super.getGuiPacket(buffer);

        buffer.writeBoolean(isActive);
        buffer.writeFluidStack(renderFluid);

        energyStorage.writeToBuffer(buffer);

        for (int i = 0; i < tankInv.getTanks(); ++i) {
            buffer.writeFluidStack(tankInv.get(i));
        }
        return buffer;
    }

    @Override
    public PacketBuffer getRedstonePacket(PacketBuffer buffer) {

        super.getRedstonePacket(buffer);

        buffer.writeInt(redstoneControl.getPower());

        return buffer;
    }

    @Override
    public PacketBuffer getStatePacket(PacketBuffer buffer) {

        super.getStatePacket(buffer);

        buffer.writeBoolean(isActive);
        buffer.writeFluidStack(renderFluid);

        return buffer;
    }

    @Override
    public void handleControlPacket(PacketBuffer buffer) {

        super.handleControlPacket(buffer);

        securityControl.readFromBuffer(buffer);
        redstoneControl.readFromBuffer(buffer);

        renderFluid = buffer.readFluidStack();
    }

    @Override
    public void handleGuiPacket(PacketBuffer buffer) {

        super.handleGuiPacket(buffer);

        isActive = buffer.readBoolean();
        renderFluid = buffer.readFluidStack();

        energyStorage.readFromBuffer(buffer);

        for (int i = 0; i < tankInv.getTanks(); ++i) {
            tankInv.set(i, buffer.readFluidStack());
        }
    }

    @Override
    public void handleRedstonePacket(PacketBuffer buffer) {

        super.handleRedstonePacket(buffer);

        redstoneControl.setPower(buffer.readInt());
    }

    @Override
    public void handleStatePacket(PacketBuffer buffer) {

        super.handleStatePacket(buffer);

        isActive = buffer.readBoolean();
        renderFluid = buffer.readFluidStack();
    }
    // endregion

    // region NBT
    @Override
    public void read(BlockState state, CompoundNBT nbt) {

        super.read(state, nbt);

        isActive = nbt.getBoolean(TAG_ACTIVE);
        wasActive = nbt.getBoolean(TAG_ACTIVE_TRACK);

        enchantments = nbt.getList(TAG_ENCHANTMENTS, 10);

        inventory.read(nbt);

        if (nbt.contains(TAG_AUGMENTS)) {
            inventory.readSlotsUnordered(nbt.getList(TAG_AUGMENTS, TAG_COMPOUND), invSize() - augSize());
        }
        updateAugmentState();

        tankInv.read(nbt);
        energyStorage.read(nbt);

        securityControl.read(nbt);
        redstoneControl.read(nbt);

        renderFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound(TAG_RENDER_FLUID));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {

        super.write(nbt);

        nbt.putBoolean(TAG_ACTIVE, isActive);
        nbt.putBoolean(TAG_ACTIVE_TRACK, wasActive);

        nbt.put(TAG_ENCHANTMENTS, enchantments);

        inventory.write(nbt);
        tankInv.write(nbt);
        getEnergyStorage().write(nbt);

        securityControl.write(nbt);
        redstoneControl.write(nbt);

        if (!renderFluid.isEmpty()) {
            nbt.put(TAG_RENDER_FLUID, renderFluid.writeToNBT(new CompoundNBT()));
        }
        return nbt;
    }
    // endregion

    // region AUGMENTS
    protected float baseMod = 1.0F;
    protected float energyStorageMod = 1.0F;
    protected float energyXferMod = 1.0F;
    protected float fluidStorageMod = 1.0F;

    /**
     * This should be called AFTER all other slots have been added.
     * Augment slots are added to the INTERNAL inventory category.
     *
     * @param numAugments Number of augment slots to add.
     */
    protected void addAugmentSlots(int numAugments) {

        for (int i = 0; i < numAugments; ++i) {
            ItemStorageCoFH slot = new ItemStorageCoFH(1, AugmentDataHelper::hasAugmentData);
            augments.add(slot);
            inventory.addSlot(slot, INTERNAL);
        }
        ((ArrayList<ItemStorageCoFH>) augments).trimToSize();
    }

    protected void updateAugmentState() {

        resetAttributes();
        for (ItemStorageCoFH slot : augments) {
            ItemStack augment = slot.getItemStack();
            CompoundNBT augmentData = AugmentDataHelper.getAugmentData(augment);
            if (augmentData == null) {
                continue;
            }
            augmentTypes.add(AugmentDataHelper.getAugmentType(augment));
            setAttributesFromAugment(augmentData);
        }
        finalizeAttributes();
    }

    protected void resetAttributes() {

        baseMod = 1.0F;
        energyStorageMod = 1.0F;
        energyXferMod = 1.0F;
        fluidStorageMod = 1.0F;
    }

    protected void setAttributesFromAugment(CompoundNBT augmentData) {

        baseMod = Math.max(getAttributeMod(augmentData, TAG_AUGMENT_BASE_MOD), baseMod);
        energyStorageMod = Math.max(getAttributeMod(augmentData, TAG_AUGMENT_ENERGY_STORAGE), energyStorageMod);
        energyXferMod = Math.max(getAttributeMod(augmentData, TAG_AUGMENT_ENERGY_XFER), energyXferMod);
        fluidStorageMod = Math.max(getAttributeMod(augmentData, TAG_AUGMENT_FLUID_STORAGE), fluidStorageMod);
    }

    protected void finalizeAttributes() {

        float scaleMin = AUG_SCALE_MIN;
        float scaleMax = AUG_SCALE_MAX;

        Map<Enchantment, Integer> encMap = EnchantmentHelper.deserializeEnchantments(enchantments);

        int holding = encMap.getOrDefault(HOLDING, 0);
        float holdingMod = 1 + holding / 2F;

        energyStorageMod = holdingMod * MathHelper.clamp(energyStorageMod, scaleMin, scaleMax);
        energyXferMod = MathHelper.clamp(energyXferMod, scaleMin, scaleMax);
        fluidStorageMod = holdingMod * MathHelper.clamp(fluidStorageMod, scaleMin, scaleMax);

        energyStorage.applyModifiers(getEnergyStorageMod(), getEnergyXferMod());
        for (int i = 0; i < tankInv.getTanks(); ++i) {
            tankInv.getTank(i).applyModifiers(getFluidStorageMod());
        }
    }

    protected float getEnergyStorageMod() {

        return energyStorageMod * baseMod;
    }

    protected float getEnergyXferMod() {

        return energyXferMod * baseMod;
    }

    protected float getFluidStorageMod() {

        return fluidStorageMod * baseMod;
    }

    protected float getAttributeMod(CompoundNBT augmentData, String key) {

        return augmentData.getFloat(key);
    }

    protected float getAttributeModWithDefault(CompoundNBT augmentData, String key, float defaultValue) {

        return augmentData.contains(key) ? augmentData.getFloat(key) : defaultValue;
    }
    // endregion

    // region MODULES
    @Override
    public SecurityControlModule securityControl() {

        return securityControl;
    }

    @Override
    public RedstoneControlModule redstoneControl() {

        return redstoneControl;
    }
    // endregion

    // region CAPABILITIES
    protected LazyOptional<?> energyCap = LazyOptional.empty();
    protected LazyOptional<?> itemCap = LazyOptional.empty();
    protected LazyOptional<?> fluidCap = LazyOptional.empty();

    protected void updateHandlers() {

        LazyOptional<?> prevEnergyCap = energyCap;
        energyCap = energyStorage.getCapacity() > 0 ? LazyOptional.of(() -> energyStorage) : LazyOptional.empty();
        prevEnergyCap.invalidate();

        LazyOptional<?> prevItemCap = itemCap;
        IItemHandler invHandler = inventory.getHandler(ACCESSIBLE);
        itemCap = inventory.hasAccessibleSlots() ? LazyOptional.of(() -> invHandler) : LazyOptional.empty();
        prevItemCap.invalidate();

        LazyOptional<?> prevFluidCap = fluidCap;
        IFluidHandler fluidHandler = tankInv.getHandler(ACCESSIBLE);
        fluidCap = tankInv.hasAccessibleTanks() ? LazyOptional.of(() -> fluidHandler) : LazyOptional.empty();
        prevFluidCap.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        if (cap == CapabilityEnergy.ENERGY && energyStorage.getMaxEnergyStored() > 0) {
            return getEnergyCapability(side);
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventory.hasAccessibleSlots()) {
            return getItemHandlerCapability(side);
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && tankInv.hasAccessibleTanks()) {
            return getFluidHandlerCapability(side);
        }
        return super.getCapability(cap, side);
    }

    protected <T> LazyOptional<T> getEnergyCapability(@Nullable Direction side) {

        if (!energyCap.isPresent() && energyStorage.getCapacity() > 0) {
            energyCap = LazyOptional.of(() -> energyStorage);
        }
        return energyCap.cast();
    }

    protected <T> LazyOptional<T> getItemHandlerCapability(@Nullable Direction side) {

        if (!itemCap.isPresent() && inventory.hasAccessibleSlots()) {
            IItemHandler handler = inventory.getHandler(ACCESSIBLE);
            itemCap = LazyOptional.of(() -> handler);
        }
        return itemCap.cast();
    }

    protected <T> LazyOptional<T> getFluidHandlerCapability(@Nullable Direction side) {

        if (!fluidCap.isPresent() && tankInv.hasAccessibleTanks()) {
            IFluidHandler handler = tankInv.getHandler(ACCESSIBLE);
            fluidCap = LazyOptional.of(() -> handler);
        }
        return fluidCap.cast();
    }
    // endregion

    // region INamedContainerProvider
    @Override
    public ITextComponent getDisplayName() {

        return new StringTextComponent(getType().getRegistryName().getPath());
    }
    // endregion

    // region ITileCallback
    @Override
    public void onInventoryChange(int slot) {

        /* Implicit requirement here that augments always come LAST in slot order.
        This isn't a bad assumption/rule though, as it's a solid way to handle it.*/
        if (slot >= invSize() - augSize()) {
            updateAugmentState();
        }
    }

    @Override
    public void onControlUpdate() {

        updateHandlers();
        callNeighborStateChange();
        TileControlPacket.sendToClient(this);
    }
    // endregion

    // region IConveyableData
    @Override
    public void readConveyableData(PlayerEntity player, CompoundNBT tag) {

        redstoneControl.read(tag);

        onControlUpdate();
    }

    @Override
    public void writeConveyableData(PlayerEntity player, CompoundNBT tag) {

        redstoneControl.write(tag);
    }
    // endregion
}
