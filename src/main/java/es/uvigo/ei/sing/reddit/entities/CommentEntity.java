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
@Table(name = "comment",
        indexes = {@Index(columnList = ("submission_id"), name = "fk_comment_submission_id"),
                @Index(columnList = ("user_id"), name = "fk_comment_user_id"),
                @Index(columnList = ("comment_id"), name = "fk_comment_comment_id")})
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "external_id", unique = true, nullable = false, length = 250)
    private String externalId;
    @Basic
    @Column(name = "type", length = 5)
    private String type;
    @Basic
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    @Basic
    @Column(name = "created")
    private LocalDateTime created;
    @Basic
    @Column(name = "edited")
    private LocalDateTime edited;
    @Basic
    @Column(name = "depth")
    private int depth;
    @Basic
    @Column(name = "score")
    private int score;

    @ManyToOne
    @JoinColumn(name = "submission_id", referencedColumnName = "id", nullable = false)
    private SubmissionEntity submission;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    // https://viralpatel.net/blogs/hibernate-self-join-annotations-one-to-many-mapping/
    // Comment replies
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private CommentEntity comment;

    @OneToMany(mappedBy = "comment")
    private Set<CommentEntity> replies = new HashSet<>();

    public String getExternalUniqueId() {
        return type + externalId;
    }
}
