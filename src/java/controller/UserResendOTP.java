/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "UserResendOTP", urlPatterns = {"/UserResendOTP"})
public class UserResendOTP extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();
        Gson gson = new Gson();

        int otp = (int) (Math.random() * 1000000);

        if (req.getSession().getAttribute("id") != null && req.getSession().getAttribute("email") != null && req.getSession().getAttribute("otp") != null) {
            String id = req.getSession().getAttribute("id").toString();
            String email = req.getSession().getAttribute("email").toString();

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
            httpSession.removeAttribute("id");
            httpSession.removeAttribute("email");
            httpSession.removeAttribute("otp");
            httpSession.setAttribute("id", id);
            httpSession.setAttribute("email", email);
            httpSession.setAttribute("otp", otp);

            Cookie cookie = new Cookie("JSESSIONID", httpSession.getId());
            cookie.setMaxAge(60 * 2);

            resp.addCookie(cookie);
            responseDTO.setOk(true);
        } else if (req.getSession().getAttribute("id") != null && req.getSession().getAttribute("password") != null && req.getSession().getAttribute("otp") != null) {
            String id = req.getSession().getAttribute("id").toString();
            String password = req.getSession().getAttribute("password").toString();

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("id", Integer.valueOf(id)));

            if (!criteria.list().isEmpty()) {
                User user = (User) criteria.list().get(0);
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
                                + "        code to verify you for update password\n"
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
                        Mail.sendMail(user.getEmail(), "Verify It's You Update Password - Limupa", content);
                    }
                };
                mailSender.start();

                HttpSession httpSession = req.getSession();
                httpSession.removeAttribute("id");
                httpSession.removeAttribute("password");
                httpSession.removeAttribute("otp");
                httpSession.setAttribute("id", user.getId());
                httpSession.setAttribute("password", password);
                httpSession.setAttribute("otp", otp);

                Cookie cookie = new Cookie("JSESSIONID", httpSession.getId());
                cookie.setMaxAge(60 * 2);

                resp.addCookie(cookie);

                session.close();
                responseDTO.setOk(true);
            }
        } else if (req.getSession().getAttribute("email") != null) {
            String email = req.getSession().getAttribute("email").toString();

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
            responseDTO.setOk(true);
        } else {
            responseDTO.setMsg("Verification unavailable please sign in");
        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
