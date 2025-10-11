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

    @PersistenceContext
    private EntityManager em;

    //Q 클래스
    private  QPost post = QPost.post;

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
    public Page<Post> search(PostSearchCondition condition, Pageable pageable) {

        // condition이 모두 null이면 전체 게시글 목록 조회
       if (condition.getTitle() == null && condition.getWriter() == null && condition.getContents() == null ) {     
           
            // //페이지 번호에 해당하는 게시글 목록 조회             
            // List<Post> posts = jpaQueryFactory.selectFrom(post)
            //     .orderBy(post.id.desc())
            //     .offset((long) pageable.getPageNumber() * pageable.getPageSize())
            //     .limit(pageable.getPageSize())
            //     .fetch();

            // long totalCount = jpaQueryFactory.selectFrom(post)
            //     .fetchCount();

            // return PageableExecutionUtils.getPage(posts, pageable, () -> totalCount);            

        } else {
            
           // 게시글 목록 조회 (데이터 조회)
            List<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(
                    writerLike(condition.getWriter()),  // 조건이 null이 아닌 경우에만 쿼리에 포함됩니다. AND 조건
                    titleLike(condition.getTitle()),
                    contentsLike(condition.getContents())
                )
                .orderBy(post.id.desc())
                .offset((long) pageable.getPageNumber() * pageable.getPageSize())  
                .limit(pageable.getPageSize())
                .fetch();


            // 총 게시글 수 조회
            long totalCount = jpaQueryFactory.selectFrom(post)
                .where(
                    writerLike(condition.getWriter())
                        .and(titleLike(condition.getTitle()))
                        .and(contentsLike(condition.getContents()))
                )
                .fetchCount();

            return PageableExecutionUtils.getPage(posts, pageable, () -> totalCount);

        }
        
        //return new PageImpl<>(posts, pageable, total);        
    }


    

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
