/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.ResponseDTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "Contact", urlPatterns = {"/Contact"})
public class Contact extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new Gson();
        JsonObject reqObject = gson.fromJson(req.getReader(), JsonObject.class);
        String name = reqObject.get("name").getAsString();
        String email = reqObject.get("email").getAsString();
        String subject = reqObject.get("subject").getAsString();
        String message = reqObject.get("message").getAsString();

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
                        + "    }\n"
                        + "    .email-body p {\n"
                        + "      font-size: 16px;\n"
                        + "      color: #333333;\n"
                        + "      line-height: 1.5;\n"
                        + "      margin: 0 0 20px;\n"
                        + "    }\n"
                        + "    .email-body table {\n"
                        + "      border: 1px solid #ddd;\n"
                        + "      padding: 10px;\n"
                        + "      margin-bottom: 20px;\n"
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
                        + "      <h1>Contact Form Submission</h1>\n"
                        + "    </div>\n"
                        + "    <div class=\"email-body\">\n"
                        + "      <p>Hello User,</p>\n"
                        + "      <p>We have received a new message through the contact form.</p>\n"
                        + "      <h3>Submission Details:</h3>\n"
                        + "      <table>\n"
                        + "        <tr>\n"
                        + "          <td><strong>Name:</strong></td>\n"
                        + "          <td>" + name + "</td>\n"
                        + "        </tr>\n"
                        + "        <tr>\n"
                        + "          <td><strong>Email:</strong></td>\n"
                        + "          <td>" + email + "</td>\n"
                        + "        </tr>\n"
                        + "        <tr>\n"
                        + "          <td><strong>Subject:</strong></td>\n"
                        + "          <td>" + subject + "</td>\n"
                        + "        </tr>\n"
                        + "        <tr>\n"
                        + "          <td><strong>Message:</strong></td>\n"
                        + "          <td>" + message + "</td>\n"
                        + "        </tr>\n"
                        + "      </table>\n"
                        + "      <p>\n"
                        + "        You can reply directly to this email or reach out to the contact via the\n"
                        + "        information provided above.\n"
                        + "      </p>\n"
                        + "    </div>\n"
                        + "    <div class=\"email-footer\">\n"
                        + "      <p>Best regards,</p>\n"
                        + "      <p>The Auto Rent Hub Team</p>\n"
                        + "      <p><a href=\"#\">Contact Support</a></p>\n"
                        + "    </div>\n"
                        + "  </div>\n"
                        + "</body>";
                Mail.sendMail(email, "Contact Form Submission - Auto Rent Hub", content);
                Mail.sendMail("disura2005@gmail.com", "Contact Form Submission - " + name, content);
            }
        };
        mailSender.start();
        responseDTO.setOk(true);

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));

    }

}
