import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Analisador Léxico (Lexer): 
// Feito para conferir a ortografia do código.
// Lê o texto caractere por caractere e separa em tokens.
public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final Map<String, TokenType> palavrasReservadas = new HashMap<>();

    private int current = 0;
    private int inicioDoToken = 0;
    private int linha = 1;
    private int coluna = 1;
    private int linhaDoToken = 1;
    private int colunaDoToken = 1;

    public Lexer(String source) {
        this.source = source;

        palavrasReservadas.put("startfarm", TokenType.INICIO);
        palavrasReservadas.put("endfarm", TokenType.FIM);
        palavrasReservadas.put("sheep", TokenType.INT);
        palavrasReservadas.put("dog", TokenType.DOUBLE);
        palavrasReservadas.put("pig", TokenType.IF);
        palavrasReservadas.put("otherwise", TokenType.ELSE);
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // Guarda onde o token começa, para usar na mensagem de erro
            inicioDoToken = current;
            linhaDoToken = linha;
            colunaDoToken = coluna;
            lerToken();
        }
        tokens.add(new Token(TokenType.EOF, "", linha, coluna));
        return tokens;
    }

    private void lerToken() {
        char c = advance();

        switch (c) {
        case ' ':
        case '\t':
        case '\r':
        case '\n':
            // Espaço e quebra de linha são ignorados
            break;
        case '(':
            addToken(TokenType.LPAREN);
            break;
        case ')':
            addToken(TokenType.RPAREN);
            break;
        case '{':
            addToken(TokenType.LBRACE);
            break;
        case '}':
            addToken(TokenType.RBRACE);
            break;
        case ';':
            addToken(TokenType.SEMICOLON);
            break;
        case '+':
            addToken(TokenType.PLUS);
            break;
        case '-':
            addToken(TokenType.MINUS);
            break;
        case '*':
            addToken(TokenType.MULTIPLY);
            break;
        case '/':
            if (match('/')) {
                // Comentário: ignora tudo até o fim da linha
                while (!isAtEnd() && peek() != '\n') {
                    advance();
                }
            } else {
                addToken(TokenType.SLASH);
            }
            break;
        case '=':
            if (match('=')) {
                addToken(TokenType.EQUAL_EQUAL);
            } else {
                addToken(TokenType.ASSIGN);
            }
            break;
        case '<':
            if (match('=')) {
                addToken(TokenType.LESS_EQUAL);
            } else {
                addToken(TokenType.LESS);
            }
            break;
        case '>':
            if (match('=')) {
                addToken(TokenType.GREATER_EQUAL);
            } else {
                addToken(TokenType.GREATER);
            }
            break;
        case '!':
            if (match('=')) {
                addToken(TokenType.NOT_EQUAL);
            } else {
                throw erro("'!' sozinho não existe em Sheep, só '!='");
            }
            break;
        default:
            if (isDigito(c)) {
                lerNumero();
            } else if (isLetra(c)) {
                lerPalavra();
            } else if (Character.isLetter(c)) {
                throw erro("nome de variável não pode ter acento");
            } else {
                throw erro("caractere inesperado '" + c + "'");
            }
            break;
        }
    }

    // Lê um número inteiro (ex: 18) ou com ponto (ex: 1500.50)
    private void lerNumero() {
        while (isDigito(peek())) {
            advance();
        }

        if (peek() == '.') {
            advance();
            if (!isDigito(peek())) {
                throw erro("número mal escrito, faltou dígito depois do '.'");
            }
            while (isDigito(peek())) {
                advance();
            }
        }

        // Igual no Java, nome de variável não pode começar com número (ex: 1idade)
        if (Character.isLetter(peek()) || peek() == '_') {
            throw erro("nome de variável não pode começar com número");
        }

        addToken(TokenType.NUMBER);
    }

    // Lê uma palavra: pode ser palavra reservada (pig, sheep, . . .) ou nome de variável (ex: valor1)
    private void lerPalavra() {
        while (isLetra(peek()) || isDigito(peek())) {
            advance();
        }

        // Igual no Java, nome de variável não pode ter acento
        if (Character.isLetter(peek())) {
            throw new ErroLexico("nome de variável não pode ter acento", linha, coluna);
        }

        // Verifica se a palavra formada é reservada
        String palavra = source.substring(inicioDoToken, current);
        if (palavrasReservadas.containsKey(palavra)) {
            addToken(palavrasReservadas.get(palavra));
        } else {
            addToken(TokenType.ID);
        }
    }

    private void addToken(TokenType type) {
        String texto = source.substring(inicioDoToken, current);
        tokens.add(new Token(type, texto, linhaDoToken, colunaDoToken));
    }

    private ErroLexico erro(String mensagem) {
        return new ErroLexico(mensagem, linhaDoToken, colunaDoToken);
    }

    // Consome o caractere atual e atualiza a linha e a coluna
    private char advance() {
        char c = source.charAt(current);
        current++;
        if (c == '\n') {
            linha++;
            coluna = 1;
        } else {
            coluna++;
        }
        return c;
    }

    // Só consome o próximo caractere se for o esperado (usado no ==, !=, <= e >=)
    private boolean match(char esperado) {
        if (isAtEnd() || source.charAt(current) != esperado) {
            return false;
        }
        advance();
        return true;
    }

    // Olha o próximo caractere sem consumir
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigito(char c) {
        return c >= '0' && c <= '9';
    }

    // Letra sem acento ou _
    private boolean isLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

}
