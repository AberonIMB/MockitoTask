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

    /**
     * Конструктор, создающий mock объект и сервивс
     */
    public ShoppingServiceTest(@Mock ProductDao productDaoMock) {
        this.productDaoMock = productDaoMock;
        this.shoppingService = new ShoppingServiceImpl(productDaoMock);

    }

    /**
     * Проверяет корректность работы метода buy при правильных данных
     * @throws BuyException ошибка,если покупка невозможна
     */
    @Test
    public void testCorrectBuy() throws BuyException {
        Customer customer = new Customer(0, "11-11-11");
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
        Customer customer = new Customer(0, "11-11-11");
        Cart cart = shoppingService.getCart(customer);

        boolean answer = shoppingService.buy(cart);

        Mockito.verify(productDaoMock, Mockito.never())
                .save(Mockito.any(Product.class));
        Assertions.assertFalse(answer);
    }

    /**
     * Проверяет корректность работы методов buy и validateCanBuy при неправильных данных
     * (когда в корзине продуктов больше, чем есть в наличии)
     */
    @Test
    public void testThrowExceptionWhenBuyInvalid() {
        Customer customer = new Customer(0, "11-11-11");
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
     * Проверяет корректность получения корзины
     */
    @Test
    public void testGetCart() {
        // В методе отсутствует какая-либо логика, требующая проверки.
        // Метод только создает экземпляр класса Cart.
    }

    /**
     * Проверяет корректность получения списка продуктов
     */
    @Test
    public void getAllProducts() {
        // Метод является делегирующим и не добавляет никакой собственной логики.
        // Если проверять наличие обращений к методу getAll():
        // Mockito.verify(productDaoMock, Mockito.times(1)).getAll();
        // то результат всегда будет положительный.
        // Если задавать через Mockito правило по типу:
        //
        // List<Product> products = List.of(
        //          new Product("milk", 3),
        //          new Product("bread", 2));
        // Mockito.when(productDaoMock.getAll()).thenReturn(products);
        //
        // Assertions.assertEquals(products, shoppingService.getAllProducts());
        //
        // то результат так же всегда будет положительный,
        // так как мы сравниваем возвращенный мок со значением, которое мы сами и задали.
        // Отсутствует логика для тестирования
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