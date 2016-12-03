package br.imd.jv.Command;

/**
 *
 * @author João Victor
 */
public class Command {

    private String commandType;

    public Command(String commandType) {
        this.commandType = commandType;
    }

    public String getCommandType() {
        return commandType;
    }
}
