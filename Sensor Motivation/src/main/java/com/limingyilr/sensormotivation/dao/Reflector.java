package com.limingyilr.sensormotivation.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanqx on 2014/5/8.
 */
public class Reflector {

    private Class kind;
    private Field[] fields;
    private Method[] methods;

    public Reflector(Class kind) {
        this.kind = kind;
        reflect();
    }

    private void reflect() {
        fields = kind.getDeclaredFields();
        methods = kind.getDeclaredMethods();
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }
}
