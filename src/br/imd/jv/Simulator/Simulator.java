/* TODO:
* (x) Mapeamento direto
* (x) Mapeamento parcialmente associativo
* (x) Mapeamento totalmente associativo
* (x) Métodos de escrita (write-back e write-through)
* (x) Substituiçção aleatória
* (\) Substituição FIFO
* ( ) Substituição LFU
* ( ) Substituição LRU
 */
package br.imd.jv.Simulator;

import br.imd.jv.Command.Read;
import br.imd.jv.Command.Write;
import br.imd.jv.Memory.Block;
import br.imd.jv.Memory.Cache;
import br.imd.jv.Memory.Line;
import br.imd.jv.Memory.MainMemory;
import br.imd.jv.Memory.Word;
import static java.lang.Double.POSITIVE_INFINITY;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author João Victor
 */
public class Simulator {

    private Configuration config;
    private MainMemory main;
    private Cache cache;
    private Queue<Integer> fifo;
    private Queue<Integer> fifo2;

    public Simulator(Configuration config) {
        this.config = config;
        cache = new Cache(config);
        main = new MainMemory(config);
    }

    public void write(Write cmd) {
        int cacheAdr = cache.findOnCache(cmd.getAdress());
        // Se der hit na cache
        if (cacheAdr != -1) {

            if (config.getSubstitutionType() == 3) {
                cache.getLines()[cacheAdr].incrementCount();
            }

            if (config.getSubstitutionType() == 4) {
                if (config.getMappingType() == 2) {
                    updateIndexes(0, cache.getLines().length);
                    cache.getLines()[cacheAdr].setCount(config.getLinesNumber());
                }

                if (config.getMappingType() == 3) {
                    int blockPos = cmd.getAdress() / config.getWordsNumber();
                    int linePos = blockPos % cache.getConfig().getSetsNumber();
                    int lineForSet = config.getLinesNumber() / config.getSetsNumber();

                    updateIndexes((linePos * lineForSet), (linePos * lineForSet) + lineForSet);
                    cache.getLines()[cacheAdr].setCount(config.getLinesNumber());
                }
            }

            cache.incrementHits();

            if (config.getWritingType() == 2) {
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
                main.write(cmd.getAdress(), cmd.getValue());
            }

            if (config.getWritingType() == 1) {
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
            }
        } else { // Se não der hit na cache
            cache.incrementMisses();
            //System.out.println(cacheAdr);

            if (config.getWritingType() == 2) {
                cacheAdr = moveBlockToCache(cmd.getAdress());
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
                main.write(cmd.getAdress(), cmd.getValue());
            }

            if (config.getWritingType() == 1) {
                cacheAdr = moveBlockToCache(cmd.getAdress());
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
            }
        }
    }

    public String read(Read cmd) {
        int cacheAdr = cache.findOnCache(cmd.getAdress());

        if (cacheAdr != -1) {

            if (config.getSubstitutionType() == 3) {
                cache.getLines()[cacheAdr].incrementCount();
            }

            if (config.getSubstitutionType() == 4) {
                if (config.getMappingType() == 2) {
                    updateIndexes(0, cache.getLines().length);
                    cache.getLines()[cacheAdr].setCount(config.getLinesNumber());
                }

                if (config.getMappingType() == 3) {
                    int blockPos = cmd.getAdress() / config.getWordsNumber();
                    int linePos = blockPos % cache.getConfig().getSetsNumber();
                    int lineForSet = config.getLinesNumber() / config.getSetsNumber();

                    updateIndexes((linePos * lineForSet), (linePos * lineForSet) + lineForSet);
                    cache.getLines()[cacheAdr].setCount(config.getLinesNumber());
                }
            }

            cache.incrementHits();

            return cache.read(cacheAdr, cmd.getAdress());
        } else { // Se não der hit na cache
            cache.incrementMisses();

            cacheAdr = moveBlockToCache(cmd.getAdress());
            return cache.read(cacheAdr, cmd.getAdress());
        }
    }

    public void show() {
        main.show();
        cache.show();
        System.out.print("\n\nHits: " + cache.getHits() + "\nMisses: " + cache.getMisses());
        System.out.print("\n\n");
    }

