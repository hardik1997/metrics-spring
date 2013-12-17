/**
 * Copyright (C) 2012 Ryan W Tenney (ryan@10e.us)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ryantenney.metrics.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils.FieldFilter;
import org.springframework.util.ReflectionUtils.MethodFilter;

import static org.springframework.util.ReflectionUtils.USER_DECLARED_METHODS;

import static java.lang.reflect.Modifier.*;

class AnnotationFilter implements MethodFilter, FieldFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationFilter.class);

	public static final int FIELDS = fieldModifiers();
	public static final int INJECTABLE_FIELDS = FIELDS ^ (FINAL | STATIC);
	public static final int INSTANCE_FIELDS = FIELDS ^ STATIC;

	public static final int METHODS = methodModifiers();
	public static final int INSTANCE_METHODS = METHODS ^ (ABSTRACT | STATIC);
	public static final int PROXYABLE_METHODS = METHODS ^ (ABSTRACT | FINAL | PRIVATE | STATIC);

	private final Class<? extends Annotation> clazz;
	private final int methodModifiers;
	private final int fieldModifiers;

	public AnnotationFilter(final Class<? extends Annotation> clazz) {
		this(clazz, METHODS, FIELDS);
	}

	public AnnotationFilter(final Class<? extends Annotation> clazz, final int modifiers) {
		this(clazz, modifiers, modifiers);
	}

	public AnnotationFilter(final Class<? extends Annotation> clazz, final int methodModifiers, final int fieldModifiers) {
		this.clazz = clazz;
		this.methodModifiers = methodModifiers & METHODS;
		this.fieldModifiers = fieldModifiers & FIELDS;
	}

	@Override
	public boolean matches(Method method) {
		return USER_DECLARED_METHODS.matches(method) && method.isAnnotationPresent(clazz) && checkModifiers(method, methodModifiers);
	}

	@Override
	public boolean matches(Field field) {
		return field.isAnnotationPresent(clazz) && checkModifiers(field, fieldModifiers);
	}

	private boolean checkModifiers(Member member, int allowed) {
		int modifiers = member.getModifiers();
		return (modifiers & allowed) == modifiers;
	}

	@Override
	public String toString() {
		return "[AnnotationFilter: @" + clazz.getSimpleName() + ", methodModifiers: (" + Modifier.toString(methodModifiers) + "), fieldModifiers: (" + Modifier.toString(fieldModifiers) + ")]";
	}

}
