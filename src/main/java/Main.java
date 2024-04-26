package main.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try (
                Connection connection = ConnectionProvider.instance();
                Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery("SELECT version()");
            while(resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StoreUtil.addUsers(StoreUtil.faker.random().nextInt(75, 100));
        StoreUtil.addUserDetails();
        StoreUtil.addProducts(StoreUtil.faker.random().nextInt(25, 50));
        int len = StoreUtil.faker.random().nextInt(200, 300);
        for (int i = 0; i < len; i++) {
            StoreUtil.makeChoice();
        }
    }
}
