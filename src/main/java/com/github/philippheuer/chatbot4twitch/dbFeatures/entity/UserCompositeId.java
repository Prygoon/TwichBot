package com.github.philippheuer.chatbot4twitch.dbFeatures.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserCompositeId implements Serializable {

    @Column(name = "twitch_id")
    private Long twitchId;

    @Column(name = "channel")
    private String channel;

    public UserCompositeId() {
    }

    public Long getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(Long twitchId) {
        this.twitchId = twitchId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
