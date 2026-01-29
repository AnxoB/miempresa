package com.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Proveedor de acceso a MongoDB
 *
 * <p>
 * Esta clase centraliza la configuracion y la creacion del cliente de MongoDB.
 * Se usa para evitar que cada parte del programa tenga que saber como se conecta a la base de datos.
 * </p>
 *
 * <h2>Variables de entorno soportadas</h2>
 * <ul>
 *   <li><b>MONGO_URI</b>: cadena de conexion completa. Ejemplo:
 *   {@code mongodb://admin:admin123@localhost:27017/?authSource=admin}</li>
 *   <li><b>MONGO_DB</b>: nombre de la base de datos (por defecto: {@code instituto}).</li>
 *   <li><b>MONGO_COLLECTION</b>: nombre de la coleccion (por defecto: {@code alumnos}).</li>
 * </ul>
 *
 * <h2>Nota:</h2>
 * <p>
 * MongoDB crea la base de datos y la coleccion automaticamente al insertar el primer documento.
 * Por eso no haremos "CREATE DATABASE" ni "CREATE TABLE" como en SQL.
 * </p>
 */
public final class MongoProvider implements AutoCloseable {

    /** Cliente de MongoDB (pool de conexiones interno). */
    private final MongoClient client;

    /** Base de datos seleccionada. */
    private final MongoDatabase database;

    /** Coleccion de trabajo (Document representa BSON/JSON). */
    private final MongoCollection<Document> collection;

    /**
     * Crea el proveedor y prepara el acceso a la coleccion.
     * <p>
     * Lee la configuracion desde variables de entorno. Si alguna no esta definida, usa valores por defecto.
     * </p>
     */
    public MongoProvider() {
        // 1) Leer configuracion del entorno o usar valores por defecto.
        String uri = envOrDefault(
                "MONGO_URI",
                "mongodb://admin:admin123@localhost:27017/?authSource=admin"
        );
        String dbName = envOrDefault("MONGO_DB", "miempresa");
        String colName = envOrDefault("MONGO_COLLECTION", "empleados");

        // 2) Crear settings a partir de la URI.
        //    Aqui podrias configurar timeouts, SSL, etc. Para aula lo dejamos simple.
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();

        // 3) Crear el cliente y seleccionar base de datos y coleccion.
        this.client = MongoClients.create(settings);
        this.database = client.getDatabase(dbName);
        this.collection = database.getCollection(colName);
    }

    /**
     * Devuelve la coleccion donde guardamos mi empresa.
     *
     * @return coleccion MongoDB que almacena documentos de mi empresa
     */
    public MongoCollection<Document> miempresa() {
        return collection;
    }

    /**
     * Cierra el cliente de MongoDB.
     * <p>
     * Al implementar {@link AutoCloseable}, se puede usar con try-with-resources.
     * </p>
     */
    @Override
    public void close() {
        client.close();
    }

    /**
     * Lee una variable de entorno o devuelve un valor por defecto.
     *
     * @param key nombre de la variable de entorno
     * @param def valor por defecto si no existe o esta en blanco
     * @return valor final para usar en la configuracion
     */
    private static String envOrDefault(String key, String def) {
        String variable = System.getenv(key);
        if (variable == null || variable.isBlank()) {
            variable=def;//Devolvemos el valor por defecto pasado por parámetro si no recupera ningún valor
        }
        return variable;
    }
}
