/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.Cart;
import entity.OrderItem;
import entity.OrderStatus;
import entity.Orders;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", false);

        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();
        Transaction transaction = session.beginTransaction();

        if (httpSession.getAttribute("user") != null) {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
            Criteria userCriteria = session.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("id", userDTO.getId()));
            User user = (User) userCriteria.uniqueResult();

            Criteria addressCriteria = session.createCriteria(Address.class);
            addressCriteria.add(Restrictions.eq("user", user));
            addressCriteria.add(Restrictions.eq("status", 1));
            addressCriteria.addOrder(Order.desc("id"));
            addressCriteria.setMaxResults(1);

            if (addressCriteria.list().isEmpty()) {
                jsonObject.addProperty("msg", "You haven't active address! Please add new or pick existing one.");
            } else {
                Address address = (Address) addressCriteria.list().get(0);
                saveOrder(session, transaction, user, address, jsonObject);
            }
        } else {
            jsonObject.addProperty("msg", "Invalid sign in! Please sign in again.");
        }
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

    private void saveOrder(Session session, Transaction transaction, User user, Address address, JsonObject jsonObject) {
        try {
            Orders order = new Orders();
            order.setAddress(address);
            order.setDate_time(new Date());
            order.setUser(user);

            int order_id = (int) session.save(order);

            Criteria cartCriteria = session.createCriteria(Cart.class);
            cartCriteria.add(Restrictions.eq("user", user));
            List<Cart> cartList = cartCriteria.list();

            OrderStatus orderStatus = (OrderStatus) session.get(OrderStatus.class, 5);

            double amount = 0;
            String items = "";
            for (Cart cartItem : cartList) {

                amount += (cartItem.getQty() * cartItem.getProduct().getPrice()) + cartItem.getProduct().getShipping();

                items += cartItem.getProduct().getTitle() + " x " + cartItem.getQty();

                Product product = cartItem.getProduct();

                OrderItem order_Item = new OrderItem();
                order_Item.setOrder(order);
                order_Item.setOrderStatus(orderStatus);
                order_Item.setProduct(product);
                order_Item.setQty(cartItem.getQty());

                session.save(order_Item);

                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                session.delete(cartItem);
            }
            transaction.commit();

            String merchnt_id = "1228209";
            String formatedAmount = new DecimalFormat("0.00").format(amount);
            String currency = "LKR";
            String merchantSecret = "ODUxNjQ0NzUyMjI0OTQyMDE1MjIzNDg4NzY3OTkxMjAxMjQ0MzEw";
            String merchantSecretMd5Hash = PayHere.generateMD5(merchantSecret);

            JsonObject payhere = new JsonObject();
            payhere.addProperty("merchant_id", merchnt_id);

            payhere.addProperty("sandbox", true);

            payhere.addProperty("return_url", "");
            payhere.addProperty("cancel_url", "");
            payhere.addProperty("notify_url", "");

            payhere.addProperty("first_name", user.getF_name());
            payhere.addProperty("last_name", user.getL_name());
            payhere.addProperty("email", user.getEmail());
            payhere.addProperty("phone", "");
            payhere.addProperty("address", "");
            payhere.addProperty("city", "");
            payhere.addProperty("country", "");
            payhere.addProperty("order_id", String.valueOf(order_id));
            payhere.addProperty("items", items);
            payhere.addProperty("currency", currency);
            payhere.addProperty("amount", formatedAmount);

            String md5Hash = PayHere.generateMD5(merchnt_id + order_id + formatedAmount + currency + merchantSecretMd5Hash);
            payhere.addProperty("hash", md5Hash);

            jsonObject.addProperty("ok", true);

            Gson gson = new Gson();
            jsonObject.add("payhereJson", gson.toJsonTree(payhere));
        } catch (HibernateException e) {
            transaction.rollback();
        }
    }
}
