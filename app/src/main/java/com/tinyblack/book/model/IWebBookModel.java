//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.model;

import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.bean.BookContentBean;
import com.tinyblack.book.bean.SearchBookBean;
import com.tinyblack.book.listener.OnGetChapterListListener;

import java.util.List;
import io.reactivex.Observable;

public interface IWebBookModel {
    /**
     * 网络请求并解析书籍信息
     */
    Observable<BookShelfBean> getBookInfo(final BookShelfBean bookShelfBean);

    /**
     * 网络解析图书目录
     */
    void getChapterList(final BookShelfBean bookShelfBean,OnGetChapterListListener getChapterListListener);

    /**
     * 章节缓存
     */
    Observable<BookContentBean> getBookContent(final String durChapterUrl, final int durChapterIndex, String tag);

    /**
     * 获取分类书籍
     */
    Observable<List<SearchBookBean>> getKindBook(String url,int page);
    /**
     * 其他站点资源整合搜索
     */
    Observable<List<SearchBookBean>> searchOtherBook(String content,int page,String tag);
}
