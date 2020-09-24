package es.uvigo.ei.sing.reddit.entities.pushshift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentJson {
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("created_utc")
    @Expose
    public int createdUtc;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("link_id")
    @Expose
    public String linkId;
    @SerializedName("parent_id")
    @Expose
    public String parentId;
    @SerializedName("score")
    @Expose
    public int score;
    @SerializedName("subreddit")
    @Expose
    public String subreddit;
    @SerializedName("subreddit_id")
    @Expose
    public String subredditId;
}
