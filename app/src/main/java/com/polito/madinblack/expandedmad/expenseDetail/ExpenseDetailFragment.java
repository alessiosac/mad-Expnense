package com.polito.madinblack.expandedmad.expenseDetail;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Currency;
import com.polito.madinblack.expandedmad.model.Expense;
import com.polito.madinblack.expandedmad.model.MyApplication;
import com.polito.madinblack.expandedmad.model.PaymentFirebase;
import com.polito.madinblack.expandedmad.tabViewGroup.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import static android.app.Activity.RESULT_OK;

public class ExpenseDetailFragment extends Fragment {
    public static final String ARG_EXPENSE_NAME = "expenseName";
    public static final String ARG_EXPENSE_ID = "expenseId";
    private static final int PAYMENT_REQUEST = 2;
    private static final int CONTENTION_REQUEST = 3;
    private static final int CONTENTION_INFORMATION = 4;

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private String expenseId;
    private String expenseName;
    private DatabaseReference mDatabaseExpenseReference;
    private ValueEventListener mValueEventListener;
    private View rootView;
    private List<String> tagsIt = new ArrayList<>();
    private List<String> tagsEn = new ArrayList<>();
    private TextView tv;


    public ExpenseDetailFragment() {
        // Required empty public constructor
    }
/*
    //usato per instanziare un ogetto fragment
    public static ExpenseDetailFragment3 newInstance() {
        ExpenseDetailFragment3 fragment = new ExpenseDetailFragment3();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_EXPENSE_ID) && getArguments().containsKey(ARG_EXPENSE_ID)) {  //a quanto ho capito questa verifica mi dice se l'utente ha selezionato qualcosa di valido
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            expenseId = getArguments().getString(ARG_EXPENSE_ID);
            expenseName = getArguments().getString(ARG_EXPENSE_NAME);

            tagsIt = getLocaleArrayString(R.array.tags, "it");
            tagsEn = getLocaleArrayString(R.array.tags, "en");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(expenseName);
            }

            mDatabaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("expenses/"+expenseId);

            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Expense expense = dataSnapshot.getValue(Expense.class);

                    if(expense.getPaidByFirebaseId().equals(MyApplication.getFirebaseId()) && expense.getState()== Expense.State.ONGOING) {
                        FloatingActionButton imageButtonGo;
                        ((TextView)rootView.findViewById(R.id.head_title)).setText(getString(R.string.list_payment));
                        (imageButtonGo = (FloatingActionButton) rootView.findViewById(R.id.go_button)).setImageResource(R.drawable.payment3);
                        imageButtonGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), PaymentDetailActivity.class);
                                intent.putExtra(PaymentDetailActivity.ARG_EXPENSE_ID, expense.getId());
                                intent.putExtra(PaymentDetailActivity.ARG_EXPENSE_NAME, expense.getName());
                                intent.putExtra(PaymentDetailActivity.ARG_GROUP_ID, expense.getGroupId());
                                intent.putExtra(PaymentDetailActivity.ARG_EXPENSE_COST, expense.getRoundedCost().toString());
                                intent.putExtra(PaymentDetailActivity.ARG_USER_NAME, expense.getPaidByName());
                                intent.putExtra(PaymentDetailActivity.ARG_USER_SURNAME, expense.getPaidBySurname());
                                intent.putExtra(PaymentDetailActivity.ARG_CURRENCY_ISO, expense.getCurrencyISO().name());
                                startActivityForResult(intent, PAYMENT_REQUEST);
                            }
                        });


                    }else if(expense.getState()== Expense.State.ONGOING){
                        FloatingActionButton imageButtonGo;
                        ((TextView)rootView.findViewById(R.id.head_title)).setText(getString(R.string.contention));
                        (imageButtonGo = (FloatingActionButton) rootView.findViewById(R.id.go_button)).setImageResource(R.drawable.stop_or_prohibition_sign);
                        imageButtonGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ContestExpenseActivity.class);
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_ID, expense.getId());
                                intent.putExtra(ContestExpenseActivity.ARG_GROUP_ID, expense.getGroupId());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_COST, expense.getRoundedCost().toString());
                                intent.putExtra(ContestExpenseActivity.ARG_CURRENCY_ISO, expense.getCurrencyISO().name());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_STATE, expense.getState().name());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_NAME, expense.getName());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_USER_FIREBASEID, expense.getPaidByFirebaseId());
                                startActivityForResult(intent, CONTENTION_REQUEST);

                            }
                        });
                    }else if(expense.getState()== Expense.State.TRANSFER){
                        (rootView.findViewById(R.id.title)).setVisibility(View.GONE);
                    }
                    else{
                        FloatingActionButton imageButtonGo;
                        ((TextView)rootView.findViewById(R.id.head_title)).setText(getString(R.string.contention_information));
                        (imageButtonGo = (FloatingActionButton) rootView.findViewById(R.id.go_button)).setImageResource(R.drawable.ic_info_black_24dp);
                        imageButtonGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ContestExpenseActivity.class);
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_ID, expense.getId());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_NAME, expense.getName());
                                intent.putExtra(ContestExpenseActivity.ARG_GROUP_ID, expense.getGroupId());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_COST, expense.getRoundedCost().toString());
                                intent.putExtra(ContestExpenseActivity.ARG_CURRENCY_ISO, expense.getCurrencyISO().name());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_STATE, expense.getState().name());
                                intent.putExtra(ContestExpenseActivity.ARG_EXPENSE_USER_FIREBASEID, expense.getPaidByFirebaseId());
                                intent.putExtra(ContestExpenseActivity.ARG_PAYMENT_CONTEST_ID, expense.getPaymentContestedId());
                                startActivityForResult(intent, CONTENTION_INFORMATION);
                            }
                        });
                    }

                    if(expense.getDescription()!=null && !(expense.getDescription().isEmpty()))
                        ((TextView) rootView.findViewById(R.id.description_field)).setText(expense.getDescription());
                    else {
                        ((TextView) rootView.findViewById(R.id.description_field)).setText(getString(R.string.no_detail));
                        ((TextView) rootView.findViewById(R.id.description_field)).setTextColor(Color.parseColor("#C1C5C0"));
                    }

                    PaymentFirebase paymentFirebase = expense.paymentFromUser(MyApplication.getFirebaseId());
                    if(paymentFirebase!=null)
                        ((TextView) rootView.findViewById(R.id.paid_container)).setText(paymentFirebase.toString());

                    int tagPos = getTagPosition(expense.getTag());
                    if(Locale.getDefault().toString().equals("en"))
                        ((TextView) rootView.findViewById(R.id.tag_container)).setText(tagsEn.get(tagPos));
                    else if(Locale.getDefault().toString().equals("it"))
                        ((TextView) rootView.findViewById(R.id.tag_container)).setText(tagsIt.get(tagPos));

                    ((TextView) rootView.findViewById(R.id.cost_container)).setText(String.format(Locale.getDefault(), "%.2f",(expense.getCost())));
                    ((TextView) rootView.findViewById(R.id.currency_container)).setText(Currency.toString(expense.getCurrencyISO()));

                    if(expense.getRoundedCost().equals(expense.getCost())){
                        (rootView.findViewById(R.id.roundedCostLayout)).setVisibility(View.GONE);
                    }
                    else {
                        (rootView.findViewById(R.id.roundedCostLayout)).setVisibility(View.VISIBLE);
                        ((TextView) rootView.findViewById(R.id.roundedCost)).setText(String.format(Locale.getDefault(), "%.2f",(expense.getRoundedCost())));
                    }

                    if( expense.getPaidByPhoneNumber().equals(MyApplication.getUserPhoneNumber()) )
                        ((TextView) rootView.findViewById(R.id.buyer_container)).setText(getString(R.string.you));
                    else
                        ((TextView) rootView.findViewById(R.id.buyer_container)).setText(expense.getPaidByName()+" "+expense.getPaidBySurname());
                    ((TextView) rootView.findViewById(R.id.date_container)).setText(Long.toString(expense.getDay()) + "/" + Long.toString(expense.getMonth()) + "/" + Long.toString(expense.getYear()));

                    Double balance = expense.paymentFromUser(MyApplication.getFirebaseId()).getBalance();

                    if(balance > 0){
                        ((TextView) rootView.findViewById(R.id.balance_container)).setText(String.format(Locale.getDefault(), "+%.2f",(balance)));
                        ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#00c200"));
                    }else if(balance < 0){
                        ((TextView) rootView.findViewById(R.id.balance_container)).setText(String.format(Locale.getDefault(), "%.2f",(balance)));
                        ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#ff0000"));
                    }
                    else{
                        ((TextView) rootView.findViewById(R.id.balance_container)).setText(String.format(Locale.getDefault(), "%.2f",(balance)));
                        ((TextView) rootView.findViewById(R.id.balance_container)).setTextColor(Color.parseColor("#000000"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.expense_detail, container, false);

        /*LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.title);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.go_button);
                fab.callOnClick();
            }
        });*/

