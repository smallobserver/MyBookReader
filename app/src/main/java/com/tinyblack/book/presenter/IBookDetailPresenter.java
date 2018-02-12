//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.presenter;

import com.tinyblack.basemvplib.IPresenter;
import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.bean.SearchBookBean;

public interface IBookDetailPresenter extends IPresenter {

    int getOpenfrom();

    SearchBookBean getSearchBook();

    BookShelfBean getBookShelf();

    Boolean getInBookShelf();

    void getBookShelfInfo();

    void addToBookShelf();

    void removeFromBookShelf();
}
