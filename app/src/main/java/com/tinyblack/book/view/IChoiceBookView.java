//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.view;

import com.tinyblack.basemvplib.IView;
import com.tinyblack.book.bean.SearchBookBean;
import com.tinyblack.book.view.adapter.ChoiceBookAdapter;

import java.util.List;

public interface IChoiceBookView extends IView{

    void refreshSearchBook(List<SearchBookBean> books);

    void loadMoreSearchBook(List<SearchBookBean> books);

    void refreshFinish(Boolean isAll);

    void loadMoreFinish(Boolean isAll);

    void searchBookError();

    void addBookShelfSuccess(List<SearchBookBean> searchBooks);

    void addBookShelfFailed(int code);

    ChoiceBookAdapter getSearchBookAdapter();

    void updateSearchItem(int index);

    void startRefreshAnim();
}
