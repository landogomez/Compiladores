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
            // Recibe un archivo
            String filePath = args[0];
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Error: El archivo especificado no existe.");
                System.exit(1);
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                procesarEntrada(content.toString());
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
        } else {
            // REPL 
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                procesarEntrada(input);
            }
            scanner.close();
        }
    }

    private static void procesarEntrada(String input) {
        Escaner escaner = new Escaner();
        List<Token> tokens = escaner.generarToken(input);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}