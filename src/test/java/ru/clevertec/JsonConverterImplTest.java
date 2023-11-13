package ru.clevertec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.adapters.gson.LocalDateAdapter;
import ru.clevertec.adapters.gson.LocalDateSerializer;
import ru.clevertec.adapters.gson.OffsetDateTimeAdapter;
import ru.clevertec.adapters.gson.OffsetDateTimeSerializer;
import ru.clevertec.converter.impl.JsonConverterImpl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

class JsonConverterImplTest {

    JsonConverterImpl jsonConverterImpl;
    Gson gson;

    @BeforeEach
    void setUp() {
        jsonConverterImpl = new JsonConverterImpl();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeSerializer())
                .create();
    }

    @Test
    void shouldObjectToJson() {
        UUID uuid = UUID.fromString("9541ddab-c5e3-4af2-9721-4102682e2ec6");

        Product product = new Product(uuid, "Яблоко", 1.2);
        Product product1 = new Product(uuid, "Груша", 1.2);

        List<Product> products = List.of(product, product1);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2023-11-12T14:21:19.432775100+03:00");
        Order order = new Order(uuid, products, offsetDateTime);

        List<Order> orders = List.of(order);

        Customer customer = Customer.builder()
                .id(uuid)
                .firstName("Вася")
                .lastName("Васильев")
                .dateBirth(LocalDate.of(2000, 1, 1))
                .orders(orders)
                .build();

        String actual = jsonConverterImpl.toJson(customer);

        String expected = gson.toJson(customer);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    void shouldJsonToObject() {
        String json = "{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\",\"firstName\":\"Вася\"," +
                "\"lastName\":\"Васильев\",\"dateBirth\":\"2000-01-01\"," +
                "\"orders\":[{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\"," +
                "\"products\":[{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\",\"name\":\"Яблоко\",\"price\":1.2}," +
                "{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\",\"name\":\"Груша\",\"price\":1.2}]," +
                "\"createDate\":\"2023-11-12T14:21:19.432775100+03:00\"}]}";
        Customer actual = jsonConverterImpl.jsonToObject(json, Customer.class);

        Customer expected = gson.fromJson(json, Customer.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void shouldTestFailureThrowsIllegalArgumentException() {
        String jsonInvalid = "\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\",\"firstName\":\"Вася\"," +
                "\"lastName\":\"Васильев\",\"dateBirth\":\"2000-01-01\"," +
                "\"orders\":[{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\"," +
                "\"products\":[{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\",\"name\":\"Яблоко\",\"price\":1.2}," +
                "{\"id\":\"9541ddab-c5e3-4af2-9721-4102682e2ec6\",\"name\":\"Груша\",\"price\":1.2}]," +
                "\"createDate\":\"2023-11-12T14:21:19.432775100+03:00\"}]}";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Customer actual = jsonConverterImpl.jsonToObject(jsonInvalid, Customer.class);
        });
    }
}