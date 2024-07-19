# VioletProject
A Kotlin-based telegram bot that implements some features that are commonly not available in regular moderator bots.

### Commands
"-" is the default prefix. You can change it in the `Config.kt`.

#### PS. <> is optional argument, () is required
#### PPS. restrictions do not apply to administrators

- `-banstickerpack do=(<s>mute/<s>kick/<s>ban/delete) <time> <reason>` -- restricts the use of a particular stickerpack in group chat. Mutes, bans, kicks a user who used a sticker from that pack, or deletes their message
- `-unbanstickerpack` -- removes the restriction on sending stickers from a specific pack.
- `-blockcards do=(<s>mute/<s>kick/<s>ban/delete) <time> <reason>` -- restricts to send card numbers (like `4242 4242 4242 4242`)
- `-unblockcards` -- removes the restriction on sending card numbers
- `-addnote (note_name_without_#) <note text or reply to a media message to save it>` -- adds a #note. If you reply #notename to any message, the bot will delete yours and send the text/media in a reply message
- `-removenote #(note_name_without_#)` -- deletes #note
- `-subscribe` -- buy pro subscription


## Running

```shell
gradlew run --args="(BOT_TOKEN) (PAYMENT_TOKEN)"
```

## Building

```shell
gradlew clean build
```

Then copy your freshly made jar file from `build/libs/` (by default `build/libs/VioletProject-[version].jar`). Afterward, run it like this:

```shell
java -jar VioletProject-[version].jar (BOT_TOKEN) (PAYMENT_TOKEN)
```
