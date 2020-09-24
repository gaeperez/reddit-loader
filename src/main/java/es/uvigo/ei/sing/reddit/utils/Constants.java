package es.uvigo.ei.sing.reddit.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    // Logger constants
    public static final String APP_ERROR_INPUT = "Please, introduce the mandatory input arguments. " +
            "The arguments are:" +
            "\n - MODE: GET or FILE." +
            "\n - PARAMETER: When using GET MODE, introduce a list of Subreddits (; separated). When using FILE MODE, introduce the path of the input file." +
            "\n Example of usage: GET java;medicine;biology";
    public static final String APP_ERROR_RUN = "An unexpected error has occurred during the retrieval of the information \"%s\". " +
            "Skipping the search for the following URL parameters %s.";
    public static final String APP_ERROR_RETRIEVAL = "An error has occurred during the retrieval with the MODE {}: {}. " +
            "Skipping the search for the following URL parameters {}.";
    public static final String APP_ERROR_SLEEP = "The main thread cannot go to sleep. Skipping...";
    public static final String APP_ERROR_FILE_PATH = "The path for the input file {} is not valid. Please, revise it and try again.";
    public static final String APP_ERROR_FILE_STRUCTURE = "The structure of the input file is invalid. Please, use the following structure \"MODE\tVALUE\" and try again.";
    public static final String APP_ERROR_INVALID_MODE = "The selected input MODE \"{}\" is not valid. Please, use one of the following MODEs {}, {}, {} or {}.";
    public static final String APP_ERROR_INVALID_URL = "The input URL \"%s\" is too big to be processed. Please, reduce the parameters to a maximum of %s characters.";

    public static final String URL_WARN_DECOMPOSE = "Cannot create a URI from the input URL \"{}\". This URL won't be parsed.";

    public static final String APP_INFO_UPDATE_START = "Starting the retrieval of the information for UPDATE MODE. Current Subreddit {}-{}-{}.";
    public static final String APP_INFO_UPDATE_END = "All information was retrieved for UPDATE MODE MODE. Current Subreddit {}-{}-{}, finishing the process...";
    public static final String APP_INFO_QUERY_START = "Starting the retrieval of the information for QUERY MODE. Current Query {}-{}-{}.";
    public static final String APP_INFO_QUERY_END = "All information was retrieved for QUERY MODE. Current Query {}-{}-{}, finishing the process...";
    public static final String APP_INFO_PSCOMMENT_START = "Starting the retrieval of the information for PSCOMMENT MODE. Current URL parameters {}.";
    public static final String APP_INFO_PSCOMMENT_END = "All information was retrieved for PSCOMMENT MODE. Current URL parameters {}, finishing the process...";
    public static final String APP_INFO_PSSUBMISSION_START = "Starting the retrieval of the information for PSSUBMISSION MODE. Current URL parameters {}.";
    public static final String APP_INFO_PSSUBMISSION_END = "All information was retrieved for PSSUBMISSION MODE. Current URL parameters {}, finishing the process...";
    public static final String APP_INFO_SAVE = "Saving the Subreddit entity %s using cascades in the database.";
    public static final String APP_INFO_SAVE_ALL = "Saving all the Subreddits entities #%s using cascades in the database.";
    public static final String APP_INFO_WAIT = "Waiting {} seconds to refresh the API limitation...";
    public static final String APP_INFO_WAIT_QUERIES = "Waiting {} minutes until executing the queries again...";
    public static final String REDDIT_INFO_SUBREDDIT_START = "Obtaining the Subreddit {}...";
    public static final String REDDIT_INFO_SUBREDDIT_END = "Finishing the Subreddit {} retrieval...";
    public static final String REDDIT_INFO_SUBMISSION_START = "Obtaining the Submissions...";
    public static final String REDDIT_INFO_SUBMISSION_END = "Finishing the Submissions retrieval. {} Submissions were retrieved for the Subreddit {}...";
    public static final String REDDIT_INFO_COMMENT_START = "Obtaining the Comments...";
    public static final String REDDIT_INFO_COMMENT_RETRIEVED = "Comments retrieved from Reddit API...";
    public static final String REDDIT_INFO_COMMENT_END = "Finishing the Comments retrieval. {} Comments were retrieved for the Submission {}...";
    public static final String REDDIT_DEBUG_SUBREDDIT = "The Subreddit {} is already in the database. Retrieving it...";
    public static final String REDDIT_DEBUG_SUBMISSION = "The Submission {} is already in the database. Retrieving it...";
    public static final String REDDIT_DEBUG_COMMENT = "The Comment {} is already in the database. Retrieving it...";
    public static final String REDDIT_DEBUG_USER = "The User {} is already in the database. Retrieving it...";
    public static final String REDDIT_DEBUG_URL = "The URL {} is already in the database. Retrieving it...";
    public static final String REDDIT_WARN_API_SUBREDDIT = "Unable to obtain the Subreddit \"{}\" from the official Reddit API. Error message: {}. Skipping it...";
    public static final String REDDIT_WARN_API_SUBMISSION = "Unable to obtain the Submissions for the Subreddit \"{}\" from the official Reddit API. Error message: {}. Skipping it...";
    public static final String REDDIT_WARN_API_QUERY = "Unable to obtain the Submissions for the Query \"{}\" from the official Reddit API. Error message: {}. Skipping it...";
    public static final String REDDIT_WARN_API_COMMENT = "Unable to obtain the Comments for the Submission ID \"{}\" from the official Reddit API. Error message: {}. Skipping it...";
    public static final String PS_INFO_INIT = "Initializing Pushshift API...";
    public static final String PS_INFO_DECOMPOSE = "Decomposing the URL \"{}\" in parameters...";
    public static final String PS_INFO_SUBMISSIONS = "Obtaining the Submissions for the following IDs {}...";
    public static final String PS_WARN_RESPONSE = "The PS request \"{}\" returned an invalid response \"code {}, {}\". Skipping it...";
    public static final String PS_WARN_RUNTIME = "The PS request \"{}\" went wrong: \"{}\". Skipping it...";
    // App constants
    public static final String EXEC_MODE_GET = "GET";
    public static final String EXEC_MODE_FILE = "FILE";
    public static final String RUN_MODE_UPDATE = "UPDATE";
    public static final String RUN_MODE_QUERY = "QUERY";
    public static final String RUN_MODE_PSCOMMENT = "PSCOMMENT";
    public static final String RUN_MODE_PSSUBMISSION = "PSSUBMISSION";
    public static final String FILE_COMMENT = "#";
    public static final int APP_SECONDS_SUCCESS = 60;
    public static final int APP_SECONDS_ERROR = 180;
    public static final int APP_SECONDS_LINES = 120;
    // Reddit API constants
    public static final String REDDIT_USERAGENT_PLATFORM = "java";
    public static final String REDDIT_USERAGENT_APPID = "XXX";
    public static final String REDDIT_USERAGENT_VERSION = "v0.0.1";
    public static final String REDDIT_INFO_INIT = "Initializing Reddit API for " + Constants.REDDIT_USERNAME + " user...";
    public static final String REDDIT_USERNAME = "XXX";
    public static final String REDDIT_PASSWORD = "XXX";
    public static final String REDDIT_CLIENT_ID = "XXX";
    public static final String REDDIT_CLIENT_SECRET = "XXX";

    public static final String REDDIT_T1_COMMENT = "t1_";
    public static final String REDDIT_T2_ACCOUNT = "t2_";
    public static final String REDDIT_T3_SUBMISSION = "t3_";
    public static final String REDDIT_T4_MESSAGE = "t4_";
    public static final String REDDIT_T5_SUBREDDIT = "t5_";
    public static final String REDDIT_T6_AWARD = "t6_";

    // PushShift API
    public static final String PS_BASE_URL = "https://api.pushshift.io/";
    public static final String PS_QUERY_IDS = "ids";
    public static final String PS_QUERY_Q = "q";
    public static final String PS_QUERY_Q_NOT = "q:not";
    public static final String PS_QUERY_SIZE = "size";
    public static final String PS_QUERY_SORT = "sort";
    public static final String PS_QUERY_SORTTYPE = "sort_type";
    public static final String PS_QUERY_AUTHOR = "author";
    public static final String PS_QUERY_SUBREDDIT = "subreddit";
    public static final String PS_QUERY_AFTER = "after";
    public static final String PS_QUERY_BEFORE = "before";
    public static final String PS_QUERY_TITLE = "title";
    public static final String PS_QUERY_TITLE_NOT = "title:not";
    public static final String PS_QUERY_SELFTEXT = "selftext";
    public static final String PS_QUERY_SELFTEXT_NOT = "selftext:not";
    public static final String PS_QUERY_SCORE = "score";
    public static final String PS_QUERY_NUMCOMMENTS = "num_comments";
    public static final String PS_QUERY_ADULT = "over_18";
    public static final String PS_QUERY_VIDEO = "is_video";
    public static final String PS_QUERY_LOCKED = "locked";
    public static final String PS_QUERY_STICKIED = "stickied";
    public static final String PS_QUERY_SPOILER = "spoiler";
    public static final String PS_QUERY_CONTEST = "contest_mode";

    public static final int PS_URL_LIMIT = 1900;
    public static final int PS_CONNECT_TIMEOUT = 300;
    public static final int PS_READ_TIMEOUT = 300;
    public static final int PS_WRITE_TIMEOUT = 300;
}
