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

import static com.flowref.core.dsl.FlowRef.flow;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.flowref.core.Workflow;
import com.flowref.core.exception.FlowRefException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class FlowRefUT {
  private static final Log LOG = LogFactory.getLog(FlowRefUT.class);

  private void oneParam(int i) {
  }
  private static void twoParams(int i, int j) {
  }
  private void threeParams(int i, int j, int k) {
  }
  @Test
  public void testCodeCompilation() {
    flow().from("a"::length);
    flow().from(new FlowRefUT()::oneParam);
    flow().from(FlowRefUT::twoParams);
    flow().from(new FlowRefUT()::threeParams);
    flow().from(TestFlows::processZero);
    flow().from(TestFlows::processZeroWithReturn);
    flow().from(TestFlows::processOne);
    flow().from(TestFlows::processTwo);
  }

  @Test
  public void testFlowReferenceIsInterfaceMethod() {
    String s = "a";
    try {
      Workflow flow = flow().from(s::length);
    } catch (FlowRefException e) {
      assertTrue(e.getMessage().contains("needs to be a Java interface in order to define flows from it"));
      return;

    }
    fail("exception not thrown");

  }

  public static class Testu {
    public String f(String a, String b) {
      System.out.println("Params:" + a + b);
      return "a";
    }
  }
  @Test
  public void testFlowDefinition() {
    //FlowRef.flow().from(TestFlows::processTwo);

//    TestFlows testFlows = flow().from2(TestFlows::processTwo);
//    testFlows.processTwo();
    Testu testu = new Testu();

    FlowRef.flow().from(TestFlows::processTwo)
        .to(testu::f);

    TestFlows testFlows = FlowRef.getFlow(TestFlows.class);
    testFlows.processTwo("a", "b");

    TestFlows flows = FlowRef.flow().from(TestFlows::processTwo).build();
//    Testu flows2 = FlowRef.flow().from(TestFlows::processTwo).build();
    FlowBase<TestFlows> f2 = FlowRef.flow().from(TestFlows::processTwo);
  }


}
