package ru.clevertec;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        String json;


        /**
         * Создание объекта Product
         */
        UUID uuid = UUID.fromString("9541ddab-c5e3-4af2-9721-4102682e2ec6");
        Product product = new Product(uuid, "Яблоко", 1.2);

        json = toJson(product);
        System.out.println(json);

        /**
         *  Создание объекта Order
         */
        List<Product> products = List.of(product);
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        Order order = new Order(uuid, products, offsetDateTime);

        json = toJson(order);
        System.out.println(json);

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
                .myTipe(new MyTipe("Yury", 30))
                .build();

        json = toJson(customer);
        System.out.println(json);
    }
}