import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Escaner {
    private static final String[] PALABRAS_RESERVADAS = {"and", "else", "false", "fun", "for", "if", "null", "or", "print", "return", "true", "var", "while"};
    private static final String[] OPERADORES = {"!=", "==", ">=", "<=", "!", "=", ">", "<", "*", "+", "-"};
    private List<Token> tokens = new ArrayList<>();
    private int linea = 1;
    private int i = 0;

    public List<Token> generarToken(String input) {
        while (i < input.length()) {
            char c = input.charAt(i);

            // Ignorar espacios en blanco
            if (Character.isWhitespace(c)) {
                if (c == '\n') linea++;
                i++;
                continue;
            }

            // Ignorar comentarios de una línea (//)
            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                while (i < input.length() && input.charAt(i) != '\n') i++;
                continue;
            }

            // Ignorar comentarios largos (/* */)
            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '*') {
                i += 2; // Saltar "/*"
                while (i < input.length() && !(input.charAt(i) == '*' && i + 1 < input.length() && input.charAt(i + 1) == '/')) {
                    if (input.charAt(i) == '\n') linea++; // Contar líneas dentro del comentario
                    i++;
                }
                if (i < input.length()) i += 2; // Saltar "*/"
                else System.err.println("Error léxico en la línea " + linea + ": comentario no cerrado.");
                continue;
            }

            // Manejar el carácter '/' fuera de un comentario
            if (c == '/') {
                System.err.println("Error léxico en la línea " + linea + ": carácter no reconocido '/' fuera de un contexto válido.");
                i++;
                continue;
            }

            // Identificar palabras reservadas e identificadores
            if (Character.isLetter(c)) {
                int start = i;
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) i++;
                String palabra = input.substring(start, i);
                if (Arrays.asList(PALABRAS_RESERVADAS).contains(palabra)) {
                    tokens.add(new Token(TipoToken.valueOf(palabra.toUpperCase()), palabra, palabra.equals("true") ? "1" : palabra.equals("false") ? "0" : palabra, linea));
                } else {
                    tokens.add(new Token(TipoToken.IDENTIFIER, palabra, palabra, linea));
                }
                continue;
            }

            // Identificar números
            if (Character.isDigit(c)) {
                int inicio = i;
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.' || input.charAt(i) == 'E' || input.charAt(i) == 'e')) {
                    i++;
                }
                String num = input.substring(inicio, i);
                tokens.add(new Token(TipoToken.NUMBER, num, num, linea));
                continue;
            }

            // Identificar cadenas
            if (c == '"') {
                int inicio = i + 1;
                i++;
                while (i < input.length() && input.charAt(i) != '"') {
                    if (input.charAt(i) == '\n') {
                        System.err.println("Error léxico en la línea " + linea + ": cadena no cerrada.");
                        return tokens;
                    }
                    i++;
                }
                if (i >= input.length()) {
                    System.err.println("Error léxico en la línea " + linea + ": cadena no cerrada.");
                    return tokens;
                }
                i++;
                tokens.add(new Token(TipoToken.STRING, input.substring(inicio - 1, i), input.substring(inicio, i - 1), linea));
                continue;
            }

            // Identificar operadores
            boolean matched = false;
            for (String op : OPERADORES) {
                if (input.startsWith(op, i)) {
                    tokens.add(new Token(TipoToken.fromSymbol(op), op, op, linea));
                    i += op.length();
                    matched = true;
                    break;
                }
            }

            // Manejar caracteres no reconocidos
            if (!matched) {
                if (c == ';') {
                    tokens.add(new Token(TipoToken.SEMICOLON, String.valueOf(c), null, linea));
                    i++;
                } else {
                    System.err.println("Error léxico en la línea " + linea + ": carácter no reconocido '" + c + "'.");
                    i++;
                }
            }
        }

        // Agregar token EOF al final
        tokens.add(new Token(TipoToken.EOF, "$", null, linea));
        return tokens;
    }
}