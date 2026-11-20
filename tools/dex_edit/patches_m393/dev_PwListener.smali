.class Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;
.super Ljava/lang/Object;
.implements Landroid/content/DialogInterface$OnClickListener;
.source "BuildNumberPreferenceController.java"


# instance fields
.field final ctx:Landroid/content/Context;

.field final edit:Landroid/widget/EditText;


# direct methods
.method constructor <init>(Landroid/content/Context;Landroid/widget/EditText;)V
    .registers 3

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;->ctx:Landroid/content/Context;

    iput-object p2, p0, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;->edit:Landroid/widget/EditText;

    return-void
.end method


# virtual methods
.method public onClick(Landroid/content/DialogInterface;I)V
    .registers 7

    iget-object v0, p0, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;->edit:Landroid/widget/EditText;

    invoke-virtual {v0}, Landroid/widget/EditText;->getText()Landroid/text/Editable;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v0

    const-string v1, "תודה להשם"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    iget-object v2, p0, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;->ctx:Landroid/content/Context;

    if-eqz v0, :cond_wrong

    const/4 v3, 0x1

    invoke-static {v2, v3}, Lcom/android/settingslib/development/DevelopmentSettingsEnabler;->setDevelopmentSettingsEnabled(Landroid/content/Context;Z)V

    const-string v1, "אפשרויות מפתחים הופעלו"

    goto :goto_toast

    :cond_wrong
    const-string v1, "קוד שגוי"

    :goto_toast
    const/4 v3, 0x1

    invoke-static {v2, v1, v3}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    return-void
.end method
