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
@Table(name = "subreddit")
public class SubredditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "external_id", unique = true, nullable = false, length = 250)
    private String externalId;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "type", length = 5)
    private String type;
    @Basic
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    @Basic
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Basic
    @Column(name = "sidebar_description", columnDefinition = "TEXT")
    private String sidebarDescription;
    @Basic
    @Column(name = "created")
    private LocalDateTime created;
    @Basic
    @Column(name = "permalink", columnDefinition = "TEXT")
    private String permalink;
    @Basic
    @Column(name = "subscribed_users")
    private int subscribedUsers;

    @OneToMany(mappedBy = "subreddit", cascade = CascadeType.ALL)
    private Set<SubmissionEntity> submissions = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "subreddit_user",
            joinColumns = @JoinColumn(name = "subreddit_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false))
    private Set<UserEntity> users = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "query_subreddit",
            joinColumns = @JoinColumn(name = "subreddit_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "query_id", referencedColumnName = "id", nullable = false))
    private Set<QueryEntity> queries = new HashSet<>();

    public String getExternalUniqueId() {
        return type + externalId;
    }
}
