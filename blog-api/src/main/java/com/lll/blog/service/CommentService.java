package com.lll.blog.service;

import com.lll.blog.vo.Result;
import com.lll.blog.vo.params.CommentParam;

public interface CommentService {


    Result commentByArticleId(Long id);

    Result comment(CommentParam commentParam);
}