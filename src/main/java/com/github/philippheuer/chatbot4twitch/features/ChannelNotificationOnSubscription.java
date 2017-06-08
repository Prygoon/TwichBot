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
        String plan = "";
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

        // New Subscription
        if (event.getSubscription().getStreak().isPresent() && event.getSubscription().getStreak().get() <= 1) {
            message = String.format(" Поздравляю @%s , ты стал подписчиком на канале %s за %s!", event.getUser().getDisplayName(), event.getChannel().getDisplayName(), plan);
        }
        // Resubscription
        if (event.getSubscription().getStreak().isPresent() && event.getSubscription().getStreak().get() > 1) {

            message = String.format("Поздравляю, @%s , ты продлил подписку на %s за %s и твой стаж подписки уже %s месяцев!", event.getUser().getDisplayName(), event.getChannel().getDisplayName(), plan, event.getSubscription().getStreak().get());
        }

        // Send Message
        event.getClient().getIrcClient().sendMessage(event.getChannel().getName(), message);
    }

}
