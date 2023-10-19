# JAVANAISE Project: SAR - M2GI
SAR project : Distributed object system

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

## Tests

### Manual tests

#### CacheTests/CacheClear
Use to briefly evaluate cache behavior by creating a number of objects, adding them, then reading them and manually checking their consistency. Every `CACHE_SIZE` object, the server cache is emptied :

### Automatic tests
