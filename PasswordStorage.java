import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase para manejar el almacenamiento de contraseñas cifradas.
 */
public class PasswordStorage {
    private static final String FILE_NAME = "passwords.dat"; // Nombre del archivo donde se guardan las contraseñas
    private static final String SECRET_KEY = "mySuperSecretKey12"; // Clave secreta (16 bytes)

    /**
     * Carga las contraseñas desde el archivo cifrado.
     * @return Lista de entradas de contraseñas.
     */
    public ArrayList<PasswordEntry> loadPasswords() {
        File file = new File(FILE_NAME); // Crear un objeto File para el archivo
        if (!file.exists()) { // Si el archivo no existe
            System.out.println("Archivo no encontrado. Se creará uno nuevo.");
            return new ArrayList<>(); // Devolver una lista vacía
        }

        try (ObjectInputStream ois = new ObjectInputStream(new CipherInputStream( // Crear un ObjectInputStream para leer objetos desde el archivo cifrado
                new FileInputStream(FILE_NAME), getCipher(Cipher.DECRYPT_MODE)))) { // Crear un FileInputStream para leer bytes desde el archivo y un CipherInputStream para descifrar los bytes
            return (ArrayList<PasswordEntry>) ois.readObject(); // Leer y devolver la lista de contraseñas
        } catch (Exception e) { // Si ocurre un error
            System.out.println("Error al cargar las contraseñas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Devolver una lista vacía
        }
    }

    /**
     * Guarda las contraseñas en el archivo cifrado.
     * @param passwordEntries Lista de entradas de contraseñas.
     */
    public void savePasswords(ArrayList<PasswordEntry> passwordEntries) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new CipherOutputStream( // Crear un ObjectOutputStream para escribir objetos en el archivo cifrado
                new FileOutputStream(FILE_NAME), getCipher(Cipher.ENCRYPT_MODE)))) { // Crear un FileOutputStream para escribir bytes en el archivo y un CipherOutputStream para cifrar los bytes
            oos.writeObject(passwordEntries); // Escribir la lista de contraseñas en el archivo
            System.out.println("Contraseñas guardadas correctamente.");
        } catch (Exception e) { // Si ocurre un error
            System.out.println("Error al guardar las contraseñas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura un objeto Cipher para cifrar o descifrar datos.
     * @param mode Modo del cifrado (Cipher.ENCRYPT_MODE o Cipher.DECRYPT_MODE).
     * @return Objeto Cipher configurado.
     * @throws Exception Si ocurre un error al configurar el cifrado.
     */
    private Cipher getCipher(int mode) throws Exception {
        byte[] keyBytes = SECRET_KEY.getBytes("UTF-8"); // Obtener los bytes de la clave secreta
        keyBytes = Arrays.copyOf(keyBytes, 16); // Asegurar que la clave tenga 16 bytes
        SecretKey key = new SecretKeySpec(keyBytes, "AES"); // Crear una clave secreta AES
        Cipher cipher = Cipher.getInstance("AES"); // Crear un objeto Cipher para el algoritmo AES
        cipher.init(mode, key); // Inicializar el objeto Cipher con el modo y la clave
        return cipher; // Devolver el objeto Cipher configurado
    }
}
