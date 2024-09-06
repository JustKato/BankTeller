![BankTeller Thumbnail](./img/Thumbnail_512.png)

# BankTeller

![Banner](./img/banner_overview.png)
**BankTeller** is a simple yet powerful plugin designed for Minecraft servers, allowing players to **withdraw their in-game money into physical BankNotes**. These BankNotes can be traded, stored, and redeemed. This plugin offers significant advantages for servers with a focus on player economy, role-playing, or even just providing an immersive way to handle currency.

BankTeller includes advanced **anti-duplication measures** to prevent item duplication exploits, ensuring the integrity of your server's economy.

Feel free to make suggestions or report bugs by opening an issue on the [GitHub Repository](https://github.com/JustKato/BankTeller/issues/new), or reach out to me personally [via my website](https://danlegt.com).

---

![Banner](./img/banner_features.png)
- **Physical Currency**: Withdraw in-game money into BankNotes that can be stored, traded, or redeemed.
- **Anti-Duplication**: Each BankNote has a unique UUID and metadata, preventing duplication exploits.
- **Admin Commands**: Simple commands for spawning and removing BankTellers in the game world.

---

![Banner](./img/banner_permissions.png)
The plugin only includes administrative permissions. Players with the `bankteller.admin` permission can spawn or remove BankTellers.

| Permission         | Description                                 |
|--------------------|---------------------------------------------|
| `bankteller.admin`  | Allows access to spawn and remove BankTellers.|

---

![Banner](./img/banner_commands.png)
Here are the core commands available in BankTeller:
```
/teller help    - Displays a help screen for more information
/teller spawn   - Spawns a BankTeller at the player's position
/teller remove  - Removes the closest teller to the player ( in a range of 2 blocks )
/teller inspect - Inspects the currently held BankNote to make sure it's not a dupe and give extra info.
```

![Banner](./img/banner_security.png)
### Anti-Duplication Measures
One of the most common issues on Minecraft servers is item duplication exploits, often caused by **packet manipulation** or other non-standard methods. BankTeller addresses this with robust anti-duplication measures:

- **Unique Identification**: Every BankNote created by a BankTeller includes a unique identifier (UUIDv4), ensuring that no two BankNotes can share the same ID.
- **Metadata**: Each BankNote also includes metadata such as the **Creation Date** and **Author** of the note.
- **Admin Notifications**: If a duplicate BankNote is detected, administrators are immediately notified. Only the original note can be redeemed, and any subsequent attempts using the same UUID will be rejected.

This system prevents the use of duplicate items and ensures a fair player economy.

---
![Banner](./img/banner_todo.png)
- [X] **UUIDv4** as the unique ID for each BankNote.
- [X] **Admin Notification** for duplicate BankNote redemption.
- [ ] Custom configuration for initial BankNote values and separate notification permissions for inspecting and detecting duplicates.
- [ ] Configurable appearance for BankTellers (custom skins, villager professions, or custom mobs).
- [ ] **Citizens** plugin compatibility for more advanced NPC management.

---

![Banner](./img/banner_installation.png)
1. Download the latest version of the plugin from the [Releases](https://github.com/JustKato/BankTeller/releases) page.
2. Place the `.jar` file in your server's `plugins` folder.
3. Restart or reload the server to initialize the plugin.

---

![Banner](./img/banner_contribution.png)
Contributions are welcome! If you'd like to contribute to BankTeller, feel free to fork the repository, make your changes, 
and submit a pull request. You can also suggest new features or report bugs through the [issue tracker](https://github.com/JustKato/BankTeller/issues/new).