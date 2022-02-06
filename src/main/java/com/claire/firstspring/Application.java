package com.claire.firstspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws SQLException {
//        Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/springbootdb?user=dbadmin&password=AReferenceMapInHumanCells534@");
//        PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1");
//        System.out.println(preparedStatement.execute());
        SpringApplication.run(Application.class, args);
    }
}
