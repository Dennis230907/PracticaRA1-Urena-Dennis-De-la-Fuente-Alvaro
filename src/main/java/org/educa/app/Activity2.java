package org.educa.app;

import jakarta.xml.bind.JAXBException;
import org.educa.service.ProductoService;

import java.io.IOException;

public class Activity2 {

    private static final String PATH_TXT = "src/main/resources/export/";
    private static final String FILE_XML = "src/main/resources/xml/inventario_junio2026.xml";

    public static void main(String[] args) {
        ProductoService productoService = new ProductoService();
        try {
            productoService.exportSummary(PATH_TXT, FILE_XML);
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
