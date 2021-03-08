package com.example.demo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;


@Component("dbcon")
public class DatabaseConnection {
    @Value("${dbcon.url}")
    private static String url;
    @Value("${dbcon.username}")
    private static String username;
    @Value("${dbcon.password}")
    private static String password;

    private  Connection conn;
    public DatabaseConnection() {

    }

    @PostConstruct
    public void init () {
        System.out.println("Init databasecon");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            this.conn = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @PreDestroy
    public void destroy () {

        try { conn.close(); } catch (Exception e) { System.out.println(e); }

    }

    public Account getAccount(String username) {

        Account account = null;

        String sql = "SELECT * FROM accounts WHERE username=?";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                String name = resultSet.getString(1);
                String cardnum = resultSet.getString(2);
                String pin = resultSet.getString(3);
                Double balance = resultSet.getDouble(4);
                account = new Account(name, cardnum, pin, balance);
            }
        }

        catch(Exception ex){
            System.out.println(ex);
        }
        return account;
    }
    public int insert(Account account) {



        String sql = "INSERT INTO accounts (username, cardnum, pin, balance) Values (?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getCardNum());
            preparedStatement.setString(3, account.getPin());
            preparedStatement.setDouble(4, account.getBalance());

            return  preparedStatement.executeUpdate();
        }
        catch(Exception ex){
            System.out.println(ex);
        }
        return 0;
    }

    public int update(Account account) {


        String sql = "UPDATE accounts SET balance = ? WHERE username = ?";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setDouble(1, account.getBalance());
            preparedStatement.setString(2, account.getUsername());

            return  preparedStatement.executeUpdate();
        }

        catch(Exception ex){
            System.out.println(ex);
        }
        return 0;
    }

}