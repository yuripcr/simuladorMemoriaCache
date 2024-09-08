import config.Config;
import mapeamentos.MapAssociativo;
import mapeamentos.MapConjunto;
import mapeamentos.MapDireto;
import utils.FileManager;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;


public class Teste {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Config config = new Config();
        ArrayList<String> dadosConfig = FileManager.stringReader("./src/data/config.txt");
        assert dadosConfig != null;
        List<Long> data = config.createConfig(dadosConfig);
        long tamMemoria = data.get(0);
        long palavra = data.get(1);
        long tamCache = data.get(2);
        long tamLinha = data.get(3);
        ArrayList<String> testeLines = FileManager.stringReader("./src/data/teste_1.txt");

        int op;
        System.out.println("Menu");
        System.out.println("1 - Mapeamento Direto");
        System.out.println("2 - Mapeamento Associativo");
        System.out.println("3 - Mapeamento Associativo em Conjuntos");
        op = input.nextInt();
        switch (op) {
            case 1:
                System.out.println(data);
                MapDireto mapDireto = new MapDireto(tamMemoria, palavra, tamCache, tamLinha);
                assert testeLines != null;
                for (String linha : testeLines) {
                    int acesso = Integer.parseInt(linha);
                    mapDireto.mapeamento(acesso);
                }
                mapDireto.imprimir();
                break;
            case 2:
                System.out.println(data);
                String subs = "LRU";
                MapAssociativo mapAssociativo = new MapAssociativo(tamMemoria, palavra, tamCache, tamLinha, subs);
                assert testeLines != null;
                for (String linha : testeLines) {
                    int acesso = Integer.parseInt(linha);
                    mapAssociativo.mapeamento(acesso);
                }
                mapAssociativo.imprimir();
                break;
            case 3:
                System.out.println(data);
                String subst = "Random";
                MapConjunto mapConjunto = new MapConjunto(tamMemoria, palavra, tamCache, tamLinha, 1,subst);
                assert testeLines != null;
                for (String linha : testeLines) {
                    int acesso = Integer.parseInt(linha);
                    mapConjunto.mapeamento(acesso);
                }
                mapConjunto.imprimir();
                break;
            default:
                break;
        }
    }
}
