package br.imd.jv.Memory;

/**
 *
 * @author João Victor
 */
public class Word {

    private String word;

    public Word(String word) {
        this.word = word;
    }

    public Word() {
        this.word = null;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
