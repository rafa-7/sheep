// Erro léxico (ortografia): caractere ou palavra que não existe na Sheep, ex: '@'
public class ErroLexico extends ErroDeAnalise {

    public ErroLexico(String mensagem, int linha, int coluna) {
        super("Erro léxico", mensagem, linha, coluna);
    }

}
