![ThumbNail](./img/Thumbnail_512.png)
# BankTeller

## About
The BankTeller plugin is a simple yet effective plugin for allowing players to withdraw their money into a physical form,
this can have many advantages for many different types of servers.

Feel free to make any kind of suggestion by opening an issue on the [Github Repository](https://github.com/JustKato/BankTeller/issues/new)
or if you would like to feel free to reach out to me personally [on my Website](https://danlegt.com).

## Permissions
The only permissions that are available are administrative permissions, the `/teller` command is covered under this 
by `bankteller.admin`, once granted to a player/group of players they can **spawn in new BankTellers** or even **Remove BankTellers**

## Commands
```
/teller help   - Displays a help screen for more information
/teller spawn  - Spawns a BankTeller at the player's position
/teller remove - Removes the closest teller to the player ( in a range of 2 blocks )
```

## Anti-Duping measures
It's common place that minecraft, especially custom servers, have many duplication bugs that unfortunately are easily exploited,
what makes them really hard to find is the use of delay packets, or other "non standard" methods. To combat these issues
every single `BankNote` that the Teller spawns in contains unique information for easily identifying them, such as the
`Creation Date`, `The Author of the note` as well as a hidden NBT data value, an unique ID, this can be used to check for
duplicated items, as well as another bonus: If someone redeems a banknote with an ID, then any other banknote with that
same ID will NOT be able to be redeemed by anyone else and will notify everyone with the administrative permissions for
the BankTeller plugin.

## TODO:
- [X] BankNote UUIDv4 as the unique ID
- [X] BankNote duplicate admin notification
- [ ] Custom configuration for the first 3 banknote values + separate notification permissions for inspect and dupe notifications
- [ ] Configurable villager look / Custom mob as the BankTeller
- [ ] Citizens compatibility
