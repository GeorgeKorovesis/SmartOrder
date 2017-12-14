package com.example.george.smartorder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by George on 11/15/2015.
 */
public class OrderItem implements Serializable {
    private ArrayList<String> SugarQuantity,SugarType,Misc,Size;
    private String ProductName;
    private Integer Table;
    private String Comments;
    private Boolean CommentsWritten;

    public OrderItem()
    {
        this.SugarQuantity=new ArrayList<>();
        this.SugarType=new ArrayList<>();
        this.Misc=new ArrayList<>();
        this.Size=new ArrayList<>();
        this.ProductName="";
        this.Table=0;
        this.Comments="";
        this.CommentsWritten = false;
    }

    public void setSugarQuantity(String sugarQuantity)
    {
        this.SugarQuantity.add(sugarQuantity);
    }
    public void setSugarType(String sugarType)
    {
        this.SugarType.add(sugarType);
    }
    public void setMisc(String Misc)
    {
        this.Misc.add(Misc);
    }
    public void setSize(String Size)
    {
        this.Size.add(Size);
    }
    public void setProductName(String ProductName)
    {
        this.ProductName = ProductName;
    }
    public void setTable(Integer table) {this.Table = table;}
    public void setComments(String comments) {this.Comments = comments;
    this.CommentsWritten=true;
    }

    public ArrayList<String> getSugarQuantity()
    {
        return this.SugarQuantity;
    }
    public ArrayList<String> getSugarType()
    {
        return this.SugarType;
    }
    public ArrayList<String> getMisc()
    {
        return this.Misc;
    }
    public ArrayList<String> getSize()
    {
        return this.Size;
    }
    public String getProductName()
    {
        return this.ProductName;
    }
    public Integer getTable() {return this.Table;}
    public Boolean areThereAnyComments() {return this.CommentsWritten;}
    public String getComments() {return this.Comments;}
}
