package mapeamentos;

import utils.BinaryConverter;
import utils.LogBase2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class MapAssociativo{
    private final long tamanhoCache;
    private final long numeroLinhas;
    private final Set<String> tags;
    private final long bitsTag;
    private final long enderecar;
    private int totalHits;
    private int totalMisses;

    private final String substituicao;
    private final Random random;
    private final List<String> randomMap;
    private final Queue<String> fifoQueue;
    private final Deque<String> lruTags;
    private final Map<String, Integer> tagUsageMap;

    DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    public MapAssociativo(long tamMemoria, long palavra, long tamanhoCache, long tamanhoLinha, String substituicao) {
        long palavra1 = LogBase2.log(palavra);
        this.tamanhoCache = tamanhoCache;
        this.numeroLinhas = tamanhoCache / tamanhoLinha;
        this.enderecar = LogBase2.log(tamMemoria / palavra);
        this.bitsTag = this.enderecar - palavra1;
        this.tags = new HashSet<>((int) numeroLinhas);
        this.totalHits = 0;
        this.totalMisses = 0;
        this.substituicao = substituicao;

        this.random = new Random();
        this.randomMap = new ArrayList<>((int) numeroLinhas);
        this.fifoQueue = new ArrayDeque<>((int) numeroLinhas);
        this.lruTags = new LinkedList<>();
        this.tagUsageMap = new LinkedHashMap<>((int)numeroLinhas);
    }

    public void mapeamento(int endereco){
        String enderecoBinario = BinaryConverter.intToBinaryString(endereco, (int) enderecar);
        assert enderecoBinario != null;
        String tag = enderecoBinario.substring(0, (int)(bitsTag));
//        String pal = enderecoBinario.substring((int)(enderecar - palavra), (int)enderecar);
//        System.out.print(tag + " " + pal + " - ");
        getTag(tag);
    }

    private void getTag(String tag){
        if(tags.contains(tag)){
            //System.out.println("HIT");
            totalHits++;
            if(substituicao.equals("LRU")){
                addTagLru(tag);
            } else if(substituicao.equals("LFU")){
                addTagLfu(tag);
            }
        } else {
            if(tags.size() >= numeroLinhas){
                switch (substituicao){
                    case "Random":
                        randomSubs();
                        break;
                    case "FIFO":
                        fifoSubs();
                        break;
                    case "LRU":
                        lruSubs();
                        break;
                    case "LFU":
                        lfuSubs();
                        break;
                }
            }
            //System.out.println("MISS);
            totalMisses++;
            tags.add(tag);
            switch (substituicao){
                case "Random":
                    randomMap.add(tag);
                    break;
                case "FIFO":
                    fifoQueue.add(tag);
                    break;
                case "LRU":
                    addTagLru(tag);
                    break;
                case "LFU":
                    addTagLfu(tag);
                    break;
            }
        }
    }

    public String imprimir() {
        return "\n\nMapeamento Assosciativo"
                + "\nSubstituição: " + substituicao
                + "\nTamanho da cache: " + tamanhoCache
                + "\nLinhas de Cache: " + tags.size()
                + "\nTotal de acessos: " + (totalHits + totalMisses)
                + "\nHits: " + totalHits
                + "\nMisses: " + totalMisses
                + "\n% Hits: " + df.format((totalHits * 100.0f)/(totalHits + totalMisses)) + "%"
                + "\n% Misses: " +  df.format((totalMisses * 100.0f)/(totalHits + totalMisses)) + "%";
    }

    public void randomSubs() {
        int randomPosition = random.nextInt(0,(int)numeroLinhas);
        String remover = randomMap.remove(randomPosition);
        tags.remove(remover);
    }

    public void fifoSubs(){
        String remover = fifoQueue.poll();
        tags.remove(remover);
    }

    public void lruSubs(){
        String remover = lruTags.remove();
        tags.remove(remover);
    }

    public void addTagLru(String tag){
        if(lruTags.size() < numeroLinhas){
            lruTags.add(tag);
        } else {
            lruTags.remove();
            lruTags.add(tag);
        }
    }

    public void lfuSubs(){
        Map.Entry<String, Integer> leastUsed = tagUsageMap.entrySet().iterator().next();
        for(Map.Entry<String,Integer> tagsEntry : tagUsageMap.entrySet()){
            if(leastUsed == null || tagsEntry.getValue() <= leastUsed.getValue()){
                leastUsed = tagsEntry;
                break;
            }
        }
        tagUsageMap.remove(leastUsed.getKey());
        String remover = leastUsed.getKey();
        tags.remove(remover);
    }

    public void addTagLfu(String tag){
        if(tagUsageMap.containsKey(tag)){
            int usageCount = tagUsageMap.get(tag);
            usageCount++;
            tagUsageMap.put(tag, usageCount);
        } else {
            tagUsageMap.put(tag, 1);
        }
    }
}

