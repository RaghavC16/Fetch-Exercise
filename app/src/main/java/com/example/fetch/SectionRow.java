package com.example.fetch;

// A generic "row" for our adapter: either a header or an item
public class SectionRow {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    public int type;
    public String headerTitle;
    public ListItem item;


    public SectionRow(String headerTitle) {
        this.type = TYPE_HEADER;
        this.headerTitle = headerTitle;
    }


    public SectionRow(ListItem item) {
        this.type = TYPE_ITEM;
        this.item = item;
    }
}