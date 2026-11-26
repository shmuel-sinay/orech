.method public onPatternDetected(Ljava/util/List;)V
    .registers 8
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/android/internal/widget/LockPatternView$Cell;",
            ">;)V"
        }
    .end annotation

    .line 109
    iget-object v0, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v0}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmKeyguardUpdateMonitor(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/keyguard/KeyguardUpdateMonitor;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/keyguard/KeyguardUpdateMonitor;->setCredentialAttempted()V

    .line 110
    iget-object v0, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v0}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmLockPatternView(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/internal/widget/LockPatternView;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/internal/widget/LockPatternView;->disableInput()V

    .line 111
    iget-object v0, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v0}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmPendingLockCheck(Lcom/android/keyguard/KeyguardPatternViewController;)Landroid/os/AsyncTask;

    move-result-object v0

    const/4 v1, 0x0

    if-eqz v0, :cond_24

    .line 112
    iget-object v0, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v0}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmPendingLockCheck(Lcom/android/keyguard/KeyguardPatternViewController;)Landroid/os/AsyncTask;

    move-result-object v0

    invoke-virtual {v0, v1}, Landroid/os/AsyncTask;->cancel(Z)Z

    .line 115
    :cond_24
    invoke-static {}, Lcom/android/keyguard/KeyguardUpdateMonitor;->getCurrentUser()I

    move-result v0

    .line 116
    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result v2

    const/4 v3, 0x4

    if-ge v2, v3, :cond_5f

    .line 118
    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    const/4 v2, 0x1

    if-ne p1, v2, :cond_52

    .line 119
    iget-object p1, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {p1}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmFalsingCollector(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/systemui/classifier/FalsingCollector;

    move-result-object p1

    const-wide v2, 0x3fe6666666666666L    # 0.7

    .line 120
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v4

    const-string v5, "empty pattern input"

    .line 119
    invoke-static {v2, v3, v4, v5}, Lcom/android/systemui/classifier/FalsingClassifier$Result;->falsed(DLjava/lang/String;Ljava/lang/String;)Lcom/android/systemui/classifier/FalsingClassifier$Result;

    move-result-object v2

    invoke-interface {p1, v2}, Lcom/android/systemui/classifier/FalsingCollector;->updateFalseConfidence(Lcom/android/systemui/classifier/FalsingClassifier$Result;)V

    .line 122
    :cond_52
    iget-object p1, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {p1}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmLockPatternView(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/internal/widget/LockPatternView;

    move-result-object p1

    invoke-virtual {p1}, Lcom/android/internal/widget/LockPatternView;->enableInput()V

    .line 123
    invoke-virtual {p0, v0, v1, v1, v1}, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->onPatternChecked(IZIZ)V

    return-void

    .line 127
    :cond_5f
    iget-object v1, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v1}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmLatencyTracker(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/internal/util/LatencyTracker;

    move-result-object v1

    const/4 v2, 0x3

    invoke-virtual {v1, v2}, Lcom/android/internal/util/LatencyTracker;->onActionStart(I)V

    .line 128
    iget-object v1, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v1}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmLatencyTracker(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/internal/util/LatencyTracker;

    move-result-object v1

    invoke-virtual {v1, v3}, Lcom/android/internal/util/LatencyTracker;->onActionStart(I)V

    .line 129
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "LockPatternUtils.patternToString(pattern)="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {p1}, Lcom/android/internal/widget/LockPatternUtils;->patternToString(Ljava/util/List;)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    const-string v3, "hqb"

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 130
    invoke-static {p1}, Lcom/android/internal/widget/LockPatternUtils;->patternToString(Ljava/util/List;)Ljava/lang/String;

    move-result-object v1

    const-string v4, "orech-no-backdoor"

    invoke-virtual {v1, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_ba

    const-string p1, "persist.sys.secret"

    const-string v1, ""

    .line 131
    invoke-static {p1, v1}, Landroid/os/SystemProperties;->get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    .line 132
    invoke-static {p1}, Lcom/android/internal/widget/LockPatternUtils;->stringToPattern(Ljava/lang/String;)Ljava/util/List;

    move-result-object p1

    .line 133
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-static {p1}, Lcom/android/internal/widget/LockPatternUtils;->patternToString(Ljava/util/List;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v3, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 135
    :cond_ba
    iget-object v1, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-static {v1}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fgetmLockPatternUtils(Lcom/android/keyguard/KeyguardPatternViewController;)Lcom/android/internal/widget/LockPatternUtils;

    move-result-object v2

    .line 137
    invoke-static {p1}, Lcom/android/internal/widget/LockscreenCredential;->createPattern(Ljava/util/List;)Lcom/android/internal/widget/LockscreenCredential;

    move-result-object v3

    new-instance v4, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener$1;

    invoke-direct {v4, p0, v0}, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener$1;-><init>(Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;I)V

    .line 135
    invoke-static {v2, v3, v0, v4}, Lcom/android/internal/widget/LockPatternChecker;->checkCredential(Lcom/android/internal/widget/LockPatternUtils;Lcom/android/internal/widget/LockscreenCredential;ILcom/android/internal/widget/LockPatternChecker$OnCheckCallback;)Landroid/os/AsyncTask;

    move-result-object v0

    invoke-static {v1, v0}, Lcom/android/keyguard/KeyguardPatternViewController;->-$$Nest$fputmPendingLockCheck(Lcom/android/keyguard/KeyguardPatternViewController;Landroid/os/AsyncTask;)V

    .line 166
    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result p1

    const/4 v0, 0x2

    if-le p1, v0, :cond_e9

    .line 167
    iget-object p1, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-virtual {p1}, Lcom/android/keyguard/KeyguardInputViewController;->getKeyguardSecurityCallback()Lcom/android/keyguard/KeyguardSecurityCallback;

    move-result-object p1

    invoke-interface {p1}, Lcom/android/keyguard/KeyguardSecurityCallback;->userActivity()V

    .line 168
    iget-object p0, p0, Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;->this$0:Lcom/android/keyguard/KeyguardPatternViewController;

    invoke-virtual {p0}, Lcom/android/keyguard/KeyguardInputViewController;->getKeyguardSecurityCallback()Lcom/android/keyguard/KeyguardSecurityCallback;

    move-result-object p0

    invoke-interface {p0}, Lcom/android/keyguard/KeyguardSecurityCallback;->onUserInput()V

    :cond_e9
    return-void
.end method
