package fi.sportti.app.ui.utilities;

public class CounterUtility {

    private int counter;
    private int min, max, step, start;

    public CounterUtility(int min, int max, int start, int step){
        this.counter = start;
        this.min = min;
        this.max = max;
        this.step = step;
        this.start = start;
    }

    public CounterUtility(){
        this.counter = 0;
        this.min = -100;
        this.max = 100;
        this.step = 1;
    }

    public String returnCounter(){
        return Integer.toString(counter);
    }

    public int returnCounterInt(){ return counter;}

    public void addToCounter(){
        if(this.counter + this.step <= this.max){
            this.counter += this.step;
        }else{
            this.counter = this.max;
        }
    }

    public void addToCounter(int i){
        if(this.counter + i <= this.max){
            this.counter += i;
        }else{
            this.counter = this.max;
        }
    }

    public void minusToCounter(){
        if(this.counter - this.step >= this.min){
            this.counter -= this.step;
        }else{
            this.counter = this.min;
        }
    }

    public void minusToCounter(int i){
        if(this.counter - i >= this.min){
            this.counter -= i;
        }else{
            this.counter = this.min;
        }
    }

    public void resetCounter(){
        this.counter = start;
    }
}
