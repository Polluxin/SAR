package jvn;

import java.io.Serializable;

import static jvn.JvnLock.NL;

public class JvnObjectImpl implements JvnObject{

    JvnLock lock = NL;
    int id;
    Serializable object;
    JvnLocalServer jvnLocalServer;

    public JvnObjectImpl(int id, Serializable o, JvnLocalServer jvnLocalServer) {
        this.id = id;
        object = o;
        this.jvnLocalServer = jvnLocalServer;
    }

    @Override
    public void jvnLockRead() throws JvnException {

    }

    @Override
    public void jvnLockWrite() throws JvnException {

    }

    @Override
    public void jvnUnLock() throws JvnException {

    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return null;
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {

    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        return null;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }
}
