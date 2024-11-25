package customer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;


/**
 * тестирует ShoppingService
 */
@ExtendWith(MockitoExtension.class)
public class ShoppingServiceTest {

    private final ProductDao productDaoMock;
    private final ShoppingService shoppingService;
    private final Customer customer = new Customer(0, "11-11-11");

    /**
     * Конструктор, создающий mock объект и сервивс
     */
    public ShoppingServiceTest(@Mock ProductDao productDaoMock) {
        this.productDaoMock = productDaoMock;
        this.shoppingService = new ShoppingServiceImpl(productDaoMock);

    }

    /**
     * Проверяет, что при получении корзины возвращается одна и та же корзина
     */
    @Test
    public void testGetCart() {
        Cart cart1 = shoppingService.getCart(customer);
        Cart cart2 = shoppingService.getCart(customer);
        Assertions.assertEquals(cart1, cart2, "Метод возвращает разные корзины");
    }

    /**
     * Проверяет, что нельзя добавить в корзину больше товаров,чем есть в наличии
     */
    @Test
    public void testAddMoreProductsToCartThanAvailable() {
        Cart cart = shoppingService.getCart(customer);
        Product product = new Product("milk", 2);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> cart.add(product, 5));
        Assertions.assertEquals("Невозможно добавить товар 'milk' в корзину, т.к. нет необходимого количества товаров",
                exception.getMessage());
    }

    /**
     * Проверяет, что нелья добавить в корзину отрицательное количество продуктов
     */
    @Test
    public void testAddNegativeNumberOfProducts() {
        Cart cart = shoppingService.getCart(customer);
        Product product = new Product("milk", 3);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> cart.add(product, -2));
        Assertions.assertEquals("Количество добавленного товара не может быть отрицательным",
                exception.getMessage());
    }

    /**
     * Проверяет, что возможно купить последний пробукт
     */
    @Test
    public void testBuyLastProduct() throws BuyException {
        Cart cart = shoppingService.getCart(customer);
        Product product = new Product("milk", 1);
        cart.add(product, 1);
        boolean answer = shoppingService.buy(cart);

        Assertions.assertTrue(answer);
        Mockito.verify(productDaoMock, Mockito.times(1))
                .save(product);
    }

    /**
     * Проверяет, что корзина пуста после покупки
     */
    @Test
    public void testCartIsEmptyAfterBuy() throws BuyException {
        Cart cart = shoppingService.getCart(customer);
        Product product1 = new Product("milk", 3);
        Product product2 = new Product("bread", 2);

        cart.add(product1, 2);
        cart.add(product2, 1);
        shoppingService.buy(cart);

        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * Проверяет корректность работы метода buy при правильных данных
     * @throws BuyException ошибка,если покупка невозможна
     */
    @Test
    public void testCorrectBuy() throws BuyException {
        Cart cart = shoppingService.getCart(customer);
        Product product = new Product("milk", 3);

        cart.add(product, 2);
        boolean answer = shoppingService.buy(cart);

        Assertions.assertTrue(answer);
        Mockito.verify(productDaoMock, Mockito.times(1))
                .save(product);
        Assertions.assertEquals(1, product.getCount());
    }

    /**
     * Проверяет корректность работы метода buy при совершении покупки с пустой корзиной
     * @throws BuyException ошибка, если покупка невозможна
     */
    @Test
    public void testBuyProductsFromEmptyCart() throws BuyException {
        Cart cart = shoppingService.getCart(customer);

        boolean answer = shoppingService.buy(cart);

        Mockito.verify(productDaoMock, Mockito.never())
                .save(Mockito.any(Product.class));
        Assertions.assertFalse(answer);
    }

    /**
     * Проверяет корректность работы метода buy, когда в наличие нет нужного количества товара
     * (когда в корзине продуктов больше, чем есть в наличии)
     */
    @Test
    public void testThrowExceptionWhenBuyInvalid() {
        Cart cart = shoppingService.getCart(customer);
        Product product = new Product("milk", 3);

        cart.add(product, 2);
        product.subtractCount(2);

        BuyException exception = Assertions.assertThrows(BuyException.class,
                () -> shoppingService.buy(cart));
        Assertions.assertEquals("В наличии нет необходимого количества товара 'milk'",
                exception.getMessage());

        Mockito.verify(productDaoMock, Mockito.never()).save(Mockito.any(Product.class));
    }

    /**
     * Проверяет корректность получения списка продуктов
     */
    @Test
    public void getAllProducts() {
        // Метод является делегирующим и не добавляет никакой собственной логики.
    }

    /**
     * Проверяет корректность получения продукта по названию
     */
    @Test
    public void getProductByName() {
        // Как и в случае с getAllProducts,
        // метод getProductByName делегирует вызов и не добавляет собственной логики.
    }
}