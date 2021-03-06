//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.model.impl;

import com.tinyblack.book.model.IWebContentModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ContentZhulangModelImpl implements IWebContentModel{
    public static final String TAG = "http://book.zhulang.com";

    public static ContentZhulangModelImpl getInstance() {
        return new ContentZhulangModelImpl();
    }

    private ContentZhulangModelImpl() {

    }
    @Override
    public String analyBookcontent(String s, String realUrl) throws Exception {
        Document doc = Jsoup.parse(s);
        Elements contentEs = doc.getElementById("read-content").children();
        StringBuilder content = new StringBuilder();
        for (int i = 3; i < contentEs.size()-1; i++) {
            String temp = contentEs.get(i).text().trim();
            temp = temp.replaceAll(" ","").replaceAll(" ","");
            if (temp.length() > 0) {
                content.append("\u3000\u3000" + temp);
                if (i < contentEs.size() - 1) {
                    content.append("\r\n");
                }
            }
        }
        return content.toString();
    }
}