    private int moveBlockToCache(int wordAdr) {
        int blockPos = wordAdr / cache.getConfig().getWordsNumber();
        int linePos = -1;

        // Caso não haja bloco na memória, criar bloco.
        if (main.getBlocks()[blockPos] == null) {
            main.getBlocks()[blockPos] = new Block(config);
        }

        // Achar posição na cache em caso de ser mapeamento direto.
        if (cache.getConfig().getMappingType() == 1) {
            linePos = blockPos % cache.getConfig().getLinesNumber();
        }

        // Achar posição na cache em caso de ser mapeamento associativo
        if (cache.getConfig().getMappingType() == 2) {
            if (linePos == -1) {
                linePos = cache.nextPlace(0, cache.getLines().length);

                if (linePos == -1) {
                    linePos = substituicao(wordAdr);// SUBSTITUIÇÕES // TODO
                }
            }
        }

        // Achar posição na cache em caso de ser mapeamento parcialmente associativo
        if (cache.getConfig().getMappingType() == 3) {
            linePos = blockPos % cache.getConfig().getSetsNumber();
            int lineForSet = config.getLinesNumber() / config.getSetsNumber();
            //System.out.println((linePos));
            //System.out.println(linePos * lineForSet);

            linePos = cache.nextPlace((linePos * lineForSet), (linePos * lineForSet) + lineForSet);

            if (linePos == -1) {
                linePos = substituicao(wordAdr);// SUBSTITUIÇÕES // TODO
            }
        }

        if (cache.getLines() == null) {
            cache.setLines(new Line[config.getLinesNumber()]);
        }

        // Caso não haja linha ainda, criar linha.
        if (cache.getLines()[linePos] == null) {
            cache.getLines()[linePos] = new Line(config);
        }

        // Caso não haja bloco dentro da linha, criar bloco.
        if (cache.getLines()[linePos].getBlock() == null) {
            cache.getLines()[linePos].setBlock(new Block(config));
        }

        //Caso seja write-through, primeiro é preciso mover o que estava na cache pra memória
        if (cache.getConfig().getWritingType() == 1) {

            int oldBlockPos = cache.getLines()[linePos].getTag();

            // Caso não tenha sido criada um Block na Block[] da memória, criar.
            if (main.getBlocks()[oldBlockPos] == null) {
                main.getBlocks()[oldBlockPos] = new Block(config);
            }

            // Caso não tenha sido criado uma Word[] no Block, criar.
            if (main.getBlocks()[oldBlockPos].getWords() == null) {
                main.getBlocks()[oldBlockPos].setWords(new Word[config.getWordsNumber()]);
            }

            // Copiar bloco da cache pra memória.
            for (int i = 0; i < config.getWordsNumber(); i++) {
                // Caso não tenha sido criada a palavra na cache, criar.
                if (cache.getLines()[linePos].getBlock().getWords()[i] == null) {
                    cache.getLines()[linePos].getBlock().getWords()[i] = new Word();
                }

                // Caso não tenha sido criado uma Word na Word[], criar.
                if (main.getBlocks()[oldBlockPos].getWords()[i] == null) {
                    main.getBlocks()[oldBlockPos].getWords()[i] = new Word();
                }

                // Copiando words.
                main.getBlocks()[oldBlockPos].getWords()[i].setWord(cache.getLines()[linePos].getBlock().getWords()[i].getWord());
            }
        }

        if (config.getSubstitutionType() == 4) {
            if (config.getMappingType() == 2) {
                updateIndexes(0, config.getLinesNumber());
            }

            if (config.getMappingType() == 3) {
                int linePoss = blockPos % config.getSetsNumber();
                int lineForSet = config.getLinesNumber() / config.getSetsNumber();
                updateIndexes((linePoss * lineForSet), (linePoss * lineForSet) + lineForSet);
            }
        }

        // Esse passo é realizado tanto por write-through quando write-back
        // Que é mover o novo bloco que estava na memória para a cache
        // Alterar tag da linha e zerar contador de hits.
        cache.getLines()[linePos].setHits(0);
        if (config.getSubstitutionType() == 3) {
            cache.getLines()[linePos].setCount(0);
        }
        if (config.getSubstitutionType() == 4) {
            cache.getLines()[linePos].setCount(cache.getLines().length);
        }
        cache.getLines()[linePos].setTag(blockPos);

        // adicionar na fila
        if (config.getSubstitutionType() == 2) {
            if (fifo == null) {
                fifo = new LinkedList<>();
            }
            if (config.getMappingType() == 3) {
                fifo2 = new LinkedList<>();
                fifo.add(blockPos);
            }
            
            if (config.getMappingType() == 2) {
                fifo.add(linePos);
            }
        }

        // Copiar bloco da memória para a cache.
        for (int i = 0; i < config.getWordsNumber(); i++) {
            // Caso não tenha sido criada a palavra na cache, criar.
            if (cache.getLines()[linePos].getBlock().getWords()[i] == null) {
                cache.getLines()[linePos].getBlock().getWords()[i] = new Word();
            }

            // Caso não tenha sido criada a palavra na memória, criar.
            if (main.getBlocks()[blockPos].getWords()[i] == null) {
                main.getBlocks()[blockPos].getWords()[i] = new Word();
            }

            // Copiando words.
            cache.getLines()[linePos].getBlock().getWords()[i].setWord(main.getBlocks()[blockPos].getWords()[i].getWord());
        }

        return linePos;
    }

