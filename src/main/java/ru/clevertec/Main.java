package ru.clevertec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
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

//        json = JsonConverter.toJson(product);
//        System.out.println(json);

        /**
         *  Создание объекта Order
         */
        List<Product> products = List.of(product, product1);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2023-11-12T14:21:19.432775100+03:00");
        Order order = new Order(uuid, products, offsetDateTime);
//        Order order1 = new Order(uuid, products, offsetDateTime);

//        json = JsonConverter.toJson(order);
//        System.out.println(json);

        /**
         *  Создание объекта Customer
         */
        List<Order> orders = List.of(order);
        HashMap<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        map.put("4", 4);

        Customer customer = Customer.builder()
                .id(uuid)
                .firstName("Вася")
                .lastName("Васильев")
                .dateBirth(LocalDate.of(2000, 1, 1))
                .orders(orders)
//                .myTipe(new MyTipe("Yury", 30, List.of(123, 456)))
                .build();


        JsonConverter jsonConverter = new JsonConverter();

        json = jsonConverter.toJson(customer);
        System.out.println(json);

        //--------------------//

        Customer customer1 = jsonConverter.jsonToObject(json, Customer.class);
        System.out.println("customer1 = " + customer1);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .create();

        Customer customer2 = gson.fromJson(json, Customer.class);
        System.out.println("customer2 = " + customer2);

        OffsetDateTime parse = OffsetDateTime.parse("2023-11-12T14:21:19.432775100+03:00");
        System.out.println("parse = " + parse);
    }
}