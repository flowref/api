/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flowref.core.flow.reference;

import com.flowref.core.exception.FlowRefException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Used to obtain method reference metadata.
 * Code inspired by:
 * https://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html
 * http://benjiweber.co.uk/blog/2015/08/17/lambda-parameter-names-with-reflection/
 * https://dzone.com/articles/how-and-why-to-serialialize-lambdas
 * https://stackoverflow.com/questions/22807912/how-to-serialize-a-lambda
 * https://in.relation.to/2016/04/14/emulating-property-literals-with-java-8-method-references/
 * https://github.com/ninjaframework/ninja/blob/develop/ninja-core/src/main/java/ninja/utils/Lambdas.java
 */
public class LambdaUtil {

  public static SerializedLambda getSerializedLambda(Object lambda) {

    for (Class<?> clazz = lambda.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
      try {
        Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
        replaceMethod.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) replaceMethod.invoke(lambda);

        return serializedLambda;
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("not a lambda");
      } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new RuntimeException("can not serialize lambda", e);
      } catch (NoSuchMethodError e) {
        // continue
      }
    }
    throw new RuntimeException("'writeReplace' not found");
  }

  public static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException, ClassNotFoundException {
    while (clazz != null) {
      for (Method method : clazz.getDeclaredMethods()) {
        if (method.getName().equals(methodName)) {
          return method;
        }
      }
      // check implemented interfaces also
      for (Class<?> interfaceClass : clazz.getInterfaces()) {
        for (Method method : interfaceClass.getDeclaredMethods()) {
          return method;
        }
      }
      clazz = clazz.getSuperclass();
    }

    throw new FlowRefException(new NoSuchMethodException("Could not find method " + methodName + " in " + clazz));
  }

  public static LambdaMeta getLambdaMeta(MethodReference lambda) {
    LambdaMeta lambdaMeta = new LambdaMeta();
    SerializedLambda serializedLambda = getSerializedLambda(lambda);
    try {
      // "package/subpackage/.../Class" format.
      lambdaMeta.setImplClass(Class.forName(serializedLambda.getImplClass().replace('/', '.')));
    } catch (ClassNotFoundException e) {
      throw new FlowRefException("Can not find class of method reference", e);
    }
    try {
      lambdaMeta.setImplMethod(getMethod(lambdaMeta.getImplClass(), serializedLambda.getImplMethodName()));
    } catch (NoSuchMethodException e) {
      throw new FlowRefException("Can not find method", e);
    } catch (ClassNotFoundException e) {
      throw new FlowRefException("Can not find class", e);
    }
    if (serializedLambda.getCapturedArgCount() > 0) {
      lambdaMeta.setTarget(serializedLambda.getCapturedArg(0));
    }
    return lambdaMeta;
  }
}
