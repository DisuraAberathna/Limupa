/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import dto.UserDTO;
import dto.WatchlistDTO;
import entity.Product;
import entity.User;
import entity.Watchlist;
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
@WebServlet(name = "RemoveFromWatchlist", urlPatterns = {"/RemoveFromWatchlist"})
public class RemoveFromWatchlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        ResponseDTO responseDTO = new ResponseDTO();

        String id = req.getParameter("id");

        if (!Validate.isInteger(id)) {
            responseDTO.setMsg("Invalid product ID! Please try again.");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            try {
                int productId = Integer.parseInt(id);
                Product product = (Product) session.get(Product.class, productId);

                if (product != null) {
                    HttpSession httpSession = req.getSession();

                    if (req.getSession().getAttribute("user") != null) {
                        UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

                        Criteria userCriteria = session.createCriteria(User.class);
                        userCriteria.add(Restrictions.eq("id", userDTO.getId()));
                        User user = (User) userCriteria.uniqueResult();

                        Criteria watchlistCriteria = session.createCriteria(Watchlist.class);
                        watchlistCriteria.add(Restrictions.eq("user", user));
                        watchlistCriteria.add(Restrictions.eq("product", product));

                        Watchlist watchlistItem = (Watchlist) watchlistCriteria.uniqueResult();
                        if (watchlistItem != null) {
                            session.delete(watchlistItem);
                            transaction.commit();
                            responseDTO.setOk(true);
                            responseDTO.setMsg("Product removed from watchlist!");
                        } else {
                            responseDTO.setMsg("Product not found in watchlist.");
                        }
                    } else {
                        if (httpSession.getAttribute("sessionWatchlist") != null) {
                            ArrayList<WatchlistDTO> sessionWatchlist = (ArrayList<WatchlistDTO>) httpSession.getAttribute("sessionWatchlist");

                            WatchlistDTO foundWatchlistDTO = null;
                            for (WatchlistDTO watchlistDTO : sessionWatchlist) {
                                if (watchlistDTO.getProduct().getId() == product.getId()) {
                                    foundWatchlistDTO = watchlistDTO;
                                    break;
                                }
                            }

                            if (foundWatchlistDTO != null) {
                                sessionWatchlist.remove(foundWatchlistDTO);
                                responseDTO.setOk(true);
                                responseDTO.setMsg("Product removed from watchlist!");
                            } else {
                                responseDTO.setMsg("Product not found in watchlist.");
                            }
                        } else {
                            responseDTO.setMsg("Your watchlist is empty.");
                        }
                    }
                } else {
                    responseDTO.setMsg("Product not found!");
                }
            } catch (HibernateException | NumberFormatException e) {
                System.out.println(e.getMessage());
                responseDTO.setMsg("Unable to process the request.");
            }
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
