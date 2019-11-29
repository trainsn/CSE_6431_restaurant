import java.io.*;
import java.util.*;

public class Restaurant {
    public static ArrayList<Integer> freeTables;
    public static PriorityQueue<Diner> nonSeatedDiners;
    public static PriorityQueue<Diner> hungryDiners; // take seats, but order not processed.
    public static PriorityQueue<Diner> burgerDiners; // wait before burger maker
    public static PriorityQueue<Diner> friesDiners; // wait before fries maker
    public static PriorityQueue<Diner> cokeDiners; // wait before coke maker

    public static int diners_count, tables_count, cooks_count;
    public static long startTime;
    public static Integer leftDiners = 0;

    public static Machine burgerMaker = new Machine(MachineType.BURGER);
    public static Machine friesMaker = new Machine(MachineType.FRIES);
    public static Machine cokeMaker = new  Machine(MachineType.COKE);
    public static MachineType typeBurger, typeFries, typeCoke;

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

    public static void main(String args[]) throws IOException, InterruptedException {
        String input = args[0];
        FileReader fRead = new FileReader(input);
        BufferedReader bRead = new BufferedReader(fRead);

        // scanning the input
        diners_count = Integer.parseInt(bRead.readLine().trim());
        tables_count = Integer.parseInt(bRead.readLine().trim());
        cooks_count = Integer.parseInt(bRead.readLine().trim());

        // Add tables
        freeTables = new ArrayList<Integer>(tables_count);
        for (int i = 0; i < tables_count; i++) {
            freeTables.add(i + 1);
        }

        // start the timer
        startTime = System.currentTimeMillis() / 1000;
        int preArrival = 0;
        nonSeatedDiners = new PriorityQueue<>();
        hungryDiners = new PriorityQueue<>();
        burgerDiners = new PriorityQueue<>();
        friesDiners = new PriorityQueue<>();
        cokeDiners = new PriorityQueue<>();
        typeBurger = MachineType.BURGER;
        typeFries = MachineType.FRIES;
        typeCoke = MachineType.COKE;

        // threads for cooks
        for (int i = 0; i < cooks_count; i++){
            Cook ck = new Cook(i+1);
            Thread cookThread = new Thread(ck);
            cookThread.start();
        }

        // threads for diners
        for (int i = 0; i < diners_count; i++){
            String line = bRead.readLine();
            String[] diner_info = line.split(",");
            int arrivalTime = Integer.parseInt(diner_info[0]);

            Order ord = new Order(Integer.parseInt(diner_info[1]),
                    Integer.parseInt(diner_info[2]),
                    Integer.parseInt(diner_info[3]));

            Diner dnr = new Diner(i + 1, arrivalTime, ord);


            Thread dinerThread = new Thread(dnr);
            int waitTime = 1000 * (dnr.arrivalTime - preArrival);
//            System.out.println(waitTime);
            Thread.sleep(waitTime);
            dinerThread.start();
            preArrival = dnr.arrivalTime;
        }

        bRead.close();
        fRead.close();
    }
}
