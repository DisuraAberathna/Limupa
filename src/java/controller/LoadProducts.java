/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadProducts", urlPatterns = {"/LoadProducts"})
public class LoadProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria categoryCriteria = session.createCriteria(Category.class);
            categoryCriteria.add(Restrictions.eq("status", 1));
            List<Category> categoryList = categoryCriteria.list();

            JsonObject jsonObject = new JsonObject();
            JsonArray categoryArray = new JsonArray();

            for (Category category : categoryList) {
                JsonObject categoryJson = new JsonObject();
                categoryJson.addProperty("id", category.getId());
                categoryJson.addProperty("name", category.getName());

                Criteria productCriteria = session.createCriteria(Product.class, "product");
                productCriteria.createAlias("product.model", "model");
                productCriteria.createAlias("model.brand", "brand");
                productCriteria.createAlias("brand.category", "category");
                productCriteria.add(Restrictions.eq("category.id", category.getId()));
                productCriteria.add(Restrictions.eq("product.status", 1));
                productCriteria.addOrder(Order.asc("id"));
                productCriteria.setMaxResults(10);

                List<Product> productList = productCriteria.list();
                categoryJson.add("productList", gson.toJsonTree(productList));

                categoryArray.add(categoryJson);
            }

            // Add categories with products to the response JSON
            jsonObject.add("categoryList", categoryArray);

            session.close();

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(jsonObject));
        } catch (NumberFormatException | HibernateException e) {
            System.out.println(e.getMessage());
        }
    }
}
