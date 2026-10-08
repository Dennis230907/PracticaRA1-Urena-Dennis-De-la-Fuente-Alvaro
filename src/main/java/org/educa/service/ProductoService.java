package org.educa.service;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBException;
import org.educa.dao.ProductoDAOImpl;
import org.educa.entity.ProductoEntity;
import org.educa.dao.ProductoDAO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio creado para la gestión del inventario.
 *
 * Actúa como intermediario entre DAO y las actividades.
 */
public class ProductoService {
    /**
     * Extrae el inventario desde un archivo XML utilizando la capa DAO,
     * convierte los datos del XML en objetos de nuestra aplicación y realiza los cálculos de precios y beneficios.
     *
     * @param fileXml Ruta absoluta o relativa al archivo XML que contiene los datos.
     * @return Una lista con las entidades de los productos con sus cálculos finalizados.
     * @throws JAXBException Si ocurre un error en el mapeo de los datos XML.
     */
    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {


            ProductoDAO dao = new ProductoDAOImpl();

            Productos datos = dao.obtenerProductos(fileXml);

            List<ProductoEntity> listaProductos = new ArrayList<>();

            if (datos != null && datos.getProducto() != null) {
                for (Producto p : datos.getProducto()) {
                    ProductoEntity entidad = new ProductoEntity();
                    entidad.setProducto(p);

                   double precio = p.getPrecio().doubleValue();
                   double descuento= p.getDescuento().doubleValue();
                   double almacenaje= p.getCostes().getCostesAlmacenaje().doubleValue();
                   double envio= p.getCostes().getCostesEnvio().doubleValue();

                   double descuentoAplicado = (precio * descuento)/100;
                   double precioFinalCalculado=precio-descuentoAplicado;
                   double costeTotalCalculado=almacenaje+envio;
                   double beneficioCalculado=precioFinalCalculado-costeTotalCalculado;

                   entidad.setPrecioFinal(BigDecimal.valueOf(precioFinalCalculado));
                   entidad.setCost(BigDecimal.valueOf(costeTotalCalculado));
                   entidad.setProfit(BigDecimal.valueOf(beneficioCalculado));

                    listaProductos.add(entidad);
                }
            }
            return listaProductos;

    }

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
            List<ProductoEntity> listaProductos = readFile(fileXml);
            File xmlFile = new File(fileXml);
            String rutaAbsoluta = xmlFile.getAbsolutePath();
            long fileSize = xmlFile.length();
            String nombreXMLsinExtension = "inventario_junio2026";
            String fecha = "junio2026";

            String rutaDestinoCompleta = path + "result_junio2026.txt";





    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}
