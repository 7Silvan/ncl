package ua.group42.taskmanager.model;

import java.util.Comparator;

/**
 *
 * @author Group42
 */
public class TaskComparator implements Comparator<Task>{

    @Override
    public int compare(Task t1, Task t2) {              
//        Long anotherTaskTime = t1.getDate().getTime();
//        Long thisTime = t2.getDate().getTime();
//        return thisTime.compareTo(anotherTaskTime);
        return ((Long)t1.getDate().getTime()).compareTo(t2.getDate().getTime());
    }
    
}
