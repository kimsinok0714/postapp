package com.example.postapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.postapp.domain.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, CustomPostRepository {

    // JPQL
    // @Query("SELECT p FROM Post AS p WHERE p.title LIKE %:title% ")
    // List<Post> findAllByTitle(@Param("title") String titleStr);

    // @Query("SELECT COUNT(p) FROM Post AS p")
    // int getTotalCount();

    // index : JPQL 표준 함수, List나 Map에 대해 순서, 키를 기준으로 필터링할 수 있도록 지원한다. 
    // index 함수는 @ElementCollection, @OrderColumn이 붙은 List 타입 필드에서만 사용 가능
    // @Query("SELECT p FROM Post p JOIN p.files f ORDER BY index(f)")
    @Query("SELECT p FROM Post p JOIN p.files f WHERE index(f) = 0")
    List<Post> findAlls();

    @Query("SELECT SIZE(p.files) FROM Post p WHERE p.id = :id")
    int getFileCount(@Param(value = "id") long id);

}


