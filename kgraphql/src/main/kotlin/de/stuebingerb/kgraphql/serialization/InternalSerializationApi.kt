package de.stuebingerb.kgraphql.serialization

/**
 * This annotation marks all classes and methods that are part of the internal serialization. They are not
 * intended for public use, they can be changed or removed at any time.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class InternalSerializationApi
