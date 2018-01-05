package com.xwaffle.universalmarket.config;

import com.xwaffle.universalmarket.UniversalMarket;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

/**
 * Created by Chase(Xwaffle) on 10/15/2017.
 */
public class MarketConfig implements Configurable {

    public Path getFileLocation() {
        return configFile;
    }

    public MarketConfig() {
        setup();
    }

    private Path configFile = Paths.get(UniversalMarket.getInstance().configDir.getParent() + File.separator + "UniversalMarket" + File.separator + "config.conf", new String[0]);
    private ConfigurationLoader<CommentedConfigurationNode> configLoader = ((HoconConfigurationLoader.Builder) HoconConfigurationLoader.builder().setPath(this.configFile)).build();
    private CommentedConfigurationNode configNode;


    public void setup() {
        File configDirectory = new File(UniversalMarket.getInstance().configDir.getParent() + File.separator + "UniversalMarket");
        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        if (!Files.exists(this.configFile, new LinkOption[0])) {
            try {
                Files.createFile(this.configFile, new FileAttribute[0]);
                load();
                populate();
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            load();
        }
    }

    public boolean isExternal() {
        return get().getNode(new Object[]{"Database", "use-external"}).getBoolean();
    }

    public void load() {
        try {
            this.configNode = this.configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.configLoader.save(this.configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populate() {
        get().getNode(new Object[]{"Database", "use-external"}).setValue(false).setComment("Use a SQL server that's not saved to a file.");
        get().getNode(new Object[]{"Database", "ip"}).setValue("1.127.0.0");
        get().getNode(new Object[]{"Database", "port"}).setValue("3306");
        get().getNode(new Object[]{"Database", "username"}).setValue("admin");
        get().getNode(new Object[]{"Database", "password"}).setValue("password");
        get().getNode(new Object[]{"Database", "database-name"}).setValue("market");

        get().getNode(new Object[]{"Market", "total-items-player-can-sell"}).setValue(1).setComment("The maximum number of items a player can have in the market at a time.");
        get().getNode(new Object[]{"Market", "enable-market-expire"}).setValue(true).setComment("Players have a limited amount of time for an item to be sold for.");
        get().getNode(new Object[]{"Market", "time-market-expires"}).setValue(86400000L).setComment("Defaults 1 Day / 24 hours. Measured in milliseconds."); //1 Day.
//        get().getNode(new Object[]{"Market", "f-key-open-market"}).setValue(false).setComment("When a user presses F to 'swap hands' it will instead open the Market.");

        get().getNode(new Object[]{"Market", "tax-to-sell"}).setValue(true).setComment("Tax the user trying to sell an item, based on the price of their item.");
        get().getNode(new Object[]{"Market", "market-tax"}).setValue(0.20).setComment("Take this number and multiply the price by it, the user must pay this much before adding an item to the market.");
        get().getNode(new Object[]{"Market", "pay-to-sell"}).setValue(false).setComment("Enable using the market-price config value.");
        get().getNode(new Object[]{"Market", "market-price"}).setValue(1000).setComment("Instead of taxing, take a flat amount of the user before they can sell.");
        get().getNode(new Object[]{"Market", "use-permissions-to-sell"}).setValue(false).setComment("You can give permissions for the amount of items a user can sell. com.xwaffle.universalmarket.add.3 allows 3 items for the user");


    }

    public CommentedConfigurationNode get() {
        return this.configNode;
    }

}
