package com.github.philippheuer.chatbot4twitch.features;

import me.philippheuer.twitch4j.enums.SubPlan;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.SubscriptionEvent;

public class ChannelNotificationOnSubscription {

    /**
     * Subscribe to the Subscription Event
     */
    @EventSubscriber
    public void onSubscription(SubscriptionEvent event) {
        String message = "";
        SubPlan subPlan = event.getSubscription().getSubPlan().get();
        int streak = event.getSubscription().getStreak().get();
        int streakEnds = streak;
        String[] months = {"месяц", "месяца", "месяцев"};
        String plan = "";
        String month;
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

        while (streakEnds > 100) {
            streakEnds %= 100;
        }

        if (streakEnds >= 11 && streak <= 19) {
            month = months[2];
        } else {
            while (streakEnds > 10) {
                streakEnds %= 10;
            }
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

        // New Subscription
        if (!event.getSubscription().getStreak().isPresent()) {
            message = String.format(" Поздравляю @%s , ты стал подписчиком на канале %s за %s!", event.getUser().getDisplayName(), event.getChannel().getDisplayName(), plan);
        }
        // Resubscription
        if (event.getSubscription().getStreak().isPresent() && streak > 1) {

            message = String.format("Поздравляю, @%s , ты продлил подписку на %s за %s и твой стаж подписки уже %s %s!", event.getUser().getDisplayName(), event.getChannel().getDisplayName(), plan, streak, month);
        }

        // Send Message
        event.getClient().getIrcClient().sendMessage(event.getChannel().getName(), message);
    }

}
