package com.rukevwe.lend.service;

import com.google.gson.Gson;
import com.rukevwe.lend.model.Loan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileService {

    private static final Logger logger = Logger.getLogger(FileService.class.getName());

    public Loan[] getLoansFromInputPath(String inputFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            Loan[] loans = new Gson().fromJson(br, Loan[].class);
            return loans;
        } catch (IOException e) {
            logger.log(Level.INFO, "file not found");
            e.printStackTrace();
        }
        return null;
    }

    public void writeToOutputFile(TreeSet<Long> loanApplicationIds, String outputFilePath) {

        Path filePath = Paths.get(outputFilePath);
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);

            try (FileWriter writer = new FileWriter(outputFilePath, true)) {
                for (Long value : loanApplicationIds) {
                    writer.write(value + "\n");
                }
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Error outputting to file");
            e.printStackTrace();
        }
    }

}
