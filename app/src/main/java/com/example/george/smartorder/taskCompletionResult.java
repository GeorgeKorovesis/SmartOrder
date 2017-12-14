package com.example.george.smartorder;

import org.json.JSONArray;

/**
 * Created by George on 9/20/2015.
 */
public interface taskCompletionResult {
   // public void setClickable(boolean status);
   // public void setProducts(JSONArray products);
    public void updateProductsDB(String itemToBeRemoved);
}

