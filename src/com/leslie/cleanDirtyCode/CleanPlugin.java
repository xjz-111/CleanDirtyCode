package com.leslie.cleanDirtyCode;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.leslie.cleanDirtyCode.env.OnScanListener;
import com.leslie.cleanDirtyCode.scan.ScanUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;

/**
 * @Desc：
 * @Author：Leslie
 * @Date： 2021-07-28 12:50
 * @Email： mr_feeling_heart@yeah.net
 */
public class CleanPlugin extends AnAction implements OnScanListener {
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        ScanUtils.getInstance().startScan(project.getBasePath(), this);

    }

    @Override
    public void onSuccess(String msg) {
        Messages.showMessageDialog(project, msg, "Information", Messages.getInformationIcon());
    }

    @Override
    public void onErr(String msg) {
        Messages.showMessageDialog(project, msg, "Information", Messages.getInformationIcon());
    }
}
