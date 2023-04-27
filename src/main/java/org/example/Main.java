package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Main {
    private static final String API_BASE_URL = "https://api.stackexchange.com/";

    public static void main(String[] args) throws IOException {
       getJavaPopularQuestions(100);
    }
    public static void getJavaPopularQuestions(int pagesize) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.stackexchange.com/2.3/tags/java/faq?pagesize=100&site=stackoverflow")
                .method("GET",null)
                .addHeader("Cookie", "prov=2d4dc682-403e-4461-82c2-24343867f91e")
                .build();
        Response response = client.newCall(request).execute();
        Gson gson= new Gson();
        JsonObject jsonObject = gson.fromJson(response.body().string(),JsonObject.class);
        System.out.println(jsonObject.get("quota_remaining"));
    }
}

/*
"items": [
        {
            "tags": [
                "java",
                "nullpointerexception"
            ],
            "owner": {
                "account_id": 14259,
                "reputation": 21755,
                "user_id": 29182,
                "user_type": "registered",
                "accept_rate": 89,
                "profile_image": "https://www.gravatar.com/avatar/af3daf5de4ebd0531d978fad803a46a0?s=256&d=identicon&r=PG",
                "display_name": "Ziggy",
                "link": "https://stackoverflow.com/users/29182/ziggy"
            },
            "is_answered": true,
            "view_count": 3964571,
            "accepted_answer_id": 218510,
            "answer_count": 12,
            "community_owned_date": 1509494214,
            "score": 209,
            "locked_date": 1409094259,
            "last_activity_date": 1662599267,
            "creation_date": 1224508689,
            "last_edit_date": 1464279301,
            "question_id": 218384,
            "content_license": "CC BY-SA 3.0",
            "link": "https://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it",
            "title": "What is a NullPointerException, and how do I fix it?"
        },
        {
            "tags": [
                "java",
                "string",
                "equality"
            ],
            "owner": {
                "account_id": 12902,
                "reputation": 47763,
                "user_id": 25645,
                "user_type": "registered",
                "accept_rate": 69,
                "profile_image": "https://www.gravatar.com/avatar/b02cab91fb3c5604163c116c494de2a5?s=256&d=identicon&r=PG",
                "display_name": "Nathan H",
                "link": "https://stackoverflow.com/users/25645/nathan-h"
            },
            "is_answered": true,
            "view_count": 4497960,
            "protected_date": 1391145623,
            "accepted_answer_id": 513839,
            "answer_count": 23,
            "community_owned_date": 1387280770,
            "score": 723,
            "locked_date": 1390766401,
            "last_activity_date": 1668251745,
            "creation_date": 1233789427,
            "last_edit_date": 1358948167,
            "question_id": 513832,
            "content_license": "CC BY-SA 3.0",
            "link": "https://stackoverflow.com/questions/513832/how-do-i-compare-strings-in-java",
            "title": "How do I compare strings in Java?"
        },
 */


