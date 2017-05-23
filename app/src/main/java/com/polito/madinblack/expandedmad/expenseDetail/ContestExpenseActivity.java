package com.polito.madinblack.expandedmad.expenseDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.login.BaseActivity;
import com.polito.madinblack.expandedmad.model.Balance;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.HistoryInfo;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.model.PaymentInfo;
import com.polito.madinblack.expandedmad.tabViewGroup.TabView;


import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ContestExpenseActivity extends BaseActivity {

    public static final String ARG_EXPENSE_ID = "expenseId";
    public static final String ARG_GROUP_ID ="expenseName";
    public static final String ARG_EXPENSE_COST ="expenseCost";
    public static final String ARG_CURRENCY_ISO ="currencyISO";

    private DatabaseReference mDatabaseRootReference;
    private DatabaseReference mDatabasePaymentReference;
    private Query mDatabaseQueryFilter;


    private String expenseId;
    private String groupId;
    private Currency.CurrencyISO currencyISO;
    private Double expenseCost;
    private MyApplication ma;
    private ValueEventListener valueEventListener;
    private String paymentId;
    private PaymentFirebase paymentFirebase;
    private Spinner mSpinnerCurrency;
    private TextView mExpenseCurrencySymbol;
    private TextView mOldToPayCurrencySymbol;
    private TextView mNewToPayCurrencySymbol;
    private TextView mExpenseCost;
    private TextView mOldToPay;
    private EditText mNewToPay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contest_expense);

        ma = MyApplication.getInstance();

        expenseId           = getIntent().getStringExtra(ARG_EXPENSE_ID);
        groupId             = getIntent().getStringExtra(ARG_GROUP_ID);
        expenseCost         = Double.valueOf(getIntent().getStringExtra(ARG_EXPENSE_COST));
        currencyISO         = Currency.CurrencyISO.valueOf(getIntent().getStringExtra(ARG_CURRENCY_ISO));


        mDatabaseRootReference = FirebaseDatabase.getInstance().getReference();
        mDatabasePaymentReference = mDatabaseRootReference.child("expenses/"+expenseId+"/payments");
        mDatabaseQueryFilter = mDatabasePaymentReference.equalTo(ma.getUserPhoneNumber(), "userFirebaseId");


        //toolbar settings
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.contention));
        setSupportActionBar(toolbar);

        // Show the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mSpinnerCurrency = (Spinner) findViewById(R.id.currencyISO);
        mExpenseCurrencySymbol = (TextView) findViewById(R.id.expense_currency);
        mOldToPayCurrencySymbol = (TextView) findViewById(R.id.old_topay_currency);
        mNewToPayCurrencySymbol = (TextView) findViewById(R.id.new_topay_currency);
        mExpenseCost = (TextView) findViewById(R.id.expense_cost);
        mOldToPay = (TextView) findViewById(R.id.old_topay);
        mNewToPay = (EditText)  findViewById(R.id.new_topay);

        mExpenseCost.setText(expenseCost.toString());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Currency.getCurrencyValues(currencyISO));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(adapter);



        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        paymentId = childDataSnapshot.getKey();
                        paymentFirebase = childDataSnapshot.getValue(PaymentFirebase.class);
                        if(paymentFirebase.getUserFirebaseId().equals(ma.getFirebaseId())) {

                            ((TextView) findViewById(R.id.old_topay)).setText(paymentFirebase.getToPay().toString());
                        }


                    }

                    mSpinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String item = (String) parent.getItemAtPosition(position);
                            mExpenseCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                            mOldToPayCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                            mNewToPayCurrencySymbol.setText(Currency.getSymbol(Currency.CurrencyISO.valueOf(item)));
                            mExpenseCost.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(expenseCost, currencyISO, Currency.CurrencyISO.valueOf(item))));
                            if(paymentFirebase!=null)
                                mNewToPay.setText(String.format(Locale.getDefault(), "%.2f", Currency.convertCurrency(paymentFirebase.getToPay(), currencyISO, Currency.CurrencyISO.valueOf(item))));

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



    }


    @Override
    public void onStart(){
        super.onStart();
        if(mDatabaseQueryFilter!=null && valueEventListener!=null)
            mDatabaseQueryFilter.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override   //questo serve per il confirm e fill all payments
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contest, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.confirm_contention) {




        }else if(id == 16908332){
            Intent intent3 = new Intent(this, ExpenseDetailFragment.class);
            intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            navigateUpTo(intent3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}