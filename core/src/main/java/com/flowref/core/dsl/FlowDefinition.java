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

package com.flowref.core.dsl;

import com.flowref.core.dsl.command.FlowCommand;
import com.flowref.core.flow.reference.LambdaMeta;
import com.flowref.core.flow.reference.LambdaUtil;
import com.flowref.core.flow.reference.MethodReference;
import java.util.ArrayList;
import java.util.List;

public class FlowDefinition<T> {
  String id;
  List<FlowCommand> flowCommands = new ArrayList<>();

  LambdaMeta flowReference;  // This is the start.

  public FlowDefinition(MethodReference flowRef) {
    flowReference = LambdaUtil.getLambdaMeta(flowRef);
    id = flowReference.getImplClass().getCanonicalName() + "#" + flowReference.getImplMethod().getName();
  }

  public LambdaMeta getFlowReference() {
    return flowReference;
  }

  public String getId() {
    return id;
  }

  public List<FlowCommand> getFlowCommands() {
    return flowCommands;
  }

  public void add(FlowCommand flowCommand) {
    /**
     * TODO: Validation when the flow is constructed will not be possible (the flow might end with .when()), thus
     * the only solution would be to validate it at least on the first run but also validate all of them after
     * application is started (appcontext started with Spring) or if no Spring call FlowRef.validateFlows();
     */
    flowCommands.add(flowCommand);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (FlowCommand flowCommand : flowCommands) {
      sb.append(flowCommand.getClass().getSimpleName());
    }
    return sb.toString();
  }


}
