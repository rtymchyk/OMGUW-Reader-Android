package com.romantymchyk.omguwreader.resources;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.Blogger.Builder;

public class BloggerHelper {

    public final static String API_KEY = "ADD_ME";

    public final static String BLOG_OMG_ID = "7392661970363847635";

    public final static String BLOG_MC_ID = "3402766312264227078";

    public final static String BLOG_OH_ID = "2858838374466163108";

    public final static String BLOG_ASK_ID = "8890256589225535002";

    public final static String MOST_RECENTLY_READ_PREF_FILE = "most_recently_read_pref";

    public final static Long BLOG_PAGE_SIZE = (long) 7;

    private static BloggerHelper bloggerHelper;

    private HttpTransport httpTransport;

    private JsonFactory jsonFactory;

    private Builder bloggerBuilder;

    private Blogger blogger;

    public BloggerHelper() {
        this.httpTransport = new NetHttpTransport();
        this.jsonFactory = new GsonFactory();
        this.bloggerBuilder = new Blogger.Builder(httpTransport, jsonFactory, null).setApplicationName("OMGUW Reader");
        this.blogger = bloggerBuilder.build();
    }

    public static BloggerHelper getInstance() {
        if (bloggerHelper == null) bloggerHelper = new BloggerHelper();

        return bloggerHelper;
    }

    public Blogger getBlogger() {
        return this.blogger;
    }

    public static String getBlogIdFromType(String type) {
        if (type.equals("OMG")) return BLOG_OMG_ID;
        else if (type.equals("MC")) return BLOG_MC_ID;
        else if (type.equals("OH")) return BLOG_OH_ID;
        else return BLOG_ASK_ID;
    }

    public static String getLongBlogTypeFromShortType(String type) {
        if (type.equals("OMG")) return "OMG";
        else if (type.equals("MC")) return "MISSED CONNECTION";
        else if (type.equals("OH")) return "OVERHEARD";
        else return "QUESTION";
    }

    public static String getShortBlogTypeFromId(String blogId) {
        if (blogId.equals(BLOG_OMG_ID)) return "OMG";
        else if (blogId.equals(BLOG_MC_ID)) return "MC";
        else if (blogId.equals(BLOG_OH_ID)) return "OH";
        else return "ASK";
    }

}
