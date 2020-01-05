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

package com.flowref.core.reference;

import static org.junit.Assert.assertEquals;

import com.flowref.core.dsl.TestFlows;
import com.flowref.core.flow.reference.LambdaMeta;
import com.flowref.core.flow.reference.LambdaUtil;
import com.flowref.core.flow.reference.MethodReference;
import com.flowref.core.flow.reference.SerializableBiConsumer;
import com.flowref.core.flow.reference.SerializableTriConsumer;
import org.junit.Test;

public class LambdaUtilUT {


  public void f(int i, int j) {

  }
  public <T, U> MethodReference from(SerializableBiConsumer<T, U> consumer) {
    return consumer;
  }

  public <T, U, V> MethodReference from(SerializableTriConsumer<T, U, V> consumer) {
    return consumer;
  }

  @Test
  public void testMethodTargetIsObtained() {

    LambdaUtilUT test = new LambdaUtilUT();

    MethodReference methodReference = test.from(test::f);
    LambdaMeta lambdaMeta = LambdaUtil.getLambdaMeta(methodReference);

    assertEquals("f", lambdaMeta.getImplMethod().getName());
    assertEquals(test, lambdaMeta.getTarget());

    lambdaMeta = LambdaUtil.getLambdaMeta(test.from(TestFlows::processTwo));
    assertEquals(TestFlows.class, lambdaMeta.getImplClass());
    assertEquals(null, lambdaMeta.getTarget());

  }

}
