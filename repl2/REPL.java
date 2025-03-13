import java.io.*;
import java.util.List;
import java.util.Scanner;

public class REPL {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Error: Se ha proporcionado más de un argumento.");
            System.exit(1);
        }
        
        if (args.length == 1) {
            // Modo archivo
            String filePath = args[0];
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Error: El archivo especificado no existe.");
                System.exit(1);
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder source = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line).append("\n");
                }
                run(source.toString());
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
        } else {
            // Modo REPL
            Scanner scanner = new Scanner(System.in);
            System.out.println("Modo REPL. Para salir, presiona CTRL+D.");

            // Bucle que sigue leyendo líneas de entrada hasta encontrar EOF
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                run(input);
            }

            // Este mensaje se imprime cuando el programa detecta EOF (CTRL+D)
            System.out.println("Fin del modo REPL.");

            // Cierra el scanner para liberar recursos
            scanner.close();
        }
    }

    private static void run(String source) {
        Escaner escaner = new Escaner(source);
        List<Token> tokens = escaner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
