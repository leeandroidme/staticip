package com.newland.myapplication;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * @author: leellun
 * @data: 2019/9/19.
 */
public class StaticSeting6 {
  private static StaticSeting6 instance;

  public static StaticSeting6 getInstance() {
    if (instance == null) {
      synchronized (StaticSeting6.class) {
        if (instance == null) {
          instance = new StaticSeting6();
        }
      }
    }

    return instance;
  }

  EthernetManager mEthManager;
  private static String mEthIpAddress = "192.168.43.2"; //IP
  private static String mEthNetmask = "255.255.255.0"; // 子网掩码
  private static String mEthGateway = "192.168.43.3"; //网关
  private static String mEthdns1 = "8.8.8.8"; // DNS1
  private static String mEthdns2 = "8.8.4.4"; // DNS2

  public void setStaticIp(Context context) {
    mEthManager = (EthernetManager)context.getSystemService("ethernet");
    InetAddress ipaddr= NetworkUtils.numericToInetAddress(mEthIpAddress);
    Inet4Address inetAddr= Utils.getIPv4Address(mEthIpAddress);
    DhcpInfo dhcpInfo = new DhcpInfo();
    dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt(inetAddr);
    int prefixLength=Utils.maskStr2InetMask(mEthNetmask);
    InetAddress gatewayAddr = Utils.getIPv4Address(mEthGateway);
    InetAddress dnsAddr = Utils.getIPv4Address(mEthdns1);
    if (inetAddr.getAddress().toString().isEmpty() || prefixLength ==0 || gatewayAddr.toString().isEmpty()
        || dnsAddr.toString().isEmpty()) {
      return;
    }
    Class<?> clazz = null;
    try {
      clazz = Class.forName("android.net.LinkAddress");
    } catch (Exception e) {
      // TODO: handle exception
    }

    Class[] cl = new Class[]{InetAddress.class, int.class};
    Constructor cons = null;

    try {
      cons = clazz.getConstructor(cl);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    Object[] x = {inetAddr, prefixLength};
    StaticIpConfiguration mStaticIpConfiguration = new StaticIpConfiguration();
    String dnsStr2 = mEthdns2;
    //mStaticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
    try {
      mStaticIpConfiguration.ipAddress = (LinkAddress) cons.newInstance(x);
      Log.d("232323", "chanson 1111111");
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    mStaticIpConfiguration.gateway=gatewayAddr;
    mStaticIpConfiguration.dnsServers.add(dnsAddr);

    if (!dnsStr2.isEmpty()) {
      mStaticIpConfiguration.dnsServers.add(Utils.getIPv4Address(dnsStr2));
    }

    IpConfiguration mIpConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC,
            IpConfiguration.ProxySettings.NONE, mStaticIpConfiguration, null);


    mEthManager.setConfiguration(mIpConfiguration);
  }
}
