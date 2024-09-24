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
        String input = """
                LOAD AX
                STORE BX
                ADD CX
                SUB DX
                MOV AX, 100
                INC AX
                SWAP BX, CX
                CMP AX, BX
                JNE 5
                inc
                PARAM 5, 10, -3
                PUSH DX
                POP AX
                INT 20H
                """;

        // Create singleton instance and process the assembly instructions
        SyntaxManager syntaxManager = SyntaxManager.getInstance(input);

        // Verify the instructions
        boolean valid = syntaxManager.verifyInstructions();
        System.out.println("All instructions are valid: " + valid);

        // Print bytecode and original code
        System.out.println("\nBytecode - Code:");
        for (int i = 0; i < syntaxManager.getBinaryInstructions().size(); i++) {
            String bytecode = syntaxManager.getBinaryInstructions().get(i);
            String originalCode = syntaxManager.getInstructions().get(i);
            System.out.println(bytecode + " - " + originalCode);
        }

        // Print values associated with MOV instructions
        System.out.println("\nValues associated with MOV:");
        syntaxManager.getValues().forEach(System.out::println);
    }
}
