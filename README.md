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

#### Irc
Irc is the initial given test used to verify Javanaise first version.

#### Irc2
Irc2 is a teste used to verify Javanaise 2 implementation (dynamic proxies). When called with an argument, it will create or lookup a distributed object named like first argument.

#### IrcManipulator
IrcManipulator is an interface that allows to create multiple Irc2 linked to choosen object running in a new process. Since memory of each process is not shared, it demonstrates the correct behavior of Javanaise.

### Automatic tests
#### Simple_test

<u>Description</u> <br />
Create an interception object that writes and reads to and from the server once

<u>Execution</u> <br />
- run JvnCoordImpl
- run Simple_test
- stop Simple_test at the end of the execution

#### Burst

<u>Description</u> <br />
creates multiple servers that can be called by multiple objects

<u>Execution</u> <br />
- run `sh BurstExec.sh` in irc folder

<u>Modify</u> <br />
You can modify the test by changing the calls in the BurstExec.sh file
