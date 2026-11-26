.class public Lcom/android/packageinstaller/InstallStart$PasswordClickListener;
.super Ljava/lang/Object;
.implements Landroid/content/DialogInterface$OnClickListener;

.field public this$0:Lcom/android/packageinstaller/InstallStart;
.field public mEditText:Landroid/widget/EditText;

.method public constructor <init>(Lcom/android/packageinstaller/InstallStart;Landroid/widget/EditText;)V
    .registers 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput-object p1, p0, Lcom/android/packageinstaller/InstallStart$PasswordClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;
    iput-object p2, p0, Lcom/android/packageinstaller/InstallStart$PasswordClickListener;->mEditText:Landroid/widget/EditText;
    return-void
.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .registers 6

    iget-object v0, p0, Lcom/android/packageinstaller/InstallStart$PasswordClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;
    iget-object v1, p0, Lcom/android/packageinstaller/InstallStart$PasswordClickListener;->mEditText:Landroid/widget/EditText;

    if-eqz v1, :wrong

    invoke-virtual {v1}, Landroid/widget/EditText;->getText()Landroid/text/Editable;
    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;
    move-result-object v1

    const-string v2, "תודה להשם"

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result v1

    if-eqz v1, :wrong

    invoke-virtual {v0}, Lcom/android/packageinstaller/InstallStart;->doInstall()V
    return-void

    :wrong
    const-string v2, "\u05e1\u05d9\u05e1\u05de\u05d0 \u05e9\u05d2\u05d5\u05d9\u05d4"
    const/4 v3, 0x0
    invoke-static {v0, v2, v3}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
    move-result-object v2
    invoke-virtual {v2}, Landroid/widget/Toast;->show()V
    invoke-virtual {v0}, Landroid/app/Activity;->finish()V
    return-void

.end method
