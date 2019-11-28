import java.awt.print.PrinterGraphics;
import java.io.*;
import java.util.*;

public class Restaurant {

    //  Variable for 4 types

    public static void main(String args[]) throws IOException{
        String input = args[0];
        FileReader fRead = new FileReader(input);
        BufferedReader bRead = new BufferedReader(fRead);

        // scanning the input
        diners_count = Interger.parseInt(bRead.readLine().trim());
        tables_count = Interger.parseInt(bRead.readLine().trim());
        cooks_count = Interger.parseInt(bRead.readLine().trim());

        // start the timer
        startTime = System.currentTimeMillis() / 1000;

        // Add tables
        freeTables = new ArrayList<Interger>(tables_count);
        for (int i = 0; i < tables_count; i++) {
            freeTables.add(i + 1);
        }

        // threads for cooks
        for (int i = 0; i < cooks_count; i++){
            Cook ck = new Cook();
            ck.cook_id = i + 1;
            Thread cookThread = new Thread(ck);
            cookThread.start();
        }

        diners = new ArrayList<Diner>(diners_count);
        hungryDiners = new ArrayList<Diner>(diners_count);
        for (int i = 0; i < diners_count; i++){
            String line = bRead.readLine();
            String[] diner_info = line.split(",");
            arrivalTime = Integer.parseInt(diner_info[0]);

            Order ord = new Order();

            ord.bugers_count = Interger.parseInt(diner_info[1]);
            ord.fries_count = Interger.parseInt(diner_info[2]);
            ord.coke_count = Integer.parseInt(diner_info[3]);


            Diner dnr = new Diner();
            dnr.diner_id = i + 1;
            dnr.arrivalTime = arrivalTime;
            dnr.order = ord;

            diners.add(dnr);
        }

        // threads for diners
        int preArrival = 0;
        for (int i = 0; i < diners_count; i++){
            Diner dnr = diners[i];
            Threads dinerThread = new Thread(dnr);
            int waitTime = 1000 * (dnr.arrivalTime - preArrival);
            dinerThread.sleep(waitTime);
            dinerThread.start();
            preArrival = dnr.arrivalTime;
        }

        bRead.close();
        fRead.close();

    }
}
