//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.listener;

import com.tinyblack.book.bean.BookShelfBean;

public interface OnGetChapterListListener {
    public void success(BookShelfBean bookShelfBean);
    public void error();
}
