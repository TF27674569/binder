# Binder机制

## **一、AIDL跨进程通讯**
&nbsp;　　1.两个进程申明两个一模一样的.aidl协议
```aidl
package com.binder;

interface ITestAidlInterface {

    String getName();

    int getAge();
}
```
&nbsp;　　2.编译项目找到生成的java，interface接口
```java
package com.binder;

public interface ITestAidlInterface extends android.os.IInterface {

    public static abstract class Stub extends android.os.Binder implements com.binder.ITestAidlInterface {
        private static final java.lang.String DESCRIPTOR = "com.binder.ITestAidlInterface";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }
        
        public static com.binder.ITestAidlInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.binder.ITestAidlInterface))) {
                return ((com.binder.ITestAidlInterface) iin);
            }
            return new com.binder.ITestAidlInterface.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getName: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _result = this.getName();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_getAge: {
                    data.enforceInterface(DESCRIPTOR);
                    int _result = this.getAge();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.binder.ITestAidlInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public java.lang.String getName() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getName, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int getAge() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getAge, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_getName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_getAge = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    public java.lang.String getName() throws android.os.RemoteException;

    public int getAge() throws android.os.RemoteException;
}
```
&nbsp;　　3.定义服务
```java
public class TestAidlServices extends Service{
    /**
     * 创建生成的本地 Binder 对象，实现 AIDL 制定的方法
     */

    private IBinder mIBinder = new ITestAidlInterface.Stub() {

        @Override
        public String getName() throws RemoteException {
            return "TIAN FENG";
        }

        @Override
        public int getAge() throws RemoteException {
            return 25;
        }
    };

    /**
     * 返回一个binder引用
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }
}
```

&nbsp;　　4.注册服务启动服务
```java
        <service
            android:name=".service.TestAidlServices"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="TestAidlServices"/>
            </intent-filter>
        </service>
        
       
        Intent intent = new Intent();
        intent.setAction("TestAidlServices");
        // 需要加上包名
        intent.setPackage(getPackageName());
        bindService(intent,connection,BIND_AUTO_CREATE);
        
        ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ITestAidlInterface asInterface = ITestAidlInterface.Stub.asInterface(service);
                    try {
                        Toast.makeText(MainActivity.this, asInterface.getName()+"  " + asInterface.getAge(), Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
        
                @Override
                public void onServiceDisconnected(ComponentName name) {
        
                }
            };
```
## **二、Binder分析**
&nbsp;　　1.两个进程之间由于内存不共享，所以进程A无法引用到进程B的对象  
&nbsp;　　&nbsp;　　A与B之间不能直接进行通讯。  

&nbsp;　　2.此时需要引入binder对象进行跨进程通讯的桥梁  
&nbsp;　　&nbsp;　　binder对象中持有所有进程的pid并分配其内存  
&nbsp;　　&nbsp;　　每个进程中又存在若干个aidl文件（包括系统自带）并在其注册  


&nbsp;　　3.aidl文件结构包含stub（存根）和proxy（代理）   
&nbsp;　　&nbsp;　　 stub：用于接收binder返回的信息  
&nbsp;　　&nbsp;　　 proxy：用于向binder写入请求