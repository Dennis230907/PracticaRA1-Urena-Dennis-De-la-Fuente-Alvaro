package org.educa.service;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBException;
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

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        List<ProductoEntity> listaProductos = readFile(fileXml);
        org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet hoja = workbook.createSheet("Inventario");

        org.apache.poi.ss.usermodel.Font fontHeader = workbook.createFont();
        fontHeader.setBold(true);

        org.apache.poi.ss.usermodel.CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setFont(fontHeader);
        styleHeader.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        org.apache.poi.ss.usermodel.CellStyle styleRowPar = workbook.createCellStyle();
        styleRowPar.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_TURQUOISE.getIndex()); // O el color suave que prefieras
        styleRowPar.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        org.apache.poi.ss.usermodel.CellStyle styleRowImpar = workbook.createCellStyle();
        styleRowImpar.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        styleRowImpar.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        int rowNum = 0;
        org.apache.poi.ss.usermodel.Row headerRow = hoja.createRow(rowNum++);
        String[] columns = {
                "Codigo", "Número de Serie", "Precio", "Descuento",
                "Precio Final", "Costes Envío", "Costes Almacenaje", "Beneficio"
        };

        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(styleHeader);
        }

        int rowIndex = 0;
        for (ProductoEntity entidad : listaProductos) {
            org.apache.poi.ss.usermodel.Row row = hoja.createRow(rowNum++);

            org.apache.poi.ss.usermodel.CellStyle currentStyle = (rowIndex % 2 == 0) ? styleRowPar : styleRowImpar;

            org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0); cell0.setCellValue(entidad.getProducto().getCodigo()); cell0.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1); cell1.setCellValue(entidad.getProducto().getNumeroSerie()); cell1.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2); cell2.setCellValue(entidad.getProducto().getPrecio().doubleValue() + " €"); cell2.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3); cell3.setCellValue(entidad.getProducto().getDescuento().doubleValue() + "%"); cell3.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4); cell4.setCellValue(entidad.getPrecioFinal().doubleValue() + " €"); cell4.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5); cell5.setCellValue(entidad.getProducto().getCostes().getCostesEnvio().doubleValue() + " €"); cell5.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6); cell6.setCellValue(entidad.getProducto().getCostes().getCostesAlmacenaje().doubleValue() + " €"); cell6.setCellStyle(currentStyle);
            org.apache.poi.ss.usermodel.Cell cell7 = row.createCell(7); cell7.setCellValue(entidad.getProfit().doubleValue() + " €"); cell7.setCellStyle(currentStyle);

            rowIndex++;
        }

        for (int i = 0; i < columns.length; i++) {
            hoja.autoSizeColumn(i);
        }

        String rutaExcel = path + "export_junio2026.xlsx";

        try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(rutaExcel)) {
            workbook.write(fileOut);
        }
        workbook.close();

    }
}
