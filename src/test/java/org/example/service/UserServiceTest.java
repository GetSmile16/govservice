package org.example.service;

import org.checkerframework.checker.units.qual.A;
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
import org.example.repository.ProductRepository;
import org.example.repository.UserProductRepository;
import org.example.repository.UserRepository;
import org.example.util.EmailUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProductRepository userProductRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MessageSource messageSource;
    @Mock
    private EmailUtil emailUtil;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    private final static String USERNAME = "test@example.com";
    private final static Long ID = 1L;
    private final static String PRODUCT_NAME = "Test product";

    @Test
    void createUser_userIsNotExist_savesUser() {
        UserInfoDto userInfo = new UserInfoDto("unexist@example.com", "Test", "Test", "Test", "password");

        when(userRepository.findByEmail(userInfo.getEmail())).thenReturn(null);

        User savedUser = UserMapper.INSTANCE.userInfoToUserWithId(userInfo, 1L);

        when(passwordEncoder.encode(userInfo.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserIdDto actual = userService.createUser(userInfo);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());

        verify(userRepository, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(userInfo.getEmail(), capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(userInfo.getFirstName(), capturedUser.getFirstName());
        assertEquals(userInfo.getLastName(), capturedUser.getLastName());
        assertEquals(userInfo.getPatronymic(), capturedUser.getPatronymic());
    }


    @Test
    void createUser_userIsExist_throwsException() {
        UserInfoDto userInfo = new UserInfoDto("exist@example.com", "Test", "Test", "Test", "password");

        when(userRepository.findByEmail(userInfo.getEmail())).thenReturn(new User());

        assertThrows(UserIsExist.class, () -> userService.createUser(userInfo));

        verify(userRepository, never()).save(new User());
    }

    @Test
    void provideSeasonProduct_validProduct_savesProduct() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                new SeasonProduct(ID, 10)
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(product);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(null);

        User expectedUser = new User(ID, USERNAME);

        ArrayList<UserProduct> userProducts = new ArrayList<>();
        userProducts.add(new UserProduct(
                ID,
                product
        ));

        expectedUser.setProducts(userProducts);

        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserProductDto actual = userService.provideSeasonProduct(USERNAME, ID);

        assertNotNull(actual);
        assertEquals(ID, actual.getId());
        assertEquals(PRODUCT_NAME, actual.getProductName());
        assertTrue(actual.getSeason());

        verify(userRepository, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        UserProduct userProduct = capturedUser.getProducts().get(0);

        assertEquals(product, userProduct.getProduct());
        assertEquals(ID, userProduct.getProduct().getId());
        assertEquals(9, userProduct.getProduct().getSeasonProduct().getRemainingCount());
        //assertNotNull(userProduct.getDateOfCreated()); TODO
    }

    @Test
    void provideSeasonProduct_isDone_throwsException() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                new SeasonProduct()
        );

        UserProduct userProduct = new UserProduct(
                ID,
                product
        );

        List<UserProduct> userProducts = new ArrayList<>();
        userProducts.add(userProduct);

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(product);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(userProducts);

        assertThrows(ProductIsDone.class, () -> userService.provideSeasonProduct(USERNAME, ID));

        verify(userRepository, never()).save(new User());
    }

    @Test
    void provideSeasonProduct_isNotFound_throwsException() {
        User testUser = new User(ID, USERNAME);

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(null);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(null);

        assertThrows(ProductNotFound.class, () -> userService.provideSeasonProduct(USERNAME, ID));

        verify(userRepository, never()).save(new User());
    }

    @Test
    void provideSeasonProduct_isNotSeason_throwsException() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                null
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(product);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(null);

        assertThrows(ProductNotSeason.class, () -> userService.provideSeasonProduct(USERNAME, ID));

        verify(userRepository, never()).save(new User());
    }

    @Test
    void provideSeasonProduct_isOver_throwsException() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                new SeasonProduct(ID, 0)
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(product);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(null);


        assertThrows(ProductIsOver.class, () -> userService.provideSeasonProduct(USERNAME, ID));

        verify(userRepository, never()).save(new User());
    }

    @Test
    void provideProduct_validProduct_savesProduct() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                null
        );

        UserProduct userProduct = new UserProduct(
                ID,
                product
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(product);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(null);

        User expectedUser = new User(ID, USERNAME);

        ArrayList<UserProduct> userProducts = new ArrayList<>();
        userProducts.add(userProduct);

        expectedUser.setProducts(userProducts);

        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserProductDto actual = userService.provideProduct(USERNAME, ID);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(PRODUCT_NAME, actual.getProductName());
        assertFalse(actual.getSeason());

        verify(userRepository, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        userProduct = capturedUser.getProducts().get(0);

        assertEquals(product, userProduct.getProduct());
        assertEquals(ID, userProduct.getProduct().getId());
        //assertNotNull(userProduct.getDateOfCreated()); TODO
    }

    @Test
    void provideProduct_isSeason_throwsException() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                new SeasonProduct()
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(productRepository.findProductById(any(Long.class))).thenReturn(product);
        when(userProductRepository.findUserProductsByUserId(any(Long.class))).thenReturn(null);

        assertThrows(ProductIsSeason.class, () -> userService.provideProduct(USERNAME, ID));

        verify(userRepository, never()).save(new User());
    }

    @Test
    void getAllProductsByUserOrderByDateDesc() {
        List<UserProduct> products = new ArrayList<>();
        products.add(
                new UserProduct(
                        2L,
                        new Product(
                                2L,
                                "Test2"
                        ),
                        LocalDateTime.MAX
                )
        );

        products.add(
                new UserProduct(
                        1L,
                        new Product(
                                1L,
                                "Test1",
                                new SeasonProduct(
                                        1L,
                                        10
                                )
                        ),
                        LocalDateTime.MIN
                )
        );

        when(userProductRepository.findUserProductsByUserEmailOrderByDateOfCreatedDesc(any(String.class))).thenReturn(products);

        List<DoneProductDto> actual = userService.getAllProductsByUserOrderByDateDesc(USERNAME);

        inOrder(userProductRepository).verify(userProductRepository, times(1)).findUserProductsByUserEmailOrderByDateOfCreatedDesc(any(String.class));

        assertEquals(2, actual.size());
        assertEquals(LocalDateTime.MAX, actual.get(0).getDateOfCreated());
        assertEquals(LocalDateTime.MIN, actual.get(1).getDateOfCreated());
    }

    @Test
    void getProductByUser_validProduct_getProduct() {
        User testUser = new User(ID, USERNAME);

        Product product = new Product(
                ID,
                PRODUCT_NAME,
                new SeasonProduct(ID, 10)
        );

        UserProduct userProduct = new UserProduct(
                ID,
                testUser,
                product
        );

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(userProductRepository.findUserProductByUserIdAndId(any(Long.class), any(Long.class))).thenReturn(userProduct);

        UserProductDto actual = userService.getProductByUser(USERNAME, ID);

        assertNotNull(actual);
        assertEquals(ID, actual.getId());
        assertEquals(PRODUCT_NAME, actual.getProductName());
    }

    @Test
    void getProductByUser_notFoundProduct_throwsException() {
        User testUser = new User(ID, USERNAME);

        when(userRepository.findByEmail(any(String.class))).thenReturn(testUser);
        when(userProductRepository.findUserProductByUserIdAndId(any(Long.class), any(Long.class))).thenReturn(null);

        assertThrows(ProductNotFound.class, () -> userService.getProductByUser(USERNAME, ID));
    }
}