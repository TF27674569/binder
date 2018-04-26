package com.cabinet.binder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.binder.ITestAidlInterface;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/4/26
 * Email : 27674569@qq.com
 * Version : 1.0
 */
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
