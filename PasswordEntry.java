import java.io.Serializable;

/**
 * Clase que representa una entrada de contraseña.
 */
public class PasswordEntry implements Serializable {
    private final String appName; // Nombre de la aplicación
    private final String username; // Nombre de usuario
    private final String password; // Contraseña

    /**
     * Constructor para crear una nueva entrada de contraseña.
     * @param appName Nombre de la aplicación.
     * @param username Nombre de usuario.
     * @param password Contraseña.
     */
    public PasswordEntry(String appName, String username, String password) {
        this.appName = appName;
        this.username = username;
        this.password = password;
    }

    /**
     * Obtiene el nombre de la aplicación.
     * @return El nombre de la aplicación.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Obtiene el nombre de usuario.
     * @return El nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtiene la contraseña.
     * @return La contraseña.
     */
    public String getPassword() {
        return password;
    }
}
