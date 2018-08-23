package com.monke.monkeybook.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.tinyblack.book.bean.SearchHistoryBean;
import com.tinyblack.book.bean.DownloadChapterBean;
import com.tinyblack.book.bean.BookInfoBean;
import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.bean.BookContentBean;
import com.tinyblack.book.bean.ChapterListBean;

import com.monke.monkeybook.dao.SearchHistoryBeanDao;
import com.monke.monkeybook.dao.DownloadChapterBeanDao;
import com.monke.monkeybook.dao.BookInfoBeanDao;
import com.monke.monkeybook.dao.BookShelfBeanDao;
import com.monke.monkeybook.dao.BookContentBeanDao;
import com.monke.monkeybook.dao.ChapterListBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig searchHistoryBeanDaoConfig;
    private final DaoConfig downloadChapterBeanDaoConfig;
    private final DaoConfig bookInfoBeanDaoConfig;
    private final DaoConfig bookShelfBeanDaoConfig;
    private final DaoConfig bookContentBeanDaoConfig;
    private final DaoConfig chapterListBeanDaoConfig;

    private final SearchHistoryBeanDao searchHistoryBeanDao;
    private final DownloadChapterBeanDao downloadChapterBeanDao;
    private final BookInfoBeanDao bookInfoBeanDao;
    private final BookShelfBeanDao bookShelfBeanDao;
    private final BookContentBeanDao bookContentBeanDao;
    private final ChapterListBeanDao chapterListBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        searchHistoryBeanDaoConfig = daoConfigMap.get(SearchHistoryBeanDao.class).clone();
        searchHistoryBeanDaoConfig.initIdentityScope(type);

        downloadChapterBeanDaoConfig = daoConfigMap.get(DownloadChapterBeanDao.class).clone();
        downloadChapterBeanDaoConfig.initIdentityScope(type);

        bookInfoBeanDaoConfig = daoConfigMap.get(BookInfoBeanDao.class).clone();
        bookInfoBeanDaoConfig.initIdentityScope(type);

        bookShelfBeanDaoConfig = daoConfigMap.get(BookShelfBeanDao.class).clone();
        bookShelfBeanDaoConfig.initIdentityScope(type);

        bookContentBeanDaoConfig = daoConfigMap.get(BookContentBeanDao.class).clone();
        bookContentBeanDaoConfig.initIdentityScope(type);

        chapterListBeanDaoConfig = daoConfigMap.get(ChapterListBeanDao.class).clone();
        chapterListBeanDaoConfig.initIdentityScope(type);

        searchHistoryBeanDao = new SearchHistoryBeanDao(searchHistoryBeanDaoConfig, this);
        downloadChapterBeanDao = new DownloadChapterBeanDao(downloadChapterBeanDaoConfig, this);
        bookInfoBeanDao = new BookInfoBeanDao(bookInfoBeanDaoConfig, this);
        bookShelfBeanDao = new BookShelfBeanDao(bookShelfBeanDaoConfig, this);
        bookContentBeanDao = new BookContentBeanDao(bookContentBeanDaoConfig, this);
        chapterListBeanDao = new ChapterListBeanDao(chapterListBeanDaoConfig, this);

        registerDao(SearchHistoryBean.class, searchHistoryBeanDao);
        registerDao(DownloadChapterBean.class, downloadChapterBeanDao);
        registerDao(BookInfoBean.class, bookInfoBeanDao);
        registerDao(BookShelfBean.class, bookShelfBeanDao);
        registerDao(BookContentBean.class, bookContentBeanDao);
        registerDao(ChapterListBean.class, chapterListBeanDao);
    }
    
    public void clear() {
        searchHistoryBeanDaoConfig.getIdentityScope().clear();
        downloadChapterBeanDaoConfig.getIdentityScope().clear();
        bookInfoBeanDaoConfig.getIdentityScope().clear();
        bookShelfBeanDaoConfig.getIdentityScope().clear();
        bookContentBeanDaoConfig.getIdentityScope().clear();
        chapterListBeanDaoConfig.getIdentityScope().clear();
    }

    public SearchHistoryBeanDao getSearchHistoryBeanDao() {
        return searchHistoryBeanDao;
    }

    public DownloadChapterBeanDao getDownloadChapterBeanDao() {
        return downloadChapterBeanDao;
    }

    public BookInfoBeanDao getBookInfoBeanDao() {
        return bookInfoBeanDao;
    }

    public BookShelfBeanDao getBookShelfBeanDao() {
        return bookShelfBeanDao;
    }

    public BookContentBeanDao getBookContentBeanDao() {
        return bookContentBeanDao;
    }

    public ChapterListBeanDao getChapterListBeanDao() {
        return chapterListBeanDao;
    }

}
