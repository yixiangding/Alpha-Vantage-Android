package com.example.yixiangding.hw9;

/**
 * Wrapper of necessary data for NEWS section
 */
public class NewsData {
    private String title;
    private String author;
    private String date;
    private String link;

    public NewsData(String title, String author, String date, String link) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }
}
