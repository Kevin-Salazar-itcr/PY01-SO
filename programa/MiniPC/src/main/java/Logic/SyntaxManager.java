package Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Singleton class to manage and verify assembly syntax.
 * Converts assembly instructions to binary and stores related values.
 */
public class SyntaxManager {
    private static SyntaxManager instance;  // Singleton instance
    private final ArrayList<String> instructions;
    private final ArrayList<String> binaryInstructions;
    private final ArrayList<Integer> values;

    /**
     * Private constructor to prevent instantiation.
     *
     * @param input Assembly code input as a string.
     */
    private SyntaxManager(String input) {
        this.instructions = new ArrayList<>(Arrays.asList(input.split("\n")));
        this.binaryInstructions = new ArrayList<>();
        this.values = new ArrayList<>();
        convertInstructionsToBinary();
    }

    /**
     * Static method to get the single instance of the class.
     *
     * @param input Assembly code input as a string.
     * @return The single instance of SyntaxManager.
     */
    public static SyntaxManager getInstance(String input) {
        instance = new SyntaxManager(input);
        return instance;
    }

    public static SyntaxManager getInstance() {
        return instance;
    }

    /**
     * Verifies if the assembly instructions are correctly formatted.
     *
     * @return true if all instructions are valid, false otherwise.
     */
    public boolean verifyInstructions() {
        String regex1 = "^(load|store|add|sub|LOAD|STORE|ADD|SUB)\\s+(AX|BX|CX|DX)$";
        String regex2 = "^(mov|MOV)\\s+(AX|BX|CX|DX)\\s*,\\s*(-?\\d+)$";
        
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);
        
        boolean allMatch = true;
        
        for (String line : instructions) {
            boolean matches = pattern1.matcher(line).matches() || pattern2.matcher(line).matches();
            System.out.println("Instruction: " + line + " | Matches: " + matches);
            if (!matches) {
                allMatch = false;
            }
        }
        
        return allMatch;
    }

    /**
     * Converts the verified assembly instructions into binary format and stores values.
     */
    private void convertInstructionsToBinary() {
        this.binaryInstructions.clear();
        this.values.clear();
        instructions.forEach(line -> {
            String binaryInstruction = convertToBinary(line);
            if (binaryInstruction != null) {
                binaryInstructions.add(binaryInstruction);
            }
        });
    }

    /**
     * Converts a single assembly instruction into its binary representation.
     *
     * @param line The assembly instruction line.
     * @return The binary representation of the instruction.
     */
    private String convertToBinary(String line) {
        String opcode;
        String register;
        String value;

        if (line.matches("^(load|store|add|sub|LOAD|STORE|ADD|SUB)\\s+(AX|BX|CX|DX)$")) {
            opcode = line.toLowerCase().startsWith("load") ? "001" :
                     line.toLowerCase().startsWith("store") ? "010" :
                     line.toLowerCase().startsWith("add") ? "101" : "100";
            register = getRegisterBinary(line.split("\\s+")[1]);
            return opcode + " " + register;
        } else if (line.matches("^(mov|MOV)\\s+(AX|BX|CX|DX)\\s*,\\s*(-?\\d+)$")) {
            opcode = "011";
            String[] parts = line.split("\\s*,\\s*");
            register = getRegisterBinary(parts[0].split("\\s+")[1]);
            values.add(Integer.valueOf(parts[1]));
            value = parts[1];
            return opcode + " " + register + " " + value;
        }
        else{
            return "NULL";
        }
    }

    /**
     * Converts a register name to its binary representation.
     *
     * @param register The register name (AX, BX, CX, DX).
     * @return The binary representation of the register.
     */
    private String getRegisterBinary(String register) {
        return switch (register.toUpperCase()) {
            case "AX" -> "00";
            case "BX" -> "01";
            case "CX" -> "10";
            case "DX" -> "11";
            default -> "";
        };
    }

    /**
     * Gets the list of binary instructions.
     *
     * @return ArrayList of binary instructions.
     */
    public ArrayList<String> getBinaryInstructions() {
        return binaryInstructions;
    }

    /**
     * Gets the list of values associated with MOV instructions.
     *
     * @return ArrayList of integer values.
     */
    public ArrayList<Integer> getValues() {
        return values;
    }

    /**
     * Replaces values in the binary instructions with their corresponding keys in the map.
     *
     * @param startNumber The starting number for the temporary map.
     */
    public void replaceValuesWithMap(int startNumber) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            map.put(startNumber + i, values.get(i));
        }

        for (int i = 0; i < binaryInstructions.size(); i++) {
            String curr = binaryInstructions.get(i);
            if(curr.length()>7){
                String a = curr.substring(0, 7);
                int b = Integer.parseInt(curr.substring(7, curr.length()));
                String c = a+String.valueOf(getKeyByValue(map,b));
                
                binaryInstructions.set(i, c);
            }
        }
        
    }

    /**
     * Helper method to get the key associated with a value in the map.
     *
     * @param map The map containing key-value pairs.
     * @param value The value to find the key for.
     * @return The key associated with the value.
     */
    private Integer getKeyByValue(HashMap<Integer, Integer> map, Integer value) {
        return map.entrySet().stream().filter(entry -> value.equals(entry.getValue())).map(HashMap.Entry::getKey).findFirst().orElse(null);
    }
}


