package org.educa.dao;
import generated.Productos;

public interface ProductoDAO {
    Productos obtenerProductos(String FILE_XML);
}
