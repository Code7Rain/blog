package com.lll.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lll.blog.dao.dos.Archives;
import com.lll.blog.dao.mapper.ArticleBodyMapper;
import com.lll.blog.dao.mapper.ArticleMapper;
import com.lll.blog.dao.mapper.ArticleTagMapper;
import com.lll.blog.dao.pojo.Article;
import com.lll.blog.dao.pojo.ArticleBody;
import com.lll.blog.dao.pojo.ArticleTag;
import com.lll.blog.dao.pojo.SysUser;
import com.lll.blog.service.*;
import com.lll.blog.utils.UserThreadLocal;
import com.lll.blog.vo.*;
import com.lll.blog.vo.params.ArticleParam;
import com.lll.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticleTagMapper articleTagMapper;


    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());

        IPage<Article> articleIPage = this.articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }
//    @Override
//    public Result listArticle(PageParams pageParams) {
//        /**
//         * 1. ???????????? article????????????
//         */
//        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//        if (pageParams.getCategoryId() != null) {
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//        List<Long> articleIdList = new ArrayList<>();
//        if (pageParams.getTagId() != null){
//            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
//?????????????????????????????????id
//            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
    //????????????
//            if (articleIdList.size() > 0){
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//        }
//
//        //????????????????????????
//        //order by create_date desc
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();
//        //????????????????????? ???????????????
//        List<ArticleVo> articleVoList = copyList(records,true,true);
//        return Result.success(articleVoList);
//    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article>  queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article>  queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));

    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Override
    public ArticleVo findArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        threadService.updateViewCount(articleMapper,article);

        return copy(article,true,true,true,true);
    }

    @Override
    @Transactional
    public Result publish(ArticleParam articleParam) {
        //???????????????????????????????????????
        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1???????????????  ??????article??????
         * 2?????????id  ??????????????????
         * 3?????????  ????????????????????????
         * 4?????????  body????????????
         */

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(articleParam.getCategory().getId());

        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common); //?????????
        article.setBodyId(-1L);
        //???????????????????????????id    ????????????articleBody
        this.articleMapper.insert(article);

        //tags
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }

        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyMapper.insert(articleBody);

        //articleBody id?????????article
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(article.getId());
        return Result.success(articleVo);  //??????id?????????????????????vo????????????

//        ??????id??????????????????????????????
//        Map<String,String> map = new HashMap<>();
//        map.put("id",article.getId().toString());
//        return Result.success(map);


    }

    //????????????
    private List<ArticleVo> copyList(List<Article> records,boolean isTag, boolean isAuthor){
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }

    //????????????
    private List<ArticleVo> copyList(List<Article> records,boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory){
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }


    private ArticleVo copy(Article article,boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if(isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            SysUser sysUser = sysUserService.findUserById(article.getAuthorId());
            articleVo.setAuthor(sysUser.getNickname());
        }
        if (isBody){
            ArticleBodyVo articleBody = findArticleBody(article.getBodyId());
            articleVo.setBody(articleBody);
        }
        if (isCategory){
            CategoryVo categoryVo = categoryService.findCategoryById(article.getCategoryId());
            articleVo.setCategory(categoryVo);
        }
        return articleVo;
    }

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBody(Long bodyId) {

        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }


}
