package pers.ahogek.youtube.wheelbarrow.file.listener;

import pers.ahogek.youtube.wheelbarrow.file.event.FileChangeEvent;

/**
 * <p>
 *  文件监听接口
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-31 22:48
 */
public interface Listener {

    /**
     * 发生文件变动事件时的处理逻辑
     *
     * @param event
     */
    void fire(FileChangeEvent event);
}
