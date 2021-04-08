package com.rukevwe.lend.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class Loan {

    private long application_id;
    private long customer_id;
    private double principal;
    private double fee;
    private Date disbursement_date;
    private List<Repayment> repayments;
    private double netAmount;
    private boolean isLoanIncomplete;

    public long getApplication_id() {
        return application_id;
    }

    public void setApplication_id(long application_id) {
        this.application_id = application_id;
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Date getDisbursement_date() {
        return disbursement_date;
    }

    public void setDisbursement_date(Date disbursement_date) {
        this.disbursement_date = disbursement_date;
    }

    public List<Repayment> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<Repayment> repayments) {
        this.repayments = repayments;
    }

    public double getNetAmount() {
        double netAmount = 0;
        for (Repayment repayment: repayments) {
            netAmount += repayment.getAmount();
        }
        return netAmount - principal;
    }

    public Date getFinalRepaymentDate() {
        List<Repayment> repayments = getRepayments();
        TreeMap<Date, Double> loanDateMap = new TreeMap<>(Comparator.naturalOrder());
        for (Repayment repayment : repayments) {
            loanDateMap.put(repayment.getDate(), repayment.getAmount());
        }
        return loanDateMap.lastKey();
    }

    public double getAmountRepaid() {
        List<Repayment> repayments = getRepayments();
        double amount = 0 ;
        for (Repayment repayment : repayments) {
            amount += repayment.getAmount() ;
        }
        return amount;
    }

}
