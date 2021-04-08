package com.rukevwe.lend.service;

import com.rukevwe.lend.model.Loan;
import com.rukevwe.lend.model.Repayment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanProcessorService {

    private static final Logger logger = Logger.getLogger(LoanProcessorService.class.getName());
    private Set<Long> activeLoanCustomerId = new HashSet<>();
    private Set<Loan> activeLoans = new HashSet<>();
    private Set<Loan> tempActiveLoans = new HashSet<>();
    private double cashAtHand;
    private TreeSet<Long> loanApplicationIds = new TreeSet<>();
    private long maxNumberOfActiveLoans;
    private String inputFilePath;
    private String outputFilePath;


    public LoanProcessorService(String inputFilePath, String outputFilePath, double startingCapital, long maxNumberOfActiveLoans) {
        this.cashAtHand = startingCapital;
        this.maxNumberOfActiveLoans = maxNumberOfActiveLoans;
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;

        processLoans();
    }

    public void processLoans() {

        FileService fileService = new FileService();

        Loan[] loans = fileService.getLoansFromInputPath(inputFilePath);

        Set<Loan> filteredLoans = filterLoans(loans);

        processFilteredLoans(filteredLoans);

        fileService.writeToOutputFile(loanApplicationIds,  outputFilePath);

        logger.log(Level.INFO, loanApplicationIds.toString());

    }

    public void processFilteredLoans(Set<Loan> filteredLoans) {

        Map<Date, Set<Loan>> orderedLoans = orderLoansByDate(filteredLoans);

        for (Map.Entry<Date, Set<Loan>> orderedLoan : orderedLoans.entrySet()) {
            processDailyLoans(orderedLoan.getKey(), orderedLoan.getValue());
        }

        removeRepaidLoans(lastDayOfYear());
    }

    // we want to give as much loans as possible while staying within our limits
    // so maximize fees while reducing principal and picking closest repayment date

    public void processDailyLoans(Date date, Set<Loan> loans) {
        removeRepaidLoans(date);

        if (activeLoans.size() < maxNumberOfActiveLoans) {

            TreeSet<Loan> loanTreeSet = orderLoansByRepaymentDateFeeAndPrincipal(loans);
            for (Loan loan : loanTreeSet) {
                if (!activeLoanCustomerId.contains(loan.getCustomer_id()) && isCashAtHandSufficient(loan.getPrincipal())) {
                    activeLoans.add(loan);
                    activeLoanCustomerId.add(loan.getCustomer_id());
                    cashAtHand -= loan.getPrincipal();
                    loanApplicationIds.add(loan.getApplication_id());
                }
                if (activeLoans.size() > maxNumberOfActiveLoans) {
                    break;
                }
            }
        }

    }

    public TreeSet<Loan> orderLoansByRepaymentDateFeeAndPrincipal(Set<Loan> loans) {
        TreeSet<Loan> loanSet = new TreeSet<Loan>((o1, o2) -> {
            if (o1.getFinalRepaymentDate().equals(o2.getFinalRepaymentDate())) {
                if (o1.getFee() == o2.getFee()) {
                    return (int) (o1.getPrincipal() - o2.getPrincipal());
                }
                return (int) (o2.getFee() - o1.getFee());
            }
            return o1.getFinalRepaymentDate().compareTo(o2.getFinalRepaymentDate());
        });

        loanSet.addAll(loans);
        return loanSet;
    }

    public void removeRepaidLoans(Date date) {
        for (Loan loan : activeLoans) {
            if (loan.getFinalRepaymentDate().compareTo(date) < 0) {
               tempActiveLoans.add(loan);
               activeLoanCustomerId.remove(loan.getCustomer_id());
               cashAtHand += loan.getAmountRepaid();
            }
        }
        activeLoans.removeAll(tempActiveLoans);
        tempActiveLoans = new HashSet<>();
    }

    public boolean isCashAtHandSufficient(double principal) {
        return cashAtHand > principal;
    }

    public Map<Date, Set<Loan>> orderLoansByDate(Set<Loan> filteredLoans) {
        Map<Date, Set<Loan>> loanDateMap = new TreeMap<>(Comparator.naturalOrder());

        filteredLoans.stream().forEach(filteredLoan -> {
            if (!loanDateMap.containsKey(filteredLoan.getDisbursement_date())) {
                loanDateMap.put(filteredLoan.getDisbursement_date(), new HashSet<>());
            }
            loanDateMap.get(filteredLoan.getDisbursement_date()).add(filteredLoan);

        });
        return loanDateMap;
    }


    public long getDurationBetweenDays(Date date1, Date date2) {
        return Duration.between(date1.toInstant(), date2.toInstant())
                .abs().toDays();
    }

    public Set<Loan> filterLoans(Loan[] loans) {
        Set<Loan> loanSet = new HashSet<>();
        for (int i = 0; i < loans.length; i++) {
            Loan loan = loans[i];
            double principal = loan.getPrincipal();
            double amount = 0;
            for (Repayment repayment : loan.getRepayments()) {
                if (repayment.getDate().before(lastDayOfYear())) {
                    amount += repayment.getAmount();
                }
            }
            double netAmount = amount - principal;
            if (netAmount < 0 || getDurationBetweenDays(loan.getDisbursement_date(), loan.getFinalRepaymentDate()) > 90) {
                continue;
            }
            loanSet.add(loan);
        }
        return loanSet;
    }

    public Date lastDayOfYear() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-31");
        } catch (ParseException ex) {
            logger.log(Level.SEVERE, "Error parsing date");
        }
        return null;
    }

}
