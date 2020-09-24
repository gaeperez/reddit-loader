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
@Table(name = "submission",
        indexes = {@Index(columnList = ("subreddit_id"), name = "fk_submission_subreddit_id"),
                @Index(columnList = ("user_id"), name = "fk_submission_user_id")})
public class SubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "external_id", unique = true, nullable = false, length = 250)
    private String externalId;
    @Basic
    @Column(name = "type", length = 5)
    private String type;
    @Basic
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
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
    @Column(name = "permalink", columnDefinition = "TEXT")
    private String permalink;
    @Basic
    @Column(name = "score")
    private int score;
    @Basic
    @Column(name = "comment_count")
    private int commentCount = -1;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private Set<CommentEntity> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "subreddit_id", referencedColumnName = "id", nullable = false)
    private SubredditEntity subreddit;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    // Unidirectional. Only creates the entry in the middle table if you add the URL to the submission and then save.
    // Otherwise, if you add the submission to the URL and then save, it won't create an entry in Submission_url table.
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "submission_url", schema = "reddit_db",
            joinColumns = @JoinColumn(name = "submission_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "url_id", referencedColumnName = "id", nullable = false))
    private Set<UrlEntity> urls = new HashSet<>();

    // Transient variable to compare the old number of comments with the new ones when updating the submission
    @Transient
    private int commentCountOld;

    public String getExternalUniqueId() {
        return type + externalId;
    }
}
