// Tipos de token que a MiniLang reconhece
public enum TokenType {

    // Palavras reservadas
    INICIO, FIM, INT, DOUBLE, IF, ELSE,

    // Nome de variável e número
    ID, NUMBER,

    // Símbolos
    LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON,
    ASSIGN, PLUS, MINUS, MULTIPLY, SLASH,
    EQUAL_EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,

    EOF

}
