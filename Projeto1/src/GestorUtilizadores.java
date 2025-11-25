import java.io.*;
import java.util.*;

public class GestorUtilizadores {
    private static final String FILE_PATH = "users.txt";

    // Método para reiniciar o estado de todos os utilizadores (remover os ;1)
    public synchronized static void resetLogins() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove o status de login se existir (ex: user1;pass1;1 -> user1;pass1)
                String[] parts = line.split(";");
                lines.add(parts[0] + ";" + parts[1]);
            }
        }
        reescreverFicheiro(lines);
    }

    // Tenta fazer login. Retorna true se sucesso, false se falha ou já logado
    public synchronized static boolean autenticar(String user, String pass) {
        List<String> lines = new ArrayList<>();
        boolean loginSucesso = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String u = parts[0];
                String p = parts[1];
                boolean isLogged = parts.length > 2 && parts[2].equals("1");

                // Verifica credenciais
                if (u.equals(user) && p.equals(pass)) {
                    if (isLogged) {
                        return false; // Já está logado noutro PC
                    } else {
                        line = u + ";" + p + ";1"; // Marca como logado
                        loginSucesso = true;
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (loginSucesso) {
            try {
                reescreverFicheiro(lines);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return loginSucesso;
    }

    private static void reescreverFicheiro(List<String> lines) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String l : lines) {
                pw.println(l);
            }
        }
    }
}