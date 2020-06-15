/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stundenplan.viewController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 *
 * @author Jan Donnerbauer
 */
public class TheMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            String url = "jdbc:derby://localhost:1527/planrexifyDB";
            String user = "app";
            String pwd = "app";
            Connection connection = DriverManager.getConnection(url, user, pwd);
            Statement statement = connection.createStatement();

            StundenplanC.show(stage, statement);
        } catch (SQLException ex) {
            Logger.getLogger(TheMain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
