import java.io.*;
import java.net.*;
import java.util.Random;

public class ServidorJogo {
    private static final int PORT = 12345;
    private static int NUMERO_SECRETO;

    public static void main(String[] args) {
        try {
            // 1. Resetar logins no ficheiro ao iniciar o servidor
            System.out.println("A reiniciar estado dos utilizadores...");
            GestorUtilizadores.resetLogins();

            // 2. Gerar número aleatório (ex: 0 a 100)
            NUMERO_SECRETO = new Random().nextInt(101);
            System.out.println("Servidor iniciado. Número Secreto gerado: " + NUMERO_SECRETO);

            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                // Espera por um cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());

                // Cria uma nova thread para este cliente
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe interna para lidar com cada cliente individualmente (Thread)
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // --- Fase 1: Login ---
                out.println("LOGIN_REQUIRED");
                String user = in.readLine();
                String pass = in.readLine();

                if (!GestorUtilizadores.autenticar(user, pass)) {
                    out.println("LOGIN_FAIL: Credenciais erradas ou utilizador já logado.");
                    socket.close();
                    return;
                }

                out.println("LOGIN_OK: Bem-vindo! Tente adivinhar o número (0-100).");

                // --- Fase 2: Jogo ---
                String inputLine;
                boolean acertou = false;

                while ((inputLine = in.readLine()) != null) {
                    try {
                        int palpite = Integer.parseInt(inputLine);

                        if (palpite == NUMERO_SECRETO) {
                            out.println("WIN: Parabéns! O número era " + NUMERO_SECRETO);
                            acertou = true;
                            break;
                        } else if (palpite < NUMERO_SECRETO) {
                            out.println("HINT: O número é MAIOR.");
                        } else {
                            out.println("HINT: O número é MENOR.");
                        }
                    } catch (NumberFormatException e) {
                        out.println("ERROR: Por favor insira um número inteiro válido.");
                    }
                }

            } catch (IOException e) {
                System.out.println("Cliente desconectado abruptamente.");
            } finally {
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}