package com.berrybyte.API;

import java.util.List;

/**
 * Defines the contract for i catalogue api.
 */
public interface ICatalogueAPI {
/**
 * Performs get catalogue.
 * This method coordinates the main operation for this action.
 *
 * @param keyword keyword
 * @return result value
 * @throws Exception when the operation fails
 */

    List<String> getCatalogue(String keyword) throws Exception;
/**
 * Performs get product details.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @return result value
 * @throws Exception when the operation fails
 */

    String getProductDetails(int itemId) throws Exception;
/**
 * Performs check stock.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @return result value
 * @throws Exception when the operation fails
 */

    int checkStock(int itemId) throws Exception;
/**
 * Performs add item.
 * This method coordinates the main operation for this action.
 *
 * @param itemDetails item details
 * @return result value
 * @throws Exception when the operation fails
 */

    boolean addItem(String itemDetails) throws Exception;
/**
 * Executes the generate low stock report workflow.
 * This method coordinates the main operation for this action.
 *
 * @return result value
 * @throws Exception when the operation fails
 */

    List<String> generateLowStockReport() throws Exception;
}
