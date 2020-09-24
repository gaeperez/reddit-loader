package es.uvigo.ei.sing.reddit.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "url")
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "complete", unique = true, nullable = false, length = 250)
    private String complete;
    @Basic
    @Column(name = "protocol")
    private String protocol;
    @Basic
    @Column(name = "subdomain")
    private String subdomain;
    @Basic
    @Column(name = "domain")
    private String domain;
    @Basic
    @Column(name = "port")
    private int port;
    @Basic
    @Column(name = "path", length = 1000)
    private String path;
    @Basic
    @Column(name = "parameters", length = 2500)
    private String parameters;
    @Basic
    @Column(name = "fragment")
    private String fragment;

    @ManyToMany(mappedBy = "urls")
    private Set<SubmissionEntity> submissions = new HashSet<>();
}
