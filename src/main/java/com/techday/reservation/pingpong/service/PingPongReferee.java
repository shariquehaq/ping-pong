package com.techday.reservation.pingpong.service;

import org.springframework.stereotype.Component;

@Component
public class PingPongReferee extends Referee {


    @Override
    void notifyUsers(String message) {

        slackWebApiClient.postMessage(getType(), "Ping pong table " + message, "resourcemaster", true);
    }

    @Override
    public String getType() {
        return "pingpong";
    }

    @Override
    public String getTypeId() {
        return "GF79B0G5N";
    }


}
