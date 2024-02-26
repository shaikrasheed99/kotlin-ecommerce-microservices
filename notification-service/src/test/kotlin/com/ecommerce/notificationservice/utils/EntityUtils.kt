package com.ecommerce.notificationservice.utils

object EntityUtils {
    fun Any.getMethodAnnotations(methodName: String): Array<Annotation> {
        return this.javaClass.declaredMethods.first { it.name == methodName }.annotations
    }
}
