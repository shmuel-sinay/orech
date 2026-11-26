.method private enableDevelopmentSettings()V
    .registers 10

    const/4 v0, 0x0

    iput v0, p0, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController;->mDevHitCountdown:I

    iput-boolean v0, p0, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController;->mProcessingLastDevHit:Z

    iget-object v1, p0, Lcom/android/settingslib/core/AbstractPreferenceController;->mContext:Landroid/content/Context;

    new-instance v2, Landroid/app/AlertDialog$Builder;

    const v3, 0x7f13038d

    invoke-direct {v2, v1, v3}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;I)V

    const-string v3, "אפשרויות מפתחים"

    invoke-virtual {v2, v3}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    const-string v3, "הזינו את קוד הגישה כדי להפעיל אפשרויות מפתחים"

    invoke-virtual {v2, v3}, Landroid/app/AlertDialog$Builder;->setMessage(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    new-instance v4, Landroid/widget/EditText;

    invoke-direct {v4, v1}, Landroid/widget/EditText;-><init>(Landroid/content/Context;)V

    const/16 v3, 0x81

    invoke-virtual {v4, v3}, Landroid/widget/EditText;->setInputType(I)V

    const-string v3, "קוד גישה"

    invoke-virtual {v4, v3}, Landroid/widget/EditText;->setHint(Ljava/lang/CharSequence;)V

    new-instance v5, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;

    invoke-direct {v5, v1, v4}, Lcom/android/settings/deviceinfo/BuildNumberPreferenceController$PwListener;-><init>(Landroid/content/Context;Landroid/widget/EditText;)V

    const-string v3, "אישור"

    invoke-virtual {v2, v3, v5}, Landroid/app/AlertDialog$Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;

    const-string v3, "ביטול"

    const/4 v6, 0x0

    invoke-virtual {v2, v3, v6}, Landroid/app/AlertDialog$Builder;->setNegativeButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;

    new-instance v8, Landroid/widget/FrameLayout;

    invoke-direct {v8, v1}, Landroid/widget/FrameLayout;-><init>(Landroid/content/Context;)V

    const/16 v3, 0x3c

    const/16 v6, 0x14

    const/4 v7, 0x0

    invoke-virtual {v8, v3, v6, v3, v7}, Landroid/view/View;->setPadding(IIII)V

    invoke-virtual {v8, v4}, Landroid/view/ViewGroup;->addView(Landroid/view/View;)V

    invoke-virtual {v2, v8}, Landroid/app/AlertDialog$Builder;->setView(Landroid/view/View;)Landroid/app/AlertDialog$Builder;

    invoke-virtual {v2}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;

    move-result-object v2

    invoke-virtual {v2}, Landroid/app/Dialog;->getWindow()Landroid/view/Window;

    move-result-object v3

    if-eqz v3, :cond_show

    const/4 v6, 0x5

    invoke-virtual {v3, v6}, Landroid/view/Window;->setSoftInputMode(I)V

    :cond_show
    invoke-virtual {v2}, Landroid/app/Dialog;->show()V

    return-void
.end method
