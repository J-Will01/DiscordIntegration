# Embed Customization Guide

This guide explains how to customize Discord embed titles and descriptions for different message types.

## Table of Contents

1. [Overview](#overview)
2. [Configuration](#configuration)
3. [Available Placeholders](#available-placeholders)
4. [Examples](#examples)
5. [Best Practices](#best-practices)
6. [Troubleshooting](#troubleshooting)

---

## Overview

Embed customization allows you to override the default embed formatting for any message type. You can set custom titles and descriptions that will replace the default behavior.

### Key Features

- ✅ Custom titles and descriptions for any embed type
- ✅ Placeholder support for dynamic content
- ✅ Automatic truncation if limits are exceeded
- ✅ Works with all message types (advancements, chat, joins, etc.)

### Requirements

- **`asEmbed` must be `true`** for the message type you want to customize
- Custom fields only apply when embeds are enabled
- If both `customTitle` and `customDescription` are set, the message becomes embed-only (no text content)

---

## Configuration

### Location

Edit `config/Discord-Integration.toml` in your server directory.

### Structure

Each embed entry in the `[embedMode]` section supports these fields:

```toml
[embedMode.advancementMessage]
asEmbed = true
colorHexCode = "#FFD700"
customTitle = ""           # Custom title (max 256 chars)
customDescription = ""     # Custom description (max 4096 chars)
customJSON = ""            # Full custom JSON (highest priority)
```

### Field Priority

When multiple custom fields are set, priority is:

1. **`customJSON`** (if not empty) - Full control via JSON
2. **`customTitle`/`customDescription`** (if set) - Custom title/description
3. **Default behavior** - Standard embed formatting

---

## Available Placeholders

Placeholders are replaced with actual values when the message is sent. Format: `%placeholderName%`

### Advancement Messages

Available in: `advancementMessage`

| Placeholder | Description | Example |
|------------|-------------|---------|
| `%player%` | Player's name | `Steve` |
| `%advName%` | Advancement name | `Diamonds!` |
| `%advDesc%` | Advancement description | `Diamonds! You're so rich!` |

### Chat Messages

Available in: `chatMessages`

| Placeholder | Description | Example |
|------------|-------------|---------|
| `%player%` | Player's name | `Steve` |
| `%msg%` | Chat message content | `Hello everyone!` |

### Player Join/Leave Messages

Available in: `playerJoinMessage`, `playerLeaveMessage`

| Placeholder | Description | Example |
|------------|-------------|---------|
| `%player%` | Player's name | `Steve` |

### Death Messages

Available in: `playerDeathMessages`

| Placeholder | Description | Example |
|------------|-------------|---------|
| `%player%` | Player's name | `Steve` |
| `%msg%` | Death message | `was slain by Zombie` |

### Server Messages

Available in: `startMessages`, `stopMessages`

| Placeholder | Description | Example |
|------------|-------------|---------|
| `%startupTime%` | Startup time (only in startMessages) | `(Started in 45s)` |

---

## Examples

### Example 1: Custom Advancement Embed

**Configuration:**
```toml
[embedMode.advancementMessage]
asEmbed = true
colorHexCode = "#FFD700"
customTitle = "🏆 Achievement Unlocked!"
customDescription = "**%player%** just earned: **%advName%**\n\n_%advDesc%_"
```

**Result in Discord:**
```
🏆 Achievement Unlocked!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
**Steve** just earned: **Diamonds!**

_Diamonds! You're so rich!_
```

### Example 2: Minimal Advancement Embed

**Configuration:**
```toml
[embedMode.advancementMessage]
asEmbed = true
colorHexCode = "#FFD700"
customTitle = "🏆 %advName%"
customDescription = "%player% earned this achievement"
```

**Result in Discord:**
```
🏆 Diamonds!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Steve earned this achievement
```

### Example 3: Custom Player Join Message

**Configuration:**
```toml
[embedMode.playerJoinMessage]
asEmbed = true
colorHexCode = "#00FF00"
customTitle = "👋 Welcome %player%!"
customDescription = "%player% has joined the server"
```

**Result in Discord:**
```
👋 Welcome Steve!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Steve has joined the server
```

### Example 4: Custom Chat Message Embed

**Configuration:**
```toml
[embedMode.chatMessages]
asEmbed = true
generateUniqueColors = true
customTitle = "💬 Chat"
customDescription = "**%player%:** %msg%"
```

**Result in Discord:**
```
💬 Chat
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
**Steve:** Hello everyone!
```

### Example 5: Title Only (No Description)

**Configuration:**
```toml
[embedMode.advancementMessage]
asEmbed = true
colorHexCode = "#FFD700"
customTitle = "🏆 %player% earned %advName%!"
customDescription = ""  # Empty = use default description
```

**Result:** Custom title with default description behavior.

### Example 6: Description Only (No Title)

**Configuration:**
```toml
[embedMode.advancementMessage]
asEmbed = true
colorHexCode = "#FFD700"
customTitle = ""  # Empty = no custom title
customDescription = "**%player%** just earned: **%advName%**\n\n_%advDesc%_"
```

**Result:** Default title behavior with custom description.

---

## Best Practices

### 1. Keep Titles Short

Discord limits titles to 256 characters. Keep them concise and impactful.

✅ **Good:**
```toml
customTitle = "🏆 Achievement Unlocked!"
```

❌ **Bad:**
```toml
customTitle = "🏆 Achievement Unlocked! Player %player% has just earned the advancement %advName% which is described as %advDesc% and this is a very long title that might get truncated"
```

### 2. Use Emojis Sparingly

Emojis can make messages more visually appealing, but don't overuse them.

✅ **Good:**
```toml
customTitle = "🏆 Achievement Unlocked!"
```

❌ **Bad:**
```toml
customTitle = "🏆🎉✨🎊 Achievement Unlocked! 🎊✨🎉🏆"
```

### 3. Use Markdown for Formatting

Discord supports markdown in embed descriptions.

**Bold:** `**text**`
**Italic:** `*text*` or `_text_`
**Code:** `` `code` ``
**New Line:** `\n`

**Example:**
```toml
customDescription = "**%player%** just earned: **%advName%**\n\n_%advDesc%_"
```

### 4. Test Your Configuration

After making changes:
1. Restart your server
2. Trigger the message type (e.g., earn an advancement)
3. Check Discord to see the result
4. Adjust if needed

### 5. Leave Fields Empty to Use Defaults

If you want to customize only the title or only the description, leave the other field empty (`""`).

---

## Troubleshooting

### Custom Fields Not Appearing

**Problem:** Custom title/description not showing in Discord.

**Solutions:**
1. ✅ Check that `asEmbed = true` for the message type
2. ✅ Verify the field names are correct (`customTitle`, `customDescription`)
3. ✅ Restart the server after making changes
4. ✅ Check server logs for warnings about invalid placeholders

### Placeholders Not Replacing

**Problem:** Placeholders like `%player%` appear as literal text.

**Solutions:**
1. ✅ Check spelling of placeholder names (case-sensitive)
2. ✅ Verify the placeholder is available for that message type
3. ✅ Check server logs for warnings about unknown placeholders
4. ✅ Ensure placeholder names don't have extra spaces: `%player%` not `% player %`

### Text Getting Truncated

**Problem:** Title or description is cut off.

**Solutions:**
1. ✅ Title limit: 256 characters
2. ✅ Description limit: 4096 characters
3. ✅ Check server logs for truncation warnings
4. ✅ Shorten your text if it exceeds limits

### Both Original and Custom Message Showing

**Problem:** Both default and custom messages appear.

**Solutions:**
1. ✅ This is expected if `customTitle` and `customDescription` are both set - the message becomes embed-only
2. ✅ If you want text content, leave both fields empty and use default behavior
3. ✅ If you want embed-only, set both fields

### Configuration Not Saving

**Problem:** Changes to config file aren't being used.

**Solutions:**
1. ✅ Restart the server after editing the config
2. ✅ Check for syntax errors in the TOML file
3. ✅ Verify the config file is in the correct location: `config/Discord-Integration.toml`
4. ✅ Check server logs for config loading errors

---

## Advanced Usage

### Combining with customJSON

If you set `customJSON`, it takes priority over `customTitle` and `customDescription`. However, you can use `%startupTime%` in customJSON for server start messages.

**Example:**
```toml
[embedMode.startMessages]
asEmbed = true
customJSON = '''
{
  "title": "Server Started",
  "description": "The server is now online!%startupTime%",
  "color": 65280
}
'''
```

### Multiple Message Types

You can customize different message types independently:

```toml
[embedMode.advancementMessage]
asEmbed = true
customTitle = "🏆 Achievement"
customDescription = "%player% earned %advName%"

[embedMode.playerJoinMessage]
asEmbed = true
customTitle = "👋 Welcome"
customDescription = "%player% joined"

[embedMode.chatMessages]
asEmbed = true
customTitle = "💬 Chat"
customDescription = "%player%: %msg%"
```

---

## Additional Resources

- [Main Features Guide](FEATURES.md) - Overview of all new features
- [Message Pattern Matching Guide](MESSAGE_PATTERN_MATCHING.md) - Pattern matching documentation
- [Wiki](https://wiki.erdbeerbaerlp.de/dcintegration:root) - Official documentation

---

## Support

Need help? Check:
- [Discord Server](https://erd.wtf/discord)
- [GitHub Issues](https://github.com/ErdbeerbaerLP/Discord-Chat-Integration/issues)
- [Wiki](https://wiki.erdbeerbaerlp.de/dcintegration:root)

