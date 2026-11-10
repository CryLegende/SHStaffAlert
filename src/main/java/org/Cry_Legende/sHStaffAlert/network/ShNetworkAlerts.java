package org.Cry_Legende.sHStaffAlert.network;

import com.google.gson.JsonObject;
import org.Cry_Legende.sHNetwork.api.SHNetworkAPI;

import java.util.function.Consumer;

/** Pont vers SHNetwork. Cette classe n'est chargée que si SHNetwork est installé. */
public final class ShNetworkAlerts implements AlertNetwork {

    private static final String CHANNEL = "staffalert";

    private final SHNetworkAPI api;

    private ShNetworkAlerts() {
        this.api = SHNetworkAPI.get();
    }

    /** Point d'entrée : le plugin ne résout cette classe qu'à cet appel, donc jamais sans SHNetwork. */
    public static AlertNetwork open() {
        return new ShNetworkAlerts();
    }

    @Override
    public void publish(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("message", message);
        api.publish(CHANNEL, json.toString());
    }

    @Override
    public void onAlert(Consumer<String> handler) {
        api.subscribe(CHANNEL, message -> {
            String text;
            try {
                text = message.object().get("message").getAsString();
            } catch (RuntimeException e) {
                return; // message illisible : le bus ne s'arrête jamais pour lui
            }
            handler.accept(text);
        }, true);
    }
}
