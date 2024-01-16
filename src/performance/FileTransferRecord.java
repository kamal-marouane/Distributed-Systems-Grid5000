package performance;

public class FileTransferRecord {
    private String fileName;
    private String destination;
    private long duration; // duration in milliseconds

    public FileTransferRecord(String fileName, String destination, long duration) {
        this.fileName = fileName;
        this.destination = destination;
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDestination() {
        return destination;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "FileTransferRecord{" +
                "fileName='" + fileName + '\'' +
                ", destination='" + destination + '\'' +
                ", duration=" + duration +
                '}';
    }
}

