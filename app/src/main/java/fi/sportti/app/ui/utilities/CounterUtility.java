package fi.sportti.app.ui.utilities;

public class CounterUtility {

    private int value;

    public CounterUtility(){
        this.value = 0;
    }

    public CounterUtility(int startValue){
        this.value = startValue;
    }

    public void plus(){
        this.value++;
    }

    public void plus(int value){
        this.value += value;
    }

    public void minus(){
        this.value--;
    }

    public void minus(int value){
        this.value -= value;
    }
}
