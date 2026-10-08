package org.educa.dao;
import generated.Productos;

/**
 * Creamos la interfaz ProductoDAO para la arquitectura del proyecto
 */
public interface ProductoDAO {
    /**
     * Lee un fichero xml y lo deserializa en un objeto Productos
     * @param FILE_XML
     * @return
     */
    Productos obtenerProductos(String FILE_XML);

    /**
     *Guarda el contenido del nuevo fichero en la ruta específica
     * @param rutaDestino
     * @param contenidoTXT
     * @return
     */
    boolean guardarFicheroTXT(String rutaDestino, String contenidoTXT);
}
