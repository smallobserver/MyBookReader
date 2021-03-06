//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.model.impl;

import com.tinyblack.book.model.IWebContentModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ContentVodtwModelImpl implements IWebContentModel {
    public static final String TAG = "http://www.vodtw.com";

    public static ContentVodtwModelImpl getInstance() {
        return new ContentVodtwModelImpl();
    }

    private ContentVodtwModelImpl() {

    }

    @Override
    public String analyBookcontent(String s, String realUrl) throws Exception {
        Document doc = Jsoup.parse(s);
        Element contentE = doc.getElementById("BookText");
        StringBuilder content = new StringBuilder();

        Elements contentEs = contentE.getElementsByTag("p");
        for (int i = 0; i < contentEs.size(); i++) {
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
