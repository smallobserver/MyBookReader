/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tinyblack.book.api;

import android.text.TextUtils;

import com.tinyblack.book.api.support.Constant;
import com.tinyblack.book.api.support.HeaderInterceptor;
import com.tinyblack.book.api.support.Logger;
import com.tinyblack.book.api.support.LoggingInterceptor;
import com.tinyblack.book.bean.AutoComplete;
import com.tinyblack.book.bean.BookContentBean;
import com.tinyblack.book.bean.BookHelp;
import com.tinyblack.book.bean.BookHelpList;
import com.tinyblack.book.bean.BookLists;
import com.tinyblack.book.bean.BookMixAToc;
import com.tinyblack.book.bean.BookSource;
import com.tinyblack.book.bean.BooksByCats;
import com.tinyblack.book.bean.CategoryList;
import com.tinyblack.book.bean.CategoryListLv2;
import com.tinyblack.book.bean.ChapterRead;
import com.tinyblack.book.bean.CommentList;
import com.tinyblack.book.bean.DiscussionList;
import com.tinyblack.book.bean.Disscussion;
import com.tinyblack.book.bean.RankingList;
import com.tinyblack.book.bean.Recommend;
import com.tinyblack.book.bean.SearchDetail;
import com.tinyblack.book.bean.user.Login;
import com.tinyblack.book.bean.user.LoginReq;
import com.tinyblack.book.bean.BookDetail;
import com.tinyblack.book.bean.BookListDetail;
import com.tinyblack.book.bean.BookListTags;
import com.tinyblack.book.bean.BookReview;
import com.tinyblack.book.bean.BookReviewList;
import com.tinyblack.book.bean.BooksByTag;
import com.tinyblack.book.bean.ChapterListBean;
import com.tinyblack.book.bean.HotReview;
import com.tinyblack.book.bean.HotWord;
import com.tinyblack.book.bean.Rankings;
import com.tinyblack.book.bean.RecommendBookList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

/**
 * https://github.com/JustWayward/BookReader
 *
 * @author yuyh.
 * @date 2016/8/3.
 */
public class BookApi {

    public static BookApi instance;

    private BookApiService service;

