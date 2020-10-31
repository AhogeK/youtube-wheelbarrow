package pers.ahogek.youtube.wheelbarrow.file.test;

import pers.ahogek.youtube.wheelbarrow.file.listener.FileChangeListener;
import pers.ahogek.youtube.wheelbarrow.file.monitor.DirectoryTargetMonitor;

import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-31 23:31
 */
public class ReadFileText {

    public static void main(String[] args) throws IOException {
        DirectoryTargetMonitor monitor = new DirectoryTargetMonitor(new FileChangeListener(), "/home/ahogek/tskkbiss");
        monitor.startMonitor();
    }
}
