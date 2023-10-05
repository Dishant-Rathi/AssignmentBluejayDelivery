import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class EmployeeRecord {
    // Data fields for employee records
    String positionId;
    String positionStatus;
    Date time;
    Date timeOut;
    double timecardHours;
    String employeeName;

    // Constructor to initialize employee records
    public EmployeeRecord(String[] data) throws ParseException {
        this.positionId = data[0];
        this.positionStatus = data[1];
        this.time = data[2] != ""?new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(data[2]):null;
        this.timeOut = data[3] != ""?new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(data[3]):null;
        this.timecardHours = data[4] != ""?parseTimeToHours(data[4]):0.0;
        this.employeeName = data[7];
    }

    // Helper method to parse time in "hh:mm" format to hours as a double
    private double parseTimeToHours(String time) {
        String[] parts = time.split(":");
        double hours = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]);
        return hours + (minutes / 60.0);
    }
}

public class Assignment_Timecard {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\welcome\\Desktop\\Assignment_Timecard.csv"; // Replace with your file path

        try {
            List<EmployeeRecord> records = readEmployeeData(filePath);
            analyzeAndPrintRecords(records);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static List<EmployeeRecord> readEmployeeData(String filePath) throws IOException, ParseException {
        List<EmployeeRecord> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Skip the header line
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            EmployeeRecord record = new EmployeeRecord(data);
            records.add(record);
        }

        reader.close();
        return records;
    }

    private static void analyzeAndPrintRecords(List<EmployeeRecord> records) {
        Map<String,String> rec = new HashMap<>();
        for (int i = 0; i < records.size(); i++) {
            EmployeeRecord currentRecord = records.get(i);

            // Criteria a) Check for 7 consecutive days
            boolean consecutiveDays = checkConsecutiveDays(records, currentRecord, 7);

            // Criteria b) Check for less than 10 hours between shifts but greater than 1 hour
            boolean shortBreaks = checkShortBreaks(records, currentRecord);

            // Criteria c) Check for more than 14 hours in a single shift
            boolean longShift = currentRecord.timecardHours > 14;

            // Print if any criteria are met
            if (consecutiveDays || shortBreaks || longShift) {
                rec.put(currentRecord.employeeName, currentRecord.positionId);
                //System.out.println("Employee: " + currentRecord.employeeName + ", Position: " + currentRecord.positionId);
            }
        }
        for (Map.Entry mp : rec.entrySet()) {
            System.out.println("Employee: " + mp.getKey() + ", Position: " + mp.getValue());
        }
    }

    private static boolean checkConsecutiveDays(List<EmployeeRecord> records, EmployeeRecord currentRecord, int days) {
        int consecutiveDaysCount = 0;

        for (int i = records.indexOf(currentRecord); i < records.size(); i++) {
            EmployeeRecord record = records.get(i);

            if (currentRecord.employeeName.equals(record.employeeName) &&
                    record.positionStatus.equals("Active") &&
                    isConsecutiveDay(currentRecord.time, record.time)) {
                consecutiveDaysCount++;
            } else {
                break;
            }

            if (consecutiveDaysCount == days) {
                return true;
            }
        }

        return false;
    }

    private static boolean isConsecutiveDay(Date date1, Date date2) {
        if(date1 != null && date2 != null)
        {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        }
        return false;
    }

    private static boolean checkShortBreaks(List<EmployeeRecord> records, EmployeeRecord currentRecord) {
        for (int i = records.indexOf(currentRecord) + 1; i < records.size(); i++) {
            EmployeeRecord nextRecord = records.get(i);
            
            if (currentRecord.employeeName.equals(nextRecord.employeeName) &&
                    nextRecord.positionStatus.equals("Active")) {
                    if(currentRecord.timeOut != null && nextRecord.time != null)
                    {
                        long timeDifference = nextRecord.time.getTime() - currentRecord.timeOut.getTime();
                        double hoursDifference = timeDifference / (1000.0 * 60 * 60);

                        if (hoursDifference > 1 && hoursDifference < 10) {
                            return true;
                        }
                    }
                
            } else {
                break;
            }
        }

        return false;
    }
}
