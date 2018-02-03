package com.xwaffle.universalmarket.market;

import com.xwaffle.universalmarket.UniversalMarket;
import com.xwaffle.universalmarket.utils.ItemSerialization;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chase(Xwaffle) on 10/16/2017.
 */
public class MarketItem {


    /**
     * This needs to be UNIQUE. Although you may specify the databaseID in the constructor. It's being used when LOADING the data from the DB.
     */
    private int databaseID = -1;
    private ItemStack item;
    private UUID ownerUUID;
    private String ownerName;
    private double price;
    private int marketID = -1;
    private long timeExpires = 0;

    public MarketItem(int databaseID, ItemStack item, UUID ownerUUID, String ownerName, double price, long timeExpires) {
        this.databaseID = databaseID;
        this.item = item;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.price = price;
        this.timeExpires = timeExpires;
    }

    public MarketItem(ItemStack item, UUID ownerUUID, String ownerName, double price, long timeExpires) {
        this.item = item;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.price = price;
    }


    public String toString() {
        return ItemSerialization.serializeItemStack(this.item).get();
    }


    public boolean isExpired() {
        return timeExpires < System.currentTimeMillis();
    }

    public String getExpireTimeFromNow() {
        if (isExpired()) {
            return "Now";
        } else {
            long millis = timeExpires - System.currentTimeMillis();
            long second = (millis / 1000) % 60;
            long minute = (millis / (1000 * 60)) % 60;
            long hour = (millis / (1000 * 60 * 60)) % 24;
            return String.format("%02dh %02dm %02ds", hour, minute, second);

        }
    }

    /**
     * @param data
     */
    public void fromString(String data) {
        this.item = ItemSerialization.deserializeItemStack(data).get();
    }

    public boolean isBlacklisted() {
        return UniversalMarket.getInstance().getMarket().isItemBlacklisted(item.getType());
    }

    public void setDatabaseID(int ID) {
        this.databaseID = ID;
    }

    public int getDatabaseID() {
        return databaseID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getPrice() {
        return price;
    }

    public ItemStack getItem() {
        return this.item.copy();
    }

    public ItemStack getDisplay() {
        ItemStack stack = this.item.copy();
//        String owner = getOwnerName();
        List<Text> lore = new ArrayList<>();
        if (stack.get(Keys.ITEM_LORE).isPresent()) {
            lore = stack.get(Keys.ITEM_LORE).get();
        }

        lore.add(Text.of(""));
        lore.add(Text.of(TextColors.AQUA, "SELLER: ", TextColors.YELLOW, getOwnerName()));
        lore.add(Text.of(TextColors.AQUA, "PRICE: ", TextColors.GOLD, getPrice()));

        if (UniversalMarket.getInstance().getMarket().expireItems()) {
            lore.add(Text.of(TextColors.AQUA, "EXPIRES: ", TextColors.GRAY, getExpireTimeFromNow()));
        }
//        lore.add(Text.of(TextColors.AQUA, "MARKET-ID: ", TextColors.GOLD, UniversalMarket.getInstance().getMarket()));

        stack.offer(Keys.ITEM_LORE, lore);
        net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(stack);

        NBTTagCompound nbt = nmsStack.hasTagCompound() ? nmsStack.getTagCompound() : new NBTTagCompound();
        nbt.setInteger("id", getDatabaseID());
        nmsStack.setTagCompound(nbt);
        return ItemStackUtil.fromNative(nmsStack);
    }

    public void setMarketID(int marketID) {
        this.marketID = marketID;
    }

    public int getMarketID() {
        return marketID;
    }

    /**
     * Delete itself from the Market.
     */
    public void delete() {
        UniversalMarket.getInstance().getMarket().getListings().remove(this);
        UniversalMarket.getInstance().getDatabase().deleteEntry(databaseID);
    }

    public long getExpireTime() {
        return timeExpires;
    }

    public void forceExpire() {
        this.timeExpires = 0;
    }
}
