/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stundenplan.viewController;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import einheit.model.*;
import einheit.viewController.*;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author Jan Donnerbauer
 */
public class StundenplanC {

    @FXML
    private BorderPane bpRoot;
    @FXML
    private VBox centerArea;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private GridPane calendarGrid1;
    @FXML
    private TreeView<Einheit> tvEinheiten;
    @FXML
    private Button btAdd;

    private VBox vBox;
    private Label label;
    private boolean isDragged = false;
    private String einheitName;

    // Verbindung zur Datenbank
    private Statement statement;

    private TreeItem<Einheit> tvRoot = new TreeItem<>(new Einheit());

    private final static String VIEWNAME = "StundenplanV.fxml";

    public static void show(Stage stage, Statement statement) {
        try {
            // View und Controller erstellen
            FXMLLoader loader = new FXMLLoader(StundenplanC.class.getResource(VIEWNAME));
            Parent root = (Parent) loader.load();

            // Scene erstellen
            Scene scene = new Scene(root, 1080, 720);

            // Stage: Entweder übergebene Stage verwenden (Primary Stage) oder neue erzeugen
            if (stage == null) {
                stage = new Stage();
            }
            stage.setScene(scene);
            stage.setTitle("Stundenplan");

            // Controller ermitteln
            StundenplanC stundenplanC = (StundenplanC) loader.getController();

            // Datenbankzugriff merken
            stundenplanC.statement = statement;
            stundenplanC.setTvRoot();
            // View initialisieren
            stundenplanC.init(null);

            // Anzeigen
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(StundenplanC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        } catch (Exception ex) {
            Logger.getLogger(StundenplanC.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
            System.exit(2);
        }
    }

    private void setTvRoot() {
        // Sollte eigentlich den ganzen Inhalt der tvEinheiten Löschen
        // Tut es aber nicht :/
        tvEinheiten.setRoot(null);
        tvRoot = new TreeItem<>(new Einheit());
        tvEinheiten.setRoot(tvRoot);
    }

    public void init(String update) throws SQLException {
        if (update != null) {
            setTvRoot();
        }

        calendarGrid.setGridLinesVisible(false);

        tvEinheiten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tvRoot.setExpanded(true);

        for (Einheit e : Einheit.findAll(statement)) {
            TreeItem<Einheit> tvItem = new TreeItem<>(e);
            if (tvRoot.getChildren().toString().indexOf(e.toString()) == -1) {
                tvRoot.getChildren().add(tvItem);
            }
        }

        for (Stunde s : Stunde.findAll(statement)) {
            VBox vBox = new VBox();
            label = new Label();
            String text = s.getName();
            label.setText(text);
            vBox.getChildren().add(label);
            vBox.getStyleClass().add("einheit");

            vBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    tvEinheiten.getScene().setCursor(Cursor.HAND);
                }
            });

            vBox.setOnMouseExited(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    tvEinheiten.getScene().setCursor(Cursor.DEFAULT);
                }
            });
            vBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        calendarGrid.getChildren().remove(event.getSource());
                        removeFromTable(s.getCol(), s.getRow());
                    }
                }
            });
            calendarGrid.add(vBox, s.getCol(), s.getRow());

        }

    tvEinheiten.setOnMouseClicked ( new EventHandler<MouseEvent>() {
            

    public void handle(MouseEvent event) {
        tvEinheiten.setMouseTransparent(true);
        event.setDragDetect(true);
    }
}
);

        tvEinheiten.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tvEinheiten.getScene().setCursor(Cursor.HAND);
            }
        });

        tvEinheiten.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tvEinheiten.getScene().setCursor(Cursor.DEFAULT);
            }
        });

        tvEinheiten.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tvEinheiten.setMouseTransparent(true);
                event.setDragDetect(true);
            }
        });

        tvEinheiten.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                calendarGrid.getStyleClass().removeAll("addGrid");
                calendarGrid.setGridLinesVisible(false);
                tvEinheiten.setMouseTransparent(false);
                if (isDragged) {
                    String node = event.getPickResult().getIntersectedNode().toString();

                    if (node.contains("Grid")) {

                        
                        vBox = new VBox();
                        label = new Label();
                        String text = tvEinheiten.getSelectionModel().getSelectedItem().getValue().toString();
                        label.setText(text);
                        vBox.getChildren().add(label);
                        vBox.getStyleClass().add("einheit");
                        

                        vBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                tvEinheiten.getScene().setCursor(Cursor.HAND);
                            }
                        });

                        vBox.setOnMouseExited(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                tvEinheiten.getScene().setCursor(Cursor.DEFAULT);
                            }
                        });
                        double cellWidth = ((calendarGrid.getWidth() - 50) / 6) + 10;
                        double cellHeight = 123.0;
                        double x = event.getPickResult().getIntersectedPoint().getX();
                        double y = event.getPickResult().getIntersectedPoint().getY();
                        double col = Math.floor(x / cellWidth) + 1;
                        double row = Math.floor(y / cellHeight);
                        insertIntoTable(text, (int)col, (int)row);
                        vBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                if (event.getClickCount() == 2) {
                                    calendarGrid.getChildren().remove(event.getSource());
                                    removeFromTable((int)col, (int)row);
                                }
                            }
                        });
                        calendarGrid.add(vBox, (int) col, (int) row);

                    }
                    calendarGrid.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    System.out.println("not dragged");
                    if (tvEinheiten.getSelectionModel().getSelectedItem() != null) {
                        if (tvEinheiten.getSelectionModel().getSelectedItem().getValue().toString() != null) {
                            einheitName = tvEinheiten.getSelectionModel().getSelectedItem().getValue().toString();
                            System.out.println(einheitName);
                            btAddOnClick();
                        }
                    }
                }

                isDragged = false;
            }
        });

        tvEinheiten.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                isDragged = true;
                event.setDragDetect(false);
                calendarGrid.getScene().setCursor(Cursor.CLOSED_HAND);
                calendarGrid.getStyleClass().add("addGrid");
                calendarGrid.setGridLinesVisible(true);
            }
        });

        tvEinheiten.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tvEinheiten.startFullDrag();
            }
        });
    }

    @FXML
        private void btAddOnClick() {
        Platform.runLater(() -> {
            if (einheitName != null) {
                EinheitC.show(null, statement, getStage(), this, einheitName);
                einheitName = null;
            } else {
                EinheitC.show(null, statement, getStage(), this, null);
            }
            try {
                init(null);
            

} catch (SQLException ex) {
                Logger.getLogger(StundenplanC.class
.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void insertIntoTable(String name, int col, int row) {
        try {
            
            String sql
                    = "  insert "
                    + " into stunde (name "
                    + "             ,col "
                    + "             ,row "
                    + "             ) "
                    + " values ( '" + name + "' "
                    + (", '" + col + "' ")
                    + (", '" + row + "' ")
                    + "        )";

            statement.executeUpdate(sql);
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void removeFromTable(int col, int row) {
        try {
            
            String sql
                    = "  delete "
                    + " from stunde "
                    + " where col = '" + col + "' "
                    + " and row = '" + row + "'";
            

            statement.executeUpdate(sql);
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Stage getStage() {
        return (Stage) bpRoot.getScene().getWindow();
    }

}
