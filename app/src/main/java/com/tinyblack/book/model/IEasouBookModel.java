//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.model;

import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.bean.SearchBookBean;
import com.tinyblack.book.bean.BookContentBean;
import com.tinyblack.book.listener.OnGetChapterListListener;

import java.util.List;
import io.reactivex.Observable;

public interface IEasouBookModel {
    /**
     * 搜索书籍
     */
    Observable<List<SearchBookBean>> searchBook(String content, int page, int rankKind);

    /**
     * 网络请求并解析书籍信息
     */
    Observable<BookShelfBean> getBookInfo(final BookShelfBean bookShelfBean);

    /**
     * 网络解析图书目录
     */
    void getChapterList(final BookShelfBean bookShelfBean, OnGetChapterListListener getChapterListListener);

    /**
     * 章节缓存
     */
    Observable<BookContentBean> getBookContent(final String durChapterUrl, final int durChapterIndex);
}
