<div align="center">

<img src="assets/logo.png" alt="SkyHand" width="140">

# SHStaffAlert

*/shstaffalert : envoie un message à tous les membres du staff connectés.*

<img src="https://img.shields.io/badge/Paper-26.3-f2a900?style=for-the-badge" alt="Paper 26.3"> <img src="https://img.shields.io/badge/Java-25-e76f00?style=for-the-badge" alt="Java 25"> <img src="https://img.shields.io/badge/SkyHand-Opale-3aa655?style=for-the-badge" alt="SkyHand Opale">

</div>

---

## Présentation

/shstaffalert : envoie un message à tous les membres du staff connectés.

Utilisé sur : **Opale**.

## Commandes

| Commande | Alias | Description | Permission |
|---|---|---|---|
| `/shstaffalert` | — | Envoie un message à tous les staffs connectés. | `shstaffalert.command` |

## Permissions

| Permission | Description | Par défaut |
|---|---|---|
| `shstaffalert.command` | Permet d'utiliser la commande /shstaffalert | `op` |
| `shstaffalert.staff` | Reçoit les messages envoyés via /shstaffalert | `false` |

## Configuration

Fichiers par défaut repris du serveur (secrets remplacés par `CHANGE_ME`). Cliquez sur un fichier pour l'ouvrir :

<pre>
plugins/SHStaffAlert/
└── <a href="src/main/resources/config.yml">config.yml</a>
</pre>

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
