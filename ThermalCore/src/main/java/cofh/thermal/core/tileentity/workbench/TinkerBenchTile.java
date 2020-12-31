package cofh.thermal.core.tileentity.workbench;

import cofh.core.energy.EnergyStorageCoFH;
import cofh.core.fluid.FluidStorageCoFH;
import cofh.core.fluid.PotionFluid;
import cofh.core.inventory.ItemStorageCoFH;
import cofh.core.util.helpers.AugmentableHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.thermal.core.inventory.container.workbench.TinkerBenchContainer;
import cofh.thermal.core.tileentity.ThermalTileBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

import static cofh.core.util.StorageGroup.INPUT;
import static cofh.core.util.StorageGroup.INTERNAL;
import static cofh.core.util.constants.Constants.*;
import static cofh.core.util.constants.NBTTags.TAG_MODE;
import static cofh.thermal.core.common.ThermalConfig.storageAugments;
import static cofh.thermal.core.init.TCoreReferences.TINKER_BENCH_TILE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class TinkerBenchTile extends ThermalTileBase implements ITickableTileEntity {

    protected ItemStorageCoFH tinkerSlot = new ItemStorageCoFH(1, item -> AugmentableHelper.isAugmentableItem(item) || EnergyHelper.hasEnergyHandlerCap(item) || FluidHelper.hasFluidHandlerCap(item));
    protected ItemStorageCoFH chargeSlot = new ItemStorageCoFH(1, EnergyHelper::hasEnergyHandlerCap);
    protected ItemStorageCoFH tankSlot = new ItemStorageCoFH(1, (item) -> FluidHelper.hasFluidHandlerCap(item) || item.getItem() == Items.POTION);

    protected FluidStorageCoFH tank = new FluidStorageCoFH(TANK_MEDIUM);

    protected static final byte REPLENISH = 0;
    protected static final byte AUGMENT = 1;

    protected byte mode;
    protected boolean pause;

    // TODO: Cap caching? Premature optimization possibly; revisit.
    //    protected LazyOptional<?> tinkerEnergyCap = LazyOptional.empty();
    //    protected LazyOptional<?> tinkerFluidCap = LazyOptional.empty();
    //    protected LazyOptional<?> chargeEnergyCap = LazyOptional.empty();
    //    protected LazyOptional<?> tankFluidCap = LazyOptional.empty();

    public TinkerBenchTile() {

        super(TINKER_BENCH_TILE);

        energyStorage = new EnergyStorageCoFH(getBaseEnergyStorage(), getBaseEnergyXfer());

        inventory.addSlot(tinkerSlot, INTERNAL);
        inventory.addSlot(chargeSlot, INTERNAL);
        inventory.addSlot(tankSlot, INTERNAL);

        tankInv.addTank(tank, INPUT);

        addAugmentSlots(storageAugments);
        initHandlers();
    }

    // region BASE PARAMETERS
    protected int getBaseEnergyStorage() {

        return 500000;
    }

    protected int getBaseEnergyXfer() {

        return 1000;
    }
    // endregion

    @Override
    public void tick() {

        if (redstoneControl.getState()) {
            chargeEnergy();
            fillFluid();
        }
    }

    public void setPause(boolean pause) {

        this.pause = pause;
    }

    public boolean allowAugmentation() {

        return mode == AUGMENT;
    }

    public void toggleTinkerSlotMode() {

        ++mode;
        mode %= 2;
    }

    protected void chargeEnergy() {

        if (!chargeSlot.isEmpty()) {
            int maxTransfer = Math.min(energyStorage.getMaxReceive(), energyStorage.getSpace());
            chargeSlot.getItemStack().getCapability(CapabilityEnergy.ENERGY, null).ifPresent(c -> energyStorage.receiveEnergy(c.extractEnergy(maxTransfer, false), false));
        }
        if (!tinkerSlot.isEmpty() && mode == REPLENISH && !pause) {
            int maxTransfer = Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored());
            tinkerSlot.getItemStack().getCapability(CapabilityEnergy.ENERGY, null).ifPresent(c -> energyStorage.extractEnergy(c.receiveEnergy(maxTransfer, false), false));
        }
    }

    protected void fillFluid() {

        if (!tankSlot.isEmpty()) {
            ItemStack tankStack = tankSlot.getItemStack();
            if (tankStack.getItem() == Items.POTION) {
                FluidStack potion = PotionFluid.getPotionFluidFromItem(BOTTLE_VOLUME, tankStack);
                if (tank.fill(potion, SIMULATE) == BOTTLE_VOLUME) {
                    tank.fill(potion, EXECUTE);
                    tankSlot.setItemStack(new ItemStack(Items.GLASS_BOTTLE));
                }
            } else {
                tankStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(c -> {
                    int toFill = tank.fill(new FluidStack(c.getFluidInTank(0), BUCKET_VOLUME), SIMULATE);
                    if (toFill > 0) {
                        tank.modify(c.drain(toFill, EXECUTE).getAmount());
                        tankSlot.setItemStack(c.getContainer());
                    }
                });
            }
        }
        if (!tinkerSlot.isEmpty() && mode == REPLENISH && !pause) {
            tinkerSlot.getItemStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(c -> {
                tank.drain(c.fill(new FluidStack(tank.getFluidStack(), Math.min(tank.getAmount(), BUCKET_VOLUME)), EXECUTE), EXECUTE);
                tinkerSlot.setItemStack(c.getContainer());
            });
        }
    }

    // region GUI
    @Override
    public boolean canOpenGui() {

        return numPlayersUsing <= 0;
    }
    // endregion

    // region NETWORK
    @Override
    public PacketBuffer getGuiPacket(PacketBuffer buffer) {

        super.getGuiPacket(buffer);

        buffer.writeByte(mode);

        return buffer;
    }

    @Override
    public void handleGuiPacket(PacketBuffer buffer) {

        super.handleGuiPacket(buffer);

        mode = buffer.readByte();
    }
    // endregion

    // region NBT
    @Override
    public void read(BlockState state, CompoundNBT nbt) {

        super.read(state, nbt);

        mode = nbt.getByte(TAG_MODE);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {

        super.write(nbt);

        nbt.putByte(TAG_MODE, mode);

        return nbt;
    }
    // endregion

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {

        return new TinkerBenchContainer(i, world, pos, inventory, player);
    }

}
