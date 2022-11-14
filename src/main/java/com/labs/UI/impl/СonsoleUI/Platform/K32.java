package com.labs.UI.impl.СonsoleUI.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.Native;

public interface K32 extends Kernel32{
    public static K32 INSTANCE = (K32)Native.load("kernel32.dll", K32.class);

    public int SetConsoleTextAttribute(HANDLE hConsoleOutput, short wAttributes);
}