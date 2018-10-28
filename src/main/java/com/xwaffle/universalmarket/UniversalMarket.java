package com.xwaffle.universalmarket;

import com.google.inject.Inject;
import com.xwaffle.universalmarket.bstats.Metrics;
import com.xwaffle.universalmarket.commands.MarketCommand;
import com.xwaffle.universalmarket.config.MarketConfig;
import com.xwaffle.universalmarket.database.Database;
import com.xwaffle.universalmarket.market.Market;
import com.xwaffle.universalmarket.market.MarketItem;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;
import java.sql.SQLException;

@Plugin(
        id = "universalmarket",
        name = "UniversalMarket",
        description = "A Universal Market",
        authors = {
                "Xwaffle"
        },
        version = "1.3"
)
public class UniversalMarket {

    @Inject
    private Logger logger;


    @Inject
    @DefaultConfig(sharedRoot = true)
    public Path configDir;

    @Inject
    private Game game;


    @SuppressWarnings("unused")
    @Inject
    private Metrics metrics;


    private static UniversalMarket instance;
    private Database database;
    private static MarketConfig config;
    private Market market;


    @Listener
    public void onAboutToStart(GameAboutToStartServerEvent event) {
        instance = this;
        config = new MarketConfig();
        database = new Database(getConfig().isExternal());
        game.getCommandManager().register(this, new MarketCommand(), "universalmarket", "market", "um");

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        market = database.loadMarket();

        if (UniversalMarket.getInstance().getMarket().expireItems()) {
            Sponge.getScheduler().createTaskBuilder().async().intervalTicks(20 * 5).execute(() -> {
                for (MarketItem marketItem : getMarket().getListings()) {
                    if (marketItem.isExpired()) {
                        if (Sponge.getServer().getPlayer(marketItem.getOwnerUUID()).isPresent()) {
                            Player player = Sponge.getServer().getPlayer(marketItem.getOwnerUUID()).get();
                            player.sendMessage(Text.of(TextColors.DARK_GRAY, "Your item did not sell, and has expired in the market."));
                            player.getInventory().offer(marketItem.getItem());
                            marketItem.delete();
                            return;
                        }
                    }
                }
            }).submit(this);
        }
    }


    @Listener
    public void onServerStopping(GameStoppingServerEvent event) throws SQLException {
        this.database.getConnection().close();
    }

    private EconomyService economyService;

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    public static UniversalMarket getInstance() {
        return instance;
    }

    public static MarketConfig getConfig() {
        return config;
    }

    public Logger getLogger() {
        return logger;
    }

    public Database getDatabase() {
        return database;
    }

    public Market getMarket() {
        return market;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

}
