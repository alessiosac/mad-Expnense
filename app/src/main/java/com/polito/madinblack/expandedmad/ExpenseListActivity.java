package com.polito.madinblack.expandedmad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polito.madinblack.expandedmad.groupManaging.GroupDetailActivity;
import com.polito.madinblack.expandedmad.groupManaging.GroupDetailFragment;
import com.polito.madinblack.expandedmad.groupManaging.GroupListActivity;
import com.polito.madinblack.expandedmad.groupMembers.GroupMemebersActivity;
import com.polito.madinblack.expandedmad.model.*;
import com.polito.madinblack.expandedmad.model.Group;

import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private MyApplication ma;

    private String index = "index";

    private List<ExpenseForUser> eItem;          //quello che vado a mostrare in questa activity è una lista di spese
    private Group groupSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        ma = MyApplication.getInstance();   //retrive del DB

        Intent beginner = getIntent();
        groupSelected = ma.getSingleGroup(Long.valueOf(beginner.getStringExtra("index"))); //recupero l'id del gruppo selezionato, e quindi il gruppo stesso
        //eItem = groupSelected.getExpenses();
        index = beginner.getStringExtra("index");   //id del gruppo, che devo considerare

        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupSelected.getName());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent intent = new Intent(c, ExpenseFillData.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra("index", index);    //passo l'indice del gruppo
                startActivityForResult(intent, 1);
            }
        });

        // Show the Up button in the action bar. (bottone indietro nella pagina 2 che è quella che mostra la lista delle spese)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //in questo punto il codice prende la lista principale e la mostra come recyclerview
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.expense_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override   //lo uso per le icone in alto a destra che posso selezionare
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, GroupDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent.putExtra(GroupDetailFragment.ARG_G_ID, index);   //il tutto viene passato come stringa
                startActivity(intent);
                return true;

            case R.id.home:
                navigateUpTo(new Intent(this, GroupListActivity.class));    //definisco il parente verso cui devo tornare indietro
                return true;

            case R.id.action_members:
                //insert here the connection
                Intent intent2 = new Intent(this, GroupMemebersActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                intent2.putExtra("GROUP_ID", index);
                startActivity(intent2);
                return true;

            case R.id.action_debts:
                //insert here the connection
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override   //questo serve per il search button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(eItem));
    }

    //questa classe la usa per fare il managing della lista che deve mostrare
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ExpenseForUser> mValues;

        public SimpleItemRecyclerViewAdapter(List<ExpenseForUser> expenses) {
            mValues = expenses;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_content, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);   //mValues.get(position) rappresenta un singolo elemento della nostra lista di spese
            holder.mIdView.setText(mValues.get(position).getName());
            if(mValues.get(position).getMyBalance()>0) {
                holder.mContentView.setText(mValues.get(position).getMyBalance() + " " + mValues.get(position).getCurrencySymbol());
                holder.mContentView.setTextColor(Color.parseColor("#00c200"));
            }else if(mValues.get(position).getMyBalance()<0) {
                holder.mContentView.setText(mValues.get(position).getMyBalance() + " " + mValues.get(position).getCurrencySymbol());
                holder.mContentView.setTextColor(Color.parseColor("#ff0000"));
            }
            else{
                holder.mContentView.setText(mValues.get(position).getMyBalance() + " " + mValues.get(position).getCurrencySymbol());
            }

            /*code useful to convert in bold only a piece of string*/
            final SpannableStringBuilder str;

            if(mValues.get(position).getPaidByPhoneNumber().equals(ma.getUserPhoneNumber())) {
                str = new SpannableStringBuilder("Paid by "+ "You");
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), "Paid by ".length(), ("Paid by ".length() + "You".length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                str = new SpannableStringBuilder("Paid by "+ mValues.get(position).getPaidByName());
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), "Paid by ".length(), ("Paid by ".length() + mValues.get(position).getPaidByName().length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.mPaydBy.setText(str);


            //sopra vengono settati i tre campi che costituisco le informazioni di ogni singolo gruppo, tutti pronti per essere mostriti nella gui

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    Intent intent = new Intent(context, ExpenseDetailActivity.class);   //qui setto la nuova attività da mostrare a schermo dopo che clicco
                    intent.putExtra(ExpenseDetailFragment.ARG_ITEM_ID, holder.mItem.getId().toString());
                    intent.putExtra(ExpenseDetailFragment.ARG_GROUP_ID, index);         //così gli passo l'indice del gruppo di interesse

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }   //ritorna il numero di elementi nella lista

        //questa è una classe di supporto che viene usata per creare la vista a schermo, non ho ben capito come funziona
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mPaydBy;
            public final TextView mContentView;
            public ExpenseForUser mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mPaydBy = (TextView) view.findViewById(R.id.paidBy);
                mContentView = (TextView) view.findViewById(R.id.content);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
