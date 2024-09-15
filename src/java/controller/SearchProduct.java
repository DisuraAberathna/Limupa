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

        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        Criteria criteria = session.createCriteria(Product.class);
        criteria.add(Restrictions.eq("status", 1));

        // Category filter
        if (requestJsonObject.has("category") && !requestJsonObject.get("category").getAsString().equals("0")) {
            int categoryId = requestJsonObject.get("category").getAsInt();

            Criteria categoryCriteria = session.createCriteria(Category.class);
            categoryCriteria.add(Restrictions.eq("id", categoryId));
            Category category = (Category) categoryCriteria.uniqueResult();

            if (category != null) {
                if (requestJsonObject.has("brand") && !requestJsonObject.get("brand").getAsString().equals("0")) {
                    int brandId = requestJsonObject.get("brand").getAsInt();

                    Criteria modelCriteria = session.createCriteria(Model.class, "model");
                    modelCriteria.createAlias("model.brand", "brand");
                    modelCriteria.createAlias("brand.category", "category");
                    modelCriteria.add(Restrictions.eq("brand.id", brandId));
                    modelCriteria.add(Restrictions.eq("category.id", categoryId));

                    List<Model> brandModelList = modelCriteria.list();
                    if (!brandModelList.isEmpty()) {
                        criteria.add(Restrictions.in("model", brandModelList));
                    }

                } else {
                    Criteria modelCriteria = session.createCriteria(Model.class, "model");
                    modelCriteria.createAlias("model.brand", "brand");
                    modelCriteria.createAlias("brand.category", "category");
                    modelCriteria.add(Restrictions.eq("category.id", categoryId));

                    List<Model> modelList = modelCriteria.list();
                    if (!modelList.isEmpty()) {
                        criteria.add(Restrictions.in("model", modelList));
                    }
                }
            }
        }

        if (requestJsonObject.has("condition")) {
            String conditionName = requestJsonObject.get("condition").getAsString();
            Criteria conditionCriteria = session.createCriteria(ProductCondition.class);
            conditionCriteria.add(Restrictions.eq("name", conditionName));
            ProductCondition productCondition = (ProductCondition) conditionCriteria.uniqueResult();

            if (productCondition != null) {
                criteria.add(Restrictions.eq("productCondition", productCondition));
            }
        }

        if (requestJsonObject.has("color") && !requestJsonObject.get("color").getAsString().equals("0")) {
            int colorId = requestJsonObject.get("color").getAsInt();
            Criteria colorCriteria = session.createCriteria(Color.class);
            colorCriteria.add(Restrictions.eq("id", colorId));
            Color color = (Color) colorCriteria.uniqueResult();

            if (color != null) {
                criteria.add(Restrictions.eq("color", color));
            }
        }

        if (requestJsonObject.has("priceStart") && requestJsonObject.has("priceEnd")) {
            Double priceStart = requestJsonObject.get("priceStart").getAsDouble();
            Double priceEnd = requestJsonObject.get("priceEnd").getAsDouble();

            criteria.add(Restrictions.ge("price", priceStart));
            criteria.add(Restrictions.le("price", priceEnd));
        }

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

        jsonObject.addProperty("allProductCount", criteria.list().size());

        if (requestJsonObject.has("firstResult")) {
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            criteria.setFirstResult(firstResult);
            criteria.setMaxResults(9);
        }

        List<Product> productList = criteria.list();

        for (Product product : productList) {
            product.setUser(null);
        }

        session.close();

        jsonObject.add("productList", gson.toJsonTree(productList));
        jsonObject.addProperty("ok", true);

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

}
