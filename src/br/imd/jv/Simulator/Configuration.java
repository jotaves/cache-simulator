package br.imd.jv.Simulator;

/**
 *
 * @author João Victor
 */
public class Configuration {

    private int wordsNumber; // words number in block
    private int linesNumber; // blocks number in cache
    private int blocksNumber; // blocks number in memory
    private int mappingType;
    private int setsNumber;
    private int substitutionType;
    private int writingType;

    public Configuration(int wordsNumber, int linesNumber, int blocksNumber, int mappingType, int setsNumber, int substitutionType, int writingType) {
        this.wordsNumber = wordsNumber;
        this.linesNumber = linesNumber;
        this.blocksNumber = blocksNumber;

        if (mappingType > 0 && mappingType < 4) {
            this.mappingType = mappingType;
        } else {
            System.out.println("O valor de Mapeamento (linha 4 em data/config.txt) deve ser entre 1 e 4. O programa será fechado.");
            System.exit(1);
        }

        this.setsNumber = setsNumber;

        if (substitutionType > 0 && substitutionType < 5) {
            this.substitutionType = substitutionType;
        } else {
            System.out.println("O valor de Política de Substituição (linha 6 em data/config.txt) deve ser entre 1 e 4. O programa será fechado.");
            System.exit(1);
        }

        if (writingType > 0 && writingType < 3) {
            this.writingType = writingType;
        } else {
            System.out.println("O valor de Política de Escrita (linha 7 em data/config.txt) deve ser entre 1 e 2. O programa será fechado.");
            System.exit(1);
        }
    }

    public int getWordsNumber() {
        return wordsNumber;
    }

    public int getLinesNumber() {
        return linesNumber;
    }

    public int getBlocksNumber() {
        return blocksNumber;
    }

    public int getMappingType() {
        return mappingType;
    }

    public int getSetsNumber() {
        return setsNumber;
    }

    public int getSubstitutionType() {
        return substitutionType;
    }

    public int getWritingType() {
        return writingType;
    }
}
