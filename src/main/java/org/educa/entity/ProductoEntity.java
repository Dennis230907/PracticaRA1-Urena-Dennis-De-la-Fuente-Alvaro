package org.educa.entity;

import generated.Producto;

import java.math.BigDecimal;

public class ProductoEntity {
    private Producto producto;
    private BigDecimal precioFinal;
    private BigDecimal cost;
    private BigDecimal profit;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String toPrint() {
        return "Código: " + producto.getCodigo() + System.lineSeparator() +
                "Número de serie: " + producto.getNumeroSerie() + System.lineSeparator() +
                "Marca: " + producto.getMarca() + System.lineSeparator() +
                "Modelo: " + producto.getModelo() + System.lineSeparator() +
                "Categoria: " + producto.getCategoria() + System.lineSeparator() +
                "Año de lanzamiento: " + producto.getAnioLanzamiento() + System.lineSeparator() +
                "Garantia (Meses): " + producto.getGarantiaMeses() + System.lineSeparator() +
                "Proveedor: " + producto.getProveedor().getEmpresa() + " " + producto.getProveedor().getCiudad() + " "
                + producto.getProveedor().getPais() + " " + producto.getProveedor().getCodigoPostal() + System.lineSeparator() +
                "Tipo Conexion: " + producto.getTipoConexion() + System.lineSeparator() +
                "Precio: " + producto.getPrecio() + System.lineSeparator() +
                "Descuento: " + producto.getDescuento() + System.lineSeparator() +
                "Precio final: " + getPrecioFinal() + System.lineSeparator() +
                "Coste: " + getCost() + System.lineSeparator() +
                "Beneficio: " + getProfit() + System.lineSeparator() +
                "====================================================";
    }
}
