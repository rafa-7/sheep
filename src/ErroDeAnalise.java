// Classe mãe dos erros do analisador.
// Fizemos como abstrata porque todo erro é léxico ou sintático
public abstract class ErroDeAnalise extends RuntimeException {

    private final int linha;
    private final int coluna;

    protected ErroDeAnalise(String tipoErro, String mensagem, int linha, int coluna) {
        super(tipoErro + " na linha " + linha + ", coluna " + coluna + ": " + mensagem);
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

}
