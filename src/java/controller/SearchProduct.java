/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Color;
import entity.Model;
import entity.Product;
import entity.ProductCondition;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "SearchProduct", urlPatterns = {"/SearchProduct"})
public class SearchProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", false);

        // Open a Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        // Create a Criteria for the Product entity
        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.eq("status", 1)); // Active products only

        // Category filter
        if (requestJsonObject.has("category") && !requestJsonObject.get("category").getAsString().equals("0")) {
            int categoryId = requestJsonObject.get("category").getAsInt();

            // Fetch category entity
            Criteria categoryCriteria = session.createCriteria(Category.class);
            categoryCriteria.add(Restrictions.eq("id", categoryId));
            Category category = (Category) categoryCriteria.uniqueResult();

            if (category != null) {
                // Brand filter
                if (requestJsonObject.has("brand") && !requestJsonObject.get("brand").getAsString().equals("0")) {
                    int brandId = requestJsonObject.get("brand").getAsInt();

                    // Fetch models for the specified brand and category
                    Criteria modelCriteria = session.createCriteria(Model.class, "model")
                            .createAlias("model.brand", "brand") // Join Model with Brand
                            .createAlias("brand.category", "category") // Join Brand with Category
                            .add(Restrictions.eq("brand.id", brandId)) // Filter by brand ID
                            .add(Restrictions.eq("category.id", categoryId));  // Filter by category ID

                    List<Model> brandModelList = modelCriteria.list();
                    if (!brandModelList.isEmpty()) {
                        criteria.add(Restrictions.in("model", brandModelList));  // Filter products by these models
                    }

                } else {
                    // No brand specified, filter by models within the category only
                    Criteria modelCriteria = session.createCriteria(Model.class, "model")
                            .createAlias("model.brand", "brand")
                            .createAlias("brand.category", "category")
                            .add(Restrictions.eq("category.id", categoryId));  // Filter by category ID

                    List<Model> modelList = modelCriteria.list();
                    if (!modelList.isEmpty()) {
                        criteria.add(Restrictions.in("model", modelList));  // Filter products by these models
                    }
                }
            }
        }

        // Condition filter
        if (requestJsonObject.has("condition")) {
            String conditionName = requestJsonObject.get("condition").getAsString();
            Criteria conditionCriteria = session.createCriteria(ProductCondition.class);
            conditionCriteria.add(Restrictions.eq("name", conditionName));
            ProductCondition productCondition = (ProductCondition) conditionCriteria.uniqueResult();

            if (productCondition != null) {
                criteria.add(Restrictions.eq("productCondition", productCondition));
            }
        }

        // Color filter
        if (requestJsonObject.has("color") && !requestJsonObject.get("color").getAsString().equals("0")) {
            int colorId = requestJsonObject.get("color").getAsInt();
            Criteria colorCriteria = session.createCriteria(Color.class);
            colorCriteria.add(Restrictions.eq("id", colorId));
            Color color = (Color) colorCriteria.uniqueResult();

            if (color != null) {
                criteria.add(Restrictions.eq("color", color));
            }
        }

        // Price range filter
        if (requestJsonObject.has("priceStart") && requestJsonObject.has("priceEnd")) {
            Double priceStart = requestJsonObject.get("priceStart").getAsDouble();
            Double priceEnd = requestJsonObject.get("priceEnd").getAsDouble();

            criteria.add(Restrictions.ge("price", priceStart));
            criteria.add(Restrictions.le("price", priceEnd));
        }

        // Sorting logic
        if (requestJsonObject.has("sort")) {
            String sort = requestJsonObject.get("sort").getAsString();
            switch (sort) {
                case "0":
                    criteria.addOrder(Order.desc("id"));
                    break;
                case "1":
                    criteria.addOrder(Order.asc("id"));
                    break;
                case "2":
                    criteria.addOrder(Order.asc("title"));
                    break;
                case "3":
                    criteria.addOrder(Order.desc("title"));
                    break;
                case "4":
                    criteria.addOrder(Order.asc("price"));
                    break;
                case "5":
                    criteria.addOrder(Order.desc("price"));
                    break;
            }
        }

        // Add product count to response
        jsonObject.addProperty("allProductCount", criteria.list().size());

        // Pagination
        if (requestJsonObject.has("firstResult")) {
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            criteria.setFirstResult(firstResult);
            criteria.setMaxResults(9);
        }

        // Execute the query and fetch the results
        List<Product> productList = criteria.list();

        // Remove any sensitive or unnecessary references (e.g., user data)
        for (Product product : productList) {
            product.setUser(null);
        }

        // Close the session
        session.close();

        // Prepare the response
        jsonObject.addProperty("ok", true);
        jsonObject.add("productList", gson.toJsonTree(productList));

        // Set response type and send the JSON response
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

}
