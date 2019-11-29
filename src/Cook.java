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
            if (ord.bugers_count > 0)
                Restaurant.burgerDiners.add(dinerServed);
            if (ord.fries_count > 0)
                Restaurant.friesDiners.add(dinerServed);
            if (ord.coke_count > 0)
                Restaurant.cokeDiners.add(dinerServed);

            System.out.println(getTime() + " - Cook " + cook_id + " processes Diner "
                    + dinerServed.diner_id + "'s order.");
        }
    }

    public void makeOrder() throws InterruptedException {
        Order ord = dinerServed.order;
        while (ord.bugers_count > 0){
            synchronized (Restaurant.typeBurger){
                while (!Restaurant.getMachine(Restaurant.typeBurger).isFree ||
                        dinerServed.diner_id != Restaurant.burgerDiners.element().diner_id){
                    Restaurant.typeBurger.wait();
                }
                Restaurant.getMachine(Restaurant.typeBurger).isFree = false;
                System.out.println(getTime() + " - Cook " + cook_id + " uses the " + Restaurant.typeBurger.getName() + " machine.");
                Thread.sleep(ord.bugers_count * Restaurant.typeBurger.getPrepTime());
                ord.bugers_count = 0;
                Restaurant.burgerDiners.remove();
                Restaurant.getMachine(Restaurant.typeBurger).isFree = true;
                Restaurant.typeBurger.notifyAll();
            }
        }

        while (ord.fries_count > 0){
            synchronized (Restaurant.typeFries){
                while (!Restaurant.getMachine(Restaurant.typeFries).isFree ||
                        dinerServed.diner_id != Restaurant.friesDiners.element().diner_id){
                    Restaurant.typeFries.wait();
                }
                Restaurant.getMachine(Restaurant.typeFries).isFree = false;
                System.out.println(getTime() + " - Cook " + cook_id + " uses the " + Restaurant.typeFries.getName() + " machine.");
                ord.fries_count = 0;
                Restaurant.friesDiners.remove();
                Thread.sleep(ord.fries_count * Restaurant.typeFries.getPrepTime());
                Restaurant.getMachine(Restaurant.typeFries).isFree = true;
                Restaurant.typeFries.notifyAll();
            }
        }

        while (ord.coke_count > 0){
            synchronized (Restaurant.typeCoke){
                while (!Restaurant.getMachine(Restaurant.typeCoke).isFree ||
                        dinerServed.diner_id != Restaurant.cokeDiners.element().diner_id){
                    Restaurant.typeCoke.wait();
                }
                Restaurant.getMachine(Restaurant.typeCoke).isFree = false;
                System.out.println(getTime() + " - Cook " + cook_id + " uses the " + Restaurant.typeCoke.getName() + " machine.");
                Thread.sleep(ord.coke_count * Restaurant.typeCoke.getPrepTime());
                ord.coke_count = 0;
                Restaurant.cokeDiners.remove();
                Restaurant.getMachine(Restaurant.typeCoke).isFree = true;
                Restaurant.typeCoke.notifyAll();
            }
        }

        dinerServed.notifyAll();
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

    public String getTime(){
        long currentTime = System.currentTimeMillis() / 1000;
        long timeSpent = currentTime - Restaurant.startTime;

        long hour = timeSpent / 60;
        String hour_str = String.format("%02d", hour);
        long min = currentTime % 60;
        String min_str = String.format("%02d", min);

        return (hour_str + ":" + min_str);
    }
}
