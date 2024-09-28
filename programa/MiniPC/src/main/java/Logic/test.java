/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author ksala
 */
public class test {
    public static void main(String[] args) {
        // Sample assembly instructions
        String input = "LOAD AX\nSTORE BX\nADD CX\nSUB DX\nMOV AX, 100\nINC AX\nSWAP BX, CX\nCMP AX, BX\nJNE 5\ninc\nPARAM 5, 10, -3\nPUSH DX\nPOP AX\nINT 20H\n";

        // Create singleton instance and process the assembly instructions
        SyntaxManager.getInstance(input);

        // Verify the instructions
        boolean valid = SyntaxManager.getInstance().verifyInstructions();
        System.out.println("All instructions are valid: " + valid);

        // Print bytecode and original code
        System.out.println("\nBytecode - Code:");
        for (int i = 0; i < SyntaxManager.getInstance().getBinaryInstructions().size(); i++) {
            String bytecode = SyntaxManager.getInstance().getBinaryInstructions().get(i);
            String originalCode = SyntaxManager.getInstance().getInstructions().get(i);
            System.out.println(bytecode + " - " + originalCode);
        }

        // Print values associated with MOV instructions
        System.out.println("\nValues associated with MOV:");
        SyntaxManager.getInstance().getValues().forEach(System.out::println);
    }
}
