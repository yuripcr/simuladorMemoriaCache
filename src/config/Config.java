package config;
import java.util.ArrayList;
import java.util.List;

public class Config{

    public List<Long> createConfig(List<String> configList) {
        List<Long> dadosConfig = new ArrayList<>();
        for (String line : configList) {
            String[] partesLinha = line.split("=");
            String[] data = partesLinha[1].split(" ");
            //System.out.println(data[1]); valores
            //System.out.println(data[2]); unidade
            StringBuilder string = new StringBuilder(data[2]);
            string.deleteCharAt(string.length() - 1);
            dadosConfig.add(converterParaBytes(Long.parseLong(data[1]), string.toString()));
        }

        long tamMemoria = dadosConfig.get(0);
        long palavra = dadosConfig.get(1);
        long tamCache = dadosConfig.get(2);
        long tamLinha = (dadosConfig.get(3) * palavra);

        dadosConfig.clear();

        dadosConfig.add(tamMemoria);
        dadosConfig.add(palavra);
        dadosConfig.add(tamCache);
        dadosConfig.add(tamLinha);

        return(dadosConfig);
    }

    private final static long B = 1;
    private final static long KB = 1024 * B;
    private final static long MB = KB * KB;
    private final static long GB = KB * MB;
    private final static long TB = KB * GB;

    //A função vai converter os bits e trazer os valores que vão ser usados
    private static long converterParaBytes(long valor, String unidade) {
        long multiplicador = switch (unidade.toUpperCase()) {
            case "B" -> B;
            case "KB" -> KB;
            case "MB" -> MB;
            case "GB" -> GB;
            case "TB" -> TB;
            case "PAL" -> 1;
            default -> throw new IllegalArgumentException("Forma Inválida de medida");
        };

        return  valor * multiplicador;
    }
}
