import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteJogo {
    private static final String SERVER_IP = "localhost"; // Ou o IP do servidor
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // Ler a primeira mensagem do servidor
            String serverResponse = in.readLine();

            if ("LOGIN_REQUIRED".equals(serverResponse)) {
                System.out.print("Username: ");
                String user = scanner.nextLine();
                System.out.print("Password: ");
                String pass = scanner.nextLine();

                // Envia credenciais
                out.println(user);
                out.println(pass);

                // Vê resultado do login
                serverResponse = in.readLine();
                System.out.println("Servidor: " + serverResponse);

                if (serverResponse.startsWith("LOGIN_FAIL")) {
                    return; // Sai se o login falhar
                }
            }

            // Loop do Jogo
            while (true) {
                System.out.print("O teu palpite: ");
                String palpite = scanner.nextLine();
                out.println(palpite);

                serverResponse = in.readLine();
                System.out.println("Servidor: " + serverResponse);

                if (serverResponse == null || serverResponse.startsWith("WIN")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}