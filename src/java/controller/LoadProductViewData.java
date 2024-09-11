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
import model.HibernateUtill;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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
        Session session = HibernateUtill.getSessionFactory().openSession();

        try {
            String productID = req.getParameter("id");

            if (Validate.isInteger(productID)) {
                Product product = (Product) session.get(Product.class, Integer.valueOf(productID));
                product.getUser().setPassword(null);
                product.getUser().setVerification(null);
                product.getUser().setEmail(null);

                Criteria modelCriteria = session.createCriteria(Model.class);
                modelCriteria.add(Restrictions.eq("brand", product.getModel().getBrand()));
                List<Model> modelList = modelCriteria.list();

                Criteria productCriteria = session.createCriteria(Product.class);
                productCriteria.add(Restrictions.in("model", modelList));
                productCriteria.add(Restrictions.ne("id", product.getId()));
                productCriteria.setMaxResults(6);

                List<Product> productList = productCriteria.list();

                for (Product product1 : productList) {
                    product1.getUser().setPassword(null);
                    product1.getUser().setVerification(null);
                    product1.getUser().setEmail(null);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.add("product", gson.toJsonTree(product));
                jsonObject.add("productList", gson.toJsonTree(productList));

                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(jsonObject));
            } 
        } catch (IOException | NumberFormatException | HibernateException e) {
            System.out.println(e.getMessage());
        }

    }

}
