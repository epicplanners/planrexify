package kalender.model;

import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import kalender.viewController.KalenderC;
import termin.model.Termin;
import termin.viewController.TerminC;

public class FullCalendarView {

    private ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
    private VBox view;
    private Text calendarTitle;

    private Statement statement;

    private Stage stage;
    private KalenderC kalenderC;

    private final ObjectProperty<YearMonth> currentYearMonth = new SimpleObjectProperty<>();

    /**
     * Create a calendar view
     *
     * @param yearMonth year month to create the calendar of
     * @param statement
     * @throws java.sql.SQLException
     */
    public FullCalendarView(YearMonth yearMonth, Statement statement, Stage stage, KalenderC kalenderC) throws SQLException {
        this.statement = statement;
        this.stage = stage;
        this.kalenderC = kalenderC;
        currentYearMonth.set(yearMonth);
        // Create the calendar grid pane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(1400, 840);
        calendar.alignmentProperty().set(Pos.CENTER);
        calendar.setGridLinesVisible(true);
        // Create rows and columns with anchor panes for the calendar
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200, 200);
                ap.setMaxSize(200, 200);
                calendar.add(ap, j, i);
                allCalendarDays.add(ap);
            }
        }
        // Days of the week labels
        Text[] dayNames = new Text[]{new Text("Monday"), new Text("Tuesday"),
            new Text("Wednesday"), new Text("Thursday"), new Text("Friday"),
            new Text("Saturday"), new Text("Sunday")};
        
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(1400);
        
        dayLabels.getStyleClass().add("big");
        Integer col = 0;
        for (Text txt : dayNames) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 50);
            
            ap.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }
        // Populate calendar with the appropriate day numbers
        populateCalendar(yearMonth);
        // Create the calendar view
        view = new VBox(dayLabels, calendar);
    }

    /**
     * Set the days of the calendar to correspond to the appropriate date
     *
     * @param yearMonth year and month of month to render
     */
    public void populateCalendar(YearMonth yearMonth) throws SQLException {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
        while (!calendarDate.getDayOfWeek().toString().equals("MONDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }
        // Populate the calendar with day numbers
        for (AnchorPaneNode ap : allCalendarDays) {
            //Termin.findAll(statement, calendarDate);
            ap.getChildren().clear();
            ap.getStyleClass().remove("current");

            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            ap.setTopAnchor(txt, 5.0);
            ap.setLeftAnchor(txt, 5.0);
            ap.getChildren().add(txt);

            ListView lv = new ListView();

            lv.setPrefSize(200, 100);
            lv.setMaxSize(200, 100);
            ap.setTopAnchor(lv, 40.0);
            ap.setLeftAnchor(lv, 0.0);
            ap.getChildren().add(lv);

            lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            ObservableList<Termin> listTermin = Termin.findAll(statement, calendarDate);

            lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {

                    if (lv.getSelectionModel().getSelectedItem() != null) {
                        Platform.runLater(() -> {
                            TerminC.show(null, statement, stage, kalenderC, (Termin) lv.getSelectionModel().getSelectedItem());
                        });
                    }

                }
            });

            lv.setItems(listTermin);

            if (calendarDate.isEqual(LocalDate.now())) {
                ap.getStyleClass().add("current");
            }

            calendarDate = calendarDate.plusDays(1);
        }
        // Change the title of the calendar
        //calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    /**
     * Move the month back by one. Repopulate the calendar with the correct
     * dates.
     */
    public void previousMonth() throws SQLException {
        currentYearMonth.set(currentYearMonth.get().minusMonths(1));
        populateCalendar(currentYearMonth.get());
    }

    /**
     * Move the month forward by one. Repopulate the calendar with the correct
     * dates.
     */
    public void nextMonth() throws SQLException {
        currentYearMonth.set(currentYearMonth.get().plusMonths(1));
        populateCalendar(currentYearMonth.get());;
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
