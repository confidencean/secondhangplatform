package com.example.secondplatform;

public class Product {
    private int id;
    private String content;
    private String imageUrl;

    public Product(int id, String content, String imageUrl) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return content; // 这将显示在 ListView 中
    }
}
