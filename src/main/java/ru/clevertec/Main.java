package ru.clevertec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.clevertec.adapters.gson.LocalDateAdapter;
import ru.clevertec.adapters.gson.LocalDateSerializer;
import ru.clevertec.adapters.gson.OffsetDateTimeAdapter;
import ru.clevertec.adapters.gson.OffsetDateTimeSerializer;
import ru.clevertec.converter.impl.JsonConverterImpl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws Exception {

        String json;
        UUID uuid = UUID.fromString("9541ddab-c5e3-4af2-9721-4102682e2ec6");

        /**
         * Создание объекта Product
         */
        Product product = new Product(uuid, "Яблоко", 1.2);
        Product product1 = new Product(uuid, "Груша", 1.2);

        /**
         *  Создание объекта Order
         */
        List<Product> products = List.of(product, product1);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2023-11-12T14:21:19.432775100+03:00");
        Order order = new Order(uuid, products, offsetDateTime);

        /**
         *  Создание объекта Customer
         */
        List<Order> orders = List.of(order);

        Customer customer = Customer.builder()
                .id(uuid)
                .firstName("Вася")
                .lastName("Васильев")
                .dateBirth(LocalDate.of(2000, 1, 1))
                .orders(orders)
                .build();


        JsonConverterImpl jsonConverterImpl = new JsonConverterImpl();

        json = jsonConverterImpl.toJson(customer);
        System.out.println(json);

        //--------------------//

        Customer customer1 = jsonConverterImpl.jsonToObject(json, Customer.class);
        System.out.println("customer1 = " + customer1);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeSerializer())
                .create();

        Customer customer2 = gson.fromJson(json, Customer.class);
        System.out.println("customer2 = " + customer2);
        System.out.println();

        String s = gson.toJson(customer);
        System.out.println("s = " + s);
        String m = jsonConverterImpl.toJson(customer);
        System.out.println("m = " + m);

    }
}