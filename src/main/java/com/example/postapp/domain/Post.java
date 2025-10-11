package com.example.postapp.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = { "id", "title", "contents" })
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String contents;

    private String writer;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    // 1 : N 관계
    @ElementCollection
    @CollectionTable(name = "attachment", joinColumns = @JoinColumn(name = "post_id"))
    @OrderColumn(name = "order_index")
    @Builder.Default
    private List<Attachment> files = new ArrayList<>();

    public void addFiles(Attachment attachment) {
        files.add(attachment);
    }

    // 이 메서드는 엔티티가 persist() 또는 save() 되기 직전에 자동 호출됩니다.
    // @PrePersist
    // public void prePersist() {
    //       this.regDate = this.regDate == null ? LocalDateTime.now() : this.regDate;
    // }

    // 비즈니스 메소드

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContents(String contents) {
        this.contents = contents;
    }

    public void changeWriter(String writer) {
        this.writer = writer;
    }

    public void changeRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

}
