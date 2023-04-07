import ui.SimpleUI;

/**
 * Created by odiachuk on 8/29/19.
 */
public class App {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showUI();
            }
        });
    }

    private static void showUI() {
        SimpleUI ui = new SimpleUI();
        ui.createPanel();
    }
}
