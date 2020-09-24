package es.uvigo.ei.sing.reddit;

import es.uvigo.ei.sing.reddit.utils.Constants;
import es.uvigo.ei.sing.reddit.utils.Functions;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FunctionsTest {

    @Test
    public void shouldConvertToLocalDateTimeFromDate() {
        Instant now = Instant.now();

        Date dateNow = Date.from(now);
        LocalDateTime localDateTimeNow = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        Assert.assertEquals(Functions.convertToLocalDateTime(dateNow), localDateTimeNow);
        Assert.assertNotEquals(Functions.convertToLocalDateTime(null), localDateTimeNow);
    }

    @Test
    public void shouldConvertToDateFromLocalDateTime() {
        Instant now = Instant.now();

        Date dateNow = Date.from(now);
        LocalDateTime localDateTimeNow = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        Assert.assertEquals(Functions.convertToDate(localDateTimeNow), dateNow);
        Assert.assertNotEquals(Functions.convertToDate(null), localDateTimeNow);
    }

    @Test
    public void shouldConvertToDateFromUnixTimestamp() {
        Date date = new Date();
        date.setTime(1559647329000L);

        Assert.assertEquals(Functions.unixTimestampToDate(1559647329), date);
    }

    @Test
    public void shouldConvertToLocalDateTimeFromUnixTimestamp() {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-06-04T13:22:09");

        Assert.assertEquals(Functions.unixTimestampToLocalDateTime(1559647329), localDateTime);
    }

    @Test
    public void shouldSaveChanges() {
        LocalDateTime entityEditSave = LocalDateTime.now();
        LocalDateTime editDate = LocalDateTime.now().plusDays(7);

        Assert.assertTrue(Functions.hasChangesToSave(entityEditSave, editDate));
        Assert.assertTrue(Functions.hasChangesToSave(null, editDate));
        Assert.assertFalse(Functions.hasChangesToSave(editDate, entityEditSave));
        Assert.assertFalse(Functions.hasChangesToSave(editDate, editDate));
        Assert.assertFalse(Functions.hasChangesToSave(entityEditSave, null));
        Assert.assertFalse(Functions.hasChangesToSave(null, null));
    }

    @Test
    public void shouldDecomposeParameters() {
        String validUrl = "ids=&q=DragonBall&size=5&sort=desc&sort_type=score&author=&subreddit=DBZDokkanBattle&after=&before=";

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(Constants.PS_QUERY_IDS, "");
        expectedMap.put(Constants.PS_QUERY_Q, "DragonBall");
        expectedMap.put(Constants.PS_QUERY_SIZE, "5");
        expectedMap.put(Constants.PS_QUERY_SORT, "desc");
        expectedMap.put(Constants.PS_QUERY_SORTTYPE, "score");
        expectedMap.put(Constants.PS_QUERY_AUTHOR, "");
        expectedMap.put(Constants.PS_QUERY_SUBREDDIT, "DBZDokkanBattle");
        expectedMap.put(Constants.PS_QUERY_AFTER, "");
        expectedMap.put(Constants.PS_QUERY_BEFORE, "");

        Assert.assertEquals(Functions.decomposePSParameters(validUrl), expectedMap);
    }

    @Test
    public void shouldSplitInSublists() {
        List<String> idsToTest = IntStream.range(1, 4000).boxed().map(String::valueOf)
                .collect(Collectors.toList());

        Assert.assertEquals(10, Functions.splitIDsInSublists(idsToTest).size());
    }
}
