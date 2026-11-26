===== getRequiredInstallerLPr ()Ljava/lang/String;

    private String getRequiredInstallerLPr()
    {
        android.content.Intent v0_1 = new android.content.Intent("android.intent.action.INSTALL_PACKAGE");
        v0_1.addCategory("android.intent.category.DEFAULT");
        v0_1.setDataAndType(android.net.Uri.parse("content://com.example/foo.apk"), "application/vnd.android.package-archive");
        java.util.List v1_2 = this.queryIntentActivitiesInternal(v0_1, "application/vnd.android.package-archive", 1835008, 0);
        if (v1_2.size() != 1) {
            RuntimeException v3_2 = new StringBuilder();
            v3_2.append("There must be exactly one installer; found ");
            v3_2.append(v1_2);
            throw new RuntimeException(v3_2.toString());
        } else {
            if (!((android.content.pm.ResolveInfo) v1_2.get(0)).activityInfo.applicationInfo.isPrivilegedApp()) {
                throw new RuntimeException("The installer must be a privileged app");
            } else {
                return ((android.content.pm.ResolveInfo) v1_2.get(0)).getComponentInfo().packageName;
            }
        }
    }

===== getRequiredUninstallerLPr ()Ljava/lang/String;

    private String getRequiredUninstallerLPr()
    {
        android.content.Intent v0_1 = new android.content.Intent("android.intent.action.UNINSTALL_PACKAGE");
        v0_1.addCategory("android.intent.category.DEFAULT");
        v0_1.setData(android.net.Uri.fromParts("package", "foo.bar", 0));
        android.content.pm.ResolveInfo v1_2 = this.resolveIntent(v0_1, 0, 1835008, 0);
        if ((v1_2 == null) || (this.mResolveActivity.name.equals(v1_2.getComponentInfo().name))) {
            String v3_3 = new StringBuilder();
            v3_3.append("There must be exactly one uninstaller; found ");
            v3_3.append(v1_2);
            throw new RuntimeException(v3_3.toString());
        } else {
            return v1_2.getComponentInfo().packageName;
        }
    }

===== getRequiredPermissionControllerLPr ()Ljava/lang/String;

    private String getRequiredPermissionControllerLPr()
    {
        android.content.Intent v0_1 = new android.content.Intent("android.intent.action.MANAGE_PERMISSIONS");
        v0_1.addCategory("android.intent.category.DEFAULT");
        java.util.List v1_3 = this.queryIntentActivitiesInternal(v0_1, 0, 1835008, 0);
        if (v1_3.size() != 1) {
            RuntimeException v3_1 = new StringBuilder();
            v3_1.append("There must be exactly one permissions manager; found ");
            v3_1.append(v1_3);
            throw new RuntimeException(v3_1.toString());
        } else {
            if (!((android.content.pm.ResolveInfo) v1_3.get(0)).activityInfo.applicationInfo.isPrivilegedApp()) {
                throw new RuntimeException("The permissions manager must be a privileged app");
            } else {
                return ((android.content.pm.ResolveInfo) v1_3.get(0)).getComponentInfo().packageName;
            }
        }
    }

===== getRequiredSharedLibraryLPr (Ljava/lang/String; I)Ljava/lang/String;

    private String getRequiredSharedLibraryLPr(String p7, int p8)
    {
        try {
            Throwable v1_2 = this.getSharedLibraryInfoLPr(p7, ((long) p8));
        } catch (Throwable v1_1) {
            throw v1_1;
        }
        if (v1_2 == null) {
            IllegalStateException v3_4 = new StringBuilder();
            v3_4.append("Missing required shared library:");
            v3_4.append(p7);
            throw new IllegalStateException(v3_4.toString());
        } else {
            IllegalStateException v2_1 = v1_2.getPackageName();
            if (v2_1 == null) {
                String v4_1 = new StringBuilder();
                v4_1.append("Expected a package for shared library ");
                v4_1.append(p7);
                throw new IllegalStateException(v4_1.toString());
            } else {
                return v2_1;
            }
        }
    }

===== getRequiredServicesExtensionPackageLPr ()Ljava/lang/String;

    private String getRequiredServicesExtensionPackageLPr()
    {
        String v0_2 = this.ensureSystemPackageName(this.mContext.getString(17039949));
        if (android.text.TextUtils.isEmpty(v0_2)) {
            throw new RuntimeException("Required services extension package is missing, check config_servicesExtensionPackage.");
        } else {
            return v0_2;
        }
    }

