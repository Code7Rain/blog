package com.lll.blog.service;

import com.lll.blog.vo.ArticleVo;
import com.lll.blog.vo.Result;
import com.lll.blog.vo.params.ArticleParam;
import com.lll.blog.vo.params.PageParams;



public interface ArticleService {

    Result listArticle(PageParams pageParams);

    Result hotArticle(int limit);

    Result newArticles(int limit);

    Result listArchives();

    ArticleVo findArticleById(Long id);

    Result publish(ArticleParam articleParam);
}
