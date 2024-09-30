/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ksala
 */
public class test {
    public static void main(String[] args) {
        Properties properties = new Properties();
        
        try (FileInputStream input = new FileInputStream(System.getProperty("user.dir")+"\\config.properties")) {
            properties.load(input);

            // Obtener valores de las propiedades
            String ram = properties.getProperty("ram");
            String disc = properties.getProperty("disc");
            String virtual = properties.getProperty("virtual");

            // Imprimir valores
            System.out.println("RAM: " + ram);
            System.out.println("Disc: " + disc);
            System.out.println("Virtual Memory: " + virtual);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
