package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.Parse;
import ru.job4j.grabber.utils.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private static final int PAGES = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dtParser) {
        this.dateTimeParser = dtParser;
    }


    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element el = document.select(".style-ugc").first();
        return el.text();
    }

    private Post parsePost(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element dateElement = element.select(".vacancy-card__date").first();
        String vacancyName = titleElement.text();
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String date = dateElement.child(0).attr("datetime");
        LocalDateTime dateTime = dateTimeParser.parse(date);
        String description = null;
        try {
            description = retrieveDescription(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Post(vacancyName, link, description, dateTime);
    }

    @Override
    public List<Post> list() {
        List<Post> listPost = new ArrayList<>();
        try {
            for (int i = 1; i <= PAGES; i++) {
                Connection connection = Jsoup.connect(String.format("%s%s", PAGE_LINK, i));
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    listPost.add(parsePost(row));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listPost;
    }
}