package org.educa.app;

import jakarta.xml.bind.JAXBException;
import org.educa.entity.ProductoEntity;
import org.educa.service.ProductoService;

import java.util.List;

public class Activity1{
    private static final String FILE_XML = "src/main/resources/xml/inventario_junio2026.xml";

    public static void main(String[] args) {
        //Leer el fichero XML
        ProductoService productoService = new ProductoService();
        try {
            List<ProductoEntity> vehiculos = productoService.readFile(FILE_XML);
            for (ProductoEntity vehiculo : vehiculos) {
                //Pintar por consola
                System.out.println(vehiculo.toPrint());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }
}
