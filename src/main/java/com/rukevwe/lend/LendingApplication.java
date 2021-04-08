package com.rukevwe.lend;

import com.rukevwe.lend.service.LoanProcessorService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LendingApplication {

    private static final Logger logger = Logger.getLogger(LendingApplication.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Start processing");

        try {
            if (args.length < 4) {
                logger.log(Level.INFO, "invalid number of arguments");
                return;
            }

            String inputFilePath = args[0];
            String outputFilePath = args[1];

            double startingCapital = Double.valueOf(args[2]);
            long maxNumberOfActiveLoans = Long.valueOf(args[3]);

            LoanProcessorService loanProcessorService = new LoanProcessorService(inputFilePath, outputFilePath, startingCapital, maxNumberOfActiveLoans);

            logger.log(Level.INFO, "Finished processing");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Encountered error while processing");
            ex.printStackTrace();
        }

    }

}
