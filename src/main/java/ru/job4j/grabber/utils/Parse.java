package ru.job4j.grabber.utils;

import java.io.IOException;
import java.util.List;

public interface Parse {
    List<Post> list() throws IOException;
}
