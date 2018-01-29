import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverflowCounter{
    public static void main(String[] args) throws  Exception{
        //String path="data/1-1511232886069.bzip2.complete.out";
        File folder = new File("/home/giant/task9/data");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> files = new ArrayList<>();
        for(int i = 0; i < listOfFiles.length; i++) {
            files.add(listOfFiles[i].getName());
        }
        Collections.sort(files);
        String output1 = "correct/res/proportion.csv";
        String output2 = "correct/res/histogram.csv";
        String output3 = "correct/res/fileNames.csv";
        PrintStream out_1 = new PrintStream(output1);
        PrintStream out_2 = new PrintStream(output2);
        PrintStream out_3 = new PrintStream(output3);
        out_1.print("fileID,total points,overflow points,overflow time(ms)");
        out_1.println("");

        for(int j=0; j<files.size();j++) {
            System.out.println(files.get(j));
            BufferedReader reader = new BufferedReader(new FileReader("/home/giant/task9/data/"+files.get(j)));
            String line;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date;
            String[] items, fields;
            String deviceId;
            Map<String, DeviceOverflow> devices = new HashMap<String, DeviceOverflow>();
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                items = line.split("\\|");
                deviceId = items[3];
                fields = items[4].split(",");
                try {
                    date = simpleDateFormat.parse(fields[0]);
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
                long time = date.getTime();

                if (devices.containsKey(deviceId)) {
                    devices.get(deviceId).insert(time);
                } else {
                    DeviceOverflow device = new DeviceOverflow(deviceId, time);
                    devices.put(deviceId, device);
                }
            }
            reader.close();


            String outputPath = "correct/"+files.get(j)+"_proportion.csv";
            PrintStream out = new PrintStream(outputPath);
            out.print("deviceID,total points,overflow points,overflow percentage, overflow time(ms)");
            out.println("");
            Long total = new Long(0);
            SortedSet<String> keys = new TreeSet<String>(devices.keySet());
            int file_number=0;
            int file_ofnumber=0;
            double file_proportion=0;
            Long file_sum=new Long(0);
            for (String key : keys) {
                DeviceOverflow device = devices.get(key);
                total += device.sum();
                out.print(device.deviceID + "," + device.number + "," + device.ofnumber + "," + ((double) device.ofnumber / device.number)
                        + "," + device.sum());
                out.println("");
                file_number+=device.number;
                file_ofnumber+=device.ofnumber;
                file_sum+=device.sum();
            }
            out.close();
            out_1.print(files.get(j)+","+file_number+","+file_ofnumber+","+file_sum);
            out_1.println("");


            String outputPath3 = "correct/"+files.get(j)+"_histogram_unit_10min.csv";
            PrintStream out3 = new PrintStream(outputPath3);
            out3.println(",=\"[\"&(COLUMN(B1)-2)*10&\"..\"&(COLUMN(B1)-1)*10&\")\"");
            ArrayList<Integer> file_count=new ArrayList<>();
            for (String key : keys) {
                DeviceOverflow device = devices.get(key);
                //1h=60min=3600s=3600000ms,1m=60s=60000ms,10m=600000ms,30m=1800000ms
                out3.print(device.deviceID+",");
                int[] histogram = device.getHistogram(new Long(600000));
                if(histogram!=null) {
                    for (int k = 0; k < histogram.length; k++) {
                        out3.print(histogram[k] + ",");
                        if (file_count.size() < k + 1) {
                            file_count.add(histogram[k]);
                        } else {
                            file_count.set(k, file_count.get(k) + histogram[k]);
                        }
                    }
                }
                out3.println("");
            }
            out3.close();
            for(int k=0; k<file_count.size();k++) {
                out_2.print(file_count.get(k)+",");
            }
            out_2.println("");


            out_3.println(files.get(j));
        }
        out_1.close();
        out_2.close();
        out_3.close();
    }
}
