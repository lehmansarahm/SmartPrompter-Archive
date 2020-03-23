package src;

import java.io.*;
import java.util.*;

public class CliRunner {

    public CliRunner() { /*  empty  */ }
    
    public boolean run(String opName, String[] command) {
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(command));
        
        try {
            Process process = processBuilder.start();
            
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            switch (exitVal) {
                case 0:
                    System.out.println("Operation [" + opName + "] was successful!");
                    System.out.println(output);
                    return true;
                default:
                    // something non-exception-throwing went wrong...
                    System.out.println("Operation [" + opName + "] returned with exit code:  " + exitVal);
                    System.out.println(output);
                    return false;
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex);
            return false;
        }
    }

}
