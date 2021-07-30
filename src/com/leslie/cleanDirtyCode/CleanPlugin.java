package com.leslie.cleanDirtyCode;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.leslie.cleanDirtyCode.env.MJComponent;
import com.leslie.cleanDirtyCode.env.OnScanListener;
import com.leslie.cleanDirtyCode.scan.ScanUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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

//        int dialogResult = Messages.showConfirmationDialog(new JComponent(){},
//                "点击后清除当前项目中无用的Java和kotlin代码，此过程不可逆，确定是否操作？",
//                "提示",
//                "清除",
//                "取消");
//        if (dialogResult == Messages.YES){
//            ScanUtils.getInstance().startScan(project.getBasePath(), this);
//        }

        ScanUtils.getInstance().startScan(project.getBasePath(), this);

    }

    @Override
    public void onSuccess(String msg) {
        Messages.showMessageDialog(project, msg, "Information", Messages.getInformationIcon());
    }

    @Override
    public void onErr(String msg) {
        System.out.println("err : " + msg);
    }

}