    private int substituicao(int wordAdr) {
        System.out.println("here");
        if (config.getSubstitutionType() == 1) {
            Random r = new Random();
            if (config.getMappingType() == 2) {
                return r.nextInt(config.getLinesNumber());
            }

            if (config.getMappingType() == 3) {
                int blockPos = wordAdr / config.getWordsNumber();
                int linePos = blockPos % config.getSetsNumber();
                int lineForSet = config.getLinesNumber() / config.getSetsNumber();

                int min = (linePos * lineForSet);
                int max = (linePos * lineForSet) + lineForSet - 1;

                int res = r.nextInt((max - min) + 1) + min;
                //System.out.println(res);
                return res;
            }
        }

        if (config.getSubstitutionType() == 2) {
            if (config.getMappingType() == 2) {
                return fifo.remove();
            }
            if (config.getMappingType() == 3) {
                int blockPos = wordAdr / config.getWordsNumber();
                int linePos = blockPos % config.getSetsNumber();
                int lineForSet = config.getLinesNumber() / config.getSetsNumber();

                int result = -1;
                for (int i = 0; i < fifo.size(); i++) {
                    int out = fifo.remove();
                    if (out%config.getSetsNumber() == linePos) {
                        result = out;
                        break;
                    } else {
                        fifo2.add(out);
                    }
                }

                while (!fifo.isEmpty()) {
                    fifo2.add(fifo.remove());
                }

                while (!fifo2.isEmpty()) {
                    fifo.add(fifo2.remove());
                }
                linePos = result % config.getSetsNumber();
                
                return cache.searchOnSet(result, (linePos * lineForSet), (linePos * lineForSet) + lineForSet);
            }
        }

        if (config.getSubstitutionType() == 3) {
            if (config.getMappingType() == 2) {
                return findSmallestCount(0, cache.getLines().length);
            }

            if (config.getMappingType() == 3) {
                int blockPos = wordAdr / config.getWordsNumber();
                int linePos = blockPos % cache.getConfig().getSetsNumber();
                int lineForSet = config.getLinesNumber() / config.getSetsNumber();

                return findSmallestCount((linePos * lineForSet), (linePos * lineForSet) + lineForSet);
            }
        }

        if (config.getSubstitutionType() == 4) {
            if (config.getMappingType() == 2) {
                updateIndexes(0, config.getLinesNumber());
                return findSmallestCount(0, config.getLinesNumber());
            }

            if (config.getMappingType() == 3) {
                int blockPos = wordAdr / config.getWordsNumber();
                int linePos = blockPos % cache.getConfig().getSetsNumber();
                int lineForSet = config.getLinesNumber() / config.getSetsNumber();

                updateIndexes((linePos * lineForSet), (linePos * lineForSet) + lineForSet);
                return findSmallestCount((linePos * lineForSet), (linePos * lineForSet) + lineForSet);
            }
        }
        return -1;
    }

    private int findSmallestCount(int init, int end) {
        double count = POSITIVE_INFINITY;
        int linePos = -1;

        for (int i = init; i < end; i++) {
            if (cache.getLines()[i] != null && cache.getLines()[i].getCount() < count) {
                count = cache.getLines()[i].getCount();
                linePos = i;
            }
        }
        return linePos;
    }

    private void updateIndexes(int init, int end) {
        for (int i = init; i < end; i++) {
            if (cache.getLines()[i] != null) {
                cache.getLines()[i].decrementCount();
            }
        }
    }
}
