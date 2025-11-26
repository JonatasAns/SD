import java.io.*;
import java.util.*;

public class GestorUtilizadores {
    private static final String FILE_PATH = "users.txt";

    // Reinicia o status de login de todos os utilizadores para o novo jogo [cite: 19]
    public synchronized static void resetLogins() throws IOException {
        List<String> lines = new ArrayList<>();
        File f = new File(FILE_PATH);

        // Se o ficheiro não existir, cria um ficheiro de exemplo ou vazio
        if(!f.exists()) {
            f.createNewFile();
            // Opcional: Popular com dados de exemplo se estiver vazio
            // return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains(" ")) continue; // Separador é espaço ou user:pass? O PDF usa espaço no exemplo  mas o código anterior usava ;

                // Assumindo separador " " conforme PDF "user1 pass1 1"
                // Se o seu ficheiro txt usa ';', mude o split para ";"
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    lines.add(parts[0] + " " + parts[1]); // Remove o flag de login
                }
            }
        }
        reescreverFicheiro(lines);
    }

    public synchronized static boolean autenticar(String user, String pass) {
        List<String> lines = new ArrayList<>();
        boolean loginSucesso = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(" "); // Atenção ao separador (Espaço vs ;)
                if (parts.length < 2) continue;

                String u = parts[0];
                String p = parts[1];
                boolean isLogged = parts.length > 2 && parts[2].equals("1");

                if (u.equals(user) && p.equals(pass)) {
                    if (isLogged) {
                        return false; // Já logado [cite: 32]
                    } else {
                        line = u + " " + p + " 1"; // Marca login [cite: 20]
                        loginSucesso = true;
                    }
                } else {
                    // Mantém a linha original se não for o utilizador a autenticar
                    // Mas precisamos garantir que mantemos o estado dos outros utilizadores
                    line = line;
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