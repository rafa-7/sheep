// Erro sintático (gramática): as palavras existem, mas estão na ordem errada, ex: x = + 1 x
public class ErroSintatico extends ErroDeAnalise {

    public ErroSintatico(String mensagem, int linha, int coluna) {
        super("Erro sintático", mensagem, linha, coluna);
    }

}
