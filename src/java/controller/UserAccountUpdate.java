/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Mail;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "UserAccountUpdate", urlPatterns = {"/UserAccountUpdate"})
public class UserAccountUpdate extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserDTO reqUser = gson.fromJson(req.getReader(), UserDTO.class);

        String f_name = reqUser.getF_name();
        String l_name = reqUser.getL_name();
        String email = reqUser.getEmail();

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
        } else {
            if (req.getSession().getAttribute("user") != null) {
                UserDTO sessionUser = (UserDTO) req.getSession().getAttribute("user");
                Session session = HibernateUtil.getSessionFactory().openSession();

                try {
                    Criteria criteria = session.createCriteria(User.class);
                    criteria.add(Restrictions.eq("id", sessionUser.getId()));

                    if (!criteria.list().isEmpty()) {
                        User user = (User) criteria.list().get(0);

                        user.setF_name(f_name);
                        user.setL_name(l_name);

                        if (!user.getEmail().equals(email)) {
                            Criteria checkEmailCriteria = session.createCriteria(User.class);
                            criteria.add(Restrictions.eq("email", email));

                            if (!checkEmailCriteria.list().isEmpty()) {
                                int otp = (int) (Math.random() * 1000000);

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
                                                + "        Please use the following verification\n"
                                                + "        code to verify your email\n"
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
                                        Mail.sendMail(email, "Verify Your Email - Limupa", content);
                                    }
                                };
                                mailSender.start();

                                HttpSession httpSession = req.getSession();
                                httpSession.removeAttribute("user");
                                httpSession.setAttribute("id", user.getId());
                                httpSession.setAttribute("email", email);
                                httpSession.setAttribute("otp", otp);

                                Cookie cookie = new Cookie("JSESSIONID", httpSession.getId());
                                cookie.setMaxAge(60 * 2);

                                resp.addCookie(cookie);

                                responseDTO.setOk(true);
                                responseDTO.setMsg("Verify your email");
                            } else {
                                responseDTO.setMsg("Email address already exists!");
                            }
                        } else {
                            session.update(user);
                            session.beginTransaction().commit();

                            UserDTO userDTO = new UserDTO();
                            userDTO.setId(user.getId());
                            userDTO.setF_name(user.getF_name());
                            userDTO.setL_name(user.getL_name());
                            userDTO.setEmail(email);
                            userDTO.setPassword(null);

                            req.getSession(true).setAttribute("user", userDTO);
                            responseDTO.setOk(true);
                        }
                    }
                } catch (HibernateException e) {
                    System.out.println(e.getMessage());
                    responseDTO.setMsg("Unable to process request!");
                }
                session.close();
            } else {
                responseDTO.setMsg("Sign in unavailable please sign in again!");
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
