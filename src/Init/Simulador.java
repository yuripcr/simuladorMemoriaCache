package Init;

import config.Config;
import mapeamentos.MapAssociativo;
import mapeamentos.MapConjunto;
import mapeamentos.MapDireto;
import utils.FileManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Simulador {
    private final String mapeamento;
    private final String substituicao;
    private final String configFile;
    private final String testFile;
    private final int linhasConjunto;


    public Simulador(String mapeamento, String substituicao, String configFile, String testFile, int linhasConjunto){
        this.mapeamento = mapeamento;
        this.substituicao = substituicao;
        this.configFile = configFile;
        this.testFile = testFile;
        this.linhasConjunto = linhasConjunto;
    }

    public void run() {
        Config config = new Config();
        ArrayList<String> dadosConfig = FileManager.stringReader(configFile);
        assert dadosConfig != null;
        List<Long> data = config.createConfig(dadosConfig);
        long tamMemoria = data.get(0);
        long palavra = data.get(1);
        long tamCache = data.get(2);
        long tamLinha = data.get(3);
        ArrayList<String> testeLines = FileManager.stringReader(testFile);

        switch (mapeamento) {
            case "Direto":
                System.out.println(data);
                MapDireto mapDireto = new MapDireto(tamMemoria, palavra, tamCache, tamLinha);
                assert testeLines != null;
                for (String linha : testeLines) {
                    int acesso = Integer.parseInt(linha);
                    mapDireto.mapeamento(acesso);
                }
                resultado(mapDireto.imprimir());
                break;
            case "Associativo":
                System.out.println(data);
                MapAssociativo mapAssociativo = new MapAssociativo(tamMemoria, palavra, tamCache, tamLinha, substituicao);
                assert testeLines != null;
                for (String linha : testeLines) {
                    int acesso = Integer.parseInt(linha);
                    mapAssociativo.mapeamento(acesso);
                }
                resultado(mapAssociativo.imprimir());
                break;
            case "Conjunto":
                System.out.println(data);
                MapConjunto mapConjunto = new MapConjunto(tamMemoria, palavra, tamCache, tamLinha, linhasConjunto, substituicao);
                assert testeLines != null;
                for (String linha : testeLines) {
                    int acesso = Integer.parseInt(linha);
                    mapConjunto.mapeamento(acesso);
                }
                resultado(mapConjunto.imprimir());
                break;
            default:
                System.out.println("Opção inválida");
                break;
        }
    }

    public void resultado(String resultado){
        JFrame frame = new JFrame("Resultados da Simulação");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        JTextArea textArea = new JTextArea(10, 30);
        textArea.setText(resultado);
        textArea.setEditable(false);

        frame.getContentPane().add(new JScrollPane(textArea));
        frame.setVisible(true);
    }
}