    public BookApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.API_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .client(okHttpClient)
                .build();
        service = retrofit.create(BookApiService.class);
    }

    public static BookApi getInstance(OkHttpClient okHttpClient) {
        if (instance == null)
            instance = new BookApi(okHttpClient);
        return instance;
    }

    public static BookApi getInstance() {
        if (instance == null)
            instance = new BookApi(provideOkHttpClient());
        return instance;
    }

    public static OkHttpClient provideOkHttpClient() {
        LoggingInterceptor logging = new LoggingInterceptor(new Logger());
        logging.setLevel(LoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true) // 失败重发
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(logging);
        return builder.build();
    }

    public Observable<Recommend> getRecommend(String gender) {
        return service.getRecomend(gender);
    }

    public Observable<HotWord> getHotWord() {
        return service.getHotWord();
    }

    public Observable<AutoComplete> getAutoComplete(String query) {
        return service.autoComplete(query);
    }

    public Observable<SearchDetail> getSearchResult(String query) {
        return service.searchBooks(query);
    }

    public Observable<BooksByTag> searchBooksByAuthor(String author) {
        return service.searchBooksByAuthor(author);
    }

    public Observable<BookDetail> getBookDetail(String bookId) {
        return service.getBookDetail(bookId);
    }

    public Observable<HotReview> getHotReview(String book) {
        return service.getHotReview(book);
    }

    public Observable<RecommendBookList> getRecommendBookList(String bookId, String limit) {
        return service.getRecommendBookList(bookId, limit);
    }

    public Observable<BooksByTag> getBooksByTag(String tags, String start, String limit) {
        return service.getBooksByTag(tags, start, limit);
    }

    public Observable<BookMixAToc> getBookMixAToc(String bookId, String view) {
        return service.getBookMixAToc(bookId, view);
    }

    public Observable<List<ChapterListBean>> getBookChapterList(final String bookId, String view) {
        return getBookMixAToc(bookId, view)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<BookMixAToc, List<ChapterListBean>>() {
                    @Override
                    public List<ChapterListBean> apply(BookMixAToc bookMixAToc) throws Exception {
                        List<ChapterListBean> chapterListBeans = new ArrayList<>();
                        if (bookMixAToc != null && bookMixAToc.ok && bookMixAToc.mixToc != null) {
                            BookMixAToc.mixToc mixToc = bookMixAToc.mixToc;
                            if (mixToc.chapters != null) {
                                List<BookMixAToc.mixToc.Chapters> chapters = mixToc.chapters;
                                for (int i = 0; i < chapters.size(); i++) {
                                    BookMixAToc.mixToc.Chapters chapter = chapters.get(i);
                                    ChapterListBean chapterListBean = new ChapterListBean();
                                    chapterListBean.setNoteUrl(bookId);
                                    chapterListBean.setDurChapterIndex(i);
                                    chapterListBean.setDurChapterUrl(chapter.link);   //id
                                    chapterListBean.setDurChapterName(chapter.title);
                                    chapterListBean.setTag(bookId);
                                    chapterListBeans.add(chapterListBean);
                                }
                            }
                        }
                        return chapterListBeans;
                    }
                });
    }


    public synchronized Observable<ChapterRead> getChapterRead(String url) {
        return service.getChapterRead(url);
    }

    public synchronized Observable<BookContentBean> getChapterContent(final String url, final String tag, final int chapterIndex) {
        return getChapterRead(url).map(new Function<ChapterRead, BookContentBean>() {
            @Override
            public BookContentBean apply(ChapterRead chapterRead) throws Exception {
                BookContentBean bookContentBean = new BookContentBean();
                bookContentBean.setDurChapterIndex(chapterIndex);
                bookContentBean.setDurChapterUrl(url);
                bookContentBean.setTag(tag);
                if (chapterRead.chapter != null && !TextUtils.isEmpty(chapterRead.chapter.body)) {
                    //内容格式化下
                    String body = chapterRead.chapter.body.replace(Constant.ENTER, Constant.ENTER + Constant.TWO_SPACE);
                    bookContentBean.setDurCapterContent(Constant.TWO_SPACE + body);
                    bookContentBean.setRight(true);
                }
                return bookContentBean;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }

    public synchronized Observable<List<BookSource>> getBookSource(String view, String book) {
        return service.getABookSource(view, book);
    }

    public Observable<RankingList> getRanking() {
        return service.getRanking();
    }

    public Observable<Rankings> getRanking(String rankingId) {
        return service.getRanking(rankingId);
    }

    public Observable<BookLists> getBookLists(String duration, String sort, String start, String limit, String tag, String gender) {
        return service.getBookLists(duration, sort, start, limit, tag, gender);
    }

    public Observable<BookListTags> getBookListTags() {
        return service.getBookListTags();
    }

    public Observable<BookListDetail> getBookListDetail(String bookListId) {
        return service.getBookListDetail(bookListId);
    }

    public synchronized Observable<CategoryList> getCategoryList() {
        return service.getCategoryList();
    }

    public Observable<CategoryListLv2> getCategoryListLv2() {
        return service.getCategoryListLv2();
    }

    public Observable<BooksByCats> getBooksByCats(String gender, String type, String major, String minor, int start, @Query("limit") int limit) {
        return service.getBooksByCats(gender, type, major, minor, start, limit);
    }

    public Observable<DiscussionList> getBookDisscussionList(String block, String duration, String sort, String type, String start, String limit, String distillate) {
        return service.getBookDisscussionList(block, duration, sort, type, start, limit, distillate);
    }

    public Observable<Disscussion> getBookDisscussionDetail(String disscussionId) {
        return service.getBookDisscussionDetail(disscussionId);
    }

    public Observable<CommentList> getBestComments(String disscussionId) {
        return service.getBestComments(disscussionId);
    }

    public Observable<CommentList> getBookDisscussionComments(String disscussionId, String start, String limit) {
        return service.getBookDisscussionComments(disscussionId, start, limit);
    }

    public Observable<BookReviewList> getBookReviewList(String duration, String sort, String type, String start, String limit, String distillate) {
        return service.getBookReviewList(duration, sort, type, start, limit, distillate);
    }

    public Observable<BookReview> getBookReviewDetail(String bookReviewId) {
        return service.getBookReviewDetail(bookReviewId);
    }

    public Observable<CommentList> getBookReviewComments(String bookReviewId, String start, String limit) {
        return service.getBookReviewComments(bookReviewId, start, limit);
    }

    public Observable<BookHelpList> getBookHelpList(String duration, String sort, String start, String limit, String distillate) {
        return service.getBookHelpList(duration, sort, start, limit, distillate);
    }

    public Observable<BookHelp> getBookHelpDetail(String helpId) {
        return service.getBookHelpDetail(helpId);
    }

    public Observable<Login> login(String platform_uid, String platform_token, String platform_code) {
        LoginReq loginReq = new LoginReq();
        loginReq.platform_code = platform_code;
        loginReq.platform_token = platform_token;
        loginReq.platform_uid = platform_uid;
        return service.login(loginReq);
    }

    public Observable<DiscussionList> getBookDetailDisscussionList(String book, String sort, String type, String start, String limit) {
        return service.getBookDetailDisscussionList(book, sort, type, start, limit);
    }

    public Observable<HotReview> getBookDetailReviewList(String book, String sort, String start, String limit) {
        return service.getBookDetailReviewList(book, sort, start, limit);
    }

    public Observable<DiscussionList> getGirlBookDisscussionList(String block, String duration, String sort, String type, String start, String limit, String distillate) {
        return service.getBookDisscussionList(block, duration, sort, type, start, limit, distillate);
    }

}
