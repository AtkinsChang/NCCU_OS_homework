package edu.nccu.plsm.osproject.web.api;

import java.util.concurrent.TimeUnit;

public interface Configurable {

    void setCapacity(int size);

    int size();

    void setTakeTime(int min, int max, TimeUnit unit);

    void setPutTime(int min, int max, TimeUnit unit);

}
