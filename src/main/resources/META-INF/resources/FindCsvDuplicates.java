package com.axinar.duplicates;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindCsvDuplicates {

    public static void main(String[] args) throws IOException {
        String inputFilePath = "/tmp/data.csv";

        // same on the col2 and col3
        Files.writeString(Path.of(inputFilePath), """
                header1,header2,header3
                col1,col2,col3
                col11,col22,col33
                col11,col2,col3
                col11,col2,col33
                col1,col13,col14""");

        String delimiter = ",";

        int[] columnsToCheck = {1, 2};

        Map<String, List<String>> duplicateGroups = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String header = reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(delimiter);

                StringBuilder duplicateKeyBuilder = new StringBuilder();
                for (int col : columnsToCheck) {
                    duplicateKeyBuilder.append(columns[col].trim()).append("|");
                }
                String duplicateKey = duplicateKeyBuilder.toString();

                duplicateGroups.computeIfAbsent(duplicateKey, k -> new ArrayList<>()).add(line);
            }

            System.out.println(header);
            for (Map.Entry<String, List<String>> duplicateGroup : duplicateGroups.entrySet()) {
                if (duplicateGroup.getValue().size() > 1) {
                    for (String duplicateLine : duplicateGroup.getValue()) {
                        System.out.println(duplicateLine);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
