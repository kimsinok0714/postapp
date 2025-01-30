package com.example.postapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchCondition {

    private String title;
    private String contents;
    private String writer;

}
