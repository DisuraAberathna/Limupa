/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Model;
import entity.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadProductViewData", urlPatterns = {"/LoadProductViewData"})
public class LoadProductViewData extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject jsonObject = new JsonObject();
        
        try {
            String productID = req.getParameter("id");
            
            if (Validate.isInteger(productID)) {
                Criteria productCriteria = session.createCriteria(Product.class);
                productCriteria.add(Restrictions.eq("id", Integer.valueOf(productID)));
                productCriteria.add(Restrictions.eq("status", 1));
                
                Product product = (Product) productCriteria.list().get(0);
                
                if (product != null) {
                    product.getUser().setPassword(null);
                    product.getUser().setVerification(null);
                    product.getUser().setEmail(null);
                    
                    Criteria modelCriteria = session.createCriteria(Model.class);
                    modelCriteria.add(Restrictions.eq("brand", product.getModel().getBrand()));
                    List<Model> modelList = modelCriteria.list();
                    
                    Criteria similerProductCriteria = session.createCriteria(Product.class);
                    similerProductCriteria.add(Restrictions.in("model", modelList));
                    similerProductCriteria.add(Restrictions.eq("status", 1));
                    similerProductCriteria.add(Restrictions.ne("id", Integer.valueOf(productID)));
                    similerProductCriteria.addOrder(Order.asc("id"));
                    similerProductCriteria.setMaxResults(10);
                    List<Product> productList = similerProductCriteria.list();
                    
                    for (Product p : productList) {
                        p.getUser().setPassword(null);
                        p.getUser().setVerification(null);
                        p.getUser().setEmail(null);
                    }
                    
                    jsonObject.add("product", gson.toJsonTree(product));
                    jsonObject.add("productList", gson.toJsonTree(productList));
                    
                    session.close();
                } else {
                    jsonObject.addProperty("error", "No product");
                }
            } else {
                jsonObject.addProperty("error", "No product");
            }
        } catch (NumberFormatException | HibernateException e) {
            System.out.println(e.getMessage());
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }
}
