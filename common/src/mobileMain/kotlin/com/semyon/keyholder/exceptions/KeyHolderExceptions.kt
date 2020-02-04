package com.semyon.keyholder.exceptions

open class KeyHolderException(msg: String) : Throwable(msg)

class EmptyUserNameException : KeyHolderException("Empty username.")
class EmptyTelegramNickname : KeyHolderException("Empty telegram nickname.")
class KeyAlreadyTakenException : KeyHolderException("Key already taken.")
class NotOwnerException : KeyHolderException("You can't return key. You don't have one")