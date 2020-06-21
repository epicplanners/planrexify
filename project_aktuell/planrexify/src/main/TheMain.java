package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import menuViewController.*;

public class TheMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            String url = "jdbc:derby://localhost:1527/planrexifyDB";
            String user = "app";
            String pwd = "app";
            Connection connection = DriverManager.getConnection(url, user, pwd);
            Statement statement = connection.createStatement();

            MenuC.show(stage, statement);
        } catch (SQLException ex) {
            Logger.getLogger(TheMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
