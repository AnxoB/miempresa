package com.example;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

public class Empleado {
    MongoProvider provider = new MongoProvider();

    public void insertarEmpleado(Document document) {
        try (MongoProvider provider = new MongoProvider()) {
            provider.miempresa().insertOne(document);
            provider.close();
        } catch (Exception e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
    }

    public void insertarMultiplesEmpleados(List<Document> documentos) {
        try (MongoProvider provider = new MongoProvider()) {
            provider.miempresa().insertMany(documentos);
            provider.close();
        } catch (Exception e) {
            System.out.println("Error al insertar multiples empleados: " + e.getMessage());
        }
    }

    public void buscarEmpleado(Document document){
        try (MongoProvider provider = new MongoProvider()) {
            provider.miempresa().find(document);
            provider.close();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    
    public Document buscarEmpleadoUnico(Document filtro) {
        try (MongoProvider provider = new MongoProvider()) {
            Document resultado = provider.miempresa().find(filtro).first();
            if (resultado != null) {
                System.out.println("Empleado encontrado");
            } else {
                System.out.println("Empleado no encontrado");
            }
            return resultado;
        } catch (Exception e) {
            System.out.println("Error al buscar: " + e.getMessage());
            return null;
        }
    }

    
    public List<Document> buscarEmpleados(Document filtro) {
        List<Document> resultados = new ArrayList<>();
        try (MongoProvider provider = new MongoProvider()) {
            resultados = provider.miempresa().find(filtro).into(resultados);
            System.out.println("Se encontraron " + resultados.size() + " empleados");
            return resultados;
        } catch (Exception e) {
            System.out.println("Error al buscar: " + e.getMessage());
            return null;
        }
    }

    public void borrarEmpleado(Document document){
        try (MongoProvider provider = new MongoProvider()) {
            provider.miempresa().deleteOne(document);
            provider.close();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}
