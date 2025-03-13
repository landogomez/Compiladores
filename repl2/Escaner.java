import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Escaner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TipoToken> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TipoToken.AND);
        keywords.put("else", TipoToken.ELSE);
        keywords.put("false", TipoToken.FALSE);
        keywords.put("fun", TipoToken.FUN);
        keywords.put("for", TipoToken.FOR);
        keywords.put("if", TipoToken.IF);
        keywords.put("null", TipoToken.NULL);
        keywords.put("or", TipoToken.OR);
        keywords.put("print", TipoToken.PRINT);
        keywords.put("return", TipoToken.RETURN);
        keywords.put("true", TipoToken.TRUE);
        keywords.put("var", TipoToken.VAR);
        keywords.put("while", TipoToken.WHILE);
    }

    public Escaner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TipoToken.EOF, "$", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TipoToken.LEFT_PAREN); break;
            case ')': addToken(TipoToken.RIGHT_PAREN); break;
            case '{': addToken(TipoToken.LEFT_BRACE); break;
            case '}': addToken(TipoToken.RIGHT_BRACE); break;
            case ',': addToken(TipoToken.COMMA); break;
            case '.': addToken(TipoToken.DOT); break;
            case '-': addToken(TipoToken.MINUS); break;
            case '+': addToken(TipoToken.PLUS); break;
            case ';': addToken(TipoToken.SEMICOLON); break;
            case '*': addToken(TipoToken.STAR); break;
            case '!': addToken(match('=') ? TipoToken.BANG_EQUAL : TipoToken.BANG); break;
            case '=': addToken(match('=') ? TipoToken.EQUAL_EQUAL : TipoToken.EQUAL); break;
            case '<': addToken(match('=') ? TipoToken.LESS_EQUAL : TipoToken.LESS); break;
            case '>': addToken(match('=') ? TipoToken.GREATER_EQUAL : TipoToken.GREATER); break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TipoToken.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignorar espacios en blanco
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.err.println("Error léxico en la línea " + line + ": carácter inesperado '" + c + "'.");
                }
                break;
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TipoToken tipo) {
        addToken(tipo, null);
    }

    private void addToken(TipoToken tipo, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(tipo, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            System.err.println("Error léxico en la línea " + line + ": cadena sin terminar.");
            return;
        }

        advance(); // El cierre de la comilla
        String value = source.substring(start + 1, current - 1);
        addToken(TipoToken.STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(TipoToken.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TipoToken tipo = keywords.get(text);
        if (tipo == null) tipo = TipoToken.IDENTIFIER;
        addToken(tipo);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}