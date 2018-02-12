//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.presenter;

import android.app.Activity;

import com.tinyblack.basemvplib.IPresenter;
import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.widget.contentswitchview.BookContentView;
import com.tinyblack.book.presenter.impl.ReadBookPresenterImpl;

public interface IBookReadPresenter extends IPresenter{

    int getOpen_from();

    BookShelfBean getBookShelf();

    void initContent();

    void loadContent(BookContentView bookContentView, long bookTag, final int chapterIndex, final int page);

    void updateProgress(int chapterIndex, int pageIndex);

    void saveProgress();

    String getChapterTitle(int chapterIndex);

    void setPageLineCount(int pageLineCount);

    void addToShelf(final ReadBookPresenterImpl.OnAddListner addListner);

    Boolean getAdd();

    void initData(Activity activity);

    void openBookFromOther(Activity activity);
}
