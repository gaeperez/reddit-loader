package es.uvigo.ei.sing.reddit.entities.pushshift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataSubmissionJson {
    @SerializedName("data")
    @Expose
    private List<SubmissionJson> data;
}
