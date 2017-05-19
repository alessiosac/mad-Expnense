package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;

//questa classe viene richiamata dopo che clicco su di un gruppo
public class ExpenseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ExpenseDetailFragment.ARG_EXPENSE_ID, getIntent().getStringExtra(ExpenseDetailFragment.ARG_EXPENSE_ID));
            arguments.putString(ExpenseDetailFragment.ARG_EXPENSE_NAME, getIntent().getStringExtra(ExpenseDetailFragment.ARG_EXPENSE_NAME));
            ExpenseDetailFragment fragmentExpense;
            fragmentExpense = new ExpenseDetailFragment();
            fragmentExpense.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.expense_detail_container, fragmentExpense).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, TabView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}