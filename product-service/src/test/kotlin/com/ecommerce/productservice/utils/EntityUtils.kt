package com.ecommerce.productservice.utils

object EntityUtils {
    fun Any.getAttributeAnnotations(attributeName: String): Array<Annotation> {
        return this.javaClass.declaredFields.first { it.name == attributeName }.annotations
    }
}