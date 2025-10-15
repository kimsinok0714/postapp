package com.example.postapp.repository;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import com.example.postapp.domain.Post;
import com.example.postapp.domain.QPost;
import com.example.postapp.dto.PageRequestDto;
// import static com.example.postapp.domain.QPost.post;
import com.example.postapp.dto.PostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {
    
    // JPA 에서 EntityManager를 주입(Inject)하기 위해 사용하는 어노테이션(annotation)입니다.
    @PersistenceContext
    private EntityManager em;

    // Q 클래스    
	// Post Entity에 대한 메타데이터를 포함한 자동 생성된 클래스를 의미한다.	
    private  QPost post = QPost.post;

    // JPAQueryFactory 클래스: QueryDSL 쿼리 생성 및 실행 도구 (핵심 클래스)
	// JPA를 사용하는 애플리케이션에서 타입 안전(Type-Safe)한 쿼리를 생성하고 실행할 수 있도록 도와줍니다.
	// 타입 안전성: 컴파일 시점에 쿼리의 문법 오류를 잡아낼 수 있어 런타임 오류를 줄일 수 있습니다.
    private final JPAQueryFactory jpaQueryFactory;

    // 중요
    public CustomPostRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    
    // 페이징 처리
    @Override
    public List<Post> paging(PageRequestDto pageRequestDto) {

        String qlString = "SELECT p FROM Post As p ORDER BY p.id DESC";

        return em.createQuery(qlString, Post.class)
                .setFirstResult((pageRequestDto.getPage() - 1) * pageRequestDto.getSize())
                .setMaxResults(pageRequestDto.getSize())
                .getResultList();
    }

    
    // 게시글 검색 및 페이징 처리
    @Override
    public Page<Post> search(PostSearchCondition condition, Pageable pageable) {  // title, writer, contents

        // condition이 모두 null이면 전체 게시글 목록 조회
       if (condition.getTitle() == null && condition.getWriter() == null && condition.getContents() == null ) {     
           
            // //페이지 번호에 해당하는 게시글 목록 조회             
            List<Post> posts = jpaQueryFactory.selectFrom(post)
                                              .orderBy(post.id.desc())
                                              .offset((long) pageable.getPageNumber() * pageable.getPageSize())   // pageable.getPageNumber() : 0부터 
                                              .limit(pageable.getPageSize())
                                              .fetch();

            long totalCount = jpaQueryFactory.selectFrom(post)
                                             .fetchCount();

            return PageableExecutionUtils.getPage(posts, pageable, () -> totalCount);            

        } else {
            
           // 게시글 목록 조회 (데이터 조회)
            List<Post> posts = jpaQueryFactory.selectFrom(post)
                                                .where(
                                                    writerLike(condition.getWriter()),  // 조건이 null이 아닌 경우에만 쿼리에 포함됩니다. AND 조건
                                                    titleLike(condition.getTitle()),
                                                    contentsLike(condition.getContents())
                                                )
                                                .orderBy(post.id.desc())
                                                .offset((long) pageable.getPageNumber() * pageable.getPageSize())  // pageable.getPageNumber() : 0부터  시작
                                                .limit(pageable.getPageSize())
                                                .fetch();


            // 총 게시글 수 조회
            long totalCount = jpaQueryFactory.selectFrom(post)
                                                .where(
                                                    writerLike(condition.getWriter())     // 조건이 null이 아닌 경우에만 쿼리에 포함됩니다. AND 조건
                                                    .and(titleLike(condition.getTitle()))
                                                    .and(contentsLike(condition.getContents()))
                                                )
                                                .fetchCount();

           return PageableExecutionUtils.getPage(posts, pageable, () -> totalCount);
        }
        
        //return new PageImpl<>(posts, pageable, total);        
    }

   
    // 동적 쿼리
    private BooleanExpression writerLike(String writer) {

        return writer == null ? null : post.writer.like("%" + writer + "%");

    }

    private BooleanExpression contentsLike(String contents) {

        return contents == null ? null : post.contents.like("%" + contents + "%");

    }

    private BooleanExpression titleLike(String title) {

        return title == null ? null : post.title.like("%" + title + "%");

    }


    @Override
    public List<Object[]> findAllByTitle1(String titleStr) {

        String qlString = "SELECT p.id, p.title, p.writer FROM Post AS p WHERE p.title LIKE :title";

        @SuppressWarnings("unchecked")
        List<Object[]> posts = em.createQuery(qlString)
                                 .setParameter("title", "%" + titleStr + "%")
                                 .getResultList();
        return posts;
    }

   
    @Override
    public List<Post> findAllByTitle(String titleStr) {

        String qlString = "SELECT p FROM Post AS p WHERE p.title LIKE :title";

        List<Post> posts = em.createQuery(qlString, Post.class)
                             .setParameter("title", "%" + titleStr + "%")
                             .getResultList();
        return posts;
    }



    @Override
    public long getTotalCount() {

        String qlString = "SELECT COUNT(p) FROM Post AS p";

        return em.createQuery(qlString, Long.class)
                 .getSingleResult().longValue();

    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findPostAll() {

        String qlString = "SELECT p.id, p.title, size(p.files), f.filename FROM Post p LEFT OUTER JOIN p.files f";

        return em.createQuery(qlString)
                .getResultList();
    }


}
