import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Escaner {
    private static final String[] PALABRAS_RESERVADAS = {
        "else", "false", "for", "int", "fun", "if", "null", "print", "return", "true", "var", "while" };
    private static final String[] OPERADORES = { "+", "-", "*", "/", "&&", "!", "=", "==", "!=", "<", "<=", ">", ">=", "||" };
    private static final String[] SIGNOS_PUNTUACION = { "(", ")", "{", "}", ",", ";"};

    private List<Token> tokens = new ArrayList<>();
    private int linea = 1;

    public List<Token> generarToken(String input) {
        int i = 0;
        while(i < input.length()) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                if (c == '\n') linea++;
                i++;
                continue;
            }
            if (Character.isDigit(c)) {
                if (c == '\n') linea++;
                    i++;
                    continue;
                
            }
            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                while (i < input.length() && input.charAt(i) != '\n') i++;
                continue;
            }

            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '*') {
                i += 2;
                while (i < input.length() - 1 && !(input.charAt(i) == '*' && input.charAt(i + 1) == '/')) {
                    if (input.charAt(i) == '\n') linea++;
                    i++;
                }
                if (i >= input.length() - 1) {
                    System.out.println("ERROR: Comentario de múltiples líneas sin cerrar en línea " + linea);
                    return tokens;
                }
                i += 2;
                continue;
            }
            if (Character.isLetter(c)) {
                int start = i;
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) i++;
                String palabra = input.substring(start, i);
                if (Arrays.asList(PALABRAS_RESERVADAS).contains(palabra)) {
                    tokens.add(new Token("PALABRA RESERVADA", palabra, palabra.equals("true") ? "1" : palabra.equals("false") ? "0" : palabra, linea));
                } else {
                    tokens.add(new Token("IDENTIFICADOR", palabra, palabra, linea));
                }
                continue;
            }

            if (Character.isDigit(c)) {
                int inicio = i;
                boolean isFloat = false;
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    if (input.charAt(i) == '.') isFloat = true;
                    i++;
                    
                }
                String num = input.substring(inicio, i);
                tokens.add(new Token(isFloat ? "FLOTANTE" : "INT", num, num, linea));
                continue;
            }

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
                tokens.add(new Token("STRING", input.substring(inicio, i), input.substring(inicio + 1, i - 1), linea));
                continue;
            }

            boolean matched = false;
            for (String op : OPERADORES) {
                if (input.startsWith(op, i)) {
                    tokens.add(new Token("OPERADOR", op, op, linea));
                    i += op.length();
                    matched = true;
                    break;
                }
            }
            if (matched) continue;
            
            // Detectar signos de puntuación
            for (String punc : SIGNOS_PUNTUACION) {
                if (input.startsWith(punc, i)) {
                    tokens.add(new Token("SIGNO DE PUNTUACION", punc, punc, linea));
                    i += punc.length();
                    matched = true;
                    break;
                }
            }
            if (matched) continue;
            
            // Manejo de errores léxicos
            if (c == '[' || c == ']') {
                System.out.println("ERROR: Se detectó un carácter no válido " + c + " en la línea " + linea);
                i++;
                continue;
            }
            System.out.println("ERROR: Carácter inesperado '" + c + "' en la línea " + linea);
            i++;
        }
        
        // Agregar token de fin de cadena
        tokens.add(new Token("EOF", "$", "", -1));
        return tokens;

            
    }
}
