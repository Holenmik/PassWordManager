import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Clase que gestiona la interfaz gráfica y la lógica principal de la aplicación.
 */
public class PasswordManager {
    private ArrayList<PasswordEntry> passwordEntries; // Lista de entradas de contraseñas
    private DefaultListModel<String> listModel; // Modelo para la lista visual
    private JList<String> passwordList; // Lista visual de contraseñas
    private final PasswordStorage storage = new PasswordStorage(); // Instancia de PasswordStorage para gestionar el almacenamiento

    /**
     * Inicializa la interfaz gráfica y carga los datos.
     */
    public void initialize() {
        passwordEntries = storage.loadPasswords(); // Cargar contraseñas desde el archivo

        JFrame frame = new JFrame("PassWord_Manager"); // Crear la ventana principal
        frame.setSize(700, 400); // Establecer el tamaño de la ventana
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Evitar el cierre predeterminado
        frame.addWindowListener(new WindowAdapter() { // Agregar un listener para gestionar el cierre de la ventana
            @Override
            public void windowClosing(WindowEvent e) {
                storage.savePasswords(passwordEntries); // Guardar contraseñas al cerrar la ventana
                System.exit(0); // Salir de la aplicación
            }
        });

        listModel = new DefaultListModel<>(); // Inicializar el modelo de la lista
        passwordList = new JList<>(listModel); // Inicializar la lista visual
        updatePasswordList(); // Actualizar la lista visual con los datos

        JButton addButton = new JButton("Añadir"); // Botón para añadir una entrada
        JButton editButton = new JButton("Editar"); // Botón para editar una entrada
        JButton deleteButton = new JButton("Eliminar"); // Botón para eliminar una entrada
        JButton copyUserButton = new JButton("Copiar Usuario"); // Botón para copiar el nombre de usuario
        JButton copyPassButton = new JButton("Copiar Contraseña"); // Botón para copiar la contraseña

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5)); // Panel para los botones
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(copyUserButton);
        buttonPanel.add(copyPassButton);

        addButton.addActionListener(e -> addEntry()); // Listener para el botón "Añadir"
        editButton.addActionListener(e -> editEntry()); // Listener para el botón "Editar"
        deleteButton.addActionListener(e -> deleteEntry()); // Listener para el botón "Eliminar"
        copyUserButton.addActionListener(e -> copyToClipboard(true)); // Listener para el botón "Copiar Usuario"
        copyPassButton.addActionListener(e -> copyToClipboard(false)); // Listener para el botón "Copiar Contraseña"

        frame.add(new JScrollPane(passwordList), BorderLayout.CENTER); // Agregar la lista a la ventana
        frame.add(buttonPanel, BorderLayout.SOUTH); // Agregar el panel de botones a la ventana
        frame.setVisible(true); // Mostrar la ventana
    }

    /**
     * Actualiza la lista visual con las entradas de contraseñas.
     */
    private void updatePasswordList() {
        listModel.clear(); // Limpiar la lista visual
        for (PasswordEntry entry : passwordEntries) { // Recorrer la lista de entradas
            listModel.addElement(entry.getAppName() + " - " + entry.getUsername()); // Agregar cada entrada a la lista visual
        }
    }

    /**
     * Muestra un diálogo para añadir o editar una entrada de contraseña.
     * @param existingEntry La entrada existente (null si es una nueva entrada).
     * @return La entrada de contraseña creada o editada.
     */
    private PasswordEntry showEntryDialog(PasswordEntry existingEntry) {
        JTextField appField = new JTextField(20); // Campo de texto para la aplicación
        JTextField userField = new JTextField(20); // Campo de texto para el nombre de usuario
        JPasswordField passField = new JPasswordField(20); // Campo de texto para la contraseña

        if (existingEntry != null) { // Si se está editando una entrada existente
            appField.setText(existingEntry.getAppName()); // Establecer el nombre de la aplicación
            userField.setText(existingEntry.getUsername()); // Establecer el nombre de usuario
            passField.setText(existingEntry.getPassword()); // Establecer la contraseña
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5)); // Panel para los campos de texto
        panel.add(new JLabel("Aplicación:")); // Etiqueta para el campo de la aplicación
        panel.add(appField); // Agregar el campo de la aplicación al panel
        panel.add(new JLabel("Usuario:")); // Etiqueta para el campo del nombre de usuario
        panel.add(userField); // Agregar el campo del nombre de usuario al panel
        panel.add(new JLabel("Contraseña:")); // Etiqueta para el campo de la contraseña
        panel.add(passField); // Agregar el campo de la contraseña al panel

        int result = JOptionPane.showConfirmDialog(null, panel, // Mostrar el diálogo
                existingEntry == null ? "Nueva Entrada" : "Editar Entrada", // Título del diálogo
                JOptionPane.OK_CANCEL_OPTION); // Opciones del diálogo

        if (result == JOptionPane.OK_OPTION) { // Si se hace clic en "OK"
            return new PasswordEntry( // Crear una nueva entrada de contraseña
                    appField.getText(), // Nombre de la aplicación
                    userField.getText(), // Nombre de usuario
                    new String(passField.getPassword()) // Contraseña
            );
        }
        return null; // Si se hace clic en "Cancelar"
    }

    /**
     * Añade una nueva entrada a la lista.
     */
    private void addEntry() {
        PasswordEntry entry = showEntryDialog(null); // Mostrar el diálogo para añadir una entrada
        if (entry != null) { // Si se crea una nueva entrada
            passwordEntries.add(entry); // Agregar la entrada a la lista
            updatePasswordList(); // Actualizar la lista visual
            storage.savePasswords(passwordEntries); // Guardar las contraseñas en el archivo
            System.out.println("Entrada añadida.");
        }
    }

    /**
     * Edita una entrada seleccionada en la lista.
     */
    private void editEntry() {
        int selectedIndex = passwordList.getSelectedIndex(); // Obtener el índice de la entrada seleccionada
        if (selectedIndex >= 0) { // Si se selecciona una entrada
            PasswordEntry existingEntry = passwordEntries.get(selectedIndex); // Obtener la entrada existente
            PasswordEntry updatedEntry = showEntryDialog(existingEntry); // Mostrar el diálogo para editar la entrada
            if (updatedEntry != null) { // Si se actualiza la entrada
                passwordEntries.set(selectedIndex, updatedEntry); // Reemplazar la entrada existente con la actualizada
                updatePasswordList(); // Actualizar la lista visual
                storage.savePasswords(passwordEntries); // Guardar las contraseñas en el archivo
                System.out.println("Entrada editada.");
            }
        } else {
            showError("Selecciona una entrada para editar"); // Mostrar un mensaje de error si no se selecciona ninguna entrada
        }
    }

    /**
     * Elimina una entrada seleccionada de la lista.
     */
    private void deleteEntry() {
        int selectedIndex = passwordList.getSelectedIndex(); // Obtener el índice de la entrada seleccionada
        if (selectedIndex >= 0) { // Si se selecciona una entrada
            passwordEntries.remove(selectedIndex); // Eliminar la entrada de la lista
            updatePasswordList(); // Actualizar la lista visual
            storage.savePasswords(passwordEntries); // Guardar las contraseñas en el archivo
            System.out.println("Entrada eliminada.");
        } else {
            showError("Selecciona una entrada para eliminar"); // Mostrar un mensaje de error si no se selecciona ninguna entrada
        }
    }

    /**
     * Copia el usuario o la contraseña al portapapeles.
     * @param copyUser Indica si se debe copiar el nombre de usuario (true) o la contraseña (false).
     */
    private void copyToClipboard(boolean copyUser) {
        int selectedIndex = passwordList.getSelectedIndex(); // Obtener el índice de la entrada seleccionada
        if (selectedIndex == -1) { // Si no se selecciona ninguna entrada
            showError("Selecciona una entrada primero"); // Mostrar un mensaje de error
            return;
        }

        PasswordEntry entry = passwordEntries.get(selectedIndex); // Obtener la entrada seleccionada
        String text = copyUser ? entry.getUsername() : entry.getPassword(); // Obtener el texto a copiar

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); // Obtener el portapapeles del sistema
        clipboard.setContents(new StringSelection(text), null); // Copiar el texto al portapapeles

        JOptionPane.showMessageDialog(null, // Mostrar un mensaje de confirmación
                "¡Texto copiado al portapapeles!", // Mensaje
                "Copiado", // Título
                JOptionPane.INFORMATION_MESSAGE); // Tipo de mensaje
    }

    /**
     * Muestra un mensaje de error.
     * @param message El mensaje de error a mostrar.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}