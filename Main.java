import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia la aplicación.
 */
public class Main {
    /**
     * Método principal que inicia la aplicación PasswordManager.
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordManager().initialize());
    }
}
