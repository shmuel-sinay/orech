.class public Lcom/android/packageinstaller/InstallStart;
.super Landroid/app/Activity;
.source "InstallStart.java"


# static fields
.field private static final LOG_TAG:Ljava/lang/String; = "InstallStart"


# instance fields
.field private mAbortInstall:Z

.field private mPasswordVerified:Z

.field private mPasswordEditText:Landroid/widget/EditText;

.field private mPackageManager:Landroid/content/pm/PackageManager;

.field private mUserManager:Landroid/os/UserManager;


# direct methods
.method static constructor <clinit>()V
    .registers 0

    return-void
.end method

.method public constructor <init>()V
    .registers 2

    .line 45
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    const/4 v0, 0x0

    .line 51
    iput-boolean v0, p0, Lcom/android/packageinstaller/InstallStart;->mAbortInstall:Z

    return-void
.end method

.method private getOriginatingUid(Landroid/content/pm/ApplicationInfo;)I
    .registers 5

    .line 194
    invoke-virtual {p0}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    const-string v1, "android.intent.extra.ORIGINATING_UID"

    const/4 v2, -0x1

    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v0

    if-eqz p1, :cond_10

    .line 199
    iget p1, p1, Landroid/content/pm/ApplicationInfo;->uid:I

    goto :goto_1c

    .line 202
    :cond_10
    :try_start_10
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object p1

    .line 203
    invoke-virtual {p0}, Landroid/app/Activity;->getActivityToken()Landroid/os/IBinder;

    move-result-object v1

    invoke-interface {p1, v1}, Landroid/app/IActivityManager;->getLaunchedFromUid(Landroid/os/IBinder;)I

    move-result p1
    :try_end_1c
    .catch Landroid/os/RemoteException; {:try_start_10 .. :try_end_1c} :catch_2d

    :goto_1c
    const-string v1, "android.permission.MANAGE_DOCUMENTS"

    .line 211
    invoke-virtual {p0, v1, v2, p1}, Landroid/app/Activity;->checkPermission(Ljava/lang/String;II)I

    move-result v1

    if-nez v1, :cond_25

    return v0

    .line 215
    :cond_25
    invoke-direct {p0, p1}, Lcom/android/packageinstaller/InstallStart;->isSystemDownloadsProvider(I)Z

    move-result p0

    if-eqz p0, :cond_2c

    return v0

    :cond_2c
    return p1

    .line 206
    :catch_2d
    sget-object p1, Lcom/android/packageinstaller/InstallStart;->LOG_TAG:Ljava/lang/String;

    const-string v0, "Could not determine the launching uid."

    invoke-static {p1, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    const/4 p1, 0x1

    .line 207
    iput-boolean p1, p0, Lcom/android/packageinstaller/InstallStart;->mAbortInstall:Z

    return v2
.end method

.method private getSourceInfo(Ljava/lang/String;)Landroid/content/pm/ApplicationInfo;
    .registers 3

    if-eqz p1, :cond_c

    .line 176
    :try_start_2
    invoke-virtual {p0}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object p0

    const/4 v0, 0x0

    invoke-virtual {p0, p1, v0}, Landroid/content/pm/PackageManager;->getApplicationInfo(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;

    move-result-object p0
    :try_end_b
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_2 .. :try_end_b} :catch_c

    return-object p0

    :catch_c
    :cond_c
    const/4 p0, 0x0

    return-object p0
.end method

.method private isSystemDownloadsProvider(I)Z
    .registers 4

    .line 223
    invoke-virtual {p0}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object p0

    const-string v0, "downloads"

    const/4 v1, 0x0

    invoke-virtual {p0, v0, v1}, Landroid/content/pm/PackageManager;->resolveContentProvider(Ljava/lang/String;I)Landroid/content/pm/ProviderInfo;

    move-result-object p0

    if-nez p0, :cond_e

    return v1

    .line 229
    :cond_e
    iget-object p0, p0, Landroid/content/pm/ProviderInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    .line 230
    invoke-virtual {p0}, Landroid/content/pm/ApplicationInfo;->isSystemApp()Z

    move-result v0

    if-eqz v0, :cond_1b

    iget p0, p0, Landroid/content/pm/ApplicationInfo;->uid:I

    if-ne p1, p0, :cond_1b

    const/4 v1, 0x1

    :cond_1b
    return v1
.end method

.method private isUidRequestingPermission(ILjava/lang/String;)Z
    .registers 9

    .line 149
    iget-object v0, p0, Lcom/android/packageinstaller/InstallStart;->mPackageManager:Landroid/content/pm/PackageManager;

    invoke-virtual {v0, p1}, Landroid/content/pm/PackageManager;->getPackagesForUid(I)[Ljava/lang/String;

    move-result-object p1

    const/4 v0, 0x0

    if-nez p1, :cond_a

    return v0

    .line 153
    :cond_a
    array-length v1, p1

    move v2, v0

    :goto_c
    if-ge v2, v1, :cond_2b

    aget-object v3, p1, v2

    .line 156
    :try_start_10
    iget-object v4, p0, Lcom/android/packageinstaller/InstallStart;->mPackageManager:Landroid/content/pm/PackageManager;

    const/16 v5, 0x1000

    invoke-virtual {v4, v3, v5}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v3
    :try_end_18
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_10 .. :try_end_18} :catch_28

    .line 162
    iget-object v3, v3, Landroid/content/pm/PackageInfo;->requestedPermissions:[Ljava/lang/String;

    if-eqz v3, :cond_28

    .line 163
    invoke-static {v3}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v3

    invoke-interface {v3, p2}, Ljava/util/List;->contains(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_28

    const/4 p0, 0x1

    return p0

    :catch_28
    :cond_28
    add-int/lit8 v2, v2, 0x1

    goto :goto_c

    :cond_2b
    return v0
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .registers 15

    .line 55
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    # הצג דיאלוג סיסמה תמיד — doInstall ייקרא מה-listener
    invoke-direct {p0}, Lcom/android/packageinstaller/InstallStart;->showBlockedDialog()V

    return-void

.end method

.method private showBlockedDialog()V
    .registers 6

    new-instance v0, Landroid/widget/TextView;
    invoke-direct {v0, p0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    const-string v1, "ההתקנה חסומה במכשיר הזה"
    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    const/16 v1, 0x11
    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setGravity(I)V

    const/16 v1, 0x50
    invoke-virtual {v0, v1, v1, v1, v1}, Landroid/widget/TextView;->setPadding(IIII)V

    new-instance v2, Landroid/app/AlertDialog$Builder;
    const v3, 0x010302d2
    invoke-direct {v2, p0, v3}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;I)V

    const-string v1, "התקנת אפליקציות"
    invoke-virtual {v2, v1}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;
    move-result-object v2

    invoke-virtual {v2, v0}, Landroid/app/AlertDialog$Builder;->setView(Landroid/view/View;)Landroid/app/AlertDialog$Builder;
    move-result-object v2

    const/4 v3, 0x0
    invoke-virtual {v2, v3}, Landroid/app/AlertDialog$Builder;->setCancelable(Z)Landroid/app/AlertDialog$Builder;
    move-result-object v2

    invoke-virtual {v2}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;
    move-result-object v2

    new-instance v3, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;
    invoke-direct {v3, p0, v2}, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;-><init>(Lcom/android/packageinstaller/InstallStart;Landroid/app/AlertDialog;)V

    invoke-virtual {v0, v3}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    invoke-virtual {v2}, Landroid/app/AlertDialog;->show()V

    return-void
.end method

.method public doInstall()V
    .registers 15

    const/4 v13, 0x0

    .line 56
    invoke-virtual {p0}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v13

    iput-object v13, p0, Lcom/android/packageinstaller/InstallStart;->mPackageManager:Landroid/content/pm/PackageManager;

    .line 57
    const-class v13, Landroid/os/UserManager;

    invoke-virtual {p0, v13}, Landroid/app/Activity;->getSystemService(Ljava/lang/Class;)Ljava/lang/Object;

    move-result-object v13

    check-cast v13, Landroid/os/UserManager;

    iput-object v13, p0, Lcom/android/packageinstaller/InstallStart;->mUserManager:Landroid/os/UserManager;

    .line 58
    invoke-virtual {p0}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object v13

    .line 59
    invoke-virtual {p0}, Landroid/app/Activity;->getCallingPackage()Ljava/lang/String;

    move-result-object v0

    .line 63
    invoke-virtual {v13}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v1

    const-string v2, "android.content.pm.action.CONFIRM_INSTALL"

    invoke-virtual {v2, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    const/4 v2, -0x1

    if-eqz v1, :cond_2f

    const-string v3, "android.content.pm.extra.SESSION_ID"

    .line 68
    invoke-virtual {v13, v3, v2}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    move-result v3

    goto :goto_30

    :cond_2f
    move v3, v2

    :goto_30
    const/4 v4, 0x0

    if-nez v0, :cond_55

    if-eq v3, v2, :cond_55

    .line 71
    invoke-virtual {p0}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/pm/PackageManager;->getPackageInstaller()Landroid/content/pm/PackageInstaller;

    move-result-object v0

    .line 72
    invoke-virtual {v0, v3}, Landroid/content/pm/PackageInstaller;->getSessionInfo(I)Landroid/content/pm/PackageInstaller$SessionInfo;

    move-result-object v0

    if-eqz v0, :cond_48

    .line 73
    invoke-virtual {v0}, Landroid/content/pm/PackageInstaller$SessionInfo;->getInstallerPackageName()Ljava/lang/String;

    move-result-object v3

    goto :goto_49

    :cond_48
    move-object v3, v4

    :goto_49
    if-eqz v0, :cond_50

    .line 75
    invoke-virtual {v0}, Landroid/content/pm/PackageInstaller$SessionInfo;->getInstallerAttributionTag()Ljava/lang/String;

    move-result-object v0

    goto :goto_51

    :cond_50
    move-object v0, v4

    :goto_51
    move-object v12, v3

    move-object v3, v0

    move-object v0, v12

    goto :goto_56

    :cond_55
    move-object v3, v4

    .line 78
    :goto_56
    invoke-direct {p0, v0}, Lcom/android/packageinstaller/InstallStart;->getSourceInfo(Ljava/lang/String;)Landroid/content/pm/ApplicationInfo;

    move-result-object v5

    .line 79
    invoke-direct {p0, v5}, Lcom/android/packageinstaller/InstallStart;->getOriginatingUid(Landroid/content/pm/ApplicationInfo;)I

    move-result v6

    const/4 v7, 0x0

    if-eqz v5, :cond_6e

    .line 81
    iget v8, v5, Landroid/content/pm/ApplicationInfo;->privateFlags:I

    and-int/lit8 v8, v8, 0x8

    if-eqz v8, :cond_6e

    const-string v8, "android.intent.extra.NOT_UNKNOWN_SOURCE"

    .line 83
    invoke-virtual {v13, v8, v7}, Landroid/content/Intent;->getBooleanExtra(Ljava/lang/String;Z)Z

    move-result v8

    goto :goto_6f

    :cond_6e
    move v8, v7

    :goto_6f
    const/4 v9, 0x1

    if-nez v8, :cond_bf

    if-eq v6, v2, :cond_bf

    .line 87
    invoke-static {p0, v6}, Lcom/android/packageinstaller/PackageUtil;->getMaxTargetSdkVersionForUid(Landroid/content/Context;I)I

    move-result v2

    if-gez v2, :cond_93

    .line 89
    sget-object v2, Lcom/android/packageinstaller/InstallStart;->LOG_TAG:Ljava/lang/String;

    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    const-string v10, "Cannot get target sdk version for uid "

    invoke-virtual {v8, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v8, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    invoke-static {v2, v8}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 91
    iput-boolean v9, p0, Lcom/android/packageinstaller/InstallStart;->mAbortInstall:Z

    goto :goto_bf

    :cond_93
    const/16 v8, 0x1a

    if-lt v2, v8, :cond_bf

    const-string v2, "android.permission.REQUEST_INSTALL_PACKAGES"

    .line 92
    invoke-direct {p0, v6, v2}, Lcom/android/packageinstaller/InstallStart;->isUidRequestingPermission(ILjava/lang/String;)Z

    move-result v8

    if-nez v8, :cond_bf

    .line 94
    sget-object v8, Lcom/android/packageinstaller/InstallStart;->LOG_TAG:Ljava/lang/String;

    new-instance v10, Ljava/lang/StringBuilder;

    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    const-string v11, "Requesting uid "

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v11, " needs to declare permission "

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v8, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 96
    iput-boolean v9, p0, Lcom/android/packageinstaller/InstallStart;->mAbortInstall:Z

    .line 99
    :cond_bf
    :goto_bf
    iget-boolean v2, p0, Lcom/android/packageinstaller/InstallStart;->mAbortInstall:Z

    if-eqz v2, :cond_ca

    .line 100
    invoke-virtual {p0, v7}, Landroid/app/Activity;->setResult(I)V

    .line 101
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    return-void

    .line 105
    :cond_ca
    new-instance v2, Landroid/content/Intent;

    invoke-direct {v2, v13}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    const v7, 0x2000001

    .line 106
    invoke-virtual {v2, v7}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    const-string v7, "EXTRA_CALLING_PACKAGE"

    .line 111
    invoke-virtual {v2, v7, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string v0, "EXTRA_CALLING_ATTRIBUTION_TAG"

    .line 112
    invoke-virtual {v2, v0, v3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string v0, "EXTRA_ORIGINAL_SOURCE_INFO"

    .line 114
    invoke-virtual {v2, v0, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    const-string v0, "android.intent.extra.ORIGINATING_UID"

    .line 115
    invoke-virtual {v2, v0, v6}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    if-eqz v1, :cond_f1

    .line 118
    const-class v13, Lcom/android/packageinstaller/PackageInstallerActivity;

    invoke-virtual {v2, p0, v13}, Landroid/content/Intent;->setClass(Landroid/content/Context;Ljava/lang/Class;)Landroid/content/Intent;

    goto :goto_11c

    .line 120
    :cond_f1
    invoke-virtual {p0}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object v13

    invoke-virtual {v13}, Landroid/content/Intent;->getData()Landroid/net/Uri;

    move-result-object v13

    if-eqz v13, :cond_109

    .line 122
    invoke-virtual {v13}, Landroid/net/Uri;->getScheme()Ljava/lang/String;

    move-result-object v0

    const-string v1, "content"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_109

    .line 128
    const-class v13, Lcom/android/packageinstaller/InstallStaging;

    invoke-virtual {v2, p0, v13}, Landroid/content/Intent;->setClass(Landroid/content/Context;Ljava/lang/Class;)Landroid/content/Intent;

    goto :goto_11c

    :cond_109
    if-eqz v13, :cond_11e

    .line 129
    invoke-virtual {v13}, Landroid/net/Uri;->getScheme()Ljava/lang/String;

    move-result-object v13

    const-string v0, "package"

    invoke-virtual {v13, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v13

    if-eqz v13, :cond_11e

    .line 131
    const-class v13, Lcom/android/packageinstaller/PackageInstallerActivity;

    invoke-virtual {v2, p0, v13}, Landroid/content/Intent;->setClass(Landroid/content/Context;Ljava/lang/Class;)Landroid/content/Intent;

    :goto_11c
    move-object v4, v2

    goto :goto_12c

    .line 133
    :cond_11e
    new-instance v13, Landroid/content/Intent;

    invoke-direct {v13}, Landroid/content/Intent;-><init>()V

    const/4 v0, -0x3

    const-string v1, "android.intent.extra.INSTALL_RESULT"

    .line 134
    invoke-virtual {v13, v1, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    .line 136
    invoke-virtual {p0, v9, v13}, Landroid/app/Activity;->setResult(ILandroid/content/Intent;)V

    :goto_12c
    if-eqz v4, :cond_131

    .line 143
    invoke-virtual {p0, v4}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V

    .line 145
    :cond_131
    invoke-virtual {p0}, Landroid/app/Activity;->finish()V

    return-void
.end method

.method public showPasswordDialog()V
    .registers 5

    new-instance v0, Landroid/widget/EditText;
    invoke-direct {v0, p0}, Landroid/widget/EditText;-><init>(Landroid/content/Context;)V

    const/16 v1, 0x81
    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setInputType(I)V

    iput-object v0, p0, Lcom/android/packageinstaller/InstallStart;->mPasswordEditText:Landroid/widget/EditText;

    new-instance v1, Landroid/app/AlertDialog$Builder;
    const v2, 0x010302d2
    invoke-direct {v1, p0, v2}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;I)V

    const-string v2, "\u05d4\u05d6\u05df \u05e1\u05d9\u05e1\u05de\u05d0"
    invoke-virtual {v1, v2}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;
    move-result-object v1

    invoke-virtual {v1, v0}, Landroid/app/AlertDialog$Builder;->setView(Landroid/view/View;)Landroid/app/AlertDialog$Builder;
    move-result-object v1

    const-string v2, "\u05d0\u05d9\u05e9\u05d5\u05e8"
    new-instance v3, Lcom/android/packageinstaller/InstallStart$PasswordClickListener;
    invoke-direct {v3, p0, v0}, Lcom/android/packageinstaller/InstallStart$PasswordClickListener;-><init>(Lcom/android/packageinstaller/InstallStart;Landroid/widget/EditText;)V
    invoke-virtual {v1, v2, v3}, Landroid/app/AlertDialog$Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;
    move-result-object v1

    const-string v2, "\u05e1\u05d2\u05d5\u05e8"
    new-instance v3, Lcom/android/packageinstaller/InstallStart$CancelClickListener;
    invoke-direct {v3, p0}, Lcom/android/packageinstaller/InstallStart$CancelClickListener;-><init>(Lcom/android/packageinstaller/InstallStart;)V
    invoke-virtual {v1, v2, v3}, Landroid/app/AlertDialog$Builder;->setNegativeButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;
    move-result-object v1

    invoke-virtual {v1}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;
    move-result-object v1

    const/4 v2, 0x0
    invoke-virtual {v1, v2}, Landroid/app/Dialog;->setCancelable(Z)V

    new-instance v2, Lcom/android/packageinstaller/InstallStart$CancelClickListener;
    invoke-direct {v2, p0}, Lcom/android/packageinstaller/InstallStart$CancelClickListener;-><init>(Lcom/android/packageinstaller/InstallStart;)V
    invoke-virtual {v1, v2}, Landroid/app/Dialog;->setOnCancelListener(Landroid/content/DialogInterface$OnCancelListener;)V

    invoke-virtual {v1}, Landroid/app/Dialog;->show()V

    return-void

.end method
