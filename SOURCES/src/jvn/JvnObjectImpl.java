package jvn;

import java.io.Serializable;

import static jvn.JvnLock.*;

public class JvnObjectImpl implements JvnObject{

    JvnLock lock = NL;
    int id;
    Serializable object;

    public JvnObjectImpl(int id, Serializable o) {
        this.id = id;
        object = o;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        System.out.print("Read : OldLock = " + lock+ " ");
        switch (lock){
            case NL:
                object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
                lock = R;
                break;
            case RC:
                lock = R;
                break;
            case WC:
                lock = RWC;
                break;
            case W:
                throw new JvnException("jvnLockRead: Lock already taken in writer mode");
        }
        System.out.println("NewLock = " + lock+ " ");
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        System.out.print("Write : OldLock = " + lock+ " ");
        switch (lock){
            case NL, RC:
                object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
                lock = W;
                break;
            case WC:
                lock = W;
                break;
            case W:
                // Lock is already taken by application
                break;
            case R, RWC:
                throw new JvnException("jvnLockWrite: Lock already taken in reader mode");
        }
        System.out.println("NewLock = " + lock+ " ");
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        System.out.print("Unlock : OldLock = " + lock+ " ");
        switch (lock){
            case R:
                lock = RC;
                notify();
                break;
            case W, RWC:
                lock = WC;
                notify();
                break;
        }
        System.out.println("NewLock = " + lock+ " ");
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return object;
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
        System.out.print("InvalidateReader: OldLock = " + lock+ " ");
        switch (lock){
            case R:
                try {
                    while (lock == R)
                        wait();
                } catch (InterruptedException e) {
                    throw new JvnException("jvnInvalidateReader: Thread interrupted when waiting for lock");
                }
                lock = NL;
                break;
            case RC:
                lock = NL;
                break;
            case RWC:
                try {
                    while (lock == RWC)
                        wait();
                } catch (InterruptedException e) {
                    throw new JvnException("jvnInvalidateReader: Thread interrupted when waiting for lock");
                }
                lock = WC;
                break;
            default:
                throw new JvnException("LockIllagalState: cannot be in the state "+lock+" when jvnInvalidateReader() is called");
        }
        System.out.println("NewLock = " + lock+ " ");
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        System.out.print("InvalidateWriter: OldLock = " + lock+ " ");
        switch (lock){
            case W:
                try {
                    while (lock == W)
                        wait();
                } catch (InterruptedException e) {
                    throw new JvnException("jvnInvalidateWriter: Thread interrupted when waiting for lock");
                }
                lock = NL;
                break;
            case RWC:
                try {
                    while (lock == RWC)
                        wait();
                } catch (InterruptedException e) {
                    throw new JvnException("jvnInvalidateWriter: Thread interrupted when waiting for lock");
                }
            case WC:
                lock = NL;
                break;
            default:
                throw new JvnException("jvnInvalidateWriter: LockIllagalState: cannot be in the state "+lock+" when jvnInvalidateWriter() is called");
        }
        System.out.println("NewLock = " + lock+ " ");
        return jvnGetSharedObject();
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        System.out.print("InvalidateWriterForReader: OldLock = " + lock+ " ");
        switch (lock){
            case W:
                try {
                    while (lock == W)
                        wait();
                } catch (InterruptedException e) {
                    throw new JvnException("jvnInvalidateWriterForReader: Thread interrupted when waiting for lock");
                }
                lock = RC;
                break;
            case WC:
                lock = NL;
                break;
            case RWC:
                lock = R;
                break;
            default:
                throw new JvnException("jvnInvalidateWriterForReader: LockIllagalState: cannot be in the state "+lock+" when jvnInvalidateWriterForReader() is called");
        }
        System.out.println("NewLock = " + lock+ " ");
        return jvnGetSharedObject();
    }
}
