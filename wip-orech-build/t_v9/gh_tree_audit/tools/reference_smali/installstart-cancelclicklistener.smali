.class Lcom/android/packageinstaller/InstallStart$CancelClickListener;
.super Ljava/lang/Object;
.implements Landroid/content/DialogInterface$OnClickListener;
.implements Landroid/content/DialogInterface$OnCancelListener;

.field final synthetic this$0:Lcom/android/packageinstaller/InstallStart;

.method constructor <init>(Lcom/android/packageinstaller/InstallStart;)V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/android/packageinstaller/InstallStart$CancelClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;

    return-void

.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .registers 4

    iget-object v0, p0, Lcom/android/packageinstaller/InstallStart$CancelClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;

    invoke-virtual {v0}, Landroid/app/Activity;->finish()V

    return-void

.end method

.method public onCancel(Landroid/content/DialogInterface;)V
    .registers 3

    iget-object v0, p0, Lcom/android/packageinstaller/InstallStart$CancelClickListener;->this$0:Lcom/android/packageinstaller/InstallStart;

    invoke-virtual {v0}, Landroid/app/Activity;->finish()V

    return-void

.end method
