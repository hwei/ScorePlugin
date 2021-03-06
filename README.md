ScorePlugin
===========

Score plugin for bukkit.


Features
--------

* Viewers can give a score to a work. The range of score is 0.0 - 10.0.
* Author will receive reward according to the final score.
* Give score will cost viewer money but he will win reward if the score is near to the final score.
* Final score is average score of viewers generally. But admin can also set it to a forced score.
* Display score sign position on dynmap.


Requirements
------------
Required:

* Minecraft server that runs CraftBukkit
* Any economy plugin supported by Vault, for example iConomy

Optional:

* Any permissions plugin supporting Vault, for example PermissionBukkit or PermissionEx


Usage
-----

1. Create a sign near your work: 1st line is `[Score]`, 2ed line is the name of your work.
2. Punch this sign, your name will display in 4th line.
3. Ask admin to use `\scr open` to open this score sign for you.
4. Inform other players of your work, and let them to give it a score.
5. Ask admin to use `\scr close` to close this score sign to get a final score and distribute rewards.




Commands
--------

### For users

    /scr info View score info.
    /scr <score> Give a score.
    /scr list List recent open score signs.
    /scr tp <num> Teleport to a score sign in list.

### For Admins

    /scr open Open a score sign.
    /scr admin View info for admins.
    /scr set <score> Set a forced score.
    /scr unset Unset forced score.
    /scr clear Clear all scores from viewers.
    /scr maxreward <amount> Set max reward for author.
    /scr close Close a score sign and distribute rewards.
    /scr reload Reload config.yml.


Permissions
-----------

    permissions:
      score.*:
        description: Gives access to all Score commands
        children:
          score.admin: true
      score.admin:
        description: Allows you to manage Score.
        default: op
        children:
          score.use: true
      score.use:
        description: Allows you to view info and give score.
        default: true
 
Configuation
-----------

config.yml example

    # How much money to take from viewer when give a score.
    price: 25.0
    # How much money the viewer will win if he has given the exact score to the final score.
    viewer_max_reward: 500.0
    # How much money the author will win if he has got score of 10.0.
    auther_max_reward: 5000.0
    # If the difference form viewer's score to final score is greater than this, he will win no money.
    viewer_score_threshold: 1.0
    # If the score of auther is less than this, he will win no money.
    auther_score_threshold: 6.0
    # The price of teleporting to score sign.
    tp_price: 100.0
    # Whether display open score signs on dynmap
    dynmap_display_open: true
    # Whether display closed score sings on dynmap
    dynmap_display_close: true 
