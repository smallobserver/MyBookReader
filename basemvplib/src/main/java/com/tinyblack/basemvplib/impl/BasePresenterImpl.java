package com.tinyblack.basemvplib.impl;

import android.support.annotation.NonNull;

import com.tinyblack.basemvplib.IPresenter;
import com.tinyblack.basemvplib.IView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenterImpl<T extends IView> implements IPresenter {
    protected T mView;

    protected CompositeDisposable disposableManager;

    public BasePresenterImpl() {
        disposableManager = new CompositeDisposable();
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mView = (T) iView;
    }

    @Override
    public void detachView() {
        cleanSubscribe();
    }

    public void addSubscribe(Disposable disposable) {
        if (disposable == null) return;
        disposableManager.add(disposable);
    }

    public void cleanSubscribe() {
        if (disposableManager != null) {
            disposableManager.dispose();
            disposableManager = null;
        }
    }

}
