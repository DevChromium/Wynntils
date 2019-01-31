package cf.wynntils.webapi.profiles;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.utils.MD5Verification;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.modules.core.enums.UpdateStream;
import cf.wynntils.modules.core.overlays.UpdateOverlay;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.WebReader;

public class UpdateProfile {

    boolean hasUpdate = false;
    boolean updateCheckFailed = false;
    String latestUpdate = Reference.VERSION;

    private WebReader versions;

    public UpdateProfile() {
        new Thread(() -> {
            try{
                MD5Verification md5Installed = new MD5Verification(ModCore.jarFile);
                if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE) {
                    String cuttingEdgeMd5 = WebManager.getCuttingEdgeJarFileMD5();
                    if (!md5Installed.getMd5().equals(cuttingEdgeMd5)) {
                        hasUpdate = true;
                        latestUpdate = "B" + WebManager.getCuttingEdgeBuildNumber();
                        UpdateOverlay.reset();
                    }
                } else {
                    String stableMd5 = WebManager.getStableJarFileMD5();
                    if (!md5Installed.getMd5().equals(stableMd5)) {
                        hasUpdate = true;
                        latestUpdate = WebManager.getStableJarVersion();
                        UpdateOverlay.forceDownload();
                    }
                }

            }catch(Exception ex) {
                ex.printStackTrace();
                updateCheckFailed = true;
            }
        }).start();
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public void forceUpdate() {
        UpdateOverlay.reset();
        hasUpdate = true;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

    public boolean updateCheckFailed() {
        return updateCheckFailed;
    }
}
