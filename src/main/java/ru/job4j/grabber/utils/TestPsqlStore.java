package ru.job4j.grabber.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.job4j.grabber.HabrCareerParse;
import ru.job4j.grabber.PsqlStore;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class TestPsqlStore {
    private static final Logger LOG = LoggerFactory.getLogger(TestPsqlStore.class.getName());
    
    public static void main(String[] args) {
        try (InputStream in = Store.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            try (PsqlStore store = new PsqlStore(config)) {
                DateTimeParser dtp = new HabrCareerDateTimeParser();
                Parse parse = new HabrCareerParse(dtp);

                List<Post> list = parse.list();
                for (Post post : list) {
                    store.save(post);
                }

                store.getAll().forEach(System.out::println);

                System.out.println(store.findById(20));
            }  catch (Exception e) {
                LOG.error("error of connect to data base", e);
            }

        } catch (Exception e) {
            LOG.error("error of read file properties", e);
        }
    }
}