.method protected updateConnectivity()V
    .registers 3

    iget-object v0, p0, Lcom/android/settingslib/deviceinfo/AbstractWifiMacAddressPreferenceController;->mWifiMacAddress:Landroidx/preference/Preference;

    if-eqz v0, :cond_end

    const-string v1, "XXX"

    invoke-virtual {v0, v1}, Landroidx/preference/Preference;->setSummary(Ljava/lang/CharSequence;)V

    :cond_end
    return-void
.end method
