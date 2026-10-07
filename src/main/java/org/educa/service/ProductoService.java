package org.educa.service;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBException;
import org.educa.entity.ProductoEntity;
import org.educa.dao.ProductoDAO;
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
            ProductoDAO dao = new ProductoDAO();
            Productos datos = dao.leerXML(fileXml);

            List<ProductoEntity> listaProductos = new ArrayList<>();

            if (datos != null && datos.getProducto() != null) {
                for (Producto p : datos.getProducto()) {
                    ProductoEntity entidad = new ProductoEntity();

                    // 1. Metemos todo el objeto crudo de golpe (adiós a las 20 líneas en rojo)
                    entidad.setProducto(p);

                    // 2. Cálculos financieros usando BigDecimal
                    BigDecimal cien = new BigDecimal("100");

                    // descuentoAplicado = (precio * descuento) / 100
                    BigDecimal descuentoAplicado = p.getPrecio().multiply(p.getDescuento()).divide(cien);

                    // precioFinal = precio - descuentoAplicado
                    BigDecimal precioFinal = p.getPrecio().subtract(descuentoAplicado);

                    // costeTotal = almacenaje + envio
                    BigDecimal costeTotal = p.getCostes().getCostesAlmacenaje().add(p.getCostes().getCostesEnvio());

                    // beneficio = precioFinal - costeTotal
                    BigDecimal beneficio = precioFinal.subtract(costeTotal);

                    // 3. Guardamos los resultados
                    entidad.setPrecioFinal(precioFinal);
                    entidad.setCost(costeTotal);
                    entidad.setProfit(beneficio);

                    listaProductos.add(entidad);
                }
            }
            return listaProductos;

    }

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        //TODO: Implementar

    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}
