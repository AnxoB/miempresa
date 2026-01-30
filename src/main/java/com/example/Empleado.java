package com.example;

import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

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
    
    public List<Document> buscarEmpleados(Bson filtro) {
        List<Document> resultados = new ArrayList<>();
        try (MongoProvider provider = new MongoProvider()) {
            resultados = provider.miempresa().find(filtro).into(resultados);
            System.out.println("Se encontraron " + resultados.size() + " empleados");
            System.out.println();
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

    public void incrementarSalario(String oficio, int incremento){
        try (MongoProvider provider = new MongoProvider()) {
            Bson filtro = Filters.eq("oficio", oficio);
            Bson operacion = Updates.inc("salario", incremento);
            UpdateResult resultado = provider.miempresa().updateMany(filtro, operacion);
            System.out.println("Se han actualizado " + resultado.getModifiedCount() + " campos");
        } catch (Exception e) {
            System.out.println("Error al actualizar " + e.getMessage());
        }
    }

    public void decrementarComision(int cantidad) {
        try (MongoProvider provider = new MongoProvider()) {
            Bson filtro = Filters.exists("comision");
            Bson operacion = Updates.inc("comision", -cantidad);

            UpdateResult resultado = provider.miempresa().updateMany(filtro, operacion);
            System.out.println("Se ha reducido la comisión a " + resultado.getModifiedCount() + " empleados.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void obtenerMedia(){
        try (MongoProvider provider = new MongoProvider()) {
            Document resultado = provider.miempresa().aggregate(
                List.of(new Document("$group", new Document("_id", "emp_no")
                    .append("media", new Document("$avg", "$salario"))))
            ).first();
            
            if (resultado != null) {
                System.out.println("Media de salario: " + String.format("%.2f", resultado.getDouble("media")) + "€\n");
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo la media: " + e.getMessage());
        }
    }

    
    
}
