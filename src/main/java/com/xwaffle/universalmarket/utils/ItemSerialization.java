package com.xwaffle.universalmarket.utils;


import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by Chase(Xwaffle) on 12/18/2017.
 */
public class ItemSerialization {

    public static Optional<String> serializeItemStack(ItemStack item) {
        try {
            StringWriter sink = new StringWriter();
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
            ConfigurationNode node = loader.createEmptyNode();
            node.setValue(TypeToken.of(ItemStack.class), item);
            loader.save(node);
            return Optional.of(sink.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ItemStack> deserializeItemStack(String string) {
        try {
            StringReader source = new StringReader(string);
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
            ConfigurationNode node = loader.load();
            return Optional.of(node.getValue(TypeToken.of(ItemStack.class)));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
