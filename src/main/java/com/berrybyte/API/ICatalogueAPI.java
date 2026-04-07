package com.berrybyte.API;

import java.util.List;

public interface ICatalogueAPI {

    List<String> getCatalogue(String keyword) throws Exception;

    String getProductDetails(int itemId) throws Exception;

    int checkStock(int itemId) throws Exception;

    boolean addItem(String itemDetails) throws Exception;

    List<String> generateLowStockReport() throws Exception;
}