/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.ResponseDTO;
import dto.UserDTO;
import entity.Brand;
import entity.Category;
import entity.Color;
import entity.Model;
import entity.Product;
import entity.ProductCondition;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtill;
import model.Validate;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author SINGER
 */
@MultipartConfig
@WebServlet(name = "AddProduct", urlPatterns = {"/AddProduct"})
public class AddProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        Gson gson = new Gson();

        String categoryId = req.getParameter("category");
        String brandId = req.getParameter("brand");
        String modelId = req.getParameter("model");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String conditionId = req.getParameter("condition");
        String colorId = req.getParameter("color");
        String price = req.getParameter("price");
        String shipping = req.getParameter("shipping");
        String qty = req.getParameter("qty");

        Part image1 = req.getPart("image_1");
        Part image2 = req.getPart("image_2");
        Part image3 = req.getPart("image_3");

        if (categoryId.equals("0")) {
            responseDTO.setMsg("Please select category!");
        } else if (!Validate.isInteger(categoryId)) {
            responseDTO.setMsg("Invalid category!");
        } else if (brandId.equals("0")) {
            responseDTO.setMsg("Please select brand!");
        } else if (!Validate.isInteger(brandId)) {
            responseDTO.setMsg("Invalid brand!");
        } else if (modelId.equals("0")) {
            responseDTO.setMsg("Please select model!");
        } else if (!Validate.isInteger(modelId)) {
            responseDTO.setMsg("Invalid model!");
        } else if (title.isEmpty()) {
            responseDTO.setMsg("Please enter title!");
        } else if (description.isEmpty()) {
            responseDTO.setMsg("Please enter description!");
        } else if (colorId.equals("0")) {
            responseDTO.setMsg("Please select colour!");
        } else if (!Validate.isInteger(colorId)) {
            responseDTO.setMsg("Invalid color!");
        } else if (conditionId.equals("0")) {
            responseDTO.setMsg("Please select condition!");
        } else if (!Validate.isInteger(conditionId)) {
            responseDTO.setMsg("Invalid condition!");
        } else if (price.isEmpty()) {
            responseDTO.setMsg("Please enter price!");
        } else if (!Validate.isDouble(price)) {
            responseDTO.setMsg("Invalid price!");
        } else if (Double.parseDouble(price) <= 0) {
            responseDTO.setMsg("Price must be greater than 0!");
        } else if (shipping.isEmpty()) {
            responseDTO.setMsg("Please enter shipping fee!");
        } else if (!Validate.isDouble(shipping)) {
            responseDTO.setMsg("Invalid shipping fee!");
        } else if (Double.parseDouble(shipping) <= 0) {
            responseDTO.setMsg("Shipping fee must be greater than 0!");
        } else if (qty.isEmpty()) {
            responseDTO.setMsg("Please enter quantity!");
        } else if (!Validate.isInteger(qty)) {
            responseDTO.setMsg("Invalid Quantity!");
        } else if (Integer.parseInt(qty) <= 0) {
            responseDTO.setMsg("Quantity must be greater than 0!");
        } else if (image1.getSubmittedFileName() == null) {
            responseDTO.setMsg("Please select image 1");
        } else if (image2.getSubmittedFileName() == null) {
            responseDTO.setMsg("Please select image 2");
        } else if (image3.getSubmittedFileName() == null) {
            responseDTO.setMsg("Please select image 3");
        } else {
            Session session = HibernateUtill.getSessionFactory().openSession();

            try {
                Category category = (Category) session.get(Category.class, Integer.valueOf(categoryId));

                if (category == null) {
                    responseDTO.setMsg("Please select a valid category!");
                } else {
                    Brand brand = (Brand) session.get(Brand.class, Integer.valueOf(brandId));

                    if (brand == null) {
                        responseDTO.setMsg("Please select a valid brand!");
                    } else {
                        if (brand.getCategory().getId() != category.getId()) {
                            responseDTO.setMsg("Please select a valid brand!");
                        } else {
                            Model model = (Model) session.get(Model.class, Integer.valueOf(modelId));

                            if (model == null) {
                                responseDTO.setMsg("Please select a valid model!");
                            } else {
                                if (model.getBrand().getId() != brand.getId()) {
                                    responseDTO.setMsg("Please select a valid model!");
                                } else {
                                    ProductCondition condition = (ProductCondition) session.get(ProductCondition.class, Integer.valueOf(conditionId));

                                    if (condition == null) {
                                        responseDTO.setMsg("Please select a valid condition!");
                                    } else {
                                        Color color = (Color) session.get(Color.class, Integer.valueOf(colorId));

                                        if (color == null) {
                                            responseDTO.setMsg("Please select a valid color!");
                                        } else {
                                            Product product = new Product();
                                            product.setModel(model);
                                            product.setTitle(title);
                                            product.setDescription(description);
                                            product.setProductCondition(condition);
                                            product.setColor(color);
                                            product.setPrice(Double.parseDouble(price));
                                            product.setShipping(Double.parseDouble(shipping));
                                            product.setQty(Integer.parseInt(qty));
                                            product.setDate_time(new Date());
                                            product.setStatus(1);

                                            UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
                                            Criteria criteria = session.createCriteria(User.class);
                                            criteria.add(Restrictions.eq("id", userDTO.getId()));
                                            User user = (User) criteria.uniqueResult();
                                            product.setUser(user);

                                            int productID = (int) session.save(product);
                                            session.beginTransaction().commit();

                                            String applicationPath = req.getServletContext().getRealPath("");
                                            String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
                                            File folder = new File(newApplicationPath + "//images//product//" + productID);
                                            folder.mkdir();

                                            File imageFile_1 = new File(folder, productID + "image1.png");
                                            InputStream inputStreamImage_1 = image1.getInputStream();
                                            Files.copy(inputStreamImage_1, imageFile_1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                            File imageFile_2 = new File(folder, productID + "image2.png");
                                            InputStream inputStreamImage_2 = image2.getInputStream();
                                            Files.copy(inputStreamImage_2, imageFile_2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                            File imageFile_3 = new File(folder, productID + "image3.png");
                                            InputStream inputStreamImage_3 = image3.getInputStream();
                                            Files.copy(inputStreamImage_3, imageFile_3.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                            responseDTO.setOk(true);
                                            responseDTO.setMsg("Your new product was added!");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException | NumberFormatException | HibernateException e) {
                System.out.println(e.getMessage());
                responseDTO.setMsg("Unable to process request!");
            }
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseDTO));
    }

}
