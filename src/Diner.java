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
            System.out.println(Restaurant.printTime() + " - Diner " + diner_id + " arrives.");
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
            System.out.println(Restaurant.printTime() + " - Diner " + diner_id + " is seated at table " + tableSeated + ".");
        }
    }

    public void takeOrder() {
        synchronized (Restaurant.seatedDiners){
            Restaurant.seatedDiners.add(this);
            Restaurant.seatedDiners.notifyAll();
        }
    }

    public void waitOrder() throws InterruptedException{
        synchronized (this){
            this.wait();
        }
    }

    public void eat() throws InterruptedException {
        System.out.println(Restaurant.printTime() + " - Diner " + diner_id + "'s order is ready. " +
                "Diner "  + diner_id + " starts eating.");
        Thread.sleep((long) (timeToEat * Restaurant.time_scale));
    }


    public void leave() {
        System.out.println(Restaurant.printTime() + " - Diner " + diner_id + " finishes. " +
                "Diner " + diner_id + " leaves the restaurant.");

        synchronized(Restaurant.freeTables){
            Restaurant.freeTables.add(tableSeated);
            Restaurant.freeTables.notify();
        }
    }

    public void lastDinerLeaves() throws InterruptedException {
        synchronized(Restaurant.leftDiners){
            Restaurant.leftDiners++;
            if (Restaurant.leftDiners == Restaurant.num_diners){
                System.out.println(Restaurant.printTime() + " - The last diner leaves the restaurant.");
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
        try{
            arrive();
            takeSeat();
            takeOrder();
            waitOrder();
            eat();
            leave();
            lastDinerLeaves();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
