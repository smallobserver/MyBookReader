//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.tinyblack.book.presenter.impl;

import android.os.Environment;
import com.hwangjr.rxbus.RxBus;
import com.tinyblack.basemvplib.impl.BasePresenterImpl;
import com.tinyblack.book.base.observer.SimpleObserver;
import com.tinyblack.book.presenter.IImportBookPresenter;
import com.tinyblack.book.view.IImportBookView;
import com.tinyblack.book.bean.LocBookShelfBean;
import com.tinyblack.book.common.RxBusTag;
import com.tinyblack.book.model.impl.ImportBookModelImpl;

import java.io.File;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ImportBookPresenterImpl extends BasePresenterImpl<IImportBookView> implements IImportBookPresenter {


    public ImportBookPresenterImpl(){

    }
    @Override
    public void searchLocationBook(){
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)){
                    searchBook(e,new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                }
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SimpleObserver<File>() {
                    @Override
                    public void onNext(File value) {
                        mView.addNewBook(value);
                    }

                    @Override
                    public void onComplete() {
                        mView.searchFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void searchBook(ObservableEmitter<File> e, File parentFile) {
        if (null != parentFile && parentFile.listFiles().length > 0) {
            File[] childFiles = parentFile.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                if (childFiles[i].isFile() && childFiles[i].getName().substring(childFiles[i].getName().lastIndexOf(".") + 1).equalsIgnoreCase("txt") && childFiles[i].length() > 100*1024) {   //100kb
                    e.onNext(childFiles[i]);
                    continue;
                }
                if (childFiles[i].isDirectory() && childFiles[i].listFiles().length > 0) {
                    searchBook(e, childFiles[i]);
                }
            }
        }
    }

    @Override
    public void importBooks(List<File> books){
        Observable.fromIterable(books).flatMap(new Function<File, ObservableSource<LocBookShelfBean>>() {
            @Override
            public ObservableSource<LocBookShelfBean> apply(File file) throws Exception {
                return ImportBookModelImpl.getInstance().importBook(file);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<LocBookShelfBean>() {
                    @Override
                    public void onNext(LocBookShelfBean value) {
                        if(value.getNew()){
                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK,value.getBookShelfBean());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.addError();
                    }

                    @Override
                    public void onComplete() {
                        mView.addSuccess();
                    }
                });
    }

    @Override
    public void detachView() {

    }
}
