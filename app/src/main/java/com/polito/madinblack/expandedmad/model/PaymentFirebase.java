package com.polito.madinblack.expandedmad.model;

public class PaymentFirebase {
    private String id;
    private String userFirebaseId;
    private String userPhoneNumber;
    private String userNameDisplayed;
    private Double paid;
    private Double toPaid;

    //costruttore per il database
    public PaymentFirebase(){

    }

    public PaymentFirebase(Payment payment){
        this.userPhoneNumber = payment.getUserPhoneNumber();
        this.userFirebaseId  = payment.getUserFirebaseId();
        this.paid            = CostUtil.round(payment.getPaid(), 2);
        this.toPaid          = CostUtil.round(payment.getToPaid(), 2);
        this.userNameDisplayed = payment.getUserName();

    }

    public String getUserFirebaseId() {
        return userFirebaseId;
    }

    public void setUserFirebaseId(String userFirebaseId) {
        this.userFirebaseId = userFirebaseId;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserNameDisplayed() {
        return userNameDisplayed;
    }

    public void setUserNameDisplayed(String userNameDisplayed) {
        this.userNameDisplayed = userNameDisplayed;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = CostUtil.round(paid,2);
    }

    public Double getToPaid() {
        return toPaid;
    }


    public void setToPaid(Double toPaid) {
        this.toPaid = CostUtil.round(toPaid,2);
    }

    public Double getDebit(){


        if(paid < toPaid)
            return CostUtil.roundDown(toPaid-paid, 2);
        else
            return 0d;
    }

    public Double getCredit(){
        if(paid > toPaid)
            return CostUtil.roundDown(paid-toPaid, 2);
        else
            return 0d;

    }

    public Double getBalance(){
        return CostUtil.roundDown((paid-toPaid), 2);
    }




    @Override
    public String toString() {

        return String.format("%.2f",(paid))+"/"+String.format("%.2f",(toPaid));
    }
}
