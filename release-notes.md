Byte Buddy release notes
------------------------

### 2. December 2015: version 0.7.4

- Added `TypePool` that returns precomputed `TypeDescription`s for given types.
- Fixed agent and nexus attachment and the corresponding value access.

### 30. November 2015: version 0.7.3

- Added visibility substitution for `@Super` when the instrumented type is instrumented to see changed state on a redefinition.
- Added patch for modifier information of inner classes on a redefinition.
- Added fallback for `Nexus` injection to attempt lookup of already loaded class if ressource cannot be located.

### 26. November 2015: version 0.7.2

- Added `TypePool` that falls back to class loading if a class cannot be located.
- Added binary locator for agent builder that uses the above class pool and only parses the class file of the instrumented type.
- Added methods for reading inner classes of a `TypeDescription`.
- Fixed random naming based on random numbers to avoid signed numbers.
- Moved `Nexus` and `Installer` types to a package-level to avoid illegal outer and inner class references which could be resolved eagerly.
- Added validation for illegal constant pool entries.
- Added a `Premature` initialization strategy for optimistically loading auxiliary types.
- Added a `ClassVisitorWrapper` for translating Java class files prior to Java 5 to use explicit class loading rather than class pool constants.

### 16. November 2015: version 0.7.1

- Fixed injection order for types to avoid premature loading by dependant auxiliary types.
- Added additional `ClassFileLocator`s and refactored class file lookup to always use these locators.

### 11. November 2015: version 0.7

- Refactored injection strategy to always inject and load the instrumented type first to avoid premature loading by reference from auxiliary types.
- Refactored `AgentBuilder.Default` to delay auxiliary type injection until load time to avoid premature loading by reference from auxiliary types.
- Added API to add additional code to type initializers while building a type.
- Refactored agent `Nexus` to allow for multiple registrations of self initializers if multiple agents are registered via Byte Buddy.
- Fixed resolution of interface methods that were represented in the type hierarchy multiple times.
- Implemented custom ASM class writer to allow for frame computation via Byte Buddy's type pool when this is required by a user.
- Fallback to no allowing for instrumenting type initializers for rebased or redefined interfaces before Java 8.

### 28. October 2015: version 0.7 (release candidate 6)

- Refactored `AgentBuilder.Default` to delegate exceptions during redefinitions to listener instead of throwing them.
- Fixed bug where instrumented type would count to auxiliary types and trigger injection strategy.
- Fixed bug where interface types would resolve to a non-generic type signature.
- Added strategy to use redefinition or retransformation of the `Instrumentation` API when building agents.
- Added lazy facade to be used by agent builder to improve performance for name-based matchers.

### 15. October 2015: version 0.7 (release candidate 5)

- Fixed parser to supress exceptions from generic signatures which are not supposed to be included in the class file if no array type is generic.
- Fixed class validator which did not allow `<clinit>` blocks in interface types.
- Added restriction to retransformation to not attempt a retransformation at all if no class should be retransformed.
- Added a factory for creating an `Implementation.Context` that is configurable. This way, it is possible to avoid a rebase of a type initializer which is not always possible.
- Added a possibility to specify for an `AgentBuilder` how it should redefine or rebase a class that is intercepted.

### 13. October 2015: version 0.7 (release candidate 4)

- Fixed naming strategy for fields that cache values which chose duplicate names.
- Fixed resolution of raw types within the type hierarchy which were represented as non-generic `TypeDescription` instances where type variables of members were not resolved.
- Added possibility to specify hints for `ClassReader` and `ClassWriter` instances.
- Fixed resolution for modifiers of members that are defined by Byte Buddy. Previously, Byte Buddy would sometimes attempt to define private synthetic methods on generated interfaces.
- Fixed assignability resolution for arrays.
- Fixed class file parser which would not recognize outer classes for version 1.3 byte code.

### 6. October 2015: version 0.7 (release candidate 3)

- Read `Nexus` instances of the Byte Buddy agents from the enclosing class loader rather than from the system class loader. This allows for their usage from OSGi environments and for user with other custom class loaders.
- Changed modifiers for accessor methods and rebased methods to be public when rebasing or accessing methods of a Java 8 interface. For interfaces, all modifiers must be public, even for such synthetic members.
- Support absolute path names for accessing class file resources of the `ByteArrayClassLoader`.
- Added random suffix to the names of rebased methods to avoid naming conflicts.

