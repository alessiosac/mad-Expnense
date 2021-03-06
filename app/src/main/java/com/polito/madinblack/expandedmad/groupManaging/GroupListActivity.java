package com.polito.madinblack.expandedmad.groupManaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.CheckLogIn;
import com.polito.madinblack.expandedmad.notification.Config;
import com.polito.madinblack.expandedmad.notification.NotificationUtils;
import com.polito.madinblack.expandedmad.settings.*;
import com.polito.madinblack.expandedmad.StatisticsGraphs;
import com.polito.madinblack.expandedmad.UserPage;
import com.polito.madinblack.expandedmad.login.Logout;
import com.polito.madinblack.expandedmad.model.GroupForUser;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.newGroup.SelectContact;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private static final String TAG = "GroupListActivity";

    private TextView tv1, tv2;

    private CircleImageView userImage;

    private DatabaseReference mUserGroupsReference;
    private Query mQueryUserGroupsReference;
    private DatabaseReference mDatabaseRootReference;
    private DatabaseReference mDatabaseForGroupUrl;
    private DatabaseReference mDatabaseForUserUrl;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private NavigationView navigationView;
    private ValueEventListener valueEventListener;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private NotificationUtils utils;
    private SearchView searchView;
    private String groupIndex;
    private String groupName;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_layoute);


        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();

        if(MyApplication.isVariablesAvailable()) {
            mDatabaseForUserUrl = mDatabaseRootReference.child("users").child(MyApplication.getUserPhoneNumber()).child(MyApplication.getFirebaseId()).child("urlImage");
            mUserGroupsReference = mDatabaseRootReference.child("users/" + MyApplication.getUserPhoneNumber() + "/" + MyApplication.getFirebaseId() + "/groups");
            mQueryUserGroupsReference = mUserGroupsReference.orderByChild("timestamp");
        }


        utils = new NotificationUtils(this);
        mRegistrationBroadcastReceiver = utils.getBroadcastReceiver();

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //le righe di codice di sotto servono al drower laterale che compare
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);  //setDrawerListener(toggle) --> addDrawerListener(toggle)
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //fine codice per Drawer

        //setto nome e cognome nella nav bar
        View header = navigationView.getHeaderView(0);
        userImage = (CircleImageView) header.findViewById(R.id.imageView);
        tv1 = (TextView) header.findViewById(R.id.textView2);
        tv2 = (TextView) header.findViewById(R.id.textView3);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.businessman).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        /*mUserStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.teamwork).into(userImage);
            }
        });*/


        if(MyApplication.isVariablesAvailable()) {
            tv1.setText(MyApplication.getUserName() + " " + MyApplication.getUserSurname());
            tv2.setText(MyApplication.getUserPhoneNumber());
        }
            if (getIntent().getExtras() != null) {
                groupIndex = getIntent().getExtras().getString("groupIndex");
                groupName = getIntent().getExtras().getString("groupName");
                index = getIntent().getExtras().getInt("request");
            }

    }

    @Override
    public void onStart(){
        super.onStart();

        if(mDatabaseForUserUrl!=null && valueEventListener!=null)
            mDatabaseForUserUrl.addValueEventListener(valueEventListener);


        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        View recyclerView = findViewById(R.id.group_list);
        assert recyclerView != null;

        if(mQueryUserGroupsReference!=null) {
            mAdapter = new SimpleItemRecyclerViewAdapter(this, mQueryUserGroupsReference);
            ((RecyclerView) recyclerView).setAdapter(mAdapter);
        }

        if(!MyApplication.isVariablesAvailable()) {
            Intent intent = new Intent(GroupListActivity.this, CheckLogIn.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        if(mAdapter!=null)
            mAdapter.cleanupListener();

        if(mDatabaseForUserUrl!=null && valueEventListener!=null)
            mDatabaseForUserUrl.removeEventListener(valueEventListener);
    }


    //le due funzioni sottostanti servono al menù laterale che esce
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

        if(index == 1 || index == 2){
            Intent intent = new Intent(GroupListActivity.this, TabView.class);
            intent.putExtra("groupIndex", groupIndex);
            intent.putExtra("groupName", groupName);
            intent.putExtra("request", index);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addgroup) {
            //handle add group activity
            Intent intent=new Intent(GroupListActivity.this, SelectContact.class);
            //intent.putExtra("phoneId",phoneId);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_expenses) {
            Intent intent = new Intent(GroupListActivity.this, StatisticsGraphs.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings){
            Intent intent = new Intent(GroupListActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Logout fragment = new Logout();
            fragment.show(getSupportFragmentManager(), "LogoutFragment");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void userInfo(View view){
        Intent intent = new Intent(GroupListActivity.this, UserPage.class);
        startActivity(intent);
    }

    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_onlysearch, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private Query mQueryReference;
        private ValueEventListener mEventListener;

        private List<GroupForUser> mValues = new ArrayList<>();
        private List<GroupForUser> duplicato = new ArrayList<>();
        private TextView emptyView;
        private RecyclerView recyclerView;


        public SimpleItemRecyclerViewAdapter(final Context context, Query ref) {
            mContext = context;
            mQueryReference = ref;
            emptyView = (TextView) findViewById(R.id.empty_view);
            recyclerView = (RecyclerView) findViewById(R.id.group_list);
            // Create child event listener
            // [START child_event_listener_recycler]
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mValues.clear();
                    duplicato.clear();
                    if(searchView!=null)
                        searchView.clearFocus();

                    int cntContested=0;
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        GroupForUser groupForUser = postSnapshot.getValue(GroupForUser.class);
                        if(groupForUser.getContestedExpensesCounter()>0) {
                            mValues.add(cntContested, postSnapshot.getValue(GroupForUser.class));
                            duplicato.add(cntContested, postSnapshot.getValue(GroupForUser.class));
                            cntContested++;
                        }else {
                            mValues.add(groupForUser);
                            duplicato.add(groupForUser);
                        }

                    }



                    if(getItemCount() != 0) {
                        if(emptyView.getVisibility()==View.VISIBLE)
                            emptyView.setVisibility(View.GONE);
                        if(recyclerView.getVisibility()==View.GONE)
                            recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        if(emptyView.getVisibility()==View.GONE)
                            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        if(recyclerView.getVisibility()==View.VISIBLE)
                            recyclerView.setVisibility(View.GONE);
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null) {
                        Log.w(TAG, "Groups:onCancelled", databaseError.toException());
                        Toast.makeText(mContext, getString(R.string.fail_load_group),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };
            if(mQueryReference!=null)
                mQueryReference.addValueEventListener(eventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mEventListener = eventListener;
        }

        @Override
        public GroupListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di gruppi
            if (mValues.get(position).getSize() > 1) {
                holder.mNumView.setText(Long.toString(mValues.get(position).getSize()) + " " + getString(R.string.members));
            } else {
                holder.mNumView.setText(Long.toString(mValues.get(position).getSize()) + " " + getString(R.string.member));
            }
            //holder.mImage.setImageBitmap(downlaoadGroupImage(mValues.get(position).getId()));
            holder.mContentView.setText(mValues.get(position).getName());
            if (mValues.get(position).getNewExpenses()+mValues.get(position).getNewMessages() != 0) {
                Long totValue = mValues.get(position).getNewExpenses()+mValues.get(position).getNewMessages();
                holder.mNotification.setText(totValue.toString());
                holder.mNotification.setVisibility(View.VISIBLE);
            }
            if(holder.mItem.getContestedExpensesCounter()>0)
                holder.mContentView.setTextColor(Color.parseColor("#FF9800"));
            else
                holder.mContentView.setTextColor(Color.parseColor("#212121"));
            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, TabView.class); //qui setto la nuova attività da mostrare a schermo dopo che clicco
                    intent.putExtra("groupIndex", holder.mItem.getId());    //passo alla nuova activity l'ide del gruppo chè l'utente ha selezionto
                    intent.putExtra("groupName", holder.mItem.getName());
                    context.startActivity(intent);
                }
            });

            mDatabaseForGroupUrl = FirebaseDatabase.getInstance().getReference().child("groups").child(mValues.get(position).getId()).child("urlImage");
            mDatabaseForGroupUrl.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.getValue(String.class);
                    Glide.with(getApplicationContext()).load(url).override(128,128).centerCrop().fitCenter().diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.teamwork).into(holder.mImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            /*final StorageReference groupRef = mStorage.child("groups").child(mValues.get(position).getId()).child("groupPicture").child("groupPicture.jpg");
            mDatabaseForUrl = FirebaseDatabase.getInstance().getReference().child("groups").child(mValues.get(position).getId()).child("urlImage");

            groupRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    url = uri.toString();
                    Glide.with(getApplicationContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(groupImages.containsKey(mValues.get(position).getId())){
                        url = groupImages.get(mValues.get(position).getId());
                    }
                }
            });*/

            /*if(!groupImages.containsValue(uri.toString())) {
                groupImages.put(mValues.get(position).getId(), uri.toString());
            }*/
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }   //ritorna il numero di elementi nella lista


        public void cleanupListener() {
            if (mEventListener != null)
                mQueryReference.removeEventListener(mEventListener);
        }

        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNumView;
            public final TextView mContentView;
            public final TextView mNotification;
            public final CircleImageView mImage;

            public GroupForUser mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNumView = (TextView) view.findViewById(R.id.num_members);
                mContentView = (TextView) view.findViewById(R.id.content);
                mNotification = (TextView) view.findViewById(R.id.notification);
                mImage = (CircleImageView) view.findViewById(R.id.group_image_storage);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            mValues.clear();
            if (charText.length() == 0) {
                mValues.addAll(duplicato);
            } else {
                for (GroupForUser wp : duplicato) {
                    if (wp.getName().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        mValues.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }
}
