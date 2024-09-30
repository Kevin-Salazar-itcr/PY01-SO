package Logic;

import java.util.ArrayList;
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
        this.instructions = new ArrayList<>();
        for (String line : input.split("\\n")) {
            this.instructions.add(line.substring(0, line.length() - 1));
        }
        this.binaryInstructions = new ArrayList<>();
        this.values = new ArrayList<>();
        convertInstructionsToBinary();
    }

    public static SyntaxManager getInstance(String input) {
        instance = new SyntaxManager(input);
        return instance;
    }

    public static SyntaxManager getInstance() {
        return instance;
    }

    public ArrayList<String> getInstructions(){
        return instructions;
    }
    
    public ArrayList<String> getBinaryInstructions() {
        return binaryInstructions;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    /**
     * Verifies if the assembly instructions are correctly formatted.
     *
     * @return true if all instructions are valid, false otherwise.
     */
    public boolean verifyInstructions() {
        // Expanded regex to support more instructions
        String regex = "^(LOAD|STORE|ADD|SUB|PUSH|POP|MOV)\\s+(AX|BX|CX|DX|AC)|" +
                       "MOV\\s+(AX|BX|CX|DX),\\s*(AX|BX|CX|DX|(-?\\d+))|" +
                       "INC(\\s(AX|BX|CX|DX|AC))?|" +
                       "DEC(\\s(AX|BX|CX|DX|AC))?|" +
                       "SWAP\\s+(AX|BX|CX|DX),\\s+(AX|BX|CX|DX)|" +
                       "INT\\s+((09|10|20)H)|" +
                       "CMP\\s+(AX|BX|CX|DX),\\s+(AX|BX|CX|DX)|" +
                       "(JMP|JG|JL|JGE|JLE|JE|JNE)\\s+(-?\\d+)|" +
                       "PARAM\\s+(-?\\d+)(,\\s(-?\\d+)){0,4}$";

        Pattern pattern = Pattern.compile(regex);
        boolean allMatch = true;

        for (String line : instructions) {
            boolean matches = pattern.matcher(line.toUpperCase()).matches();
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
            String binaryInstruction = convertToBinary(line.toUpperCase());
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
        String[] parts = line.replace(",", "").split("\\s+");
        String instruction = parts[0];
        String opcode = getInstructionBinary(instruction);
        if (opcode.equals("1111")) {
            return "null"; // Invalid instruction
        }

        // Handle MOV with immediate values
        if (instruction.matches("^(MOV|CMP|SWAP)$")) {
            String register = getRegisterBinary(parts[1]);
            
            try{
                values.add(Integer.valueOf(parts[2]));
            }
            catch(Exception e){
                return opcode + " " + register + " " + getRegisterBinary(parts[2]);
            }
            return opcode + " " + register + " " + parts[2]; // MOV with immediate value
        } 
        // Handle jumps
        else if (instruction.startsWith("J")){
            String jumpType = getJumpTypeBinary(instruction);
            return opcode + " " + jumpType + " " + parts[1]; // Jump with type and number
        }
        //Handle instruccions with one register
        else if (parts[0].matches("^(LOAD|STORE|ADD|SUB|PUSH|POP|INC|DEC)$")){
            if(parts.length==1){
                return opcode + " 0111";
            }
            String reg = getRegisterBinary(parts[1]);
            return opcode + " " + reg;
        }
        else if (parts[0].matches("^(PARAM)$")){
            String params = "";
            for(int i=1; i<parts.length;i++){
                params+= parts[i]+" ";
            }
            return opcode + " " + params;   
        }
        else if (parts[0].equals("INT")){
            switch(parts[1]){
                case "09H"->{return opcode + " 0011 0001";} //input
                case "10H"->{return opcode + " 0011 0010";} //print
                case "20H"->{return opcode + " 0011 0011";} //std exit        
                default -> {return null;}
            }
        }
        
        return null; // Unsupported format
    }

    /**
     * Converts a register name to its binary representation.
     *
     * @param register The register name (AX, BX, CX, DX).
     * @return The binary representation of the register.
     */
    private String getRegisterBinary(String register) {
        return switch (register.toUpperCase()) {
            case "AX" -> "0000";
            case "BX" -> "0001";
            case "CX" -> "0010";
            case "DX" -> "0011";
            default -> "1111";     // Invalid register
        };
    }

    /**
     * Converts an instruction name to its binary representation.
     *
     * @param instruction The instruction name.
     * @return The binary representation of the instruction.
     */
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
            default -> "1111"; // Invalid instruction
        };
    }

    /**
     * Converts a jump instruction to its binary representation.
     *
     * @param jumpType The jump instruction name.
     * @return The 4-bit binary representation of the jump type.
     */
    private String getJumpTypeBinary(String jumpType) {
        return switch (jumpType.toUpperCase()) {
            case "JMP" -> "0000";
            case "JG" -> "0001";
            case "JL" -> "0010";
            case "JGE" -> "0011";
            case "JLE" -> "0100";
            case "JE" -> "0101";
            case "JNE" -> "0110";
            default -> "1111"; // Invalid jump type
        };
    }
 }