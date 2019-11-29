public class Cook implements Runnable{

    public int cook_id;
    Diner dinerServed;

    public Cook(int cook_id){
        this.cook_id = cook_id;
    }

    public void processOrder() throws InterruptedException {
        synchronized (Restaurant.hungryDiners){
            while (Restaurant.hungryDiners.isEmpty()){
                Restaurant.hungryDiners.wait();
            }
            dinerServed = Restaurant.hungryDiners.remove();

            Order ord = dinerServed.order;
            if (ord.burgers_count > 0)
                Restaurant.getMachine(Restaurant.typeBurger).diners.add(dinerServed);
            if (ord.fries_count > 0)
                Restaurant.getMachine(Restaurant.typeFries).diners.add(dinerServed);
            if (ord.coke_count > 0)
                Restaurant.getMachine(Restaurant.typeCoke).diners.add(dinerServed);

            System.out.println(Restaurant.printTime() + " - Cook " + cook_id + " processes Diner "
                    + dinerServed.diner_id + "'s order.");
        }
    }

    private void cookItem(MachineType type, int quantity) throws InterruptedException{
        Order ord = dinerServed.order;
        synchronized (type){
            while (!Restaurant.getMachine(type).isFree ||
                    dinerServed.diner_id != Restaurant.getMachine(type).diners.element().diner_id){
                type.wait();
            }
            Restaurant.getMachine(type).isFree = false;
            System.out.println(Restaurant.printTime() + " - Cook " + cook_id + " uses the " + type.getName() + " machine.");
            Thread.sleep((long) (quantity * type.getPrepTime() * Restaurant.time_scale));
            if (type == MachineType.BURGER)
                ord.burgers_count = 0;
            else if (type == MachineType.FRIES)
                ord.fries_count = 0;
            else if (type == MachineType.COKE)
                ord.coke_count = 0;
            Restaurant.getMachine(type).diners.remove();
            Restaurant.getMachine(type).isFree = true;
            type.notifyAll();
        }
    }

    public void makeOrder() throws InterruptedException {
        Order ord = dinerServed.order;
        if (ord.burgers_count > 0){
            cookItem(Restaurant.typeBurger, ord.burgers_count);
        }

        if (ord.fries_count > 0){
            cookItem(Restaurant.typeFries, ord.fries_count);
        }

        if (ord.coke_count > 0){
            cookItem(Restaurant.typeCoke, ord.coke_count);
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
