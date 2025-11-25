package com.proyectosso.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal de la aplicacion Spring Boot.
 *
 * Esta clase inicializa el contexto de Spring y levanta la aplicacion.
 */
@SpringBootApplication
public class DemoApplication {

    /**
     * Metodo principal que inicia la aplicacion.
     *
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
