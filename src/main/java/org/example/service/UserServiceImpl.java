package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.product.DoneProductDto;
import org.example.dto.product.UserProductDto;
import org.example.dto.user.UserIdDto;
import org.example.dto.user.UserInfoDto;
import org.example.exception.product.*;
import org.example.exception.user.UserIsExist;
import org.example.mapper.UserMapper;
import org.example.model.Product;
import org.example.model.SeasonProduct;
import org.example.model.User;
import org.example.model.UserProduct;
import org.example.model.enums.Role;
import org.example.repository.ProductRepository;
import org.example.repository.UserProductRepository;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private UserProductRepository userProductRepository;
    private PasswordEncoder encoder;
    private MessageSource messageSource;

    public UserServiceImpl() {
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ProductRepository productRepository,
                           UserProductRepository userProductRepository,
                           PasswordEncoder passwordEncoder,
                           MessageSource messageSource) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.encoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    @Override
    public UserIdDto createUser(UserInfoDto userInfo) {
        if (userRepository.findByEmail(userInfo.getEmail()) != null) {
            String message = messageSource.getMessage("user.is_exist", null, Locale.getDefault());
            log.warn(message);
            throw new UserIsExist(message);
        }

        String encodePwd = encoder.encode(userInfo.getPassword());

        userInfo.setPassword(encodePwd);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        User user = UserMapper.INSTANCE.userInfoToUser(userInfo, roles);

        user = userRepository.save(user);
        log.info("User with username \"" + user.getUsername() + "\" created");

        return new UserIdDto(user.getId());
    }

    @Override
    @Transactional
    public UserProductDto provideSeasonProduct(String username, Long productId) {
        User user = userRepository.findByEmail(username);
        Product product = getProduct(user, productId);

        SeasonProduct seasonProduct = product.getSeasonProduct();
        if (seasonProduct == null) {
            String message = messageSource.getMessage("product.is_not_season", null, Locale.getDefault());
            log.warn(message);
            throw new ProductNotSeason(message);
        }

        if (seasonProduct.getRemainingCount() == 0) {
            String message = messageSource.getMessage("product.is_over", null, Locale.getDefault());
            log.warn(message);
            throw new ProductIsOver(message);
        }

        seasonProduct.setRemainingCount(
                seasonProduct.getRemainingCount() - 1
        );

        UserProduct savedProduct = saveDoneProduct(user, product);

        return new UserProductDto(
                savedProduct.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                product.getProductName(),
                savedProduct.getDateOfCreated(),
                true
        );
    }

    @Override
    @Transactional
    public UserProductDto provideProduct(String username, Long productId) {
        User user = userRepository.findByEmail(username);
        Product product = getProduct(user, productId);

        SeasonProduct seasonProduct = product.getSeasonProduct();
        if (seasonProduct != null) {
            String message = messageSource.getMessage("product.is_season", null, Locale.getDefault());
            log.warn(message);
            throw new ProductIsSeason(message);
        }

        UserProduct savedProduct = saveDoneProduct(user, product);

        return new UserProductDto(
                savedProduct.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                product.getProductName(),
                savedProduct.getDateOfCreated(),
                false
        );
    }

    private UserProduct saveDoneProduct(User user, Product product) {
        List<UserProduct> products = user.getProducts();
        products.add(new UserProduct(
                user,
                product
        ));
        user.setProducts(products);

        User saved = userRepository.save(user);
        products = saved.getProducts();

        return products.get(products.size() - 1);
    }

    private Product getProduct(User user, Long productId) {
        if (userProductRepository.findUserProductByUserId(user.getId()) != null) {
            String message = messageSource.getMessage("product.is_done", null, Locale.getDefault());
            log.warn(message);
            throw new ProductIsDone(message);
        }

        Product product = productRepository.findProductById(productId);

        if (product == null) {
            String message = messageSource.getMessage("product.not_found", null, Locale.getDefault());
            log.warn(message);
            throw new ProductNotFound(message);
        }

        return product;
    }

    @Override
    @Transactional
    public List<DoneProductDto> getAllProductsByUserOrderByDateDesc(String username) {
        List<UserProduct> products = userProductRepository.findUserProductsByUserEmailOrderByDateOfCreatedDesc(username);
        return products.stream()
                .map(o ->
                        new DoneProductDto(
                                o.getId(),
                                o.getProduct().getProductName(),
                                o.getDateOfCreated(),
                                o.getProduct().getSeasonProduct() != null
                        )
                ).toList();
    }

    @Override
    @Transactional
    public UserProductDto getProductByUser(String username, Long id) {
        User user = userRepository.findByEmail(username);
        UserProduct product = userProductRepository.findUserProductByUserIdAndId(user.getId(), id);

        if (product == null) {
            String message = messageSource.getMessage("product.not_found", null, Locale.getDefault());
            log.warn(message);
            throw new ProductNotFound(message);
        }

        return new UserProductDto(
                id,
                username,
                user.getFirstName(),
                user.getUsername(),
                user.getPatronymic(),
                product.getProduct().getProductName(),
                product.getDateOfCreated(),
                product.getProduct().getSeasonProduct() != null
        );
    }
}
