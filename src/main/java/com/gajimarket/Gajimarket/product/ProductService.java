package com.gajimarket.Gajimarket.product;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final DataSource dataSource;

    @Autowired
    public ProductService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Product> searchProductsByTitle(String title) {
        String sql = "SELECT * FROM Product WHERE title LIKE ?";
        List<Product> productList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductIdx(resultSet.getInt("product_idx"));
                product.setTitle(resultSet.getString("title"));
                product.setContent(resultSet.getString("content"));
                product.setPrice(resultSet.getInt("price"));
                product.setLocation(resultSet.getString("location"));
                product.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

                productList.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error during product search", e);
        }

        return productList;
    }
}

