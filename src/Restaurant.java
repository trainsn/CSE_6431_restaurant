import java.io.*;
import java.util.*;

public class Restaurant {
    public static ArrayList<Integer> freeTables;
    public static PriorityQueue<Diner> nonSeatedDiners;
    public static PriorityQueue<Diner> seatedDiners; // take seats, but order not processed.

    public static int num_diners, num_tables, num_cooks;
    public static long startTime;
    public static Integer leftDiners = 0;

    public static Machine burgerMaker = new Machine(MachineType.BURGER);
    public static Machine friesMaker = new Machine(MachineType.FRIES);
    public static Machine cokeMaker = new  Machine(MachineType.COKE);
    public static MachineType typeBurger, typeFries, typeCoke;

    public static float time_scale = (float) 2.0;

    static public Machine getMachine(MachineType type) throws InterruptedException{
        switch (type){
            case BURGER:
                return burgerMaker;
            case FRIES:
                return friesMaker;
            case COKE:
                return cokeMaker;
        }
        return null;
    }

    public static long getTime(){
        long currentTime = System.currentTimeMillis() / 1000;
        long timeSpent = (long) ((currentTime - startTime) / time_scale);

        return timeSpent;
    }

    public static String printTime(){
        long timeSpent = getTime();

        long hour = timeSpent / 60;
        String hour_str = String.format("%02d", hour);
        long min = timeSpent % 60;
        String min_str = String.format("%02d", min);

        return (hour_str + ":" + min_str);
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        String input_file = args[0];
        FileReader fRead = new FileReader(input_file);
        BufferedReader bRead = new BufferedReader(fRead);

        num_diners = Integer.parseInt(bRead.readLine().trim());
        num_tables = Integer.parseInt(bRead.readLine().trim());
        num_cooks = Integer.parseInt(bRead.readLine().trim());

        freeTables = new ArrayList<Integer>(num_tables);
        for (int i = 0; i < num_tables; i++) {
            freeTables.add(i + 1);
        }

        startTime = System.currentTimeMillis() / 1000;
        int preArrival = 0;

        nonSeatedDiners = new PriorityQueue<>();
        seatedDiners = new PriorityQueue<>();
        typeBurger = MachineType.BURGER;
        typeFries = MachineType.FRIES;
        typeCoke = MachineType.COKE;

        // threads for cooks
        for (int i = 0; i < num_cooks; i++){
            Cook ck = new Cook(i+1);
            Thread cookThread = new Thread(ck);
            cookThread.start();
        }

        // threads for diners
        for (int i = 0; i < num_diners; i++){
            String line = bRead.readLine();
            String[] diner_info = line.split(",");
            int arrivalTime = Integer.parseInt(diner_info[0]);

            Order ord = new Order(Integer.parseInt(diner_info[1]),
                    Integer.parseInt(diner_info[2]),
                    Integer.parseInt(diner_info[3]));

            Diner dnr = new Diner(i + 1, arrivalTime, ord);

            Thread dinerThread = new Thread(dnr);
            int waitTime = 1000 * (dnr.arrivalTime - preArrival);
            Thread.sleep((long) (waitTime * time_scale));
            dinerThread.start();
            preArrival = dnr.arrivalTime;
        }

        bRead.close();
        fRead.close();
    }
}
