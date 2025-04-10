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
        } else {
            preanalisis = null; // Fin de los tokens
        }
    }

    // Coincide el token actual con el esperado
    private void match(String expectedType) {
        if (preanalisis != null && preanalisis.getType().equals(expectedType)) {
            advance();
        } else {
            String found = (preanalisis != null) ? preanalisis.getType() : "EOF";
            int line = (preanalisis != null) ? preanalisis.getLine() : -1;
            throw new RuntimeException("Error sintáctico en la línea " + line + ". Se esperaba " + expectedType + " pero se recibió " + found);
        }
    }

    // Método inicial para el análisis sintáctico
    public void parse() {
        declaration();
        if (preanalisis != null) {
            throw new RuntimeException("Error sintáctico: código extra después del final del programa.");
        }
        System.out.println("El código fuente es válido.");
    }

    // Implementación de la regla DECLARATION
    private void declaration() {
        if (preanalisis == null) {
            return; // Ɛ (epsilon)
        }

        switch (preanalisis.getType()) {
            case "fun":
                funDecl();
                declaration();
                break;
            case "var":
                varDecl();
                declaration();
                break;
            case "IDENTIFIER":
            case "NUMBER":
                statement();
                declaration();
                break;
            case "}":
                return; // Ɛ (epsilon) para cerrar un bloque
            default:
                throw new RuntimeException("Error sintáctico en la línea " + preanalisis.getLine() + ". Token inesperado: " + preanalisis.getType());
        }
    }

    // Implementación de la regla FUN_DECL
    private void funDecl() {
        match("fun");
        match("IDENTIFIER");
        match("(");
        match(")");
        block();
    }

    // Implementación de la regla VAR_DECL
    private void varDecl() {
        match("var");
        match("IDENTIFIER");
        if (preanalisis != null && preanalisis.getType().equals("=")) {
            match("=");
            expression();
        }
        match(";");
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
            case "PRINT": // Asegúrate de que el tipo sea "PRINT" en mayúsculas
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
            default:
                throw new RuntimeException("Error sintáctico en la línea " + preanalisis.getLine() + ". Token inesperado: " + preanalisis.getType());
        }
    }

    // Implementación de la regla EXPR_STMT
    private void exprStmt() {
        expression();
        match(";");
    }

    // Implementación de la regla FOR_STMT
    private void forStmt() {
        match("for");
        match("(");
        forStmtInit();
        forStmtCond();
        forStmtInc();
        match(")");
        statement();
    }

    // Implementación de la regla FOR_STMT_INIT
    private void forStmtInit() {
        if (preanalisis.getType().equals("var")) {
            varDecl();
        } else if (preanalisis.getType().equals(";")) {
            match(";");
        } else {
            exprStmt();
        }
    }

    // Implementación de la regla FOR_STMT_COND
    private void forStmtCond() {
        if (!preanalisis.getType().equals(";")) {
            expression();
        }
        match(";");
    }

    // Implementación de la regla FOR_STMT_INC
    private void forStmtInc() {
        if (!preanalisis.getType().equals(")")) {
            expression();
        }
    }

    // Implementación de la regla IF_STMT
    private void ifStmt() {
        match("if");
        match("(");
        expression();
        match(")");
        statement();
        elseStatement();
    }

    // Implementación de la regla ELSE_STATEMENT
    private void elseStatement() {
        if (preanalisis != null && preanalisis.getType().equals("else")) {
            match("else");
            statement();
        }
    }

    // Implementación de la regla PRINT_STMT
    private void printStmt() {
        match("PRINT"); // Coincide con el token PRINT
        expression();   // Procesa la expresión (por ejemplo, STRING o IDENTIFIER)
        match(";");     // Coincide con el punto y coma
    }

    // Implementación de la regla RETURN_STMT
    private void returnStmt() {
        match("return");
        returnExpOpc();
        match(";");
    }

    // Implementación de la regla RETURN_EXP_OPC
    private void returnExpOpc() {
        if (preanalisis != null && !preanalisis.getType().equals(";")) {
            expression();
        }
    }

    // Implementación de la regla EXPRESSION
    private void expression() {
        if (preanalisis.getType().equals("STRING")) {
            match("STRING");
        } else if (preanalisis.getType().equals("IDENTIFIER")) {
            match("IDENTIFIER");
        } else if (preanalisis.getType().equals("NUMBER")) {
            match("NUMBER");
        } else {
            throw new RuntimeException("Error sintáctico en la línea " + preanalisis.getLine() + ". Se esperaba una expresión.");
        }
    }

    // Implementación de la regla WHILE_STMT
    private void whileStmt() {
        match("while");
        match("(");
        expression();
        match(")");
        statement();
    }

    // Implementación de la regla BLOCK
    private void block() {
        match("{");
        declaration();
        match("}");
    }
}