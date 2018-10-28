package com.xwaffle.universalmarket.database;

import com.xwaffle.universalmarket.UniversalMarket;
import com.xwaffle.universalmarket.market.Market;
import com.xwaffle.universalmarket.market.MarketItem;
import com.xwaffle.universalmarket.utils.ItemSerialization;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Chase(Xwaffle) on 10/15/2017.
 */
public class Database {


    private final boolean isExternal;
    private Connection connection;

    public Database(boolean isExternalDB) {

        this.isExternal = isExternalDB;
        if (!isExternalDB) {
            createNewDatabase();
            try {
                Statement stmt = getConnection().createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS `items` (\n" +
                        "  ID          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "  seller_uuid VARCHAR(45) NOT NULL,\n" +
                        "  seller_name VARCHAR(45) NOT NULL,\n" +
                        "  price       VARCHAR(16) NOT NULL,\n" +
                        "  item        TEXT,\n" +
                        "  time_expire BIGINT\n" +
                        ");\n");
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {

        }
    }


    public void executeSqlScript(Connection conn, File inputFile) {
        String delimiter = ";";
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile).useDelimiter(delimiter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }
        Statement currentStatement = null;
        while (scanner.hasNext()) {

            String rawStatement = scanner.next() + delimiter;
            if (rawStatement.trim().length() < 2)
                continue;
            try {
                currentStatement = conn.createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (currentStatement != null) {
                    try {
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        scanner.close();
    }


    public void connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + UniversalMarket.getConfig().getFileLocation().getParent().toString() + File.separator + "universalmarket.db";
            conn = DriverManager.getConnection(url);
            connection = conn;

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void createNewDatabase() {
        String url = "jdbc:sqlite:" + UniversalMarket.getConfig().getFileLocation().getParent().toString() + File.separator + "universalmarket.db";

        UniversalMarket.getInstance().getLogger().info(url + " DB file.");

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {

        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {

                //TODO REMOVE CONTINUOUS LOOP.

                connect();
                return getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Market loadMarket() {
        Market market = new Market();

        try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM items;"); ResultSet resultSet = statement.executeQuery();) {
            while (resultSet.next()) {

                String itemBase64 = resultSet.getString("item");
                ItemStack stack = ItemSerialization.deserializeItemStack(itemBase64).get();
                int id = resultSet.getInt("ID");
                String uuid = resultSet.getString("seller_uuid");
                String name = resultSet.getString("seller_name");
                double price = resultSet.getDouble("price");
                long time = resultSet.getLong("time_expire");
                MarketItem marketItem = new MarketItem(id, stack, UUID.fromString(uuid), name, price, time);
                market.addItem(marketItem, false);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }


        UniversalMarket.getInstance().getLogger().info("Loaded " + market.getListings().size() + " items!");

        return market;
    }

    public int createEntry(ItemStack item, UUID uuid, String username, double price, long time) {

        try {
            Statement statement = getConnection().createStatement();
            statement.execute(new Query().Insert().Into().Field("items").Parenthesis("seller_uuid", "seller_name", "item", "price", "time_expire").Values(uuid.toString(), username, ItemSerialization.serializeItemStack(item).get(), price, time).getQuery());
            int id = statement.getGeneratedKeys().getInt(1);
            statement.close();
            return id;

        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return -1;

    }

    public void deleteEntry(int databaseID) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute("DELETE FROM items WHERE ID=" + databaseID + ";");
            statement.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
