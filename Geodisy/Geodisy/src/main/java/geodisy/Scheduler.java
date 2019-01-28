/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geodisy;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author pdante
 */
public class Scheduler {
   private final static int HOUR_TO_RUN = 2;
    private final static int MINUTE_TO_RUN = 0; 
    protected Calendar today;
    protected Scheduler() {
        today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, HOUR_TO_RUN);
        today.set(Calendar.MINUTE, MINUTE_TO_RUN);
        today.set(Calendar.SECOND, 0);
    }

public void run(){
// every night at 2am you run your task
Timer timer = new Timer();
timer.schedule(new MyTimerTask(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
    }
    
    




}
