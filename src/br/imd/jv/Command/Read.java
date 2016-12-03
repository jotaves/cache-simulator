package br.imd.jv.Command;

/**
 *
 * @author João Victor
 */
public class Read extends Command {

    private int adress;

    public Read(String commandType, int adress) {
        super(commandType);
        this.adress = adress;
    }

    public int getAdress() {
        return adress;
    }
}
