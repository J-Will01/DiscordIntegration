# Message Pattern Matching Guide

This guide explains how to use the message pattern matching system to intercept, filter, and customize console/log messages before they're sent to Discord.

## Table of Contents

1. [Overview](#overview)
2. [Configuration](#configuration)
3. [Pattern Syntax](#pattern-syntax)
4. [Capture Groups](#capture-groups)
5. [Examples](#examples)
6. [Channel Routing](#channel-routing)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)

---

## Overview

The message pattern matching system allows you to:
- ✅ Intercept console/log messages before they reach Discord
- ✅ Replace messages with custom text
- ✅ Suppress unwanted messages
- ✅ Route messages to different Discord channels
- ✅ Create custom embeds for specific events
- ✅ Use regex capture groups for dynamic content

### How It Works

1. **Pattern Matching** - When a message is about to be sent to Discord, it's checked against your configured patterns
2. **First Match Wins** - Patterns are evaluated in order; the first matching pattern is used
3. **Replacement** - If a pattern matches, the replacement message is sent (or original is suppressed)
4. **Channel Routing** - Messages can be routed to specific channels or use the default channel

---

## Configuration

### Enable Pattern Matching

In `config/Discord-Integration.toml`:

```toml
[messagePatterns]
enabled = true  # Set to false to disable pattern matching
```

### Add Patterns

```toml
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

### Pattern Fields

| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `pattern` | String | ✅ Yes | - | Regex pattern to match messages |
| `replacement` | String | ❌ No | `""` | Custom message to send (supports `$1`, `$2`, etc.) |
| `suppressOriginal` | Boolean | ❌ No | `true` | If `true`, original message is not sent |
| `channelID` | String | ❌ No | `"default"` | Target channel ID or `"default"` |
| `asEmbed` | Boolean | ❌ No | `false` | Send replacement as embed |
| `embedTitle` | String | ❌ No | `""` | Embed title (if `asEmbed = true`) |
| `embedDescription` | String | ❌ No | `""` | Embed description (if `asEmbed = true`) |
| `embedColor` | String | ❌ No | `#808080` | Embed color hex code (if `asEmbed = true`) |

---

## Pattern Syntax

### Basic Patterns

Patterns use **Java regular expressions**. Matching is case-insensitive by default.

#### Simple Text Match

```toml
pattern = "backup"
```

Matches any message containing "backup" (case-insensitive).

#### Case-Sensitive Match

```toml
pattern = "(?-i)Backup"
```

The `(?-i)` flag makes matching case-sensitive.

#### Word Boundaries

```toml
pattern = "\\bbackup\\b"
```

Matches "backup" as a whole word, not as part of "backupstart".

#### Start/End of Message

```toml
pattern = "^backup started$"
```

Matches messages that start with "backup" and end with "started".

#### Multiple Words

```toml
pattern = ".*backup.*start.*"
```

Matches messages containing both "backup" and "start" in any order.

### Common Regex Patterns

| Pattern | Matches |
|---------|---------|
| `.*` | Any characters (zero or more) |
| `.+` | Any characters (one or more) |
| `\\d+` | One or more digits |
| `\\w+` | One or more word characters (letters, digits, underscore) |
| `\\s+` | One or more whitespace characters |
| `[0-9]+` | One or more digits (alternative syntax) |
| `[a-zA-Z]+` | One or more letters |

---

## Capture Groups

Capture groups allow you to extract parts of the matched message and use them in replacements.

### Basic Capture Groups

Use parentheses `()` to create capture groups:

```toml
pattern = "player (\\w+) joined"
replacement = "Welcome $1!"
```

**Matches:** `Player Steve joined`
**Result:** `Welcome Steve!`

### Multiple Capture Groups

```toml
pattern = "(\\w+) earned (\\w+)"
replacement = "$1 just got $2!"
```

**Matches:** `Steve earned Diamonds`
**Result:** `Steve just got Diamonds!`

### Capture Group Reference

- `$0` - Entire matched text
- `$1` - First capture group
- `$2` - Second capture group
- `$3` - Third capture group
- etc.

### Example: Extracting Player Names

```toml
pattern = "player\\s+(\\w+)\\s+joined"
replacement = "👋 Welcome $1!"
```

**Matches:** `Player Steve joined`
**Result:** `👋 Welcome Steve!`

---

## Examples

### Example 1: Backup Notifications

**Configuration:**
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

**Result:** Sends a custom gold embed instead of the original log message.

### Example 2: Filter Out Spam

**Configuration:**
```toml
[[messagePatterns.patterns]]
pattern = "(?i).*spam.*message.*"
replacement = ""
suppressOriginal = true
channelID = "default"
asEmbed = false
```

**Result:** Completely suppresses messages matching the pattern.

### Example 3: Player Join Customization

**Configuration:**
```toml
[[messagePatterns.patterns]]
pattern = "(?i)player\\s+(\\w+)\\s+joined"
replacement = "👋 Welcome $1!"
suppressOriginal = true
channelID = "default"
asEmbed = false
```

**Matches:** `Player Steve joined`
**Result:** `👋 Welcome Steve!`

### Example 4: Error Routing

**Configuration:**
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

### Example 5: Plugin Messages

**Configuration:**
```toml
[[messagePatterns.patterns]]
pattern = "(?i)\\[(\\w+)\\]\\s+(.*)"
replacement = "🔌 [$1] $2"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Plugin: $1"
embedDescription = "$2"
embedColor = "#5865F2"
```

**Matches:** `[Economy] Player balance updated`
**Result:** Embed with title "Plugin: Economy" and description "Player balance updated"

### Example 6: Multiple Patterns (Order Matters)

**Configuration:**
```toml
# Critical errors first (more specific)
[[messagePatterns.patterns]]
pattern = "(?i).*critical.*error.*"
replacement = "🚨 CRITICAL: $0"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Critical Error"
embedDescription = "$0"
embedColor = "#FF0000"

# Regular errors second (less specific)
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

**Important:** More specific patterns must come first, otherwise they'll be matched by less specific patterns.

### Example 7: Server Status Updates

**Configuration:**
```toml
[[messagePatterns.patterns]]
pattern = "(?i).*server.*start.*"
replacement = "🟢 Server starting..."
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "Server Status"
embedDescription = "The server is starting up"
embedColor = "#00FF00"
```

### Example 8: World Save Notifications

**Configuration:**
```toml
[[messagePatterns.patterns]]
pattern = "(?i).*saved.*world.*"
replacement = "💾 World saved successfully"
suppressOriginal = true
channelID = "default"
asEmbed = true
embedTitle = "World Saved"
embedDescription = "The world has been saved"
embedColor = "#00CED1"
```

---

## Channel Routing

### Default Channel

Set `channelID = "default"` to use the original message's intended channel:
- Advancement messages → `advancementChannelID`
- Server messages → `serverChannelID`
- Chat messages → `chatOutputChannelID`
- etc.

### Specific Channel

Set `channelID` to a Discord channel ID to route messages there:

```toml
channelID = "123456789012345678"
```

**How to get a channel ID:**
1. Enable Developer Mode in Discord (User Settings → Advanced → Developer Mode)
2. Right-click the channel → Copy ID

### Example: Error Log Channel

```toml
[[messagePatterns.patterns]]
pattern = "(?i).*error.*"
replacement = "⚠️ $0"
suppressOriginal = true
channelID = "987654321098765432"  # Your error log channel
asEmbed = true
embedTitle = "Error"
embedDescription = "$0"
embedColor = "#FF0000"
```

---

## Best Practices

### 1. Order Patterns by Specificity

More specific patterns should come first:

✅ **Good:**
```toml
# Specific first
pattern = "(?i).*critical.*error.*"

# General second
pattern = "(?i).*error.*"
```

❌ **Bad:**
```toml
# General first (will match everything)
pattern = "(?i).*error.*"

# Specific second (never reached)
pattern = "(?i).*critical.*error.*"
```

### 2. Use Case-Insensitive Matching

Most log messages have inconsistent capitalization. Use `(?i)` for case-insensitive matching:

✅ **Good:**
```toml
pattern = "(?i).*backup.*"
```

❌ **Bad:**
```toml
pattern = ".*Backup.*"  # Won't match "backup" or "BACKUP"
```

### 3. Test Your Patterns

Test patterns before deploying:

1. Enable pattern matching
2. Restart server
3. Trigger the event (e.g., start a backup)
4. Check Discord for the result
5. Adjust if needed

### 4. Use Capture Groups Wisely

Capture groups make replacements dynamic:

✅ **Good:**
```toml
pattern = "player (\\w+) joined"
replacement = "Welcome $1!"
```

❌ **Bad:**
```toml
pattern = "player.*joined"
replacement = "Welcome Steve!"  # Hardcoded name
```

### 5. Keep Patterns Simple

Simple patterns are easier to maintain:

✅ **Good:**
```toml
pattern = "(?i).*backup.*start.*"
```

❌ **Bad:**
```toml
pattern = "(?i)^(?:.*?backup.*?start.*?|.*?start.*?backup.*?)$"
```

### 6. Document Your Patterns

Add comments in your config:

```toml
# Route critical errors to error channel
[[messagePatterns.patterns]]
pattern = "(?i).*critical.*error.*"
channelID = "123456789012345678"
```

---

## Troubleshooting

### Pattern Not Matching

**Problem:** Pattern doesn't match expected messages.

**Solutions:**
1. ✅ Check regex syntax (use online regex tester)
2. ✅ Verify case sensitivity (use `(?i)` for case-insensitive)
3. ✅ Check for special characters that need escaping (`\\`, `\\.`, etc.)
4. ✅ Test the pattern in a regex tester with sample messages
5. ✅ Check server logs for pattern compilation errors

### Replacement Not Working

**Problem:** Replacement message not appearing.

**Solutions:**
1. ✅ Verify `suppressOriginal = true` if you want to hide the original
2. ✅ Check that `replacement` field is set (not empty)
3. ✅ Verify capture group references (`$1`, `$2`, etc.) match your pattern
4. ✅ Check server logs for errors

### Both Original and Replacement Showing

**Problem:** Both original and replacement messages appear.

**Solutions:**
1. ✅ Set `suppressOriginal = true` to hide the original
2. ✅ Check that only one pattern matches (first match wins)

### Wrong Channel

**Problem:** Message goes to wrong channel.

**Solutions:**
1. ✅ Verify `channelID` is correct (Discord channel ID or `"default"`)
2. ✅ Check that channel ID is a string (in quotes)
3. ✅ Ensure the bot has access to the target channel

### Pattern Order Issues

**Problem:** Wrong pattern is matching.

**Solutions:**
1. ✅ Reorder patterns (more specific first)
2. ✅ Check that patterns don't overlap unexpectedly
3. ✅ Test each pattern individually

### Invalid Regex

**Problem:** Server errors about invalid regex.

**Solutions:**
1. ✅ Check regex syntax
2. ✅ Escape special characters properly (`\\.` for `.`, `\\s` for space, etc.)
3. ✅ Use online regex tester to validate
4. ✅ Check server logs for specific error messages

---

## Advanced Usage

### Complex Patterns

**Multiple Conditions:**
```toml
pattern = "(?i)(?:backup|save).*(?:start|begin)"
```

Matches messages containing ("backup" OR "save") AND ("start" OR "begin").

**Time Extraction:**
```toml
pattern = ".*at\\s+(\\d{1,2}:\\d{2})\\s+(AM|PM)"
replacement = "Event occurred at $1 $2"
```

**IP Address Extraction:**
```toml
pattern = ".*IP:\\s+(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"
replacement = "Connection from $1"
```

### Combining with Embed Customization

Pattern matching works alongside embed customization:

```toml
# Pattern matching for routing
[[messagePatterns.patterns]]
pattern = "(?i).*backup.*"
channelID = "123456789012345678"
asEmbed = true
embedTitle = "Backup Event"
embedDescription = "$0"
embedColor = "#FFD700"
```

---

## Additional Resources

- [Main Features Guide](FEATURES.md) - Overview of all new features
- [Embed Customization Guide](EMBED_CUSTOMIZATION.md) - Embed customization documentation
- [Regex Tutorial](https://regexr.com/) - Learn regular expressions
- [Wiki](https://wiki.erdbeerbaerlp.de/dcintegration:root) - Official documentation

---

## Support

Need help? Check:
- [Discord Server](https://erd.wtf/discord)
- [GitHub Issues](https://github.com/ErdbeerbaerLP/Discord-Chat-Integration/issues)
- [Wiki](https://wiki.erdbeerbaerlp.de/dcintegration:root)

