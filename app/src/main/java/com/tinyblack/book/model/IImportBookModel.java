//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.model;

import com.tinyblack.book.bean.LocBookShelfBean;
import java.io.File;
import io.reactivex.Observable;

public interface IImportBookModel {

    Observable<LocBookShelfBean> importBook(File book);
}
