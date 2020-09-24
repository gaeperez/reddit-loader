package es.uvigo.ei.sing.reddit.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "username", unique = true, nullable = false, length = 250)
    private String username;
    @Basic
    @Column(name = "created")
    private LocalDateTime created;
    @Basic
    @Column(name = "is_moderator")
    private boolean isModerator;
    @Basic
    @Column(name = "has_verified_email")
    private boolean hasVerifiedEmail;
    @Basic
    @Column(name = "comment_karma")
    private int commentKarma;
    @Basic
    @Column(name = "link_karma")
    private int linkKarma;

    @OneToMany(mappedBy = "user")
    private Set<CommentEntity> comments = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<SubmissionEntity> submissions = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<SubredditEntity> subreddits = new HashSet<>();
}
