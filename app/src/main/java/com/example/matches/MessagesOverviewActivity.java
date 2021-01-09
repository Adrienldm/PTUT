package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MessagesOverviewActivity extends AppCompatActivity {

        ListView usersList;
        TextView noUsersText;
        ArrayList<String> al = new ArrayList<>();
        int totalUsers = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_messagesoverview);

            usersList = (ListView)findViewById(R.id.usersList);
            noUsersText = (TextView)findViewById(R.id.noUsersText);

            String url = "https://mychat-e84d5.firebaseio.com/users.json";

            if(totalUsers <=1){
                noUsersText.setVisibility(View.VISIBLE);
                usersList.setVisibility(View.GONE);
            }
            else{
                noUsersText.setVisibility(View.GONE);
                usersList.setVisibility(View.VISIBLE);
                usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
            }

        }
}
