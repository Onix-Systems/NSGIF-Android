package com.libnsgif.entity.exception

/**
 * An exception indicating that the provided destination array has the wrong size for a specific operation.
 *
 * This exception is thrown when an operation, such as copying pixel data, requires the destination array to have
 * specific dimensions (e.g., matching the dimensions of a GIF frame), and the provided array does not meet these
 * requirements.
 *
 * @constructor Creates a [WrongArraySizeException] instance.
 */
class WrongArraySizeException : Throwable()