public enum TipoToken {
    // Tokens de un sólo caracter
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // Tokens de uno o dos caracteres
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literales
    IDENTIFIER, STRING, NUMBER,

    // Palabras clave
    AND, ELSE, FALSE, FUN, FOR, IF, NULL, OR,
    PRINT, RETURN, TRUE, VAR, WHILE,

    EOF;

    public static TipoToken fromSymbol(String symbol) {
        switch (symbol) {
            case "!": return BANG;
            case "!=": return BANG_EQUAL;
            case "=": return EQUAL;
            case "==": return EQUAL_EQUAL;
            case ">": return GREATER;
            case ">=": return GREATER_EQUAL;
            case "<": return LESS;
            case "<=": return LESS_EQUAL;
            default: throw new IllegalArgumentException("Símbolo no reconocido: " + symbol);
        }
    }
}