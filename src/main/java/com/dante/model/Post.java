package com.dante.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String postName;
    private String url;

    @Lob    //@Lob은 일반적인 데이터베이스에서 저장하는 길이인 255개 이상의 문자를 저장하고 싶을 때 지정한다.
    private String description;
    private Integer voteCount = 0;

//    @ManyToOne, @OneToOne과 같이 @XXXToOne 어노테이션들은 기본이 즉시 로딩(EAGER) 이다.꼭 LAZY로 명시적으로 설정해서 사용하자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Subreddit subreddit;



}
