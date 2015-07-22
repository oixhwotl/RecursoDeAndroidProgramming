/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\AndExam\\AndExam4_1\\src\\andexam\\ver4_1\\c30_service\\ICalc.aidl
 */
package andexam.ver4_1.c30_service;
public interface ICalc extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements andexam.ver4_1.c30_service.ICalc
{
private static final java.lang.String DESCRIPTOR = "andexam.ver4_1.c30_service.ICalc";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an andexam.ver4_1.c30_service.ICalc interface,
 * generating a proxy if needed.
 */
public static andexam.ver4_1.c30_service.ICalc asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof andexam.ver4_1.c30_service.ICalc))) {
return ((andexam.ver4_1.c30_service.ICalc)iin);
}
return new andexam.ver4_1.c30_service.ICalc.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getLCM:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.getLCM(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isPrime:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isPrime(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements andexam.ver4_1.c30_service.ICalc
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getLCM(int a, int b) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(a);
_data.writeInt(b);
mRemote.transact(Stub.TRANSACTION_getLCM, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isPrime(int n) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(n);
mRemote.transact(Stub.TRANSACTION_isPrime, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getLCM = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isPrime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public int getLCM(int a, int b) throws android.os.RemoteException;
public boolean isPrime(int n) throws android.os.RemoteException;
}
