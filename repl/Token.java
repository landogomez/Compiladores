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

    @Override
    public String toString() {
        if (tipo == TipoToken.BANG || tipo == TipoToken.BANG_EQUAL || tipo == TipoToken.EQUAL || tipo == TipoToken.EQUAL_EQUAL ||
            tipo == TipoToken.GREATER || tipo == TipoToken.GREATER_EQUAL || tipo == TipoToken.LESS || tipo == TipoToken.LESS_EQUAL) {
            return String.format("<%s, Linea: %d>", tipo.name(), linea);
        } else {
            return String.format("<%s, Lexema: %s, Linea: %d>", tipo.name(), lexema, linea);
        }
    }
}