package icu.takeneko.whitecat.mirai.command

import icu.takeneko.whitecat.mirai.data.Data.config

val whitelistCommand = LiteralCommand("wl") {
    literal("req") {
        literal("a") {
            requires { this.groupUin in config.get().availableGroups }
            wordArgument("playerName") {
                execute {
                    0
                }
                literal("into") {
                    wordArgument("serverTarget") {
                        execute {
                            0
                        }
                    }
                }
            }
        }
        literal("r") {
            requires { this.groupUin in config.get().availableGroups }
            wordArgument("playerName") {
                execute {
                    0
                }
                literal("into") {
                    wordArgument("serverTarget") {
                        execute {
                            0
                        }
                    }
                }
            }
        }

        literal("p") {
            requires { this.senderUin in config.get().allOperators }
            execute {
                0
            }
        }
        literal("cl") {
            requires { this.senderUin in config.get().allOperators }
            execute {
                0
            }
        }
    }
    literal("tgt") {
        requires { this.groupUin in config.get().availableGroups || this.senderUin in config.get().allOperators }
        execute {
            0
        }
    }
    literal("approve") {
        requires { this.senderUin in config.get().allOperators }
        wordArgument("playerName") {
            execute {
                0
            }
            literal("into") {
                wordArgument("serverTarget") {
                    execute {
                        0
                    }
                }
            }
        }
    }
}
