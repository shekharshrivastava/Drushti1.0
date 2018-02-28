package com.example.drushti.views;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.drushti.R;
import com.example.drushti.utils.PermissionUtils;


public class MessageInboxActivity extends AppCompatActivity {

    private SimpleCursorAdapter adapter;
    private ListView lvMsg;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list2);
        lvMsg = (ListView) findViewById(R.id.lvMsg);
        Intent intent = getIntent();
        content = intent.getStringExtra("messages");
        if (!PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.READ_SMS)) {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), MessageInboxActivity.this, Manifest.permission.READ_CONTACTS, PermissionUtils.PERMISSION_READ_CONTACT);
        } else {
            getMessages(content);
        }
    }

    public void getMessages(String content) {
        Uri inboxURI = Uri.parse(content);

        // List required columns
        String[] reqCols = new String[]{"_id", "body", "address"};

        // Get Content Resolver object, which will deal with Content Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        // Attached Cursor with adapter and display in listview
        adapter = new SimpleCursorAdapter(this, R.layout.list_row, c,
                new String[]{"address","body"}, new int[]{
                R.id.lblNumber, R.id.lblMsg});
        lvMsg.setAdapter(adapter);
    }
}
