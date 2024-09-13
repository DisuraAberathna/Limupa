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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", false);

        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        Criteria criteria = session.createCriteria(Product.class);

        if (requestJsonObject.has("category_name")) {
            String category_name = requestJsonObject.get("category_name").getAsString();
            Criteria categoryCriteria = session.createCriteria(Category.class);
            categoryCriteria.add(Restrictions.eq("name", category_name));
            Category category = (Category) categoryCriteria.uniqueResult();

            Criteria criteria3 = session.createCriteria(Model.class);
            criteria3.add(Restrictions.eq("category", category));
            List<Model> modelList = criteria3.list();

            criteria.add(Restrictions.in("model", modelList));
        }

        if (requestJsonObject.has("condition_name")) {
            String condition_name = requestJsonObject.get("condition_name").getAsString();
            Criteria conditionCriteria = session.createCriteria(ProductCondition.class);
            conditionCriteria.add(Restrictions.eq("name", condition_name));
            ProductCondition product_condition = (ProductCondition) conditionCriteria.uniqueResult();

            criteria.add(Restrictions.eq("condition", product_condition));
        }

        if (requestJsonObject.has("color_name")) {
            String color_name = requestJsonObject.get("color_name").getAsString();
            Criteria colorCriteria = session.createCriteria(Color.class);
            colorCriteria.add(Restrictions.eq("name", color_name));
            Color color = (Color) colorCriteria.uniqueResult();

            criteria.add(Restrictions.eq("color", color));
        }

        Double price_range_start = requestJsonObject.get("price_range_start").getAsDouble();
        Double price_range_end = requestJsonObject.get("price_range_end").getAsDouble();

        criteria.add(Restrictions.ge("price", price_range_start));
        criteria.add(Restrictions.le("price", price_range_end));

        String sort_text = requestJsonObject.get("sort_text").getAsString();

        if (sort_text.equals("Sort by Latest")) {
            criteria.addOrder(Order.desc("id"));
        } else if (sort_text.equals("Sort by Oldest")) {
            criteria.addOrder(Order.asc("id"));
        } else if (sort_text.equals("Sort by Name")) {
            criteria.addOrder(Order.asc("title"));
        } else if (sort_text.equals("Sort by Price")) {
            criteria.addOrder(Order.asc("price"));
        }

        jsonObject.addProperty("allProductCount", criteria.list().size());

        int firstResult = requestJsonObject.get("firstResult").getAsInt();
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(9);

        List<Product> productList = criteria.list();

        for (Product product : productList) {
            product.setUser(null);
        }
        session.close();

        jsonObject.addProperty("ok", true);
        jsonObject.add("productList", gson.toJsonTree(productList));

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }
}
