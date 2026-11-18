===== getProvider (Landroid/content/ContentResolver;)Landroid/content/IContentProvider;

    public android.content.IContentProvider getProvider(android.content.ContentResolver p3)
    {
        try {
            if (this.mContentProvider == null) {
                this.mContentProvider = p3.acquireProvider(this.mUri.getAuthority());
            }
        } catch (android.content.IContentProvider v1_1) {
            throw v1_1;
        }
        return this.mContentProvider;
    }

===== getStringForUser (Landroid/content/ContentResolver; Ljava/lang/String; I)Ljava/lang/String;

    public String getStringForUser(android.content.ContentResolver p26, String p27, int p28)
    {
        android.util.ArrayMap v0_54;
        if (p28 != android.os.UserHandle.myUserId()) {
            v0_54 = 0;
        } else {
            v0_54 = 1;
        }
        int v14;
        int v13 = v0_54;
        Throwable v2_1 = -1;
        if (v13 == 0) {
            v14 = -1;
        } else {
            try {
                if (this.mGenerationTracker != null) {
                    if (!this.mGenerationTracker.isGenerationChanged()) {
                        if (this.mValues.containsKey(p27)) {
                            return ((String) this.mValues.get(p27));
                        }
                    } else {
                        this.mValues.clear();
                    }
                    if (this.mGenerationTracker != null) {
                        v2_1 = this.mGenerationTracker.getCurrentGeneration();
                    }
                }
            } catch (android.util.ArrayMap v0_42) {
                throw v0_42;
            }
            v14 = v2_1;
        }
        android.content.IContentProvider v23 = this.mProviderHolder.getProvider(p26);
        if (this.mCallGetCommand != null) {
            try {
                Throwable v2_10;
                if (v13 != 0) {
                    v2_10 = 0;
                } else {
                    android.util.ArrayMap v0_47 = new android.os.Bundle();
                    v0_47.putInt("_user", p28);
                    v2_10 = v0_47;
                }
                try {
                    if ((v13 == 0) || (this.mGenerationTracker != null)) {
                        android.os.Bundle v16_2 = v2_10;
                        int v17_1 = 0;
                        try {
                        } catch (android.util.ArrayMap v0_52) {
                            String v3 = v17_1;
                        }
                        if ((!android.provider.Settings.isInSystemServer()) || (android.os.Binder.getCallingUid() == android.os.Process.myUid())) {
                            Throwable v2_3 = v23.call(p26.getPackageName(), p26.getAttributionTag(), android.provider.Settings$ContentProviderHolder.access$000(this.mProviderHolder).getAuthority(), this.mCallGetCommand, p27, v16_2);
                        } else {
                            long v18_0 = android.os.Binder.clearCallingIdentity();
                            android.util.ArrayMap v0_2 = v23.call(p26.getPackageName(), p26.getAttributionTag(), android.provider.Settings$ContentProviderHolder.access$000(this.mProviderHolder).getAuthority(), this.mCallGetCommand, p27, v16_2);
                            android.os.Binder.restoreCallingIdentity(v18_0);
                            v2_3 = v0_2;
                        }
                        if (v2_3 != null) {
                            String v3_1 = v2_3.getString("value");
                            if (v13 != 0) {
                                if (v17_1 != 0) {
                                    android.util.ArrayMap v0_8 = ((android.util.MemoryIntArray) v2_3.getParcelable("_track_generation"));
                                    int v4_2 = v2_3.getInt("_generation_index", -1);
                                    if ((v0_8 != null) && (v4_2 >= 0)) {
                                        int v5_3 = v2_3.getInt("_generation", 0);
                                        if (this.mGenerationTracker != null) {
                                            this.mGenerationTracker.destroy();
                                        }
                                        this.mGenerationTracker = new android.provider.Settings$GenerationTracker(v0_8, v4_2, v5_3, new android.provider.-$$Lambda$Settings$NameValueCache$qSyMM6rUAHCa-5rsP-atfAqR3sA(this));
                                        v14 = v5_3;
                                    }
                                }
                                if ((this.mGenerationTracker != null) && (v14 == this.mGenerationTracker.getCurrentGeneration())) {
                                    this.mValues.put(p27, v3_1);
                                }
                            }
                            return v3_1;
                        }
                        android.database.Cursor v24 = 0;
                        try {
                            Throwable v2_4 = new String[1];
                            v2_4[0] = p27;
                            int v11_0 = 0;
                            try {
                                Throwable v2_6;
                                android.provider.-$$Lambda$Settings$NameValueCache$qSyMM6rUAHCa-5rsP-atfAqR3sA v7_3 = android.content.ContentResolver.createSqlQueryBundle("name=?", v2_4, 0);
                            } catch (android.util.ArrayMap v0_17) {
                                String v3_8 = new StringBuilder();
                                v3_8.append("Can\'t get key ");
                                v3_8.append(p27);
                                v3_8.append(" from ");
                                v3_8.append(this.mUri);
                                android.util.Log.w("Settings", v3_8.toString(), v0_17);
                                if (v24 != null) {
                                    v24.close();
                                }
                                return v11_0;
                            }
                            if ((!android.provider.Settings.isInSystemServer()) || (android.os.Binder.getCallingUid() == android.os.Process.myUid())) {
                                v2_6 = v23.query(p26.getPackageName(), p26.getAttributionTag(), this.mUri, android.provider.Settings$NameValueCache.SELECT_VALUE_PROJECTION, v7_3, 0);
                            } else {
                                android.os.Bundle v16_1 = android.os.Binder.clearCallingIdentity();
                                v24 = v23.query(p26.getPackageName(), p26.getAttributionTag(), this.mUri, android.provider.Settings$NameValueCache.SELECT_VALUE_PROJECTION, v7_3, 0);
                                android.os.Binder.restoreCallingIdentity(v16_1);
                                v2_6 = v24;
                            }
                            try {
                                if (v2_6 != null) {
                                    android.os.Bundle v8_2;
                                    if (!v2_6.moveToNext()) {
                                        v8_2 = 0;
                                    } else {
                                        v8_2 = v2_6.getString(0);
                                    }
                                    String v3_3 = v8_2;
                                    if ((this.mGenerationTracker != null) && (v14 == this.mGenerationTracker.getCurrentGeneration())) {
                                        this.mValues.put(p27, v3_3);
                                    }
                                    if (v2_6 != null) {
                                        v2_6.close();
                                    }
                                    return v3_3;
                                } else {
                                    String v3_5 = new StringBuilder();
                                    v3_5.append("Can\'t get key ");
                                    v3_5.append(p27);
                                    v3_5.append(" from ");
                                    v3_5.append(this.mUri);
                                    android.util.Log.w("Settings", v3_5.toString());
                                    if (v2_6 != null) {
                                        v2_6.close();
                                    }
                                    return 0;
                                }
                            } catch (android.util.ArrayMap v0_17) {
                                v24 = v2_6;
                            } catch (android.util.ArrayMap v0_35) {
                                v24 = v2_6;
                                if (v24 != null) {
                                    v24.close();
                                }
                                throw v0_35;
                            }
                            return v3_1;
                        } catch (android.util.ArrayMap v0_17) {
                            v11_0 = 0;
                        } catch (android.util.ArrayMap v0_35) {
                        }
                    } else {
                        if (v2_10 == null) {
                            v2_10 = new android.os.Bundle();
                        }
                        v2_10.putString("_track_generation", 0);
                        v16_2 = v2_10;
                        v17_1 = 1;
                    }
                } catch (android.util.ArrayMap v0_52) {
                }
                throw v0_52;
            } catch (android.util.ArrayMap v0) {
            }
        }
    }

