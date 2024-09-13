/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.CartDTO;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        ResponseDTO responseDTO = new ResponseDTO();

        String id = req.getParameter("id");
        String qty = req.getParameter("qty");

        if (!Validate.isInteger(id)) {
            responseDTO.setMsg("Product not found! Please try again later.");
        } else if (!Validate.isInteger(qty)) {
            responseDTO.setMsg("Invalid quantity! Please add a valid quantity.");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            try {
                int productId = Integer.parseInt(id);
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {
                    responseDTO.setMsg("Please add a quantity greater than 0!");
                } else {
                    Product product = (Product) session.get(Product.class, productId);

                    if (product != null) {
                        if (product.getQty() > 0) {
                            if (req.getSession().getAttribute("user") != null) {
                                UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

                                if (product.getUser().getId() != userDTO.getId()) {
                                    Criteria userCriteria = session.createCriteria(User.class);
                                    userCriteria.add(Restrictions.eq("id", userDTO.getId()));
                                    User user = (User) userCriteria.uniqueResult();

                                    Criteria cartCriteria = session.createCriteria(Cart.class);
                                    cartCriteria.add(Restrictions.eq("user", user));
                                    cartCriteria.add(Restrictions.eq("product", product));

                                    if (cartCriteria.list().isEmpty()) {
                                        if (productQty <= product.getQty()) {
                                            Cart cart = new Cart();
                                            cart.setProduct(product);
                                            cart.setQty(productQty);
                                            cart.setUser(user);

                                            session.save(cart);
                                            transaction.commit();

                                            responseDTO.setOk(true);
                                        } else {
                                            responseDTO.setMsg("Quantity can't be greater than " + product.getQty());
                                        }
                                    } else {
                                        Cart cartItem = (Cart) cartCriteria.uniqueResult();
                                        if ((cartItem.getQty() + productQty) <= product.getQty()) {
                                            cartItem.setQty(cartItem.getQty() + productQty);
                                            transaction.commit();
                                            responseDTO.setOk(true);
                                        } else {
                                            responseDTO.setMsg("Can't update your cart! Quantity is unavailable.");
                                        }
                                    }
                                } else {
                                    responseDTO.setMsg("It's a your product! can't add to cart.");
                                }
                            } else {
                                HttpSession httpSession = req.getSession();

                                if (httpSession.getAttribute("sessionCart") != null) {
                                    ArrayList<CartDTO> sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                                    CartDTO foundCartDTO = null;

                                    for (CartDTO cartDTO : sessionCart) {
                                        if (cartDTO.getProduct().getId() == product.getId()) {
                                            foundCartDTO = cartDTO;
                                            break;
                                        }
                                    }
                                    if (foundCartDTO != null) {
                                        if ((foundCartDTO.getQty() + productQty) <= product.getQty()) {
                                            foundCartDTO.setQty(foundCartDTO.getQty() + productQty);
                                            responseDTO.setOk(true);
                                        } else {
                                            responseDTO.setMsg("Can't update your cart! Quantity is unavailable.");
                                        }
                                    } else {
                                        if (productQty <= product.getQty()) {
                                            CartDTO cartDTO = new CartDTO();
                                            cartDTO.setProduct(product);
                                            cartDTO.setQty(productQty);
                                            sessionCart.add(cartDTO);

                                            responseDTO.setOk(true);
                                        } else {
                                            responseDTO.setMsg("Quantity can't be greater than " + product.getQty());
                                        }
                                    }
                                } else {
                                    if (productQty <= product.getQty()) {
                                        ArrayList<CartDTO> sessionCart = new ArrayList<>();

                                        CartDTO cartDTO = new CartDTO();
                                        cartDTO.setProduct(product);
                                        cartDTO.setQty(productQty);
                                        sessionCart.add(cartDTO);

                                        httpSession.setAttribute("sessionCart", sessionCart);

                                        responseDTO.setOk(true);
                                    } else {
                                        responseDTO.setMsg("Quantity can't be greater than " + product.getQty());
                                    }
                                }
                            }
                        } else {
                            responseDTO.setMsg("Out of stock product!");
                        }
                    } else {
                        responseDTO.setMsg("Product not found!");
                    }
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
