//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.presenter;

import com.tinyblack.basemvplib.IPresenter;

public interface IMainPresenter extends IPresenter{
    void queryBookShelf(Boolean needRefresh);
}
