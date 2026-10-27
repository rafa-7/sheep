// Guarda a linha e a coluna para mostrar onde está o erro.
public class Token {

    private final TokenType type;
    private final String lexeme;
    private final int linha;
    private final int coluna;

    public Token(TokenType type, String lexeme, int linha, int coluna) {
        this.type = type;
        this.lexeme = lexeme;
        this.linha = linha;
        this.coluna = coluna;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public String toString() {
        // ID e NUMBER mostram o texto junto, ex: ID(idade)
        if (type == TokenType.ID || type == TokenType.NUMBER) {
            return type + "(" + lexeme + ")";
        }
        return type + "";
    }

}
