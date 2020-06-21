package kalender.model;

import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import kalender.viewController.KalenderC;
import termin.model.Termin;
import termin.viewController.TerminC;

public class FullCalendarView {

    private ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
    private final VBox view;
    private Text calendarTitle;

    private final Statement statement;

    private final Stage stage;
    private final KalenderC kalenderC;

    private final ObjectProperty<YearMonth> currentYearMonth = new SimpleObjectProperty<>();

    /**
     *
     * @param yearMonth year month to create the calendar of
     * @param statement
     * @param stage
     * @param kalenderC
     * @throws java.sql.SQLException
     */
    public FullCalendarView(YearMonth yearMonth, Statement statement, Stage stage, KalenderC kalenderC) throws SQLException {
        this.statement = statement;
        this.stage = stage;
        this.kalenderC = kalenderC;
        currentYearMonth.set(yearMonth);
        // Kalender GridPane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(1400, 840);
        calendar.alignmentProperty().set(Pos.CENTER);
        calendar.setGridLinesVisible(true);
        // Kalender Reihen und Spalten
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200, 200);
                ap.setMaxSize(200, 200);
                calendar.add(ap, j, i);
                allCalendarDays.add(ap);
            }
        }
        // Tage-Labels
        Text[] dayNames = new Text[]{new Text("Mo."), new Text("Di."),
            new Text("Mi."), new Text("Do."), new Text("Fr."),
            new Text("Sa."), new Text("So.")};

        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(1400);
       
        Integer col = 0;
        for (Text txt : dayNames) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 50);
            
            txt.setStyle("-fx-font-size: 16px;");

            ap.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }
        // Populate calendar (Kalendertage)
        populateCalendar(yearMonth);

        view = new VBox(dayLabels, calendar);
        view.setMaxSize(1200, 850);
        view.alignmentProperty().set(Pos.CENTER);
    }

    /**
     *
     * @param yearMonth year and month of month to render
     */
    public void populateCalendar(YearMonth yearMonth) throws SQLException {

        // Startdatum
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        // Zeilenumbruch nach jedem Sonntag
        while (!calendarDate.getDayOfWeek().toString().equals("MONDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        // Kalender mit Kalendertagen
        for (AnchorPaneNode ap : allCalendarDays) {

            //alte Kalendertage werden entfernt
            ap.getChildren().clear();
            ap.getStyleClass().remove("current");

            //Text mit Kalendertag
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            //Kalendertag wird gesetzt
            ap.setDate(calendarDate);
            ap.setTopAnchor(txt, 5.0);
            ap.setLeftAnchor(txt, 5.0);
            ap.getChildren().add(txt);

            //Listview mit Terminen an diesem Tag
            ListView lv = new ListView();

            lv.setPrefSize(200, 100);
            lv.setMaxSize(200, 100);
            ap.setTopAnchor(lv, 40.0);
            ap.setLeftAnchor(lv, 0.0);
            ap.getChildren().add(lv);

            lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            //alle Termine an diesem Tag
            ObservableList<Termin> listTermin = Termin.findAll(statement, calendarDate);

            //MouseEvent bei Klick
            //Termin soll geändert werden können
            lv.setOnMouseClicked((MouseEvent event) -> {
                if (lv.getSelectionModel().getSelectedItem() != null) {
                    Platform.runLater(() -> {
                        TerminC.show(null, statement, stage, kalenderC, (Termin) lv.getSelectionModel().getSelectedItem());
                    });
                }
            });

            //Termine werden in die Lsitview eingefügt
            lv.setItems(listTermin);

            //aktueller Tag wird markiert
            if (calendarDate.isEqual(LocalDate.now())) {
                ap.getStyleClass().add("current");
            }

            //Zähler wird erhöht
            calendarDate = calendarDate.plusDays(1);
        }
    }

    /**
     * Monat eins zurück dates.
     *
     * @throws java.sql.SQLException
     */
    public void previousMonth() throws SQLException {
        currentYearMonth.set(currentYearMonth.get().minusMonths(1));
        populateCalendar(currentYearMonth.get());
    }

    /**
     * Monat eins nach vorn dates.
     *
     * @throws java.sql.SQLException
     */
    public void nextMonth() throws SQLException {
        currentYearMonth.set(currentYearMonth.get().plusMonths(1));
        populateCalendar(currentYearMonth.get());
    }

    public VBox getView() {
        return view;
    }

    public ArrayList<AnchorPaneNode> getAllCalendarDays() {
        return allCalendarDays;
    }

    public void setAllCalendarDays(ArrayList<AnchorPaneNode> allCalendarDays) {
        this.allCalendarDays = allCalendarDays;
    }

    public ObjectProperty<YearMonth> yearMonthProperty() {
        return currentYearMonth;
    }

}
