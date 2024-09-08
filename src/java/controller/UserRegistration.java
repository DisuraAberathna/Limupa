/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "UserRegistration", urlPatterns = {"/UserRegistration"})
public class UserRegistration extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new Gson();
        JsonObject reqObject = gson.fromJson(req.getReader(), JsonObject.class);

        String f_name = reqObject.get("f_name").getAsString();
        String l_name = reqObject.get("l_name").getAsString();
        String email = reqObject.get("email").getAsString();
        String password = reqObject.get("password").getAsString();
        String confirm_password = reqObject.get("confirm_password").getAsString();

        if (f_name.isEmpty()) {
            responseDTO.setMsg("Please enter your first name!");
        } else if (Validate.hasDigit(f_name)) {
            responseDTO.setMsg("First name can not contains digit!");
        } else if (f_name.length() > 50) {
            responseDTO.setMsg("First name must have less than 50 characters!");
        } else if (l_name.isEmpty()) {
            responseDTO.setMsg("Please enter your last name!");
        } else if (Validate.hasDigit(l_name)) {
            responseDTO.setMsg("Last name can not contains digit!");
        } else if (l_name.length() > 50) {
            responseDTO.setMsg("Last name must have less than 50 characters!");
        } else if (email.isEmpty()) {
            responseDTO.setMsg("Please enter your email!");
        } else if (!Validate.isValidEmail(email)) {
            responseDTO.setMsg("Please enter valid email!");
        } else if (email.length() > 100) {
            responseDTO.setMsg("First name must have less than 100 characters!");
        } else if (password.isEmpty()) {
            responseDTO.setMsg("Please enter your password!");
        } else if (password.length() < 8 || password.length() > 20) {
            responseDTO.setMsg("Password must be between 8 and 20 characters!");
        } else if (!Validate.isValidPassword(password)) {
            responseDTO.setMsg("Password must contain at least one uppercase letter, one lowercase letter, one number and one special character!");
        } else if (!password.equals(confirm_password)) {
            responseDTO.setMsg("Passwords do not match!");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (criteria.list().isEmpty()) {
                int otp = (int) (Math.random() * 1000000);

                User user = new User();
                user.setF_name(f_name);
                user.setL_name(l_name);
                user.setEmail(email);
                user.setPassword(password);
                user.setVerification(String.valueOf(otp));
                user.setStatus(1);

                Thread mailSender = new Thread() {
                    @Override
                    public void run() {
                        String content = "<head>\n"
                                + "  <style>\n"
                                + "    body {\n"
                                + "      font-family: Arial, sans-serif;\n"
                                + "      background-color: #f4f4f4;\n"
                                + "      margin: 0;\n"
                                + "      padding: 0;\n"
                                + "      -webkit-font-smoothing: antialiased;\n"
                                + "      -moz-osx-font-smoothing: grayscale;\n"
                                + "    }\n"
                                + "    .email-container {\n"
                                + "      max-width: 600px;\n"
                                + "      margin: 0 auto;\n"
                                + "      background-color: #ffffff;\n"
                                + "      padding: 20px;\n"
                                + "      border-radius: 8px;\n"
                                + "      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\n"
                                + "    }\n"
                                + "    .email-header {\n"
                                + "      text-align: center;\n"
                                + "      padding: 20px 0;\n"
                                + "      background-color: #ff3333;\n"
                                + "      color: white;\n"
                                + "      border-radius: 8px 8px 0 0;\n"
                                + "    }\n"
                                + "    .email-body {\n"
                                + "      padding: 20px;\n"
                                + "      border-left: 2px solid #ff3333;\n"
                                + "      border-right: 2px solid #ff3333;\n"
                                + "      text-align: center;\n"
                                + "    }\n"
                                + "    .email-body p {\n"
                                + "      font-size: 16px;\n"
                                + "      color: #333333;\n"
                                + "      line-height: 1.5;\n"
                                + "      margin: 0 0 20px;\n"
                                + "    }\n"
                                + "    .verification-code {\n"
                                + "      display: inline-block;\n"
                                + "      font-size: 24px;\n"
                                + "      color: #ff3333;\n"
                                + "      background-color: #f4f4f4;\n"
                                + "      padding: 10px 20px;\n"
                                + "      border-radius: 5px;\n"
                                + "      letter-spacing: 2px;\n"
                                + "      margin: 20px 0;\n"
                                + "    }\n"
                                + "    .email-footer {\n"
                                + "      text-align: center;\n"
                                + "      font-size: 14px;\n"
                                + "      color: #777777;\n"
                                + "      padding: 20px 0;\n"
                                + "      border-top: 1px solid #eeeeee;\n"
                                + "      border-left: 2px solid #ff3333;\n"
                                + "      border-right: 2px solid #ff3333;\n"
                                + "      border-bottom: 2px solid #ff3333;\n"
                                + "      border-bottom-left-radius: 10px;\n"
                                + "      border-bottom-right-radius: 10px;\n"
                                + "    }\n"
                                + "    .email-footer a {\n"
                                + "      color: #ff3333;\n"
                                + "      text-decoration: none;\n"
                                + "    }\n"
                                + "  </style>\n"
                                + "</head>\n"
                                + "<body>\n"
                                + "  <div class=\"email-container\">\n"
                                + "    <div class=\"email-header\">\n"
                                + "      <h1>Verification Code</h1>\n"
                                + "    </div>\n"
                                + "    <div class=\"email-body\">\n"
                                + "      <p>Hello User,</p>\n"
                                + "      <p>\n"
                                + "        Thank you for registering with us. Please use the following verification\n"
                                + "        code to complete your registration process:\n"
                                + "      </p>\n"
                                + "      <div class=\"verification-code\">" + otp + "</div>\n"
                                + "      <p>If you did not request this, please ignore this email.</p>\n"
                                + "    </div>\n"
                                + "    <div class=\"email-footer\">\n"
                                + "      <p>Best regards,</p>\n"
                                + "      <p>The Limupa Team</p>\n"
                                + "      <p><a href=\"#\">Contact Support</a></p>\n"
                                + "    </div>\n"
                                + "  </div>\n"
                                + "</body>";
                        Mail.sendMail(email, "Verify Your Account - Limupa", content);
                    }
                };
                mailSender.start();

                session.save(user);
                session.beginTransaction().commit();

                req.getSession().setAttribute("email", email);
                responseDTO.setOk(true);
            } else {
                responseDTO.setMsg("Email address already exists!");
            }

            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));

    }

}
