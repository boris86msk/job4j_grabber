package ru.job4j.grabber.utils;

import ru.job4j.grabber.HabrCareerParse;
import ru.job4j.grabber.PsqlStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class testPsqlStore {
    public static void main(String[] args) throws IOException {
        try (InputStream in = Store.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Store store = new PsqlStore(config);
            DateTimeParser dtp = new HabrCareerDateTimeParser();
            Parse parse = new HabrCareerParse(dtp);

//            List<Post> list = parse.list();
//            for (Post post : list) {
//                store.save(post);
//            }

           // store.getAll().forEach(System.out::println);

            System.out.println(store.findById(20));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}