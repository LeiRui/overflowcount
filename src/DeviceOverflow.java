import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceOverflow {
    public String deviceID;
    public long currTime;
    public int number;
    public int ofnumber;
    public ArrayList<Long> overflows;
    DeviceOverflow(String ID, long time) {
        deviceID=ID;
        currTime=time;
        overflows=new ArrayList<Long>();
        number=1;
        ofnumber=0;
    }

    public void insert(long time) {
        if(time>=currTime){
            currTime=time; // update currTime
        }
        else { // time < currTime
            Long delta = currTime-time;
            //System.out.println(delta/60000);
            overflows.add(delta);
            ofnumber++;
        }
        number++;
    }

    public Long sum() {
        long res=0;
        for(Long delta: overflows) {
            res+=delta;
        }
        return res;
    }

    public int[] getHistogram(Long interval) { // unit: milliseconds
        if(overflows.size()==0)
            return null;
        Long max = Collections.max(overflows);
        int steps = (int)Math.ceil(((double)(max+1))/interval);
        int[] histogram = new int[steps];
        for(Long delta: overflows){
            int num = (int)Math.floor(((double)delta)/interval);
            histogram[num]++;
        }
        return histogram;
    }
}
