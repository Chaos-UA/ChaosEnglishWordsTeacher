import com.chaos.english.word.teacher.ProgramController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.swing.*;

public class Main {

    public static void main(String[] args) { // tested with jdk1.7
        try {
            ChaosFX.initJavaFX();
            Application.launch(JavaFxLauncherClass.class, args);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static class JavaFxLauncherClass extends Application {

        @Override public void start(Stage stage) throws Exception {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            Platform.setImplicitExit(false);
            ProgramController.showRandomWord();
        }
    }
}
