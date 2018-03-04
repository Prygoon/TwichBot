package com.github.philippheuer.chatbot4twitch.features;

import me.philippheuer.twitch4j.enums.SubPlan;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.channel.SubscriptionEvent;

public class ChannelNotificationOnSubscription {

    /**
     * Subscribe to the Subscription Event
     */
    @EventSubscriber
    public void onSubscription(SubscriptionEvent event) {
        String message;
        SubPlan subPlan;
        int streak = event.getSubscription().getStreak();
        int streakEnds = streak;
        String[] months = {"месяц", "месяца", "месяцев"};
        String plan = "";
        String month;
        subPlan = event.getSubscription().getSubPlan();

        assert subPlan != null;
        switch (subPlan) {
            case PRIME: {
                plan = "Twitch Prime";
                break;
            }
            case TIER_1: {
                plan = "4.99$";
                break;
            }
            case TIER_2: {
                plan = "9.99$";
                break;
            }
            case TIER_3: {
                plan = "24.99$";
                break;
            }
        }

        streakEnds %= 100;

        if (streakEnds >= 11 && streakEnds <= 19) {
            month = months[2];
        } else {
            streakEnds %= 10;
            switch (streakEnds) {
                case 1: {
                    month = months[0];
                    break;
                }
                case 2:
                case 3:
                case 4: {
                    month = months[1];
                    break;
                }
                default:
                    month = months[2];
            }
        }

        // Resubscription
        if (streak > 1) {
            message = String.format("Поздравляю, @%s , ты продлил(а) подписку на %s за %s и твой стаж подписки уже %s %s!", event.getUser().getDisplayName(), event.getChannel().getDisplayName(), plan, streak, month);
        } else {
            // New Subscription
            //if (event.getSubscription().getStreak().isPresent() && streak <= 1) {
            message = String.format("Поздравляю @%s , ты стал(а) подписчиком на канале %s за %s!", event.getUser().getDisplayName(), event.getChannel().getDisplayName(), plan);
        }

        // Send Message
        event.getClient().getMessageInterface().sendMessage(event.getChannel().getName(), message);
    }

}
