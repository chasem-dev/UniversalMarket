package com.xwaffle.universalmarket.market;

import com.xwaffle.universalmarket.UniversalMarket;
import com.xwaffle.universalmarket.utils.InventoryBuilder;
import com.xwaffle.universalmarket.utils.ItemBuilder;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.common.item.inventory.adapter.impl.slots.SlotAdapter;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chase(Xwaffle) on 10/16/2017.
 */
public class Market {
    private boolean useTax = true;
    private boolean payFlatPrice = false;
    private int totalItemsCanSell = 1;
    private boolean expireItems = true;
    private long expireTime = 86400000L;
    private double tax = 0.20; //20% of the item.
    private int flatPrice = 1000;
    private boolean usePermissionToSell = false;

    private boolean useFKey = false;

    public boolean useTax() {
        return useTax;
    }

    public boolean payFlatPrice() {
        return payFlatPrice;
    }

    public int getTotalItemsCanSell() {
        return totalItemsCanSell;
    }

    public boolean expireItems() {
        return expireItems;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public double getTax() {
        return tax;
    }

    public int getFlatPrice() {
        return flatPrice;
    }

    public Market() {
        this.totalItemsCanSell = UniversalMarket.getConfig().get().getNode("Market", "total-items-player-can-sell").getInt();
        this.expireItems = UniversalMarket.getConfig().get().getNode("Market", "enable-market-expire").getBoolean();
        this.expireTime = UniversalMarket.getConfig().get().getNode("Market", "time-market-expires").getLong();
        this.useTax = UniversalMarket.getConfig().get().getNode("Market", "tax-to-sell").getBoolean();
        this.tax = UniversalMarket.getConfig().get().getNode("Market", "market-tax").getDouble();
        this.payFlatPrice = UniversalMarket.getConfig().get().getNode("Market", "pay-to-sell").getBoolean();
        this.flatPrice = UniversalMarket.getConfig().get().getNode("Market", "market-price").getInt();
        this.usePermissionToSell = UniversalMarket.getConfig().get().getNode("Market", "use-permissions-to-sell").getBoolean();

        this.useFKey = UniversalMarket.getConfig().get().getNode("Market", "f-key-open-market").getBoolean();

    }

    List<MarketItem> marketItems = new ArrayList<>();

//    private int marketID = -1;

    public void addItem(MarketItem marketItem, boolean addToDB) {
        if (addToDB) {
            //Adds Item to DB.
            int id = UniversalMarket.getInstance().getDatabase().createEntry(marketItem.getItem(), marketItem.getOwnerUUID(), marketItem.getOwnerName(), marketItem.getPrice(), marketItem.getExpireTime());
            marketItem.setDatabaseID(id);
        }

//        marketItem.setMarketID(++marketID);
        //Adds Item to Local Cache.
        marketItems.add(marketItem);
    }

    public void removeItem(MarketItem item) {
        removeItem(item.getDatabaseID());
    }

    public void removeItem(int databaseID) {

        getMarketItem(databaseID);

        try {
            Statement statement = UniversalMarket.getInstance().getDatabase().getConnection().createStatement();
            statement.execute("DELETE FROM items WHERE ID=" + databaseID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MarketItem getMarketItem(int databaseID) {
        for (MarketItem marketItem : marketItems) {
            if (marketItem.getDatabaseID() == databaseID) {
                return marketItem;
            }
        }
        return null;
    }

    public boolean doesItemExist(int databaseID) {
        return getMarketItem(databaseID) != null;
    }

    public List<MarketItem> getListings() {
        return this.marketItems;
    }

    public void openMarket(Player player) {
        openMarket(player, 1);
    }

    public void openMarket(Player player, int page) {
        Inventory inv = new InventoryBuilder("Universal Market | " + (page), 6) {
            @Override
            public void onClickInventoryEvent(ClickInventoryEvent e) {
                e.setCancelled(true);

                boolean isLeftClick = e instanceof ClickInventoryEvent.Primary;
                boolean isRightClick = e instanceof ClickInventoryEvent.Secondary;
                if (!(e instanceof ClickInventoryEvent.Shift) && e.getTransactions().size() != 0) {
                    int slotClicked = ((SlotAdapter) e.getTransactions().get(0).getSlot()).slotNumber;
                    if (!isLeftClick)
                        return;
                    if (slotClicked >= 54) {
                        return;
                    }


                    if (slotClicked >= 45) {
                        ItemStack stack = e.getTransactions().get(0).getOriginal().createStack();

                        if (slotClicked == 53 && stack.getItem() == ItemTypes.ARROW) {
                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                int nextPage = page + 1;
                                openMarket(player, nextPage);
                            }).delayTicks(1).submit(UniversalMarket.getInstance());
                        } else if (slotClicked == 45 && stack.getItem() == ItemTypes.ARROW) {
                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                int nextPage = page - 1;
                                if (nextPage < 0) {
                                    nextPage = 0;
                                }
                                openMarket(player, nextPage);
                            }).delayTicks(1).submit(UniversalMarket.getInstance());
                        } else if (slotClicked == 47 && stack.getItem() == ItemTypes.NAME_TAG) {

                            Iterator<MarketItem> iterator = getListings().iterator();
                            while (iterator.hasNext()) {
                                MarketItem marketItem = iterator.next();
                                if (marketItem.getOwnerUUID().equals(player.getUniqueId())) {
                                    player.getInventory().offer(marketItem.getItem());
                                    UniversalMarket.getInstance().getDatabase().deleteEntry(marketItem.getDatabaseID());
                                    iterator.remove();

                                }
                            }
                            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                player.closeInventory(Cause.of(NamedCause.of("UM", UniversalMarket.getInstance())));
                                player.sendMessage(Text.of(TextColors.GREEN, "Your items have been removed from the Market."));
                            }).delayTicks(1).submit(UniversalMarket.getInstance());

                        }
                        return;
                    }

                    ItemStack stack = e.getTransactions().get(0).getOriginal().createStack();
                    net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(stack);
                    NBTTagCompound nbt = nmsStack.getTagCompound();
                    int databaseID = nbt.getInteger("id");
                    MarketItem marketItem = UniversalMarket.getInstance().getMarket().getMarketItem(databaseID);
                    if (marketItem == null) {
                        return;
                    }
                    Sponge.getScheduler().createTaskBuilder().execute(() -> UniversalMarket.getInstance().getMarket().openItemPurchase(player, marketItem)).delayTicks(1).submit(UniversalMarket.getInstance());

                }
            }
        }.build();
        int firstListing = (page * 45) - 45;

        int index = 0;
        int currentListing = firstListing;
        while (index < 45) {
            if (currentListing >= getListings().size()) {
                //Draw Empty.
//                if (index == 0) {
                inv.offer(new ItemBuilder(ItemTypes.STAINED_GLASS_PANE, 1).setName(Text.of("")).setDyeColor(DyeColors.BLACK).addTag("unique", UUID.randomUUID().toString()).build());
//                } else {
//                    inv.query(new SlotIndex(index)).set(new ItemBuilder(ItemTypes.STAINED_GLASS_PANE, 1).setName(Text.of("")).setDyeColor(DyeColors.BLACK).build());
//                }
            } else {
                MarketItem item = getListings().get(currentListing);

                if (item.isExpired()) {
                    currentListing++;
                    continue;
                }
                inv.offer(item.getDisplay().copy());

            }
            currentListing++;
            index++;
        }

        for (int i = 45; i < 54; i++) {
            inv.query(new SlotIndex(i)).set(new ItemBuilder(ItemTypes.STAINED_GLASS_PANE, 1).setName(Text.of("")).setDyeColor(DyeColors.LIME).build());

        }

        if (page > 1) {
            inv.query(new SlotIndex(45)).set(new ItemBuilder(ItemTypes.ARROW).setName(Text.of(TextColors.YELLOW, "Previous Page")).build());
        }

        if (getListings().size() >= (page * 45)) {
            inv.query(new SlotIndex(53)).set(new ItemBuilder(ItemTypes.ARROW).setName(Text.of(TextColors.YELLOW, "Next Page")).build());
        }

        if (countListings(player.getUniqueId()) > 0) {
            inv.query(new SlotIndex(47)).set(new ItemBuilder(ItemTypes.NAME_TAG).setName(Text.of(TextColors.GREEN, "Return Your Items")).setLore(Text.of(TextColors.GRAY, "Return all items that you've put up for sell.")).build());
        }


        player.openInventory(inv, Cause.of(NamedCause.owner(UniversalMarket.getInstance())));
    }


    public void openItemPurchase(Player player, MarketItem marketItem) {

        ItemStack background = new ItemBuilder(ItemTypes.STAINED_GLASS_PANE, 1).setName(Text.of("")).setDyeColor(DyeColors.CYAN).build();
        Inventory inv = new InventoryBuilder("Market Listing", 1) {
            @Override
            public void onClickInventoryEvent(ClickInventoryEvent e) {
                e.setCancelled(true);
                if (e.getTransactions().size() != 0) {
                    int slotClicked = ((SlotAdapter) e.getTransactions().get(0).getSlot()).slotNumber;

                    if (slotClicked == 0 && marketItem.getOwnerUUID().equals(player.getUniqueId())) {
                        player.sendMessage(Text.of(TextColors.DARK_GRAY, "Removed item from UniversalMarket."));
                        player.getInventory().offer(marketItem.getItem());
                        marketItem.delete();
                        Sponge.getScheduler().createTaskBuilder().execute(() ->
                                player.closeInventory(Cause.of(NamedCause.of("Close", UniversalMarket.getInstance())))).submit(UniversalMarket.getInstance());

                        return;
                    } else if (slotClicked == 3) {
                        ItemStack stack = e.getTargetInventory().query(new SlotIndex(4)).peek().get();
                        net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(stack);
                        NBTTagCompound nbt = nmsStack.getTagCompound();
                        int databaseID = nbt != null ? nbt.getInteger("id") : -1;
                        MarketItem item = UniversalMarket.getInstance().getMarket().getMarketItem(databaseID);
                        UniqueAccount account = UniversalMarket.getInstance().getEconomyService().getOrCreateAccount(player.getUniqueId()).get();
                        org.spongepowered.api.service.economy.Currency currency = UniversalMarket.getInstance().getEconomyService().getDefaultCurrency();

                        if (!UniversalMarket.getInstance().getMarket().doesItemExist(databaseID)) {
                            player.sendMessage(Text.of(TextColors.RED, "It appears that item is no longer for sale!"));
                            Sponge.getScheduler().createTaskBuilder().execute(() -> UniversalMarket.getInstance().getMarket().openMarket(player)).submit(UniversalMarket.getInstance());
                            return;
                        }


                        if (account.getBalance(currency).doubleValue() >= marketItem.getPrice()) {
                            item.delete();
                            player.sendMessage(Text.of(TextColors.DARK_GRAY, "Item Purchased"));
                            account.withdraw(currency, new BigDecimal(marketItem.getPrice()), Cause.of(NamedCause.of("Withdraw", UniversalMarket.getInstance())));
                            UniversalMarket.getInstance().getEconomyService().getOrCreateAccount(marketItem.getOwnerUUID()).get().deposit(currency, new BigDecimal(marketItem.getPrice()), Cause.of(NamedCause.of("Deposit", UniversalMarket.getInstance())));
                            Sponge.getServer().getPlayer(marketItem.getOwnerUUID()).ifPresent(seller -> seller.sendMessage(Text.of(TextColors.DARK_GRAY, "Item Sold.")));
                            Sponge.getServer().getPlayer(marketItem.getOwnerUUID()).ifPresent(seller -> seller.sendMessage(Text.of(TextColors.YELLOW, "+ ", TextColors.GREEN, marketItem.getPrice())));
                            player.sendMessage(Text.of(TextColors.DARK_RED, "- ", TextColors.RED, marketItem.getPrice()));
                            player.sendMessage(Text.of(TextColors.YELLOW, "New Balance: ", TextColors.GREEN, account.getBalance(currency)));
                            player.getInventory().offer(item.getItem());
                            Sponge.getScheduler().createTaskBuilder().execute(() ->
                                    player.closeInventory(Cause.of(NamedCause.of("Close", UniversalMarket.getInstance())))).submit(UniversalMarket.getInstance());
                        } else {
                            player.sendMessage(Text.of(TextColors.RED, "Insufficient funds."));
                        }
                    } else if (slotClicked == 5) {
                        Sponge.getScheduler().createTaskBuilder().execute(() ->
                                openMarket(player)).submit(UniversalMarket.getInstance());
                    } else if (slotClicked == 8 && player.hasPermission("com.xwaffle.universalmarket.remove")) {
                        player.sendMessage(Text.of(TextColors.RED, "You removed a players Listing."));
                        marketItem.forceExpire();
                        Sponge.getScheduler().createTaskBuilder().execute(() ->
                                player.closeInventory(Cause.of(NamedCause.of("Close", UniversalMarket.getInstance())))).submit(UniversalMarket.getInstance());

                        return;
                    }
                }
            }
        }
                .setItem(0, background.copy())
                .setItem(1, background.copy())
                .setItem(2, background.copy())
                .setItem(6, background.copy())
                .setItem(7, background.copy())
                .setItem(8, background.copy())
                .build();


        if (player.hasPermission("com.xwaffle.universalmarket.remove")) {
            inv.query(new SlotIndex(8)).set(new ItemBuilder(ItemTypes.LAVA_BUCKET, 1).setName(Text.of(TextColors.GRAY, "Remove Player Listing")).setLore(Text.of(TextColors.RED, "Admin Only")).build());
        }

        ItemStack purchaseItem = new ItemBuilder(ItemTypes.DYE, 1, 10)
                .setName(Text.of(TextColors.YELLOW, "Accept"))
                .setDyeColor(DyeColors.LIME).build();


        if (marketItem.getOwnerUUID().equals(player.getUniqueId())) {
            inv.query(new SlotIndex(0)).set(new ItemBuilder(ItemTypes.SHEARS, 1).setName(Text.of(TextColors.GRAY, "Remove Listing")).build());
        }

        inv.query(new SlotIndex(3)).set(purchaseItem);
        inv.query(new SlotIndex(4)).set(marketItem.getDisplay());
        ItemStack backItem = new ItemBuilder(ItemTypes.BARRIER, 1).setName(Text.of(TextColors.RED, "Back")).build();
        inv.query(new SlotIndex(5)).set(backItem);

        player.openInventory(inv, Cause.of(NamedCause.owner(UniversalMarket.getInstance())));

    }

    public int countListings(UUID uniqueId) {

        int listings = 0;
        for (MarketItem marketItem : getListings()) {
            if (marketItem.getOwnerUUID().equals(uniqueId)) {
                listings++;
            }
        }

        return listings;

    }

    public boolean isUsePermissionToSell() {
        return usePermissionToSell;
    }

    public boolean useFKey() {
        return useFKey;
    }
}
