//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.presenter.impl;

import android.support.annotation.NonNull;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.tinyblack.basemvplib.IView;
import com.tinyblack.basemvplib.impl.BasePresenterImpl;
import com.tinyblack.book.base.observer.SimpleObserver;
import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.bean.ChapterListBean;
import com.tinyblack.book.dao.BookInfoBeanDao;
import com.tinyblack.book.dao.BookShelfBeanDao;
import com.tinyblack.book.dao.ChapterListBeanDao;
import com.tinyblack.book.dao.DbHelper;
import com.tinyblack.book.presenter.IMainPresenter;
import com.tinyblack.book.utils.NetworkUtil;
import com.tinyblack.book.view.IMainView;
import com.tinyblack.book.bean.BookInfoBean;
import com.tinyblack.book.common.RxBusTag;
import com.tinyblack.book.listener.OnGetChapterListListener;
import com.tinyblack.book.model.impl.WebBookModelImpl;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenterImpl extends BasePresenterImpl<IMainView> implements IMainPresenter {

    public void queryBookShelf(final Boolean needRefresh) {
        if (needRefresh)
            mView.activityRefreshView();
        Observable.create(new ObservableOnSubscribe<List<BookShelfBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<BookShelfBean>> e) throws Exception {
                List<BookShelfBean> bookShelfes = DbHelper.getInstance().getmDaoSession().getBookShelfBeanDao().queryBuilder().orderDesc(BookShelfBeanDao.Properties.FinalDate).list();
                for (int i = 0; i < bookShelfes.size(); i++) {
                    List<BookInfoBean> temp = DbHelper.getInstance().getmDaoSession().getBookInfoBeanDao().queryBuilder().where(BookInfoBeanDao.Properties.NoteUrl.eq(bookShelfes.get(i).getNoteUrl())).limit(1).build().list();
                    if (temp != null && temp.size() > 0) {
                        BookInfoBean bookInfoBean = temp.get(0);
                        List<ChapterListBean> chapterList = DbHelper.getInstance().getmDaoSession().getChapterListBeanDao().queryBuilder()
                                .where(ChapterListBeanDao.Properties.NoteUrl.eq(bookShelfes.get(i).getNoteUrl()))
                                .orderAsc(ChapterListBeanDao.Properties.DurChapterIndex).build().list();
                        bookInfoBean.setChapterlist(chapterList);
                        bookShelfes.get(i).setBookInfoBean(bookInfoBean);
                    } else {
                        DbHelper.getInstance().getmDaoSession().getBookShelfBeanDao().delete(bookShelfes.get(i));
                        bookShelfes.remove(i);
                        i--;
                    }
                }
                e.onNext(bookShelfes == null ? new ArrayList<BookShelfBean>() : bookShelfes);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<List<BookShelfBean>>() {
                    @Override
                    public void onNext(List<BookShelfBean> value) {
                        if (null != value) {
                            mView.refreshBookShelf(value);
                            if (needRefresh) {
                                startRefreshBook(value);
                            } else {
                                mView.refreshFinish();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.refreshError(NetworkUtil.getErrorTip(NetworkUtil.ERROR_CODE_ANALY));
                    }
                });
    }

    public void startRefreshBook(List<BookShelfBean> value) {
        if (value != null && value.size() > 0) {
            mView.setRecyclerMaxProgress(value.size());
            refreshBookShelf(value, 0);
        } else {
            mView.refreshFinish();
        }
    }

    private void refreshBookShelf(final List<BookShelfBean> value, final int index) {
        if (index <= value.size() - 1) {
            WebBookModelImpl.getInstance().getChapterList(value.get(index), new OnGetChapterListListener() {
                @Override
                public void success(BookShelfBean bookShelfBean) {
                    saveBookToShelf(value, index);
                }

                @Override
                public void error() {
                    mView.refreshError(NetworkUtil.getErrorTip(NetworkUtil.ERROR_CODE_NONET));
                }
            });
        } else {
            queryBookShelf(false);
        }
    }

    private void saveBookToShelf(final List<BookShelfBean> datas, final int index) {
        Observable.create(new ObservableOnSubscribe<BookShelfBean>() {
            @Override
            public void subscribe(ObservableEmitter<BookShelfBean> e) throws Exception {
                DbHelper.getInstance().getmDaoSession().getChapterListBeanDao().insertOrReplaceInTx(datas.get(index).getBookInfoBean().getChapterlist());
                e.onNext(datas.get(index));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<BookShelfBean>() {
                    @Override
                    public void onNext(BookShelfBean value) {
                        mView.refreshRecyclerViewItemAdd();
                        refreshBookShelf(datas, index + 1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.refreshError(NetworkUtil.getErrorTip(NetworkUtil.ERROR_CODE_NONET));
                    }
                });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(RxBusTag.HAD_ADD_BOOK),
                    @Tag(RxBusTag.HAD_REMOVE_BOOK),
                    @Tag(RxBusTag.UPDATE_BOOK_PROGRESS)
            }
    )
    public void hadddOrRemoveBook(BookShelfBean bookShelfBean) {
        queryBookShelf(false);
    }
}
