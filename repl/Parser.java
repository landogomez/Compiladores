import java.util.List;

public class Parser {
    private Token preanalisis;
    private List<Token> tokens;
    private int posicion;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.preanalisis = tokens.get(0);
        this.posicion = 0;
    
    }
    public boolean parse() {
        try {
            program();  // Llama al procedimiento principal del análisis

            if (preanalisis.getTipo() == TipoToken.EOF) {
                System.out.println("Programa válido");
                return true;
            } else {
                lanzarError("EOF", preanalisis);
            }

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return false; 
    }

    // Método principal del análisis sintáctico
    private void program() {
        while (preanalisis.getTipo() != TipoToken.EOF) {
            statement(); // Analiza una declaración o instrucción
        }
    }

    // Método para analizar declaraciones o instrucciones
    private void statement() {
        switch (preanalisis.getTipo()) {
            case VAR:
                varDeclaration();
                break;
            case PRINT:
                printStatement();
                break;
            case INPUT:
                inputStatement();
                break;
            case EXIT:
                exitStatement();
                break;
            case IF:
                ifStatement();
                break;
            case WHILE:
                whileStatement();
                break;
            default:
                lanzarError("Declaración válida", preanalisis);
        }
    }

    // Declaración de variables: var IDENTIFIER = expresion;
    private void varDeclaration() {
        consumir(TipoToken.VAR); // Consume 'var'
        consumir(TipoToken.IDENTIFIER); // Consume el identificador
        consumir(TipoToken.EQUAL); // Consume '='
        expression(); // Analiza la expresión
        consumir(TipoToken.SEMICOLON); // Consume ';'
    }

    // Instrucción de impresión: print expresion;
    private void printStatement() {
        consumir(TipoToken.PRINT); // Consume 'print'
        expression(); // Analiza la expresión
        consumir(TipoToken.SEMICOLON); // Consume ';'
    }

    // Instrucción de entrada: input IDENTIFIER;
    private void inputStatement() {
        consumir(TipoToken.INPUT); // Consume 'input'
        consumir(TipoToken.IDENTIFIER); // Consume el identificador
        consumir(TipoToken.SEMICOLON); // Consume ';'
    }

    // Instrucción de salida: exit;
    private void exitStatement() {
        consumir(TipoToken.EXIT); // Consume 'exit'
        consumir(TipoToken.SEMICOLON); // Consume ';'
    }

    // Instrucción condicional: if (expresion) { ... }
    private void ifStatement() {
        consumir(TipoToken.IF); // Consume 'if'
        consumir(TipoToken.LEFT_PAREN); // Consume '('
        expression(); // Analiza la condición
        consumir(TipoToken.RIGHT_PAREN); // Consume ')'
        consumir(TipoToken.LEFT_BRACE); // Consume '{'
        while (preanalisis.getTipo() != TipoToken.RIGHT_BRACE) {
            statement(); // Analiza las instrucciones dentro del bloque
        }
        consumir(TipoToken.RIGHT_BRACE); // Consume '}'
    }

    // Instrucción de bucle: while (expresion) { ... }
    private void whileStatement() {
        consumir(TipoToken.WHILE); // Consume 'while'
        consumir(TipoToken.LEFT_PAREN); // Consume '('
        expression(); // Analiza la condición
        consumir(TipoToken.RIGHT_PAREN); // Consume ')'
        consumir(TipoToken.LEFT_BRACE); // Consume '{'
        while (preanalisis.getTipo() != TipoToken.RIGHT_BRACE) {
            statement(); // Analiza las instrucciones dentro del bloque
        }
        consumir(TipoToken.RIGHT_BRACE); // Consume '}'
    }

    // Método para analizar expresiones (simplificado)
    private void expression() {
        if (preanalisis.getTipo() == TipoToken.NUMBER || preanalisis.getTipo() == TipoToken.IDENTIFIER) {
            consumir(preanalisis.getTipo()); // Consume un número o identificador
        } else {
            lanzarError("Expresión válida", preanalisis);
        }
    }

    // Método para consumir un token esperado
    private void consumir(TipoToken tipoEsperado) {
        if (preanalisis.getTipo() == tipoEsperado) {
            avanzar(); // Avanza al siguiente token
        } else {
            lanzarError(tipoEsperado.name(), preanalisis);
        }
    }

    // Método para avanzar al siguiente token
    private void avanzar() {
        posicion++;
        if (posicion < tokens.size()) {
            preanalisis = tokens.get(posicion);
        }
    }

    // Método para lanzar un error sintáctico
    private void lanzarError(String esperado, Token recibido) {
        String esperadoLiteral = traducirToken(esperado); // Traduce el token esperado
        String recibidoLiteral = traducirToken(recibido.getLexema()); // Traduce el token recibido

        String mensaje = String.format(
            "Error sintáctico en la línea %d. Se esperaba %s pero se recibió %s",
            recibido.getLinea(), esperadoLiteral, recibidoLiteral
        );
        throw new RuntimeException(mensaje);
    }

    // Método auxiliar para traducir tokens a sus representaciones literales
    private String traducirToken(String token) {
        switch (token) {
            case "SEMICOLON": return "';'";
            case "EQUAL": return "'='";
            case "LEFT_PAREN": return "'('";
            case "RIGHT_PAREN": return "')'";
            case "LEFT_BRACE": return "'{'";
            case "RIGHT_BRACE": return "'}'";
            case "VAR": return "'var'";
            case "PRINT": return "'print'";
            case "INPUT": return "'input'";
            case "EXIT": return "'exit'";
            case "IF": return "'if'";
            case "WHILE": return "'while'";
            default: return token; // Devuelve el token original si no hay traducción
        }
    }
}
