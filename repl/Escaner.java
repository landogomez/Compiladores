import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Escaner {
    private static final String[] PALABRAS_RESERVADAS = {"if", "else", "while", "for", "true", "false"};
    private static final String[] OPERADORES = {"!", "!=", "=", "==", ">", ">=", "<", "<="};
    private List<Token> tokens = new ArrayList<>();
    private int linea = 1;
    private int i = 0;

    public List<Token> generarToken(String input) {
        while (i < input.length()) {
            char c = input.charAt(i);
            if (Character.isWhitespace(c)) {
                if (c == '\n') linea++;
                i++;
                continue;
            }
            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                while (i < input.length() && input.charAt(i) != '\n') i++;
                continue;
            }
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
            if (Character.isDigit(c)) {
                int inicio = i;
                boolean isFloat = false;
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    if (input.charAt(i) == '.') isFloat = true;
                    i++;
                }
                String num = input.substring(inicio, i);
                tokens.add(new Token(isFloat ? TipoToken.NUMBER : TipoToken.NUMBER, num, num, linea));
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
                tokens.add(new Token(TipoToken.STRING, input.substring(inicio, i), input.substring(inicio + 1, i - 1), linea));
            }
            boolean matched = false;
            for (String op : OPERADORES) {
                if (input.startsWith(op, i)) {
                    tokens.add(new Token(TipoToken.fromSymbol(op), op, op, linea));
                    i += op.length();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                System.err.println("Error léxico en la línea " + linea + ": carácter no reconocido '" + c + "'.");
                i++;
            }
        }
        return tokens;
    }
}