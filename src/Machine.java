public class Machine {
    MachineType type;
    boolean isFree;
    long finishedTime;

    public Machine(MachineType type){
        this.type = type;
        this.isFree = true;
        this.finishedTime = 0;
    }
}

enum MachineType{
    BURGER(5000, "burger"), FRIES(3000, "fries"), COKE(1000, "coke");

    private int prepTime;
    private String name;
    MachineType(int prepTime, String name){
        this.prepTime = prepTime;
        this.name = name;
    }
    public int getPrepTime(){
        return this.prepTime;
    }
    public String getName(){
        return this.name;
    }
}
