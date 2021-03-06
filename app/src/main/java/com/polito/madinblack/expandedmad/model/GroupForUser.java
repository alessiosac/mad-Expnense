package com.polito.madinblack.expandedmad.model;


/**
 * Created by Ale on 26/04/2017.
 */

public class GroupForUser {


    private String  name;
    private String  id;
    private Long    size;
    private Long    newExpenses;
    private Long    newMessages;
    private Long    contestedExpensesCounter;
    private Long    timestamp;

    public GroupForUser(){

    }

    public GroupForUser(Group group){
        this.name        = group.getName();
        this.size        = group.getSize();
        this.id          = group.getId();
        this.newExpenses = 0L;
        this.newMessages = 0L;
        this.contestedExpensesCounter = 0L;

    }

    public GroupForUser(String name, String id, Long size, Long newExpenses) {
        this.name = name;
        this.id = id;
        this.size = size;
        this.newExpenses = newExpenses;
        this.newMessages = 0L;
        this.contestedExpensesCounter = 0L;
    }

    public String getName(){ return name; }

    public String getId(){
        return id;
    }

    public Long   getSize(){ return size; }

    public Long   getNewExpenses() { return newExpenses; }


    public void   setName(String name) { this.name = name; }

    public void   setId(String id) { this.id = id; }

    public void   setSize(Long size) { this.size = size; }

    public void   setNewExpenses(Long newExpenses) { this.newExpenses = newExpenses; }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = -1*System.currentTimeMillis();
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getContestedExpensesCounter() {
        return contestedExpensesCounter;
    }

    public void setContestedExpensesCounter(Long contestedExpensesCounter) {
        this.contestedExpensesCounter = contestedExpensesCounter;
    }

    public Long getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(Long newMessages) {
        this.newMessages = newMessages;
    }
}
