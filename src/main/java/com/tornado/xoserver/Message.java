package com.tornado.xoserver;

public class Message
{
//    public String name;
//    public int age;
//    //ssssssssssssssssssssssssss
    public Header header;
    public Object data;

    public Message(){
        header = new Header();
        data = new Object();
    }

    public Message(Header header, Object data){
        this.header = header;
        this.data = data;
    }

    // ===== Getters & Setters =====

    public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = (Object) data;
    }
}
