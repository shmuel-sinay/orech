.class public Lcom/android/packageinstaller/InstallStart$BlockedClickListener;
.super Ljava/lang/Object;
.implements Landroid/view/View$OnClickListener;

.field public this$0:Lcom/android/packageinstaller/InstallStart;
.field public mDialog:Landroid/app/AlertDialog;
.field public mCount:I

.method public constructor <init>(Lcom/android/packageinstaller/InstallStart;Landroid/app/AlertDialog;)V
    .registers 4

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;

    iput-object p2, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->mDialog:Landroid/app/AlertDialog;

    const/4 v0, 0x0

    iput v0, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->mCount:I

    return-void
.end method

.method public onClick(Landroid/view/View;)V
    .registers 5

    iget v0, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->mCount:I

    add-int/lit8 v0, v0, 0x1

    iput v0, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->mCount:I

    const/16 v1, 0x7

    if-lt v0, v1, :not_yet

    iget-object v2, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->mDialog:Landroid/app/AlertDialog;

    invoke-virtual {v2}, Landroid/app/AlertDialog;->dismiss()V

    iget-object v3, p0, Lcom/android/packageinstaller/InstallStart$BlockedClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;

    invoke-virtual {v3}, Lcom/android/packageinstaller/InstallStart;->showPasswordDialog()V

    :not_yet
    return-void
.end method
