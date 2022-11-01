package ru.job4j.utils;

import java.time.LocalDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        parse = parse.substring(0, 19);
        return LocalDateTime.parse(parse);
    }
}
