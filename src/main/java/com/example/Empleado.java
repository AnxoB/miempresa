package com.example;

import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
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

    public void estadisticasPorDepartamento() {
        try (MongoProvider provider = new MongoProvider()) {
            List<Document> resultados = provider.miempresa().aggregate(
                List.of(
                    new Document("$group", new Document("_id", "$dep")
                        .append("numEmpleados", new Document("$sum", 1))
                        .append("salarioMedio", new Document("$avg", "$salario"))
                        .append("salarioMaximo", new Document("$max", "$salario")))
                )
            ).into(new ArrayList<>());
            
            if (resultados.isEmpty()) {
                System.out.println("No hay empleados registrados.\n");
            } else {
                System.out.println("Estadísticas por departamento:");
                for (Document doc : resultados) {
                    int departamento = doc.getInteger("_id");
                    int numEmpleados = doc.getInteger("numEmpleados");
                    double salarioMedio = doc.getDouble("salarioMedio");
                    int salarioMaximo = doc.getInteger("salarioMaximo");
                    
                    System.out.println("Departamento " + departamento + ":");
                    System.out.println("  - Número de empleados: " + numEmpleados);
                    System.out.println("  - Salario medio: " + String.format("%.2f", salarioMedio) + "€");
                    System.out.println("  - Salario máximo: " + salarioMaximo + "€");
                    System.out.println();
                }
                System.out.println("=====================================\n");
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo estadísticas: " + e.getMessage());
        }
    }


    public List<Document> getEmpleadoSalarioDepto(){
        List<Document> lista = new ArrayList<>();
        List<Bson> pipeline = List.of(
            Aggregates.group("$dep", 
                Accumulators.sum("numEmpleados", 1),
                Accumulators.avg("salarioMedio", "$salario"),
                Accumulators.max("salarioMaximo", "$salario")
            ),
            Aggregates.sort(Sorts.ascending("dep"))
        );

        try (MongoProvider mongo = new MongoProvider()) {
            provider.miempresa().aggregate(pipeline).into(lista);
        } catch (Exception e) {
            System.out.println("Error obteniendo estadísticas: " + e.getMessage());
        }
        return lista;
    } 


    public void empleadoConMaximoSalario() {
        try (MongoProvider provider = new MongoProvider()) {
            Document resultado = provider.miempresa().aggregate(
                List.of(
                    new Document("$sort", new Document("salario", -1)),
                    new Document("$limit", 1),
                    new Document("$project", new Document("nombre", 1).append("salario", 1))
                )
            ).first();
            
            if (resultado != null) {
                String nombre = resultado.getString("nombre");
                int salario = resultado.getInteger("salario");
                System.out.println("Empleado con máximo salario:");
                System.out.println("=====================================");
                System.out.println("Nombre: " + nombre);
                System.out.println("Salario: " + salario + "€");
                System.out.println("=====================================\n");
            } else {
                System.out.println("No hay empleados registrados.\n");
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo empleado con máximo salario: " + e.getMessage());
        }
    }

    
    
}
