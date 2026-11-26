.class public Lcom/orech/patcher/DevOptionsUnlockListener;
.super Ljava/lang/Object;
.implements Landroid/content/DialogInterface$OnClickListener;

.field private final mContext:Landroid/content/Context;
.field private final mEditText:Landroid/widget/EditText;

.method public constructor <init>(Landroid/content/Context;Landroid/widget/EditText;)V
    .registers 3

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/orech/patcher/DevOptionsUnlockListener;->mContext:Landroid/content/Context;

    iput-object p2, p0, Lcom/orech/patcher/DevOptionsUnlockListener;->mEditText:Landroid/widget/EditText;

    return-void
.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .registers 7

    iget-object v0, p0, Lcom/orech/patcher/DevOptionsUnlockListener;->mEditText:Landroid/widget/EditText;

    invoke-virtual {v0}, Landroid/widget/EditText;->getText()Landroid/text/Editable;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "תודה להשם"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    iget-object v1, p0, Lcom/orech/patcher/DevOptionsUnlockListener;->mContext:Landroid/content/Context;

    if-eqz v0, :wrong

    const/4 v2, 0x1

    invoke-static {v1, v2}, Lcom/android/settingslib/development/DevelopmentSettingsEnabler;->setDevelopmentSettingsEnabled(Landroid/content/Context;Z)V

    sget v2, Lcom/android/settings/R$string;->show_dev_on:I

    const/4 v3, 0x1

    invoke-static {v1, v2, v3}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object v2

    invoke-virtual {v2}, Landroid/widget/Toast;->show()V

    return-void

    :wrong
    const-string v2, "סיסמא שגויה"

    const/4 v3, 0x0

    invoke-static {v1, v2, v3}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v2

    invoke-virtual {v2}, Landroid/widget/Toast;->show()V

    return-void
.end method
