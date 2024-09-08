package mapeamentos;

import utils.BinaryConverter;
import utils.LogBase2;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class MapConjunto {
    private final long tamanhoCache;
    private final long numeroConjuntos;
    private final long numeroLinhasPorConjunto;
    private final ArrayList<HashSet<String>> tags;
    private final long bitsTag;
    private final long tamConjunto;
    private final long enderecar;
    private int totalHits;
    private int totalMisses;
    private final String substituicao;

    private final Random random;
    private final ArrayList<ArrayList<String>> randomMap;
    private final ArrayList<Queue<String>> fifoQueue;
    private final ArrayList<Deque<String>> lruTags;
    private final ArrayList<Map<String, Integer>> tagUsageMap;

    DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    public MapConjunto(long tamMemoria, long palavra, long tamanhoCache, long tamanhoLinha, long numeroLinhasPorConjunto, String substituicao) {
        long palavra1 = LogBase2.log(palavra);
        this.tamanhoCache = tamanhoCache;
        long numeroLinhas = tamanhoCache / tamanhoLinha;
        this.enderecar = LogBase2.log(tamMemoria / palavra);
        this.substituicao = substituicao;
        this.numeroLinhasPorConjunto = numeroLinhasPorConjunto;
        this.numeroConjuntos = (int) (numeroLinhas / numeroLinhasPorConjunto);
        this.tamConjunto = LogBase2.log(numeroConjuntos);
        this.bitsTag = this.enderecar - palavra1 - this.tamConjunto;
        this.tags = new ArrayList<>();
        for (int i = 0; i < numeroConjuntos; i++) {
            tags.add(new HashSet<>((int) numeroLinhasPorConjunto));
        }
        this.totalHits = 0;
        this.totalMisses = 0;

        this.random = new Random();
        this.randomMap = new ArrayList<>();
        this.fifoQueue = new ArrayList<>();
        this.lruTags = new ArrayList<>();
        this.tagUsageMap = new ArrayList<>();
        for (int i = 0; i < numeroConjuntos; i++) {
            randomMap.add(new ArrayList<>((int) numeroLinhasPorConjunto));
            fifoQueue.add(new ArrayDeque<>((int) numeroLinhasPorConjunto));
            lruTags.add(new LinkedList<>());
            tagUsageMap.add(new LinkedHashMap<>((int) numeroLinhasPorConjunto));
        }
    }

    public void mapeamento(int endereco) {
        String addressBinaryString = BinaryConverter.intToBinaryString(endereco, (int) enderecar);
        assert addressBinaryString != null;

        //System.out.print(addressBinaryString + " - ");

        int tagEndIndex = (int) bitsTag;
        int linhaEndIndex = tagEndIndex + (int) tamConjunto;

        String tag = addressBinaryString.substring(0, tagEndIndex);
        String linha = addressBinaryString.substring(tagEndIndex, linhaEndIndex);

        getTag(tag, linha);
    }

    private void getTag(String tag, String linha) {
        int conjuntoIndex = numeroConjuntos > 1 ? Integer.parseInt(linha, 2) % (int) numeroConjuntos : 0;
        HashSet<String> conjuntoTags = tags.get(conjuntoIndex);

        if (conjuntoTags.contains(tag)) {
            totalHits++;
            if (substituicao.equals("LRU")) {
                addTagLru(conjuntoIndex, tag);
            } else if (substituicao.equals("LFU")) {
                addTagLfu(conjuntoIndex, tag);
            }
        } else {
            totalMisses++;
            if (conjuntoTags.size() >= numeroLinhasPorConjunto) {
                switch (substituicao) {
                    case "Random":
                        randomSubs(conjuntoIndex);
                        break;
                    case "FIFO":
                        fifoSubs(conjuntoIndex);
                        break;
                    case "LRU":
                        lruSubs(conjuntoIndex);
                        break;
                    case "LFU":
                        lfuSubs(conjuntoIndex);
                        break;
                }
            }
            conjuntoTags.add(tag);
            switch (substituicao) {
                case "Random":
                    randomMap.get(conjuntoIndex).add(tag);
                    break;
                case "FIFO":
                    fifoQueue.get(conjuntoIndex).add(tag);
                    break;
                case "LRU":
                    addTagLru(conjuntoIndex, tag);
                    break;
                case "LFU":
                    addTagLfu(conjuntoIndex, tag);
                    break;
            }
        }
    }

    private void randomSubs(int conjuntoIndex) {
        ArrayList<String> randomTags = randomMap.get(conjuntoIndex);
        if (!randomTags.isEmpty()) {
            int randomPosition = random.nextInt(randomTags.size());
            String remover = randomTags.remove(randomPosition);
            tags.get(conjuntoIndex).remove(remover);
        }
    }

    private void fifoSubs(int conjuntoIndex) {
        Queue<String> fifoTags = fifoQueue.get(conjuntoIndex);
        String remover = fifoTags.poll();
        tags.get(conjuntoIndex).remove(remover);
    }

    private void lruSubs(int conjuntoIndex) {
        Deque<String> lruTagsList = lruTags.get(conjuntoIndex);
        String remover = lruTagsList.poll();
        tags.get(conjuntoIndex).remove(remover);
    }

    private void addTagLru(int conjuntoIndex, String tag) {
        Deque<String> lruTagsList = lruTags.get(conjuntoIndex);
        if (lruTagsList.size() >= numeroLinhasPorConjunto) {
            lruTagsList.remove();
        }
        lruTagsList.add(tag);
    }

    private void lfuSubs(int conjuntoIndex) {
        Map<String, Integer> usageMap = tagUsageMap.get(conjuntoIndex);
        if (!usageMap.isEmpty()) {
            Map.Entry<String, Integer> leastUsed = Collections.min(usageMap.entrySet(), Map.Entry.comparingByValue());
            String remover = leastUsed.getKey();
            tags.get(conjuntoIndex).remove(remover);
            usageMap.remove(remover);
        }
    }

    private void addTagLfu(int conjuntoIndex, String tag) {
        Map<String, Integer> usageMap = tagUsageMap.get(conjuntoIndex);
        usageMap.put(tag, usageMap.getOrDefault(tag, 0) + 1);
    }

    public String imprimir() {
        return "\n\nMapeamento Associativo por Conjunto"
                + "\nSubstituição: " + substituicao
                + "\nTamanho da cache: " + tamanhoCache
                + "\nNúmero de conjuntos: " + numeroConjuntos
                + "\nLinhas por conjunto: " + numeroLinhasPorConjunto
                + "\nTotal de acessos: " + (totalHits + totalMisses)
                + "\nHits: " + totalHits
                + "\nMisses: " + totalMisses
                + "\n% Hits: " + df.format((totalHits * 100.0f) / (totalHits + totalMisses)) + "%"
                + "\n% Misses: " + df.format((totalMisses * 100.0f) / (totalHits + totalMisses)) + "%";
    }
}
