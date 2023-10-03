package jvn;

import java.io.Serializable;

import static jvn.JvnLock.*;

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
        switch (lock){
            case NL:
                jvnLocalServer.jvnLockRead(id);
                lock = R;
                break;
            case RC, RWC:
                lock = R;
                break;
            case WC:
                lock = RWC;
                break;
            case R:
                // Lock is already taken by application
                break;
            case W:
                throw new JvnException("Lock already taken in writer mode");
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        switch (lock){
            case NL, RC:
                jvnLocalServer.jvnLockWrite(id);
                lock = W;
                break;
            case WC, RWC:
                lock = W;
                break;
            case W:
                // Lock is already taken by application
                break;
            case R:
                throw new JvnException("Lock already taken in reader mode");
        }
    }

    @Override
    public void jvnUnLock() throws JvnException {
        switch (lock){
            case R:
                lock = RC;
                break;
            case W:
                lock = WC;
                break;
            default:
                throw new JvnException("Lock is not currently used");
        }
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
