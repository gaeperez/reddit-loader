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
@Table(name = "query", indexes = {@Index(columnList = ("value,type"), name = "value_name_UNIQUE")})
public class QueryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "value", nullable = false, length = 500)
    private String value;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "enum")
    private QueryType type;
    @Basic
    @Column(name = "suspended")
    private boolean isSuspended;
    @Basic
    @Column(name = "created")
    private LocalDateTime created;
    @Basic
    @Column(name = "updated")
    private LocalDateTime updated;
    @Basic
    @Column(name = "times_executed")
    private int timesExecuted;
    @Basic
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "query_subreddit",
            joinColumns = @JoinColumn(name = "query_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "subreddit_id", referencedColumnName = "id", nullable = false))
    private Set<SubredditEntity> subreddits = new HashSet<>();

    public void incrementTimesExecuted() {
        this.timesExecuted++;
    }

    // Allowed values to the type in the database
    private enum QueryType {
        UPDATE,
        QUERY,
        PSCOMMENT,
        PSSUBMISSION
    }
}
