.method public handleMessage(Landroid/os/Message;)V
    .registers 13
    .param p1, "msg"    # Landroid/os/Message;

    .line 903
    iget v0, p1, Landroid/os/Message;->what:I

    const-string/jumbo v1, "usb-screen-unlocked-config-%d"

    const-wide/16 v2, 0x2

    const/4 v4, 0x4

    const-wide/16 v5, 0x0

    const/4 v7, 0x0

    const/4 v8, 0x1

    packed-switch v0, :pswitch_data_29e

    :pswitch_f
    goto/16 :goto_29d

    .line 1133
    :pswitch_11
    iget v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSendStringCount:I

    add-int/2addr v0, v8

    iput v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSendStringCount:I

    goto/16 :goto_29d

    .line 1123
    :pswitch_18
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mBootCompleted:Z

    if-eqz v0, :cond_21

    .line 1124
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->broadcastUsbAccessoryHandshake()V

    goto/16 :goto_29d

    .line 1128
    :cond_21
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mPendingBootAccessoryHandshakeBroadcast:Z

    .line 1130
    goto/16 :goto_29d

    .line 1040
    :pswitch_25
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-ne v0, v8, :cond_2b

    move v0, v8

    goto :goto_2c

    :cond_2b
    move v0, v7

    :goto_2c
    iget-boolean v1, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenLocked:Z

    if-ne v0, v1, :cond_32

    .line 1041
    goto/16 :goto_29d

    .line 1043
    :cond_32
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-ne v0, v8, :cond_37

    goto :goto_38

    :cond_37
    move v8, v7

    :goto_38
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenLocked:Z

    .line 1044
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mBootCompleted:Z

    if-nez v0, :cond_40

    .line 1045
    goto/16 :goto_29d

    .line 1047
    :cond_40
    if-eqz v8, :cond_4b

    .line 1048
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mConnected:Z

    if-nez v0, :cond_29d

    .line 1049
    invoke-virtual {p0, v5, v6, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    goto/16 :goto_29d

    .line 1052
    :cond_4b
    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenUnlockedFunctions:J

    cmp-long v0, v0, v5

    if-eqz v0, :cond_29d

    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    cmp-long v0, v0, v5

    if-nez v0, :cond_29d

    .line 1055
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setScreenUnlockedFunctions()V

    goto/16 :goto_29d

    .line 1024
    :pswitch_5c
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v0, Ljava/lang/Long;

    invoke-virtual {v0}, Ljava/lang/Long;->longValue()J

    move-result-wide v2

    iput-wide v2, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenUnlockedFunctions:J

    .line 1025
    iget-object v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSettings:Landroid/content/SharedPreferences;

    if-eqz v0, :cond_8a

    .line 1026
    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    .line 1027
    .local v0, "editor":Landroid/content/SharedPreferences$Editor;
    sget-object v2, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    new-array v3, v8, [Ljava/lang/Object;

    iget v4, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentUser:I

    .line 1028
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v3, v7

    .line 1027
    invoke-static {v2, v1, v3}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-wide v2, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenUnlockedFunctions:J

    .line 1029
    invoke-static {v2, v3}, Landroid/hardware/usb/UsbManager;->usbFunctionsToString(J)Ljava/lang/String;

    move-result-object v2

    .line 1027
    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    .line 1030
    invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->commit()Z

    .line 1032
    .end local v0    # "editor":Landroid/content/SharedPreferences$Editor;
    :cond_8a
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenLocked:Z

    if-nez v0, :cond_99

    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenUnlockedFunctions:J

    cmp-long v0, v0, v5

    if-eqz v0, :cond_99

    .line 1034
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setScreenUnlockedFunctions()V

    goto/16 :goto_29d

    .line 1036
    :cond_99
    invoke-virtual {p0, v5, v6, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    .line 1038
    goto/16 :goto_29d

    .line 1086
    :pswitch_9e
    invoke-virtual {p0, v8}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateAdbNotification(Z)V

    .line 1087
    invoke-virtual {p0, v8}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbNotification(Z)V

    .line 1088
    goto/16 :goto_29d

    .line 985
    :pswitch_a6
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v0, Ljava/util/Iterator;

    .line 986
    .local v0, "devices":Ljava/util/Iterator;
    iget v1, p1, Landroid/os/Message;->arg1:I

    if-ne v1, v8, :cond_b0

    move v1, v8

    goto :goto_b1

    :cond_b0
    move v1, v7

    :goto_b1
    iput-boolean v1, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mUsbAccessoryConnected:Z

    .line 992
    iput-boolean v7, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mHideUsbNotification:Z

    .line 993
    :goto_b5
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    move-result v1

    if-eqz v1, :cond_f9

    .line 994
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/Map$Entry;

    .line 998
    .local v1, "pair":Ljava/util/Map$Entry;
    invoke-interface {v1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Landroid/hardware/usb/UsbDevice;

    .line 999
    .local v2, "device":Landroid/hardware/usb/UsbDevice;
    invoke-virtual {v2}, Landroid/hardware/usb/UsbDevice;->getConfigurationCount()I

    move-result v3

    sub-int/2addr v3, v8

    .line 1000
    .local v3, "configurationCount":I
    :goto_cc
    if-ltz v3, :cond_f8

    .line 1001
    invoke-virtual {v2, v3}, Landroid/hardware/usb/UsbDevice;->getConfiguration(I)Landroid/hardware/usb/UsbConfiguration;

    move-result-object v4

    .line 1002
    .local v4, "config":Landroid/hardware/usb/UsbConfiguration;
    add-int/lit8 v3, v3, -0x1

    .line 1003
    invoke-virtual {v4}, Landroid/hardware/usb/UsbConfiguration;->getInterfaceCount()I

    move-result v5

    sub-int/2addr v5, v8

    .line 1004
    .local v5, "interfaceCount":I
    :goto_d9
    if-ltz v5, :cond_f7

    .line 1005
    invoke-virtual {v4, v5}, Landroid/hardware/usb/UsbConfiguration;->getInterface(I)Landroid/hardware/usb/UsbInterface;

    move-result-object v6

    .line 1006
    .local v6, "intrface":Landroid/hardware/usb/UsbInterface;
    add-int/lit8 v5, v5, -0x1

    .line 1007
    invoke-static {}, Lcom/android/server/usb/UsbDeviceManager;->-$$Nest$sfgetsDenyInterfaces()Ljava/util/Set;

    move-result-object v9

    invoke-virtual {v6}, Landroid/hardware/usb/UsbInterface;->getInterfaceClass()I

    move-result v10

    invoke-static {v10}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    invoke-interface {v9, v10}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v9

    if-eqz v9, :cond_f6

    .line 1008
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mHideUsbNotification:Z

    .line 1009
    goto :goto_f7

    .line 1011
    .end local v6    # "intrface":Landroid/hardware/usb/UsbInterface;
    :cond_f6
    goto :goto_d9

    .line 1012
    .end local v4    # "config":Landroid/hardware/usb/UsbConfiguration;
    .end local v5    # "interfaceCount":I
    :cond_f7
    :goto_f7
    goto :goto_cc

    .line 1013
    .end local v1    # "pair":Ljava/util/Map$Entry;
    .end local v2    # "device":Landroid/hardware/usb/UsbDevice;
    .end local v3    # "configurationCount":I
    :cond_f8
    goto :goto_b5

    .line 1014
    :cond_f9
    invoke-virtual {p0, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbNotification(Z)V

    .line 1015
    goto/16 :goto_29d

    .line 981
    .end local v0    # "devices":Ljava/util/Iterator;
    :pswitch_fe
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-ne v0, v8, :cond_103

    goto :goto_104

    :cond_103
    move v8, v7

    :goto_104
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mUsbCharging:Z

    .line 982
    invoke-virtual {p0, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbNotification(Z)V

    .line 983
    goto/16 :goto_29d

    .line 1114
    :pswitch_10b
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mConnected:Z

    if-eqz v0, :cond_116

    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    and-long/2addr v0, v2

    cmp-long v0, v0, v5

    if-nez v0, :cond_29d

    .line 1115
    :cond_116
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->notifyAccessoryModeExit()V

    goto/16 :goto_29d

    .line 940
    :pswitch_11b
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v0, Lcom/android/internal/os/SomeArgs;

    .line 941
    .local v0, "args":Lcom/android/internal/os/SomeArgs;
    iget-boolean v1, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mHostConnected:Z

    .line 942
    .local v1, "prevHostConnected":Z
    iget-object v2, v0, Lcom/android/internal/os/SomeArgs;->arg1:Ljava/lang/Object;

    check-cast v2, Landroid/hardware/usb/UsbPort;

    .line 943
    .local v2, "port":Landroid/hardware/usb/UsbPort;
    iget-object v3, v0, Lcom/android/internal/os/SomeArgs;->arg2:Ljava/lang/Object;

    check-cast v3, Landroid/hardware/usb/UsbPortStatus;

    .line 945
    .local v3, "status":Landroid/hardware/usb/UsbPortStatus;
    if-eqz v3, :cond_176

    .line 946
    invoke-virtual {v3}, Landroid/hardware/usb/UsbPortStatus;->getCurrentDataRole()I

    move-result v5

    if-ne v5, v8, :cond_133

    move v5, v8

    goto :goto_134

    :cond_133
    move v5, v7

    :goto_134
    iput-boolean v5, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mHostConnected:Z

    .line 947
    invoke-virtual {v3}, Landroid/hardware/usb/UsbPortStatus;->getCurrentPowerRole()I

    move-result v5

    if-ne v5, v8, :cond_13e

    move v5, v8

    goto :goto_13f

    :cond_13e
    move v5, v7

    :goto_13f
    iput-boolean v5, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSourcePower:Z

    .line 948
    invoke-virtual {v3}, Landroid/hardware/usb/UsbPortStatus;->getCurrentPowerRole()I

    move-result v5

    const/4 v6, 0x2

    if-ne v5, v6, :cond_14a

    move v5, v8

    goto :goto_14b

    :cond_14a
    move v5, v7

    :goto_14b
    iput-boolean v5, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSinkPower:Z

    .line 949
    invoke-virtual {v3}, Landroid/hardware/usb/UsbPortStatus;->getCurrentMode()I

    move-result v5

    if-ne v5, v4, :cond_155

    move v5, v8

    goto :goto_156

    :cond_155
    move v5, v7

    :goto_156
    iput-boolean v5, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mAudioAccessoryConnected:Z

    .line 954
    invoke-virtual {v3, v8, v8}, Landroid/hardware/usb/UsbPortStatus;->isRoleCombinationSupported(II)Z

    move-result v5

    if-eqz v5, :cond_172

    .line 956
    invoke-virtual {v3, v6, v8}, Landroid/hardware/usb/UsbPortStatus;->isRoleCombinationSupported(II)Z

    move-result v5

    if-eqz v5, :cond_172

    .line 957
    invoke-virtual {v3, v8, v6}, Landroid/hardware/usb/UsbPortStatus;->isRoleCombinationSupported(II)Z

    move-result v5

    if-eqz v5, :cond_172

    .line 959
    invoke-virtual {v3, v6, v6}, Landroid/hardware/usb/UsbPortStatus;->isRoleCombinationSupported(II)Z

    move-result v5

    if-eqz v5, :cond_172

    move v5, v8

    goto :goto_173

    :cond_172
    move v5, v7

    :goto_173
    iput-boolean v5, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSupportsAllCombinations:Z

    goto :goto_180

    .line 961
    :cond_176
    iput-boolean v7, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mHostConnected:Z

    .line 962
    iput-boolean v7, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSourcePower:Z

    .line 963
    iput-boolean v7, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSinkPower:Z

    .line 964
    iput-boolean v7, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mAudioAccessoryConnected:Z

    .line 965
    iput-boolean v7, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSupportsAllCombinations:Z

    .line 968
    :goto_180
    invoke-virtual {v2, v4}, Landroid/hardware/usb/UsbPort;->isModeSupported(I)Z

    move-result v4

    iput-boolean v4, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mAudioAccessorySupported:Z

    .line 970
    invoke-virtual {v0}, Lcom/android/internal/os/SomeArgs;->recycle()V

    .line 971
    invoke-virtual {p0, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbNotification(Z)V

    .line 972
    iget-boolean v4, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mBootCompleted:Z

    if-eqz v4, :cond_1a1

    .line 973
    iget-boolean v4, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mHostConnected:Z

    if-nez v4, :cond_196

    if-eqz v1, :cond_29d

    .line 974
    :cond_196
    iget-wide v4, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    invoke-virtual {p0, v4, v5}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->getAppliedFunctions(J)J

    move-result-wide v4

    invoke-virtual {p0, v4, v5}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbStateBroadcastIfNeeded(J)V

    goto/16 :goto_29d

    .line 977
    :cond_1a1
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mPendingBootBroadcast:Z

    .line 979
    goto/16 :goto_29d

    .line 1061
    .end local v0    # "args":Lcom/android/internal/os/SomeArgs;
    .end local v1    # "prevHostConnected":Z
    .end local v2    # "port":Landroid/hardware/usb/UsbPort;
    .end local v3    # "status":Landroid/hardware/usb/UsbPortStatus;
    :pswitch_1a5
    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    invoke-virtual {p0, v0, v1}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->isUsbDataTransferActive(J)Z

    move-result v0

    if-eqz v0, :cond_29d

    invoke-virtual {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->isUsbTransferAllowed()Z

    move-result v0

    if-nez v0, :cond_29d

    .line 1062
    invoke-virtual {p0, v5, v6, v8}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    goto/16 :goto_29d

    .line 1094
    :pswitch_1b8
    iget v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentUser:I

    iget v2, p1, Landroid/os/Message;->arg1:I

    if-eq v0, v2, :cond_29d

    .line 1098
    iget v0, p1, Landroid/os/Message;->arg1:I

    iput v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentUser:I

    .line 1099
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenLocked:Z

    .line 1100
    iput-wide v5, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenUnlockedFunctions:J

    .line 1101
    iget-object v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSettings:Landroid/content/SharedPreferences;

    if-eqz v0, :cond_1e6

    .line 1102
    sget-object v2, Ljava/util/Locale;->ENGLISH:Ljava/util/Locale;

    new-array v3, v8, [Ljava/lang/Object;

    iget v4, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentUser:I

    .line 1104
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v3, v7

    .line 1103
    invoke-static {v2, v1, v3}, Ljava/lang/String;->format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    const-string v2, ""

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 1102
    invoke-static {v0}, Landroid/hardware/usb/UsbManager;->usbFunctionsFromString(Ljava/lang/String;)J

    move-result-wide v0

    iput-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mScreenUnlockedFunctions:J

    .line 1106
    :cond_1e6
    invoke-virtual {p0, v5, v6, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    goto/16 :goto_29d

    .line 1090
    :pswitch_1eb
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mBootCompleted:Z

    .line 1091
    invoke-virtual {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->finishBoot()V

    .line 1092
    goto/16 :goto_29d

    .line 1066
    :pswitch_1f2
    iget-object v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mContext:Landroid/content/Context;

    .line 1067
    const-string v1, "notification"

    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/NotificationManager;

    iput-object v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mNotificationManager:Landroid/app/NotificationManager;

    .line 1069
    const-class v0, Landroid/debug/AdbManagerInternal;

    invoke-static {v0}, Lcom/android/server/LocalServices;->getService(Ljava/lang/Class;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/debug/AdbManagerInternal;

    new-instance v1, Lcom/android/server/usb/UsbDeviceManager$UsbHandler$AdbTransport;

    invoke-direct {v1, p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler$AdbTransport;-><init>(Lcom/android/server/usb/UsbDeviceManager$UsbHandler;)V

    .line 1070
    invoke-virtual {v0, v1}, Landroid/debug/AdbManagerInternal;->registerTransport(Landroid/debug/IAdbTransport;)V

    .line 1073
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->isTv()Z

    move-result v0

    if-eqz v0, :cond_22a

    .line 1075
    iget-object v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mNotificationManager:Landroid/app/NotificationManager;

    new-instance v1, Landroid/app/NotificationChannel;

    iget-object v2, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mContext:Landroid/content/Context;

    const v3, 0x1040117

    .line 1077
    invoke-virtual {v2, v3}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v2

    const-string/jumbo v3, "usbdevicemanager.adb.tv"

    invoke-direct {v1, v3, v2, v4}, Landroid/app/NotificationChannel;-><init>(Ljava/lang/String;Ljava/lang/CharSequence;I)V

    .line 1075
    invoke-virtual {v0, v1}, Landroid/app/NotificationManager;->createNotificationChannel(Landroid/app/NotificationChannel;)V

    .line 1082
    :cond_22a
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mSystemReady:Z

    .line 1083
    invoke-virtual {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->finishBoot()V

    .line 1084
    goto/16 :goto_29d

    .line 1020
    :pswitch_231
    iget-object v0, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v0, Ljava/lang/Long;

    invoke-virtual {v0}, Ljava/lang/Long;->longValue()J

    move-result-wide v0

    .line 1021
    .local v0, "functions":J
    invoke-virtual {p0, v0, v1, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    .line 1022
    goto/16 :goto_29d

    .line 1017
    .end local v0    # "functions":J
    :pswitch_23e
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-ne v0, v8, :cond_243

    move v7, v8

    :cond_243
    invoke-direct {p0, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setAdbEnabled(Z)V

    .line 1018
    goto :goto_29d

    .line 905
    :pswitch_247
    iget v0, p1, Landroid/os/Message;->arg1:I

    if-ne v0, v8, :cond_24d

    move v0, v8

    goto :goto_24e

    :cond_24d
    move v0, v7

    :goto_24e
    iput-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mConnected:Z

    .line 906
    iget v0, p1, Landroid/os/Message;->arg2:I

    if-ne v0, v8, :cond_256

    move v0, v8

    goto :goto_257

    :cond_256
    move v0, v7

    :goto_257
    iput-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mConfigured:Z

    .line 908
    invoke-virtual {p0, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbNotification(Z)V

    .line 909
    invoke-virtual {p0, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateAdbNotification(Z)V

    .line 910
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mBootCompleted:Z

    if-eqz v0, :cond_26c

    .line 911
    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    invoke-virtual {p0, v0, v1}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->getAppliedFunctions(J)J

    move-result-wide v0

    invoke-virtual {p0, v0, v1}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbStateBroadcastIfNeeded(J)V

    .line 913
    :cond_26c
    iget-wide v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    and-long/2addr v0, v2

    cmp-long v0, v0, v5

    if-eqz v0, :cond_276

    .line 914
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateCurrentAccessory()V

    .line 916
    :cond_276
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mBootCompleted:Z

    if-eqz v0, :cond_297

    .line 917
    iget-boolean v0, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mConnected:Z

    if-nez v0, :cond_293

    const/16 v0, 0x8

    invoke-virtual {p0, v0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_293

    const/16 v0, 0x11

    .line 918
    invoke-virtual {p0, v0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->hasMessages(I)Z

    move-result v0

    if-nez v0, :cond_293

    .line 929
    const-wide/16 v0, 0x4

    invoke-virtual {p0, v0, v1, v7}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    .line 931
    .line 933
    :cond_293
    invoke-direct {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbFunctions()V

    goto :goto_299

    .line 935
    :cond_297
    iput-boolean v8, p0, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mPendingBootBroadcast:Z

    .line 937
    :goto_299
    invoke-virtual {p0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateUsbSpeed()V

    .line 938
    nop

    .line 1136
    :cond_29d
    :goto_29d
    return-void

    :pswitch_data_29e
    .packed-switch 0x0
        :pswitch_247
        :pswitch_23e
        :pswitch_231
        :pswitch_1f2
        :pswitch_1eb
        :pswitch_1b8
        :pswitch_1a5
        :pswitch_11b
        :pswitch_10b
        :pswitch_fe
        :pswitch_a6
        :pswitch_9e
        :pswitch_5c
        :pswitch_25
        :pswitch_f
        :pswitch_f
        :pswitch_f
        :pswitch_f
        :pswitch_f
        :pswitch_f
        :pswitch_18
        :pswitch_11
    .end packed-switch
.end method
