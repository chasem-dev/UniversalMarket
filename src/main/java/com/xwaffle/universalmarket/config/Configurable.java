package com.xwaffle.universalmarket.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * Created by chase on 5/21/2016.
 */
public interface Configurable {
    void setup();

    void load();

    void save();

    void populate();

    CommentedConfigurationNode get();
}
