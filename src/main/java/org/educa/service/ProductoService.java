package org.educa.service;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBException;
import org.educa.dao.ProductoDAOImpl;
import org.educa.entity.ProductoEntity;
import org.educa.dao.ProductoDAO;
import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

                    BigDecimal precio = p.getPrecio();
                    BigDecimal descuento = p.getDescuento();
                    BigDecimal almacenaje = p.getCostes().getCostesAlmacenaje();
                    BigDecimal envio = p.getCostes().getCostesEnvio();
                    BigDecimal descuentoAplicado = precio.multiply(descuento).divide(BigDecimal.valueOf(100));
                    BigDecimal precioFinalCalculado = precio.subtract(descuentoAplicado);
                    BigDecimal costeTotalCalculado = almacenaje.add(envio);
                    BigDecimal beneficioCalculado = precioFinalCalculado.subtract(costeTotalCalculado);

                    entidad.setPrecioFinal(precioFinalCalculado.setScale(2,RoundingMode.HALF_UP));
                    entidad.setCost(costeTotalCalculado.setScale(2,RoundingMode.HALF_UP));
                    entidad.setProfit(beneficioCalculado.setScale(2,RoundingMode.HALF_UP));

                    listaProductos.add(entidad);
                }
            }
            return listaProductos;

    }

    /**
     * Lee el fichero XML, procesa los productos para calcular
     * el número total de productos y beneficio total y
     * crea el fichero.txt en la ruta asignada con todos los datos solicitados.
     *
     * @param path
     * @param fileXml
     * @throws JAXBException
     * @throws IOException
     */

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        List<ProductoEntity> listaProductos = readFile(fileXml);
        File xmlFile = new File(fileXml);
        String rutaAbsoluta = xmlFile.getAbsolutePath();
        long fileSize = xmlFile.length();
        String fecha = "junio2026";

        int numeroProductos = listaProductos.size();
        BigDecimal beneficioTotal = BigDecimal.valueOf(0);

        for (ProductoEntity producto : listaProductos) {
            if (producto.getProfit() != null) {
                beneficioTotal = beneficioTotal.add(producto.getProfit());
            }
        }

        SummaryEntity summary = new SummaryEntity();
        summary.setName(fecha);
        summary.setNumberOfProducts(numeroProductos);
        summary.setTotalProfit(beneficioTotal);
        summary.setFileAbsolutePath(rutaAbsoluta);
        summary.setFileName("inventario_junio2026");
        summary.setFileSize(fileSize);

        String rutaDestinoCompleta = path + "result_junio2026.txt";
        ProductoDAO dao = new ProductoDAOImpl();
        dao.guardarFicheroTXT(rutaDestinoCompleta, summary.toPrint());
    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}
