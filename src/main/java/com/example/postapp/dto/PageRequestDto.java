package com.example.postapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PageRequestDto {
    // 기본값이 빌더에 의해 무시되지 않도록 보장
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

}
