public class Diner implements Runnable, Comparable<Diner>{

    public int diner_id;
    public int arrivalTime;
    public Order order;
    public int tableSeated;
    static int timeToEat = 30000;

    public Diner(int diner_id, int arrivalTime, Order order){
        this.diner_id = diner_id;
        this.arrivalTime = arrivalTime;
        this.order = order;
    }

    public void arrive(){
        synchronized (Restaurant.nonSeatedDiners){
            System.out.println(getTime() + " - Diner " + diner_id + " arrives.");
            Restaurant.nonSeatedDiners.add(this);
        }
    }

    public void takeSeat() throws InterruptedException{
        synchronized (Restaurant.freeTables){
            while (Restaurant.freeTables.isEmpty() ||
                    this.diner_id != Restaurant.nonSeatedDiners.element().diner_id){
                Restaurant.freeTables.wait();
            }
            tableSeated = Restaurant.freeTables.remove(0);
            Restaurant.nonSeatedDiners.remove();
            System.out.println(getTime() + " - Diner " + diner_id + " is seated at table " + tableSeated + ".");
        }
    }

    public void takeOrder() {
        synchronized (Restaurant.hungryDiners){
            Restaurant.hungryDiners.add(this);
            Restaurant.hungryDiners.notifyAll();
        }
    }

    public void waitOrder() throws InterruptedException{
        synchronized (this){
            this.wait();
        }
    }

    public void eat() throws InterruptedException {
        System.out.println(getTime() + " - Diner " + diner_id + "'s order is ready. " +
                "Diner "  + diner_id + " starts eating.");
        Thread.sleep(timeToEat);
    }


    public void leave() {
        System.out.println(getTime() + " - Diner " + diner_id + " finishes. " +
                "Diner " + diner_id + " leaves the restaurant.");

        synchronized(Restaurant.freeTables){
            Restaurant.freeTables.add(tableSeated);
            Restaurant.freeTables.notify();
        }
    }

    public void lastDinerLeaves() throws InterruptedException {
        synchronized(Restaurant.leftDiners){
            Restaurant.leftDiners++;
            if (Restaurant.leftDiners == Restaurant.diners_count){
                System.out.println(getTime() + " - The last diner leaves the restaurant.");
                System.exit(0);
            }
        }
    }

    public int compareTo(Diner o){
        if (arrivalTime >  o.arrivalTime)
            return 1;
        else if (arrivalTime < o.arrivalTime)
            return -1;
        return 0;
    }

    @Override
    public void run(){
        arrive();

        try{
            takeSeat();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        takeOrder();

        try {
            waitOrder();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        try {
            eat();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        leave();

        try {
            lastDinerLeaves();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getTime(){
        long currentTime = System.currentTimeMillis() / 1000;
        long timeSpent = currentTime - Restaurant.startTime;

        long hour = timeSpent / 60;
        String hour_str = String.format("%02d", hour);
        long min = timeSpent % 60;
        String min_str = String.format("%02d", min);

        return (hour_str + ":" + min_str);
    }
}
