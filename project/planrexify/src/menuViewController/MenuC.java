package menuViewController;

import java.io.IOException;
import java.net.URL;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuC implements Initializable {

    @FXML
    private VBox background;
    
    private Statement statement;
    
    private static final String VIEWNAME = "MenuV.fxml";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public static void show(Stage stage, Statement statement) {
    try {
      FXMLLoader loader = new FXMLLoader(MenuC.class.getResource(VIEWNAME));
      Parent root = (Parent) loader.load();

      Scene scene = new Scene(root);

      if (stage == null) {
        stage = new Stage();
      }
      stage.setScene(scene);
      stage.setTitle("Personenwartung");

      MenuC menuC = (MenuC) loader.getController();

      menuC.statement = statement;

      stage.show();
    }
    catch (IOException ex) {
      Logger.getLogger(MenuC.class.getName()).log(Level.SEVERE, null, ex);
      System.err.println("Something wrong with " + VIEWNAME + "!");
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
    
}