        tv = (TextView) rootView.findViewById(R.id.head_title);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.go_button);
                fab.callOnClick();
            }
        });
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        //adapter = new RecyclerViewAdapterExpense(getContext(), mDatabaseExpenseListReference);
        if(mValueEventListener!=null)
            mDatabaseExpenseReference.addValueEventListener(mValueEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mValueEventListener!=null)
            mDatabaseExpenseReference.removeEventListener(mValueEventListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), getString(R.string.payment_updated),
                        Toast.LENGTH_SHORT).show();
            } else if(resultCode != 0){
                Toast.makeText(getContext(), getString(R.string.operation_failed),
                        Toast.LENGTH_SHORT).show();
            }

        }
        else if(requestCode == CONTENTION_REQUEST){
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), getString(R.string.contention_generated),
                        Toast.LENGTH_SHORT).show();
            }else if(resultCode == ContestExpenseActivity.RESULT_MODIFIED_YET){
                Toast.makeText(getContext(), getString(R.string.err_no_ongoing),
                        Toast.LENGTH_SHORT).show();

            }


        }
        else if(requestCode == CONTENTION_INFORMATION){
            if(resultCode == RESULT_OK){
                Toast.makeText(getContext(), getString(R.string.contention_confermed),
                        Toast.LENGTH_SHORT).show();

            }else if(resultCode == ContestExpenseActivity.RESULT_DELETED){
                Toast.makeText(getContext(), getString(R.string.contention_deleted),
                        Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == ContestExpenseActivity.RESULT_MODIFIED_YET){
                Toast.makeText(getContext(), getString(R.string.err_no_ongoing),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    protected List<String> getLocaleArrayString(int tags, String locale) {
        Configuration configuration = getLocaleConfiguration(locale);
        return Arrays.asList(getContext().createConfigurationContext(configuration).getResources().getStringArray(tags));
    }

    @NonNull
    private Configuration getLocaleConfiguration(String locale) {
        Configuration configuration = new Configuration(getContext().getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return configuration;
    }

    public int getTagPosition(String tagSelected){
        for(int i = 0; i < tagsIt.size(); i++){
            if(tagsIt.get(i).equals(tagSelected) || tagsEn.get(i).equals(tagSelected))
                return i;
        }
        return -1;
    }
}
