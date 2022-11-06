package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;
import ru.job4j.grabber.utils.Parse;
import ru.job4j.grabber.utils.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private static int pages = 1;
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

    private Post parsePost(Elements elements) {
        return null;
    }

    @Override
    public List<Post> list() throws IOException {
        List<Post> listPost = new ArrayList<>();
        for (int i = 0; i < pages; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%s", PAGE_LINK, i + 1));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElement.child(0).attr("datetime");
                DateTimeParser parser = new HabrCareerDateTimeParser();
                LocalDateTime dateTime = parser.parse(date);
                String description = null;
                try {
                    description = retrieveDescription(link);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Post post = new Post(vacancyName, link, description, dateTime);
                listPost.add(post);
            });
        }
        return listPost;
    }
}