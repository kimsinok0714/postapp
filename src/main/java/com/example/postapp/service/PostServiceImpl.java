package com.example.postapp.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.postapp.domain.Post;
import com.example.postapp.dto.PageRequestDto;
import com.example.postapp.dto.PageResponseDto;
import com.example.postapp.dto.PostDto;
import com.example.postapp.dto.PostSearchCondition;
import com.example.postapp.repository.PostRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    
   

    @Override
    public PostDto retrievePost(Long id) {
        
        Optional<Post> result = postRepository.findById((int)id.longValue());

        // Optional이 비어 있으면 예외를 던지고, 값이 있으면 그 값을 반환한다.
        Post post = result.orElseThrow(() -> new IllegalStateException(id + " 번호에 해당하는 개시글이 없습니다."));

        return entityToDto(post);
    }


    // 게시글 등록
    @Transactional(readOnly = false)
    @Override
    public Long registerPost(PostDto postDto) {
        Post savedPost = postRepository.save(dtoToEntity(postDto));
        return savedPost.getId();
    }


    // 게시글 목록 조회 (페이징)
    @Override    
    public PageResponseDto<PostDto> paging(PageRequestDto pageRequestDto) {

        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("id").descending());

        Page<Post> page = postRepository.findAll(pageable);

        // List<Post> posts = page.getContent();

        // List<PostDto> posts = page.getContent().stream().map(post ->
        // entityToDto(post)).collect(Collectors.toList());
        List<PostDto> posts = page.get().map(post -> entityToDto(post)).collect(Collectors.toList());

        long totalCount = page.getTotalElements();

        return PageResponseDto.<PostDto>builder()
                .dtoList(posts)
                .pageRequestDto(pageRequestDto)
                .totalCount(totalCount)
                .build();

    }


    @Override
    public PageResponseDto<PostDto> search(PostSearchCondition condition, PageRequestDto pageRequestDto) {

        // Spring Data JPA에서 페이징과 정렬을 처리하기 위해서 사용 : 요청한 페이지 번호, 사이즈 (페이지당 게시글 수)
        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1 , pageRequestDto.getSize(), Sort.by("id").descending());

        // Page<T>는 Spring Data에서 페이징 처리 결과를 담는 객체입니다.
        // 내부적으로 전체 데이터 개수, 현재 페이지의 데이터 목록, 페이지 정보 등을 포함합니다.
        Page<Post> page = postRepository.search(condition, pageable);
        
        List<PostDto> posts = page.get().map(this::entityToDto).collect(Collectors.toList());

        return PageResponseDto.<PostDto>builder()
            .dtoList(posts)
            .pageRequestDto(pageRequestDto)
            .totalCount(0)
            .build();
    }    

}
