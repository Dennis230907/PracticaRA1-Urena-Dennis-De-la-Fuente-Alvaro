package org.educa.service;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBException;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.educa.dao.ProductoDAOImpl;
import org.educa.entity.ProductoEntity;
import org.educa.dao.ProductoDAO;
import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.FileInputStream;
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

    /**
     * Lee el fichero XML, recoge los datos de Producto
     * y crea el fichero Excel con los datos solicitados
     * @param path
     * @param fileXml
     * @throws JAXBException
     * @throws IOException
     * @throws ParseException
     */

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        List<ProductoEntity> listaProductos = readFile(fileXml);
        org.apache.poi.ss.usermodel.Workbook libro = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet hoja = libro.createSheet("Inventario");

        org.apache.poi.ss.usermodel.Font fontHeader = libro.createFont();
        fontHeader.setBold(true);

        org.apache.poi.ss.usermodel.CellStyle styleHeader = libro.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        org.apache.poi.ss.usermodel.CellStyle styleFilaPar = libro.createCellStyle();
        styleFilaPar.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        styleFilaPar.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        org.apache.poi.ss.usermodel.CellStyle styleFilaImpar = libro.createCellStyle();
        styleFilaImpar.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        styleFilaImpar.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        int columna = 0;
        org.apache.poi.ss.usermodel.Row headerFila = hoja.createRow(columna++);
        String[] columns = {
                "Codigo", "Número de Serie", "Precio", "Descuento",
                "Precio Final", "Costes Envío", "Costes Almacenaje", "Beneficio"
        };

        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell celda = headerFila.createCell(i);
            celda.setCellValue(columns[i]);
            celda.setCellStyle(styleHeader);
        }

        int filaIndice = 0;
        for (ProductoEntity entidad : listaProductos) {
            org.apache.poi.ss.usermodel.Row fila = hoja.createRow(columna++);

            org.apache.poi.ss.usermodel.CellStyle colorFila = (filaIndice % 2 == 0) ? styleFilaPar : styleFilaImpar;

            org.apache.poi.ss.usermodel.Cell celda0 = fila.createCell(0); celda0.setCellValue(entidad.getProducto().getCodigo()); celda0.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda1 = fila.createCell(1); celda1.setCellValue(entidad.getProducto().getNumeroSerie()); celda1.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda2 = fila.createCell(2); celda2.setCellValue(entidad.getProducto().getPrecio().doubleValue() + " €"); celda2.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda3 = fila.createCell(3); celda3.setCellValue(entidad.getProducto().getDescuento().doubleValue() + "%"); celda3.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda4 = fila.createCell(4); celda4.setCellValue(entidad.getPrecioFinal().doubleValue() + " €"); celda4.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda5 = fila.createCell(5); celda5.setCellValue(entidad.getProducto().getCostes().getCostesEnvio().doubleValue() + " €"); celda5.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda6 = fila.createCell(6); celda6.setCellValue(entidad.getProducto().getCostes().getCostesAlmacenaje().doubleValue() + " €"); celda6.setCellStyle(colorFila);
            org.apache.poi.ss.usermodel.Cell celda7 = fila.createCell(7); celda7.setCellValue(entidad.getProfit().doubleValue() + " €"); celda7.setCellStyle(colorFila);

            filaIndice++;
        }

        for (int i = 0; i < columns.length; i++) {
            hoja.autoSizeColumn(i);
        }

        String rutaExcel = path + "export_junio2026.xlsx";

        try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(rutaExcel)) {
            libro.write(fileOut);
        }
        libro.close();

    }
}
