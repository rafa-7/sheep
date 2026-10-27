import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Se não passar nenhum arquivo, usa o codigo.txt
        if (args.length == 0) {
            analisarArquivo("codigo.txt");
        } else {
            for (String arquivo : args) {
                analisarArquivo(arquivo);
            }
        }
    }

    private static void analisarArquivo(String nomeArquivo) {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            System.out.println("Arquivo não encontrado: " + arquivo.getAbsolutePath());
            System.out.println();
            return;
        }

        String codigoFonte;
        try {
            codigoFonte = Files.readString(arquivo.toPath());
        } catch (IOException e) {
            // Captura outras falhas na leitura, como o programa não ter permissão
            System.out.println("Erro ao tentar ler o arquivo: " + arquivo.getAbsolutePath());
            System.out.println("Detalhe do erro: " + e);
            System.out.println();
            return;
        }

        // O Bloco de Notas às vezes salva um caractere invisível (0xFEFF) no começo do arquivo, então tira ele
        if (!codigoFonte.isEmpty() && codigoFonte.charAt(0) == 0xFEFF) {
            codigoFonte = codigoFonte.substring(1);
        }

        // No Windows a quebra de linha é \r\n, aqui deixa tudo como \n
        codigoFonte = codigoFonte.replace("\r\n", "\n");
        codigoFonte = codigoFonte.replace("\r", "\n");
        String[] linhas = codigoFonte.split("\n");

        System.out.println("Arquivo em uso: " + nomeArquivo);
        for (int i = 0; i < linhas.length; i++) {
            System.out.printf("%3d | %s%n", i + 1, linhas[i]);
        }
        System.out.println();

        try {
            // 1. Análise léxica: separa o código em tokens
            Lexer lexer = new Lexer(codigoFonte);
            List<Token> tokens = lexer.scanTokens();
            mostrarTokens(tokens);

            // 2. Análise sintática: confere se os tokens estão na ordem da gramática
            Parser parser = new Parser(tokens);
            parser.parsePrograma();
            System.out.println("Resultado: código sintaticamente correto!");
        } catch (ErroDeAnalise e) {
            System.out.println("Resultado: " + e.getMessage());
            mostrarOndeEstaOErro(linhas, e.getLinha(), e.getColuna());
        }
        System.out.println();
    }

    // Mostra os tokens separados pela linha do código em que aparecem
    private static void mostrarTokens(List<Token> tokens) {
        System.out.print("Tokens:");
        int linhaAtual = 0;
        for (Token token : tokens) {
            if (token.getLinha() != linhaAtual) {
                linhaAtual = token.getLinha();
                System.out.printf("%n  linha %d:", linhaAtual);
            }
            System.out.print(" " + token);
        }
        System.out.println();
        System.out.println();
    }

    // Repete a linha do erro e coloca um ^ embaixo da coluna
    private static void mostrarOndeEstaOErro(String[] linhas, int linha, int coluna) {
        if (linha > linhas.length) {
            return;
        }
        String textoDaLinha = linhas[linha - 1];
        System.out.printf("%3d | %s%n", linha, textoDaLinha);

        StringBuilder espacos = new StringBuilder();
        for (int i = 0; i < coluna - 1; i++) {
            // Se a linha tiver tab, coloca tab também para o ^ não ficar torto
            if (i < textoDaLinha.length() && textoDaLinha.charAt(i) == '\t') {
                espacos.append('\t');
            } else {
                espacos.append(' ');
            }
        }
        System.out.println("    | " + espacos + "^");
    }

}
