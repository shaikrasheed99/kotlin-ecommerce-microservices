package com.ecommerce.notificationservice.utils

object EntityUtils {
    fun Any.getMethodAnnotations(methodName: String): Array<Annotation> {
        return this.javaClass.declaredMethods.first { it.name == methodName }.annotations
    }

    fun Any.getAttributeAnnotations(attributeName: String): Array<Annotation> {
        return this.javaClass.declaredFields.first { it.name == attributeName }.annotations
    }
}
