package br.imd.jv.Memory;

import br.imd.jv.Simulator.Configuration;

/**
 *
 * @author João Victor
 */
public class Cache {

    int hits;
    int misses;

    Configuration config;
    Line[] lines;

    public Cache(Configuration config) {
        this.config = config;

        lines = new Line[config.getLinesNumber()];
    }

    public String read(int cacheAdr, int wordAdr) {
        int blockPos = wordAdr / config.getWordsNumber();
        int wordPos = wordAdr % config.getWordsNumber();
        int linePos = -1;

        if (config.getMappingType() == 1) {
            linePos = blockPos % config.getLinesNumber();
        }

        if (config.getMappingType() == 2) {
            linePos = cacheAdr;
        }

        if (config.getMappingType() == 3) {
            linePos = cacheAdr;
        }

        return lines[linePos].getBlock().getWords()[wordPos].getWord();
    }

    public void write(int cacheAdr, int wordAdr, String value) {
        int blockPos = wordAdr / config.getWordsNumber();
        int wordPos = wordAdr % config.getWordsNumber();
        int linePos = -1;

        if (config.getMappingType() == 1) {
            linePos = blockPos % config.getLinesNumber();
        }

        if (config.getMappingType() == 2) {
            linePos = cacheAdr;
        }

        if (config.getMappingType() == 3) {
            linePos = cacheAdr;
        }

        // Criar linha, se não tiver sido criada.
        if (lines[linePos] == null) {
            lines[linePos] = new Line(config);
        }

        // Criar bloco, se não tiver sido criado.
        if (lines[linePos].getBlock() == null) {
            lines[linePos].setBlock(new Block(config));
        }

        // Criar Word[], se não tiver sido criada.
        if (lines[linePos].getBlock().getWords() == null) {
            lines[linePos].getBlock().setWords(new Word[config.getWordsNumber()]);
        }

        // Criar Word, se não tiver sido criada.
        if (lines[linePos].getBlock().getWords()[wordPos] == null) {
            lines[linePos].getBlock().getWords()[wordPos] = new Word();
        }

        // Escrever Word na cache.
        lines[linePos].getBlock().getWords()[wordPos].setWord(value);
    }

    public void show() {
        System.out.print("\n\n::: Memória cache::: ");
        System.out.print("\nPosição na cache:   ");
        for (int i = 0; i < lines.length; i++) {
            System.out.print(i + " ");
        }

        System.out.print("\nLinhas na cache:    ");

        for (Line l : lines) {
            if (l == null) {
                System.out.print("n ");
            } else {
                System.out.print("" + l.getTag() + " ");
            }
        }
    }

    public Line[] getLines() {
        return lines;
    }

    public int findOnCache(int wordAdr) {
        int blockPos = wordAdr / config.getWordsNumber();
        int wordPos = wordAdr % config.getWordsNumber();
        int linePos = -1;

        if (config.getMappingType() == 1) {
            linePos = blockPos % config.getLinesNumber();

            if (lines[linePos] == null || lines[linePos].getTag() != blockPos) {
                return -1;
            }
        }

        if (config.getMappingType() == 2) {

            if (lines == null) {
                lines = new Line[config.getLinesNumber()];
            }

            int i = 0;
            for (Line l : lines) {
                if (l != null && l.getTag() == blockPos) {
                    return i;
                }
                i++;
            }
            return -1;
        }

        if (config.getMappingType() == 3) {
            linePos = blockPos % config.getSetsNumber();
            int lineForSet = config.getLinesNumber() / config.getSetsNumber();

            for (int i = linePos * config.getSetsNumber(); i < (linePos + lineForSet + 1); i++) {
                if (lines[i] != null && lines[i].getTag() == blockPos) {
                    return i;
                }
            }
            return -1;
        }

        return linePos;
    }

    public void incrementHits() {
        hits++;
    }

    public void incrementMisses() {
        misses++;
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public Configuration getConfig() {
        return config;
    }

//    public int getNextPlace(int initial) {
//        if (nextPlace == lines.length) {
//            return nextPlace;
//        }
//        return nextPlace++;
//    }
    public int nextPlace(int init, int end) {
        for (int i = init; i < end; i++) {
            if (lines[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void setLines(Line[] lines) {
        this.lines = lines;
    }
}
