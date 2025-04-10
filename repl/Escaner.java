import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Escaner {
    private static final String[] PALABRAS_RESERVADAS = {"and", "else", "false", "fun", "for", "if", "null", "or", "print", "return", "true", "var", "while", "input"};
    private static final String[] OPERADORES = {"!=", "==", ">=", "<=", "!", "=", ">", "<", "*", "+", "-", ";", ",", ".", "(", ")", "{", "}"};
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

            //Identificar números y flotantes
if (Character.isDigit(c)) {
    int inicio = i;
    boolean esFloat = false;
    boolean esDouble = false;

    // Avanzar mientras sea un dígito
    while (i < input.length() && Character.isDigit(input.charAt(i))) {
        i++;
    }

    // Verificar si hay un punto decimal seguido de más dígitos
    if (i < input.length() && input.charAt(i) == '.' && i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
        esFloat = true;
        i++; // Saltar el punto decimal
        while (i < input.length() && Character.isDigit(input.charAt(i))) {
            i++;
        }
    }
// Verificar si hay una E o e para notación exponencial
    if (i < input.length()  && (input.charAt(i) == 'E' || input.charAt(i) == 'e')) {
        esDouble = true;
        i++; // Saltar la E o e

        // Verificar si hay un signo (+ o -) después de la E
        if (i < input.length() && (input.charAt(i) == '+' || input.charAt(i) == '-')) {
            i++; // Saltar el signo
        }

        // Avanzar mientras sea un dígito
        while (i < input.length() && Character.isDigit(input.charAt(i))) {
            i++;
        }
    }

    // Extraer el número
    String num = input.substring(inicio, i);

    // Crear el token correspondiente
    if (esDouble) {
        tokens.add(new Token(TipoToken.DOUBLE, num, num, linea));
    } if (esFloat) {
        tokens.add(new Token(TipoToken.FLOAT, num, num, linea));
    } else {
        tokens.add(new Token(TipoToken.NUMBER, num, num, linea));
    }
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

            // Identificar operadores y símbolos
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
                System.err.println("Error léxico en la línea " + linea + ": carácter no reconocido '" + c + "'.");
                i++;
            }
        }

        // Agregar token EOF al final
        tokens.add(new Token(TipoToken.EOF, "$", null, linea));
        return tokens;
    }
}