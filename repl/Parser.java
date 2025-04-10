import java.util.List;
import java.util.Iterator;

public class Parser {
    private final Iterator<Token> tokens;
    private Token preanalisis;

    public Parser(List<Token> tokens) {
        this.tokens = tokens.iterator();
        advance(); // Inicializa el primer token
    }


    // Avanza al siguiente token
    private void advance() {
        if (tokens.hasNext()) {
            preanalisis = tokens.next();
            System.out.println("Avanzando al token: " + preanalisis);
        } else {
            preanalisis = null; // Fin de los tokens
            System.out.println("No hay más tokens.");
        }
    }

    // Coincide el token actual con el esperado
    private void match(TipoToken expectedType) {
        System.out.println("Esperando: " + expectedType + ", Encontrado: " + (preanalisis != null ? preanalisis.tipo : "EOF"));
        if (preanalisis != null && preanalisis.tipo == expectedType) {
            advance();
        } else {
            TipoToken found = (preanalisis != null) ? preanalisis.tipo : null;
            int line = (preanalisis != null) ? preanalisis.getLine() : -1;
            throw new RuntimeException("Error sintáctico en la línea " + line + ". Se esperaba " + expectedType + " pero se recibió " + (found != null ? found : "EOF"));
        }
    }

    // Método inicial para el análisis sintáctico
    public void parse() {
        declaration();
        if (preanalisis != null && preanalisis.tipo != TipoToken.EOF) {
            throw new RuntimeException("Error sintáctico: código extra después del final del programa.");
        }
        System.out.println("El código fuente es válido.");
    }

    // Implementación de la regla DECLARATION
    private void declaration() {
        if (preanalisis == null || preanalisis.tipo == TipoToken.EOF) {
            return; // Ɛ (epsilon)
        }

        switch (preanalisis.tipo) {
            case FUN:
                funDecl();
                declaration();
                break;
            case VAR:
                varDecl();
                declaration();
                break;
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                if (preanalisis != null && preanalisis.tipo == TipoToken.EQUAL) {
                    match(TipoToken.EQUAL);
                    expression();
                }
                match(TipoToken.SEMICOLON);
                declaration();
                break;
            case NUMBER:
                statement();
                declaration();
                break;
            case RIGHT_BRACE:
                return; // Ɛ (epsilon) para cerrar un bloque
            case PRINT:
                printStmt();
                break;
            case RETURN:
                returnStmt();
                break;
            case WHILE:
                whileStmt();
                break;
            case LEFT_BRACE:
                block();
                break;
            case IF:
                ifStmt();
                declaration();
                break;
            case FOR:
                forStmt();
                declaration();
                break;
            default:
                throw new RuntimeException("Error sintáctico en la línea " + preanalisis.getLine() + ". Token inesperado: " + preanalisis.getType());
        }
    }

    // Implementación de la regla FUN_DECL
    private void funDecl() {
        match(TipoToken.FUN); // Coincide con la palabra clave "fun"
        match(TipoToken.IDENTIFIER); // Coincide con el nombre de la función
        match(TipoToken.LEFT_PAREN); // Coincide con '('

        // Procesar la lista de parámetros (uno o varios)
        if (preanalisis.tipo != TipoToken.RIGHT_PAREN) {
                match(TipoToken.IDENTIFIER); // Coincide con cada parámetro
        }

        match(TipoToken.RIGHT_PAREN); // Coincide con ')'
        block(); // Procesa el cuerpo de la función
    }

    // Implementación de la regla VAR_DECL
    private void varDecl() {
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        if (preanalisis != null && preanalisis.tipo == TipoToken.EQUAL) {
            match(TipoToken.EQUAL);
            expression();
        }
        match(TipoToken.SEMICOLON);
    }

    // Implementación de la regla STATEMENT
    private void statement() {
        switch (preanalisis.getType()) {
            case "IDENTIFIER":
            case "NUMBER":
                exprStmt();
                break;
            case "for":
                forStmt();
                break;
            case "if":
                ifStmt();
                break;
            case "PRINT":
                printStmt();
                break;
            case "return":
                returnStmt();
                break;
            case "while":
                whileStmt();
                break;
            case "{":
                block();
                break;
            case "VAR":
                varDecl();
                break;
            case "EOF":
                return; // Ɛ (epsilon) para cerrar un bloque
            default:
                throw new RuntimeException("Error sintáctico en la línea " + preanalisis.getLine() + ". Token inesperado: " + preanalisis.getType());
        }
    }

    // Implementación de la regla EXPR_STMT
    private void exprStmt() {
        expression();
        match(TipoToken.SEMICOLON);
    }

    // Implementación de la regla FOR_STMT
    private void forStmt() {
        match(TipoToken.FOR); // Coincide con la palabra clave "for"
        match(TipoToken.LEFT_PAREN); // Coincide con '('

        // Procesar la inicialización (opcional)
        if (preanalisis.tipo == TipoToken.VAR) {
            varDecl(); // Declaración de variable
        } else if (preanalisis.tipo != TipoToken.SEMICOLON) {
            expression(); // Procesa una expresión
        }
        match(TipoToken.SEMICOLON); // Coincide con ';'

        // Procesar la condición (opcional)
        if (preanalisis.tipo != TipoToken.SEMICOLON) {
            expression(); // Procesa la condición
        }
        match(TipoToken.SEMICOLON); // Coincide con ';'

        // Procesar la actualización (opcional)
        if (preanalisis.tipo != TipoToken.RIGHT_PAREN) {
            expression(); // Procesa la actualización
        }
        match(TipoToken.RIGHT_PAREN); // Coincide con ')'

        // Procesar el cuerpo del bucle
        if (preanalisis.tipo == TipoToken.LEFT_BRACE) {
            block(); // Procesa un bloque de código
        } else {
            statement(); // Procesa una única sentencia
        }
    }

