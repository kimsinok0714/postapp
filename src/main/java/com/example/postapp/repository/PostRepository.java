package com.example.postapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.postapp.domain.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, CustomPostRepository {
    // @Query(value = """
        SELECT *
        FROM post p
        LEFT JOIN file f ON f.post_id = p.id
        AND f.file_order = 0
        """, nativeQuery = true)
    List<Object[]> findPostsWithFirstFile();
    
    for (Object[] row : results) {
            // row[0] ~ row[n]은 SELECT * 결과 순서대로 들어옵니다.
            // 주의: * 를 쓰면 컬럼 순서는 DB에 따라 다를 수 있으므로 명시적 컬럼 지정이 더 안전합니다.

            // 예시로 post 컬럼 3개, file 컬럼 3개라고 가정
            Long postId     = ((Number) row[0]).longValue();
            String title    = (String) row[1];
            String content  = (String) row[2];

            // 파일 정보는 LEFT JOIN이라 null일 수도 있음
            Long fileId     = row[3] != null ? ((Number) row[3]).longValue() : null;
            Long filePostId = row[4] != null ? ((Number) row[4]).longValue() : null;
            String fileName = (String) row[5];

            System.out.printf("Post: [%d] %s\n", postId, title);
            if (fileId != null) {
                System.out.printf("  - File: [%d] %s\n", fileId, fileName);
            } else {
                System.out.println("  - No file attached");
            }
    }
    
    // JPQL
    // @Query("SELECT p FROM Post AS p WHERE p.title LIKE %:title% ")
    // List<Post> findAllByTitle(@Param("title") String titleStr);

    // @Query("SELECT COUNT(p) FROM Post AS p")
    // int getTotalCount();

    // index : JPQL 표준 함수, List인경우 순서를 기준으로 필터링할 수 있도록 지원한다. 
    // index 함수는 @ElementCollection, @OrderColumn이 붙은 List 타입 필드에서만 사용 가능
    // @Query("SELECT p FROM Post p JOIN p.files f ORDER BY index(f)")
    @Query("SELECT p FROM Post p JOIN p.files f WHERE index(f) = 0")   // p 엔티티를 통해서 연관 관계에 있는 files 엔티티를 찹조한다.
    List<Post> findAlls();

    @Query("SELECT p FROM Post p JOIN p.files f WHERE p.id = :postId AND index(f) = 0")
    Post findPostByIdWithFirstFile(@Param("postId") Long postId);

    // Spring Data JPA에서 사용자 정의 JPQL 쿼리를 정의할 때 사용
    @Query("SELECT SIZE(p.files) FROM Post p WHERE p.id = :id")
    int getFileCount(@Param(value = "id") long id);

}


