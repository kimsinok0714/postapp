package com.example.postapp.exception;

public class ArticleNotFoundException extends RuntimeException {

    public ArticleNotFoundException() {
        super("게시글 정보가 존재하지 않습니다.");
    }

    public ArticleNotFoundException(String message) {
        super(message);
    }

}
