svc bluetooth disable
sleep 3
setprop persist.service.bdroid.bdaddr 00:11:22:33:44:55
svc bluetooth enable
sleep 10
echo settings: $(settings get secure bluetooth_address)
dumpsys bluetooth_manager | grep -i address | head -3
setprop persist.service.bdroid.bdaddr ""