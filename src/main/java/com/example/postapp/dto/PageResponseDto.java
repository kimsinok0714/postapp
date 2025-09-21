package com.example.post_project.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PageResponseDto<T> {

    private List<T> dtoList;

    private PageRequestDto pageRequestDto;

    private long totalCount; // 총 게시글 수

    private boolean prev = false, next = false;

    private int prevPage, nextPage, totalPage, currentPage;

    private int pageSize = 10;  // 페이지 블록 수

    private List<Integer> pageNumList = new ArrayList<>();


    @Builder
    public PageResponseDto(List<T> dtoList, PageRequestDto pageRequestDto, long totalCount) {

        this.dtoList = dtoList;
        this.pageRequestDto = pageRequestDto;
        this.totalCount = totalCount;
        this.currentPage = pageRequestDto.getPage();

        // 현재 페이지 번호가 속한 페이지 블록의 끝 페이지 번호 계산
        int end = (int) (Math.ceil(pageRequestDto.getPage() / (double) pageSize)) * pageSize;  

        
        // 현재 페이지 번호가 속한 페이지 블록의 시작 페이지 번호 계산
        int start = end - (pageSize - 1);    

        // 총 페이지 수 계산 : 총 게시글 수 / 한 페이지당 보여줄 게시글 수
        int lastPage = (int) (Math.ceil(totalCount / (double) pageRequestDto.getSize()));

        this.totalPage = lastPage;

        // 끝 페이지 번호가 총 페이지 수를 넘지 않도록 설정
        end = end > lastPage ? lastPage : end;

        // 이전/다음 페이지 블록 존재 여부
        prev = start > 1;

        // 다음 페이지 블록의 존재하는 조건 : 총 게시글 수 > (현재 블록의 마지막 페이지 번호 × 한 페이지당 게시글 수)
        next = totalCount > (end * pageRequestDto.getSize());

        pageNumList = IntStream.range(start, end + 1).boxed().collect(Collectors.toList());  

        // 이전 페이지 블록의 마지막 페이지 번호 계산 (이전 페이지 블록이 없으면 0 설정)
        // 다음 페이지 블록의 첫 페이지 번호 계산 (다음 페이지 블록이 없으면 0 설정)        
        prevPage = prev ? start - 1 : 0;  

        nextPage = next ? end + 1 : 0;


    }

}






    
