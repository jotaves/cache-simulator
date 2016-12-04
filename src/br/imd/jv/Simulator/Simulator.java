/* TODO:
* (x) Mapeamento direto
* (x) Mapeamento parcialmente associativo
* (x) Mapeamento totalmente associativo
* (x) Métodos de escrita (write-back e write-through)
* ( ) Métodos de substituição
 */
package br.imd.jv.Simulator;

import br.imd.jv.Command.Read;
import br.imd.jv.Command.Write;
import br.imd.jv.Memory.Block;
import br.imd.jv.Memory.Cache;
import br.imd.jv.Memory.Line;
import br.imd.jv.Memory.MainMemory;
import br.imd.jv.Memory.Word;

/**
 *
 * @author João Victor
 */
public class Simulator {

    private Configuration config;
    private MainMemory main;
    private Cache cache;

    public Simulator(Configuration config) {
        this.config = config;
        cache = new Cache(config);
        main = new MainMemory(config);
    }

    public void write(Write cmd) {
        int cacheAdr = cache.findOnCache(cmd.getAdress());
        // Se der hit na cache
        if (cacheAdr != -1) {
            cache.incrementHits();
            if (config.getWritingType() == 1) {
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
                main.write(cmd.getAdress(), cmd.getValue());
            }

            if (config.getWritingType() == 2) {
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
            }
        } else { // Se não der hit na cache
            cache.incrementMisses();
            //System.out.println(cacheAdr);

            if (config.getWritingType() == 1) {
                cacheAdr = moveBlockToCache(cmd.getAdress());
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
                main.write(cmd.getAdress(), cmd.getValue());
            }

            if (config.getWritingType() == 2) {
                cacheAdr = moveBlockToCache(cmd.getAdress());
                cache.write(cacheAdr, cmd.getAdress(), cmd.getValue());
            }
        }
    }

    public String read(Read cmd) {
        int cacheAdr = cache.findOnCache(cmd.getAdress());

        if (cacheAdr != -1) {
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
                    // SUBSTITUIÇÕES // TODO
                }
            }
        }

        // Achar posição na cache em caso de ser mapeamento parcialmente associativo
        if (cache.getConfig().getMappingType() == 3) {
            linePos = blockPos % cache.getConfig().getSetsNumber();
            int lineForSet = config.getLinesNumber() / config.getSetsNumber();

            linePos = cache.nextPlace(linePos * config.getSetsNumber(), linePos + lineForSet + 1);

            if (linePos == -1) {
                // SUBSTITUIÇÕES // TODO
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
        if (cache.getConfig().getWritingType() == 2) {

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

        // Esse passo é realizado tanto por write-through quando write-back
        // Que é mover o novo bloco que estava na memória para a cache
        // Alterar tag da linha e zerar contador de hits.
        cache.getLines()[linePos].setHits(0);
        cache.getLines()[linePos].setTag(blockPos);

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
}
