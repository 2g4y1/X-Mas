# 🎄 X-Mas - Ultimate Christmas Tree Plugin

![Christmas Banner](http://puu.sh/dKlK1/85c3dad454.jpg)

[![Paper 1.21.10](https://img.shields.io/badge/Paper-1.21.10-blue.svg)](https://papermc.io/)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

A feature-rich Christmas tree plugin for Paper servers. Plant magical Christmas trees, upgrade them through multiple levels, collect weighted gifts, unlock achievements, and compete on leaderboards!

## 📋 Features

### 🌲 Tree System
- **4 Tree Levels**: Sapling → Small Tree → Tree → Magic Tree
- **Progressive Upgrades**: Feed your tree materials to level it up
- **Magical Effects**: Each level has unique particle effects
- **Gift Spawning**: Trees automatically spawn presents based on their level
- **Christmas Bonus**: 2x spawn rate on December 24-25

### 🎁 Gift System
- **Weighted Rarity System**:
  - **Common** (70%): Iron, Gold, Redstone, Coal
  - **Rare** (20%): Diamonds, Emeralds, Enchanted Books
  - **Epic** (8%): Diamond Blocks, Netherite Scraps, Golden Apples
  - **Legendary** (2%): Netherite Ingots, Elytra, Stacks of valuables
- **Christmas Special**: Guaranteed legendary gift on December 24-25 (once per player)
- **Cooldown System**: 1 gift per hour per player to prevent farming
- **Custom Gifts**: Admins can add custom items with `/xmas addhand`

### 🏆 Achievement System
- **5 Achievements**:
  - 🌱 **First Tree**: Plant your first Christmas tree
  - ⭐ **Max Level**: Reach the Magic Tree level
  - 🎁 **Gift Collector**: Collect 100 gifts
  - 🍀 **Lucky Streak**: Get 5 good gifts in a row
  - 🎄 **Tree Master**: Have 10 trees planted simultaneously
- **Server Broadcasts**: Major achievements are announced to all players
- **Persistent Progress**: All achievements are saved

### 📊 Statistics & Rankings
- **Player Stats**: Trees planted, gifts collected, achievements unlocked
- **Leaderboards**:
  - Most trees planted
  - Most gifts collected
  - Fastest to reach Magic Tree
- **GUI Interface**: Beautiful inventory GUI showing tree status and progress

### ⚖️ Balance Features
- **Gift Cooldown**: Max 1 gift per hour per player (global)
- **Tree Limit**: Configurable max trees per player
- **Level Gate**: Magic Tree only available after December 20
- **Owner Protection**: Only tree owners (or admins) can destroy trees

## 🚀 Installation

1. **Requirements**:
   - Paper/Spigot 1.21.10+
   - Java 21+

2. **Download** the latest `xmas-3.0.jar` from [Releases](https://github.com/2g4y1/X-Mas/releases)

3. **Place** the JAR in your server's `plugins/` folder

4. **Restart** your server

5. **Configure** (optional) in `plugins/X-Mas/config.yml`

## 📝 Commands

### Player Commands
| Command | Description |
|---------|-------------|
| `/xmas help` | Show all available commands |
| `/xmas stats` | View your personal statistics |
| `/xmas top [trees\|gifts\|fastest]` | View top 10 leaderboards |
| `/xmas achievements` | Check your achievement progress |
| `/xmas gui` | Open tree status GUI |

### Admin Commands (require `xmas.admin`)
| Command | Description |
|---------|-------------|
| `/xmas give <player>` | Give X-Mas Crystal to a player |
| `/xmas addhand` | Add held item to gift pool |
| `/xmas reload` | Reload plugin configuration |
| `/xmas end` | End the Christmas event |
| `/xmas gifts` | Spawn gifts for all trees |

## 🔐 Permissions

### Default Permissions
| Permission | Description | Default |
|------------|-------------|---------|
| `xmas.use` | Basic gameplay and commands | Everyone |
| `xmas.admin` | All admin commands | OP only |
| `xmas.tree.destroy.other` | Destroy other players' trees | OP only |

### Detailed Permissions
```yaml
xmas.*                      # All permissions
xmas.use                    # Basic usage
xmas.admin                  # Admin features
xmas.tree.plant            # Plant trees
xmas.tree.upgrade          # Upgrade trees
xmas.tree.destroy.own      # Destroy own trees
xmas.tree.destroy.other    # Destroy any tree
xmas.gift.collect          # Collect gifts
xmas.command.give          # /xmas give
xmas.command.reload        # /xmas reload
xmas.command.end           # /xmas end
```

## 🎮 How to Play

1. **Craft X-Mas Crystal**:
   ```
   [ ] [D] [ ]
   [D] [E] [D]
   [ ] [D] [ ]
   
   D = Diamond, E = Emerald
   ```

2. **Plant a Tree**:
   - Place a Spruce Sapling
   - Right-click with X-Mas Crystal
   - Your tree is now planted! 🌱

3. **Upgrade Your Tree**:
   - Right-click your tree to see requirements
   - Feed it the required materials
   - Level up: Sapling → Small Tree → Tree → Magic Tree

4. **Collect Gifts**:
   - Wait for presents to spawn around your tree
   - Right-click or break presents to open them
   - Remember: Only 1 gift per hour!

5. **Unlock Achievements**:
   - Check progress with `/xmas achievements`
   - Complete challenges to unlock all 5 achievements

6. **Compete**:
   - View rankings with `/xmas top`
   - Race to be the first to Magic Tree!

## 🎄 Special Events

### Christmas Event (Dec 24-25)
- 🎁 **Guaranteed Legendary**: First gift is always legendary
- ⚡ **2x Spawn Rate**: Presents spawn twice as fast
- 🎊 **Special Messages**: Festive chat messages

### Magic Tree Gate (Dec 20+)
- The final Magic Tree level is only unlockable after December 20
- Plan your progression accordingly!

## ⚙️ Configuration

Edit `plugins/X-Mas/config.yml`:

```yaml
core:
  plugin-enabled: true          # Enable/disable plugin
  update-speed: 7               # Tree update tick speed
  particles-delay: 35           # Particle effect delay
  tree-limit: 5                 # Max trees per player
  locale: en                    # Language (en, ru, ru_santa)

xmas:
  tree-lvl:
    sapling:
      gift-cooldown: 300        # Seconds between gifts
      lvlup:
        IRON_INGOT: 16
        GOLD_INGOT: 8
    # ... more levels
```

## 🔧 Version History

### Version 3.0 (November 2025)
- ✅ **Paper 1.21.10 Compatibility**
- ✅ **Java 21 Support**
- ✅ **Player Statistics System** with YAML persistence
- ✅ **5 Achievements** with unlock tracking
- ✅ **Weighted Gift Rarity** (4 tiers: Common → Legendary)
- ✅ **Interactive GUI** for tree status
- ✅ **Ranking System** (Top 10 leaderboards)
- ✅ **Christmas Event** (Dec 24-25: Guaranteed legendary + 2x spawn)
- ✅ **Extended Commands** (/stats, /top, /achievements, /gui)
- ✅ **Balance Features**:
  - 1 gift per hour cooldown (global per player)
  - Magic Tree only after Dec 20
- ✅ **Complete plugin.yml** with full permission system
- ✅ **Bug Fixes**:
  - Fixed cooldown enforcement
  - Fixed level gate logic
  - Improved block removal timing

### Version 2.0
- ✅ Support for 1.20+ MC versions
- ✅ Java 21 requirement
- ✅ Updated dependencies (2024)
- ✅ Fixed deprecated methods
- ✅ Skulls system using Paper API
- ✅ Autocomplete for commands
- ✅ `/xmas reload` command
- ✅ `/xmas addhand` for custom gifts

## 🐛 Bug Reports

Found a bug? [Open an issue](https://github.com/2g4y1/X-Mas/issues)

## 📜 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- **GitHub**: https://github.com/2g4y1/X-Mas
- **SpigotMC**: https://www.spigotmc.org/resources/x-mas-upgradeable-christmas-tree-event-updated.121040/
- **Original Version**: [LucidAPs/X-Mas](https://github.com/LucidAPs/X-Mas)

## 💝 Credits

- **Original Author**: LucidAPs
- **Updated by**: Community contributors
- **Special Thanks**: Paper development team

---

**Merry Christmas! 🎅🎄🎁**