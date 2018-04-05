package com.github.philippheuer.chatbot4twitch.dbFeatures.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "newusers")
public class User implements Serializable{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "display_nickname")
    private String displayNickname;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "channel")
    private String channel;

    @Column(name = "copypaste_count")
    private int copypasteCount;

    @Column(name = "message_count")
    private int messageCount;

    @Column(name = "word_count")
    private int wordCount;

    @Column(name = "ban_count")
    private int banCount;

    @Column(name = "twitch_id")
    private Long twitchId ;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayNickname() {
        return displayNickname;
    }

    public void setDisplayNickname(String displayNickname) {
        this.displayNickname = displayNickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getCopypasteCount() {
        return copypasteCount;
    }

    public void setCopypasteCount(int copypasteCount) {
        this.copypasteCount = copypasteCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getBanCount() {
        return banCount;
    }

    public void setBanCount(int banCount) {
        this.banCount = banCount;
    }

    public long getTwitchId() {
        return twitchId;
    }

    public void setTwitchId(long twitchId) {
        this.twitchId = twitchId;
    }
}