### 16. September 2015: version 0.7 (release candidate 2)

- Refactored runtime attachment of Java agents to support Java 9 and additional legacy VM (version 8-).
- Refactored `MethodGraph` to only represent virtual methods.
- Changed notion of visibility to not longer consider the declaring type as part of the visibility.
- Increased flexibility of defining proxy types for `@Super` and `@Default` annotations.
- Added directional `AmbigouityResolver`.
- Fixed detection of methods that can be rebased to not include methods that did not previously exist.

### 11. August 2015: version 0.7 (release candidate 1)

- Added support for generic types.
- Replaced `MethodLookupEngine` with `MethodGraph.Compiler` to provide a richer data structure.
- Added support for bridge methods (type and visibility bridges).
- Refactored the predefined `ElementMatcher`s to allow for matching generic types.
- Replaced the `ModifierResolver` with a more general `MethodTransformer`.

### 11. August 2015: version 0.6.15

- Added support for discovery and handling of Java 9 VMs.
- Fixed class loading for Android 5 (Lollipop) API.

### 20. July 2015: version 0.6.14

- Fixed resolution of ignored methods. Previously, additional ignored methods were not appended but added as an additional criteria for ignoring a method.

### 17. July 2015: version 0.6.13

- Fixed resolution of field accessors to not attempt reading of non-static fields from static methods.
- Fixed renaming strategy for type redefinitions to work around a constraint of ASM where stack map frames required to be expanded even though this was not strictly necessary.

### 10. July 2015: version 0.6.12

- Added API for altering a method's modifiers when intercepting it.
- Added API for allowing to filter default values when writing annotations.

### 22. June 2015: version 0.6.11

- Added additional `ClassFileLocator`s for locating jar files in folders and jar files.
- Added explicit check for invalid access of instance fields from static methods in field accessing interceptors.
- Added the `@StubValue` and `@FieldValue` annotations.

### 18. June 2015: version 0.6.10 (and 0.6.9)

- Corrected the resolution of a type's visibility to another type to determine if a method can be legally overridden.
- Previous version 0.6.9 contained another bug when attempting to fix this problem.

Corrected incorrect deployment of version 0.6.7 which does not use a dependency reduced POM for the *byte-buddy* module.

### 1. June 2015: version 0.6.8 (and 0.6.7)

- Upgraded ASM dependency to 5.0.4.
- Fixed OSGi headers in all relevant artifacts.

*Warning*: The *byte-buddy* artifact of version 0.6.7 is accidentally deployed with a defect POM file which does not exclude the shaded resources.

### 28. May 2015: version 0.6.6

Fixed error in resolution of the `TargetType` pseudo-variable when used as component type of an array.

### 7. May 2015: version 0.6.5

Extended public API with convenience methods.

### 6. May 2015: version 0.6.4

- Extended public API to accept more general argument types when appropriate.
- Extended `@Origin` annotation to allow for accepting modifiers.

### 29. April 2015: version 0.6.3

- Made the `TypeDescription.ForLoadedType` class loader agnostic. Before, a class that was loaded by multiple class
  loaders would have been considered inequal what is not true for the byte code level.
  
### 23. April 2015: version 0.6.2

- Added additional class validation such that it becomes impossible to define members on classes that do not fit
  the class's structure, i.e. default methods on Java interfaces in version seven.
- Added default `Assigner` singleton.

### 21. April 2015: version 0.6.1

- Added `AnnotationDescription.Builder` to allow easy definition of annotation values without loading any values.
- Added possibility to define enumerations at runtime.
- Added possibility to dynamically read enumerations for the `MethodCall` and `InvokeDynamic` implementations.
- Further API clean-up.

### 15. April 2015: version 0.6

- Renamed the `Instrumentation` interface to `Implementation` to avoid naming conflicts with Java types.
- Renamed the `Field` annotation to `FieldProxy` to avoid naming conflicts with Java types.
- Refactored package structure to make the implementation more readable.
- Added possibility to define annotation default values.
- Avoid creation of an auxiliary placeholder type for method rebasements if it is not required.
- Avoid rebasing of methods if they are not instrumented.
- Reimplemented `TypeWriter`, `MethodRegistry` and other supporting infrastructure to make  the code simpler.
- Refactored testing that is related to the previous infrastructure.

### 21. March 2015: version 0.5.6

- Added possibility to write parameter meta information to created classes if it is fully available for a method.

