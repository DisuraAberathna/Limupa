/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import entity.OrderItem;
import entity.Orders;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@WebServlet(name = "SendMail", urlPatterns = {"/SendMail"})
public class SendMail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        ResponseDTO responseDTO = new ResponseDTO();
        String id = req.getParameter("id");

        if (!Validate.isInteger(id)) {
            responseDTO.setMsg("Invalid order!");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();

            try {
                int orderId = Integer.parseInt(id);

                Criteria orderCriteria = session.createCriteria(Orders.class);
                orderCriteria.add(Restrictions.eq("id", orderId));

                System.out.println(orderCriteria.list());
                if (!orderCriteria.list().isEmpty()) {
                    Orders order = (Orders) orderCriteria.list().get(0);

                    Criteria orderListCriteria = session.createCriteria(OrderItem.class);
                    orderListCriteria.add(Restrictions.eq("order", order));
                    List<OrderItem> orderItemList = orderListCriteria.list();

                    String mailContentStart = "  <head>\n"
                            + "    <style>\n"
                            + "      body {\n"
                            + "        font-family: Arial, sans-serif;\n"
                            + "        background-color: #f4f4f4;\n"
                            + "        margin: 0;\n"
                            + "        padding: 0;\n"
                            + "        color: #333;\n"
                            + "      }\n"
                            + "      .container {\n"
                            + "        width: 100%;\n"
                            + "        max-width: 600px;\n"
                            + "        margin: 20px auto;\n"
                            + "        background-color: #ffffff;\n"
                            + "        border: 1px solid #ddd;\n"
                            + "        padding: 20px;\n"
                            + "      }\n"
                            + "      h1 {\n"
                            + "        color: #333;\n"
                            + "      }\n"
                            + "      p {\n"
                            + "        line-height: 1.6;\n"
                            + "      }\n"
                            + "      .invoice-details {\n"
                            + "        width: 100%;\n"
                            + "        margin-top: 20px;\n"
                            + "        border-collapse: collapse;\n"
                            + "      }\n"
                            + "      .invoice-details th,\n"
                            + "      .invoice-details td {\n"
                            + "        padding: 10px;\n"
                            + "        border: 1px solid #ddd;\n"
                            + "      }\n"
                            + "      .invoice-details th {\n"
                            + "        background-color: #f8f8f8;\n"
                            + "      }\n"
                            + "      .text-start {\n"
                            + "         text-align: start;\n"
                            + "      }\n"
                            + "      .text-end {\n"
                            + "         text-align: end;\n"
                            + "      }\n"
                            + "      .text-center {\n"
                            + "         text-align: center;\n"
                            + "      }\n"
                            + "      .total {\n"
                            + "        font-weight: bold;\n"
                            + "        text-align: start;\n"
                            + "      }\n"
                            + "      .footer {\n"
                            + "        margin-top: 20px;\n"
                            + "        font-size: 12px;\n"
                            + "        color: #888;\n"
                            + "        text-align: center;\n"
                            + "      }\n"
                            + "    </style>\n"
                            + "  </head>\n"
                            + "  <body>\n"
                            + "    <div class=\"container\">\n"
                            + "      <h1>Invoice #" + orderId + "</h1>\n"
                            + "      <p>Dear " + order.getUser().getF_name() + " " + order.getUser().getL_name() + ",</p>\n"
                            + "      <p>Thank you for your purchase! Below is the summary of your order:</p>\n"
                            + "\n"
                            + "      <table class=\"invoice-details\">\n"
                            + "        <tr>\n"
                            + "          <th class=\"text-start\">Item</th>\n"
                            + "          <th class=\"text-center\">Quantity</th>\n"
                            + "          <th class=\"text-center\">Price</th>\n"
                            + "          <th class=\"text-center\">Total</th>\n"
                            + "        </tr>";

                    String mailContentMiddle = "";

                    double total = 0.00;
                    double shipping = 0.00;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");

                    for (OrderItem orderItem : orderItemList) {
                        int qty = orderItem.getQty();
                        double price = orderItem.getProduct().getPrice();

                        total += price * qty;
                        shipping += orderItem.getProduct().getShipping();

                        mailContentMiddle += "<tr>\n"
                                + "          <td>" + orderItem.getProduct().getTitle() + "</td>\n"
                                + "          <td class=\"text-center\">" + qty + "</td>\n"
                                + "          <td class=\"text-end\">LKR " + decimalFormat.format(price) + "</td>\n"
                                + "          <td class=\"text-end\">LKR " + decimalFormat.format(price * qty) + "</td>\n"
                                + "        </tr>";
                    }

                    String mailContentEnd = "<tr>\n"
                            + "          <td colspan=\"3\" class=\"total\">Subtotal:</td>\n"
                            + "          <td class=\"text-end\">LKR " + decimalFormat.format(total) + "</td>\n"
                            + "        </tr>\n"
                            + "        <tr>\n"
                            + "          <td colspan=\"3\" class=\"total\">Shipping Fee:</td>\n"
                            + "          <td class=\"text-end\">LKR " + decimalFormat.format(shipping) + "</td>\n"
                            + "        </tr>\n"
                            + "        <tr>\n"
                            + "          <td colspan=\"3\" class=\"total\">Total:</td>\n"
                            + "          <td class=\"text-end\">LKR " + decimalFormat.format(total + shipping) + "</td>\n"
                            + "        </tr>\n"
                            + "      </table>\n"
                            + "\n"
                            + "      <p>\n"
                            + "        If you have any questions or need further assistance, feel free to\n"
                            + "        contact us.\n"
                            + "      </p>\n"
                            + "      <p>Best regards,<br />Limupa&trade;</p>\n"
                            + "\n"
                            + "      <div class=\"footer\">\n"
                            + "        <p>&copy; 2024 Limupa&trade;. All rights reserved.</p>\n"
                            + "      </div>\n"
                            + "    </div>\n"
                            + "  </body>";

                    String content = mailContentStart + mailContentMiddle + mailContentEnd;

                    Thread mailSender = new Thread() {
                        @Override
                        public void run() {
                            Mail.sendMail(order.getUser().getEmail(), "Invoice - Limupa", content);
                        }
                    };
                    mailSender.start();

                    responseDTO.setOk(true);
                } else {
                    responseDTO.setMsg("Order not found!");
                }
            } catch (NumberFormatException | HibernateException e) {
                System.out.println(e.getMessage());
                responseDTO.setMsg("Unable to process request!");
            }
            session.close();
        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }
}
