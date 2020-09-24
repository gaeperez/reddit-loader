package es.uvigo.ei.sing.reddit.entities.pushshift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionJson {
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("created_utc")
    @Expose
    public int createdUtc;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("num_comments")
    @Expose
    public int numComments;
    @SerializedName("permalink")
    @Expose
    public String permalink;
    @SerializedName("pinned")
    @Expose
    public boolean pinned;
    @SerializedName("score")
    @Expose
    public int score;
    @SerializedName("selftext")
    @Expose
    public String selftext;
    @SerializedName("subreddit")
    @Expose
    public String subreddit;
    @SerializedName("subreddit_id")
    @Expose
    public String subredditId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("url")
    @Expose
    public String url;
}
