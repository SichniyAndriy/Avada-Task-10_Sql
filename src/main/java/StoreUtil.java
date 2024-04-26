package main.java;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import net.datafaker.Faker;

public class StoreUtil {
    public final static Faker faker = new Faker(Locale.getDefault());

    public static int addUsers(int len) {
        String query =
                "INSERT INTO users(first_name, last_name, email, phone, registration) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionProvider.instance();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < len; i++) {
                preparedStatement.setString(1, faker.name().firstName());
                preparedStatement.setString(2, faker.name().lastName());
                preparedStatement.setString(3, faker.internet().emailAddress());
                preparedStatement.setString(4, faker.phoneNumber().phoneNumber());
                preparedStatement.setTimestamp(5, Timestamp.valueOf(String.valueOf(faker.date().past(100000, 100, TimeUnit.HOURS))));
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return len;
    }

    public static int addUserDetails() {
        List<Integer> indexes = new ArrayList<>();
        try (Connection connection = ConnectionProvider.instance();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT id FROM users");
            while (resultSet.next()) {
                indexes.add(resultSet.getInt(1));
            }
            if (indexes.size() < 1) return 0;
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO user_details " +
                            "(user_id, postal_code, city_id, street, house, ipn, passport) " +
                            "VALUES (?,?,?,?,?,?,?)");
            PreparedStatement getRecordsByUserId =
                    connection.prepareStatement("SELECT id FROM user_details WHERE user_id = ?");
            for (var index: indexes) {
                getRecordsByUserId.setLong(1, index);
                ResultSet executed = getRecordsByUserId.executeQuery();
                boolean flag = faker.random().nextBoolean() ? true : (faker.random().nextBoolean() ? true : false);
                if (executed.next() || !flag) continue;

                String city = faker.address().cityName();
                OptionalLong cityIndex = checkCity(city, connection);
                if (cityIndex.isEmpty()) {
                    PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO cities (name) VALUES (?)");
                    preparedStatement1.setString(1, city);
                    preparedStatement1.execute();
                    cityIndex = checkCity(city, connection);
                }
                preparedStatement.setLong(1, index);
                preparedStatement.setString(2, faker.address().postcode());
                preparedStatement.setLong(3, cityIndex.getAsLong());
                preparedStatement.setString(4, faker.address().streetName());
                preparedStatement.setString(5, faker.address().buildingNumber());
                preparedStatement.setString(6, faker.numerify("##########"));
                preparedStatement.setString(7, faker.bothify("??######", true));
                preparedStatement.execute();
            }
            return preparedStatement.getUpdateCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int addProducts(int len) {
        try (
                Connection connection = ConnectionProvider.instance();
                Statement statement  = connection.createStatement()
        ) {
            for (int i = 0; i < len; i++) {
                String[] tmpArr = faker.commerce().productName().split(" ");
                String productName = String.join(" ", tmpArr[0], tmpArr[2]);
                PreparedStatement preparedStatement =
                        connection.prepareStatement("SELECT id FROM products WHERE name = ?");
                preparedStatement.setString(1, productName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    continue;
                }

                StringBuilder stringBuilder = new StringBuilder("INSERT INTO products (name, price) VALUES ");
                stringBuilder
                        .append("(\'")
                        .append(productName)
                        .append("\', ")
                        .append(faker.commerce().price(50, 2500).replace(',', '.'))
                        .append(");");
                String query = stringBuilder.toString();
                statement.execute(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return len;
    }

    public static void makeChoice() {
        try (Connection connection = ConnectionProvider.instance();
             Statement statement = connection.createStatement()
        ) {
            List<Long> userIds = new ArrayList<>();
            ResultSet resultSetUsers = statement.executeQuery("SELECT id FROM users");
            while (resultSetUsers.next()) {
                userIds.add(resultSetUsers.getLong(1));
            }

            List<Long> productIds = new ArrayList<>();
            ResultSet resultSetProducts = statement.executeQuery("SELECT id FROM products");
            while (resultSetProducts.next()) {
                productIds.add(resultSetProducts.getLong(1));
            }

            int len = faker.random().nextInt(1, 5);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO shopping_card (user_id, product_id, amount, status) VALUES (?, ?, ?, ?::CURRENT_STATUS)");
            Long userId = userIds.get(faker.random().nextInt(userIds.size()));
            for (int i = 0; i < len; ++i) {
                Long productId = productIds.get(faker.random().nextInt(productIds.size()));
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, productId);
                preparedStatement.setInt(3, faker.random().nextInt(1, 3));
                preparedStatement.setString(4, CURRENT_STATUS.CHOICED.value);
                preparedStatement.execute();
            }
            formOrder(connection, userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void formOrder(Connection connection, Long userId) throws SQLException {
        String query = "SELECT shc.id, p.name, p.price, shc.amount " +
                "FROM shopping_card AS shc " +
                "INNER JOIN products AS p ON shc.product_id = p.id " +
                "INNER JOIN users AS u ON shc.user_id = u.id " +
                "WHERE status = \'CH\' AND u.id = ?";
        PreparedStatement psShopCard = connection.prepareStatement(query);
        psShopCard.setLong(1, userId);
        ResultSet resultSet = psShopCard.executeQuery();

        StringBuilder stringBuilder = new StringBuilder();
        BigDecimal sum = BigDecimal.valueOf(0);
        PreparedStatement changeStatus =
                connection.prepareStatement("UPDATE shopping_card SET status = ?::CURRENT_STATUS WHERE id = ?");

        while (resultSet.next()) {
            long cardId = resultSet.getLong(1);
            stringBuilder.append(resultSet.getString(2)).append(", ");
            BigDecimal price = resultSet.getBigDecimal(3);
            int amount = resultSet.getInt(4);
            sum = sum.add(price.multiply(BigDecimal.valueOf(amount)).setScale(2));
            //Update status after extracting data
            changeStatus.setString(1, CURRENT_STATUS.ORDERED.value);
            changeStatus.setLong(2, cardId);
            changeStatus.execute();
        }

        // write order
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        String insertOrder = "INSERT INTO orders (user_id, total_price, description, order_date) VALUES (?, ?, ?, ?)";
        PreparedStatement psOrder = connection.prepareStatement(insertOrder);
        psOrder.setLong(1, userId);
        psOrder.setBigDecimal(2, sum);
        psOrder.setString(3, stringBuilder.toString());
        Timestamp timestamp = faker.date().past(80000, 100, TimeUnit.HOURS);
        psOrder.setTimestamp(4, timestamp);
        psOrder.execute();
    }

    private static OptionalLong checkCity(String city, Connection connection) throws SQLException{
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT id FROM cities WHERE name = ?");
        preparedStatement.setString(1, city);
        OptionalLong optionalLong;
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return OptionalLong.of(resultSet.getLong(1));
        }
        return OptionalLong.empty();
    }

    private enum CURRENT_STATUS {
        CHOICED("CH"), ORDERED("ORD");

        CURRENT_STATUS(String ch) {
            value = ch;
        }
        private String value;
    }

}
