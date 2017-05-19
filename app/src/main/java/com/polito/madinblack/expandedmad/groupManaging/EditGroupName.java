package com.polito.madinblack.expandedmad.groupManaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditGroupName extends AppCompatActivity {
    private EditText editGroupName;
    private String groupName;
    private String groupId;
    private String newGroupName;
    private MyApplication ma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_group_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.modify_group_name));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString("groupName");
            groupId = extras.getString("groupIndex");
        }

        ma = MyApplication.getInstance();

        editGroupName = (EditText)findViewById(R.id.edit_group_name);
        editGroupName.setText(groupName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_group_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, GroupSettings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        if (id == R.id.confirm_group_name){
            if(!editGroupName.getText().toString().equals(groupName)) {
                //devo aggiornare il nome del gruppo nel database
                newGroupName = editGroupName.getText().toString();
                updateGroupName(groupId, newGroupName);

                Intent intent = new Intent(this, GroupSettings.class);
                intent.putExtra("groupName", newGroupName);
                intent.putExtra("groupIndex", groupId);
                navigateUpTo(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateGroupName(String groupId, String newGroupName){
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String,Object> nameUpdate = new HashMap<>();
        nameUpdate.put("/groups/" + groupId + "/name", newGroupName);
        nameUpdate.put("/users/" + ma.getUserPhoneNumber() + "/" + ma.getFirebaseId() + "/groups/" + groupId + "/name", newGroupName);
        mDatabaseReference.updateChildren(nameUpdate);
    }
}
