package com.libnsgif.entity.exception

/**
 * Exception indicating that there is no GIF with the specified ID.
 *
 * This exception is thrown when an operation, such as copying pixel data, requires a valid GIF with a specific ID,
 * and no GIF exists with the provided ID.
 *
 * @constructor Creates a [NoSuchGifException] instance.
 */
class NoSuchGifException : Throwable()