    // Implementación de la regla FOR_STMT_INIT
    private void forStmtInit() {
        if (preanalisis.getType().equals("var")) {
            varDecl();
        } else if (preanalisis.getType().equals(";")) {
            match(TipoToken.SEMICOLON);
        } else {
            exprStmt();
        }
    }

    // Implementación de la regla FOR_STMT_COND
    private void forStmtCond() {
        if (!preanalisis.getType().equals(";")) {
            expression();
        }
        match(TipoToken.SEMICOLON);
    }

    // Implementación de la regla FOR_STMT_INC
    private void forStmtInc() {
        if (!preanalisis.getType().equals(")")) {
            expression();
        }
    }

    // Implementación de la regla IF_STMT
    private void ifStmt() {
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        expression(); // Procesa la condición del if
        match(TipoToken.RIGHT_PAREN);

        // Procesar el bloque o la sentencia del if
        if (preanalisis.tipo == TipoToken.LEFT_BRACE) {
            block(); // Si es un bloque, procesarlo
        } else {
            statement(); // Si no es un bloque, procesar una única sentencia
        }

        // Procesar el else opcional
        if (preanalisis != null && preanalisis.tipo == TipoToken.ELSE) {
            match(TipoToken.ELSE);

            // Verificar si el else contiene otro if (else if)
            if (preanalisis.tipo == TipoToken.IF) {
                ifStmt(); // Llamar recursivamente a ifStmt para manejar else if
            } else if (preanalisis.tipo == TipoToken.LEFT_BRACE) {
                block(); // Procesar el bloque del else
            } else {
                statement(); // Procesar una única sentencia del else
            }
        }
    }

    // Implementación de la regla PRINT_STMT
    private void printStmt() {
        match(TipoToken.PRINT); // Coincide con el token PRINT
        expression();   // Procesa la expresión (por ejemplo, STRING o IDENTIFIER)
        match(TipoToken.SEMICOLON);     // Coincide con el punto y coma
    }

    // Implementación de la regla RETURN_STMT
    private void returnStmt() {
        match(TipoToken.RETURN); // Coincide con el token RETURN
        returnExpOpc();
        match(TipoToken.SEMICOLON); // Coincide con el punto y coma
    }

    // Implementación de la regla RETURN_EXP_OPC
    private void returnExpOpc() {
        if (preanalisis != null && !preanalisis.getType().equals(";")) {
            expression();
        }
    }

    // Implementación de la regla EXPRESSION
    private void expression() {
        logicalOr();
    }

    private void logicalOr() {
        logicalAnd();
        while (preanalisis.tipo == TipoToken.OR) {
            match(TipoToken.OR); // Coincide con el operador lógico "or"
            logicalAnd();
        }
    }

    private void logicalAnd() {
        equality();
        while (preanalisis.tipo == TipoToken.AND) {
            match(TipoToken.AND); // Coincide con el operador lógico "and"
            equality();
        }
    }

    private void equality() {
        comparison();
        while (preanalisis.tipo == TipoToken.EQUAL_EQUAL || preanalisis.tipo == TipoToken.BANG_EQUAL) {
            match(preanalisis.tipo); // Coincide con == o !=
            comparison();
        }
    }

    private void comparison() {
        term();
        while (preanalisis.tipo == TipoToken.LESS || preanalisis.tipo == TipoToken.LESS_EQUAL ||
               preanalisis.tipo == TipoToken.GREATER || preanalisis.tipo == TipoToken.GREATER_EQUAL) {
            match(preanalisis.tipo); // Coincide con <, <=, >, >=
            term();
        }
    }

    private void term() {
        factor();
        while (preanalisis.tipo == TipoToken.PLUS || preanalisis.tipo == TipoToken.MINUS) {
            match(preanalisis.tipo); // Coincide con + o -
            factor();
        }
    }

    private void factor() {
        unary();
        while (preanalisis.tipo == TipoToken.STAR || preanalisis.tipo == TipoToken.SLASH) {
            match(preanalisis.tipo); // Coincide con * o /
            unary();
        }
    }

    private void unary() {
        if (preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS) {
            match(preanalisis.tipo); // Coincide con ! o -
            unary();
        } else {
            primary();
        }
    }

    private void primary() {
        if (preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER) {
            match(preanalisis.tipo);
        } else if (preanalisis.tipo == TipoToken.LEFT_PAREN) {
            match(TipoToken.LEFT_PAREN);
            expression();
            match(TipoToken.RIGHT_PAREN);
        } else {
            throw new RuntimeException("Error sintáctico en la línea " + preanalisis.getLine() + ". Se esperaba una expresión.");
        }
    }

    // Implementación de la regla WHILE_STMT
    private void whileStmt() {
        match(TipoToken.WHILE); // Coincide con la palabra clave "while"
        match(TipoToken.LEFT_PAREN); // Coincide con '('
        expression(); // Procesa la condición del while
        match(TipoToken.RIGHT_PAREN); // Coincide con ')'

        // Procesar el cuerpo del while
        if (preanalisis.tipo == TipoToken.LEFT_BRACE) {
            block(); // Si es un bloque, procesarlo
        } else {
            statement(); // Si no es un bloque, procesar una única sentencia
        }
    }

    // Implementación de la regla BLOCK
    private void block() {
        match(TipoToken.LEFT_BRACE); // Coincide con la llave de apertura '{'
        while (preanalisis != null && preanalisis.tipo != TipoToken.RIGHT_BRACE) {
            declaration(); // Procesa las declaraciones dentro del bloque
        }
        match(TipoToken.RIGHT_BRACE); // Coincide con la llave de cierre '}'
    }
}