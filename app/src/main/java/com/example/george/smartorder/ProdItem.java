package com.example.george.smartorder;

import java.io.Serializable;

/**
 * Created by George on 9/2/2015.
 */

public class ProdItem implements Serializable {



    /*These values are taken from Table with Products set by Admin*/
    private String Product_name;
    private String Product_category;




    public ProdItem(String Name, String Category)
    {
        Product_name = Name;
        Product_category = Category;
    }

    public ProdItem()
    {
        Product_name = null;
        Product_category = null;

    }
    public String getProductName ()
    {
        return Product_name;
    }
    public String getProductCategory() {
        return Product_category;
    }




}
