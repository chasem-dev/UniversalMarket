package com.xwaffle.universalmarket.utils;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Chase(Xwaffle) on 12/22/2017.
 */
public class ItemBuilder {

    public ItemStack itemStack;


    public ItemBuilder(ItemType itemType) {
        this(itemType, 1);
    }

    public ItemBuilder(ItemType itemType, int ammount) {
        this(itemType, ammount, 0);
    }

    public ItemBuilder(ItemType itemType, int ammount, int meta) {
        itemStack = ItemStack.of(itemType, ammount);
        DataContainer container = itemStack.toContainer();
        container.set(DataQuery.of("UnsafeDamage"), meta);
        itemStack.setRawData(container);
    }

    public ItemBuilder setName(Text name) {
        itemStack.offer(Keys.DISPLAY_NAME, name);
        return this;
    }

    public ItemBuilder setLore(List<Text> lore) {
        itemStack.offer(Keys.ITEM_LORE, lore);
        return this;
    }

    public ItemBuilder setLore(Text... text) {
        itemStack.offer(Keys.ITEM_LORE, Arrays.asList(text));
        return this;
    }

    public ItemBuilder setLore(String... strings) {

        List<Text> lore = new ArrayList<>();

        for (String string : strings) {
            lore.add(Text.of(string));
        }


        itemStack.offer(Keys.ITEM_LORE, lore);
        return this;
    }

    public ItemBuilder getNBT(Consumer<NBTTagCompound> consumer) {
        net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);
        consumer.accept(nmsStack.hasTagCompound() ? nmsStack.getTagCompound() : new NBTTagCompound());
        return this;
    }

    public ItemBuilder setNBT(NBTTagCompound newNBT) {
        net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);
        nmsStack.setTagCompound(newNBT);
        itemStack = ItemStackUtil.fromNative(nmsStack);
        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        itemStack.offer(Keys.DYE_COLOR, color);
        return this;
    }


    public ItemStack build() {
        return itemStack;
    }

    public ItemBuilder addTag(String key, String value) {
        net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);
        NBTTagCompound nbt = nmsStack.hasTagCompound() ? nmsStack.getTagCompound() : new NBTTagCompound();
        nbt.setString(key, value);
        nmsStack.setTagCompound(nbt);
        itemStack = ItemStackUtil.fromNative(nmsStack);
        return this;
    }
}
