# SAR: JAVANAISE Project - M2GI
SAR project : Distributed objects system called `Javanaise`

## Authors
Eva GAILLARD & Geoffrey DAVID

## Javanaise API

### Version 1
Javanaise user has to get the Application server reference by calling `JvnServerImpl.jvnGetServer()`.
  - The distributed objects are wrapped by `JvnObjectImpl` class implementing `JvnObject` interface.
  - The method `JvnServerImpl.jvnGetServer().jvnCreateObject((Serializable) Object o)` create a `JvnObject` based on the given serializable object. This object can become a distributed object using `JvnServerImpl.jvnGetServer().jvnRegisterObject(String objName, JvnObject jo)`.
  - Then it is possible to get a distributed object by calling `JvnServerImpl.jvnGetServer().jvnLookupObject(String objName)` method.

Moreover user should manually call functions on `JvnObjects` to ensure distributed objects synchronization:
  - `jvnLockRead()` has to be called before reading a distributed object
  - `jvnLockWrite()` has to be called before editing a distributed object
  - `jvnUnLock()` must be called after any lock

### Version 2
Dynamic proxies and annotations allow to use distributed object with more transparency. Javanaise user will no longer manage locks.

To get a distributed object or to register a new one, a single call `JvnProxy.newInstance((Serializable) Object o, String objName)` is needed.

Before, the user must have used `JvnAnnotationType.WRITE` and `JvnAnnotationType.READ` to annotate functions that read or edit distributed object.

## Additional features

### Application's objects cache management
Each application can use a limited amount of distributed objects defined by attribut `JvnServerImpl.CACHE_SIZE` (`5` by default).
When the cache is full it is automatically cleared.

It is possible to clear manually the cache by using `JvnLocalServer.jvnClearObjectsCache()`.

### Client's crash management
If the coordinator lost connection with a client, it will remove all reader/writer status it may have to any shared object. The coordinator will print a message "Client connection lost" if it happens.

## Coordinator's crash management
The status of the coordinator's tables is regularly saved in the directory `CoordStates`. When the coordinator is launched, it attempts to retrieve previous states.
Note: A current limitation is that a client will not be automaticly disconnected/informed that the coordinator is down. Moreover, it has to retrieve the reference to new coordinator launched using `Registry`.

## Tests

### Manual tests

#### CacheTests/CacheClear
Use to briefly evaluate cache behavior by creating a number of objects, adding them, then reading them and manually checking their consistency. Every `CACHE_SIZE` object, the server cache is emptied :

### Automatic tests

#### Simple_test

<u>Description</u> <br />
Create an interception object that writes to and reads from the server once

<u>Execution</u> <br />
- run JvnCoordImpl
- run Simple_test
- stop Simple_test at the end of the execution

#### Two_objects

<u>Description</u> <br />
Creates two interception objects that execute in order:
object 1: read, write, read
object 2: read, write, read
object 1: read

<u>Execution</u> <br />
- run JvnCoordImpl
- run Two_objects
- stop Two_objects at the end of the execution

#### Locks

<u>Description</u> <br />
Create n interception objects that execute m times : all the objects one by one read, write, read

<u>Execution</u> <br />
- run JvnCoordImpl
- run Locks
- stop Locks at the end of the execution


