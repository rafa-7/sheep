import java.util.List;

// Analisador Sintático (Parser): confere a "gramática" do código, ou seja,
// se os tokens que o Lexer separou estão na ordem certa.
// Cada regra da gramática (está no README) virou um método parse.
public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // startfarm <declarações> <pig-otherwise> endfarm
    public void parsePrograma() {
        matchExact(TokenType.INICIO, "O programa tem que começar com 'startfarm'");

        // <n variáveis>
        while (check(TokenType.INT) || check(TokenType.DOUBLE)) {
            parseDeclaracao();
        }

        // <um único bloco pig-otherwise>
        if (!check(TokenType.IF)) {
            throw erro("esperado uma declaração de variável (sheep ou dog) ou o 'pig'");
        }
        parsePigs();

        if (check(TokenType.IF)) {
            throw erro("o programa só pode ter um bloco pig-otherwise");
        }
        matchExact(TokenType.FIM, "esperado 'fim' depois do bloco if-else");
        matchExact(TokenType.EOF, "não pode ter nada depois de 'endfarm'");
    }

    // <tipo> <nome> = <valor inicial>;   (o "= valor inicial" é opcional)
    private void parseDeclaracao() {
        Token tipo = advance();
        Token nome = matchExact(TokenType.ID, "esperado o nome da variável depois de '" + tipo.getLexeme() + "'");

        if (match(TokenType.ASSIGN)) {
            parseExpressao();
        }

        matchPontoEVirgula("esperado ';' no fim da declaração de '" + nome.getLexeme() + "'");
    }

    // if ( comparacao ) { comandos } else { comandos }   (o else é opcional)
    private void parsePigs() {
        matchExact(TokenType.IF, "esperado 'pig'");
        matchExact(TokenType.LPAREN, "esperado '(' depois de 'pig'");
        parseComparacao();
        matchExact(TokenType.RPAREN, "esperado ')' para fechar a condição do 'pig'");
        parseBloco("if");

        // O else é opcional (apenas "espia" se existe)
        if (match(TokenType.ELSE)) {
            parseBloco("else");
        }
    }

    // { comandos }
    private void parseBloco(String nomeDoBloco) {
        matchExact(TokenType.LBRACE, "esperado '{' para abrir o bloco do '" + nomeDoBloco + "'");

        // Lê os comandos até achar o '}'. Se aparecer else, fim ou o arquivo acabar,
        // é porque esqueceram de fechar o bloco
        while (!check(TokenType.RBRACE) && !check(TokenType.ELSE)
                && !check(TokenType.FIM) && !check(TokenType.EOF)) {
            parseComando();
        }

        matchExact(TokenType.RBRACE, "esperado '}' para fechar o bloco do '" + nomeDoBloco + "'");
    }

    // Atribuição
    private void parseComando() {
        if (check(TokenType.ID)) {
            parseAtribuicao();
        } else if (check(TokenType.INT) || check(TokenType.DOUBLE)) {
            throw erro("variável só pode ser declarada antes do if");
        } else if (check(TokenType.IF)) {
            throw erro("não pode ter outro if dentro do bloco");
        } else {
            throw erro("esperado um comando (atribuição)");
        }
    }

    // nome = expressao;
    private void parseAtribuicao() {
        Token nome = advance();
        matchExact(TokenType.ASSIGN, "esperado '=' depois de '" + nome.getLexeme() + "'");
        parseExpressao();
        matchPontoEVirgula("esperado ';' no fim da atribuição de '" + nome.getLexeme() + "'");
    }

    // Regra da comparação: expressao operador expressao (ex: idade >= 18)
    private void parseComparacao() {
        parseExpressao();

        TokenType op = peek().getType();
        if (op == TokenType.EQUAL_EQUAL || op == TokenType.NOT_EQUAL
                || op == TokenType.LESS || op == TokenType.GREATER
                || op == TokenType.LESS_EQUAL || op == TokenType.GREATER_EQUAL) {
            advance();
        } else {
            throw erro("esperado um operador relacional (==, !=, <, >, <=, >=)");
        }

        parseExpressao();
    }

    // A expressão é dividida em termo e fator por causa da ordem de precedência:
    // o * e o / são feitos antes do + e do -
    private void parseExpressao() {
        parseTermo();
        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            parseTermo();
        }
    }

    private void parseTermo() {
        parseFator();
        while (match(TokenType.MULTIPLY) || match(TokenType.SLASH)) {
            parseFator();
        }
    }

    // Fator: número, variável, ( expressao ) ou número negativo (ex: -5)
    private void parseFator() {
        if (match(TokenType.NUMBER) || match(TokenType.ID)) {
            return;
        }
        if (match(TokenType.LPAREN)) {
            parseExpressao();
            matchExact(TokenType.RPAREN, "esperado ')' para fechar o parêntese");
            return;
        }
        if (match(TokenType.MINUS)) {
            parseFator();
            return;
        }
        throw erro("esperado um número, uma variável ou '('");
    }

    // Métodos auxiliares

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        Token token = peek();
        if (token.getType() != TokenType.EOF) {
            current++;
        }
        return token;
    }

    private boolean check(TokenType type) {
        return peek().getType() == type;
    }

    // Consome o token só se ele for do tipo pedido
    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    // O token tem que ser do tipo esperado, senão é erro
    private Token matchExact(TokenType expectedType, String mensagem) {
        if (check(expectedType)) {
            return advance();
        }
        throw erro(mensagem);
    }

    // Se faltar o ';' o próximo token já está na outra linha,
    // por isso o erro aponta para o fim do token anterior
    private void matchPontoEVirgula(String mensagem) {
        if (!match(TokenType.SEMICOLON)) {
            throw erroDepoisDoAnterior(mensagem + encontrado());
        }
    }

    private ErroSintatico erro(String mensagem) {
        if (check(TokenType.EOF)) {
            return erroDepoisDoAnterior(mensagem + encontrado());
        }
        Token token = peek();
        return new ErroSintatico(mensagem + encontrado(), token.getLinha(), token.getColuna());
    }

    private ErroSintatico erroDepoisDoAnterior(String mensagem) {
        if (current == 0) {
            return new ErroSintatico(mensagem, 1, 1);
        }
        Token anterior = tokens.get(current - 1);
        int coluna = anterior.getColuna() + anterior.getLexeme().length();
        return new ErroSintatico(mensagem, anterior.getLinha(), coluna);
    }

    private String encontrado() {
        if (check(TokenType.EOF)) {
            return ", mas o arquivo acabou";
        }
        return ", mas foi encontrado '" + peek().getLexeme() + "'";
    }

}
