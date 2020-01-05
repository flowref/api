# FlowRef

FlowRef is the **simplest Java workflow**. <br>
Starting a flow means just invoking you own methods.

## Flow reference 
A workflow reference is a **Java method reference to an interface method**. Flowref is the one that will implement your interface method based on your flow definition. 
Thus every workflow is uniquely identified by a *workflow reference* allowing direct calls or workflow composition. 
```java
flow()
  .from(YourBusiness::feature)   // this is a FLOW REFERENCE, and YourBusiness.java is a Java interface
  .to(yourInstance::implementation);
```

## Flow execution
To execute a flow you just call the workflow reference method.
```java
@Autowire          // annotation used to inject flow implementation
MyFlows myFlows;   // your interface with workflows
// ...
myFlows.startProcessing();        // starts a flow.
myFlows.processPrintJob("input"); // starts another flow.
myFlows.handleNewUser(new UserData()); // starts another flow.
```
```@Flow``` annotation is used to autowire the flow implementation (based on Spring framework). For non Spring way see example [example](core/test/CONTRIBUTING.md)

## Flow definition 
The benefit of using Java interface methods as flow reference allows for the flow implementation to vary over time, to be run locally or remote. 
Flowref automatically creates an implementation based on the workflow definition.
```java
// Simple workflow definition
from(MyJobFlows::processJob)          // your interface containing flowRef references
 .async(MyLogFlows::syslog)           // another flow is called async
 .to(jobServiceInstance::processJob)  // existing code within an instance is called (non flow)
```
You can do both **flowRef composition** by referring to other flowRef and also code composition using the instance as the denominator: ```beanInstance::method``` 
For a working example see [example](core/test/CONTRIBUTING.md)
Limitation: Java does not allows method references for methods with same name in same class.

## Input/Output binding
There are two ways the payload/data gets passed from one step to the other inside the flow: input/output chaining and workflow session
### Method input/output chaining
The return object of one method becomes the input of the next one.
This implies that the method references can take a maximum of 1 parameter.
If the type of the return object is not the same then a FlowrefDefinitionException is thrown when the workflow definition is interpreted at runtime.
### Workflow session
There is a workflow session that is maintained as long a 
2. Workflow instance data(or session) that is kept for the durration of the flow run can be accessed by just referring it in the method parameters:
     void myMethod(User currentUser, PrintJob job) {  // instance data contains these 2 objects or else null is returned.
     }
 You will need to create the new data. Session data keeps the return of one method if the method is marked with @Session or you can initialize it the moment you use it
  void myMethod(@DataInit User currentUser, PrintJob job)
  When the method will be called if User is null then the default constructor will be called to initialise it.
  

Each time a workflow is run a workflow instance is created and thus data relative to that run can be accessed using the workflow instance.

## Runtime flow updates
Flow is editable/changeable at runtime with no code deploy needed!!!
Since a workflow can also be statically coded, the code can be generated at runtime if needed.
This implies you can also see all the flowRef with just a mouse click. 

Nice to have: a graphical flow viewer/editor.

## Spring integration
Annotate any interface that contains the flow start methods with @Flows.
This will result in a Spring bean being created that can be autowired like any other bean.

## Some of the features
### Async steps
You can execute async a workflow step by using the *.async(flowOrBean::method)* command. This will continue the flow without waiting for the result of the async call.

### Async flow
The entire flow will be async if the flow reference method returns a *Future*.
```java
interface MyFlows {
  Future processJob(String param1);
}
...
@Flow
MyFlows flows;

Future future = flows.processJob("param1");
```

## Other features
Flow export/import to JSON standard document.
Flow transactionality. JMS queue, serializable data,
Flow remoting - TODO - remote running of flow that is started locally, i.e. RPC style, scaling DSL needed.
Flowref DSL extensibility. For example you could add your own constructs to the flow DSL and still benefit of the base engine.
All you have to do is to extend the FlowRef class and add your own constructs. Please send that additional construct also to us and we may adopt it.
Remote flow running (see Spring https://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/overview.html). 

Events are also modeled like 
flowref.onEvent(EventClass::newUser)
  .to(a::b)
  .fireEvent(EventClass::existingUser)

- Defining or updating a flow from a database or UI is supported via a simple JSON based model:
```js
[
{"from": "MyFlows::authenticateUser",
  "to": "spring:MyAuthService::authorize"
  "to": "flow:MyFlows::authorize"
  "async": "java:new Util()::createLoginEvent",
  {"type": "to", "ref": "flow:MyFlows::authorize"}
  .retry(mail::sendMailForNewUser).times(1).delay(1000)
  {"type": "retry", "ref": "flow:mailService::sendMailForNewUser", retry:}
},

]
```

- method references are both building blocks and start points. 
```java
interface MyFlows {
  User createNewUser(User user);
  void deleteUser(userId);
}

@Flow
MyFlows myFlows;
..
MyMailService myMail = new MyMailService();
..

.for(myFlows::authenticateUser)
  .go(myAuth::authorize)
  .async(myMail::sendMailForNewUser)
  .retry(mail::sendMailForNewUser).times(10).delay(1000)   // Can also specify them at .from level to enforce on all callers. Or if you use Spring just use the @Retryable annotation on your beans.
  .retry(mail::sendMailForNewUser).times(10).delay(1000).multiplier(2) 
  .retry(mail::sendMailForNewUser).retryPolicy(new RetryPolicy())  // see https://docs.spring.io/spring-batch/docs/1.1.x/apidocs/org/springframework/batch/retry/RetryPolicy.html
  
   
 
 



