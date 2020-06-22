package timespinner;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputEvent;
import javafx.util.StringConverter;

public class TimeSpinner extends Spinner<LocalTime> {

    // Editiermodus (Stunde oder Minuten werden geändert)
    enum Mode {

        HOURS {
            //Stunden werden erhöht
            @Override
            LocalTime increment(LocalTime time, int steps) {
                return time.plusHours(steps);
            }

            //Stundenteil wird selektiert
            @Override
            void select(TimeSpinner spinner) {
                int index = spinner.getEditor().getText().indexOf(':');
                spinner.getEditor().selectRange(0, index);
            }
        },
        MINUTES {
            //Minuten werden erhöht
            @Override
            LocalTime increment(LocalTime time, int steps) {
                return time.plusMinutes(steps);
            }

            //MInutenteil wird selektiert
            @Override
            void select(TimeSpinner spinner) {
                int hrIndex = spinner.getEditor().getText().indexOf(':');
                int minIndex = spinner.getEditor().getText().length();
                System.out.println(minIndex);
                spinner.getEditor().selectRange(hrIndex + 1, minIndex);
            }
        };

        abstract LocalTime increment(LocalTime time, int steps);

        abstract void select(TimeSpinner spinner);

        //reduzieren
        LocalTime decrement(LocalTime time, int steps) {
            return increment(time, -steps);
        }
    }

    // current editing Mode
    private final ObjectProperty<Mode> mode = new SimpleObjectProperty<>(Mode.HOURS);

    public ObjectProperty<Mode> modeProperty() {
        return mode;
    }

    public final Mode getMode() {
        return modeProperty().get();
    }

    public final void setMode(Mode mode) {
        modeProperty().set(mode);
    }

    public TimeSpinner(LocalTime time) {
        setEditable(true);
        

        // Spinner Format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        StringConverter<LocalTime> localTimeConverter = new StringConverter<LocalTime>() {

            //Zeit wird formatiert LocalTime to String
            @Override
            public String toString(LocalTime time) {
                System.out.println(formatter.format(time));
                return formatter.format(time);
            }
            
            //Zeit wird formatiert String to LocalTime
            @Override
            public LocalTime fromString(String string) {
                String[] tokens = string.split(":");
                int hours = getIntField(tokens, 0);
                int minutes = getIntField(tokens, 1);
                int totalSeconds = (hours * 60 + minutes) * 60;
                return LocalTime.of((totalSeconds / 3600) % 24, (totalSeconds / 60) % 60);
            }

            private int getIntField(String[] tokens, int index) {
                if (tokens.length <= index || tokens[index].isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(tokens[index]);
            }

        };

        // Textformatter überprüft ob String regex entspricht
        TextFormatter<LocalTime> textFormatter = new TextFormatter<LocalTime>(localTimeConverter, time, c -> {
            String newText = c.getControlNewText();
            if (newText.matches("[0-9]{0,2}:[0-9]{0,2}")) {
                return c;
            }
            return null;
        });

        // The spinner value factory defines increment and decrement by
        // delegating to the current editing mode:
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {

            {

                setConverter(localTimeConverter);
                setValue(time);
            }

            //decrement je nach Mode
            @Override
            public void decrement(int steps) {
                setValue(mode.get().decrement(getValue(), steps));
                mode.get().select(TimeSpinner.this);
            }

            //increment je nach Mode
            @Override
            public void increment(int steps) {
                setValue(mode.get().increment(getValue(), steps));
                mode.get().select(TimeSpinner.this);
            }

        };

        this.setValueFactory(valueFactory);
        this.getEditor().setTextFormatter(textFormatter);

        // definiert den Mode (was ist ausgewählt)
        this.getEditor().addEventHandler(InputEvent.ANY, e -> {
            int caretPos = this.getEditor().getCaretPosition();
            int hrIndex = this.getEditor().getText().indexOf(':');
            if (caretPos <= hrIndex) {
                mode.set(Mode.HOURS);
            } else {
                mode.set(Mode.MINUTES);
                System.out.println("miiinn");
            }
        });

        //Selektion (Mode) wird aktualisiert, wenn sich der Mode ändert
        mode.addListener((obs, oldMode, newMode) -> newMode.select(this));

    }

    public TimeSpinner() {
        this(LocalTime.now());
    }
}
