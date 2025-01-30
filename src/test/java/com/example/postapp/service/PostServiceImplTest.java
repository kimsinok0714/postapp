package com.example.postapp.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import com.example.postapp.dto.PageRequestDto;
import com.example.postapp.dto.PageResponseDto;
import com.example.postapp.dto.PostDto;
import com.example.postapp.dto.PostSearchCondition;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class PostServiceImplTest {

    @Autowired
    private PostService postService;

    @Test
    void testRegisterPost() {
        // given
        PostDto postDto = PostDto.builder()
                .title("test1")
                .contents("content1")
                .writer("writer1")
                .regDate(LocalDateTime.now())
                .build();

        // when
        Long id = postService.registerPost(postDto);

        // then
        assertNotNull(id);

        log.info("post id : {}", id);

    }

    @Test
    public void testPaging() {

        // given
        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .page(15)
                .size(10)
                .build();

        // when
        PageResponseDto<PostDto> pageResponseDto = postService.paging(pageRequestDto);

        // then
        log.info("totalCount : {}", pageResponseDto.getTotalCount());
        log.info("page : {}", pageResponseDto.getPageRequestDto().getPage());
        log.info("size : {}", pageResponseDto.getPageRequestDto().getSize());

        pageResponseDto.getDtoList().forEach(post -> {

            log.info("id : {}, title : {}", post.getId(), post.getTitle());

        });

    }


    @Test
    public void testSearch() {

        // given
        PostSearchCondition condition = new PostSearchCondition();
        condition.setTitle("title2");

        PageRequestDto pageRequestDto = PageRequestDto.builder().page(1).size(10).build();

        // when
        PageResponseDto<PostDto> page = postService.search(condition, pageRequestDto);

        // then
        page.getDtoList().forEach(post -> {
            log.info("id : {}, title : {}", post.getId(), post.getTitle());
        });

        log.info("totalCount : {}", page.getTotalCount());
        log.info("pageNumList : {}", page.getPageNumList());




    }
}
