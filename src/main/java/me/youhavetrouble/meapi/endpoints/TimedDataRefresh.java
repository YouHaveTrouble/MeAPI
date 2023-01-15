package me.youhavetrouble.meapi.endpoints;

import java.util.TimerTask;

public interface TimedDataRefresh {

    TimerTask getTimerTask();

    int refreshInterval();

}
