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

import com.flowref.core.Workflow;
import com.flowref.core.dsl.command.ToCommand;
import com.flowref.core.flow.build.FlowRegistry;
import com.flowref.core.flow.reference.SerializableBiConsumer;
import com.flowref.core.flow.reference.SerializableConsumer;
import com.flowref.core.flow.reference.SerializableSupplier;
import com.flowref.core.flow.reference.SerializableVoidConsumerVoidSupplier;

/**
 * Workflow base class containing the base DSL flowCommands.
 * Subclasses of this class add more DSL flowCommands and/or restrain the DSL in a consistent way.
 */
public class FlowBase<FLOW_CLASS> implements Workflow<FLOW_CLASS> {


  FlowDefinition flowDefinition;
  Class<FLOW_CLASS> flowClass;

  public Class<FLOW_CLASS> getFlowClass() {
    return flowClass;
  }

  public void setFlowClass(Class<FLOW_CLASS> flowClass) {
    this.flowClass = flowClass;
  }

  public FlowDefinition getFlowDefinition() {
    return flowDefinition;
  }

  protected void setFlowDefinition(FlowDefinition flowDefinition) {
    this.flowDefinition = flowDefinition;
  }

  // For methods that do not have arguments and return void.
  public FlowBase<FLOW_CLASS> from(SerializableVoidConsumerVoidSupplier consumer) {
    flowDefinition.add(new ToCommand(consumer));
    return this;
  }

  public <T> FlowBase<FLOW_CLASS> to(SerializableSupplier<T> consumer) {
    flowDefinition.add(new ToCommand(consumer));
    return this;
  }

  public <T> FlowBase<FLOW_CLASS> to(SerializableConsumer<T> consumer) {
    flowDefinition.add(new ToCommand(consumer));
    return this;
  }

  public <T, U> FlowBase<FLOW_CLASS> to(SerializableBiConsumer<T, U> consumer) {
    flowDefinition.add(new ToCommand(consumer));
    return this;
  }

  public FLOW_CLASS build() {
    return (FLOW_CLASS) FlowRegistry.getFlow(flowDefinition.getFlowReference().getImplClass());
  }
}
