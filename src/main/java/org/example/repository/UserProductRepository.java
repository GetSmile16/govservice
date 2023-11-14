package org.example.repository;

import org.example.model.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
    List<UserProduct> findUserProductsByUserId(Long userId);

    UserProduct findUserProductByUserIdAndId(Long userId, Long recordId);

    List<UserProduct> findUserProductsByUserEmailOrderByDateOfCreatedDesc(String username);
}
