package com.techday.reservation.pingpong.service;


import java.util.List;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.techday.reservation.pingpong.model.TableStatus;

import lombok.RequiredArgsConstructor;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;

@Component
@RequiredArgsConstructor
public class SlackBot extends Bot {


    @Value("${slackBotToken}")
    private String slackToken;
    private final List<Referee> referees;

    @Controller(events = { EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE })
    public void onReceiveDM(WebSocketSession session, Event event) {
        reply(session, event, new Message("Hi, I am " + slackService.getCurrentUser().getName()));
    }

    @Controller(events = EventType.MESSAGE, pattern = "reserve|help|status")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        if (!matcher.group(0).isEmpty()) {
            doBusiness(session, event, matcher);
        }
    }

    private void doBusiness(WebSocketSession session, Event event, Matcher matcher) {
        final Referee channelReferee = referees
                .stream()
                .filter(referee -> referee.getTypeId().equals(event.getChannelId()))
                .findFirst()
                .get();
        switch (matcher.group()) {
            case "status":
                reply(session, event, new Message(channelReferee.getSlackMessage()));
                break;

            case "reserve":
                if (channelReferee != null && channelReferee.getStatus().equals(TableStatus.FREE)) {
                    channelReferee.reserveTable();
                    reply(session, event, new Message("Your kicker table has been reserved. \nThe admin will free the table in 3 mins, if not occupied."));
                } else {
                    reply(session, event, new Message(channelReferee.getSlackMessage()));
                }
                break;
            case "help":
                reply(session, event, new Message("Following are the commands: \n 1. status -- to check the current status for the table. \n 2. reserve -- to reserve the table, you need to go to the table and press occupy."));

        }
    }

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

}
