/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.CartDTO;
import dto.UserDTO;
import dto.WatchlistDTO;
import entity.Cart;
import entity.Product;
import entity.User;
import entity.Watchlist;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@WebServlet(name = "LoadWatchlist", urlPatterns = {"/LoadWatchlist"})
public class LoadWatchlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        HttpSession httpSession = req.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();
        ArrayList<WatchlistDTO> watchlistDTOList = new ArrayList<>();

        try {
            if (httpSession.getAttribute("user") != null) {
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                Criteria userCriteria = session.createCriteria(User.class);
                userCriteria.add(Restrictions.eq("id", userDTO.getId()));
                User user = (User) userCriteria.uniqueResult();

                Criteria watchlistCriteria = session.createCriteria(Watchlist.class);
                watchlistCriteria.add(Restrictions.eq("user", user));

                List<Watchlist> watchlistList = watchlistCriteria.list();

                for (Watchlist watchlist : watchlistList) {
                    WatchlistDTO watchlistDTO = new WatchlistDTO();
                    Product product = watchlist.getProduct();

                    product.setUser(null);
                    watchlistDTO.setProduct(product);
                    watchlistDTOList.add(watchlistDTO);
                }
            } else {
                if (httpSession.getAttribute("sessionWatchlist") != null) {

                    watchlistDTOList = (ArrayList<WatchlistDTO>) httpSession.getAttribute("sessionWatchlist");

                    for (WatchlistDTO watchlistDTO : watchlistDTOList) {
                        watchlistDTO.getProduct().setUser(null);
                    }
                } else {
                    System.out.println("empty");
                }
            }
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(watchlistDTOList));
    }
}
