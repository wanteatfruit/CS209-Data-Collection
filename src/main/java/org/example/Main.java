package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.DatabaseConnection;
import okhttp3.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String API_BASE_URL = "https://api.stackexchange.com/";

    public static void main(String[] args) throws IOException {
//        JsonArray questions = searchJavaTaggedQuestions();
        Connection connection = DatabaseConnection.getInstance();

        try {
            List<Integer> idList = getAcceptedAnswerId();
            getAnswerById(idList);
//            deleteQuestions(connection);
//            for (int i = 1; i <=20; i++) { //get i pages, 100 entries per page
//                JsonArray questions = searchJavaTaggedQuestions(i); //activity date从新到老
//                insertQuestions(questions, connection);
//            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

    }

    public static List<Integer> getAcceptedAnswerId(){
        String sql = "select * from questions where accepted_answer_id is not null";
        Connection connection = DatabaseConnection.getInstance();
        List<Integer> id = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                id.add(resultSet.getInt("accepted_answer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //up to 100 ids per query, separated by comma
    public static JsonArray getAnswerById(List<Integer> id) throws IOException {

        //split id into 100 per query
        for (int i = 0; i <1+ id.size()/100; i++) {
            List<Integer> subList = id.subList(i*100, Math.min((i+1)*100, id.size()));
            System.out.println(subList.size());
            StringBuilder api = new StringBuilder("https://api.stackexchange.com/2.3/answers/");
            for (int j = 0; j < subList.size(); j++) {
                api.append(subList.get(j));
                if(j != subList.size()-1){
                    api.append(";");
                }
            }
            api.append("?order=desc&sort=activity&site=stackoverflow");
            Response response = callAPI(api.toString());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");
            System.out.println(items.size());
        }



        return null;
    }

    public static JsonArray getJavaPopularQuestions(int page) throws IOException {
        String api = "https://api.stackexchange.com/2.3/tags/java/faq?page=" + page + "&pagesize=100&site=stackoverflow&filter=!6Wfm_gTKI0ROr";
        System.out.println(api);
        Response response = callAPI(api);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
        System.out.println("quota remaining " + jsonObject.get("quota_remaining"));
        JsonArray items = jsonObject.getAsJsonArray("items");
        return items;
    }

    public static JsonArray searchJavaTaggedQuestions(int page) throws IOException {
        String url = "https://api.stackexchange.com/2.3/search?page=" + page + "&pagesize=100&order=desc&sort=activity&tagged=java&site=stackoverflow&filter=!6Wfm_gTKI0ROr";
        System.out.println(url);
        Response response = callAPI(url);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
        System.out.println("quota remaining " + jsonObject.get("quota_remaining"));
        JsonArray items = jsonObject.getAsJsonArray("items");
        return items;
    }

    private static Response callAPI(String url) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Cookie", "prov=2d4dc682-403e-4461-82c2-24343867f91e")
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static void insertQuestions(JsonArray array, Connection connection) throws SQLException {

        PreparedStatement statement = connection.prepareStatement("insert or replace into questions values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        for (int i = 0; i < array.size(); i++) {
            JsonObject question = array.get(i).getAsJsonObject();
            String tags = question.get("tags").toString().replace("[", "").replace("]", "");
            String user_id;
            if (question.get("owner") == null || question.get("owner").getAsJsonObject().get("user_id") == null) {
                user_id = null;
            } else {
                user_id = question.get("owner").getAsJsonObject().get("user_id").toString();
            }
            String is_answered = question.get("is_answered").toString();
            String view_count = question.get("view_count").toString();
            String answer_count = question.get("answer_count").toString();
            String score = question.get("score").toString();
            String last_activity_date;
            if (question.get("last_activity_date") != null) {
                last_activity_date = question.get("last_activity_date").toString();
            } else {
                last_activity_date = null;
            }
            String creation_date = question.get("creation_date").toString();
            String question_id = question.get("question_id").toString();
            String title = question.get("title").toString();
            String body = question.get("body").toString();
//            String accepted_answer_id = question.get("accepted_answer_id").toString();
            String accepted_answer_id;
            if (question.get("accepted_answer_id") != null) {
                accepted_answer_id = question.get("accepted_answer_id").toString();
            } else {
                accepted_answer_id = null;
            }
//            String closed_date = question.get("closed_date").toString();
            String closed_date = null;
            String last_edit_date;
            if (question.get("last_edit_date") != null) {
                last_edit_date = question.get("last_edit_date").toString();
            } else {
                last_edit_date = null;
            }
            statement.setString(1, question_id);
            statement.setString(2, score);
            statement.setString(3, view_count);
            statement.setString(4, answer_count);
            statement.setString(5, is_answered);
            statement.setString(6, title);
            statement.setString(7, tags);
            statement.setString(8, accepted_answer_id);
            statement.setString(9, last_activity_date);
            statement.setString(10, creation_date);
            statement.setString(11, closed_date);
            statement.setString(12, last_edit_date);
            statement.setString(13, body);
            statement.setString(14, user_id);
            statement.addBatch();
        }
        statement.executeBatch();
    }

    public static void deleteQuestions(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from questions");
        statement.execute();
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


