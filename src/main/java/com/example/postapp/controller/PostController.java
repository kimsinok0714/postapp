package com.example.postapp.controller;

import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.postapp.dto.PageRequestDto;
import com.example.postapp.dto.PageResponseDto;
import com.example.postapp.dto.PostDto;
import com.example.postapp.dto.PostSearchCondition;
import com.example.postapp.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class PostController {
    // DI
    private final PostService postService;

    // 게시글 등록
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Long>> postPost(@RequestBody PostDto postDto) {

        Long id = postService.registerPost(postDto);

        log.info("id : {}", id);

        return new ResponseEntity<>(Map.of("id", id), HttpStatus.CREATED); // 201 상태 코드
        // return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }


    // // 게시글 목록 조회 (페이징 처리)
    // @GetMapping("/posts")
    // public ResponseEntity<PageResponseDto<PostDto>> getPosts(PageRequestDto pageRequestDto) {

    //     log.info("page : {}, size : {}", pageRequestDto.getPage(), pageRequestDto.getSize());

    //     PageResponseDto<PostDto> result = postService.paging(pageRequestDto);

    //     return new ResponseEntity<>(result, HttpStatus.OK);
    // }


    // 게시글 검색 및 게시글 목록 조회
     @GetMapping("/posts")
    public ResponseEntity<PageResponseDto<PostDto>> search(
            @RequestParam Map<String, String> params,
            PageRequestDto pageRequestDto) {

        log.info("page: {}, size: {}, params: {}", 
                pageRequestDto.getPage(), pageRequestDto.getSize(), params);

        PostSearchCondition condition = new PostSearchCondition();

        // 효율적인 검색 조건: Map을 활용하여 동적으로 조건을 처리
        if (params.containsKey("title") && params.get("title") != null) {
            condition.setTitle(params.get("title"));
        }
        if (params.containsKey("contents") && params.get("title") != null) {
            condition.setContents(params.get("contents"));
        }
        if (params.containsKey("writer") && params.get("title") != null) {
            condition.setWriter(params.get("writer"));
        }

        PageResponseDto<PostDto> result = postService.search(condition, pageRequestDto);

        return ResponseEntity.ok(result);
    }



    // 게시글 상세조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDto> getPostView(@PathVariable("id") Long id) {
        PostDto postDto = postService.retrievePost(id);

        return new ResponseEntity(postDto, HttpStatus.OK);

    }
    



                                                                                

}
