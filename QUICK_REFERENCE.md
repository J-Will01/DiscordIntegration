# Quick Reference Guide

Quick reference for Discord Integration's new features.

## Embed Customization

### Basic Setup

```toml
[embedMode.advancementMessage]
asEmbed = true
customTitle = "🏆 Achievement Unlocked!"
customDescription = "**%player%** earned: **%advName%**"
```

### Available Placeholders

| Message Type | Placeholders |
|-------------|-------------|
| Advancement | `%player%`, `%advName%`, `%advDesc%` |
| Chat | `%player%`, `%msg%` |
| Join/Leave | `%player%` |
| Death | `%player%`, `%msg%` |
| Server Start | `%startupTime%` |

### Limits

- **Title:** 256 characters max
- **Description:** 4096 characters max

---

## Message Pattern Matching

### Basic Setup

```toml
[messagePatterns]
enabled = true

[[messagePatterns.patterns]]
pattern = "(?i).*backup.*start.*"
replacement = "🧰 Backup started"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Backup"
embedDescription = "World backup initiated"
embedColor = "#FFD700"
```

### Pattern Quick Reference

| Pattern | Matches |
|---------|---------|
| `.*backup.*` | Any message with "backup" |
| `^backup$` | Exact match "backup" |
| `(\\w+)` | Capture word (use `$1` in replacement) |
| `(?i)` | Case-insensitive |
| `\\bword\\b` | Whole word only |

### Capture Groups

- `$0` - Entire match
- `$1` - First capture group
- `$2` - Second capture group

**Example:**
```toml
pattern = "player (\\w+) joined"
replacement = "Welcome $1!"
```

---

## Common Use Cases

### Custom Advancement Embed

```toml
[embedMode.advancementMessage]
asEmbed = true
colorHexCode = "#FFD700"
customTitle = "🏆 %advName%"
customDescription = "**%player%** earned this achievement!\n\n_%advDesc%_"
```

### Filter Backup Messages

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*backup.*"
replacement = "🧰 Backup in progress"
suppressOriginal = true
asEmbed = true
embedTitle = "Backup"
embedColor = "#FFD700"
```

### Route Errors to Channel

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*error.*"
replacement = "⚠️ $0"
suppressOriginal = true
channelID = "YOUR_CHANNEL_ID"
asEmbed = true
embedColor = "#FF0000"
```

### Custom Player Join

```toml
[embedMode.playerJoinMessage]
asEmbed = true
colorHexCode = "#00FF00"
customTitle = "👋 Welcome %player%!"
customDescription = "%player% has joined the server"
```

---

## Troubleshooting

### Custom Fields Not Working?

1. ✅ Check `asEmbed = true`
2. ✅ Restart server after config changes
3. ✅ Verify placeholder names (case-sensitive)

### Pattern Not Matching?

1. ✅ Use `(?i)` for case-insensitive
2. ✅ Test pattern in regex tester
3. ✅ Check server logs for errors

### Need More Help?

- 📖 [Full Features Guide](FEATURES.md)
- 📖 [Embed Customization Guide](EMBED_CUSTOMIZATION.md)
- 📖 [Pattern Matching Guide](MESSAGE_PATTERN_MATCHING.md)
- 💬 [Discord Server](https://erd.wtf/discord)



