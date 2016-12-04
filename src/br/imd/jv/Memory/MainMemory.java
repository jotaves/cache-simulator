package br.imd.jv.Memory;

import br.imd.jv.Simulator.Configuration;

/**
 *
 * @author João Victor
 */
public class MainMemory {

    private Configuration config;
    private Block[] blocks;

    public MainMemory(Configuration config) {
        this.config = config;

        blocks = new Block[config.getBlocksNumber()];
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public void setBlock(int adress, Block block) {
        Block b = new Block(config);
        b = block;
        blocks[adress] = b;
    }

    public Block read(int position) {
        return blocks[position];
    }

    public void write(int wordAdr, String value) {
        int blockPos = wordAdr / config.getWordsNumber();
        int wordPos = wordAdr % config.getWordsNumber();

        if (blocks[blockPos] == null) {
            blocks[blockPos] = new Block(config);
        }
        
        if (blocks[blockPos].getWords() == null) {
            blocks[blockPos].setWords(new Word[config.getWordsNumber()]);
        }

        blocks[blockPos].getWords()[wordPos] = new Word(value);
    }

    public void show() {
        System.out.print("\n::: Memória Principal :::\n");
        System.out.print("Conteúdos da memória: ");

        for (Block b : blocks) {
            if (b == null) {
                System.out.print("[] ");
            } else if (b.getWords() == null) {
                System.out.print("[]");
            } else {
                System.out.print("[");
                for (Word w : b.getWords()) {
                    if (w == null) {
                        System.out.print("<>");
                    } else if (w.getWord() == null) {
                        System.out.print("<>");
                    } else {
                        System.out.print("<" + w.getWord() + ">");
                    }
                }
                System.out.print("]");
            }
        }
    }
}
