package com.example.secondplatform;

class SelfProduct {
    private String id;
    private String content;
    private String price;
    private String addr;
    private String typeId;
    private String typeName;

    public SelfProduct(String id, String content, String price, String addr, String typeId, String typeName) {
        this.id = id;
        this.content = content;
        this.price = price;
        this.addr = addr;
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getPrice() {
        return price;
    }

    public String getAddr() {
        return addr;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }
}
