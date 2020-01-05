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

package com.flowref.core.flow.build;

import com.flowref.core.dsl.FlowDefinition;
import com.flowref.core.exception.FlowRefException;
import com.flowref.core.flow.reference.LambdaMeta;
import java.lang.reflect.Proxy;

public class FlowInstanceBuilder {

  public static void build(FlowDefinition flowDefinition) {
    LambdaMeta lambdaMeta = flowDefinition.getFlowReference();
    Class lambdaClass = lambdaMeta.getImplClass();

    try {
      FlowInstanceData flowInstanceData = FlowRegistry.getFlowInstanceData(lambdaClass);

      if (flowInstanceData == null) {
        synchronized (lambdaClass) {
          if (flowInstanceData == null) {
            flowInstanceData = new FlowInstanceData();
            FlowInvocationHandler flowInvocationHandler = new FlowInvocationHandler();
            flowInvocationHandler.registerFlowDefinition(flowDefinition);
            // https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html
            Object flowInstance = Proxy.newProxyInstance(
                lambdaClass.getClassLoader(),
                new Class[]{lambdaClass},
                flowInvocationHandler);
            flowInstanceData.setFlow(flowInstance);
            flowInstanceData.setFlowInvocationHandler(flowInvocationHandler);
            FlowRegistry.registerFlow(lambdaClass, flowInstanceData);
            return;
          }
        }
      }
      // Else a flow was already defined for a method in the flow class, thus use the same invocation handler.
      flowInstanceData.getFlowInvocationHandler().registerFlowDefinition(flowDefinition);

    } catch (IllegalArgumentException e) {
      if (e.getMessage().contains("not an interface")) {
        throw new FlowRefException(lambdaClass.getCanonicalName() + " needs to be a Java interface in order to define flows from it.", e);
      } else {
        throw new FlowRefException(e);
      }
    }

  }
}
