package cofh.core.energy;

import cofh.core.util.IResourceStorage;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

import static cofh.core.util.constants.Constants.MAX_CAPACITY;
import static cofh.core.util.constants.NBTTags.*;

/**
 * Implementation of an Energy Storage object. See {@link IEnergyStorage}.
 *
 * @author King Lemming
 */
public class EnergyStorageCoFH implements IEnergyStorage, IResourceStorage, INBTSerializable<CompoundNBT> {

    protected final int baseCapacity;
    protected final int baseReceive;
    protected final int baseExtract;

    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyStorageCoFH(int capacity) {

        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorageCoFH(int capacity, int maxTransfer) {

        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorageCoFH(int capacity, int maxReceive, int maxExtract) {

        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorageCoFH(int capacity, int maxReceive, int maxExtract, int energy) {

        this.baseCapacity = capacity;
        this.baseReceive = maxReceive;
        this.baseExtract = maxExtract;

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    public EnergyStorageCoFH applyModifiers(float storageMod, float transferMod) {

        setCapacity(Math.round(baseCapacity * storageMod));
        setMaxReceive(Math.round(baseReceive * transferMod));
        setMaxExtract(Math.round(baseExtract * transferMod));
        return this;
    }

    public EnergyStorageCoFH setCapacity(int capacity) {

        this.capacity = MathHelper.clamp(capacity, 0, MAX_CAPACITY);
        this.energy = Math.max(0, Math.min(capacity, energy));
        return this;
    }

    public EnergyStorageCoFH setMaxReceive(int maxReceive) {

        this.maxReceive = maxReceive;
        return this;
    }

    public EnergyStorageCoFH setMaxExtract(int maxExtract) {

        this.maxExtract = maxExtract;
        return this;
    }

    public void setEnergyStored(int amount) {

        energy = amount;
        energy = Math.max(0, Math.min(capacity, energy));
    }

    public int getMaxReceive() {

        return maxReceive;
    }

    public int getMaxExtract() {

        return maxExtract;
    }

    public int receiveEnergyOverride(int maxReceive, boolean simulate) {

        int energyReceived = Math.min(capacity - energy, maxReceive);
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    public int extractEnergyOverride(int maxExtract, boolean simulate) {

        int energyExtracted = Math.min(energy, maxExtract);
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    // region NETWORK
    public void readFromBuffer(PacketBuffer buffer) {

        setCapacity(buffer.readInt());
        setEnergyStored(buffer.readInt());
        setMaxExtract(buffer.readInt());
        setMaxReceive(buffer.readInt());
    }

    public void writeToBuffer(PacketBuffer buffer) {

        buffer.writeInt(getMaxEnergyStored());
        buffer.writeInt(getEnergyStored());
        buffer.writeInt(getMaxExtract());
        buffer.writeInt(getMaxReceive());
    }
    // endregion

    // region NBT
    public EnergyStorageCoFH read(CompoundNBT nbt) {

        this.energy = nbt.getInt(TAG_ENERGY);
        return this;
    }

    public CompoundNBT write(CompoundNBT nbt) {

        if (this.capacity <= 0) {
            return nbt;
        }
        nbt.putInt(TAG_ENERGY, energy);
        return nbt;
    }

    public CompoundNBT writeWithParams(CompoundNBT nbt) {

        if (this.capacity <= 0) {
            return nbt;
        }
        nbt.putInt(TAG_ENERGY, energy);
        nbt.putInt(TAG_ENERGY_MAX, baseCapacity);
        nbt.putInt(TAG_ENERGY_RECV, this.maxReceive);
        nbt.putInt(TAG_ENERGY_SEND, this.maxExtract);
        return nbt;
    }

    @Override
    public CompoundNBT serializeNBT() {

        return write(new CompoundNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

        read(nbt);
    }
    // endregion

    // region IEnergyStorage
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {

        return energy;
    }

    @Override
    public int getMaxEnergyStored() {

        return capacity;
    }

    @Override
    public boolean canExtract() {

        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {

        return maxReceive > 0;
    }
    // endregion

    // region IResourceStorage
    @Override
    public void modify(int amount) {

        energy += amount;
        if (energy > capacity) {
            energy = capacity;
        } else if (energy < 0) {
            energy = 0;
        }
    }

    @Override
    public boolean isEmpty() {

        return energy <= 0 && capacity > 0;
    }

    @Override
    public int getCapacity() {

        return getMaxEnergyStored();
    }

    @Override
    public int getStored() {

        return getEnergyStored();
    }

    @Override
    public String getUnit() {

        return "RF";
    }
    // endregion
}
