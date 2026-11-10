package org.Cry_Legende.sHStaffAlert.network;

import java.util.function.Consumer;

/**
 * Ce dont l'alerte staff a besoin du réseau. Seule {@link ShNetworkAlerts} connaît SHNetwork : sans lui, aucune
 * classe de ce paquet n'est chargée.
 */
public interface AlertNetwork {

    /** Envoie l'alerte, déjà colorée, aux autres serveurs du réseau. */
    void publish(String message);

    /** Reçoit les alertes des autres serveurs, sur le fil principal. */
    void onAlert(Consumer<String> handler);
}
