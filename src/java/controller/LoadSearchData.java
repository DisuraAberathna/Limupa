/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Category;
import entity.Color;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadSearchData", urlPatterns = {"/LoadSearchData"})
public class LoadSearchData extends HttpServlet {

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

                Criteria brandCriteria = session.createCriteria(Brand.class);
                brandCriteria.add(Restrictions.eq("category", category));
                brandCriteria.add(Restrictions.eq("status", 1));
                brandCriteria.addOrder(Order.asc("name"));

                List<Brand> brandList = brandCriteria.list();
                categoryJson.add("brandList", gson.toJsonTree(brandList));

                categoryArray.add(categoryJson);
            }

            Criteria colorCriteria = session.createCriteria(Color.class);
            colorCriteria.addOrder(Order.asc("name"));
            List<Color> colorList = colorCriteria.list();

            Criteria conditionCriteria = session.createCriteria(ProductCondition.class);
            conditionCriteria.addOrder(Order.asc("name"));
            List<ProductCondition> conditionList = conditionCriteria.list();

            jsonObject.add("categoryList", categoryArray);
            jsonObject.add("colorList", gson.toJsonTree(colorList));
            jsonObject.add("conditionList", gson.toJsonTree(conditionList));

            session.close();

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(jsonObject));
        } catch (NumberFormatException | HibernateException e) {
            System.out.println(e.getMessage());
        }
    }
}
