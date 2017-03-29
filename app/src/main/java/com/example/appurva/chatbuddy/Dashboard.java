package com.example.appurva.chatbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Dashboard extends AppCompatActivity {
    private RelativeLayout linearlayout;
    TextView tvUsername;
    EditText etSendText;
    Button bSend;
    ListView mListView;
    ScrollView scroll;
    ArrayList<String> arrayList= new ArrayList<>();
    FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef,mref;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bSend = (Button)findViewById(R.id.bSend);
        mListView=(ListView)findViewById(R.id.listView);
        scroll=(ScrollView)findViewById(R.id.scroll) ;
        etSendText = (EditText) findViewById(R.id.etSendText);
        firebaseAuth = FirebaseAuth.getInstance();
        tvUsername=(TextView)findViewById(R.id.tvuserName);
        user = firebaseAuth.getInstance().getCurrentUser();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        linearlayout = (RelativeLayout)findViewById(R.id.linearlayout);
        Snackbar.make(linearlayout,"Login Successful",Snackbar.LENGTH_SHORT).show();
        tvUsername.setText("Welcome "+user.getDisplayName());

        mref= FirebaseDatabase.getInstance().getReference();
        mRootRef=mref.child("Users");


        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference msg_root=mRootRef.child(mRootRef.push().getKey());
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Name:",user.getDisplayName());
                map.put("Message:",etSendText.getText().toString());
                msg_root.updateChildren(map);
                etSendText.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
            }
        });
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        mListView.setAdapter(arrayAdapter);

        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                arrayList.add(append_chat_conversation(dataSnapshot));
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private String chat_msg,chat_user_name;

    private String append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        String ans="";
        while (i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            if(!user.getDisplayName().equals(chat_user_name))
                ans=chat_user_name +" : "+chat_msg;
            else
                ans="Me : " +chat_msg;

        }
        return ans;
    }












    public void open(View view) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you really want to Logout?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                firebaseAuth.signOut();
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_chat, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_logout) {
                open(linearlayout);
            }

            return super.onOptionsItemSelected(item);
        }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.exit(0);
    }
}
