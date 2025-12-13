# Discord Integration - New Features Guide

This document explains the new features added to Discord Integration, including embed customization and message pattern matching.

## Table of Contents

1. [Embed Customization](#embed-customization)
2. [Message Pattern Matching](#message-pattern-matching)
3. [Startup Time Tracking](#startup-time-tracking)

---

## Embed Customization

### Overview

The embed customization feature allows you to set custom titles and descriptions for Discord embeds, replacing the default formatting. This gives you full control over how messages appear in Discord.

### Configuration

In your `config/Discord-Integration.toml` file, each embed entry (like `advancementMessage`, `playerJoinMessage`, etc.) now supports two new fields:

- **`customTitle`** - Custom title for the embed (max 256 characters)
- **`customDescription`** - Custom description for the embed (max 4096 characters)

### Requirements

- **`asEmbed` must be `true`** - Custom titles/descriptions only work when embeds are enabled
- Leave fields empty (`""`) to use default behavior
- If both `customTitle` and `customDescription` are set, the message will be embed-only (no text content)

### Placeholders

You can use placeholders in your custom titles and descriptions. Available placeholders vary by message type:

#### Advancement Messages
- `%player%` - Player's name
- `%advName%` - Advancement name
- `%advDesc%` - Advancement description

#### Chat Messages
- `%player%` - Player's name
- `%msg%` - Chat message content

#### Player Join/Leave Messages
- `%player%` - Player's name

#### Death Messages
- `%player%` - Player's name
- `%msg%` - Death message

### Examples

#### Custom Advancement Embed

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

#### Custom Player Join Embed

```toml
[embedMode.playerJoinMessage]
asEmbed = true
colorHexCode = "#00FF00"
customTitle = "👋 Welcome %player%!"
customDescription = "%player% has joined the server"
```

#### Custom Chat Message Embed

```toml
[embedMode.chatMessages]
asEmbed = true
generateUniqueColors = true
customTitle = "💬 Chat"
customDescription = "**%player%:** %msg%"
```

### Priority Order

When custom fields are set, the system uses this priority:

1. **`customJSON`** (if set) - Full custom JSON embed, highest priority
2. **`customTitle`/`customDescription`** (if set) - Custom title/description
3. **Default behavior** - Standard embed formatting

### Limitations

- Title: Maximum 256 characters (Discord API limit)
- Description: Maximum 4096 characters (Discord API limit)
- If limits are exceeded, the text will be automatically truncated with a warning logged

For more detailed information, see [EMBED_CUSTOMIZATION.md](EMBED_CUSTOMIZATION.md).

---

## Message Pattern Matching

### Overview

The message pattern matching system allows you to intercept and replace console/log messages before they're sent to Discord. This is useful for:
- Filtering out unwanted messages
- Reformatting messages
- Routing messages to different channels
- Creating custom embeds for specific events

### Configuration

Enable pattern matching in `config/Discord-Integration.toml`:

```toml
[messagePatterns]
enabled = true

[[messagePatterns.patterns]]
pattern = "your-regex-pattern"
replacement = "replacement message"
suppressOriginal = true
channelID = "default"
asEmbed = false
embedTitle = ""
embedDescription = ""
embedColor = "#808080"
```

### Pattern Matching Fields

#### `pattern` (Required)
- **Type:** String (Regex pattern)
- **Description:** Regular expression to match against console/log messages
- **Note:** Matching is case-insensitive by default
- **Example:** `"(?i).*backup.*start.*"` matches any message containing "backup" and "start"

#### `replacement` (Optional)
- **Type:** String
- **Description:** Custom message to send if pattern matches
- **Supports:** Capture groups `$1`, `$2`, etc. from regex
- **If empty:** Original message is suppressed (if `suppressOriginal = true`)

#### `suppressOriginal` (Default: `true`)
- **Type:** Boolean
- **Description:** If `true`, the original message won't be sent to Discord
- **If `false`:** Both original and replacement messages are sent

#### `channelID` (Default: `"default"`)
- **Type:** String
- **Description:** Target channel for the replacement message
- **`"default"`:** Uses the original message's intended channel (e.g., advancementChannelID, serverChannelID)
- **Channel ID:** Specific Discord channel ID to override routing

#### `asEmbed` (Default: `false`)
- **Type:** Boolean
- **Description:** Send replacement as an embed instead of plain text

#### `embedTitle` (Only if `asEmbed = true`)
- **Type:** String
- **Description:** Embed title
- **Supports:** Capture groups `$1`, `$2`, etc.

#### `embedDescription` (Only if `asEmbed = true`)
- **Type:** String
- **Description:** Embed description
- **Supports:** Capture groups `$1`, `$2`, etc.

#### `embedColor` (Only if `asEmbed = true`)
- **Type:** String (Hex color code)
- **Description:** Embed color
- **Default:** `#808080` (gray)
- **Example:** `#FFD700` (gold), `#00FF00` (green)

### Pattern Evaluation

- Patterns are evaluated **in order** (top to bottom)
- **First matching pattern wins** - subsequent patterns are not checked
- If no pattern matches, the original message is sent normally

### Examples

#### Example 1: Backup Notifications

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*backup.*start.*"
replacement = "🧰 World backup started"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Backup Started"
embedDescription = "A world backup has been initiated"
embedColor = "#FFD700"
```

**Matches:**
- `[INFO] World backup started successfully`
- `[BACKUP] Starting backup process...`
- `Backup started at 12:00 PM`

**Result:** Sends a custom embed instead of the original log message.

#### Example 2: Filter Out Spam Messages

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*spam.*message.*"
replacement = ""
suppressOriginal = true
channelID = "default"
asEmbed = false
```

**Result:** Completely suppresses messages matching the pattern.

#### Example 3: Capture Group Replacement

```toml
[[messagePatterns.patterns]]
pattern = "(?i)player\\s+(\\w+)\\s+joined"
replacement = "👋 Welcome $1!"
suppressOriginal = true
channelID = "default"
asEmbed = false
```

**Matches:** `Player Steve joined`
**Result:** Sends `👋 Welcome Steve!` instead

#### Example 4: Route to Different Channel

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*error.*"
replacement = "⚠️ Error detected: $0"
suppressOriginal = true
channelID = "123456789012345678"  # Your error log channel ID
asEmbed = true
embedTitle = "Server Error"
embedDescription = "$0"
embedColor = "#FF0000"
```

**Result:** Error messages are routed to a specific channel with a red embed.

#### Example 5: Multiple Patterns (Order Matters)

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*critical.*error.*"
replacement = "🚨 CRITICAL: $0"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Critical Error"
embedDescription = "$0"
embedColor = "#FF0000"

[[messagePatterns.patterns]]
pattern = "(?i).*error.*"
replacement = "⚠️ Error: $0"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Error"
embedDescription = "$0"
embedColor = "#FFA500"
```

**Note:** The "critical error" pattern must come first, otherwise all errors (including critical) will match the second pattern.

### Regex Tips

- Use `(?i)` at the start for case-insensitive matching
- Use `.*` to match any characters
- Use `\\s+` to match whitespace
- Use `(\\w+)` to capture words
- Use `$1`, `$2`, etc. in replacement to reference capture groups
- Use `$0` to reference the entire matched text

### Common Use Cases

1. **Backup Notifications** - Format backup start/complete messages
2. **Error Filtering** - Route errors to specific channels
3. **Plugin Messages** - Customize plugin-specific messages
4. **Spam Filtering** - Suppress unwanted log messages
5. **Status Updates** - Create embeds for server status changes

For more detailed information, see [MESSAGE_PATTERN_MATCHING.md](MESSAGE_PATTERN_MATCHING.md).

---

## Startup Time Tracking

### Overview

Discord Integration now tracks server startup time and displays it in the "Server Started" message. This helps you monitor server performance and identify slow startups.

### How It Works

1. **Early Initialization** - The mod loads early (before most other mods) and sends the "Server Starting..." message as soon as Discord is connected
2. **Time Tracking** - The timestamp when the message is sent is recorded
3. **Completion** - When the server fully starts, the message is updated with the elapsed time

### Configuration

No configuration needed! This feature works automatically.

### Example Output

The "Server Started" message will automatically include startup time:

- **Short startup:** `Server Started (Started in 45s)`
- **Long startup:** `Server Started (Started in 2m 15s)`

### Custom JSON Support

If you use `customJSON` for start messages, you can use the `%startupTime%` placeholder:

```json
{
  "title": "Server Started",
  "description": "The server is now online!%startupTime%",
  "color": 65280
}
```

**Result:** `The server is now online! (Started in 45s)`

---

## Additional Resources

- [Embed Customization Guide](EMBED_CUSTOMIZATION.md) - Detailed embed customization documentation
- [Message Pattern Matching Guide](MESSAGE_PATTERN_MATCHING.md) - Detailed pattern matching documentation
- [Main README](README.md) - General mod information
- [Wiki](https://wiki.erdbeerbaerlp.de/dcintegration:root) - Official documentation

---

## Support

If you have questions or need help:
- Check the [Discord Server](https://erd.wtf/discord)
- Visit the [GitHub Issues](https://github.com/ErdbeerbaerLP/Discord-Chat-Integration/issues)
- Read the [Wiki](https://wiki.erdbeerbaerlp.de/dcintegration:root)

