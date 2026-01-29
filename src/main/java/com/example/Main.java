package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

public class Main {
    private static Empleado empleado = new Empleado();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = obtenerOpcion();

            switch (opcion) {
                case 1:
                    menuInsertarEmpleados();
                    break;
                case 2:
                    menuBuscarPorDepartamento();
                    break;
                case 3:
                    menuBuscarMultiplesDepartamentos();
                    break;
                case 4:
                    menuBuscarPorSalarioYOficio();
                    break;
                case 5:
                    System.out.println("\n¡Hasta luego!");
                    continuar = false;
                    break;
                default:
                    System.out.println("\n❌ Opción inválida. Intente nuevamente.\n");
            }
        }
        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("1. Insertar empleados");
        System.out.println("2. Buscar empleados por departamento 10");
        System.out.println("3. Buscar empleados por múltiples departamentos");
        System.out.println("4. Buscar empleados por salario y oficio");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static int obtenerOpcion() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Limpiar buffer
            return -1;
        }
    }

    private static void menuInsertarEmpleados() {
        System.out.println("Insertar empleados");

        List<Document> empleados = new ArrayList<>();

        empleados.add(new Document()
                .append("emp_no", 1)
                .append("nombre", "Juan")
                .append("dep", 10)
                .append("salario", 1000)
                .append("fechaalta", "10/10/1999"));

        empleados.add(new Document()
                .append("emp_no", 2)
                .append("nombre", "Alicia")
                .append("dep", 10)
                .append("salario", 1400)
                .append("fechaalta", "07/08/2000")
                .append("oficio", "Profesora"));

        empleados.add(new Document()
                .append("emp_no", 3)
                .append("nombre", "María Jesús")
                .append("dep", 20)
                .append("salario", 1500)
                .append("fechaalta", "05/01/2005")
                .append("oficio", "Analista")
                .append("comision", 100));

        empleados.add(new Document()
                .append("emp_no", 4)
                .append("nombre", "Alberto")
                .append("dep", 20)
                .append("salario", 1100)
                .append("fechaalta", "15/11/2001"));

        empleados.add(new Document()
                .append("emp_no", 5)
                .append("nombre", "Fernando")
                .append("dep", 30)
                .append("salario", 1400)
                .append("fechaalta", "20/11/1999")
                .append("oficio", "Analista")
                .append("comision", 200));

        empleado.insertarMultiplesEmpleados(empleados);
        System.out.println("Se han insertado 5 empleados correctamente");
    }

    private static void menuBuscarPorDepartamento() {
        System.out.println("Buscar empleado por departamento=10");

        Document filtro = new Document("dep", 10);
        List<Document> resultado = empleado.buscarEmpleados(filtro);

        System.out.println(resultado);
        System.out.println();
    }

    private static void menuBuscarMultiplesDepartamentos() {
        System.out.println("Buscando por departamento 10 y 20");

        Bson filtro = Filters.in("dep", 10, 20);
        List<Document> resultado = empleado.buscarEmpleados(filtro);

        System.out.println(resultado);
        System.out.println();
    }

    private static void menuBuscarPorSalarioYOficio() {
        System.out.println("Buscar por salario y oficio");

        Bson filtro = Filters.and(
                Filters.gt("salario", 1300),
                Filters.eq("oficio", "Profesora"));
        List<Document> resultado = empleado.buscarEmpleados(filtro);

        System.out.println(resultado);
        System.out.println();
    }

    private static void incrementarValor() {
        System.out.println("Subiendo 100€ a todos los Analistas");
        empleado.incrementarSalario("Analista", 100);

        System.out.println("Verificar resultado");
        Bson filtroAnalistas = Filters.eq("oficio", "Analista");
        List<Document> analistas = empleado.buscarEmpleados(filtroAnalistas);
        System.out.println(analistas);
    }

    private static void decrementarValor() {
        System.out.println("Restando 20€ de comisión");
        empleado.decrementarComision(20);
    }
}