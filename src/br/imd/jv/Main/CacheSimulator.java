package br.imd.jv.Main;

import br.imd.jv.Command.Read;
import br.imd.jv.Command.Write;
import br.imd.jv.Simulator.Configuration;
import br.imd.jv.Simulator.Simulator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author João Victor
 */
public class CacheSimulator {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String args[]) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("data/config.txt"));
        Simulator sim;
        //Command cmd;
        //Configuration config;

        // Verificando arquivo de configuração
        int index = 0;

        while (sc.hasNextInt() && index < 8) {
            sc.nextInt();
            index++;
        }

        if (index == 7) {
            sc = new Scanner(new File("data/config.txt"));
            sim = new Simulator(new Configuration(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));

            System.out.print("Insira o comando ou \"exit\" para sair: ");
            String command[];

            sc = new Scanner(System.in);
            command = sc.nextLine().trim().split(" ");

            while (!"exit".equals(command[0])) {
                if ("write".equals(command[0]) && command.length == 3) {
                    //System.out.println(command[0] + command[1] + command[2]);
                    sim.write(new Write(command[0], Integer.parseInt(command[1]), command[2]));
                } else if ("read".equals(command[0]) && command.length == 2) {
                    //System.out.println(command[0] + command[1]);
                    try {
                        System.out.println(sim.read(new Read(command[0], Integer.parseInt(command[1]))));
                    } catch (Exception e) {
                        System.out.println("O endereço solicitado está vazio.");
                    }
                } else if ("show".equals(command[0]) && command.length == 1) {
                    //System.out.println(command[0]);
                    sim.show();
                } else {
                    System.out.println("Comando digitado de forma errada. Tente novamente.");
                }

                System.out.print("Insira o comando: ");
                sc = new Scanner(System.in);
                command = sc.nextLine().trim().split(" ");
            }
        } else {
            System.out.println("Arquivo de configuração (data/config.txt) está com linhas faltando, há " + index + " linhas legíveis (7 são necessárias). O programa será fechado.");
            System.exit(1);
        }
    }
}
