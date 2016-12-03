package br.imd.jv.Memory;

import br.imd.jv.Simulator.Configuration;

/**
 *
 * @author João Victor
 */
public class Block {

    private Word[] words;
    Configuration config;

    public Block(Configuration config) {
        this.config = config;

        words = new Word[config.getWordsNumber()];
    }

    public Word[] getWords() {
        return words;
    }

    void setWords(Word[] words) {
        this.words = words;
    }
}
