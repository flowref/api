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
import com.flowref.core.dsl.command.FlowCommand;
import com.flowref.core.dsl.command.ToCommand;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * For each interface for which we define flows there will be a single proxy(flow instance) doing the magic.
 *
 * JDK dynamic proxying infrastructure (from the java.lang.reflect package) that is only capable of creating proxies for interfaces.
 * https://spring.io/blog/2007/07/19/debunking-myths-proxies-impact-performance/
 *
 * TODO: repackage cglib inside so that no conflicts will arise. See: https://stackoverflow.com/questions/41478307/whats-the-difference-between-spring-cglib-and-cglib
 * https://docs.spring.io/spring/docs/2.5.x/reference/aop.html#aop-proxying
 * http://stackoverflow.com/questions/10664182/what-is-the-difference-between-jdk-dynamic-proxy-and-cglib
 * https://gist.github.com/premraj10/3a3eac42a72c32de3a41ec13ef3d56ad
 * https://github.com/edc4it/jpa-case/blob/master/src/main/java/util/JPAUtil.java
 * If you will want more flexible behavior like having the flows inside a non interface then you will need to use cglib or bytebuddy
 */
public class FlowInvocationHandler implements InvocationHandler {

  static final Log LOG = LogFactory.getLog(FlowInvocationHandler.class);
  private Map<String, FlowDefinition> method2FlowDefinition = new ConcurrentHashMap<>();

  public void registerFlowDefinition(FlowDefinition flowDefinition) {
    method2FlowDefinition.put(flowDefinition.getFlowReference().getImplMethod().getName(), flowDefinition);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    LOG.info("Invoking method: " + method.getName());

    FlowDefinition flowDefinition = method2FlowDefinition.get(method.getName());
    Object result = null;
    List<FlowCommand> commands = flowDefinition.getFlowCommands();
    for (FlowCommand command : commands) {
      if (command instanceof ToCommand) {
        result = ((ToCommand)command).run(args);
      }
    }

    return result;
  }
}
