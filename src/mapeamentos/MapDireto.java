package mapeamentos;

import utils.BinaryConverter;
import utils.LogBase2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//mem = 128 KB = 131072 = 2^17
//palavra = 4 B = 2^2;
//cache = 4 KB = = 4096 = 2^12
//tamLinha = 4 pal = 4*4 = 16 = 2^4;
//end = tam memoria / palavra = 131072 / 2 = 2^17 / 2^2 = 2^15
//nlinhas = tam cache / tamLinha  = 4096/16  = 2^12 / 2^4 = 2^8

public class MapDireto {
    private final long palavra;
    private final long tamanhoCache;
    private final Map<Integer, String> tags;
    private final long bitsTag;
    private final long enderecar;
    private int totalHits;
    private int totalMisses;

    DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    public MapDireto(long tamMemoria,long palavra, long tamanhoCache, long tamanhoLinha) {
        this.palavra = LogBase2.log(palavra);
        this.tamanhoCache = tamanhoCache;
        long numeroLinhas = tamanhoCache / tamanhoLinha;
        this.enderecar = LogBase2.log(tamMemoria / palavra);
        int LogNLinhas = LogBase2.log(numeroLinhas);
        bitsTag = this.enderecar - this.palavra - LogNLinhas;
        this.tags = new HashMap<>((int) numeroLinhas);
        for(int index = 0; index < numeroLinhas; index++) tags.put(index, "");
        this.totalHits = 0;
        this.totalMisses = 0;
    }

    public void mapeamento(int endereco) {
        String enderecoBinario = BinaryConverter.intToBinaryString(endereco, (int) enderecar);
        assert enderecoBinario != null;
        String linha = enderecoBinario.substring((int)(bitsTag), (int)(enderecar-palavra));
        String tag = enderecoBinario.substring(0, (int)(bitsTag));
        //String pal = enderecoBinario.substring((int)(enderecar - palavra), (int)enderecar);
        //System.out.print(tag + " " + linha + " " + pal + " - ");
        getTag(tag, linha);
    }

    private void getTag(String tag, String linha) {
        int position = Integer.parseInt(linha, 2);
        if (tags.get(position).equals(tag)) {
            //System.out.println("HIT");
            totalHits++;
        } else {
            //System.out.println("MISS");
            totalMisses++;
            tags.put(position, tag);
        }
    }

    public String imprimir() {
        return "\nMapeamento Direto\nTamanho da cache: " + tamanhoCache
                + "\nLinhas de Cache: " + tags.size()
                + "\nTotal de acessos: " + (totalHits + totalMisses)
                + "\nHits: " + totalHits
                + "\nMisses: " + totalMisses
                + "\n% Hits: " + df.format((totalHits * 100.0f)/(totalHits + totalMisses)) + "%"
                + "\n% Misses: " +  df.format((totalMisses * 100.0f)/(totalHits + totalMisses)) + "%";
    }
}


