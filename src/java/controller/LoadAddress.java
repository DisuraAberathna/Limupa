/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.City;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadAddress", urlPatterns = {"/LoadAddress"})
public class LoadAddress extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        if (req.getSession().getAttribute("user") != null) {
            UserDTO sessionUser = (UserDTO) req.getSession().getAttribute("user");
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            try {
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("id", sessionUser.getId()));
                
                if (!criteria.list().isEmpty()) {
                    User user = (User) criteria.list().get(0);
                    
                    Criteria addressCriteria = session.createCriteria(Address.class);
                    addressCriteria.add(Restrictions.eq("user", user));
                    List<Address> addressList = addressCriteria.list();
                    
                    for (Address address : addressList) {
                        address.getUser().setPassword(null);
                    }
                    
                    Criteria cityCriteria = session.createCriteria(City.class);
                    List<City> cityList = cityCriteria.list();
                    
                    Gson gson = new Gson();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("addressList", gson.toJsonTree(addressList));
                    jsonObject.add("cityList", gson.toJsonTree(cityList));
                    
                    resp.setContentType("application/json");
                    resp.getWriter().write(gson.toJson(jsonObject));
                }
            } catch (HibernateException e) {
                System.out.println(e.getMessage());
            }
            session.close();
        }
    }
}
