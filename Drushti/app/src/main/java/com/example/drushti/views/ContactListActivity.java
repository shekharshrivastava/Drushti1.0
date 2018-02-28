package com.example.drushti.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drushti.model.ContactVO;
import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.utils.PermissionUtils;
import com.example.drushti.R;
import com.example.drushti.utils.RecyclerItemTouchHelper;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, TextWatcher {

    RecyclerView rvContacts;
    private EditText searchEditText;
    private ArrayList<ContactVO> contactVOList;
    String textSearch = "";
    private ArrayList<Object> listClone;
    private ContactListAdapter contactAdapter;
    private ProgressDialog dlg;
    private Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(this);
        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        listClone = new ArrayList<Object>();

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvContacts);
        new UpdateContacts().execute();
    }

    private void getAllContacts() {
        if (!PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)) {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), ContactListActivity.this, Manifest.permission.READ_CONTACTS, PermissionUtils.PERMISSION_READ_CONTACT);
        } else {
            contactVOList = new ArrayList();
            ContactVO contactVO;

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        contactVO = new ContactVO();
                        contactVO.setContactName(name);

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactVO.setContactNumber(phoneNumber);
                        }

                        phoneCursor.close();

                        Cursor emailCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCursor.moveToNext()) {
                            String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        }
                        contactVOList.add(contactVO);
                    }
                }

                contactAdapter = new ContactListAdapter(contactVOList, getApplicationContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvContacts.setLayoutManager(new LinearLayoutManager(ContactListActivity.this));
                        rvContacts.setAdapter(contactAdapter);
                    }
                });

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalTextToSpeech._INSTANCE.speak("Contact List", TextToSpeech.SUCCESS, null);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)) {
            String numberString = contactVOList.get(viewHolder.getAdapterPosition()).getContactNumber();
            LocalTextToSpeech._INSTANCE.speak("Calling" + contactVOList.get(viewHolder.getAdapterPosition()).getContactName(), TextToSpeech.SUCCESS, null);
            Uri number = Uri.parse("tel:" + numberString);
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            startActivity(dial);

        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), ContactListActivity.this, Manifest.permission.CALL_PHONE, PermissionUtils.PERMISSION_CALL_PHONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        textSearch = searchEditText.getText().toString();
       /* for (Object object : contactVOList ){
            if (((String)object).matches(textSearch)){
                listClone.add(object);
               *//* contactVOList.clear();
                contactVOList = listClone;
                contactVOList*//*
            }
        }*/
    }

    @Override
    public void afterTextChanged(Editable editable) {
        textSearch = searchEditText.getText().toString();
        LocalTextToSpeech._INSTANCE.speak(textSearch, TextToSpeech.SUCCESS, null);

        contactAdapter.filter(textSearch);
    }

    public class UpdateContacts extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            LocalTextToSpeech._INSTANCE.speak("please wait contact list is loading ", TextToSpeech.SUCCESS, null);
            dlg = new ProgressDialog(ContactListActivity.this);
            dlg.setMessage("Loading contact list");
            dlg.setTitle("Please wait");
            dlg.setCancelable(false);
            dlg.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            getAllContacts();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dlg.dismiss();
            LocalTextToSpeech._INSTANCE.speak("Contact list loaded successfully  ", TextToSpeech.SUCCESS, null);

        }
    }

    @Override
    public void onPause() {

        super.onPause();
        if (dlg != null)
            dlg.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_VIBRATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(ContactListActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case PermissionUtils.PERMISSION_READ_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(ContactListActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
