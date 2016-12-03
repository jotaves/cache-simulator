package br.imd.jv.Command;

/**
 *
 * @author João Victor
 */
public class Write extends Command {

    private int adress;
    private String value;

    public Write(String commandType, int adress, String value) {
        super(commandType);
        this.adress = adress;
        this.value = value;
    }

    public int getAdress() {
        return adress;
    }

    public String getValue() {
        return value;
    }

    public void write(Command cmd) {

    }
}
