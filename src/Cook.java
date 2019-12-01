public class Cook implements Runnable{

    public int cook_id;
    Diner dinerServed;

    public Cook(int cook_id){
        this.cook_id = cook_id;
    }

    public void processOrder() throws InterruptedException {
        synchronized (Restaurant.seatedDiners){
            while (Restaurant.seatedDiners.isEmpty()){
                Restaurant.seatedDiners.wait();
            }
            dinerServed = Restaurant.seatedDiners.remove();

            System.out.println(Restaurant.printTime() + " - Cook " + cook_id + " processes Diner "
                    + dinerServed.diner_id + "'s order.");
        }
    }

    private void cookItem(MachineType type, int quantity) throws InterruptedException{
        Order ord = dinerServed.order;
        synchronized (type){
            while (!Restaurant.getMachine(type).isFree){
                type.wait();
            }
            Restaurant.getMachine(type).isFree = false;
            long time_span = 0;
            if (type == MachineType.BURGER)
                time_span = ord.num_burgers * type.getPrepTime() / 1000;
            else if (type == MachineType.FRIES)
                time_span = ord.num_fries * type.getPrepTime() / 1000;
            else if (type == MachineType.COKE)
                time_span = ord.num_coke * type.getPrepTime() / 1000;
            Restaurant.getMachine(type).finishedTime = Restaurant.getTime() + time_span;
            System.out.println(Restaurant.printTime() + " - Cook " + cook_id + " uses the " + type.getName() + " machine.");
            Thread.sleep((long) (quantity * type.getPrepTime() * Restaurant.time_scale));
            if (type == MachineType.BURGER)
                ord.num_burgers = 0;
            else if (type == MachineType.FRIES)
                ord.num_fries = 0;
            else if (type == MachineType.COKE)
                ord.num_coke = 0;
            Restaurant.getMachine(type).isFree = true;
            Restaurant.getMachine(type).finishedTime = 0;
            type.notifyAll();
        }
    }

    public void makeOrder() throws InterruptedException {
        Order ord = dinerServed.order;
        if (Restaurant.getMachine(MachineType.BURGER).finishedTime <= Restaurant.getMachine(MachineType.FRIES).finishedTime){
            if (ord.num_burgers > 0){
                cookItem(Restaurant.typeBurger, ord.num_burgers);
            }
            if (ord.num_fries > 0){
                cookItem(Restaurant.typeFries, ord.num_fries);
            }
        } else {
            if (ord.num_fries > 0){
                cookItem(Restaurant.typeFries, ord.num_fries);
            }
            if (ord.num_burgers > 0){
                cookItem(Restaurant.typeBurger, ord.num_burgers);
            }
        }

        if (ord.num_coke > 0){
            cookItem(Restaurant.typeCoke, ord.num_coke);
        }

        synchronized (dinerServed) {
            dinerServed.notifyAll();
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                processOrder();
                makeOrder();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