### 20. March 2015: version 0.5.5

- Retrofitted method parameters to be represented by `ParameterDescription`s and added possibility to extract names
  and modifiers for these parameters, either by using the Java 8 API (if available) or by reading this information
  from the underlying class file.
- Fixed a `NullPointerException` being thrown due to accidental return of a `null` value from a method.

### 15. March 2015: version 0.5.4

- Fixed missing retention of method annotations of instrumented types.
- Allowed dynamic lookup of methods for the `MethodCall` instrumentation.

### 24. February 2015: version 0.5.3

- Changed the `SuperMethodCall` instrumentation to fall back to a default method call if required. A different
  behavior was found to surprise users and would introduce subtle bugs in user code as the super method instrumentation
  would always work with subclassing due to Java super method call semantics.
- Added a `MethodCall` instrumentation that allows hard-coding a method call.
- Added an `InvokeDynamic` instrumentation that allows runtime dispatching by bootstrap methods.
- Fixed the default `TypePool` to retain generic signatures in order to avoid that agents delete such signatures.
- Fixed a bug in all of the the default `ConstructorStrategy` that effectively prevented intercepting of constructors.

### 18. January 2015: version 0.5.2

- Fixed a bug where interface generation would result in a `NullPointerException`.
- Added additional `ElementMatcher`s that allow to identify class loaders.

### 5. December 2014: version 0.5.1

Added the `andThen` method to the `SuperMethodCall` instrumentation in order to allow for a more convenient 
constructor interception where a hard-coded super method call is required by the Java verifier.

### 3. December 2014: version 0.5

- Added the `DeclaringTypeResolver` as a component in the default chain which selects the most specific method out
  of two. This is mainly meant to avoid the accidental matching of the methods that are declared by the `Object` type.
- Added `TypeInitializer`s in order to allow `Instrumentation`s to define type initializer blocks.
- Replaced the `MethodMatcher` API with the `ElementMatcher` API which allows for a more sophisticated matching DSL.
- Added a `ClassLoadingStrategy` for Android in its own module.
- Introduced an `AgentBuilder` API and implementation.

### 26. November 2014: version 0.4.1

- Refactored the implementation of the `VoidAwareAssigner` which would otherwise cause unexpected behavior in its 
  default state.
- Added a missing boxing instruction to the `InvocationHandlerAdapter`.

### 18. November 2014: version 0.4

- Extended `Instrumentation.Context` to support field accessors.
- Added the `TypePool` abstraction and added a default implementation.
- Refactored annotations to have an intermediate form as `AnnotationDescription` which does not need to 
  represent loaded values.
- Refactored several built-in `Instrumentation`, among others, all implementations now support `TypeDescription` 
  in addition to loaded `Class` as their arguments
- Added several annotations that apply to the `MethodDelegation`.

### 19. September 2014: version 0.3.1

- Added support for optionally specifying a `ProtectionDomain` for the built-in `ClassLoadingStrategy` implementations.
- Fixed a bug in the resolution of resources of the `ByteArrayClassLoader` and its child-first implementation.

### 15. September 2014: version 0.3

- Added basic support for Java 7 types `MethodHandle` and `MethodType` which are available from Java 7 for injection.
- Added support for type redefinition and type rebasing.
- Added support for accessing a JVM's HotSwap features and a Java agent.
- Added latent a child-first `ClassLoadingStrategy` and manifest versions of the `WRAPPER` and `CHILD_FIRST` default
  class loading strategies.
  
### 20. June 2014: version 0.2.1

Added proper support for defining class initializers. Added support for field caching from method instrumentations,
mainly for allowing the reuse of `Method` instances for the `@Origin` annotation and the `InvocationHandlerAdapter`.
  
### 16. June 2014: version 0.2

Added several bug fixes for existing features. Beyond that the following features were added or changed:

 - Changed the semantics of the `@SuperCall` to be only bindable, if a super method can be invoked. Before, an
   exception was thrown if only a non-existent or abstract super method was found.
 - Added features for the interaction with Java 8 default methods. Refactored method lookup to extract invokable
   default methods.
 - Refactored the invocation of super methods to be created by an `Instrumentation.Target`. For a future release,
   this hopefully allows for class redefinitions using today's API for creating subclasses.
 - Upgraded to ASM 5.0.3.
 
### 02. May 2014: version 0.1

First general release.
