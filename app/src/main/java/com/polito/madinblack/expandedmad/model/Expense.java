package com.polito.madinblack.expandedmad.model;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Expense {

    /*
    public enum State{CONTEST, ACCEPTED}
    public enum Tag{FOOD, WATER_BILL, GAS_BILL, LIGHT_BILL, FLIGHT, HOTEL, FUEL, DRINK, OTHER}
    public enum Currency{YEN, EURO, DOLLAR, GBP}
    */

    private String  id;
    private String  name;
    private String  tag;
    private String  paidByName;
    private String  paidBySurname;
    private String  paidByFirebaseId;
    private String  paidByPhoneNumber;
    private Double  cost;
    private Double  roundedCost;
    private Currency.CurrencyISO  currencyISO;
    //private String state
    private String  groupId;
    private Long    year;
    private Long    month;
    private Long    day;
    private String  description;
    private String urlImage;


    private Map<String, PaymentFirebase> payments = new HashMap<>();

    //a map showing for each user the cost of the Payment
    //private Map<String, Payment> userCost = new HashMap<>();

    public Expense(){

    }

    public Expense(String id, String name, String tag, String paidByName, String paidBySurname, String paidByFirebaseId, String paidByPhoneNumber, Double cost, Double roundedCost, Currency.CurrencyISO currencyISO, String groupId, Long year, Long month, Long day, String description) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.paidByName = paidByName;
        this.paidBySurname = paidBySurname;
        this.paidByFirebaseId = paidByFirebaseId;
        this.paidByPhoneNumber = paidByPhoneNumber;
        this.cost = cost;
        this.roundedCost = roundedCost;
        this.currencyISO = currencyISO;
        this.groupId = groupId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.description = description;
    }


    public Expense(String id, String name, String tag, String paidByName, String paidBySurname, String paidByFirebaseId, String paidByPhoneNumber, Double cost, Double roundedCost, Currency.CurrencyISO currencyISO, String groupId, Long year, Long month, Long day, String description, Map<String, PaymentFirebase> payments) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.paidByName = paidByName;
        this.paidBySurname = paidBySurname;
        this.paidByFirebaseId = paidByFirebaseId;
        this.paidByPhoneNumber = paidByPhoneNumber;
        this.cost = cost;
        this.roundedCost = roundedCost;
        this.currencyISO = currencyISO;
        this.groupId = groupId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.description = description;
        this.payments = payments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPaidByFirebaseId() {
        return paidByFirebaseId;
    }

    public void setPaidByFirebaseId(String paidByFirebaseId) {
        this.paidByFirebaseId = paidByFirebaseId;
    }

    public String getPaidByPhoneNumber() {
        return paidByPhoneNumber;
    }

    public void setPaidByPhoneNumber(String paidByPhoneNumber) {
        this.paidByPhoneNumber = paidByPhoneNumber;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }

    public String getPaidBySurname() {
        return paidBySurname;
    }

    public void setPaidBySurname(String paidBySurname) {
        this.paidBySurname = paidBySurname;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getRoundedCost() {
        return roundedCost;
    }

    public void setRoundedCost(Double roundedCost) {
        this.roundedCost = roundedCost;
    }

    public Currency.CurrencyISO getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(Currency.CurrencyISO currencyISO) {
        this.currencyISO = currencyISO;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, PaymentFirebase> getPayments() {
        return payments;
    }

    public void setPayments(Map<String, PaymentFirebase> payments) {
        this.payments = payments;
    }

    public PaymentFirebase givePaymentForUser(String userFirebaseId){
        for(PaymentFirebase pay:payments.values()){
            if(pay.getUserFirebaseId().equals(userFirebaseId))
                return pay;
        }
        return null;
    }


    public static String writeNewExpense(final DatabaseReference mDatabaseRootRefenrence, String name, String tag,
                                         String paidByFirebaseId, String paidByPhoneNumber, String paidByName, String paidBySurname, Double cost, Double roundedCost,
                                         final Currency.CurrencyISO currencyISO, final String groupId, Long year, Long month, Long day, String description, List<Payment> paymentList){

        DatabaseReference myExpenseRef = mDatabaseRootRefenrence.child("expenses").push();
        final String expenseKey = myExpenseRef.getKey();

        Expense expense = new Expense(expenseKey, name, tag, paidByName, paidBySurname, paidByFirebaseId, paidByPhoneNumber, CostUtil.round(cost, 2), CostUtil.round(roundedCost, 2), currencyISO, groupId, year, month, day, description);
        myExpenseRef.setValue(expense);

        DatabaseReference myPaymentRef;
        String paymentKey;

        final Map<String, Double> toUpdate = new HashMap<>();

        /*Initiaze the map*/
        for(Payment payment: paymentList)
            if(!(payment.getUserFirebaseId().equals(paidByFirebaseId)))
                toUpdate.put(payment.getUserPhoneNumber()+expenseKey, payment.getDebit());


        for(Payment payment : paymentList){

            myPaymentRef = myExpenseRef.child("payments").push();
            paymentKey = myPaymentRef.getKey();

            PaymentFirebase paymentFirebase = new PaymentFirebase(payment);
            paymentFirebase.setId(paymentKey);
            if(!paymentFirebase.getUserFirebaseId().equals(paidByFirebaseId))
                paymentFirebase.setSortingField(payment.getUserFullName());

            myPaymentRef.setValue(paymentFirebase);

            ExpenseForUser expenseForUser = new ExpenseForUser(expense, payment.getBalance());
            expenseForUser.setTimestamp();
            mDatabaseRootRefenrence.child("users/"+payment.getUserPhoneNumber()+"/"+payment.getUserFirebaseId()+"/groups/"+groupId+"/expenses/"+expenseKey).setValue(expenseForUser);
            mDatabaseRootRefenrence.child("users/"+payment.getUserPhoneNumber()+"/"+payment.getUserFirebaseId()+"/groups/"+groupId+"/timestamp").setValue(expenseForUser.getTimestamp());

            if(!(payment.getUserFirebaseId().equals(paidByFirebaseId))) {

                mDatabaseRootRefenrence.child("users/"+payment.getUserPhoneNumber()+"/"+payment.getUserFirebaseId()+"/groups/"+groupId+"/newExpenses").runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() == null) {
                            //no default value for data, set one
                            currentData.setValue(1L);
                        } else {
                            // perform the update operations on data
                            currentData.setValue(currentData.getValue(Long.class) + 1);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group

                    }
                });

                /*update users balances*/


                /*RIVEDERE*/
                mDatabaseRootRefenrence.child("groups/"+groupId+"/users/"+payment.getUserFirebaseId()+"/balances/"+paidByFirebaseId).runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() != null) {
                            Balance balance = currentData.getValue(Balance.class);
                            for(MutableData currentDataChild : currentData.getChildren()){
                                if(currentDataChild.getKey().equals("balance")) {
                                    currentDataChild.setValue(balance.getBalance() - Currency.convertCurrency(toUpdate.get(balance.getParentUserPhoneNumber() + expenseKey), currencyISO, balance.getCurrencyISO()));
                                }
                            }

                        }


                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group


                    }
                });

                mDatabaseRootRefenrence.child("groups/"+groupId+"/users/"+paidByFirebaseId+"/balances/"+payment.getUserFirebaseId()).runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() != null) {
                            if (currentData.getValue() != null) {
                                Balance balance = currentData.getValue(Balance.class);
                                for(MutableData currentDataChild : currentData.getChildren()){
                                    if(currentDataChild.getKey().equals("balance"))
                                        currentDataChild.setValue(balance.getBalance() + Currency.convertCurrency(toUpdate.get(balance.getUserPhoneNumber() + expenseKey), currencyISO, balance.getCurrencyISO()));
                                }

                            }
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group


                    }
                });

            }
        }

        mDatabaseRootRefenrence.child("groups/"+groupId+"/expenses/"+expenseKey).setValue(true);

        /*update the history*/
        HistoryInfo historyInfo = new HistoryInfo(paidByName+" "+paidBySurname, 0l, cost, currencyISO, null);
        mDatabaseRootRefenrence.child("history/"+groupId).push().setValue(historyInfo);

        return expenseKey;
    }

    public PaymentFirebase paymentFromUser(String userId){

        PaymentFirebase paymentToReturn = null;
        for(PaymentFirebase paymentFirebase : payments.values()){
            if(paymentFirebase.getUserFirebaseId().equals(userId)){
                paymentToReturn = paymentFirebase;
                break;
            }

        }

        return  paymentToReturn;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }


}
