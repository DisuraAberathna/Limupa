/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Brand;
import entity.Category;
import entity.Color;
import entity.Model;
import entity.OrderItem;
import entity.Orders;
import entity.Product;
import entity.ProductCondition;
import entity.User;
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
@WebServlet(name = "UserLoadData", urlPatterns = {"/UserLoadData"})
public class UserLoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        UserDTO user = (UserDTO) req.getSession().getAttribute("user");

        Session session = HibernateUtil.getSessionFactory().openSession();

        UserDTO userDTO = new UserDTO();
        userDTO.setF_name(user.getF_name());
        userDTO.setL_name(user.getL_name());
        userDTO.setEmail(user.getEmail());

        Criteria userCriteria = session.createCriteria(User.class);
        userCriteria.add(Restrictions.eq("id", user.getId()));
        User dbuser = (User) userCriteria.list().get(0);

        Criteria productCriteria = session.createCriteria(Product.class);
        productCriteria.add(Restrictions.eq("user", dbuser));
        productCriteria.addOrder(Order.desc("id"));
        jsonObject.addProperty("allProductCount", productCriteria.list().size());
        productCriteria.setFirstResult(0);
        productCriteria.setMaxResults(10);

        List<Product> productList = productCriteria.list();

        for (Product product : productList) {
            product.setUser(null);
        }

        Criteria orderCriteria = session.createCriteria(OrderItem.class, "item");
        orderCriteria.createAlias("item.order", "order");
        orderCriteria.add(Restrictions.eq("order.user", dbuser));
        orderCriteria.addOrder(Order.desc("id"));
        jsonObject.addProperty("allOrderCount", orderCriteria.list().size());

        orderCriteria.setFirstResult(0);
        orderCriteria.setMaxResults(10);

        List<OrderItem> orderList = orderCriteria.list();

        for (OrderItem orderItem : orderList) {
            orderItem.getOrder().setUser(null);
        }

        Criteria categoryCriteria = session.createCriteria(Category.class);
        categoryCriteria.add(Restrictions.eq("status", 1));
        categoryCriteria.addOrder(Order.asc("name"));
        List<Category> categoryList = categoryCriteria.list();

        Criteria brandCriteria = session.createCriteria(Brand.class);
        brandCriteria.add(Restrictions.eq("status", 1));
        brandCriteria.addOrder(Order.asc("name"));
        List<Model> brandList = brandCriteria.list();

        Criteria modelCriteria = session.createCriteria(Model.class);
        modelCriteria.add(Restrictions.eq("status", 1));
        modelCriteria.addOrder(Order.asc("name"));
        List<Model> modelList = modelCriteria.list();

        Criteria colorCcriteria = session.createCriteria(Color.class);
        colorCcriteria.addOrder(Order.asc("name"));
        List<Color> colorList = colorCcriteria.list();

        Criteria conditionCriteria = session.createCriteria(ProductCondition.class);
        conditionCriteria.addOrder(Order.asc("name"));
        List<ProductCondition> conditionList = conditionCriteria.list();

        jsonObject.add("user", gson.toJsonTree(userDTO));
        jsonObject.add("orderList", gson.toJsonTree(orderList));
        jsonObject.add("productList", gson.toJsonTree(productList));
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("brandList", gson.toJsonTree(brandList));
        jsonObject.add("modelList", gson.toJsonTree(modelList));
        jsonObject.add("colorList", gson.toJsonTree(colorList));
        jsonObject.add("conditionList", gson.toJsonTree(conditionList));

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));

        session.close();
    }

}
