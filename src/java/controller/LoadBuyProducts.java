/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.OrderItem;
import entity.Product;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadBuyProducts", urlPatterns = {"/LoadBuyProducts"})
public class LoadBuyProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            if (req.getSession().getAttribute("user") != null) {
                UserDTO sessionUser = (UserDTO) req.getSession().getAttribute("user");
                JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("id", sessionUser.getId()));

                if (!criteria.list().isEmpty()) {
                    User user = (User) criteria.list().get(0);
                    Criteria orderCriteria = session.createCriteria(OrderItem.class, "item");
                    orderCriteria.createAlias("item.order", "order");
                    orderCriteria.add(Restrictions.eq("order.user", user));

                    if (requestJsonObject.has("sort")) {
                        String sort = requestJsonObject.get("sort").getAsString();
                        switch (sort) {
                            case "0":
                                orderCriteria.addOrder(Order.desc("id"));
                                break;
                            case "1":
                                orderCriteria.addOrder(Order.asc("id"));
                                break;
                        }
                    }

                    jsonObject.addProperty("allOrderCount", orderCriteria.list().size());

                    if (requestJsonObject.has("firstResult")) {
                        int firstResult = requestJsonObject.get("firstResult").getAsInt();
                        orderCriteria.setFirstResult(firstResult);
                        orderCriteria.setMaxResults(10);
                    }
                    List<OrderItem> orderList = orderCriteria.list();

                    for (OrderItem orderItem : orderList) {
                        orderItem.getOrder().setUser(null);
                    }

                    jsonObject.add("orderList", gson.toJsonTree(orderList));
                    jsonObject.addProperty("ok", true);
                }
            }
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

}
