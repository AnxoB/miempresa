package com.example;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class Main {
    public static void main(String[] args) {
        Empleado empleado = new Empleado();
        List<Document> empleados = new ArrayList<>();
        
        empleados.add(new Document()
                .append("emp_no", 1)
                .append("nombre", "Juan")
                .append("dep", 10)
                .append("salario", 1000)
                .append("fechaalta", "10/10/1999")
        );
        
        empleados.add(new Document()
                .append("emp_no", 2)
                .append("nombre", "Alicia")
                .append("dep", 10)
                .append("salario", 1400)
                .append("fechaalta", "07/08/2000")
                .append("oficio", "Profesora")
        );
        
        empleados.add(new Document()
                .append("emp_no", 3)
                .append("nombre", "María Jesús")
                .append("dep", 20)
                .append("salario", 1500)
                .append("fechaalta", "05/01/2005")
                .append("oficio", "Analista")
                .append("comision", 100)
        );
        
        empleados.add(new Document()
                .append("emp_no", 4)
                .append("nombre", "Alberto")
                .append("dep", 20)
                .append("salario", 1100)
                .append("fechaalta", "15/11/2001")
        );
        
        empleados.add(new Document()
                .append("emp_no", 5)
                .append("nombre", "Fernando")
                .append("dep", 30)
                .append("salario", 1400)
                .append("fechaalta", "20/11/1999")
                .append("oficio", "Analista")
                .append("comision", 200)
        );
        
        empleado.insertarMultiplesEmpleados(empleados);
        
        
        System.out.println("Buscar empleado por dep");
        Document filtro1 = new Document("dep", 10);
        List<Document> resultado1 = empleado.buscarEmpleados(filtro1);
        System.out.println(resultado1);

        System.out.println("Buscar todos los empleados por dep");
    }
}