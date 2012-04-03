package ua.group42.taskmanager.common.model;

import java.util.Comparator;

/**
 *
 * @author Group42
 */
public class TaskComparator implements Comparator<Task>{

    /**
     * if t1 and t2 have not the same state, the "earlier" is alive one
     * otherwise it comes out from date comparation
     * @param t1 first task to compare
     * @param t2 second task to compare
     * @return 0 if tasks equals, a value less than 0 if t1.date is less than t2.date, 
     * otherwise a value greater than 0
     */
    @Override
    public int compare(Task t1, Task t2) {              
//        Long thisTime = t1.getDate().getTime();
//        Long anotherTaskTime = t2.getDate().getTime();
//        return thisTime.compareTo(anotherTaskTime);
        if (t1.isAlive() == t2.isAlive())
            return ((Long)t1.getDate().getTime()).compareTo(t2.getDate().getTime());
        else 
            return (t1.isAlive())?-1:1;
    }
    
}
