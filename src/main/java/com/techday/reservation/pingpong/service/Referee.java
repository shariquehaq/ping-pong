package com.techday.reservation.pingpong.service;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.techday.reservation.pingpong.model.Table;
import com.techday.reservation.pingpong.model.TableStatus;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.webapi.SlackWebApiClient;

public abstract class Referee {

    @Value("${slackBotToken}")
    private String slackToken;

    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long RESERVATION_TIME = 3 * ONE_MINUTE;
    private static final long PLAYING_TIME = 15 * ONE_MINUTE;

    private long reservationTimeLeft;
    private long playingTimeLeft;

    private TimerTask reservationTask;
    private TimerTask playingTask;
    protected TableStatus tableStatus = TableStatus.FREE;
    private Timer timer;
    protected SlackWebApiClient slackWebApiClient;

    public void reserveTable() {
        this.reservationTask = new ReservationTask();
        this.timer.schedule(this.reservationTask, ONE_SECOND, ONE_SECOND);
        this.tableStatus = TableStatus.RESERVED;
    }

    @PostConstruct
    private void init() {
        this.timer = new Timer("ReservationTimer");
        slackWebApiClient = SlackClientFactory.createWebApiClient(slackToken);
    }

    public TableStatus getStatus() {
        return tableStatus;
    }

    public void occupyResource() {

        if (tableStatus.equals(TableStatus.RESERVED) || tableStatus.equals(TableStatus.FREE)) {
            this.playingTask = null;
            this.playingTask = new PlayingTask();
            this.timer.schedule(this.playingTask, ONE_SECOND, ONE_SECOND);
            this.cancelReservationTask(TableStatus.OCCUPIED);
        }

    }

    public Table getTableInformation() {
        long timeRemaining = 0;
        if (reservationTask != null && tableStatus.equals(TableStatus.RESERVED)) {
            timeRemaining = reservationTimeLeft;
        } else if (playingTask != null && tableStatus.equals(TableStatus.OCCUPIED)) {
            timeRemaining = playingTimeLeft;
        }
        return Table.builder().status(this.tableStatus).timeRemaining(timeRemaining / ONE_SECOND).build();
    }

    public String getSlackMessage() {
        switch (tableStatus) {
            case FREE:
                return Emoticons.FREE;
            case RESERVED:
                return Emoticons.RESERVED + ", if not occupied it will be free in " + ((reservationTimeLeft / ONE_SECOND) / 60) + ":" + ((reservationTimeLeft / ONE_SECOND) % 60) + "minutes";
            case OCCUPIED:
                return Emoticons.OCCUPIED + ", will be free in " + ((playingTimeLeft / ONE_SECOND) / 60) + ":" + ((playingTimeLeft / ONE_SECOND) % 60) + "minutes";
        }
        return null;
    }


    private class PlayingTask extends TimerTask {

        public PlayingTask() {
            super();
            Referee.this.playingTimeLeft = PLAYING_TIME;
        }

        @Override
        public void run() {
            try {
                decreasePlayingTime();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel();
            }
        }

    }

    private class ReservationTask extends TimerTask {

        public ReservationTask() {
            super();
            Referee.this.reservationTimeLeft = RESERVATION_TIME;
        }

        @Override
        public void run() {
            try {
                decreaseReservationTime();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel();
            }
        }
    }

    private void decreaseReservationTime() {
        this.reservationTimeLeft -= ONE_SECOND;
        if (this.reservationTimeLeft <= 0) {
            cancelReservationTask(TableStatus.FREE);
            notifyUsers("is free now");
        }

    }

    private synchronized void decreasePlayingTime() {
        this.playingTimeLeft -= ONE_SECOND;
        if (this.playingTimeLeft <= 0) {
            cancelPlayingTask();
        }
    }

    abstract void notifyUsers(String message);

    public void cancelReservationTask(TableStatus tableStatus) {
        this.tableStatus = tableStatus;
        if (this.reservationTask != null) {
            this.reservationTask.cancel();
            this.reservationTask = null;
        }
    }

    private void cancelPlayingTask() {
        if (this.playingTask != null) {
            this.playingTask.cancel();
            this.playingTask = null;
        }
    }


    public abstract String getType();

    public abstract String getTypeId();

}
