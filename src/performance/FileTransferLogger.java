package performance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class FileTransferLogger {
    private List<FileTransferRecord> records;

    public FileTransferLogger() {
        this.records = new ArrayList<>();
    }

    public void addRecord(String fileName, String destination, long duration) {
        FileTransferRecord record = new FileTransferRecord(fileName, destination, duration);
        records.add(record);
    }

    public void saveRecordsToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the header
            writer.append("FileName,Destination,Duration\n");

            // Write each record
            for (FileTransferRecord record : records) {
                writer.append(record.getFileName())
                        .append(",")
                        .append(record.getDestination())
                        .append(",")
                        .append(Long.toString(record.getDuration()))
                        .append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

