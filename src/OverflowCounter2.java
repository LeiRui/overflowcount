import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverflowCounter2{
    public static void main(String[] args) throws  Exception{
        File folder = new File("/home/giant/task9/data");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> files = new ArrayList<>();
        for(int i = 0; i < listOfFiles.length; i++) {
            files.add(listOfFiles[i].getName());
        }
        Collections.sort(files);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Map<String, DeviceOverflow> devices = new HashMap<>();
        for(int j=0; j<files.size();j++) {
            Map<String, DeviceOverflow> devices_singlefile = new HashMap<>();
            System.out.println(files.get(j));
            BufferedReader reader = new BufferedReader(new FileReader("/home/giant/task9/data/"+files.get(j)));
            String line;
            Date date;
            String[] items, fields;
            String deviceId;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                items = line.split("\\|");
                deviceId = items[3];
                fields = items[4].split(",");
                try {
                    date = simpleDateFormat.parse(fields[0]);
                }catch (Exception e){ //only care about realtimedata
                    e.printStackTrace();
                    continue;
                }
                long time = date.getTime();

                if (devices.containsKey(deviceId)) { // maintain all files
                    devices.get(deviceId).insert(time);
                } else {
                    DeviceOverflow device = new DeviceOverflow(deviceId, time);
                    devices.put(deviceId, device);
                }

                if (devices_singlefile.containsKey(deviceId)) { // maintain a single file
                    devices_singlefile.get(deviceId).insert(time);
                } else {
                    DeviceOverflow device = new DeviceOverflow(deviceId, time);
                    devices_singlefile.put(deviceId, device);
                }
            }
            reader.close();


            // write a single file
            String outputPath_proportion="correct/singlefile/"+files.get(j)+"_proportion.csv";
            PrintStream out_proportion = new PrintStream(outputPath_proportion);
            out_proportion.println("deviceID,total points,overflow points,overflow percentage, overflow time(ms)");
            String outputPath_histogram = "correct/singlefile/"+files.get(j)+"_histogram_unit_10min.csv";
            PrintStream out_histogram = new PrintStream(outputPath_histogram);
            out_histogram.println(",=\"[\"&(COLUMN(B1)-2)*10&\"..\"&(COLUMN(B1)-1)*10&\")\"");
            SortedSet<String> keys = new TreeSet<String>(devices_singlefile.keySet());
            for (String key : keys) {
                DeviceOverflow device = devices_singlefile.get(key);
                out_proportion.println(device.deviceID + "," + device.number + "," + device.ofnumber + "," + ((double) device.ofnumber / device.number)
                        + "," + device.sum());
                out_histogram.print(device.deviceID+",");
                int[] histogram = device.getHistogram(new Long(600000));
                if(histogram!=null) {
                    for (int k = 0; k < histogram.length; k++) {
                        out_histogram.print(histogram[k] + ",");
                    }
                }
                out_histogram.println("");
            }
            out_proportion.close();
            out_histogram.close();
        }

        // write all files
        String outputPath_proportion="correct/allfiles/proportion.csv";
        PrintStream out_proportion = new PrintStream(outputPath_proportion);
        out_proportion.println("deviceID,total points,overflow points,overflow percentage, overflow time(ms)");
        String outputPath_histogram = "correct/allfiles/histogram_unit_10min.csv";
        PrintStream out_histogram = new PrintStream(outputPath_histogram);
        out_histogram.println(",=\"[\"&(COLUMN(B1)-2)*10&\"..\"&(COLUMN(B1)-1)*10&\")\"");
        SortedSet<String> keys = new TreeSet<String>(devices.keySet());
        for (String key : keys) {
            DeviceOverflow device = devices.get(key);
            out_proportion.println(device.deviceID + "," + device.number + "," + device.ofnumber + "," + ((double) device.ofnumber / device.number)
                    + "," + device.sum());
            out_histogram.print(device.deviceID+",");
            int[] histogram = device.getHistogram(new Long(600000));
            if(histogram!=null) {
                for (int k = 0; k < histogram.length; k++) {
                    out_histogram.print(histogram[k] + ",");
                }
            }
            out_histogram.println("");
        }
        out_proportion.close();
        out_histogram.close();
    }
}
