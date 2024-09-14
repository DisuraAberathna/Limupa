/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import entity.Product;
import java.io.Serializable;

/**
 *
 * @author SINGER
 */
public class WatchlistDTO implements Serializable {

    private Product product;

    public WatchlistDTO() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
