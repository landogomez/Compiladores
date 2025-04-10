public class Token {
    TipoToken tipo;
    String lexema, literal;
    int linea;

    public Token(TipoToken tipo, String lexema, String literal, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
        this.linea = linea;
    }
    public String getType() {
        return tipo.name();
    }

    public String getLexeme() {
        return lexema;
    }

    public int getLine() {
        return linea;
    }

    @Override
    public String toString() {
        if (tipo == TipoToken.STRING) {
            return String.format("<%s, lexema: %s, literal: %s, línea: %d>", tipo.name(), lexema, literal, linea);
        } else if (tipo == TipoToken.SEMICOLON || tipo == TipoToken.EOF) {
            return String.format("<%s, línea: %d>", tipo.name(), linea);
        } else if (tipo == TipoToken.BANG || tipo == TipoToken.BANG_EQUAL || tipo == TipoToken.EQUAL || tipo == TipoToken.EQUAL_EQUAL ||
                   tipo == TipoToken.GREATER || tipo == TipoToken.GREATER_EQUAL || tipo == TipoToken.LESS || tipo == TipoToken.LESS_EQUAL ||
                   tipo == TipoToken.AND || tipo == TipoToken.ELSE || tipo == TipoToken.FALSE || tipo == TipoToken.FUN || tipo == TipoToken.FOR ||
                   tipo == TipoToken.IF || tipo == TipoToken.NULL || tipo == TipoToken.OR || tipo == TipoToken.PRINT || tipo == TipoToken.RETURN ||
                   tipo == TipoToken.TRUE || tipo == TipoToken.VAR || tipo == TipoToken.WHILE || tipo == TipoToken.STAR || tipo == TipoToken.PLUS || tipo == TipoToken.MINUS || tipo == TipoToken.LEFT_PAREN || tipo == TipoToken.RIGHT_PAREN || tipo == TipoToken.LEFT_BRACE || tipo == TipoToken.RIGHT_BRACE || tipo == TipoToken.COMMA || tipo == TipoToken.DOT) {
            return String.format("<%s, línea: %d>", tipo.name(), linea);
        } else {
            return String.format("<%s, lexema: %s, línea: %d>", tipo.name(), lexema, linea);
        }
    }
}