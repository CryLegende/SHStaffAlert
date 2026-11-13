<div align="center">

<img src="assets/logo.png" alt="SkyHand" width="140">

# SHStaffAlert

*/staffalert : envoie un message à tous les membres du staff connectés.*

<img src="https://img.shields.io/badge/Paper-26.3-f2a900?style=for-the-badge" alt="Paper 26.3"> <img src="https://img.shields.io/badge/Java-25-e76f00?style=for-the-badge" alt="Java 25"> <img src="https://img.shields.io/badge/SkyHand-Opale-3aa655?style=for-the-badge" alt="SkyHand Opale">

</div>

---

## Présentation

/staffalert : envoie un message à tous les membres du staff connectés.

Utilisé sur : **Opale**.

Avec **SHNetwork** (facultatif), l'alerte atteint le staff de tous les serveurs du réseau. Sans SHNetwork, ou avec `reseau.actif: false`, seuls les staffs du serveur la reçoivent.

## Commandes

| Commande | Alias | Description | Permission |
|---|---|---|---|
| `/staffalert <message>` | `/shstaffalert <message>` | Envoie un message à tous les staffs connectés. | `shstaffalert.use` |
| `/shstaffalert reload` | — | Recharge `config.yml` et `messages.yml` ; si un fichier est illisible, l'ancienne configuration reste active. Doit être le seul argument. | `shstaffalert.admin.reload` |

`/shstaffalert <message>` est l'ancienne forme de `/staffalert` : d'autres plugins l'appellent en console, elle continue de fonctionner. Sans argument, les deux commandes affichent une ligne d'usage qui ne liste que ce que l'expéditeur peut faire.

## Permissions

| Permission | Description | Par défaut |
|---|---|---|
| `shstaffalert.use` | Envoyer une alerte avec `/staffalert`. | `op` |
| `shstaffalert.staff` | Reçoit les messages envoyés via `/staffalert`. | `false` |
| `shstaffalert.admin` | Toutes les sous-commandes de `/shstaffalert`. | `op` |
| `shstaffalert.admin.reload` | `/shstaffalert reload`. | `op` |

## Configuration

Fichiers par défaut, avec des valeurs d'exemple. Cliquez sur un fichier pour l'ouvrir :

<pre>
plugins/SHStaffAlert/
├── <a href="src/main/resources/config.yml">config.yml</a>
└── <a href="src/main/resources/messages.yml">messages.yml</a>
</pre>

La clé `reseau.actif` (par défaut `true`) active l'envoi vers les autres serveurs quand SHNetwork est présent. Elle se lit au démarrage.

## Compilation

Java 25 et Maven :

```bash
mvn clean package
```

Le jar est généré dans `target/`.

---

<div align="center">

<sub>Développé par <b>Cry_Legende</b> pour le serveur <b>SkyHand</b> · Tous droits réservés</sub>

</div>
