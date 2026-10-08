package org.educa.dao;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Creamos la clase ProductoDAOImpl que implementa la interfaz ProductoDAO
 */

public class ProductoDAOImpl implements ProductoDAO {
    /**
     * Leemos el fichero XML mediante un unmarshaller y obtenemos los valores de los Productos
     * @param FILE_XML
     * @return
     */
    @Override
    public Productos obtenerProductos(String FILE_XML ) {
        try {
            System.out.println("Intentando leer" + FILE_XML);
            JAXBContext context = JAXBContext.newInstance(Productos.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Productos) unmarshaller.unmarshal(new File(FILE_XML ));
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;

        }
    }

    /**
     * Creamos un boolean para la creación del fichero del segundo ejercicio.
     * @param rutaDestino
     * @param contenidoTXT
     * @return
     */
    @Override
    public boolean guardarFicheroTXT(String rutaDestino, String contenidoTXT) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaDestino))) {
            writer.write(contenidoTXT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

