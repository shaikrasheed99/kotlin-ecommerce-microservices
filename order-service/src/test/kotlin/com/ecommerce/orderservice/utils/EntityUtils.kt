package com.ecommerce.orderservice.utils

object EntityUtils {
    fun Any.getMethodAnnotations(methodName: String): Array<Annotation> {
        return this.javaClass.declaredMethods.first { it.name == methodName }.annotations
    }

    fun Any.getMethodParameterAnnotations(methodName: String, parameterName: String): Array<out Annotation> {
        return this.javaClass
            .declaredMethods.first { it.name == methodName }
            .parameters.first { it.name == parameterName }.annotations
    }
}
