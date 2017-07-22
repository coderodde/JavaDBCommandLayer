package net.coderodde.javadb.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class App {

    public static void main(String[] args) {
//        List<String> commandLineBuffer = new ArrayList<>();
        CommandLayer layer = new CommandLayer();
        Scanner scanner = new Scanner(System.in);
        layer.emptyDatabase("funck_db");
        
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();
            
            if (command.equals("quit")) {
                break;
            }
            
            if (command.startsWith("select")) {
                System.out.println(layer.select(command));
            }
//            commandLineBuffer.add(command + " ");
            
//            StringBuilder sb = new StringBuilder();
//            
//            for (String bufferLine : commandLineBuffer) {
//                sb.append(bufferLine);
//            }
            
//            String currentCommand = sb.toString().trim().toLowerCase();
            
//            if (currentCommand.endsWith(";")) {
//                if (currentCommand.equals("quit;")) {
//                    break;
//                }
//                
//                if (currentCommand.startsWith("select ")) {
//                    try {
//                        System.out.println(layer.select(currentCommand));
//                    } catch (IllegalArgumentException ex) {
//                        System.out.println("> " + ex.getMessage());
//                    }
//                }
//            }
        }
        
        System.out.println("> Bye!");
    }
}
