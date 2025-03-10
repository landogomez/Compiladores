public class Token {
    String nombre, lexema, literal;
    int linea;

    public Token(String nombre, String lexema, String literal, int linea) {
        this.nombre = nombre;
        this.lexema = lexema;
        this.literal = literal;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s, %s, %d>", nombre, lexema, literal, linea);
    }
}
