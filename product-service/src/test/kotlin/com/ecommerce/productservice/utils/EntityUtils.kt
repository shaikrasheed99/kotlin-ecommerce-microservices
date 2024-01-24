package com.ecommerce.productservice.utils

object EntityUtils {
    fun Any.getAttributeAnnotations(attributeName: String): Array<Annotation> {
        return this.javaClass.declaredFields.first { it.name == attributeName }.annotations
    }

    fun Any.getMethodAnnotations(methodName: String): Array<Annotation> {
        return this.javaClass.declaredMethods.first { it.name == methodName }.annotations
    }

    fun Any.getMethodParameterAnnotations(methodName: String, parameterName: String): Array<out Annotation> {
        return this.javaClass
            .declaredMethods.first { it.name == methodName }
            .parameters.first { it.name == parameterName }.annotations
    }
}