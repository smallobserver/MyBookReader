//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.presenter.impl;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;
import com.tinyblack.basemvplib.impl.BaseActivity;
import com.tinyblack.basemvplib.impl.BasePresenterImpl;
import com.tinyblack.book.MApplication;
import com.tinyblack.book.base.observer.SimpleObserver;
import com.tinyblack.book.bean.BookContentBean;
import com.tinyblack.book.bean.BookShelfBean;
import com.tinyblack.book.bean.ChapterRead;
import com.tinyblack.book.bean.ReadBookContentBean;
import com.tinyblack.book.dao.BookContentBeanDao;
import com.tinyblack.book.dao.BookShelfBeanDao;
import com.tinyblack.book.presenter.IBookReadPresenter;
import com.tinyblack.book.utils.PremissionCheck;
import com.tinyblack.book.view.IBookReadView;
import com.tinyblack.book.widget.contentswitchview.BookContentView;
import com.tinyblack.book.BitIntentDataManager;
import com.tinyblack.book.api.BookApi;
import com.tinyblack.book.bean.BookInfoBean;
import com.tinyblack.book.bean.ChapterListBean;
import com.tinyblack.book.bean.LocBookShelfBean;
import com.tinyblack.book.common.RxBusTag;
import com.tinyblack.book.dao.DbHelper;
import com.tinyblack.book.model.impl.ImportBookModelImpl;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ReadBookPresenterImpl extends BasePresenterImpl<IBookReadView> implements IBookReadPresenter {
    private static final String TAG = "ReadBookPresenterImpl0";

    public final static int OPEN_FROM_OTHER = 0;
    public final static int OPEN_FROM_APP = 1;

    private Boolean isAdd = false; //判断是否已经添加进书架
    private int open_from;
    private BookShelfBean bookShelf;

    private int pageLineCount = 5;   //假设5行一页

    private BookApi bookApi;

    public ReadBookPresenterImpl() {
        bookApi = BookApi.getInstance();
    }

    @Override
    public void initData(Activity activity) {
        Intent intent = activity.getIntent();
        open_from = intent.getIntExtra("from", OPEN_FROM_OTHER);
        if (open_from == OPEN_FROM_APP) {
            String key = intent.getStringExtra("data_key");
            bookShelf = (BookShelfBean) BitIntentDataManager.getInstance().getData(key);
            if (!bookShelf.getTag().equals(BookShelfBean.LOCAL_TAG)) {
                mView.showDownloadMenu();
            }
            BitIntentDataManager.getInstance().cleanData(key);
            checkInShelf();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PremissionCheck.checkPremission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //申请权限
                activity.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x11);
            } else {
                openBookFromOther(activity);
            }
        }
    }

    @Override
    public void openBookFromOther(Activity activity) {
        //APP外部打开
        Uri uri = activity.getIntent().getData();
        mView.showLoadBook();
        getRealFilePath(activity, uri)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String value) {
                        ImportBookModelImpl.getInstance().importBook(new File(value))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new SimpleObserver<LocBookShelfBean>() {
                                    @Override
                                    public void onNext(LocBookShelfBean value) {
                                        if (value.getNew())
                                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK, value);
                                        bookShelf = value.getBookShelfBean();
                                        mView.dimissLoadBook();
                                        checkInShelf();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        mView.dimissLoadBook();
                                        mView.loadLocationBookError();
                                        Toast.makeText(MApplication.getInstance(), "文本打开失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dimissLoadBook();
                        mView.loadLocationBookError();
                        Toast.makeText(MApplication.getInstance(), "文本打开失败！", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void detachView() {
        cleanSubscribe();
    }

    @Override
    public int getOpen_from() {
        return open_from;
    }

    @Override
    public BookShelfBean getBookShelf() {
        return bookShelf;
    }

    @Override
    public void initContent() {
        mView.initContentSuccess(bookShelf.getDurChapter(), bookShelf.getBookInfoBean().getChapterlist().size(), bookShelf.getDurChapterPage());
    }

    @Override
    public void loadContent(final BookContentView bookContentView, final long bookTag, final int chapterIndex, int pageIndex) {
        if (null != bookShelf && bookShelf.getBookInfoBean().getChapterlist().size() > 0) {
            if (null != bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean() && null != bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getDurCapterContent()) {
                if (bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineSize() == mView.getPaint().getTextSize() && bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineContent().size() > 0) {
                    //已有数据
                    int tempCount = (int) Math.ceil(bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineContent().size() * 1.0 / pageLineCount) - 1;

                    if (pageIndex == BookContentView.DURPAGEINDEXBEGIN) {
                        pageIndex = 0;
                    } else if (pageIndex == BookContentView.DURPAGEINDEXEND) {
                        pageIndex = tempCount;
                    } else {
                        if (pageIndex >= tempCount) {
                            pageIndex = tempCount;
                        }
                    }

                    int start = pageIndex * pageLineCount;
                    int end = pageIndex == tempCount ? bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineContent().size() : start + pageLineCount;
                    if (bookContentView != null && bookTag == bookContentView.getqTag()) {
                        bookContentView.updateData(bookTag, bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getDurChapterName()
                                , bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineContent().subList(start, end)
                                , chapterIndex
                                , bookShelf.getBookInfoBean().getChapterlist().size()
                                , pageIndex
                                , tempCount + 1);
                    }
                } else {
                    //有元数据  重新分行
                    bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().setLineSize(mView.getPaint().getTextSize());
                    final int finalPageIndex = pageIndex;
                    SeparateParagraphtoLines(bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getDurCapterContent())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .compose(((BaseActivity) mView.getContext()).<List<String>>bindUntilEvent(ActivityEvent.DESTROY))
                            .subscribe(new SimpleObserver<List<String>>() {
                                @Override
                                public void onNext(List<String> value) {
                                    bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineContent().clear();
                                    bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getBookContentBean().getLineContent().addAll(value);
                                    loadContent(bookContentView, bookTag, chapterIndex, finalPageIndex);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (bookContentView != null && bookTag == bookContentView.getqTag())
                                        bookContentView.loadError();
                                }
                            });
                }
            } else {
                final int finalPageIndex1 = pageIndex;
                Observable.create(new ObservableOnSubscribe<ReadBookContentBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<ReadBookContentBean> e) throws Exception {
                        List<BookContentBean> tempList = DbHelper.getInstance().getmDaoSession().getBookContentBeanDao().queryBuilder().where(BookContentBeanDao.Properties.DurChapterUrl.eq(bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getDurChapterUrl())).build().list();
                        e.onNext(new ReadBookContentBean(tempList == null ? new ArrayList<BookContentBean>() : tempList, finalPageIndex1));
                        e.onComplete();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .compose(((BaseActivity) mView.getContext()).<ReadBookContentBean>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new SimpleObserver<ReadBookContentBean>() {
                            @Override
                            public void onNext(ReadBookContentBean tempList) {
                                if (tempList.getBookContentList() != null && tempList.getBookContentList().size() > 0 && tempList.getBookContentList().get(0).getDurCapterContent() != null) {
                                    bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).setBookContentBean(tempList.getBookContentList().get(0));
                                    loadContent(bookContentView, bookTag, chapterIndex, tempList.getPageIndex());
                                } else {
                                    final int finalPageIndex1 = tempList.getPageIndex();
//                                    WebBookModelImpl.getInstance().getBookContent(bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getDurChapterUrl(), chapterIndex, bookShelf.getTag())
                                    if (bookShelf == null && bookShelf.getBookInfoBean() == null && bookShelf.getBookInfoBean().getChapterlist() == null
                                            && bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex) == null)
                                        return;
                                    final String durChapterUrl = bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getDurChapterUrl();
                                    bookApi.getChapterContent(durChapterUrl, bookShelf.getTag(), chapterIndex)
                                            .map(new Function<BookContentBean, BookContentBean>() {
                                                @Override
                                                public BookContentBean apply(BookContentBean bookContentBean) throws Exception {
                                                    if (bookContentBean.getRight()) {
                                                        DbHelper.getInstance().getmDaoSession().getBookContentBeanDao().insertOrReplace(bookContentBean);
                                                        bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).setHasCache(true);
                                                        DbHelper.getInstance().getmDaoSession().getChapterListBeanDao().update(bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex));
                                                    }
                                                    return bookContentBean;
                                                }
                                            })
                                            .compose(((BaseActivity) mView.getContext()).<BookContentBean>bindUntilEvent(ActivityEvent.DESTROY))
                                            .subscribe(new SimpleObserver<BookContentBean>() {
                                                @Override
                                                public void onNext(BookContentBean value) {
                                                    if (value.getDurChapterUrl() != null && value.getDurChapterUrl().length() > 0) {
                                                        bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).setBookContentBean(value);
                                                        if (bookTag == bookContentView.getqTag())
                                                            loadContent(bookContentView, bookTag, chapterIndex, finalPageIndex1);
                                                    } else {
                                                        loadError(bookContentView, bookTag);
                                                    }
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    e.printStackTrace();
                                                    loadError(bookContentView, bookTag);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
        } else {
            if (TextUtils.isEmpty(bookShelf.getNoteUrl()) || bookShelf.getBookInfoBean() == null) {
                loadError(bookContentView, bookTag);
                return;
            }
            //请求书签
            final int finalPageIndex2 = pageIndex;
            bookApi.getBookChapterList(bookShelf.getNoteUrl(), "chapters")
                    .subscribe(new SimpleObserver<List<ChapterListBean>>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            addSubscribe(d);
                        }

                        @Override
                        public void onNext(List<ChapterListBean> chapterListBeans) {
                            BookInfoBean bookInfoBean = bookShelf.getBookInfoBean();
                            if (chapterListBeans.size() > 0) {
                                bookInfoBean.setChapterlist(chapterListBeans);
                                loadContent(bookContentView, bookTag, chapterIndex, finalPageIndex2);
                                return;
                            }
                            onError(null);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e != null) {
                                Log.e(TAG, e.getMessage());
                            }
                            loadError(bookContentView, bookTag);
                        }
                    });

        }
    }

    private void loadError(BookContentView bookContentView, long bookTag) {
        if (bookContentView != null && bookTag == bookContentView.getqTag())
            bookContentView.loadError();
    }

    @Override
    public void updateProgress(int chapterIndex, int pageIndex) {
        bookShelf.setDurChapter(chapterIndex);
        bookShelf.setDurChapterPage(pageIndex);
    }

    @Override
    public void saveProgress() {
        if (bookShelf != null) {
            Observable.create(new ObservableOnSubscribe<BookShelfBean>() {
                @Override
                public void subscribe(ObservableEmitter<BookShelfBean> e) throws Exception {
                    bookShelf.setFinalDate(System.currentTimeMillis());
                    DbHelper.getInstance().getmDaoSession().getBookShelfBeanDao().insertOrReplace(bookShelf);
                    e.onNext(bookShelf);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .subscribe(new SimpleObserver<BookShelfBean>() {
                        @Override
                        public void onNext(BookShelfBean value) {
                            RxBus.get().post(RxBusTag.UPDATE_BOOK_PROGRESS, bookShelf);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Override
    public String getChapterTitle(int chapterIndex) {
        if (bookShelf.getBookInfoBean().getChapterlist().size() == 0) {
            return "无章节";
        } else
            return bookShelf.getBookInfoBean().getChapterlist().get(chapterIndex).getDurChapterName();
    }

    public Observable<List<String>> SeparateParagraphtoLines(final String paragraphstr) {
        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                TextPaint mPaint = (TextPaint) mView.getPaint();
                mPaint.setSubpixelText(true);
                Layout tempLayout = new StaticLayout(paragraphstr, mPaint, mView.getContentWidth(), Layout.Alignment.ALIGN_NORMAL, 0, 0, false);
                List<String> linesdata = new ArrayList<>();
                for (int i = 0; i < tempLayout.getLineCount(); i++) {
                    linesdata.add(paragraphstr.substring(tempLayout.getLineStart(i), tempLayout.getLineEnd(i)));
                }
                e.onNext(linesdata);
                e.onComplete();
            }
        });
    }

    @Override
    public void setPageLineCount(int pageLineCount) {
        this.pageLineCount = pageLineCount;
    }

    private void checkInShelf() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                List<BookShelfBean> temp = DbHelper.getInstance().getmDaoSession().getBookShelfBeanDao().queryBuilder().where(BookShelfBeanDao.Properties.NoteUrl.eq(bookShelf.getNoteUrl())).build().list();
                if (temp == null || temp.size() == 0) {
                    isAdd = false;
                } else
                    isAdd = true;
                e.onNext(isAdd);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .compose(((BaseActivity) mView.getContext()).<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        mView.initPop();
                        mView.setHpbReadProgressMax(bookShelf.getBookInfoBean().getChapterlist().size());
                        mView.startLoadingBook();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public interface OnAddListner {
        public void addSuccess();
    }

    @Override
    public void addToShelf(final OnAddListner addListner) {
        if (bookShelf != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    List<ChapterListBean> chapterlist = bookShelf.getBookInfoBean().getChapterlist();
                    DbHelper.getInstance().getmDaoSession().getChapterListBeanDao().insertOrReplaceInTx(chapterlist);
                    DbHelper.getInstance().getmDaoSession().getBookInfoBeanDao().insertOrReplace(bookShelf.getBookInfoBean());
                    //网络数据获取成功  存入BookShelf表数据库
                    DbHelper.getInstance().getmDaoSession().getBookShelfBeanDao().insertOrReplace(bookShelf);
                    RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookShelf);
                    isAdd = true;
                    e.onNext(true);
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SimpleObserver<Object>() {
                        @Override
                        public void onNext(Object value) {
                            if (addListner != null)
                                addListner.addSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, e.getMessage());
                        }
                    });
        }
    }

    public Boolean getAdd() {
        return isAdd;
    }

    public Observable<String> getRealFilePath(final Context context, final Uri uri) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String data = "";
                if (null != uri) {
                    final String scheme = uri.getScheme();
                    if (scheme == null)
                        data = uri.getPath();
                    else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                        data = uri.getPath();
                    } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                        if (null != cursor) {
                            if (cursor.moveToFirst()) {
                                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                                if (index > -1) {
                                    data = cursor.getString(index);
                                }
                            }
                            cursor.close();
                        }

                        if ((data == null || data.length() <= 0) && uri.getPath() != null && uri.getPath().contains("/storage/emulated/")) {
                            data = uri.getPath().substring(uri.getPath().indexOf("/storage/emulated/"));
                        }
                    }
                }
                e.onNext(data == null ? "" : data);
                e.onComplete();
            }
        });
    }
}