//class who needs to be configured with the syntax manager
/*
public class AssemblyValidator {
    public static void main(String[] args) {
        // Assembly instruction regex (condensed into a more concise version)
        String regex = "^(LOAD|STORE|ADD|SUB|PUSH|POP) (AX|BX|CX|DX)|" +
                       "MOV (AX|BX|CX|DX), (AX|BX|CX|DX|(-?\\d+))|" +
                       "INC(\\s(AX|BX|CX|DX|AC))?|" +
                       "DEC(\\s(AX|BX|CX|DX|AC))?|" +
                       "SWAP (AX|BX|CX|DX), (AX|BX|CX|DX)|" +
                       "INT ((09|10|20)H)|" +
                       "CMP (AX|BX|CX|DX), (AX|BX|CX|DX)|" +
                       "(JMP|JG|JL|JGE|JLE|JE|JNE) [a-zA-Z_][a-zA-Z0-9_]*|" +
                       "PARAM (-?\\d+)(,\\s*(-?\\d+)){0,4}$";

        // Example input (file content simulation)
        String fileContent = String.join("\n",
            "LOAD AX", "STORE DX", "ADD BX", "SUB CX", "MOV AX, BX", "INC AC", "DEC AX", "INC", "DEC",
            "SWAP BX, CX", "INT 20H", "CMP AX, DX", "JMP label1", "PARAM 2,3, -5,   6", "PARAM 1", "PUSH AX", "POP DX");

        // Pattern compilation
        Pattern pattern = Pattern.compile(regex);

        // Validate each line in the file content
        for (String instruction : fileContent.split("\\n")) {
            System.out.println(instruction + (pattern.matcher(instruction.trim()).matches() ? " is valid." : " is NOT valid."));
        }
    }
}


private class AssemblyCodeConverter {

    // Function to convert register name to binary
    private String getRegisterBinary(String register) {
        return switch (register.toUpperCase()) {
            case "AX" -> "000";
            case "BX" -> "001";
            case "CX" -> "010";
            case "DX" -> "011";
            case "AC" -> "100";
            case "ZF" -> "101";
            case "SF" -> "110";
            default -> "111"; // For invalid registers
        };
    }

    // Function to convert instruction name to binary
    private String getInstructionBinary(String instruction) {
        return switch (instruction.toUpperCase()) {
            case "LOAD" -> "0000";
            case "STORE" -> "0001";
            case "ADD" -> "0010";
            case "SUB" -> "0011";
            case "MOV" -> "0100";
            case "INC" -> "0101";
            case "DEC" -> "0110";
            case "SWAP" -> "0111";
            case "INT" -> "1000";
            case "CMP" -> "1001";
            case "JMP", "JG", "JL", "JGE", "JLE", "JE", "JNE" -> "1010"; // All jump types
            case "PARAM" -> "1011";
            case "PUSH" -> "1100";
            case "POP" -> "1101";
            default -> "1111"; // For invalid instructions
        };
    }

    public static void main(String[] args) {
        AssemblyCodeConverter converter = new AssemblyCodeConverter();

        // Example usage of the register conversion
        System.out.println("AX to binary: " + converter.getRegisterBinary("AX"));
        System.out.println("ZF to binary: " + converter.getRegisterBinary("ZF"));
        System.out.println("Invalid register: " + converter.getRegisterBinary("XYZ"));

        // Example usage of the instruction conversion
        System.out.println("LOAD to binary: " + converter.getInstructionBinary("LOAD"));
        System.out.println("ADD to binary: " + converter.getInstructionBinary("JMP"));
        System.out.println("Invalid instruction: " + converter.getInstructionBinary("XYZ"));
    }
}
*/