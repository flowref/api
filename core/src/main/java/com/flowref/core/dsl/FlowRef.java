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
import com.flowref.core.dsl.command.FromCommand;
import com.flowref.core.flow.build.FlowInstanceBuilder;
import com.flowref.core.flow.build.FlowRegistry;
import com.flowref.core.flow.reference.MethodReference;
import com.flowref.core.flow.reference.SerializableBiConsumer;
import com.flowref.core.flow.reference.SerializableConsumer;
import com.flowref.core.flow.reference.SerializableHeptaConsumer;
import com.flowref.core.flow.reference.SerializableHexaConsumer;
import com.flowref.core.flow.reference.SerializablePentaConsumer;
import com.flowref.core.flow.reference.SerializableSupplier;
import com.flowref.core.flow.reference.SerializableTetraConsumer;
import com.flowref.core.flow.reference.SerializableTriConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class containing FlowRef DSL. Fluent API with minimal set of commands.
 */
public class FlowRef implements Workflow {

  private static final Log LOG = LogFactory.getLog(FlowRef.class);

  protected FlowDefinition flowDefinition;

  public static FlowRef flow() {
    return new FlowRef();
  }

  public FlowDefinition getFlowDefinition() {
    return flowDefinition;
  }

  protected <FLOW_CLASS> FlowBase<FLOW_CLASS> build(MethodReference<FLOW_CLASS> flowRef) {
    flowDefinition = new FlowDefinition(flowRef);
    flowDefinition.add(new FromCommand(flowRef));
    FlowBase<FLOW_CLASS> flowBase = new FlowBase();
    flowBase.setFlowDefinition(flowDefinition);
    FlowInstanceBuilder.build(flowDefinition);
    return flowBase;
  }

  /**
   * Call it to start the definition of a workflow.
   */
  public <FLOW_CLASS> FlowBase from(SerializableSupplier<FLOW_CLASS> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS> FlowBase from(SerializableConsumer<FLOW_CLASS> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS, U> FlowBase from(SerializableBiConsumer<FLOW_CLASS, U> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS, U, V> FlowBase<FLOW_CLASS> from(SerializableTriConsumer<FLOW_CLASS, U, V> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS, U, V, X> FlowBase<FLOW_CLASS> from(SerializableTetraConsumer<FLOW_CLASS, U, V, X> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS, U, V, X, Y> FlowBase<FLOW_CLASS> from(
      SerializablePentaConsumer<FLOW_CLASS, U, V, X, Y> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS, U, V, X, Y, Z> FlowBase<FLOW_CLASS> from(
      SerializableHexaConsumer<FLOW_CLASS, U, V, X, Y, Z> consumer) {
    return build(consumer);
  }

  public <FLOW_CLASS, U, V, X, Y, Z, A> FlowBase<FLOW_CLASS> from(
      SerializableHeptaConsumer<FLOW_CLASS, U, V, X, Y, Z, A> consumer) {
    return build(consumer);
  }

  /**
   * There are several options to obtain a flow in order to execute it:<br>
   * <ul>
   * <li>1. Directly from definition: <code>MyFlows myFlows = FlowRef.flow().from(MyFlows::flow1).to(...).build();</code>
   * <li>2. If you use Spring just autowire/inject it from everywhere:
   * <code>@Autowired
   * MyFlows myFlows;
   * </code>
   * <li>3. Using this method.
   * </ul>
   */
  public static <T> T getFlow(Class<T> flowClass) {
    return FlowRegistry.getFlow(flowClass);
  }


}
