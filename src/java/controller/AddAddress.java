/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Address;
import entity.Brand;
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
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "AddAddress", urlPatterns = {"/AddAddress"})
public class AddAddress extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new Gson();
        JsonObject reqObject = gson.fromJson(req.getReader(), JsonObject.class);

        String line_1 = reqObject.get("line_1").getAsString();
        String line_2 = reqObject.get("line_2").getAsString();
        String cityId = reqObject.get("city").getAsString();
        String postal_code = reqObject.get("postal_code").getAsString();
        String mobile = reqObject.get("mobile").getAsString();

        if (line_1.isEmpty()) {
            responseDTO.setMsg("Please enter address line 01!");
        } else if (line_2.isEmpty()) {
            responseDTO.setMsg("Please enter address line 02!");
        } else if (cityId.equals("0")) {
            responseDTO.setMsg("Please select city!");
        } else if (!Validate.isInteger(cityId)) {
            responseDTO.setMsg("Invalid city!");
        } else if (postal_code.isEmpty()) {
            responseDTO.setMsg("Please enter postal code!");
        } else if (!Validate.isInteger(postal_code)) {
            responseDTO.setMsg("Invalid postal code!");
        } else if (postal_code.length() != 5) {
            responseDTO.setMsg("Invalid postal code!");
        } else if (mobile.isEmpty()) {
            responseDTO.setMsg("Please enter mobile number!");
        } else if (!Validate.isValidMobile(mobile)) {
            responseDTO.setMsg("Invalid mobile number!");
        } else {
            if (req.getSession().getAttribute("user") != null) {
                UserDTO sessionUser = (UserDTO) req.getSession().getAttribute("user");
                Session session = HibernateUtil.getSessionFactory().openSession();

                try {
                    Criteria criteria = session.createCriteria(User.class);
                    criteria.add(Restrictions.eq("id", sessionUser.getId()));

                    if (!criteria.list().isEmpty()) {
                        User user = (User) criteria.list().get(0);

                        City city = (City) session.get(City.class, Integer.valueOf(cityId));

                        if (city != null) {
                            Criteria addressCriteria = session.createCriteria(Address.class);
                            addressCriteria.add(Restrictions.eq("user", user));
                            List<Address> addressList = addressCriteria.list();

                            boolean addNew = true;

                            for (Address address : addressList) {
                                if (address.getLine_1().equals(line_1) && address.getLine_2().equals(line_2) && address.getMobile().equals(mobile) && address.getPostal_code().equals(postal_code) && address.getCity().equals(city)) {
                                    address.setStatus(1);
                                    addNew = false;
                                } else {
                                    address.setStatus(0);
                                    addNew = true;
                                }
                                session.update(address);
                                session.beginTransaction().commit();
                            }

                            if (addNew) {
                                Address address = new Address();
                                address.setLine_1(line_1);
                                address.setLine_2(line_2);
                                address.setPostal_code(postal_code);
                                address.setMobile(mobile);
                                address.setStatus(1);
                                address.setUser(user);
                                address.setCity(city);

                                session.save(address);
                                session.beginTransaction().commit();
                            }
                            responseDTO.setOk(true);
                        } else {
                            responseDTO.setMsg("Please select a valid city!");
                        }
                    } else {
                        responseDTO.setMsg("Invalid user!");
                    }
                } catch (HibernateException e) {
                    System.out.println(e.getMessage());
                    responseDTO.setMsg("Unable to process request!");
                }
                session.close();
            } else {
                responseDTO.setMsg("Invalid sign in! Please sign in again.");
            }
        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }
}
