/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Cart;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadCheckout", urlPatterns = {"/LoadCheckout"})
public class LoadCheckout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", false);

        HttpSession httpSession = req.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();

        if (httpSession.getAttribute("user") != null) {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

            Criteria userCriteria = session.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("id", userDTO.getId()));
            User user = (User) userCriteria.uniqueResult();

            Criteria cartCriteria = session.createCriteria(Cart.class);
            cartCriteria.add(Restrictions.eq("user", user));
            List<Cart> cartList = cartCriteria.list();

            for (Cart cart : cartList) {
                cart.setUser(null);
                cart.getProduct().setUser(null);
            }
            jsonObject.add("cartList", gson.toJsonTree(cartList));
            jsonObject.addProperty("ok", true);

            session.close();
        } else {
            jsonObject.addProperty("msg", "Not signed in");
        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }
}
