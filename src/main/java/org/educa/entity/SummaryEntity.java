package org.educa.entity;

import java.math.BigDecimal;

public class SummaryEntity {
    private String name;
    private int numberOfProducts;
    private BigDecimal totalProfit;
    private String fileAbsolutePath;
    private String fileName;
    private long fileSize;

    public SummaryEntity() {
    }

    public SummaryEntity(String name, int numberOfProducts, BigDecimal totalProfit, String fileAbsolutePath,
                         String fileName, long fileSize) {
        this.name = name;
        this.numberOfProducts = numberOfProducts;
        this.totalProfit = totalProfit;
        this.fileAbsolutePath = fileAbsolutePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getFileAbsolutePath() {
        return fileAbsolutePath;
    }

    public void setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String toPrint() {
        return "Fecha: " + getName() + System.lineSeparator() +
                "NumeroDeVehiculos: " + getNumberOfProducts() + System.lineSeparator() +
                "BeneficioTotal: " + getTotalProfit() + System.lineSeparator() +
                "Ruta del Fichero: " + getFileAbsolutePath() + System.lineSeparator() +
                "Nombre del Fichero: " + getFileName() + System.lineSeparator() +
                "Tamaño del Fichero: " + getFileSize() + " bytes";
    }
}
