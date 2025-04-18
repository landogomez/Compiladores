public enum TipoToken {
    // Tokens de un sólo caracter
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, STAR,

    // Tokens de uno o dos caracteres
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,


    // Literales
    IDENTIFIER, STRING, NUMBER, FLOAT, DOUBLE,

    // Palabras clave
    AND, ELSE, FALSE, FUN, FOR, IF, NULL, OR,
    PRINT, RETURN, TRUE, VAR, WHILE,

    //Nueva Funcionalidad
    INPUT,

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
            case ";": return SEMICOLON;
            case "*": return STAR;
            case "+": return PLUS;
            case "-": return MINUS;
            case "(": return LEFT_PAREN;
            case ")": return RIGHT_PAREN;
            case "{": return LEFT_BRACE;
            case "}": return RIGHT_BRACE;
            case ",": return COMMA;
            case ".": return DOT;
            default: throw new IllegalArgumentException("Símbolo no reconocido: " + symbol);
        }
    }
}