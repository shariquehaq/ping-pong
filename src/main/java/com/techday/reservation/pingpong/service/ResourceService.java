package com.techday.reservation.pingpong.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.techday.reservation.pingpong.model.Table;
import com.techday.reservation.pingpong.model.TableStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceService {

    private final List<Referee> referees;

    public void releaseResource(String resourceType) {
        final Referee resourceReferee = getResourceReferee(resourceType);
        resourceReferee.cancelReservationTask(TableStatus.FREE);
        resourceReferee.notifyUsers("is free now");
    }

    public void occupyResource(String resourceType) {
        final Referee resourceReferee = getResourceReferee(resourceType);
        resourceReferee.occupyResource();
    }

    private Referee getResourceReferee(String resourceType) {
        return referees.stream().filter(referee -> referee.getType().equals(resourceType)).findFirst().get();
    }

    public Table getStatus(String resourceType) {
        final Referee resourceReferee = getResourceReferee(resourceType);
        return resourceReferee.getTableInformation();
    }
}
